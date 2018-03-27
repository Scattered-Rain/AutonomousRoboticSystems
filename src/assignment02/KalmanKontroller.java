package assignment02;

import org.ejml.simple.SimpleMatrix;

import assignment01.ANN20;
import util.Point;

/** Debug Class to test the Kalman */
public class KalmanKontroller{
	
	/** Kalman Filter held by this Kalman Kontroller */
	private Kalman kalman;
	
	/** Collision Map */
	private boolean[][] collMap;
	/** Dust Map */
	private boolean[][] dustMap;
	/** init rota */
	private double rota;
	
	
	/** Constructs New KalmanKontroller */
	public void initKalman(boolean[][] map, Point start, double rota){
		this.collMap = map;
		this.dustMap = new boolean[collMap.length][collMap[0].length];
		this.kalman = Kalman.prepKalman(new double[]{start.getX(), start.getY()});
		this.rota = rota;
	}
	
	
	/** Processes Simulation */
	public double[] process(double[] input){
		double[] control = new double[]{1, 0.75};
		kalman.doTheKalman(control, procNoise, measureNoise, beacons)
		return new double[]{control[0], control[1]};
	}
	
}
