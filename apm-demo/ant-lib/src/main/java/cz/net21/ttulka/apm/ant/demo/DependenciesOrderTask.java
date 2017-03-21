package cz.net21.ttulka.apm.ant.demo;

import org.apache.tools.ant.BuildException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by ttulka, 2017, http://blog.net21.cz
 *
 * Reads a folder with applications and executes the nested tasks in the order of their dependencies.
 * The actual application is written in the @{application} attribute as the path to the application.
 *
 * Parameters:
 * - path
 * - relPathMetaInfoFile
 */
public class DependenciesOrderTask extends SequentialTask {
    /**
     * Attribute name.
     */
    private static final String ATTR_NAME = "application";

    private String path;
    private String relPathMetaInfoFile;

    /**
     * Sets the path of the applications folder.
     * @param path the applications folder
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Sets the relative path the the meta info file of the application.
     * @param relPathMetaInfoFile the relative path to the meta info file
     */
    public void setRelPathMetaInfoFile(String relPathMetaInfoFile) {
        this.relPathMetaInfoFile = relPathMetaInfoFile;
    }

    @Override
    String getAttributeName() {
        return ATTR_NAME;
    }

    /**
     * Execution point of the task.
     */
    @Override
    public void execute() {
        if (path == null || path.trim().isEmpty()) {
            throw new BuildException("Parameter 'path' must be specified. Should lead to the applications folder.");
        }
        if (relPathMetaInfoFile == null || relPathMetaInfoFile.trim().isEmpty()) {
            throw new BuildException("Parameter 'relPathMetaInfoFile' must be specified. Put the relative path to the Meta-info file in the application package.");
        }

        Path pathToApplications = Paths.get(path);

        List<String> dependencies = getDependenciesOrder(pathToApplications);
        List<Path> applications = getAppPathsFromDependencies(dependencies, pathToApplications);

        for (Path application : applications) {
            executeSequential(application.toString());
        }
    }

    /**
     * Returns the application in the dependencies order.
     * @param folder the path to the applications folder
     * @return the applications
     */
    List<String> getDependenciesOrder(Path folder) {
        if (!Files.exists(folder) || !Files.isReadable(folder) || !Files.isDirectory(folder)) {
            throw new BuildException(folder + " is not a readable directory.");
        }

        List<String> dependencies = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path file: stream) {
                if (Files.isDirectory(file)) {
                    List<String> appDependencies = getDependenciesOrderForApp(file);
                    dependencies = mergeDependencies(dependencies, appDependencies);
                }
            }
        }
        catch (IOException | DirectoryIteratorException e) {
            throw new BuildException("Error by reading '" + folder + "'.", e);
        }

        return dependencies;
    }

    /**
     * Recursive get dependencies for the application.
     * @param folder the application folder
     * @return the all dependencies
     */
    List<String> getDependenciesOrderForApp(Path folder) {
        final Properties metaInfo = loadMetaInfoProperties(folder);
        final String name = metaInfo.getProperty("name");
        final String version = metaInfo.getProperty("version");
        final String dependenciesString = metaInfo.getProperty("dependencies");

        List<String> dependencies = new ArrayList<>();

        if (dependenciesString != null) {
            for (String dependency : dependenciesString.split(",")) {
                Path dependencyFolder = getFolderFromDependency(dependency, folder.getParent());

                if (dependencyFolder != null) {
                    List<String> dependenciesRecursion = getDependenciesOrderForApp(dependencyFolder);
                    dependencies = mergeDependencies(dependencies, dependenciesRecursion);
                }
            }
        }
        dependencies.add(name + ":" + version);

        return dependencies;
    }

    /**
     * Returns a path for the dependency in the format "name:version".
     * @param dependency the dependency
     * @param parent the applications parent folder
     * @return the path
     */
    Path getFolderFromDependency(String dependency, Path parent) {
        dependency = dependency.trim();
        if (dependency == null || dependency.isEmpty()) {
            return null;
        }
        int separatorPos = dependency.indexOf(":");
        if (separatorPos < 1 || separatorPos == dependency.length() - 1) {
            throw new BuildException("Dependency '" + dependency + "' must be in the form 'name:version'.");
        }
        String name = dependency.substring(0, separatorPos);
        String version = dependency.substring(separatorPos + 1);

        return parent.resolve(name + "-" + version);
    }

    Properties loadMetaInfoProperties(Path folder) {
        final Properties metaInfo = new Properties();
        final Path metaInfoFilename = folder.resolve(relPathMetaInfoFile);

        try (InputStream metaInfoInput =  Files.newInputStream(metaInfoFilename)) {
            metaInfo.load(metaInfoInput);
        }
        catch (IOException e) {
            throw new BuildException("Error by reading meta info file '" + metaInfoFilename + "'.", e);
        }
        return metaInfo;
    }

    List<String> mergeDependencies(List<String> dependencies, List<String> appDependencies) {
        final List<String> merged = new ArrayList<>(dependencies);

        for (String addDependency : appDependencies) {
            if (!merged.contains(addDependency)) {
                merged.add(addDependency);
            }
        }
        return merged;
    }

    List<Path> getAppPathsFromDependencies(List<String> dependencies, Path parent) {
        final List<Path> paths = new ArrayList<>();

        for (String dependency : dependencies) {
            paths.add(getFolderFromDependency(dependency, parent));
        }
        return paths;
    }
}
