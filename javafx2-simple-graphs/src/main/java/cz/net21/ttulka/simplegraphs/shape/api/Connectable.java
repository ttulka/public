package cz.net21.ttulka.simplegraphs.shape.api;

import javafx.scene.Node;

/**
 * Interface for the nodes, which can be connected to each other or to itself.
 * 
 * @author ttulka
 *
 */
public interface Connectable {
	
	/**
	 * Connects to another connectable.
	 * @param connecting the connectable node.
	 */
	void addConnecting(Connecting connecting);

	/**
	 * Returns the X position of the bottom.
	 * @return the position
	 */
	double downX();
	/**
	 * Returns the Y position of the bottom.
	 * @return the position
	 */
	double downY();
	/**
	 * Returns the X position of the top.
	 * @return the position
	 */
	double upX();
	/**
	 * Returns the Y position of the top.
	 * @return the position
	 */
	double upY();
	
	/**
	 * Returns the width.
	 * @return the width
	 */
	double getWidth();
	/**
	 * Returns the height.
	 * @return the height
	 */
	double getHeight();
	
	/**
	 * Adds a child to this node.
	 * @param node the child node
	 */
	void addChild(Node node);
	
	/**
	 * Adds an arrow to another node.
	 * @param nodeTo the referenced node
	 * @param text the title of the arrow
	 */
	void addArrowTo(Connectable nodeTo, String text);
}
