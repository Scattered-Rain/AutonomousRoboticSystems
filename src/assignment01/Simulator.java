package assignment01;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.Point;
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
	
	
	/** The Map in boolean dimension (where true=collision, false=walkable), map[y][x] */
	private boolean[][] map;
	
	
	/** Constructs new Simulator */
	public Simulator(BotEvolution evo){
		this.evo = evo;
		setMap(new boolean[10][10]);
	}
	
	/** Modifies the given map to have outer edges, sets it to be the test map */
	public void setMap(boolean[][] map){
		boolean[][] newMap = new boolean[map.length+2][map[0].length+2];
		for(int cy=0; cy<newMap.length; cy++){
			for(int cx=0; cx<newMap[0].length; cx++){
				if(cy==0 || cx==0 || cy==newMap.length-1 || cx== newMap[0].length-1){
					newMap[cy][cx] = true;
				}
				else{
					newMap[cy][cx] = map[cy-1][cx-1];
				}
			}
		}
		this.map = newMap;
	}
	
	
	/** Simulates the bot controller, returns fitnessvalue */
	public double simulateFitness(ANN controller){
		double out = simulateDebug(controller);
		return out;
	}
	
	/** Does the actual simulation step (Right now this is a Debug method for testing) */
	public double simulateDebug(ANN controller){
		double[] testGoals = new double[]{0.5, 0.5};
		Random rand = new Random(1502);
		double out = 0;
		double[] sensAndMem = new double[14];
		for(int c=0; c<sensAndMem.length; c++){
			sensAndMem[c] = rand.nextDouble();
		}
		double[] outs = controller.process(sensAndMem);
		out = 2 - (Math.abs(testGoals[0]-outs[0]) + Math.abs(testGoals[1]-outs[1]));
		return out;
	}
	
	/** Simulates Robot */
	public double simulate(ANN controller){
		final double sensorRange = 0.9;
		final double speed = 0.8;
		final int time = 1000;
		Random rand = evo.getRandom();
		//Build dust map (false = not yet cleaned)
		boolean[][] dust = new boolean[map.length][map[0].length];
		//init bot as rotation and X|Y with chance of spawning anywhere without collision on map
		double rota = rand.nextDouble();
		double x = -1;
		double y = -1;
		do{
			//Make sure that bot doesn't spawn in a wall
			x = rand.nextDouble() * map[0].length;
			y = rand.nextDouble() * map.length;
		}while(map[(int)y][(int)x]);
		//Simulate
		double[] wheels = new double[2];
		double[] sensorIns = new double[12];
		for(int c=0; c<time; c++){
			double inputAngleDifference = 1.0 / sensorIns.length;
			//calculate sensory inputs
			for(int inc=0; inc<sensorIns.length; inc++){
				double seenAngle = (rota + inputAngleDifference*c) % 1;
				double seenX = x + Math.cos(seenAngle)*sensorRange;
				double seenY = y + Math.sin(seenAngle)*sensorRange;
				if(map[(int)seenY][(int)seenX]){
					//Collision is seen, calculate nearness of obstacle (directly on it=~1, very far away=~0)
					//TODO add the math for this
					sensorIns[inc] = 1;
				}
				else{
					//No collision seen
					sensorIns[inc] = 0;
				}
			}
			//get Controller out
			double[] inputs = new double[sensorIns.length + wheels.length];
			System.arraycopy(sensorIns, 0, inputs, 0, sensorIns.length);
			System.arraycopy(wheels, 0, inputs, sensorIns.length, wheels.length);
			wheels = controller.process(inputs);
			double[] newPos = Kinematics.calculatePosition(new Point(((wheels[0]*2)-1)*speed, ((wheels[1]*2)-1)*speed), new Point(x, y), rota);
			rota = newPos[2];
			double newX = newPos[0];
			double newY = newPos[1];
			//check collision
			if(map[(int)newY][(int)newX]){
				//Collision occurs, set bot close to wall
				double trajX = newX - x;
				double trajY = newY - y;
				//binary search legal location on trajectory (a bit of a cheat, but ehh)
				double length = 0.5;
				for(int chc=0; chc<5; chc++){
					if(map[(int)(y+trajY*length)][(int)(x+trajX*length)]){
						length -= 0.25*(chc+1);
					}
					else{
						length += 0.25*(chc+1);
					}
				}
				length = length<=0.07?0:length;
				x = x + trajX*length;
				y = y + trajY*length;
			}
			else{
				x = newX;
				y = newY;
			}
			//Add anti-dust
			dust[(int)y][(int)x] = true;
		}
		//Calculate score
		double out = 0;
		for(int c=0; c<dust.length; c++){
			for(int c2=0; c2<dust[0].length; c2++){
				out += dust[c][c2]?1:0;
			}
		}
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
