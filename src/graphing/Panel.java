package graphing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lombok.Getter;

public abstract class Panel extends JPanel{
	
	/** The Buffer Image Which Is Printed Each Print Cycle.
	 *  Replacing This Image Is The Only Way Of Changing The Output Of This Frame.
	 *  Can Only Be Replaced Using The Manipulation Image And The SetOutput Method.
	 *  Will, When Printed, Automatically Be Scaled To Fit The Frame. */
	private BufferedImage bufferImage;
	
	/** Reference to the Frame this Panel is rendered onto */
	private Frame frame;
	
	/** The Width Of The Image Buffer In Pixels */
	@Getter private int panelWidth;
	/** The Height Of The Image Buffer In Pixels */
	@Getter private int panelHeight;
	
	
	/** Constructs New Panel */
	public Panel(int panelWidth, int panelHeight, int frameWidth, int frameHeight, String frameName){
		//Sets Basic Dimensions As Defined With
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		//Basic JPanel Housekeeping
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		this.frame = new Frame(frameWidth, frameHeight, this, frameName);
	}
	
	/** Paints Scaled Buffer Image */
	public final void paint(Graphics g){
		super.paint(g);
		Graphics2D g2D = (Graphics2D)g;
		if(bufferImage!=null){
			g2D.drawImage(bufferImage, 0, 0, this);
		}
		//Basic Housekeeping
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}
	
	/** Init Graphical Update of Panel */
	public final void updateImg(){
		BufferedImage bi = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = bi.createGraphics();
		doUpdateImg(g, bi);
		this.bufferImage = bi;
		g.dispose();
		repaint();
	}
	
	/** Actual drawing process to update the Buffered Image */
	protected abstract void doUpdateImg(Graphics2D g, BufferedImage img);
	
	
	//---Inner Classes---
	/** Frame that holds the Panel */
	private class Frame extends JFrame{
		
		/** Holds The Name Of The Frame */
		@Getter private String name;
		/** Holds The Frame Width */
		@Getter private int frameWidth;
		/** Holds The Frame Height */
		@Getter private int frameHeight;
		
		/** Creates New Frame */
		protected Frame(int frameWidth, int frameHeight, Panel panel, String name){
			this.name = name;
			this.frameWidth = frameWidth;
			this.frameHeight = frameHeight;
			add(panel);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//The Magic Numbers 6 And 28 Represent The Amount Of Pixels Of 
			//Drawing Space That Get Lost Because Of The Actual Frame.
			setSize(frameWidth+6, frameHeight+28);
			setLocationRelativeTo(null);
			setTitle(name);
			setResizable(false);
			setVisible(true);
		}
		
		/** Close Game Frame */
		protected void close() {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
	        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
		}

	}
	
}
