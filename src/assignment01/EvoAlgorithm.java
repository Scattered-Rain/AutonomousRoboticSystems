package assignment01;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import util.Tuple;

public class EvoAlgorithm {
	public static final double ELITE_PERCENTILE = 0.08;
	public static final double POPULATION_PROPORTION_TO_BE_COVERED_BY_OFFSPRINGS = 0.20;
	public static final double MUTATION_RATE = 0.05;
	
	@Getter private Random random = new Random();
	
	/**
	 * The Selection algorithm, applies some techniques to decide which individuals are the fittest
	 * and uses them as a base to create the next generation. There are five different types of algorithms that can be used: 
	 * Elitist, Proportionate, Rank-Based, Truncated Rank-Based & Tournament
	 * 
	 * @param selection - A string that determines which algorithm will be used for the Selection process
	 * @param botEvolution - An object that denotes the current Bot Evolution session
	 * @param population - An array of ANNs that correspond to the different individuals
	 * @param fitnesses - An array with the fitness values of the different individuals
	 * @return - The new version of the population
	 */
	public ANN[] selection(String selection, BotEvolution botEvolution, ANN[] population, double[] fitnesses){
		int numberofIndivuduals = population.length;
		ANN[] out = new ANN[numberofIndivuduals];
		
		switch(selection) {
			case "ELITIST":
				out = elitistSelection(botEvolution, population, fitnesses);
				break;
			
			case "PROPORTIONATE":
				out = proportionateSelection(botEvolution, population, fitnesses);
				break;
				
			case "RANK-BASED":
				out = rankBasedSelection(botEvolution, population, fitnesses);
				break;
				
			case "TRUNCATED RANK-BASED":
				out = truncatedRankBasedSelection(botEvolution, population, fitnesses);
				break;
				
			case "TOURNAMENT":
				out = tournamentSelection(botEvolution, population, fitnesses);
				break;
		}
		
		return out;
	}
	
	/**
	 * An implementation of the Elitist Selection algorithm. The best individuals from the current population are
	 * added to the new version of the population. Later, couples of individuals are drawn randomly from
	 * the population and are used for reproduction.
	 * 
	 * @param botEvolution - An object that denotes the current Bot Evolution session
	 * @param population - An array of ANNs that correspond to the different individuals
	 * @param fitnesses - An array with the fitness values of the different individuals
	 * @return - The new version of the population
	 */
	public ANN[] elitistSelection(BotEvolution botEvolution, ANN[] population, double[] fitnesses){
		int numberofIndivuduals = population.length;
		int numberOfSelectionsToBeMade = (int)Math.round(numberofIndivuduals * ELITE_PERCENTILE);
		int numberOfOffspringsToBeGenerated = numberofIndivuduals - numberOfSelectionsToBeMade;
		ANN[] selectedIndividuals = new ANN[numberOfSelectionsToBeMade];
		Tuple<ANN[], double[]> rankedIndividuals = EvoAlgorithm.rankIndividuals(population, fitnesses);
		population = rankedIndividuals.getA();
		fitnesses = EvoAlgorithm.rankIndividuals(population, fitnesses).getB();
		
		//Add Elite
		for (int c = 0; c < (int)(population.length * ELITE_PERCENTILE); c++){
			selectedIndividuals[c] = population[c];
		}
				
		ANN[] newGeneration = makeOffsprings(botEvolution, population, numberOfOffspringsToBeGenerated);
		ANN[] newPop = new ANN[numberofIndivuduals];
		System.arraycopy(selectedIndividuals, 0, newPop, 0, selectedIndividuals.length);
		System.arraycopy(newGeneration, 0, newPop, selectedIndividuals.length, newGeneration.length);
		return newPop;
	}
	
