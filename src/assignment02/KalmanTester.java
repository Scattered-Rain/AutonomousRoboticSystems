package assignment02;

import org.ejml.simple.SimpleMatrix;

/** Debug Class to test the Kalman */
public class KalmanTester{
	
	/** Debug Entry for Kalman Stuff */
	public static void main(String[] args){
		SimpleMatrix a = SimpleMatrix.identity(3);
		SimpleMatrix b = new SimpleMatrix(new double[][]{{1}, {1}, {1}});
		SimpleMatrix c = SimpleMatrix.identity(3);
		//Kalman kalman = new Kalman(a, b, c);
		
	}
	
}
