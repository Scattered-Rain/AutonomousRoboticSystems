package optimization.algorithm;

import optimization.benchmarks.OpFunction;
import util.Point;

/** Interface for Optimization Algorithms in 2D Space */
public interface OpAlgorithm{
	
	/** Optimizes the given Function, returns Point of best found location */
	public Point optimize(OpFunction toOptimize);
	
}
