package debug;

import optimization.algorithm.PSOAlgorithm;
import optimization.benchmarks.OpFunction;
import util.Point;

public class DebugLaunch{
	
	public static void main(String[] args){
		PSOAlgorithm pso = new PSOAlgorithm().setMaxNumberOfIterations(1000);
		Point best = pso.optimize(new OpFunction.InvertFunction(new OpFunction.Rosenbrock().setA(1)));
	}

}
