package cz.net21.ttulka.ant;

import org.apache.tools.ant.BuildException;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttulka, 2017, http://blog.net21.cz
 *
 * Reads a directory and returns files with a name suffix (optional).
 * The actual file path is written in the @{file} attribute.
 *
 * Parameters:
 * - path
 * - suffix
 */
public class FetchFilesTask extends SequentialTask {

    /**
     * Attribute name.
     */
    private static final String ATTR_NAME = "file";

    private String path;
    private String suffix;

    public void setPath(String path) {
        this.path = path;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
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
            throw new BuildException("Parameter 'path' must be specified.");
        }

        List<String> filesList = getFilesList(Paths.get(path), suffix);

        for (String file : filesList) {
            executeSequential(file.toString());
        }
    }

    /**
     * Returns a list of files for the source path and the suffix.
     * @param sourcePath the path
     * @param suffix the suffix
     * @return the list of files
     */
    List<String> getFilesList(Path sourcePath, String suffix) {
        final List<String> toReturn = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath)) {
            for (Path file: stream) {
                if (Files.isRegularFile(file)) {
                    if (suffix == null || file.toString().endsWith(suffix)) {
                        toReturn.add(file.toString());
                    }
                }
            }
        }
        catch (IOException | DirectoryIteratorException e) {
            throw new BuildException("Error by reading '" + sourcePath + "'.", e);
        }
        return toReturn;
    }
}
