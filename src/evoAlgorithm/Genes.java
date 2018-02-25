package evoAlgorithm;

import java.util.ArrayList;
import java.util.List;

import util.Point;

/** Genotype of an Individual */
public class Genes{
	
	/** A List of raw Double values */
	private List<Double> vals;
	
	/** A List of Integers representing the indecis (with modulo) of vals that are added up to produce X */
	private List<Integer> xVals;
	/** A List of Integers representing the indecis (with modulo) of vals that are added up to produce Y */
	private List<Integer> yVals;
	
	
	/** Returns a Point representing the expression of this set of Genes */
	public Point pheno(){
		double[] xy = new double[]{0, 0};
		List<Integer>[] lxy = new List[]{xVals, yVals};
		for(int c=0; c<lxy.length; c++){
			for(int c2=0; c2<lxy[c].size(); c2++){
				xy[c] += vals.get(lxy[c].get(c2) % vals.size());
			}
		}
		return new Point(xy[0], xy[1]);
	}
	
}
