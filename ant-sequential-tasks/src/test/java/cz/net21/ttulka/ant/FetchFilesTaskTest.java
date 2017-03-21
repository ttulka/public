package cz.net21.ttulka.ant;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Tomas Tulka
 */
public class FetchFilesTaskTest {

    private static final Path FILES_PATH = Paths.get("src/test/resources/files");

    private FetchFilesTask task;

    @Before
    public void setUp() {
        task = new FetchFilesTask();
    }

    @Test
    public void getFilesListTest() {
        List<String> files = task.getFilesList(FILES_PATH, null);

        assertNotNull(files);
        assertEquals(4, files.size());

        files = task.getFilesList(FILES_PATH, ".xml");

        assertNotNull(files);
        assertEquals(2, files.size());

        files = task.getFilesList(FILES_PATH, ".txt");

        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("[src\\test\\resources\\files\\sometextfile.txt]", files.toString());
    }
}
