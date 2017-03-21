package cz.net21.ttulka.simplegraphs.shape.api;

/**
 * Helper class for the holding X Y positions.
 * 
 * @author ttulka
 *
 */
public class XY {

	private final double x;
	private final double y;
	
	/**
	 * Constructor.
	 * @param x the position X
	 * @param y the position Y
	 */
	public XY(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns X
	 * @return the X
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns Y
	 * @return the Y
	 */
	public double getY() {
		return y;
	}
}
