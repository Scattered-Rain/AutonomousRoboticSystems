package assignment01;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** The Simulation Environment For da Bots */
public class Simulator{
	
	/** Whether the Simulation Process is to be recorded */
	@Setter private boolean recordSimulation = false;
	/** List of all recorded Simulations */
	@Getter private List<Recorder> simRecords = new ArrayList<Recorder>();
	
	/** Reference to Bot Evolution */
	private BotEvolution evo;
	
	
	/** Constructs new Simulator */
	public Simulator(BotEvolution evo){
		this.evo = evo;
	}
	
	
	/** Simulates the bot controller, returns fitnessvalue */
	public double simulateFitness(ANN controller){
		double out = 0;
		return out;
	}
	
	/** Does the actual simulation step */
	public double simulate(ANN controller){
		double out = 0;
		return out;
	}
	
	
	//---inner classes---
	/** Object keeping track of every action happeneing in the simulation */
	public static class Recorder{
		/** List of all Actions that have been taken by the Bot during the Simulation */
		@Getter private List<Action> actions;
		/** Constructs new empty Recorder */
		public Recorder(){
			this.actions = new ArrayList<Action>();
		}
		/** Adds an Action to the List of Actions */
		public void add(Action action){
			this.actions.add(action);
		}
	}
	
	/** Object Representing the result of an acion taken by the bot (i.e. movement and rotation) */
	@AllArgsConstructor public static class Action{
		/** The x location of the bot */
		@Getter private double x;
		/** The Y location of the bot */
		@Getter private double y;
		/** The rotation of the bot, 0-1 range, where 0 points in up (0, -1) direction */
		@Getter private double rotation;
	}
	
}
