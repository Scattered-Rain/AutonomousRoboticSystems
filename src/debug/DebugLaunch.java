package debug;

import optimization.benchmarks.OpFunction;
import util.Point;
import graphing.Graph;
import graphing.GraphElement;

public class DebugLaunch{
	
	public static void main(String[] args){
		new Graph(480, 400).addGraphElement(new GraphElement.GraphOpFunction(new OpFunction.Rastrigin(), new Point(0, 0), 0.14)).updateImg();
	}
	
}
