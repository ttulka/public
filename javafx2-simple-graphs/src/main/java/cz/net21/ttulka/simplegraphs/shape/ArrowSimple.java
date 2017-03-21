package cz.net21.ttulka.simplegraphs.shape;

import java.util.Timer;
import java.util.TimerTask;

import cz.net21.ttulka.simplegraphs.shape.api.Connectable;
import cz.net21.ttulka.simplegraphs.shape.api.XY;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

/**
 * Simple arrow connector.
 * 
 * @author ttulka
 *
 */
public class ArrowSimple extends Connector {

	private static final int BARB = 15;
	private static final double PHI = Math.PI / 10;	

	private final Line arrow1;
	private final Line arrow2;
		
	/**
	 * Constructor.
	 * @param node1 the node to connect from
	 * @param node2 the node to connect to
	 */
	public ArrowSimple(final Connectable node1, final Connectable node2) {
		this(node1, node2, Color.BLACK, null);
	}
	
	/**
	 * Constructor.
	 * @param node1 the node to connect from
	 * @param node2 the node to connect to
	 * @param color the color of the connector
	 */
	public ArrowSimple(final Connectable node1, final Connectable node2, Paint color) {
		this(node1, node2, color, null);
	}
	
	/**
	 * Constructor.
	 * @param node1 the node to connect from
	 * @param node2 the node to connect to
	 * @param text the title of the connector
	 */
	public ArrowSimple(final Connectable node1, final Connectable node2, final String text) {
		this(node1, node2, Color.BLACK, text);
	}

	/**
	 * Constructor.
	 * @param node1 the node to connect from
	 * @param node2 the node to connect to
	 * @param color the color of the connector
	 * @param text the title of the connector
	 */
	public ArrowSimple(final Connectable node1, final Connectable node2, final Paint color, final String text) {
		super(node1, node2, color, text);
		
		arrow1 = new Line();
		arrow2 = new Line();

		arrow1.setStroke(color);
		arrow2.setStroke(color);
		
		line.setOnMouseEntered(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				line.setStroke(Color.BLUE);
				arrow1.setStroke(Color.BLUE);
				arrow2.setStroke(Color.BLUE);
			}
		});
		line.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						line.setStroke(color);
						arrow1.setStroke(color);
						arrow2.setStroke(color);
					}
				}, 1000);
			}
		});

		node1.addChild(arrow1);
		node1.addChild(arrow2);
		
		moveEvent();
	}
	
	@Override
	public void moveEvent() {
		super.moveEvent();	
		
		final XY endXY = calculateEndXY();

		if (!reflexive) {
			double theta = Math.atan2(endXY.getY() - (node1.downY() - node1.getHeight() / 2), endXY.getX() - node1.downX());
			drawArrow(theta, endXY.getX(), endXY.getY());
		}
	}

	private void drawArrow(double theta, double x0, double y0) {		
		double x = x0 - BARB * Math.cos(theta + PHI);
		double y = y0 - BARB * Math.sin(theta + PHI);

		arrow1.setStartX(x0);
		arrow1.setStartY(y0);
		arrow1.setEndX(x);
		arrow1.setEndY(y);

		x = x0 - BARB * Math.cos(theta - PHI);
		y = y0 - BARB * Math.sin(theta - PHI);

		arrow2.setStartX(x0);
		arrow2.setStartY(y0);
		arrow2.setEndX(x);
		arrow2.setEndY(y);
	}
}
