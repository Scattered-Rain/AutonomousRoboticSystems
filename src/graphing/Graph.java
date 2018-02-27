package graphing;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** Builds and renders graphing things */
public class Graph extends Panel{
	
	
	/** Constructs new Graph */
	public Graph(int width, int height){
		super(width, height, width, height, "Graph");
	}
	
	
	/** Draw Graph */
	@Override protected BufferedImage doUpdateImg(Graphics2D g, BufferedImage img) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
