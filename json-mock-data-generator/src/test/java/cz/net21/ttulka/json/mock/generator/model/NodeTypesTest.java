package cz.net21.ttulka.json.mock.generator.model;

import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class NodeTypesTest {
	
	@Test
	public void parseTest() {
		// TODO
		assertEquals(NodeTypes.FULL_NAME, NodeTypes.parse("fullName"));
	}

}
