package evoAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import util.Tuple;
import java.util.Scanner;

/** The Core class of the Evolutionary algorithm, where all the magic is supposed to happen. Sofia. */
public class EvoAlgorithm {
	public static final double PROPORTION_OF_POPULATION_TO_BE_SELECTED = 0.2;
	public static final int NUMBER_OF_OFFSPRINGS_PER_COUPLE = 0;
	public static String SELECTION_ALGORITHM = "PROPORTIONATE";
	
	/** The actual method containing the Evolutionary Algorithm, return type should correspond to whatever the algorithm optimizes */
	public void initEvolution(){
		
		int  number_of_individuals = 100;
		
		List population = new ArrayList();
		
		//initialisation of number_of_individuals individuals
		for(int i=1; i<=number_of_individuals; i++){
			Individual individual = new Individual();
			population.add(individual);
		} 
		
		List<Tuple<Individual, Double>> populationFitness = generateFitnessList(population);
		
		Scanner reader = new Scanner(System.in);				
		System.out.println("Pick a selection Method(options: PROPORTIONATE, RANK-BASED, TRUNCATED RANK-BASED, TOURNAMENT):");
		String selection_option = reader.nextLine();
		SELECTION_ALGORITHM = selection_option;
		
		for (int i =0; i < 1000 ;i++){ //the loop can also be created as while fitness[t-1] != fitness[t]
			List<Individual> selected_individuals = selection(populationFitness);

			populationFitness.clear();
			populationFitness = generateFitnessList(selected_individuals);		
			}		
	}


	/**
	 * The Selection algorithm, uses some method to decide which Individuals are fittest, and uses them as a base to create the 
	 * next generation.
	 * There are four different types of algorithms that can be used: Proportionate, Rank-Based, Truncated Rank-Based & Tournament
	 * A constant at the beginning of the class denotes which one of them will be, eventually, used by this method.
	 * 
	 * @param evaluatedPopulation - A list of evaluated individuals
	 * @return - The new version of the population
	 */
	private List<Individual> selection(List<Tuple<Individual, Double>> evaluatedPopulation){
		List<Individual> out = new ArrayList<Individual>();
		String selection = SELECTION_ALGORITHM;
		
		switch(selection) {
			case "PROPORTIONATE":
				out = proportionateSelection(evaluatedPopulation);
				break;
				
			case "RANK-BASED":
				out = rankBasedSelection(evaluatedPopulation);
				break;
				
			case "TRUNCATED RANK-BASED":
				out = truncatedRankBasedSelection(evaluatedPopulation);
				break;
				
			case "TOURNAMENT":
				out = tournamentSelection(evaluatedPopulation);
				break;
		}
		
		return out;
	}
	
	/**
	 * An implementation of the Proportionate Selection algorithm. Each individual is associated with a proportion with
	 * respect to the total population fitness (p(i) = f(i)/Sf). The individuals to be reproduced are then selected using
	 * a roulette wheel and, after they are coupled in pairs, they produce their offsprings which form the "new generation".
	 * The non-selected individuals are discarded and the new version of the population is consisted of only the previously
	 * selected individuals and the new generation.
	 * 
	 * @param evaluatedPopulation - A list of evaluated individuals
	 * @return - The new version of the population
	 */
	private List<Individual> proportionateSelection(List<Tuple<Individual, Double>> evaluatedPopulation){
		double populationFitness = calculatePopulationFitness(evaluatedPopulation);
		List<Tuple<Individual, Double>> proportionsPerIndividual = new ArrayList<Tuple<Individual, Double>>();
		
		for (Tuple<Individual, Double> t: evaluatedPopulation) {
			Individual individual = (Individual)t.getA();
			double fitness = (Double)t.getB();
			double proportion = fitness/populationFitness;
			proportionsPerIndividual.add(new Tuple<Individual, Double>(individual, proportion));
		}
		
		List<Individual> selectedIndividuals = useRouletteWheel(proportionsPerIndividual);
		int numberOfOffspringsPerCouple = NUMBER_OF_OFFSPRINGS_PER_COUPLE;
		List<Individual> newGeneration = makeOffsprings(selectedIndividuals, numberOfOffspringsPerCouple);
		selectedIndividuals.addAll(newGeneration);
		return selectedIndividuals;	
	}
	
