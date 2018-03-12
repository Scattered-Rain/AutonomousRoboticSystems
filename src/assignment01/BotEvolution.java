package assignment01;

import graphing.Frame;

import java.util.Arrays;
import java.util.Random;

import assignment01.Simulator.Action;
import assignment01.Simulator.Recorder;
import lombok.Getter;
import util.Tuple;

/** Evolutionary Algorithm to evolve cleaning Bots */
public class BotEvolution{
	
	/** Seed for the random Object */
	private static final int RANDOM_SEED = 12011994;
	/** Random Object for use throughout the Bot evolution */
	@Getter private Random random = new Random(RANDOM_SEED);
	
	private final int MAP_SIZE = 12;
	private final int OBS = (MAP_SIZE*MAP_SIZE)/4;

	/** Starts Evolutionary Process of bots, returns the best performing ANN once done */
	public ANN20 initEvolution(){
		final int INIT_POP = 300;
		final double ELITE_PERCENTILE = 0.08;
		final double TRUNCATED_PERCENTILE = 0.1;
		final double MUTATION_RATE = 0.005;
		//Initialization
		final ANN20[] repANNs = new ANN20[3]; //index=3 where 0=best ANN, 1=median ANN, 2=worst ANN: ANN in population
		double[] repANNfit = null; //index=3 where 0=best ANN, 1=median ANN, 2=worst ANN: Fitness of ANN in population, index linked to repANNs
		Simulator sim = new Simulator(this, makeMap(MAP_SIZE, MAP_SIZE, OBS));
		final ANN20[] population = new ANN20[INIT_POP];
		for(int c=0; c<population.length; c++){
			population[c] = new ANN20(27, 2, 8);
		}
		//Main evo loop
		int generations = 0;
		while(generations<100000){
			//-Fitness Evaluation Step
			//index linked array of fitnesses of individauls in the current population
			final double[] fitnesses = new double[population.length];
			final int threads = 5;
			Thread[] rThreads = new Thread[threads];
			final boolean[] done = new boolean[threads];
			for(int ts=0; ts<threads; ts++){
				final int from = (population.length/threads)*ts;
				final int to = (population.length/threads)*(ts+1);
				final int id = ts;
				try{
					//Create new Thread to run part of the fitnessees
					rThreads[ts] = new Thread(new Runnable(){
						//runs it's part
						public void run(){
							for(int c=from; c<to; c++){
								fitnesses[c] = sim.simulateFitness(population[c], false);
							}
							done[id] = true;
						}
					});
					//starts thread
					rThreads[ts].start();
				}catch(Exception ex){}
			}
			boolean threadsDone = false;
			while(!threadsDone){
				threadsDone = true;
				for(int c=0; c<done.length; c++){
					if(!done[c]){
						threadsDone = false;
					}
				}
			}
			
			//-Selection & Generation Step
			ANN20[] newPop = new EvoAlgorithm().selection("TOURNAMENT", this, population, fitnesses);
			
			
//			//-Selection & Generation Step
//			ANN20[] newPop = new ANN20[population.length];
//			//Sort according to fitnesses, preserve index linking
//			for(int c=0; c<population.length; c++){
//				for(int c2=c+1; c2<population.length; c2++){
//					if(fitnesses[c2] > fitnesses[c]){
//						double helpF = fitnesses[c];
//						fitnesses[c] = fitnesses[c2];
//						fitnesses[c2] = helpF;
//						ANN20 helpA = population[c];
//						population[c] = population[c2];
//						population[c2] = helpA;
//					}
//				}
//			}
//			//Add Elite
//			for(int c=0; c<(int)(population.length*ELITE_PERCENTILE); c++){
//				newPop[c] = population[c];
//			}
//			//Create Offspring :D (Elitist Truncated Rank Based Stupid)
//			for(int c=(int)(population.length*ELITE_PERCENTILE); c<population.length; c++){
//				//Select mother and father
//				int[] parents = new int[2];
//				for(int c2=0; c2<parents.length; c2++){
//					parents[c2] = random.nextInt((int)(population.length - population.length*TRUNCATED_PERCENTILE));
//				}
//				//Make a Baby (This method is NSFW)
//				newPop[c] = ANN20.crossoverAndMutation(population[parents[0]], population[parents[1]], this, 0.8, MUTATION_RATE);
//			}
			
			
			
			
			//Keep track of last generations best/med/worst ANN
			repANNs[0] = population[0];
			repANNs[1] = population[population.length/2];
			repANNs[2] = population[population.length-1];
			repANNfit = new double[]{fitnesses[0], fitnesses[fitnesses.length/2], fitnesses[fitnesses.length-1]};
			//replace old population with new Population
			for(int c=0; c<population.length; c++){
				population[c] = newPop[c];
			}
			//-Post Generation Processing Housekeeping
			generations++;
			//Coevolution of the map
			sim.setMap(makeMap(MAP_SIZE, MAP_SIZE, OBS));
			//Optional Console Outs
			System.out.println("Gen: "+(generations-1)+", Best Individual Fitness: "+repANNfit[0]+", Median Fit: "+repANNfit[1]+", Worst Fit: "+repANNfit[2]);
			//System.out.println(sim.getSimRecords().get(sim.getSimRecords().size()-1));
			//System.out.println();
			//draw best individual
			if((generations==10 || generations%100==0) && generations!=0){
				try{
					new Thread(new Runnable(){
						//runs it's part
						public void run(){
							sim.simulate(repANNs[0], repANNs[0].getSimSeed(), true);
							Simulator.Recorder rec = sim.getSimRecords().get(sim.getSimRecords().size()-1);
							Frame f = new Frame(600, 400, 0, rec);
							for(Action a : rec.getActions()){
								f.update(a);
								try{Thread.sleep(100);}catch(Exception ex){}
							}
						}
					}).start();
				}catch(Exception ex){}
			}
			//write population to file
			if(generations%100==0){
				for(int c=0; c<population.length; c++){
					population[c].dump("test"+c);
				}
			}
		}
		
		System.out.println("Best Fitness: "+repANNfit[0]);
		return repANNs[0];
	}
	
	/** Creates new Map */
	private boolean[][] makeMap(int width, int height, int obstacles){
		boolean[][] map = new boolean[height][width];
		for(int c=0; c<obstacles; c++){
			int x, y;
			do{
				x = random.nextInt(map[0].length);
				y = random.nextInt(map.length);
			}while(map[y][x]);
			map[y][x] = true;
		}
		return map;
	}
	
	
	
	
}