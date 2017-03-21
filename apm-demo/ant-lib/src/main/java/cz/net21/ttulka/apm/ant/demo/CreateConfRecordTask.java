package cz.net21.ttulka.apm.ant.demo;

import org.apache.tools.ant.BuildException;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttulka, 2017, http://blog.net21.cz
 *
 * Create a config record in the form "application-name=comma,separated,list,of,files".
 */
public class CreateConfRecordTask extends SequentialTask {
    /**
     * Attribute name.
     */
    private static final String ATTR_NAME = "record";

    private String path;

    public void setPath(String path) {
        this.path = path;
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

        final List<String> filesList = getFilesList(Paths.get(path));

        final String recordValue = getCommaSeparated(filesList);

        executeSequential(recordValue);
    }

    List<String> getFilesList(Path sourcePath) {
        final List<String> toReturn = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath)) {
            for (Path file: stream) {
                if (Files.isRegularFile(file)) {
                    toReturn.add(file.getFileName().toString());
                }
            }
        }
        catch (IOException | DirectoryIteratorException e) {
            throw new BuildException("Error by reading '" + sourcePath + "'.", e);
        }
        return toReturn;
    }

    String getCommaSeparated(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String item : list) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(item);
        }
        return sb.toString();
    }
}
