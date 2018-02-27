package graphing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import optimization.benchmarks.OpFunction;
import util.Point;
import lombok.AllArgsConstructor;

/** Abstract class designed to graph some element on usingthe Graph class */
public abstract class GraphElement{
	
	/** Draws Itslef */
	public abstract void drawElement(Graphics2D g, BufferedImage img);
	
	
	/** Graph Element that draws given OpFunction */
	@AllArgsConstructor public static class GraphOpFunction extends GraphElement{
		
		/** The OpFunction to be drawn */
		private OpFunction function;
		/** The origin for the drawing of the OpFunction (set to be the center) */
		private Point origin;
		/** Scale of the rendering of the OpFunction, 1 pixel = scale*units */
		private double scale;
		
		/** Draw OpFunction */
		@Override public void drawElement(Graphics2D g, BufferedImage img){
			int imgHeight = img.getHeight();
			int imgWidth = img.getWidth();
			Point start = new Point(origin.getX()-scale*(imgWidth/2), origin.getY()-scale*(imgHeight/2));
			//Figure out shown scaling in colour space
			double low = Double.POSITIVE_INFINITY;
			double high = Double.NEGATIVE_INFINITY;
			for(int cy=0; cy<imgHeight; cy++){
				for(int cx=0; cx<imgWidth; cx++){
					double opVal = function.value(start.add((new Point(cx, imgHeight-cy).multiply(scale))));
					if(opVal > high){
						high = opVal;
					}
					if(opVal < low){
						low = opVal;
					}
				}
			}
			System.out.println(high+" "+low);
			//double colScale = 1/(high*high-low*low);
			//Render
			for(int cy=0; cy<imgHeight; cy++){
				for(int cx=0; cx<imgWidth; cx++){
					double opVal = function.value(start.add((new Point(cx, imgHeight-cy).multiply(scale))));
					//int rgb = Color.HSBtoRGB((float)(opVal>1?1:opVal), 1.0f, 1.0f);
					Color col = new Color((int)opVal);
					g.setColor(col);
					g.drawRect(cx, cy, 0, 0);
				}
			}
		}
		
	}
	
}
