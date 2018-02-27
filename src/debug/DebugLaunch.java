package debug;

import evoAlgorithm.EvoAlgorithm;
import optimization.benchmarks.OpFunction;
import util.Point;

public class DebugLaunch{
	
	public static void main(String[] args){
		System.out.println(new OpFunction.Rosenbrock().value(new Point(3, 9)));
		EvoAlgorithm e = new EvoAlgorithm();
		System.out.println(e.initEvolution());
//		System.out.println(new OpFunction.Rosenbrock().value(new Point(0.00,0.00)));
	}

}
