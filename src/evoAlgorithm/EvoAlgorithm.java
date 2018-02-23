package evoAlgorithm;

import java.util.ArrayList;
import java.util.List;

import util.Tuple;

/** The Core class of the Evolutionary algorithm, where all the magic is supposed to happen. Sofia. */
public class EvoAlgorithm {
	
	/** The actual method containing the Evolutionary Algorithm, return type should correspond to whatever the algorithm optimizes */
	public void initEvolution(){
		//TODO: Actually write this
	}
	
	/** The Selection algorithm, uses some method to decide which Individuals are fittest, and uses them as a base to create the next generation */
	private List<Individual> selection(List<Tuple<Individual, Double>> evaluatedPopulation){
		List<Individual> out = new ArrayList<Individual>();
		//TODO: Use some election algorithm to find best individuals. Discard the worst. It's up to you whether you wanna keep some of the last generation.
		//TODO: Fill the new generation with offspring generated using them (Just use the Individual(mother, father) constructor), make sure the parents are actually different individuals :P
		//TODO: Put new generation in out List and return it
		return out;
	}
	
	/** Given a list of Individuals, this method generates a List which contains Tuples containing the individuals of the given population and a double value representing the fitness of that individual. The list is sorted highest to lowest based on that fitness value */
	private List<Tuple<Individual, Double>> generateFitnessList(List<Individual> population){
		List<Tuple<Individual, Double>> out = new ArrayList<Tuple<Individual, Double>>();
		for(int c=0; c<population.size(); c++){
			out.add(new Tuple<Individual, Double>(population.get(c), population.get(c).fitness()));
		}
		for(int c=0; c<out.size(); c++){
			for(int c2=c+1; c2<out.size(); c2++){
				if(out.get(c).getB()<out.get(c2).getB()){
					Tuple<Individual, Double> help = out.get(c);
					out.set(c, out.get(c2));
					out.set(c2, help);
				}
			}
		}
		return out;
	}
	
}
