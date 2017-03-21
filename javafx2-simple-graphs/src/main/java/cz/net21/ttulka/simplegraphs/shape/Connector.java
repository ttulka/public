package cz.net21.ttulka.simplegraphs.shape;

import java.util.Timer;
import java.util.TimerTask;

import cz.net21.ttulka.simplegraphs.shape.api.Connectable;
import cz.net21.ttulka.simplegraphs.shape.api.Connecting;
import cz.net21.ttulka.simplegraphs.shape.api.XY;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;

/**
 * Abstract general connector.
 * 
 * @author ttulka
 *
 */
public abstract class Connector extends Parent implements Connecting {
	
	protected final Line line;
	private final Ellipse ellipse;
	
	private final Label label;
		
	protected final Connectable node1;
	protected final Connectable node2;
	
	protected final boolean reflexive; 	// both nodes are identical
	
	/**
	 * Constructor.
	 * @param node1 the node to connect from
	 * @param node2 the node to connect to
	 */
	public Connector(final Connectable node1, final Connectable node2) {
		this(node1, node2, Color.BLACK, null);
	}
	
	/**
	 * Constructor.
	 * @param node1 the node to connect from
	 * @param node2 the node to connect to
	 * @param color the color of the connector
	 */
	public Connector(final Connectable node1, final Connectable node2, final Paint color) {
		this(node1, node2, color, null);
	}
	
	/**
	 * Constructor.
	 * @param node1 the node to connect from
	 * @param node2 the node to connect to
	 * @param text the title of the connector
	 */
	public Connector(final Connectable node1, final Connectable node2, final String text) {
		this(node1, node2, Color.BLACK, text);
	}

	/**
	 * Constructor.
	 * @param node1 the node to connect from
	 * @param node2 the node to connect to
	 * @param color the color of the connector
	 * @param text the title of the connector
	 */
	public Connector(final Connectable node1, final Connectable node2, final Paint color, final String text) {		
		this.node1 = node1;
		this.node2 = node2;
				
		reflexive = node1.equals(node2);
		
		node1.addConnecting(this);
		node2.addConnecting(this);
		
		line = new Line();
		line.setStroke(color);
		
		line.setOnMouseEntered(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				line.setStroke(Color.BLUE);
			}
		});
		line.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						line.setStroke(color);
					}
				}, 1000);
			}
		});
		
		ellipse = new Ellipse();
		ellipse.setStroke(color);
		ellipse.setFill(Color.TRANSPARENT);

		if (!reflexive) {
			node1.addChild(line);
		} else {
			node1.addChild(ellipse);
		}
		
		label = new Label();
		label.setTextFill(Color.GRAY);
		
		if (text != null && !text.isEmpty()) {
			label.setText(text);		
			node1.addChild(label);
		}
	}
	
	protected XY calculateEndXY() {
		double endX = node2.upX();
		double endY = node2.upY();
		
		if (node1.downY() > node2.upY() && node1.upY() < node2.downY()) {
			endY = node2.upY() + node2.getHeight() / 2;
			
			if (node1.upX() + node1.getWidth() / 2 < node2.upX() - node2.getWidth() / 2) {
				endX = node2.upX() - node2.getWidth() / 2; 
			} else {
				endX = node2.upX() + node2.getWidth() / 2;
			}
		}
		else if (node1.downY() < node2.upY()) {
			endY = node2.upY();
		} 
		else {
			endY = node2.downY();
		}
		return new XY(endX, endY);
	}

	@Override
	public void moveEvent() {
		final XY endXY = calculateEndXY();
				
		if (!reflexive) {
			line.setStartX(node1.downX());
			line.setStartY(node1.downY() - node1.getHeight() / 2);
			line.setEndX(endXY.getX());
			line.setEndY(endXY.getY());
			
			line.toBack();
		} else {
			ellipse.setCenterX(node1.upX() + node1.getWidth() / 2);
			ellipse.setCenterY(node1.upY() + node1.getHeight() / 2);
			ellipse.setRadiusX(node1.getWidth() / 4);
			ellipse.setRadiusY(node1.getWidth() / 6);
			
			ellipse.toBack();
		}		
		setLabelLayout();
	}
	
	private void setLabelLayout() {
		if (!reflexive) {
			double offsetX = label.getText().length() * 3 * 0;
			
			if (node1.downX() > node2.upX()) {
				offsetX *= -1;
			}
			
			final double minX = Math.min(node1.downX(), node2.upX());
			final double maxX = Math.max(node1.downX(), node2.upX());
			
			final double minY = Math.min(node1.downY() - node1.getHeight() / 2, node2.upY());
			final double maxY = Math.max(node1.downY() - node1.getHeight() / 2, node2.upY());
			
			label.setLayoutX((maxX - minX) / 2 + minX + offsetX);
			label.setLayoutY((maxY - minY) / 2 + minY);
		} else {
			label.setLayoutX(node1.upX() + node1.getWidth() / 2 + node1.getWidth() / 4 + 5);
			label.setLayoutY(node1.upY() + node1.getHeight() / 2 - label.heightProperty().get() / 2);
		}
	}
}
