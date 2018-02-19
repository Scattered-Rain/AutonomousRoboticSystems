package optimization.algorithm;

import optimization.benchmarks.OpFunction;
import util.Point;

/** Particle Swarm Optimization Algorithm */
public class PSOAlgorithm implements OpAlgorithm{
	
	/** The Number of Particles this instance of the PSO uses */
	private int numberOfParticles = 64;
	/** The Max Number of Iterations this instance of the PSO will run (not including maxOptimazationDeltaIterations), -1=infinity */
	private int maxNumberOfIterations = 128;
	/** The Max Number of Iterations without improvement in the best value after which the PSO will stop  (if maxNumberIterations reached or -1) */
	private double maxOptimazationDeltaIterations = 16;
	
	
	/** Optimizes given Function */
	public Point optimize(OpFunction toOptimize){
		//TODO: Write the actual algorithm :P
		return null;
	}
	
	
	/** Set Number of Particles */
	public PSOAlgorithm setNumberOfParticles(int particles){
		this.numberOfParticles = particles;
		return this;
	}
	
	/** Set the max Number of Iterations (not including optimiazation delta iterations */
	public PSOAlgorithm setMaxNumberOfIterations(int maxIterations){
		this.maxNumberOfIterations = maxIterations;
		return this;
	}
	
	/** Set the max Number of Iterations without improvement that the algorithm will stop after max number of iterations is reached */
	public PSOAlgorithm setMaxOptimizationDeltaIterations(int maxOpDeltaIterations){
		this.maxOptimazationDeltaIterations = maxOpDeltaIterations;
		return this;
	}
	
}
