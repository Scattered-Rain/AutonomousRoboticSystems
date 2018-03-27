package assignment01;

import util.Point;

public class DiscoveredBeacon {
	private Point location;
	private double distanceFromRobot;
	private int identifiedDirection;
	
	public DiscoveredBeacon(Point location, double distanceFromRobot, int identifiedDirection) {
		this.location = location;
		this.distanceFromRobot = distanceFromRobot;
		this.identifiedDirection = identifiedDirection;
	}
	
	public Point getLocation() {
		return this.location;
	}
	
	public double getDistanceFromRobot() {
		return this.distanceFromRobot;
	}
	
	public int getIdentifiedDirection() {
		return this.identifiedDirection;
	}
	
	public void setLocation(Point location) {
		this.location = location;
	}
	
	public void setDistanceFromRobot(double distanceFromRobot) {
		this.distanceFromRobot = distanceFromRobot;
	}
	
	public void setIdentifiedDirection(int identifiedDirection) {
		this.identifiedDirection = identifiedDirection;
	}
}
