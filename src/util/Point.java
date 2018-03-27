package util;

import lombok.Getter;
import lombok.Setter;

/** 2 Dimensional Double Object */
public class Point{
	
	/** Epsilon value for comparisons of float values */
	public static final double EPSILON = 0.0000001f;
	
	
	/** The x value this Point holds */
	@Getter private double x;
	/** The y value this Point holds */
	@Getter private double y;
	
	
	/** Constructs new Point given the x|y */
	public Point(double x, double y){
		this.x = x;
		this.y = y;
		if(Math.abs(x)<EPSILON){
			this.x = 0;
		}
		if(Math.abs(y)<EPSILON){
			this.y = 0;
		}
	}
	
	/** Returns the angle between this vector (xy) to the given where scaled to 1, i.e. 360 degrees = 1.0*/
	public double calcAngle(Point vector){
		return (Math.toDegrees(Math.acos(((x*y) + (vector.x*vector.y))/(Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2))*Math.sqrt(Math.pow(vector.x, 2)+Math.pow(vector.y, 2)))))%360)/360;
	}
	
	/** Returns the distances between this and the given point */
	public double distance(Point point){
		return Math.sqrt(Math.pow(x-point.x, 2)+Math.pow(y-point.y, 2));
	}
	
	/** Constructs new Point given an single XY */
	public Point(double xy){
		this(xy, xy);
	}
	
	/** Adds given values to this Point */
	public Point add(double x, double y){
		return new Point(this.x+x, this.y+y);
	}
	
	/** Adds given Point to this Point */
	public Point add(Point point){
		return add(point.x, point.y);
	}
	
	/** Adds given value to this Point */
	public Point add(float xy){
		return add(xy, xy);
	}
	
	/** Substracts given Point from this Point */
	public Point substract(Point point){
		return add(-point.getX(), -point.getY());
	}
	
	/** Multiplies given values with this Point */
	public Point multiply(double x, double y){
		return new Point(this.x*x, this.y*y);
	}
	
	/** Multiplies given Point with Point */
	public Point multiply(Point point){
		return multiply(point.x, point.y);
	}
	
	/** Multiplies given value with this Point */
	public Point  multiply(double xy){
		return multiply(xy, xy);
	}
	
	/** Normalizes this Point (x+y = 1) */
	public Point normalize(){
		if(x==0 && y==0){
			return new Point(0, 0);
		}
		else{
			int bx = x==0?0:(int)(Math.abs(x)/x);
			int by = y==0?0:(int)(Math.abs(y)/y);
			double wx = Math.abs(x);
			double wy = Math.abs(y);
			double sum = wx+wy;
			double nx = (wx/sum)*bx;
			double ny = (wy/sum)*by;
			return new Point(nx, ny);
		}
	}
	
	/** Returns the average Point between this Point and the given Point */
	public Point avg(Point point){
		return new Point((x+point.getX())/2, (y+point.getY())/2);
	}
	
	/** Returns whether the the given point is (more or less) equivalent to this point */
	public boolean equals(Point point){
		return Math.abs(point.getX()-x)<=EPSILON && Math.abs(point.getY()-y)<=EPSILON;
	}
	
	/** Returns X value as Integer */
	public int getIntX(){
		return (int) x;
	}
	
	/** Returns Y value as Integer */
	public int getIntY(){
		return (int) y;
	}
	
	/** Returns deep clone of this Point */
	public Point clone(){
		return new Point(x, y);
	}
	
	/** Returns this Point as String */
	public String toString(){
		return "["+x+"|"+y+"]";
	}
	
}