package assignment02;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Setter;

import org.ejml.simple.SimpleMatrix;

import util.Point;
import assignment01.Kinematics;

/** Class that handles all the Kalman Calcualtions */
@AllArgsConstructor public class Kalman{
	
	
	/** A Matrix */
	@Setter private SimpleMatrix a;
	/** B Matrix */
	@Setter private SimpleMatrix b;
	/** C Matrix */
	@Setter private SimpleMatrix c;
	
	
	/** Memory of x */
	@Setter private SimpleMatrix xLast;
	/** Memory of sigma */
	@Setter private SimpleMatrix lastSigma;
	/** Memory of mu */
	@Setter private SimpleMatrix lastMu;
	
	
	/** Does Kalman Processing */
	public SimpleMatrix[] doTheKalman(double[] control, SimpleMatrix procNoise, SimpleMatrix measureNoise, List[] beacons){
		SimpleMatrix u = new SimpleMatrix(new double[][]{
				{control[0]},
				{control[1]},
				{control[2]}
		});
		//Prep x & z
		SimpleMatrix x = a.mult(xLast)	.plus(b.mult(u))	.plus(procNoise);
		//SimpleMatrix z = c.mult(x)	.plus(measureNoise);
		//Prepare observation z
		double mX = x.get(0, 0);
		double mY = x.get(1, 0);
		double mT = x.get(2, 0);
		List<Point> bl = beacons[0];
		List<Double> bld = beacons[1];
		for(int c=0; c<beacons[0].size(); c++){
			if(bld.get(c)!=-1){
				//There is a beacon of id c seen
				Point loc = bl.get(c);
				double xDist = mX-loc.getX();
				double yDist = mY-loc.getY();
				double dist = Math.sqrt((Math.pow(xDist, 2) + Math.pow(yDist, 2))) - bld.get(c);
				mX += (xDist/(xDist+yDist)) * dist;
				mY += (yDist/(xDist+yDist)) * dist;
			}
		}
		SimpleMatrix z = new SimpleMatrix(new double[][]{
				{mX},
				{mY},
				{mT}
		});
		//R & Q Variance-Covariance Matrix
		SimpleMatrix r = buildVcMatrix(procNoise);
		SimpleMatrix q = buildVcMatrix(measureNoise);
		//Prediction
		SimpleMatrix muHat = a.mult(lastMu)	.plus(b.mult(u));
		SimpleMatrix sigmaHat = a.mult(lastSigma).mult(a.transpose())	.plus(r);
		//Correction
		SimpleMatrix k = sigmaHat.mult(c.transpose())	.mult((c.mult(sigmaHat).mult(c.transpose()).plus(q)).invert());
		SimpleMatrix mu = muHat	.plus(k.mult(z.minus(c.mult(muHat))));
		SimpleMatrix sigma = (SimpleMatrix.identity(sigmaHat.numRows()).minus(k.mult(c))).mult(sigmaHat);
		//Memorize some variables
		this.xLast = x;
		this.lastSigma = sigma;
		this.lastMu = mu;
		//Return
		return new SimpleMatrix[]{mu, sigma};
	}
	
	/** Returns Varaiance-Covariance Matrix of given error */
	private SimpleMatrix buildVcMatrix(SimpleMatrix error){
		return error.transpose().mult(error).scale(1d/error.numRows());
	}
	
	
	/** Advances the Kalman filter in the Context of the Assignment */
	public double[] iterateAssignmentKalman(double[] beaconMeasures, double[] wheelControls){
		//compute wheel controls to translate to linear delta of [movement, angle]
		double[] kinMov = Kinematics.calculatePosition(new Point(wheelControls[0], wheelControls[1]), new Point(0, 0), 0);
		double kinDist = Math.sqrt((Math.pow(kinMov[0], 2)+Math.pow(kinMov[1], 2)));
		double[] deltaControls = new double[]{kinDist, kinMov[2]};
		return null;
	}
	
	
	/** Sets up and prepares new Kalman filter to be suited for this second assignment */
	public static Kalman prepKalman(double[] initX){
		SimpleMatrix a = new SimpleMatrix(new double[][]{
				{1, 0, 0},
				{0, 1, 0},
				{0, 0, 1}
		});
		SimpleMatrix c = new SimpleMatrix(new double[][]{
				{1, 0, 0},
				{0, 1, 0},
				{0, 0, 1}
		});
		SimpleMatrix b = new SimpleMatrix(new double[][]{
				{1, 0, 0},
				{0, 1, 0},
				{0, 0, 1}
		});
		SimpleMatrix x = new SimpleMatrix(new double[][]{
				{initX[0], 0, 0},
				{0, initX[1], 0},
				{0, 0, initX[2]}
		});
		SimpleMatrix sigma = new SimpleMatrix(new double[][]{
				{initX[0], 0, 0},
				{0, initX[1], 0},
				{0, 0, initX[2]}
		});
		SimpleMatrix mu = new SimpleMatrix(new double[][]{
				{initX[0], 0, 0},
				{0, initX[1], 0},
				{0, 0, initX[2]}
		});
		return new Kalman(a, b, c, x, sigma, mu);
	}
	
}