	/**
	 * An implementation of the Proportionate Selection algorithm. Each individual is associated with a proportion with
	 * respect to the total population fitness (p(i) = f(i)/Sf). The individuals to be reproduced are then selected using
	 * a roulette wheel and, after they are coupled in pairs, they produce their offsprings which form the "new generation".
	 * The non-selected individuals are discarded and the new version of the population is consisted of only the previously
	 * selected individuals and the new generation.
	 * 
	 * @param botEvolution - An object that denotes the current Bot Evolution session
	 * @param population - An array of ANNs that correspond to the different individuals
	 * @param fitnesses - An array with the fitness values of the different individuals
	 * @return - The new version of the population
	 */
	public ANN[] proportionateSelection(BotEvolution botEvolution, ANN[] population, double[] fitnesses){
		int numberofIndivuduals = population.length;
		int numberOfSelectionsToBeMade = (int)Math.round(numberofIndivuduals * ELITE_PERCENTILE);
		int numberOfOffspringsToBeGenerated = numberofIndivuduals - numberOfSelectionsToBeMade;
		double populationFitness = calculatePopulationFitness(fitnesses);
		double[] proportionsPerIndividual = new double[population.length];
		
		for (int i = 0; i < numberofIndivuduals; i++) {
			double fitness = fitnesses[i];
			double proportion = (double)fitness/(double)populationFitness;
			proportionsPerIndividual[i] = proportion;
		}
		
		ANN[] selectedIndividuals = useRouletteWheel(population, proportionsPerIndividual);
		ANN[] newGeneration = makeOffsprings(botEvolution, selectedIndividuals, numberOfOffspringsToBeGenerated);
		ANN[] newPop = new ANN[numberofIndivuduals];
		System.arraycopy(selectedIndividuals, 0, newPop, 0, selectedIndividuals.length);
		System.arraycopy(newGeneration, 0, newPop, selectedIndividuals.length, newGeneration.length);
		return newPop;
	}
	
	/**
	 * An implementation of the Rank-Based Selection algorithm. All the individuals are sorted based on their fitness value
	 * from the best to worse. The rank in which each individual ends up after the sorting is used to associate them with a
	 * proportion (p(i) = (1 - r(i))/Sr). The individuals to be reproduced are then selected using a roulette wheel and after 
	 * they are coupled in pairs, they produce their offsprings that form the "new generation". The non-selected individuals are 
	 * discarded and the new version of the population is consisted of only the previously selected individuals and the new 
	 * generation.
	 * 
	 * @param botEvolution - An object that denotes the current Bot Evolution session
	 * @param population - An array of ANNs that correspond to the different individuals
	 * @param fitnesses - An array with the fitness values of the different individuals
	 * @return - The new version of the population
	 */
	public ANN[] rankBasedSelection(BotEvolution botEvolution, ANN[] population, double[] fitnesses){
		int numberofIndivuduals = population.length;
		int numberOfSelectionsToBeMade = (int)Math.round(numberofIndivuduals * ELITE_PERCENTILE);
		int numberOfOffspringsToBeGenerated = numberofIndivuduals - numberOfSelectionsToBeMade;
		ANN[] rankedIndividuals = rankIndividuals(population, fitnesses).getA();
		int sumOfRanks = calculateSumOfRanks(rankedIndividuals);
		double[] proportionsPerIndividual = new double[numberofIndivuduals];

		for (int i = 0; i < rankedIndividuals.length; i++) {
			int rank = (rankedIndividuals.length - i) + 1;
			double proportion = ((double)(rank/(double)sumOfRanks));
			proportionsPerIndividual[i] = proportion;
		}
		
		ANN[] selectedIndividuals = useRouletteWheel(population, proportionsPerIndividual);
		ANN[] newGeneration = makeOffsprings(botEvolution, selectedIndividuals, numberOfOffspringsToBeGenerated);
		ANN[] newPop = new ANN[numberOfSelectionsToBeMade + numberOfOffspringsToBeGenerated];
		System.arraycopy(selectedIndividuals, 0, newPop, 0, selectedIndividuals.length);
		System.arraycopy(newGeneration, 0, newPop, selectedIndividuals.length, newGeneration.length);
		return newPop;
	}
	