	/**
	 * An implementation of the Rank-Based Selection algorithm. All the individuals are sorted based on their fitness value
	 * from the best to worse. The rank in which each individual ends up after the sorting is used to associate them with a
	 * proportion (p(i) = (1 - r(i))/Sr). The individuals to be reproduced are then selected using a roulette wheel and after 
	 * they are coupled in pairs, they produce their offsprings that form the "new generation". The non-selected individuals are 
	 * discarded and the new version of the population is consisted of only the previously selected individuals and the new 
	 * generation.
	 * 
	 * @param evaluatedPopulation - A list of evaluated individuals
	 * @return - The new version of the population
	 */
	private List<Individual> rankBasedSelection(List<Tuple<Individual, Double>> evaluatedPopulation){
		List<Individual> rankedIndividuals = rankIndividuals(evaluatedPopulation);
		int sumOfRanks = calculateSumOfRanks(rankedIndividuals);
		List<Tuple<Individual, Double>> proportionsPerIndividual = new ArrayList<Tuple<Individual, Double>>();
		
		for (int i = 0; i < rankedIndividuals.size(); i++) {
			int rank = i + 1;
			Individual individual = (Individual)rankedIndividuals.get(i);
			double proportion = (1 - rank)/sumOfRanks;
			proportionsPerIndividual.add(new Tuple<Individual, Double>(individual, proportion));
		}
		
		List<Individual> selectedIndividuals = useRouletteWheel(proportionsPerIndividual);
		int numberOfOffspringsPerCouple = NUMBER_OF_OFFSPRINGS_PER_COUPLE;
		List<Individual> newGeneration = makeOffsprings(selectedIndividuals, numberOfOffspringsPerCouple);
		selectedIndividuals.addAll(newGeneration);
		return selectedIndividuals;	
	}
	
	/**
	 * An implementation of the Truncated Rank-Based Selection algorithm. All the individuals are sorted based on their fitness 
	 * value from the best to worse. Only the best x of the individuals are allowed to reproduce and make the same amount (k) of 
	 * offsprings (both x and k are regulated by constants defined at the beginning of the class). They are then coupled in pairs 
	 * and produce their offsprings that form the "new generation". The non-selected individuals are discarded and the new version 
	 * of the population is consisted of only the previously selected individuals and the new generation.
	 * 
	 * @param evaluatedPopulation - A list of evaluated individuals
	 * @return - The new version of the population
	 */
	private List<Individual> truncatedRankBasedSelection(List<Tuple<Individual, Double>> evaluatedPopulation){
		List<Individual> rankedIndividuals = rankIndividuals(evaluatedPopulation);
		int numberofIndivuduals = rankedIndividuals.size();
		int numberOfSelectionsToBeMade = (int)Math.round(numberofIndivuduals * PROPORTION_OF_POPULATION_TO_BE_SELECTED);
		List<Individual> selectedIndividuals = rankedIndividuals.subList(0, numberOfSelectionsToBeMade);
		int numberOfOffspringsPerCouple = NUMBER_OF_OFFSPRINGS_PER_COUPLE;
		List<Individual> newGeneration = makeOffsprings(selectedIndividuals, numberOfOffspringsPerCouple);
		selectedIndividuals.addAll(newGeneration);
		return selectedIndividuals;	
	}
	
