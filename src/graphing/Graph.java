package graphing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/** Builds and renders graphing things */
public class Graph extends Panel{
	
	/** The Elements the Graph is drawing */
	private List<GraphElement> elements;
	
	
	/** Constructs new Graph */
	public Graph(int width, int height){
		super(width, height, width, height, "Graph");
		this.elements = new ArrayList<GraphElement>();
		super.updateImg();
	}
	
	/** Constructs new Graph */
	public Graph(int width, int height, List<GraphElement> elements){
		super(width, height, width, height, "Graph");
		this.elements = elements;
		super.updateImg();
	}
	
	
	/** Draw Graph */
	@Override protected void doUpdateImg(Graphics2D g, BufferedImage img){
		for(GraphElement e : elements){
			e.drawElement(g, img);
		}
	}
	
	/** Add Graph Element to be drawn */
	public Graph addGraphElement(GraphElement element){
		this.elements.add(element);
		return this;
	}
	
}
