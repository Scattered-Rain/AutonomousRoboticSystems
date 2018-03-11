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
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import lombok.Getter;

public class Frame extends JFrame{
	
	/** The Buffer Image Which Is Printed Each Print Cycle.
	 *  Replacing This Image Is The Only Way Of Changing The Output Of This Frame.
	 *  Can Only Be Replaced Using The Manipulation Image And The SetOutput Method.
	 *  Will, When Printed, Automatically Be Scaled To Fit The Frame. */
	private BufferedImage bufferImage;
		
    int x = 10;
    int y = 10;
    int velx =0, vely =0;
    int angle_ = 0;


    private static final int D_W = 600;
    private static final int D_H = 400;

    Panel drawPanel = new Panel();

    public Frame(double width, double height, int angle) {
        ActionListener listener = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (x >= D_W) {
                    x = 10;
                    drawPanel.repaint();
                } else {
                	int randomx = ThreadLocalRandom.current().nextInt(-1, 10 + 1);
                    //System.out.println(randomx);
                	x += width;
                	angle_ += angle;
                    drawPanel.repaint();
                }
                
                if (y >= D_H) {
                    y = 10;
                    drawPanel.repaint();
                } else {
                	int randomy = ThreadLocalRandom.current().nextInt(-1, 10 + 1);
                    y += height;
                    drawPanel.repaint();
                }
            }
        };
        Timer timer = new Timer(1000, listener);
        timer.start();
        add(drawPanel);
        
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
	

    private class Panel extends JPanel implements ActionListener, KeyListener{

    	Timer t = new Timer(5, this);
    	
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //public void drawRect(int x, int y, int width, int height): draw
            g.fillRect(0, 0, 10, 400); //ka8eth aristerh
            g.fillRect(0, 0, 600, 10); //orizontia panw
            g.fillRect(0, 390, 600, 10); //orizontia katw
            g.fillRect(590, 0, 10, 400); //ka8eth de3ia
            g.fillRect(300, 50, 10, 100);
            g.fillRect(50, 300, 100, 10);
            g.setColor(Color.BLACK);
            g.fillArc(x, y, 70,70,angle_,350);  
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
    		x+=velx;
    		y+=vely;
    	}
    	
    	
    	
    	public void keyTyped(KeyEvent e) {}
    	public void keyReleased(KeyEvent e) {
    		velx=0;
    		vely=0;
    	}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(x < 0)
			{
				velx=0;
				x = 0;		
			}
			
			if(x > 530)
			{
				velx=0;
				x = 530;		
			}
			
			if(y < 0)
			{
				vely=0;
				y = 0;		
			}
			
			if(y > 330)
			{
				vely=0;
				y = 330;		
			}
			
			
			x += velx;
			y += vely;
			repaint();
			
		}
    }

}
