package cz.net21.ttulka.json.mock.generator.source;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cz.net21.ttulka.json.mock.generator.model.Node;
import cz.net21.ttulka.json.mock.generator.model.NodeTypes;

/**
 * Factory for the sources.
 * 
 * @author ttulka
 *
 */
public class SourceFactory {	
	
	private final Map<NodeTypes, Source<String>> cache;
	
	private final Random random;
	private final SimpleDateFormat dateFormat;
	
	public SourceFactory() {
		super();
		cache = new HashMap<>();
		random = new Random(new Date().getTime());
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	public Source<?> getSource(Node node) {
		switch (node.getType()) {
			case COMPOSITE:	
				return null;
			case VALUE:		
				return () -> node.getValue();
			case INTEGER:	
				return getSource(node.getMin(), node.getMax(), (min,max) -> Math.abs(random.nextInt(max + min)) + min);
			case FLOAT:		
				return getSource(node.getMin(), node.getMax(), (min,max) -> Math.abs(random.nextLong() % (max + min)) + min);
			case DATE:		
				return getSource((Date)null, (Date)null);// TODO
			case ID: 	
				return cache.computeIfAbsent(node.getType(), (t) -> new Id());
			case LOREM: 	
				return cache.computeIfAbsent(node.getType(), (t) -> new Lorem(random, node.getMax()));
			case RANDOM: 	
				return () -> node.getValues().get(random.nextInt(node.getValues().size()));
			case FILE: 	
				return new File(node.getPath());
			default:		
				return cache.computeIfAbsent(node.getType(), Bundle::new);				
		}
	}
	
	public Source<Number> getSource(Integer min, Integer max, RandomSupplier<Number> supplier) {
		Integer minimum = min != null ? min : 0;
		Integer maximum = max != null ? max : Integer.MAX_VALUE;
		return () -> supplier.get(minimum, maximum);
	}
	
	public Source<String> getSource(Date min, Date max) {	
		long minimum = min != null ? min.getTime() : 0;
		long maximum = max != null ? max.getTime() : Integer.MAX_VALUE;
		return () -> dateFormat.format(new Date(random.nextLong() % (maximum + minimum) + minimum));
	}
	
	@FunctionalInterface
	private interface RandomSupplier<T extends Number> {	
		T get(int min, int max);
	}
}
