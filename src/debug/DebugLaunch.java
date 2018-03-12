package debug;

import assignment01.ANN;
import assignment01.BotEvolution;
import assignment01.Kinematics;
import assignment01.Simulator;
import evoAlgorithm.EvoAlgorithm;
import optimization.benchmarks.OpFunction;
import util.Point;
import graphing.Frame;
import graphing.Graph;
import graphing.GraphElement;

public class DebugLaunch{
	
	public static void main(String[] args){
		ANN output;
		new BotEvolution().initEvolution();
		//double[] pasd = Kinematics.calculatePosition(new Point(0.5, 0), new Point(0, 0), 0.0);
		//System.out.println(pasd[0]+" "+pasd[1]+" "+pasd[2]);
		//System.out.println(output);
		//new Frame(0,0,0);
	}
	
	
}
