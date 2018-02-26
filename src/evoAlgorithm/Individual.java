package evoAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import optimization.benchmarks.OpFunction.Rosenbrock;
import optimization.benchmarks.OpFunction.Rastrigin;
import util.Point;

/** Class representing a single individual in the Evolutionary Algorithm */
public class Individual{
	private static final String BENCHMARK_FUNCTION = "ROSENBROCK";
	private static final String FITNESS_FUNCTION = "RATIO";
	
	/** Reference to the EvoAlgorithm class that uses this Individual */
	private EvoAlgorithm evo;
	/** The Genotype held by the Individual */
	private Genes genes;
	
	
	/** Create new Individual with random Genotype */
	public Individual(EvoAlgorithm evo){
		this.evo = evo;
		this.genes = new Genes(evo.getRandom());
	}
	
	/** Create new Individual based on the Genotypes of 2 parents (Crossover & Mutation) */
	public Individual(Individual mother, Individual father){
		this.evo = mother.evo;
		this.genes = new Genes(mother.genes, father.genes, evo.getRandom());
	}
	
	/** Returns Point */
	public Point getPoint(){
		return genes.pheno();
	}
	
	
	/** Returns the fitness value of this Individual */
	public double fitness(){
		double a = 50;
		String benchmarkFunction = BENCHMARK_FUNCTION;
		String fitnessFunction = FITNESS_FUNCTION;
		double x;
		double F = 0.0;
		
		switch(benchmarkFunction) {
		case "ROSENBROCK":
			switch(fitnessFunction) {
				case "POLYNOMIAL":
					x = new Rosenbrock().value(genes.pheno());
					F = x * a;
					break;
				
				case "RATIO":
					x = new Rosenbrock().value(genes.pheno());
					F = Math.pow(x, a);
					break;
			}
		
		case "RASTRIGIN":
			switch(fitnessFunction) {
			case "POLYNOMIAL":
				x = new Rastrigin().value(genes.pheno());
				F = x * a;
				break;
			
			case "RATIO":
				x = new Rastrigin().value(genes.pheno());
				F = Math.pow(x, a);
				break;
		
			}
		}
		return F;
	}
	
	//---Inner Classes---
	/** Genotype of an Individual */
	private static class Genes{
		
		/** Minimal value possible for a number in vals */
		private static final double MIN_VAL = 0.01;
		/** Maximal value possible for a number in vals */
		private static final double MAX_VAL = 100;
		/** The length of the three arrays */
		private static final int LENGTH = 128;
		/** The likelyhood of mutation to occur per operation */
		private static final double MUTATION_CHANCE_PER_OPERATION = 0.1/LENGTH;
		
		
		/** A List of raw Double values */
		private List<Double> vals;
		/** A List of Integers representing the indecis (with modulo) of vals that are added up to produce X[0] and Y[1] */
		private List<Integer>[] xyVals;
		
		
		/** Constructs new random set of genes based on naught */
		public Genes(Random random){
			List<Double> vals = new ArrayList<Double>();
			List<Integer>[] xyVals = new List[2];
			for(int c=0; c<LENGTH; c++){
				vals.add((random.nextDouble()*(MAX_VAL-MIN_VAL))+MIN_VAL);
			}
			for(int c=0; c<xyVals.length; c++){
				xyVals[c] = new ArrayList<Integer>();
				for(int c2=0; c2<LENGTH; c2++){
					xyVals[c].add(random.nextInt(LENGTH));
				}
			}
			this.vals = vals;
			this.xyVals = xyVals;
		}
		
		/** Constructs new set of genes based on a given father and mother set of genes */
		public Genes(Genes mother, Genes father, Random random){
			//Crossover
			List<Double> vals = new ArrayList<Double>();
			for(int c=0; c<mother.vals.size(); c++){
				vals.add(random.nextBoolean()?mother.vals.get(c):father.vals.get(c));
			}
			List<Integer>[] xyVals = new List[2];
			for(int c=0; c<xyVals.length; c++){
				xyVals[c] = new ArrayList<Integer>();
				for(int c2=0; c2<mother.xyVals[0].size(); c2++){
					xyVals[c].add(random.nextBoolean()?mother.xyVals[c].get(c2):father.xyVals[c].get(c2));
				}
			}
			//Mutation
			for(int c=0; c<vals.size(); c++){
				if(random.nextDouble()<=MUTATION_CHANCE_PER_OPERATION){
					vals.set(c, (random.nextDouble()*(MAX_VAL-MIN_VAL))+MIN_VAL);
				}
			}
			for(int c=0; c<xyVals.length; c++){
				for(int c2=0; c2<mother.xyVals[0].size(); c2++){
					if(random.nextDouble()<=MUTATION_CHANCE_PER_OPERATION){
						xyVals[c].set(c2, random.nextInt(LENGTH));
					}
				}
			}
			//Finally set the values
			this.vals = vals;
			this.xyVals = xyVals;
		}
		
		
		/** Returns a Point representing the expression of this set of Genes */
		public Point pheno(){
			double[] xy = new double[]{0, 0};
			List<Integer>[] lxy = xyVals;
			for(int c=0; c<lxy.length; c++){
				for(int c2=0; c2<lxy[c].size(); c2++){
					xy[c] *= vals.get(lxy[c].get(c2) % vals.size());
				}
			}
			return new Point(xy[0], xy[1]);
		}
	}
	
}