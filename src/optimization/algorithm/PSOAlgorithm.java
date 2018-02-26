package optimization.algorithm;

import java.util.Random;

import optimization.benchmarks.OpFunction;
import util.Point;

/** Particle Swarm Optimization Algorithm */
public class PSOAlgorithm implements OpAlgorithm{
	
	/** Random Object which is used for all random operations in PSO */
	private Random random = new Random();
	
	private double a = 0.3;
	private double b = 0.3;
	private double c = 0.3;
	
	/** The Number of Particles this instance of the PSO uses */
	private int numberOfParticles = 64;
	/** The Max Number of Iterations this instance of the PSO will run */
	private int maxNumberOfIterations = 128;
	/** The Range from which the particles are spawned at (around 0|0) */
	private double spawnRange = 128;
	
	
	/** Optimizes given Function */
	public Point optimize(OpFunction toOptimize){
		//The particles, position and pBest, index linked
		Point[] particlePosition = new Point[numberOfParticles];
		Point[] pBestPos = new Point[numberOfParticles];
		double[] pBest = new double[numberOfParticles];
		Point[] lastVelocity = new Point[numberOfParticles];
		//Spawn
		for(int c=0; c<particlePosition.length; c++){
			particlePosition[c] = new Point(random.nextDouble()*spawnRange*2-spawnRange, random.nextDouble()*spawnRange*2-spawnRange);
		}
		//The Actual PSO Loop
		for(int iteration=0; iteration<maxNumberOfIterations; iteration++){
			//Figure out all new fitness (and update pBest)
			int newBestIndex = -1;
			for(int c=0; c<particlePosition.length; c++){
				double currentP = toOptimize.value(particlePosition[c]);
				if(currentP > pBest[c]){
					pBest[c] = currentP;
					pBestPos[c] = particlePosition[c];
					if(newBestIndex==-1 || pBest[c] > pBest[newBestIndex]){
						newBestIndex = c;
					}
				}
			}
			//Update Velocity & Position
			for(int c=0; c<particlePosition.length; c++){
				//Find gBest (list based approach)
				int numNeighbours = 3;
				Point gBestPos = null;
				double gBest = Double.NEGATIVE_INFINITY;
				for(int n=-numNeighbours; n<numNeighbours; n++){
					int spot = n;
					if(n!=0){
						if(c-spot < 0){
							spot = numNeighbours-spot;
						}
						if(pBest[spot] > gBest){
							gBest = pBest[spot];
							gBestPos = pBestPos[spot];
						}
					}
				}
				//Calc Velocity
				double r = 1;
				Point newVelocity = lastVelocity[c].multiply(a).add(pBestPos[c].substract(particlePosition[c]).multiply(b * r)).add(gBestPos.substract(particlePosition[c]).multiply(c * r));
				lastVelocity[c] = newVelocity;
			}
		}
		//Return best found point
		return null;
	}
	
	
	/** Set Number of Particles */
	public PSOAlgorithm setNumberOfParticles(int particles){
		this.numberOfParticles = particles;
		return this;
	}
	
	/** Set the max Number of Iterations */
	public PSOAlgorithm setMaxNumberOfIterations(int maxIterations){
		this.maxNumberOfIterations = maxIterations;
		return this;
	}
	
	/** Set the spawn range around 0|0 */
	public PSOAlgorithm setSpawnRange(double range){
		this.spawnRange = range;
		return this;
	}
	
}