package evoAlgorithm;

/** Class representing a single individual in the Evolutionary Algorithm */
public class Individual{
	
	/** The Genotype held by the Individual */
	private Genes genes;
	
	
	/** Create new Individual with random Genotype */
	public Individual(){
		//TODO
	}
	
	/** Create new Individual based on the Genotypes of 2 parents (Crossover & Mutation) */
	public Individual(Individual mother, Individual father){
		//TODO
	}
	
	
	/** Returns the fitness value of this Individual */
	public double fitness(){
		//TODO: Write fitness function
		return -1;
	}
	
}