	/**
	 * An implementation of the Truncated Rank-Based Selection algorithm. All the individuals are sorted based on their fitness 
	 * value from the best to worse. Only the best x of the individuals are allowed to reproduce and make the same amount (k) of 
	 * offsprings (both x and k are regulated by constants defined at the beginning of the class). They are then coupled in pairs 
	 * and produce their offsprings that form the "new generation". The non-selected individuals are discarded and the new version 
	 * of the population is consisted of only the previously selected individuals and the new generation.
	 * 
	 * @param botEvolution - An object that denotes the current Bot Evolution session
	 * @param population - An array of ANNs that correspond to the different individuals
	 * @param fitnesses - An array with the fitness values of the different individuals
	 * @return - The new version of the population
	 */
	public ANN[] truncatedRankBasedSelection(BotEvolution botEvolution, ANN[] population, double[] fitnesses){
		int numberofIndivuduals = population.length;
		int numberOfSelectionsToBeMade = (int)Math.round(numberofIndivuduals * ELITE_PERCENTILE);
		int numberOfOffspringsToBeGenerated = numberofIndivuduals - numberOfSelectionsToBeMade;
		ANN[] rankedIndividuals = rankIndividuals(population, fitnesses).getA();
		ANN[] selectedIndividuals = Arrays.copyOfRange(rankedIndividuals, 0, numberOfSelectionsToBeMade);
		ANN[] newGeneration = makeOffsprings(botEvolution, selectedIndividuals, numberOfOffspringsToBeGenerated);
		ANN[] newPop = new ANN[numberofIndivuduals];
		System.arraycopy(selectedIndividuals, 0, newPop, 0, selectedIndividuals.length);
		System.arraycopy(newGeneration, 0, newPop, selectedIndividuals.length, newGeneration.length);
		return newPop;
	}
	
	/**
	 * An implementation of the Tournament Selection algorithm. K individuals (where k is regulated by a constant defined at the 
	 * beginning of the class) individuals are picked randomly from the population. The two individuals with the highest fitness 
	 * amongst them are coupled in a pair and produce a single offspring. Then, the new offspring joins the population along with
	 * its parents and other individuals. This process is repeated for a number of times which is equal to the half of the
	 * individuals that is selected in each round.
	 * 
	 * @param botEvolution - An object that denotes the current Bot Evolution session
	 * @param population - An array of ANNs that correspond to the different individuals
	 * @param fitnesses - An array with the fitness values of the different individuals
	 * @return - The new version of the population
	 */
	public ANN[] tournamentSelection(BotEvolution botEvolution, ANN[] population, double[] fitnesses){
		int numberofIndivuduals = population.length;
		int numberOfSelectionsToBeMade = (int)Math.round(numberofIndivuduals * ELITE_PERCENTILE);
		int numberOfOffspringsToBeGenerated = (int)Math.round(numberofIndivuduals * POPULATION_PROPORTION_TO_BE_COVERED_BY_OFFSPRINGS);
		ANN[] newGeneration = new ANN[numberOfOffspringsToBeGenerated];
		
		for (int i = 0; i < numberOfOffspringsToBeGenerated; i++) {
			List<Integer> drawnIndividuals = new ArrayList<Integer>();
			ANN[] winningIndividuals = new ANN[numberOfOffspringsToBeGenerated * 2];
			double[] winningIndividualsFitnesses = new double[numberOfOffspringsToBeGenerated *2];
			
			for (int j = 0; j < numberOfSelectionsToBeMade; j++) {
				int  indexOfWinningIndividual = random.nextInt(numberofIndivuduals);
				
				while (drawnIndividuals.contains(indexOfWinningIndividual)) {
					indexOfWinningIndividual = random.nextInt(numberofIndivuduals);
				}
				
				drawnIndividuals.add(indexOfWinningIndividual);
				winningIndividuals[j] = population[indexOfWinningIndividual];
				winningIndividualsFitnesses[j] = fitnesses[indexOfWinningIndividual];
			}
			
			ANN[] rankedIndividuals = rankIndividuals(winningIndividuals, winningIndividualsFitnesses).getA();
			ANN[] selectedIndividuals = Arrays.copyOfRange(rankedIndividuals, 0, 2);
			ANN offspring = makeOffsprings(botEvolution, selectedIndividuals, 1)[0];
			newGeneration[i] = offspring;
		}
		
		ANN[] rankedIndividuals = rankIndividuals(population, fitnesses).getA();
		ANN[] newPop = new ANN[numberofIndivuduals];
		System.arraycopy(rankedIndividuals, 0, newPop, 0, rankedIndividuals.length - numberOfOffspringsToBeGenerated);
		System.arraycopy(newGeneration, 0, newPop, rankedIndividuals.length - numberOfOffspringsToBeGenerated, newGeneration.length);
		return newPop;
	}
	
