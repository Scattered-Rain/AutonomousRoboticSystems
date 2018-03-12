package graphing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import util.Point;
import assignment01.BotEvolution;
import assignment01.Simulator;
import assignment01.Simulator.Action;
import assignment01.Simulator.Recorder;
import lombok.Getter;

public class Frame extends JFrame{
	
	/** The Buffer Image Which Is Printed Each Print Cycle.
	 *  Replacing This Image Is The Only Way Of Changing The Output Of This Frame.
	 *  Can Only Be Replaced Using The Manipulation Image And The SetOutput Method.
	 *  Will, When Printed, Automatically Be Scaled To Fit The Frame. */
	private BufferedImage bufferImage;
		
    int x;
    int y;
    int velx =0, vely =0;
    int angle_;
    
    private static Frame thisHere = null;
    
    private Action lastAction = null;
    
    boolean[][] MAP;
    boolean[][] DUST;

    public static int D_W;
    public static int D_H;

 
    
    public void init(Recorder rec){
    	MAP = rec.getMap();
    	D_H = MAP.length*70;
    	D_W = MAP[0].length*70;
    	this.DUST = new boolean[MAP.length][MAP[0].length];
    }
    
    public void update(Action act){
    	this.x = (int) (act.getX()*70-25);
    	this.y = (int) (act.getY()*70-25);
    	this.angle_ = (int) (act.getRotation()*360);
    	DUST[(int)act.getY()][(int)act.getX()] = true;
    	this.lastAction = act;
    }

    public Frame(int width, int height, int angle, Recorder rec) {
    	if(thisHere!=null){
    		thisHere.dispatchEvent(new WindowEvent(thisHere, WindowEvent.WINDOW_CLOSING));
    	}
    	thisHere = this;
    	init(rec);
    	x=width;
    	y=height;
    	angle_ = angle;
    	Panel drawPanel = new Panel();
        ActionListener listener = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (x >= D_W-1.6*70) {
                    x = 70;
                    drawPanel.repaint();
                } else {
//                	x = width;
//                	angle_ += 30;
                    drawPanel.repaint();
                }
                
                if (y >= D_H-1.6*70) {
                    y = 70;
                    drawPanel.repaint();
                } else {
//                    y = height;
                    drawPanel.repaint();
                }
            }
        };
        Timer timer = new Timer(1000, listener);
        timer.start();
        add(drawPanel);
        
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
	

    private class Panel extends JPanel implements ActionListener, KeyListener{

    	Timer t = new Timer(5, this);
    	
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //public void drawRect(int x, int y, int width, int height): draw
            for(int i = 0; i<=D_W/70; i++){
	            g.fillRect(i*70, 0, 1, D_H);
            }
            for(int j = 0; j<=D_H/70; j++){
            	g.fillRect(0, j*70, D_W, 1);
            }
            for(int cy=0; cy< MAP.length ; cy++){
    			for(int cx=0; cx< MAP[0].length; cx++){
    				if(!DUST[cy][cx]){
    					Color col = g.getColor();
    					g.setColor(Color.ORANGE);
    					g.fillRect(cx*70+3, cy*70+3, 70-5, 70-5);
    					g.setColor(col);
    				}
    				if(MAP[cy][cx]){
    					g.fillRect(cx*70, cy*70, 70, 70);
    				}
    			}
    		}
            g.setColor(Color.BLACK);
            g.fillArc(x, y, 50,50,360-angle_,350);  
            drawSeen((Graphics2D)g);
        }
        
        /** Draws Sensors */
        private void drawSeen(Graphics2D g){
        	Color col = g.getColor();
        	boolean first = true;
        	if(lastAction != null && lastAction.getSensors()!=null){
        		for(Point seen : lastAction.getSensors()){
            		if(first){
            			g.setColor(Color.BLUE);
            			first = false;
            		}
            		else{
            			g.setColor(Color.CYAN);
            		}
            		if(seen.getX()>=0 && seen.getY()>=0){
            			g.fillOval(((int)(seen.getX()*70))-6, ((int)(seen.getY()*70))-6, 12, 12);
            		}
            	}
    			g.setColor(col);
        	}
        }

        public Dimension getPreferredSize() {
            return new Dimension(D_W, D_H);
        }
        
        public Panel(){
        	t.start();
        	addKeyListener(this);
        	setFocusable(true);
    		setFocusTraversalKeysEnabled(false);
        	}

        public void keyPressed(KeyEvent e) {
    		int code = e.getKeyCode();
    		
    		if (code == KeyEvent.VK_DOWN){
    			vely = 1;
    			velx = 0;
    		}
    		if (code == KeyEvent.VK_UP){
    			vely = -1;
    			velx = 0;
    		}
    		if (code == KeyEvent.VK_LEFT){
    			vely = 0;
    			velx = -1;
    		}
    		if (code == KeyEvent.VK_RIGHT){
    			vely = 0;
    			velx = 1;
    			
    		}
    		//x+=velx;
    		//y+=vely;
    	}
    	
    	
    	
    	public void keyTyped(KeyEvent e) {}
    	public void keyReleased(KeyEvent e) {
    		velx=0;
    		vely=0;
    	}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(x < 70)
			{
				velx=0;
				x = 70;		
			}
			
			if(x > D_W-1.7*70)
			{
				velx=0;
				x=(int) (D_W-1.7*70);
						
			}
			
			if(y < 70)
			{
				vely=0;
				y = 70;		
			}
			
			if(y > D_H-1.7*70)
			{
				y=(int) (D_H-1.7*70);
				vely = 0;		
			}			
			x += velx;
			y += vely;
			repaint();
			
		}
    }



	protected void doUpdateImg(Graphics2D g, BufferedImage img) {
		// TODO Auto-generated method stub
		
	}

}