package debug;

import evoAlgorithm.EvoAlgorithm;
import optimization.benchmarks.OpFunction;
import util.Point;

public class DebugLaunch{
	
	public static void main(String[] args){
		EvoAlgorithm e = new EvoAlgorithm();
		e.initEvolution();
//		System.out.println(new OpFunction.Rosenbrock().value(new Point(0.00,0.00)));
	}

}
