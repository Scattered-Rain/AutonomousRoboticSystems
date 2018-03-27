package assignment01;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import util.Point;
import util.Tuple;

public class LaserScannerModel {
	public static final int K = 8;
	public static final int B = 5;
	
	private boolean[][] occupancyGridMap;
	private ArrayList<Point> beaconsList;
	
	
	public LaserScannerModel(boolean[][] occupancyGridMap) {
		this.occupancyGridMap = occupancyGridMap;
		this.beaconsList = createBeaconsList();
		
	}
	
	/**
	 * Creates an ArrayList with all the obstacle beacons that appear on the environment
	 * @return
	 */
	public ArrayList<Point> createBeaconsList() {
		ArrayList<Point> beaconsList = new ArrayList<Point>();
		
		for (int i = 0; i<this.occupancyGridMap.length; i++) {
			for (int j = 0; i<this.occupancyGridMap[i].length; j++) {
				if (occupancyGridMap[i][j] == true) {
					beaconsList.add(new Point(i, j));
				}
			}
		}
		
		return beaconsList;
	}
	
	public boolean[][] getOccupancyGridMap() {
		return this.occupancyGridMap;
	}
	
	public void setOccupancyGridMap(boolean[][] occupancyGridMap) {
		this.occupancyGridMap = occupancyGridMap;
	}
	
	

	public Tuple<ArrayList<DiscoveredBeacon>, Tuple<Point, Double>> checkBeaconsInDirection(ArrayList<DiscoveredBeacon> discoveredBeacons, Point location, int direction) {
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
					discoveredBeacons.set(indexOfBeaconListIndex, discoveredBeacon);
					beaconFound = true;
				}
				
			}
		}
		
		return new Tuple<ArrayList<DiscoveredBeacon>, Tuple<Point, Double>>(discoveredBeacons, new Tuple<Point, Double>(obstacleCoordinates, distance));
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
	
	public Tuple<ArrayList<DiscoveredBeacon>, ArrayList<Tuple<Point,Double>>> scanArea(Point location) {
		ArrayList<DiscoveredBeacon> beaconsFound = initialiseBeaconsArrayList();
		ArrayList<Tuple<Point,Double>> scan = new ArrayList<Tuple<Point,Double>>();
		
		for (int i = 0; i<K; i++) {
			Tuple<ArrayList<DiscoveredBeacon>, Tuple<Point, Double>> checkBeaconsInDirectionResults = checkBeaconsInDirection(beaconsFound, location, i);
			beaconsFound = checkBeaconsInDirectionResults.getA();
			Tuple<Point, Double> measurement = checkBeaconsInDirectionResults.getB();
			scan.add(measurement);
		}
		
		return new Tuple<ArrayList<DiscoveredBeacon>, ArrayList<Tuple<Point,Double>>>(beaconsFound, scan);
	}
	
	public double computeRangeScanLikelihood(Point location) {
		ArrayList<DiscoveredBeacon>  beaconsFound = scanArea(location).getA();
		ArrayList<Tuple<Point,Double>> scan = scanArea(location).getB();
		ArrayList<Tuple<Point,Double>> actualMeasurements = calculateActualMeasurements(scan, location);
		double sigma = calculateSigma(scan, actualMeasurements);		
		double q = 1.0;
		
		for (int i = 0; i<scan.size(); i++) {
			double calculatedZ = scan.get(i).getB();
			double actualZ = actualMeasurements.get(i).getB();
			double N = calculateN(calculatedZ, actualZ, sigma);
			
			
		}
		
		return 0.55555;
		
		
		
	}
	
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
	
}
