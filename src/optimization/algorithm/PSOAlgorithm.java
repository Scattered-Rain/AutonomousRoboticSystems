package optimization.algorithm;

import java.util.Random;

import optimization.benchmarks.OpFunction;
import util.Point;

/** Particle Swarm Optimization Algorithm */
public class PSOAlgorithm implements OpAlgorithm{
	
	/** Random Object which is used for all random operations in PSO */
	private Random random = new Random();
	
	private double aA = 0.3;
	private double bB = 0.3;
	private double cC = 0.3;
	
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
			lastVelocity[c] = new Point(0, 0);
			pBest[c] = Double.NEGATIVE_INFINITY;
		}
		//The result variables
		double bestVal = Double.NEGATIVE_INFINITY;
		Point bestPoint = null;
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
						//check if new global best
						if(pBest[c] > bestVal){
							bestVal = pBest[c];
							bestPoint = pBestPos[c];
						}
					}
				}
			}
			//Update Velocity & Position
			for(int c=0; c<particlePosition.length; c++){
				//Find gBest (list based approach)
				int numNeighbours = 3;
				Point gBestPos = null;
				double gBest = Double.NEGATIVE_INFINITY;
				for(int n=-numNeighbours; n<numNeighbours+1; n++){
					int spot = n;
					if(n!=0){
						if(c+spot < 0){
							spot = pBest.length+spot-1;
						}
						spot = (c+spot)%pBest.length;
						if(pBest[spot] > gBest){
							gBest = pBest[spot];
							gBestPos = pBestPos[spot];
						}
					}
				}
				//Calc Velocity & update position
				double r = 1;
				Point newVelocity = lastVelocity[c].multiply(aA).add(pBestPos[c].substract(particlePosition[c]).multiply(bB * r)).add(gBestPos.substract(particlePosition[c]).multiply(cC * r));
				lastVelocity[c] = newVelocity;
				Point newLoc = particlePosition[c].add(newVelocity);
				particlePosition[c] = newLoc;
			}
			System.out.println("Iteration: "+iteration+", Point: "+bestPoint+", Value: "+bestVal);
		}
		return bestPoint;
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