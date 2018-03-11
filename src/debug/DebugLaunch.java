package debug;

import java.awt.EventQueue;
import java.util.Arrays;

import assignment01.ANN;
import evoAlgorithm.EvoAlgorithm;
import optimization.benchmarks.OpFunction;
import util.Point;
import graphing.Frame;
import graphing.Graph;
import graphing.GraphElement;

public class DebugLaunch{
	
	public static void main(String[] args){
		//System.out.println(new OpFunction.Rosenbrock().value(new Point(3, 9)));
		ANN e = new ANN();
//		double[] aahdshfs = new double[12];
		double[] aahdshfs = {52.1 ,243.2 ,2.2 ,2.5 ,23 ,22 ,5.5 ,2.52, 5 ,12.6 ,12.4 ,11.5};
		double[] aaa = {11.5,11.5};
		double[] output;
		output = e.process( aahdshfs, aaa);
		int angle = 0;
		angle =90;
        new Frame(output[0]*10,output[1]*10,angle);
		//System.out.println(Arrays.toString(output));
//		System.out.println(new OpFunction.Rosenbrock().value(new Point(0.00,0.00)));
	}
	
}
