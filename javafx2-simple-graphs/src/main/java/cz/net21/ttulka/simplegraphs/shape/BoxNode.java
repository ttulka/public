package cz.net21.ttulka.simplegraphs.shape;

import java.util.LinkedList;
import java.util.List;

import cz.net21.ttulka.simplegraphs.shape.api.Connectable;
import cz.net21.ttulka.simplegraphs.shape.api.Connecting;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * A graph node in the shape of a rectangle.
 * 
 * @author ttulka
 *
 */
public class BoxNode extends Parent implements Connectable {
	
	private static final int DEFWIDTH = 150;
	private static final int DEFHEIGHT = 30;
	
	private final Label text;
	
	private final int width;
	private final int height;
	
	private final List<Connecting> connectings = new LinkedList<>();
	
	/**
	 * Constructor.
	 * @param name the title of the node
	 * @param posX the position X in the scene
	 * @param posY the position Y in the scene
	 */
	public BoxNode(final String name, double posX, double posY) {
		this(name, posX, posY, DEFWIDTH, DEFHEIGHT);
	}
	
	/**
	 * Constructor.
	 * @param name the title of the node
	 * @param posX the position X in the scene
	 * @param posY the position Y in the scene
	 * @param width the width of the node
	 * @param height the height of the node
	 */
	public BoxNode(final String name, double posX, double posY, int width, int height) {
		super();
		this.width = width;
		this.height = height;
		
        text = new Label(name);
        
        text.setPrefWidth(width);
        text.setPrefHeight(height);        
        
        text.setLayoutX(posX);
        text.setLayoutY(posY);
        
        final String normalStyle = "-fx-border-color:#777; -fx-border-width:2px; -fx-padding:3px; -fx-background-color:#dddeee";
        final String overStyle = "-fx-border-color:blue; -fx-border-width:2px; -fx-padding:3px; -fx-background-color:#dddeee";
        final String moveStyle = "-fx-border-color:red; -fx-border-style:dashed; -fx-border-width:2px; -fx-padding:3px; -fx-background-color:#dddeee";
        
        text.setStyle(normalStyle);
        text.setAlignment(Pos.CENTER);
        
        text.setOnMouseEntered(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				text.setStyle(overStyle);
			}
		});
        text.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				text.setStyle(normalStyle);
			}
		});
        text.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				text.setStyle(moveStyle);
				calculateOffsets(event.getSceneX(), event.getSceneY());
			}
		});
        text.setOnMouseReleased(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				text.setStyle(normalStyle);
			}
		});
        text.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				move(event.getSceneX() + offsetX, event.getSceneY() + offsetY);
			}
		});        
        
        getChildren().addAll(text);
    }
	
	private double offsetX;
	private double offsetY;

	private void calculateOffsets(double x, double y) {
		offsetX = text.getLayoutX() - x;
		offsetY = text.getLayoutY() - y;
	}
	
	@Override
	public double downX() {
		return text.getLayoutX() + width / 2;
	}

	@Override
	public double downY() {
		return text.getLayoutY() + height;
	}

	@Override
	public double upX() {
		return text.getLayoutX() + width / 2;
	}

	@Override
	public double upY() {
		return text.getLayoutY();
	}

	@Override
	public void addConnecting(Connecting connecting) {
		connectings.add(connecting);
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public void addChild(Node node) {
		getChildren().add(node);
	}

	@Override
	public void addArrowTo(Connectable nodeTo, String text) {
		new ArrowSimple(this, nodeTo, text);
	}
	
	/**
	 * Moves the node to the specified position.
	 * @param x the position X
	 * @param y the position Y
	 */
	public void move(double x, double y) {
		text.setLayoutX(x);
        text.setLayoutY(y);
        
        adaptConnectings();
	}
	
	private void adaptConnectings() {
		for (Connecting c : connectings) {
			c.moveEvent();
		}
	}
}