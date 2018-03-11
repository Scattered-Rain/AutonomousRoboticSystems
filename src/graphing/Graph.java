package graphing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/** Builds and renders graphing things */
public class Graph extends Frame{
	
	/** The Elements the Graph is drawing */
	private List<GraphElement> elements;
	
	
	/** Constructs new Graph */
	public Graph(int width, int height, int angle){
		super(width, height, angle);
		this.elements = new ArrayList<GraphElement>();
	}
	
	/** Constructs new Graph */
	public Graph(int width, int height, int angle,  List<GraphElement> elements){
		super(width, height, angle);
		this.elements = elements;
	}
	
	/** Add Graph Element to be drawn */
	public Graph addGraphElement(GraphElement element){
		this.elements.add(element);
		return this;
	}
	
}
