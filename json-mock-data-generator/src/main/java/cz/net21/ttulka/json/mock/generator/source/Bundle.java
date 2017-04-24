package cz.net21.ttulka.json.mock.generator.source;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.net21.ttulka.json.mock.generator.model.NodeTypes;

/**
 * Bundle of example values.
 * 
 * @author ttulka
 *
 */
public class Bundle implements Source<String> {

	private final List<String> values;
	
	private int index = 0;
		
	private Bundle() {
		super();
		values = new ArrayList<>();
	}
	
	public Bundle(NodeTypes type) {
		this();		
		InputStream is = Bundle.class.getResourceAsStream("/bundles/" + type + ".dat");
		BufferedReader br = new BufferedReader(new InputStreamReader(is)); 
		br.lines().forEach(values::add);
	}
	
	public Iterator<String> iterator() {
		return values.iterator();
	}
	
	@Override
	public String getNext() {
		return values.get(index ++ % values.size());
	}
}