	/**
	 * A helper method for all the Selection algorithms that make use of a roulette wheel. The whole roulette covers 
	 * an area of proportions which ranges between 0.0 (inclusive) and 1.0 (exclusive). Each slice of the wheel 
	 * (where each slice has a starting and an ending point) occupies an area which is directly proportional to 
	 * the calculated proportion of its corresponding individual. The first slice covers the range between [0.0, p0)
	 * where p0 is the proportion of the first individual. The second slice covers the range between [p0, p0+p1). The
	 * same logic is followed for every other slice which corresponds to a different individual.
	 * 
	 * @param proportionsPerIndividual - An array with the proportions that were assigned to each individual
	 * @return - An array whose entries include the ending point of the wheel proportion occupied 
	 * by each individual
	 */
	private double[] buildRouletteWheel(double[] proportionsPerIndividual) {
		int numberofIndivuduals = proportionsPerIndividual.length;
		double[] rouletteWheel = new double[numberofIndivuduals];
		double sliceEndingPoint = proportionsPerIndividual[0];
		rouletteWheel[0] = sliceEndingPoint;
		
		for (int i = 1; i < numberofIndivuduals; i++) {
			sliceEndingPoint += proportionsPerIndividual[i];
			rouletteWheel[i] = sliceEndingPoint;
		}
		
		return rouletteWheel;
	}
	
	/**
	 * A helper method for all the Selection algorithms that make use of a roulette wheel. It simulates a number of spins of
	 * the roulette wheel. This number of spins corresponds to the number of individuals that need to be selected (which is
	 * regulated by a constant defined at the beginning of the class). During each spin of the wheel, a random double number 
	 * from the range [0.0,1.0) is generated which falls inside the range of one of the roulette wheel slices. The individual to 
	 * whom this slice belongs is identified and in case it is not already drawn in a previous spin, it is added to the list of 
	 * selected individuals. If it has already been drawn, a new spin is performed until a new individual is selected.
	 * 
	 * @param population - An array of ANNs that correspond to the different individuals
	 * @param proportionsPerIndividual - An array with the proportions that were assigned to each individual
	 * @return - An array with the selected individuals that will be used for reproduction
	 */
	private ANN[] useRouletteWheel(ANN[] population, double[] proportionsPerIndividual) {
		double[] rouletteWheel = buildRouletteWheel(proportionsPerIndividual);
		int numberofIndivuduals = proportionsPerIndividual.length;
		int numberOfSelectionsToBeMade = (int)Math.round(numberofIndivuduals * ELITE_PERCENTILE);
		List<Integer> selectedIndividualsIndexes = new ArrayList<Integer>();
		ANN[] selectedIndividuals = new ANN[numberOfSelectionsToBeMade];
		
		for (int i = 0; i < numberOfSelectionsToBeMade; i++) {
			double selectedNumber = random.nextDouble();
			int indexOfSelectedIndividual = returnIndexOfSelectedIndividual(selectedNumber, rouletteWheel);
			
			while (selectedIndividualsIndexes.contains(indexOfSelectedIndividual)) {
				selectedNumber = random.nextDouble();
				indexOfSelectedIndividual = returnIndexOfSelectedIndividual(selectedNumber, rouletteWheel);
			}

			selectedIndividualsIndexes.add(indexOfSelectedIndividual);
			selectedIndividuals[i] = population[i];
		}
		
		return selectedIndividuals;
	}
	
