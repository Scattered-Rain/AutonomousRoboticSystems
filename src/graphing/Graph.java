package graphing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import assignment01.Simulator.Recorder;

/** Builds and renders graphing things */
public class Graph extends Frame{
	
	/** The Elements the Graph is drawing */
	private List<GraphElement> elements;
	
	
	/** Constructs new Graph */
	public Graph(int width, int height,int angle,Recorder rec){
		super(width, height, angle, rec);
		this.elements = new ArrayList<GraphElement>();
		//super.updateImg();
	}
	
	/** Constructs new Graph */
	public Graph(int width, int height,int angle,Recorder rec, List<GraphElement> elements){
		super(width, height, angle,rec );
		this.elements = elements;
		//super.updateImg();
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
