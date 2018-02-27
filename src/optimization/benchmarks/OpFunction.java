package optimization.benchmarks;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import util.Point;

/** The Interface representing Optimizable Functions (Benchmarks) in 2D Space */
public interface OpFunction{
	
	
	/** Returns the value of this function at given X|Y */
	public double value(double x, double y);
	
	/** Returns the value of this function at given Point */
	public default double value(Point point){
		return value(point.getX(), point.getY());
	}
	
	
	//---inner classes---
	/** Meta Function that inverts the value given by held Function (If build with constructed with Function, returns -Rosenbrock) */
	@AllArgsConstructor public static class InvertFunction implements OpFunction{
		/** The Function that is Inverted */
		private OpFunction function;
		/** Returns inverse of the value of the held Function, i.e. value of 5 -> -5 etc. */
		public double value(double x, double y){
			return -function.value(x, y);
		}
	}
	
	/** Implementation of a 2 dimensional Rosenbrock Function */
	@NoArgsConstructor public static class Rosenbrock implements OpFunction{
		/** a value -> minimum at (a, a^2)*/
		private double a = 3;
		/** b value (doesn't influence minimum?) */
		private double b = 100;
		/**Constructs new Rosenbrock Function with given A and B values */
		public Rosenbrock(double a, double b){
			this.a = a;
			this.b = b;
		}
		/** Returns value at X|Y */
		public double value(double x, double y){
			double out = Math.pow((a - x), 2) + (b*Math.pow((y - Math.pow(y, 2)), 2));
			return out;
		}
	}
	
	/** Implementation of a 2 dimensional Rastrigin Function (Known global minimum at 0|0) */
	public static class Rastrigin implements OpFunction{
		/** Returns value at X|Y */
		public double value(double x, double y){
			double[] vals = new double[]{x, y};
			double out = 10*vals.length;
			for(int c=0; c<vals.length; c++){
				out += Math.pow(vals[c], 2) - 10*Math.cos(2*Math.PI*vals[c]);
			}
			return out;
		}
	}
	
}
