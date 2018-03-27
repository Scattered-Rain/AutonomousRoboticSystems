package assignment01;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import util.Point;
import util.Tuple;

public class LaserScannerModel {
	public static final int K = 8;
	public static final int B = 5;
	
	private boolean[][] occupancyGridMap;
	private ArrayList<Point> beaconsList;
	
	
	public LaserScannerModel(boolean[][] occupancyGridMap) {
		this.occupancyGridMap = new boolean[occupancyGridMap.length][occupancyGridMap[0].length];
		for(int cy=0; cy<occupancyGridMap.length; cy++){
			for(int cx=0; cx<occupancyGridMap[0].length; cx++){
				this.occupancyGridMap[cy][cx] = occupancyGridMap[cy][cx];
			}
		}
		this.beaconsList = createBeaconsList();
		
	}
	
	/**
	 * Creates an ArrayList with all the obstacle beacons that appear on the environment
	 * @return
	 */
	public ArrayList<Point> createBeaconsList() {
		ArrayList<Point> beaconsList = new ArrayList<Point>();
		
		for (int i = 0; i<this.occupancyGridMap.length; i++) {
			for (int j = 0; j<this.occupancyGridMap[i].length; j++) {
				occupancyGridMap[i][j] = true;
				if (occupancyGridMap[i][j]) {
					beaconsList.add(new Point(i, j));
				}
			}
		}
		
		return beaconsList;
	}
	
	
	
	public List<Tuple<Point, Double>> findBeacons(Point loc, boolean[][] collMap, double range){
		final double noiseMax = 0.3;
		List<Tuple<Point, Double>> out = new ArrayList<Tuple<Point, Double>>();
		for(int c=0; c<beaconsList.size(); c++){
			double dist = beaconsList.get(c).distance(loc);
			if(dist<=range){
				out.add(new Tuple<Point, Double>(beaconsList.get(c), dist+new Random().nextDouble()*noiseMax));
			}
		}
		return out;
	}
	
	
	
	
	public boolean[][] getOccupancyGridMap() {
		return this.occupancyGridMap;
	}
	
	public void setOccupancyGridMap(boolean[][] occupancyGridMap) {
		this.occupancyGridMap = occupancyGridMap;
	}
	
	

	public Tuple<List<Double>, Tuple<Point, Double>> checkBeaconsInDirection(List<Double> beaconsFound, Point location, int direction) {
		boolean beaconFound = false;
		Point obstacleCoordinates = null;
		double distance = 0.0;
		
		for(int i = 0; i<K && !beaconFound; i++) {
			location = returnCoordinates(location, direction);
			int x = (int)location.getIntX();
			int y = (int)location.getIntY();
			
			if (this.getOccupancyGridMap()[x][y]) {
				obstacleCoordinates = new Point(x, y);
				int indexOfBeaconListIndex = this.beaconsList.indexOf(obstacleCoordinates);
				
				if (indexOfBeaconListIndex != -1) {
					distance = (double)i + 1.0;
					DiscoveredBeacon discoveredBeacon = new DiscoveredBeacon(obstacleCoordinates, distance, direction);
				}
			}
		}
		
		return new Tuple<List<Double>, Tuple<Point, Double>>(beaconsFound, new Tuple<Point, Double>(obstacleCoordinates, distance));
	}
	
	public Point returnCoordinates(Point coordinates, int direction) {
		double x = coordinates.getX();
		double y = coordinates.getY();
		
		switch(direction) {
		case 0:
			y+=1.0;
			break;
		case 1:
			x-=1.0;
			y+=1.0;
			break;
		case 2:
			x-=1.0;
			break;
		case 3:
			x-=1.0;
			y-=1.0;
			break;
		case 4:
			y-=1.0;
			break;
		case 5:
			x+=1.0;
			y-=1.0;
			break;
		case 6:
			x+=1.0;
			break;
		case 7:
			x+=1.0;
			y+=1.0;
			break;
		}
		
		return new Point(x,y);
	}
	
//	public Tuple<List<Double>, ArrayList<Tuple<Point,Double>>> scanArea(Point location) {
//		
//		
//		List<Double> beaconsFound = Arrays.asList(LaserScannerModel.initialiseArray(new Double[this.beaconsList.size()]));
//		ArrayList<Tuple<Point,Double>> scan = new ArrayList<Tuple<Point,Double>>();
//		
//		for (int i = 0; i<K; i++) {
//			Tuple<List<Double>, Tuple<Point, Double>> checkBeaconsInDirectionResults = checkBeaconsInDirection(beaconsFound, location, i);
//			beaconsFound = checkBeaconsInDirectionResults.getA();
//			Tuple<Point, Double> measurement = checkBeaconsInDirectionResults.getB();
//			scan.add(measurement);
//		}
//		
//		return new Tuple<ArrayList<DiscoveredBeacon>, ArrayList<Tuple<Point,Double>>>(beaconsFound, scan);
//	}
	
//	public double computeRangeScanLikelihood(Point location) {
//		ArrayList<DiscoveredBeacon>  beaconsFound = scanArea(location).getA();
//		ArrayList<Tuple<Point,Double>> scan = scanArea(location).getB();
//		ArrayList<Tuple<Point,Double>> actualMeasurements = calculateActualMeasurements(scan, location);
//		double sigma = calculateSigma(scan, actualMeasurements);		
//		double q = 1.0;
//		
//		for (int i = 0; i<scan.size(); i++) {
//			double calculatedZ = scan.get(i).getB();
//			double actualZ = actualMeasurements.get(i).getB();
//			double N = calculateN(calculatedZ, actualZ, sigma);	
//		}
//		return beaconsFound;
//	}
	
//	public double calculateN(ArrayList<Tuple<Point,Double>> scan, ArrayList<Tuple<Point,Double>> actualMesaurements) {
//		double calculatedZ = scan.get(i).getB();
//	}
	
//	public calculateIta(ArrayList<Tuple<Point,Double>> scan) {
//		for (int i = 0; i<K; i++) {
//			
//		}
//	}
	
