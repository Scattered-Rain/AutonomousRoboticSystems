package debug;

import graphing.Panel;
import optimization.algorithm.PSOAlgorithm;
import optimization.benchmarks.OpFunction;
import util.Point;

public class DebugLaunch{
	
	public static void main(String[] args){
		OpFunction f = new OpFunction.InvertFunction(new OpFunction.Rosenbrock().setA(5));
		PSOAlgorithm pso = new PSOAlgorithm().setMaxNumberOfIterations(10000);
		Point best = pso.optimize(f);
		System.out.println(best+" "+f.value(best));
	}

}
