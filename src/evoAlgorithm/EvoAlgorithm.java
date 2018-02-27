package evoAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import util.Point;
import util.Tuple;

import java.util.Scanner;

/** The Core class of the Evolutionary algorithm, where all the magic is supposed to happen. Sofia. */
public class EvoAlgorithm {
	public static final double PROPORTION_OF_POPULATION_TO_BE_SELECTED = 0.2;
	public static final double PROPORTION_OF_POPULATION_TO_BE_COVERED_BY_OFFSPRINGS = 1-PROPORTION_OF_POPULATION_TO_BE_SELECTED;
	public static String SELECTION_ALGORITHM = "PROPORTIONATE";
	
	@Getter private Random random = new Random();
	
	/** The actual method containing the Evolutionary Algorithm, return type should correspond to whatever the algorithm optimizes */
	public Point initEvolution(){
		int  number_of_individuals = 1000;
		List population = new ArrayList();
		
		//initialisation of number_of_individuals individuals
		for(int i=1; i<=number_of_individuals; i++){
			Individual individual = new Individual(this);
			population.add(individual);
		} 
		
		List<Tuple<Individual, Double>> populationFitness = generateFitnessList(population);
		Scanner reader = new Scanner(System.in);				
		System.out.println("Pick a selection Method(options: PROPORTIONATE, RANK-BASED, TRUNCATED RANK-BASED, TOURNAMENT, Elitist):");
		String selection_option = reader.nextLine();
		reader.close();
		SELECTION_ALGORITHM = selection_option;
		
		for (int i =0; i < 1000 ;i++){ //the loop can also be created as while fitness[t-1] != fitness[t]
			List<Individual> selected_individuals = selection(populationFitness);
			populationFitness.clear();
			populationFitness = generateFitnessList(selected_individuals);
			System.out.println("==== ITERATION NO. " + String.valueOf(i+1) + " ====");
			System.out.println(populationFitness.get(0).getA().getPoint()+" : "+populationFitness.get(0).getB());
		}
		
		return populationFitness.get(0).getA().getPoint();
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
				
			case "Elitist":
				out = elitistSelection(evaluatedPopulation);
				break;
		}
		
