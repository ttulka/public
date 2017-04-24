package cz.net21.ttulka.json.mock.generator.source;

/**
 * Source of values.
 * 
 * @author TT
 *
 */
@FunctionalInterface
public interface Source<T> {
	
	public T getNext();
}
