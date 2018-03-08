package assignment01;

import java.util.Random;

import lombok.Getter;

/** Evolutionary Algorithm to evolve cleaning Bots */
public class BotEvolution{
	
	/** Seed for the random Object */
	private static final int RANDOM_SEED = 12011994;
	/** Random Object for use throughout the Bot evolution */
	@Getter private Random random = new Random(RANDOM_SEED);
	
	
	/** Starts Evolutionary Process of bots, returns the best performing ANN once done */
	public ANN initEvolution(){
		final int INIT_POP = 1000;
		final double ELITE_PERCENTILE = 0.2;
		//Initialization
		Simulator sim = new Simulator(this);
		ANN[] population = new ANN[INIT_POP];
		for(int c=0; c<population.length; c++){
			population[c] = new ANN(this);
		}
		//Main evo loop
		int generations = 0;
		while(generations<1000){
			//-Fitness Evaluation Step
			//index linked array of fitnesses of individauls in the current population
			double[] fitnesses = new double[population.length];
			for(int c=0; c<population.length; c++){
				fitnesses[c] = sim.simulateFitness(population[c]);
			}
			//-Selection & Generation Step
			ANN[] newPop = new ANN[population.length];
			//Sort according to fitnesses, preserve index linking
			for(int c=0; c<population.length; c++){
				for(int c2=c+1; c2<population.length; c2++){
					if(fitnesses[c2] > fitnesses[c]){
						double helpF = fitnesses[c];
						ANN helpA = population[c];
						fitnesses[c] = fitnesses[c2];
						fitnesses[c2] = helpF;
						population[c] = population[c2];
						population[c2] = helpA;
					}
				}
			}
			//Add Elite
			for(int c=0; c<population.length*ELITE_PERCENTILE; c++){
				newPop[c] = population[c];
			}
			//Create Offspring :D (Rank Based Stupid)
			//TODO: Prog this
			//-Post Generation Processing Housekeeping
			generations++;
		}
		return null;
	}
	
	
}