	/**
	 * This method uses the number that was generated from spinning the roulette wheel to identify to which slice range 
	 * this number belongs. When the slice is identified, the index of the corresponding individual is returned.
	 * 
	 * @param selectedNumber - The number that was generated by spinning the roulette wheel
	 * @param rouletteWheel - An array whose entries include the ending point of the wheel proportion occupied 
	 * by each individual
	 * @return - The index of the individual that was selected by the spin of the roulette wheel
	 */
	private int returnIndexOfSelectedIndividual(double selectedNumber, double[] rouletteWheel) {
		double sliceStartingPoint = 0.0;
		boolean found = false;
		int indexOfSelectedIndividual = 0;
				
		for (int i = 0; i < rouletteWheel.length && !found; i++) {
			if (selectedNumber >= sliceStartingPoint && selectedNumber < rouletteWheel[i]) {
				found = true;
				indexOfSelectedIndividual = i;
			}
			
			sliceStartingPoint = rouletteWheel[i];
		}
		
		return indexOfSelectedIndividual;
	}
	
	/**
	 * This methods calculates the total fitness of a given population.
	 * 
	 * @param fitnesses - An array with the fitness values of the different individuals
	 * @return - The total fitness of the given population
	 */
	private double calculatePopulationFitness(double[] fitnesses) {
		double populationFitness = 0;
		
		for (double fitness: fitnesses) {
			populationFitness += fitness;
		}
		
		return populationFitness;
	}
	
	/**
	 * This method calculates the sum of the ranks given a list of ranked individuals.
	 * 
	 * @param population - An array of ANNs that correspond to the different individuals
	 * @return - The sum of the ranks for the given list of ranked individuals
	 */
	private int calculateSumOfRanks(ANN[] rankedIndividuals) {
		int sumOfRanks = 0;
		
		for (int i = 0; i < rankedIndividuals.length; i++) {
			sumOfRanks += (i+1);
		}
		
		return sumOfRanks;
	}
	
	/**
	 * This method ranks the population with respect to their fitness value in decreasing order.
	 * The index linking between the population and their fitness values is preserved.
	 * 
	 * @param population - An array of ANNs that correspond to the different individuals
	 * @param fitnesses - An array with the fitness values of the different individuals	 
	 * @return - A tuple with the sorted versions of the two aforementioned arrays.
	 */
	public static Tuple<ANN[], double[]> rankIndividuals(ANN[] population, double[] fitnesses) {
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
		
		return new Tuple<ANN[], double[]>(population, fitnesses);
	}
	
	/**
	 * Given a list of selected individuals, this method randomly allocates them into a number pairs which is equal to
	 * the amount of offsprings to be created. Each couple then procreates and produces their offspring.
	 * 
	 * @param botEvolution - An object that denotes the current Bot Evolution session
	 * @param selectedIndividuals - An array of the selected individuals that will be used for reproduction
	 * @param numberOfOffspringsToBeCreated - The amount of offsprings to be created
	 * @return - The produced offsprings
	 */
	private ANN[] makeOffsprings(BotEvolution botEvolution, ANN[] selectedIndividuals, int numberOfOffspringsToBeCreated) {
		ANN[] newPop = new ANN[numberOfOffspringsToBeCreated];
		
		for(int c = 0; c < numberOfOffspringsToBeCreated; c++){
			//Select mother and father
			int  indexOfMother = random.nextInt(selectedIndividuals.length);
			int  indexOfFather = random.nextInt(selectedIndividuals.length);
			
			while (indexOfFather == indexOfMother) {
				indexOfFather = random.nextInt(selectedIndividuals.length);
			}
			
			//Make a Baby (This method is NSFW)
			newPop[c] = ANN.crossoverAndMutation(selectedIndividuals[indexOfMother], selectedIndividuals[indexOfFather], botEvolution, 0.8, MUTATION_RATE);
		}
		
		return newPop;
	}
}