	/**
	 * An implementation of the Tournament Selection algorithm. K individuals (where k is regulated by a constant defined at the 
	 * beginning of the class) individuals are picked randomly from the population. The two individuals with the highest fitness 
	 * amongst them are coupled in a pair and produce a single offspring. Then, the new offspring joins the population along with
	 * its parents and other individuals. This process is repeated for a number of times which is equal to the half of the
	 * individuals that is selected in each round.
	 * 
	 * @param evaluatedPopulation - A list of evaluated individuals
	 * @return - The new version of the population
	 */
	private List<Individual> tournamentSelection(List<Tuple<Individual, Double>> evaluatedPopulation){
		int numberofIndivuduals = evaluatedPopulation.size();
		int numberOfSelectionsToBeMade = (int)Math.round(numberofIndivuduals * PROPORTION_OF_POPULATION_TO_BE_SELECTED);
		int numberOfOffspringsToBeGenerated = numberOfSelectionsToBeMade/2;
		int[] drawnIndividuals = new int[numberOfSelectionsToBeMade];
		List<Tuple<Individual, Double>> winningIndividuals = new ArrayList<Tuple<Individual, Double>>();
		List<Individual> selectedIndividuals = new ArrayList<Individual>();
		
		for (int i = 0; i < numberOfOffspringsToBeGenerated; i++) {
			for (int j = 0; j < numberOfSelectionsToBeMade; j++) {
				int  indexOfWinningIndividual = new Random().nextInt(numberofIndivuduals);
				Arrays.sort(drawnIndividuals);
				
				while (Arrays.binarySearch(drawnIndividuals, indexOfWinningIndividual) >= 0) {
					indexOfWinningIndividual = new Random().nextInt(numberofIndivuduals);
				}
				
				drawnIndividuals[j] = indexOfWinningIndividual;
				Tuple<Individual, Double> winningIndividual = evaluatedPopulation.get(indexOfWinningIndividual);
				winningIndividuals.add(winningIndividual);
			}
			
			List<Individual> rankedIndividuals = rankIndividuals(winningIndividuals);
			selectedIndividuals = rankedIndividuals.subList(0, 2);
			Individual newOffspring = makeOffsprings(selectedIndividuals, 1).get(0);
			evaluatedPopulation.add(new Tuple<Individual, Double>(newOffspring, 0.0));
		}
		
		return convertToCollectionOfIndividuals(evaluatedPopulation);	
		}
	