	public double calculateN(double calculatedZ, double actualZ, double sigma) {
		double squaredDifference = Math.pow(calculatedZ - actualZ, 2);
		double exponentPower = (-1/2) * (squaredDifference/ Math.pow(sigma, 2));
		double denominator = Math.sqrt(2 * Math.PI * Math.pow(sigma, 2));
		return (1/denominator) * Math.exp(exponentPower);
	}

	public ArrayList<Tuple<Point,Double>> calculateActualMeasurements(ArrayList<Tuple<Point,Double>> scan, Point location) {
		ArrayList<Tuple<Point,Double>> actualMesaurements = new ArrayList<Tuple<Point,Double>>();
		
		for (int i = 0; i<scan.size(); i++) {
			Point beaconCoordinates = scan.get(i).getA();
			if (beaconCoordinates != null) {
				double actualMeasurement = calculateDistanceBetweenPoints(location, beaconCoordinates);
				actualMesaurements.add(new Tuple<Point,Double>(beaconCoordinates, actualMeasurement));
			}
		}
		
		return actualMesaurements;
	}
	
	public double calculateSigma(ArrayList<Tuple<Point,Double>> scan, ArrayList<Tuple<Point,Double>> actualMeasurements) {
		
		double sum = 0.0;
		int sizeOfMeasurement = scan.size();
		
		for (int i = 0; i<sizeOfMeasurement; i++) {
			double calculatedZ = scan.get(i).getB();
			double actualZ = actualMeasurements.get(i).getB();
			double difference = calculatedZ - actualZ;
			double s = Math.pow(difference, 2);
			sum+=s;
		}
		
		return Math.sqrt((1/sizeOfMeasurement) * sum);
	}
	
	public static double calculateDistanceBetweenPoints(Point point1, Point point2) {
		double x1 = point1.getX();
		double y1 = point1.getY();
		double x2 = point2.getX();
		double y2 = point2.getY();
		return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));	
	}
	
	public ArrayList<DiscoveredBeacon> initialiseBeaconsArrayList() {
		ArrayList<DiscoveredBeacon> discoveredBeacons = new ArrayList<DiscoveredBeacon>();
		
		for (int i = 0; i<this.beaconsList.size(); i++) {
			DiscoveredBeacon discoveredBeacon = new DiscoveredBeacon(null, -1.0, -1);
		}
		
		return discoveredBeacons;
	}
	
	
	public static Point findXfromBeacons(List<Point> beacons, List<Double> distance){
		int i=0;
		int flag =0;
		List<Integer> indexes = new ArrayList<Integer>();
		while(i < distance.size() && flag<3) {
			if(distance.get(i)!=-1){
				flag++;
				indexes.add(i);
			}
			i++;
		}
		if (flag<3){
			return null;
		}else{
			//System.out.println(flag+"\n"+indexes);
			double a = Math.sqrt(Math.pow((beacons.get(indexes.get(1)).getIntX()-beacons.get(indexes.get(0)).getIntX()),2) + Math.pow((beacons.get(indexes.get(1)).getIntY()-beacons.get(indexes.get(0)).getIntY()),2));
			double xx = (Math.pow(a, 2) + Math.pow(distance.get(indexes.get(0)),2) - Math.pow(distance.get(indexes.get(1)),2)) / (2*a);
			double yy = Math.sqrt(Math.pow(distance.get(indexes.get(0)),2) - Math.pow(xx, 2));
			double yyminus = -yy;
			//System.out.println(xx);
			Point x = new Point(xx,yy);
			Point xtonos = new Point(xx,yyminus);
			System.out.println(x);
			System.out.println(xtonos);
			a = Math.sqrt(Math.pow((beacons.get(indexes.get(2)).getIntX()-beacons.get(indexes.get(1)).getIntX()),2) + Math.pow((beacons.get(indexes.get(2)).getIntY()-beacons.get(indexes.get(1)).getIntY()),2));
			xx = (Math.pow(a, 2) + Math.pow(distance.get(indexes.get(1)),2) - Math.pow(distance.get(indexes.get(2)),2)) / (2*a);
			yy = Math.sqrt(Math.pow(distance.get(indexes.get(1)),2) - Math.pow(xx, 2));
			Point xdistono = new Point(xx,yy);
			Point xtreistono = new Point(xx,-yy);
			System.out.println(xdistono);
			System.out.println(xtreistono);
			if (x.equals(xdistono) || x.equals(xtreistono)){
				return x;
			}
			if (xtonos.equals(xdistono) || xtonos.equals(xtreistono)){
				return xtonos;
			}
			
			return null;
		}
		
	}
	
	
}
