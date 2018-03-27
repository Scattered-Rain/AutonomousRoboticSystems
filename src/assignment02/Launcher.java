package assignment02;

import java.util.Random;

import util.Point;

public class Launcher{
	
	public static void main(String[] args){
		Point start = new Point(5, 5);
		KalmanKontroller k = new KalmanKontroller();
		Simulator sim = new Simulator(new boolean[10][10]);
		sim.simulate(k, new Random().nextInt(Integer.MAX_VALUE), true);
	}
	
	
}
