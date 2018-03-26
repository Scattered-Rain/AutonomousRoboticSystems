package assignment02;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.DefaultProcessModel;
//import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

public class KalmanFilter{
	
	public KalmanFilter(){
		Random ran = new Random();
		DescriptiveStatistics stats = new DescriptiveStatistics();
		DescriptiveStatistics dis = new DescriptiveStatistics();
		//System.out.println(ran+"   "+stats+"   "+dis);
		
		for(int i=0;i<5;i++){
			stats.clear();
			for(int j=0;j<2000;j++){
				dis.clear();
				NormalDistribution id1 = new NormalDistribution(55.0,15.0);
				dis.addValue(id1.inverseCumulativeProbability(ran.nextDouble()));
				
				NormalDistribution id2 = new NormalDistribution(88.0,30.0);
				dis.addValue(id2.inverseCumulativeProbability(ran.nextDouble()));
				
				NormalDistribution id3 = new NormalDistribution(190.0,65.0);
				dis.addValue(id3.inverseCumulativeProbability(ran.nextDouble()));
				
				stats.addValue(dis.getMax());
			}
			System.out.println("Number of elements: "+stats.getN());
			System.out.println("Mean of Max: "+stats.getMean());
			System.out.println("Standard deviation"+stats.getStandardDeviation()+"\n");
		}
	}
}