		return out;
	}
	
	/** Simple Selecetion method to serve as reference */
	private List<Individual> elitistSelection(List<Tuple<Individual, Double>> evaluatedPopulation){
		List<Individual> out = new ArrayList<Individual>();
		int winnerLoserCutoffIndex = (int)(evaluatedPopulation.size()*PROPORTION_OF_POPULATION_TO_BE_SELECTED);
		for(int c=0; c<winnerLoserCutoffIndex; c++){
			out.add(evaluatedPopulation.get(c).getA());
		}
		for(int c=winnerLoserCutoffIndex; c<evaluatedPopulation.size(); c++){
			out.add(new Individual(out.get(random.nextInt(winnerLoserCutoffIndex)), out.get(random.nextInt(winnerLoserCutoffIndex))));
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
		int numberOfOffspringsToBeGenerated = (int)Math.round(evaluatedPopulation.size() * PROPORTION_OF_POPULATION_TO_BE_COVERED_BY_OFFSPRINGS);
		List<Tuple<Individual, Double>> proportionsPerIndividual = new ArrayList<Tuple<Individual, Double>>();
		
		for (Tuple<Individual, Double> t: evaluatedPopulation) {
			Individual individual = (Individual)t.getA();
			double fitness = (Double)t.getB();
			double proportion = (double)fitness/(double)populationFitness;
			proportionsPerIndividual.add(new Tuple<Individual, Double>(individual, proportion));
		}
		
		List<Individual> selectedIndividuals = useRouletteWheel(proportionsPerIndividual);
		List<Individual> newGeneration = makeOffsprings(selectedIndividuals, numberOfOffspringsToBeGenerated);
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
		int numberOfOffspringsToBeGenerated = (int)Math.round(evaluatedPopulation.size() * PROPORTION_OF_POPULATION_TO_BE_COVERED_BY_OFFSPRINGS);
		List<Individual> rankedIndividuals = rankIndividuals(evaluatedPopulation);
		int sumOfRanks = calculateSumOfRanks(rankedIndividuals);
		List<Tuple<Individual, Double>> proportionsPerIndividual = new ArrayList<Tuple<Individual, Double>>();
		
		for (int i = 0; i < rankedIndividuals.size(); i++) {
			int rank = i + 1;
			Individual individual = (Individual)rankedIndividuals.get(i);
			double proportion = ((double)rank/(double)sumOfRanks);
			proportionsPerIndividual.add(new Tuple<Individual, Double>(individual, proportion));
		}
		
		List<Individual> selectedIndividuals = useRouletteWheel(proportionsPerIndividual);
		List<Individual> newGeneration = makeOffsprings(selectedIndividuals, numberOfOffspringsToBeGenerated);
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
		int numberOfOffspringsToBeGenerated = (int)Math.round(numberofIndivuduals * PROPORTION_OF_POPULATION_TO_BE_COVERED_BY_OFFSPRINGS);
		List<Individual> selectedIndividuals = rankedIndividuals.subList(numberOfSelectionsToBeMade, rankedIndividuals.size());
		List<Individual> newGeneration = makeOffsprings(selectedIndividuals, numberOfOffspringsToBeGenerated);
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
		int numberOfOffspringsToBeGenerated = (int)Math.round(numberofIndivuduals * PROPORTION_OF_POPULATION_TO_BE_COVERED_BY_OFFSPRINGS);
		List<Individual> newGeneration = new ArrayList<Individual>();
		
		for (int i = 0; i < numberOfOffspringsToBeGenerated; i++) {
			List<Integer> drawnIndividuals = new ArrayList<Integer>();
			List<Tuple<Individual, Double>> winningIndividuals = new ArrayList<Tuple<Individual, Double>>();
			
			for (int j = 0; j < numberOfSelectionsToBeMade; j++) {
				int  indexOfWinningIndividual = random.nextInt(numberofIndivuduals);
				
				while (drawnIndividuals.contains(indexOfWinningIndividual)) {
					indexOfWinningIndividual = random.nextInt(numberofIndivuduals);
				}
				
				drawnIndividuals.add(indexOfWinningIndividual);
				Tuple<Individual, Double> winningIndividual = evaluatedPopulation.get(indexOfWinningIndividual);
				winningIndividuals.add(winningIndividual);
			}
			
			List<Individual> rankedIndividuals = rankIndividuals(winningIndividuals);
			List<Individual> selectedIndividuals = rankedIndividuals.subList(rankedIndividuals.size()-2, rankedIndividuals.size());
			Individual newOffspring = makeOffsprings(selectedIndividuals, 1).get(0);
			newGeneration.add(newOffspring);
		}
		
		return newGeneration;	
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
		List<Integer> selectedIndividualsIndexes = new ArrayList<Integer>();
		List<Individual> selectedIndividuals = new ArrayList<Individual>();
		
		for (int i = 0; i < numberOfSelectionsToBeMade; i++) {
			double selectedNumber = random.nextDouble();
			int indexOfSelectedIndividual = returnIndexOfSelectedIndividual(selectedNumber, rouletteWheel);
			
			while (selectedIndividualsIndexes.contains(indexOfSelectedIndividual)) {
				selectedNumber = random.nextDouble();
				indexOfSelectedIndividual = returnIndexOfSelectedIndividual(selectedNumber, rouletteWheel);
			}

			selectedIndividualsIndexes.add(indexOfSelectedIndividual);
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
	 * This method ranks the evaluated population with respect to their fitness value in increasing order.
	 * 
	 * @param evaluatedPopulation - A list of evaluated individuals
	 * @return - A sorted version of the above list in increasing order
	 */
	private List<Individual> rankIndividuals(List<Tuple<Individual, Double>> evaluatedPopulation) {
	    int n = evaluatedPopulation.size();
	    int j;
	    	
		do {
			j = 0;
					
	    	for (int i = 1; i < n; i++) {
	    		if (evaluatedPopulation.get(i-1).getB() > evaluatedPopulation.get(i).getB()) {
	    			Tuple<Individual, Double> temp = evaluatedPopulation.get(i-1);
	    			evaluatedPopulation.set(i-1, evaluatedPopulation.get(i));
	    			evaluatedPopulation.set(i, temp);
	    			j = i;
	    		}
 	    	}
	    	
	    	n = j;
	    } while (n != 0);
	    
		return convertToCollectionOfIndividuals(evaluatedPopulation);
	}
	
	/**
	 * Given a list of selected individuals, this method randomly allocates them into a number pairs which is equal to
	 * the amount of offsprings to be created. Each couple then procreates and produces their offspring.
	 * 
	 * @param selectedIndividuals - A list of the selected individuals that will be used for reproduction
	 * @param numberOfOffspringsToBeCreated - The amount of offsprings to be created
	 * @return - The produced offsprings
	 */
	private List<Individual> makeOffsprings(List<Individual> selectedIndividuals, int numberOfOffspringsToBeCreated) {
		List<Individual> offsprings = new ArrayList<Individual>();
		
		for (int i = 0; i < numberOfOffspringsToBeCreated; i++) {
			int  indexOfMother = random.nextInt(selectedIndividuals.size());
			int  indexOfFather = random.nextInt(selectedIndividuals.size());
			
			while (indexOfFather == indexOfMother) {
				indexOfFather = random.nextInt(selectedIndividuals.size());
			}
			
			Individual mother = selectedIndividuals.get(indexOfMother);
			Individual father = selectedIndividuals.get(indexOfFather);
			Individual offspring = new Individual(mother, father);
			offsprings.add(offspring);
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
		double minVal = Double.POSITIVE_INFINITY;
		for(int c=0; c<population.size(); c++){
			out.add(new Tuple<Individual, Double>(population.get(c), population.get(c).fitness()));
			if(population.get(c).fitness() < minVal) {
				minVal = population.get(c).fitness();
			}
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
		for(int c=0; c<out.size(); c++) {
			out.get(c).setB(out.get(c).getB()+Math.abs(0)+0.000001);
		}
		System.out.println(minVal);
		//System.exit(0);
		return out;
	}
}