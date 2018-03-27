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
			for (int j = 0; j<this.occupancyGridMap[i].length; j++) {
				occupancyGridMap[i][j] = true;
				if (occupancyGridMap[i][j]) {
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
	
	

	public Tuple<List<Double>, Tuple<Point, Double>> checkBeaconsInDirection(List<Double> beaconsFound, Point location, int direction) {
		boolean beaconFound = false;
		Point obstacleCoordinates = null;
		double distance = 0.0;
		
		for(int i = 0; i<K && !beaconFound; i++) {
			location = returnCoordinates(location, direction);
			int x = (int)location.getIntX();
			int y = (int)location.getIntY();
			
			try{
				if (this.getOccupancyGridMap()[y][x]) {
					obstacleCoordinates = new Point(x, y);
					int indexOfBeaconListIndex = this.beaconsList.indexOf(obstacleCoordinates);
					
					if (indexOfBeaconListIndex != -1) {
						distance = (double)i + 1.0;
						beaconsFound.add(indexOfBeaconListIndex, distance);
						beaconFound = true;
					}
					
				}
			}catch(Exception ex){}
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
	
	public Tuple<List<Double>, ArrayList<Tuple<Point,Double>>> scanArea(Point location) {
		
		
		List<Double> beaconsFound = Arrays.asList(LaserScannerModel.initialiseArray(new Double[this.beaconsList.size()]));
		ArrayList<Tuple<Point,Double>> scan = new ArrayList<Tuple<Point,Double>>();
		
		for (int i = 0; i<K; i++) {
			Tuple<List<Double>, Tuple<Point, Double>> checkBeaconsInDirectionResults = checkBeaconsInDirection(beaconsFound, location, i);
			beaconsFound = checkBeaconsInDirectionResults.getA();
			Tuple<Point, Double> measurement = checkBeaconsInDirectionResults.getB();
			scan.add(measurement);
		}
		
		return new Tuple<List<Double>, ArrayList<Tuple<Point,Double>>>(beaconsFound, scan);
	}
	
	public List<Double> computeRangeScanLikelihood(Point location) {
		List<Double> beaconsFound = scanArea(location).getA();
		ArrayList<Tuple<Point,Double>> scan = scanArea(location).getB();
		ArrayList<Tuple<Point,Double>> actualMesaurements = calculateActualMeasurements(scan, location);
		
		double q = 1.0;
		
		for (int i = 0; i<scan.size(); i++) {
			
			
			
		}
		
		return beaconsFound;
	}
	
//	public double calculateN(ArrayList<Tuple<Point,Double>> scan, ArrayList<Tuple<Point,Double>> actualMesaurements) {
//		double calculatedZ = scan.get(i).getB();
//		double actualZ = actualMesaurements.get(i).getB();
//		double difference = calculatedZ - actualZ;
//		double s = Math.pow(difference, 2);
//	}
//	
	
	public ArrayList<Tuple<Point,Double>> calculateActualMeasurements(ArrayList<Tuple<Point,Double>> scan, Point location) {
		ArrayList<Tuple<Point,Double>> actualMesaurements = new ArrayList<Tuple<Point,Double>>();
		
		for (int i = 0; i<scan.size(); i++) {
			Point beaconCoordinates = scan.get(i).getA();
			if(beaconCoordinates != null){
				double actualMeasurement = calculateDistanceBetweenPoints(location, beaconCoordinates);
				actualMesaurements.add(new Tuple<Point,Double>(beaconCoordinates, actualMeasurement));
			}
		}
		
		return actualMesaurements;
	}
	
	public double calculateSigma(ArrayList<Tuple<Point,Double>> scan, ArrayList<Tuple<Point,Double>> actualMesaurements) {
		
		double sum = 0.0;
		int sizeOfMeasurement = scan.size();
		
		for (int i = 0; i<sizeOfMeasurement; i++) {
			double calculatedZ = scan.get(i).getB();
			double actualZ = actualMesaurements.get(i).getB();
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
	
	public static Double[] initialiseArray(Double[] ar) {
		for (int i = 0; i<ar.length; i++) {
			ar[i] = -1.0;
		}
		
		return ar;
	}
}
