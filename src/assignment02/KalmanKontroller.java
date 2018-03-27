package assignment02;

import graphing.Frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ejml.simple.SimpleMatrix;

import assignment01.ANN20;
import assignment01.Kinematics;
import assignment01.LaserScannerModel;
import util.Point;
import util.Tuple;

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
	
	private LaserScannerModel las;
	private List<Point> beacs;
	private ANN20 ann;
	
	/** Constructs New KalmanKontroller */
	public void initKalman(boolean[][] map, Point start, double rota, Simulator sim){
		this.collMap = map;
		this.dustMap = new boolean[collMap.length][collMap[0].length];
		this.kalman = Kalman.prepKalman(new double[]{start.getX(), start.getY(), rota});
		this.rota = rota;
		this.las = new LaserScannerModel(collMap);
		this.beacs = las.createBeaconsList();
		this.ann = ANN20.buildFromFile(new int[]{27, 2, 8}, "test0", null);
	}
	
	
	/** Processes Simulation */
	public double[] process(double[] input, Point loc, double rota){
		double[] control = ann.process(input);
		System.out.println(loc + " " + kalman.getLastMu().get(0, 0)+"|"+kalman.getLastMu().get(1, 0));
		List<Tuple<Point,Double>> lasDist = las.findBeacons(loc, collMap, 1.2d);
		List<Point> beacs = new ArrayList<Point>();
		List<Double> beacsDists = new ArrayList<Double>();
		for(int c=0; c<lasDist.size(); c++){
			beacs.add(lasDist.get(c).getA());
			beacsDists.add(lasDist.get(c).getB());
		}
		Point probLoc = new Point(kalman.getLastMu().get(0, 0), kalman.getLastMu().get(1, 0));
		double rott = kalman.getLastMu().get(2, 0);
		if(beacs.size()>=2){//Good Measure, overwrite
			Point pp = calcCircleCenter(beacs, beacsDists);
			if(pp.getX() > 0){
				probLoc = pp;
			}
			//System.out.println(loc+" "+probLoc);
			rott = rota;
		}
		
		kalman.doTheKalman(Simulator.kin(control, rott, new Point(0, 0)), makeMatrix(0.1), makeMatrix(0.001), getZ(probLoc, rott));//new List[]{beacs, las.computeRangeScanLikelihood(loc)});
		return new double[]{control[0], control[1]};
	}
	
	/** Makes vals I*val matrix */
	public SimpleMatrix makeMatrix(double val){
		SimpleMatrix out = new SimpleMatrix(new double[][]{
			{val, 0, 0},
			{0, val, 0},
			{0, 0, val}
		});
		return out;
	}
	
	public SimpleMatrix getZ(Point loc, double rota){
		SimpleMatrix out = new SimpleMatrix(new double[][]{
				{loc.getX()},
				{loc.getY()},
				{rota}
		});
		return out;
	}
	
	/** Returns probable center and rota based on given points & dists */
	public static Point calcCircleCenter(List<Point> points, List<Double> ditances){
		List<Point> bagOPoints = new ArrayList<Point>();
		for(int c=0; c<points.size(); c++){
			for(int c2=c+1; c2<points.size(); c2++){
				Point[] temp = circleIntersect(points.get(c), points.get(c2), ditances.get(c), ditances.get(c2));
				bagOPoints.add(temp[0]);
				bagOPoints.add(temp[1]);
			}
		}
		List<Point> sDps = new ArrayList<Point>();
		for(int c=0; c<bagOPoints.size(); c+=2){
			for(int c2=c+2; c2<bagOPoints.size(); c2+=2){
				Point o0 = bagOPoints.get(c);
				Point o1 = bagOPoints.get(c+1);
				Point t0 = bagOPoints.get(c2);
				Point t1 = bagOPoints.get(c2+1);
				double[] dist = new double[]{o0.distance(t0), o0.distance(t1), o1.distance(t0), o1.distance(t1)};
				double small = dist[0];
				int distId = 0;
				for(int d=1; d<dist.length; d++){
					if(small > dist[d]){
						small = dist[d];
						distId = d;
					}
				}
				if(distId==0 || distId==1){
					sDps.add(o0);
				}
				else{
					sDps.add(o1);
				}
			}
		}
		Point avg = sDps.get(0);
		for(int c=1; c<sDps.size(); c++){
			avg = sDps.get(c).avg(avg);
		}
		return avg;
	}
	
	private static Point[] circleIntersect(Point ca, Point cb, double da, double db){
		double x0 = ca.getX();
		double x1 = cb.getX();
		double y0 = ca.getY();
		double y1 = cb.getY();
		double d = ca.distance(cb);
		double a = (Math.pow(da, 2) - Math.pow(db, 2) + Math.pow(d, 2))/(2*d);
		double h = Math.sqrt(Math.pow(da, 2) - Math.pow(a, 2));
		double x2 = x0+a*(x1-x0)/d;   
		double y2 = y0+a*(y1-y0)/d;
		double x3=x2+h*(y1-y0)/d;
		double x3t=x2-h*(y1-y0)/d;
		double y3=y2-h*(x1-x0)/d;
		double y3t=y2+h*(x1-x0)/d;
		return new Point[]{new Point(x3, y3), new Point(x3t, y3t)};
	}
	
//	public static void main(String[] args){
//		Random rand = new Random();
//		Point origin = new Point(rand.nextDouble()*100, rand.nextDouble()*100);
//		Point[] beacs = new Point[7];
//		double[] dists = new double[beacs.length];
//		for(int c=0; c<beacs.length; c++){
//			beacs[c] = new Point(rand.nextDouble()*100, rand.nextDouble()*100);
//			dists[c] = beacs[c].distance(origin) + rand.nextDouble()*3;
//		}
//		System.out.println(origin+": "+calcCircleCenter(beacs, dists)+", with "+beacs.length);
//	}
	
//	public SimpleMatrix getZ(){
//		//Prepare observation z
//		double mX = x.get(0, 0);
//		double mY = x.get(1, 0);
//		double mT = x.get(2, 0);
//		List<Point> bl = beacons[0];
//		List<Double> bld = beacons[1];
//		for(int c=0; c<beacons[0].size(); c++){
//			if(bld.get(c)!=-1){
//				//There is a beacon of id c seen
//				Point loc = bl.get(c);
//				double xDist = mX-loc.getX();
//				double yDist = mY-loc.getY();
//				double dist = Math.sqrt((Math.pow(xDist, 2) + Math.pow(yDist, 2))) - bld.get(c);
//				mX += (xDist/(xDist+yDist)) * dist;
//				mY += (yDist/(xDist+yDist)) * dist;
//			}
//		}
//		SimpleMatrix z = new SimpleMatrix(new double[][]{
//				{mX},
//				{mY},
//				{mT}
//		});
//		return z;
//	}
	
}
