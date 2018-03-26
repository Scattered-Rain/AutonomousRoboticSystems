package assignment02;

import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

public class Filter {
	
	public static void main(String[] args){
	// discrete time interval
	double dt = 0.1d;
	// position measurement noise (meter)
	double measurementNoise = 10d;
	// acceleration noise (meter/sec^2)
	double accelNoise = 0.2d;

	// A = [ 1 dt ]
	//     [ 0  1 ]
	RealMatrix A = new Array2DRowRealMatrix(new double[][] { { 1, dt }, { 0, 1 } });
	
	// B = [ dt^2/2 ]
	//	   [ dt     ]
	RealMatrix B = new Array2DRowRealMatrix(new double[][] { { Math.pow(dt, 2d) / 2d }, { dt } });
	
	// C = [ 1 0 ]
	RealMatrix C = new Array2DRowRealMatrix(new double[][] { { 1d, 0d } });
	
	// x = [ 0 0 ]
	RealMatrix x = new Array2DRowRealMatrix(new double[][] {{ 0}, {0 }});
	
	RealMatrix xhat = x; // initial state estimate

	Random randn = new Random();
	
	
	RealMatrix tmp = new Array2DRowRealMatrix(new double[][] {
	    { Math.pow(dt, 4d) / 4d, Math.pow(dt, 3d) / 2d },
	    { Math.pow(dt, 3d) / 2d, Math.pow(dt, 2d) } });

	// Q = [ dt^4/4 dt^3/2 ]
	//     [ dt^3/2 dt^2   ]   Sw 
	RealMatrix Q = tmp.scalarMultiply(Math.pow(accelNoise, 2));
	
	RealMatrix P = Q; //P
	
	// P0 = [ 1 1 ]
	//	    [ 1 1 ]
	//RealMatrix P0 = new Array2DRowRealMatrix(new double[][] { { 1, 1 }, { 1, 1 } });
	
	// R = [ measurementNoise^2 ]  Sz sto file
	RealMatrix R = new Array2DRowRealMatrix(new double[] { Math.pow(measurementNoise, 2) });

	// constant control input, increase velocity by 0.1 m/s per cycle
	double u = 0.1d; 
	
	RealMatrix tempera = new Array2DRowRealMatrix(new double[][] { { (Math.pow(dt, 2d) / 2d) * randn.nextDouble() }, { dt*randn.nextDouble() } });

	RealMatrix ProcessNoise;
	RealMatrix y = null;
	RealMatrix Inn, sInverse, K;
	
	for(int i=0;i<2000;i++){
		ProcessNoise = tempera.scalarMultiply(accelNoise);
		
		x = A.multiply(x).add(B.scalarMultiply(u)).add(ProcessNoise);
		measurementNoise = measurementNoise * randn.nextDouble();
		y = C.multiply(x).scalarAdd(measurementNoise);
		 	 
		xhat = A.multiply(xhat).add(B.scalarMultiply(u));
		 	 
		Inn = y.subtract(C.multiply(xhat));
		RealMatrix s = C.multiply(P).multiply(C.transpose()).add(R);
		sInverse = new LUDecomposition(s).getSolver().getInverse();
		K = A.multiply(P).multiply(C.transpose()).multiply(sInverse); 
		 	 
  		xhat = xhat.add(K.multiply(Inn));
		 	 
  		P = A.multiply(P).multiply(A.transpose()).subtract(A.multiply(P).multiply(C.transpose()).multiply(sInverse).multiply(C).multiply(P).multiply(A.transpose())).add(Q);
	}
  	System.out.println(x.getEntry(0, 0));
  	System.out.println(y);
	System.out.println(xhat.getEntry(0, 0));
	System.out.println(x.getEntry(1,0));
	System.out.println(xhat.getEntry(1, 0));
	
	}


}