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
	
	/** List of all recorded Simulations */
	@Getter private List<Recorder> simRecords = new ArrayList<Recorder>();
	/** Reference to Bot Evolution */
	private BotEvolution evo;
	
	/** A Human Controller. When this variable is != null the Simulation is updating in meat time based on inputs given by the user via this object */
	private HumanController humanController = null;
	
	
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
	public double simulateFitness(ANN controller, boolean record){
		int tests = 1;
		double out = 0;
		for(int c=0; c<tests; c++){
			out += simulate(controller, evo.getRandom().nextInt(Integer.MAX_VALUE), record);
		}
		return out/tests;
	}
	
	/** Does the actual simulation step (as a simple debug function to check that the evolution algorithm and ANN are working properly) */
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
	public double simulate(ANN controller, int randomSeed, boolean record){
		final double sensorRange = 0.9;
		final double speed = 0.7;
		final int iterations = 100;
		Random rand = new Random(666);//randomSeed);
		Recorder rec = null;
		if(record){
			rec = new Recorder(map);
		}
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
		for(int c=0; c<iterations; c++){
			if(record){
				rec.add(new Action(x, y, rota));
			}
			//If the Simulated bot is controlled by a human, only iterate if a new command has been sent, otherwise stall.
			if(humanController!=null){
				while(humanController.updated){
					try{Thread.sleep(5);}catch(Exception ex){}
				}
			}
			//calculate sensory inputs
			double inputAngleDifference = 1.0 / sensorIns.length;
			for(int inc=0; inc<sensorIns.length; inc++){
				double seenAngle = (rota + inputAngleDifference*inc) % 1.0;
				double seenX = Math.cos(Math.toRadians(seenAngle*360))*sensorRange;
				double seenY = Math.sin(Math.toRadians(seenAngle*360))*sensorRange;
				if(map[(int)(y+seenY)][(int)(x+seenX)]){
					//Collision is seen, calculate nearness of obstacle (directly on it=~1, very far away=~0)
					//TODO improve this here code
					double increments = 10.0;
					double nearness = 1.0;
					for(int igv=0; igv<increments; igv++){
						if(map[(int)(y+seenY*nearness)][(int)(x+seenX*nearness)]){
							nearness -= nearness/increments;
						}
						else{
							sensorIns[inc] = 1.0-nearness;
							break;
						}
					}
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
			if(humanController!=null){
				//update wheels based on human interaction (For debugging purposes)
				wheels = humanController.wheels;
			}
			else{
				//update wheels the via ANN
				wheels = controller.process(inputs);//new double[]{evo.getRandom().nextDouble(), evo.getRandom().nextDouble()};//
			}
			double[] newPos = Kinematics.calculatePosition(new Point(((wheels[0]))*speed, ((wheels[1]))*speed), new Point(x, y), rota);
			rota = newPos[2];
			double newX = newPos[0];
			double newY = newPos[1];
			//check collision
			if(map[(int)newY][(int)newX]){
				//Collision occurs, set bot close to wall
				double trajX = newX - x;
				double trajY = newY - y;
				//binary search legal location on trajectory (a bit of a cheat, but ehh)
				double length = 1.0;
				int steps = 8;
				for(int chc=0; chc<steps; chc++){
					if(map[(int)(y+trajY*length)][(int)(x+trajX*length)]){
						length -= 1.0/steps;
					}
				}
				length = length<=0.001?0:length;
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
		if(record){
			this.simRecords.add(rec);
		}
		return out;
	}
	
	/** Draws Map to the Console */
	public void consoleOutMap(){
		for(int cy=0; cy<map.length; cy++){
			System.out.println();
			for(int cx=0; cx<map[0].length; cx++){
				System.out.print(map[cy][cx]?1:0);
			}
		}
	}
	
	
	//---inner classes---
	/** Object keeping track of every action happeneing in the simulation */
	public static class Recorder{
		/** The Map used for the recorded Simulation */
		@Getter boolean[][] map;
		/** List of all Actions that have been taken by the Bot during the Simulation */
		@Getter private List<Action> actions;
		/** Constructs new empty Recorder based on Map */
		public Recorder(boolean[][] map){
			this.actions = new ArrayList<Action>();
			this.map = map;
		}
		/** Adds an Action to the List of Actions */
		public void add(Action action){
			this.actions.add(action);
		}
		/** Returns String of the Record */
		public String toString(){
			StringBuffer buffer = new StringBuffer();
			for(Action a:actions){
				buffer.append(a+"\n");
			}
			return buffer.toString();
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
		public String toString(){
			return "Point: "+x+"|"+y+", Angle: "+rotation;
		}
	}
	
	/** Utility Class for use when interacting with the Simulation from Meat Space */
	public static class HumanController{
		/** The values wheels is supposedto take */
		@Getter private double[] wheels;
		/** Boolean to check whether this input has already caused an input (to be manually set true once used in Simulation) */
		@Getter @Setter private boolean updated;
	}
	
}