	/**
	 * A helper method for all the Selection algorithms that make use of a roulette wheel. The whole roulette covers 
	 * an area of proportions which ranges between 0.0 (inclusive) and 1.0 (exclusive). Each slice of the wheel 
	 * (where each slice has a starting and an ending point) occupies an area which is directly proportional to 
	 * the calculated proportion of its corresponding individual. The first slice covers the range between [0.0, p0)
	 * where p0 is the proportion of the first individual. The second slice covers the range between [p0, p0+p1). The
	 * same logic is followed for every other slice which corresponds to a different individual.
	 * 
	 * @param proportionsPerIndividual - A list of the proportions that were assigned to each individual
	 * @return - An array whose entries include the ending point of the wheel proportion occupied 
	 * by each individual
	 */
	private double[] buildRouletteWheel(List<Tuple<Individual, Double>> proportionsPerIndividual) {
		int numberofIndivuduals = proportionsPerIndividual.size();
		double[] rouletteWheel = new double[numberofIndivuduals];
		double sliceEndingPoint = proportionsPerIndividual.get(0).getB();
		rouletteWheel[0] = sliceEndingPoint;
		
		for (int i = 1; i < numberofIndivuduals; i++) {
			sliceEndingPoint += proportionsPerIndividual.get(i).getB();
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
	 * @param proportionsPerIndividual - A list of the proportions that were assigned to all individuals
	 * @return - A list of the selected individuals that will be used for reproduction
	 */
	private List<Individual> useRouletteWheel(List<Tuple<Individual, Double>> proportionsPerIndividual) {
		double[] rouletteWheel = buildRouletteWheel(proportionsPerIndividual);
		int numberofIndivuduals = proportionsPerIndividual.size();
		int numberOfSelectionsToBeMade = (int)Math.round(numberofIndivuduals * PROPORTION_OF_POPULATION_TO_BE_SELECTED);
		int[] selectedIndividualsIndexes = new int[numberOfSelectionsToBeMade];
		List<Individual> selectedIndividuals = new ArrayList<Individual>();
		
		for (int i = 0; i < numberOfSelectionsToBeMade; i++) {
			double selectedNumber = new Random().nextDouble();
			int indexOfSelectedIndividual = returnIndexOfSelectedIndividual(selectedNumber, rouletteWheel);
			Arrays.sort(selectedIndividualsIndexes);
			
			while (Arrays.binarySearch(selectedIndividualsIndexes, indexOfSelectedIndividual) >= 0) {
				selectedNumber = new Random().nextDouble();
				indexOfSelectedIndividual = returnIndexOfSelectedIndividual(selectedNumber, rouletteWheel);
			}
			
			selectedIndividualsIndexes[i] = indexOfSelectedIndividual;
			Individual selectedIndividual = proportionsPerIndividual.get(indexOfSelectedIndividual).getA();
			selectedIndividuals.add(selectedIndividual);
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
		int sliceStartingPoint = 0;
		boolean found = false;
		int indexOfSelectedIndividual = 0;
		
		for (int i = 0; i < rouletteWheel.length && !found; i++) {
			if (selectedNumber >= sliceStartingPoint && selectedNumber < rouletteWheel[i]) {
				found = true;
				indexOfSelectedIndividual = i;
			}
		}
		
		return indexOfSelectedIndividual;
	}
	
	/**
	 * This methods calculates the total fitness of a given population.
	 * 
	 * @param evaluatedPopulation - A list of evaluated individuals
	 * @return - The total fitness of the given population
	 */
	private double calculatePopulationFitness(List<Tuple<Individual, Double>> evaluatedPopulation) {
		double populationFitness = 0;
		
		for (Tuple<Individual, Double> t: evaluatedPopulation) {
			double fitness = (Double)t.getB();
			populationFitness += fitness;
		}
		
		return populationFitness;
	}
	
	/**
	 * This method calculates the sum of the ranks given a list of ranked individuals.
	 * 
	 * @param rankedIndividuals - A list of ranked individuals
	 * @return - The sum of the ranks for the given list of ranked individuals
	 */
	private int calculateSumOfRanks(List<Individual> rankedIndividuals) {
		int sumOfRanks = 0;
		
		for (int i = 0; i < rankedIndividuals.size(); i++) {
			sumOfRanks += (i+1);
		}
		
		return sumOfRanks;
	}
	
	/**
	 * This method ranks the evaluated population with respect to their fitness value in decreasing order.
	 * 
	 * @param evaluatedPopulation - A list of evaluated individuals
	 * @return - A sorted version of the above list in decreasing order
	 */
	private List<Individual> rankIndividuals(List<Tuple<Individual, Double>> evaluatedPopulation) {
	    int n = evaluatedPopulation.size();
	    int j;
	    	
		do {
			j = 0;
					
	    	for (int i = 1; i < n; i++) {
	    		if (evaluatedPopulation.get(i-1).getB() < evaluatedPopulation.get(i).getB()) {
	    			Tuple<Individual, Double> temp = evaluatedPopulation.get(i-1);
	    			evaluatedPopulation.add(i-1, evaluatedPopulation.get(i));
	    			evaluatedPopulation.add(i, temp);
	    			j = i;
	    		}
 	    	}
	    	
	    	n = j;
	    } while (n != 0);
	    
		return convertToCollectionOfIndividuals(evaluatedPopulation);
	}
	
	/**
	 * Given a list of selected individuals, this method allocates them into pairs and creates a number of new
	 * new individuals to be their offsprings. Assuming that the number of selected individuals is even, the first
	 * individual mates with the last one, the second with the penultimate one and so on and so forth. Each
	 * couple procreates and produces a number of offsprings (which is specified on the corresponding constant at 
	 * the beginning of the class).
	 * 
	 * @param selectedIndividuals - A list of the selected individuals that will be used for reproduction
	 * @param numberOfOffspringsPerCouple - The number of offsprings to be made per couple
	 * @return - The produced offsprings
	 */
	private List<Individual> makeOffsprings(List<Individual> selectedIndividuals, int numberOfOffspringsPerCouple) {
		List<Individual> offsprings = new ArrayList<Individual>();
		
		for (int i = 0; i < selectedIndividuals.size()/2; i++) {
			Individual mother = selectedIndividuals.get(i);
			Individual father = selectedIndividuals.get(selectedIndividuals.size() - (i+1));
			
			for (int j = 0; j < numberOfOffspringsPerCouple; j++) {
				Individual offspring = new Individual(mother, father);
				offsprings.add(offspring);
			}
		}
		
		return offsprings;
	}
	
	/**
	 * Given a list of evaluated individuals (i.e. individuals whose fitness value was calculated), this method returns
	 * a collection that includes an unassessed version of these individuals.
	 * 
	 * @param evaluatedPopulation - A list of evaluated individuals
	 * @return - An unassessed collection of the individuals
	 */
	private List<Individual> convertToCollectionOfIndividuals(List<Tuple<Individual, Double>> evaluatedPopulation) {
		List<Individual> collectionOfIndividuals = new ArrayList<Individual>();
		
		for (Tuple<Individual, Double> t: evaluatedPopulation) {
			collectionOfIndividuals.add(t.getA());
		}
		
		return collectionOfIndividuals;
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