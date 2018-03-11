package assignment01;

import java.util.Random;

import assignment01.Simulator.Action;
import assignment01.Simulator.Recorder;
import graphing.Frame;
import lombok.Getter;

/** Evolutionary Algorithm to evolve cleaning Bots */
public class BotEvolution{
	
	/** Seed for the random Object */
	private static final int RANDOM_SEED = 12011994;
	/** Random Object for use throughout the Bot evolution */
	@Getter private Random random = new Random(RANDOM_SEED);
	
	
	/** Starts Evolutionary Process of bots, returns the best performing ANN once done */
	public ANN initEvolution(){
		final int INIT_POP = 100;
		final double ELITE_PERCENTILE = 0.08;
		final double TRUNCATED_PERCENTILE = 0.05;
		final double MUTATION_RATE = 0.05;
		//Initialization
		ANN[] repANNs = null; //index=3 where 0=best ANN, 1=median ANN, 2=worst ANN: ANN in population
		double[] repANNfit = null; //index=3 where 0=best ANN, 1=median ANN, 2=worst ANN: Fitness of ANN in population, index linked to repANNs
		Simulator sim = new Simulator(this);
		Recorder reec = new Recorder(sim.map);
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
						fitnesses[c] = fitnesses[c2];
						fitnesses[c2] = helpF;
						ANN helpA = population[c];
						population[c] = population[c2];
						population[c2] = helpA;
					}
				}
			}
			//Add Elite
			for(int c=0; c<(int)(population.length*ELITE_PERCENTILE); c++){
				newPop[c] = population[c];
			}
			//Create Offspring :D (Elitist Truncated Rank Based Stupid)
			for(int c=(int)(population.length*ELITE_PERCENTILE); c<population.length; c++){
				//Select mother and father
				int[] parents = new int[2];
				for(int c2=0; c2<parents.length; c2++){
					parents[c2] = random.nextInt((int)(population.length - population.length*TRUNCATED_PERCENTILE));
				}
				//Make a Baby (This method is NSFW)
				newPop[c] = ANN.crossoverAndMutation(population[parents[0]], population[parents[1]], this, 0.8, MUTATION_RATE);
			}
			//Keep track of last generations best/med/worst ANN
			repANNs = new ANN[]{population[0], population[population.length/2], population[population.length-1]};
			repANNfit = new double[]{fitnesses[0], fitnesses[fitnesses.length/2], fitnesses[fitnesses.length-1]};
			//replace old population with new Population
			population = newPop;
			//-Post Generation Processing Housekeeping
			generations++;
			//Optional Console Outs
			//System.out.println("Gen: "+(generations-1)+", Best Individual Fitness: "+repANNfit[0]+", Median Fit: "+repANNfit[1]+", Worst Fit: "+repANNfit[2]);
		}
		//System.out.println("Best Fitness: "+repANNfit[0]);
		Frame frm = new Frame(70,70,0,reec);
		Action act = new Simulator.Action(5.0,5.0,0.5);
		frm.update(act);
		return repANNs[0];
	}
	
	
}
