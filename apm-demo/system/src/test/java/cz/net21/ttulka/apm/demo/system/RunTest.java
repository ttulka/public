package cz.net21.ttulka.apm.demo.system;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by ttulka, 2017, http://blog.net21.cz
 */
public class RunTest {

    private Run run;

    @Before
    public void setUp() {
        run = new Run();
    }

    @Test
    public void getFilesTest() {
        String property = "system=a1, b-2,c.3, d-1.txt";

        List<String> files = run.getFiles(property);

        assertEquals("[a1, b-2, c.3, d-1.txt]", files.toString());
    }
}
