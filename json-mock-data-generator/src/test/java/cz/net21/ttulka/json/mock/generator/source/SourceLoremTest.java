package cz.net21.ttulka.json.mock.generator.source;

import java.util.Random;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Source Lorem test.
 * 
 * @author ttulka
 *
 */
public class SourceLoremTest {

	private Lorem source;
	
	@Before
	public void setUp() {
		source = new Lorem(new Random());
	}
	
	@Test
	public void getNextTest() {
		String lorem = source.getNext();
		
		assertTrue(lorem.endsWith("."));
		assertTrue(lorem.contains(","));
	}
}
