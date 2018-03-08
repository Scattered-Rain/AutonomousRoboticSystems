package assignment01;

import java.util.Arrays;
import java.util.Random;
//import assignment01.BotEvolution;

/** The ANN controlling the Bot */
public class ANN{
	
	/** Array that represents the number of nodes that can be found in each layer. index 0=input, index length-1=out, hence length represents number of layers */
	private static final int[] NODES_PER_LAYER = new int[]{14, 14, 2};
	
	/** Reference to the BotEvolution Class */
	private BotEvolution evo;
	
	/** The Weights of this ANN where dim 0=layer, 1=toNode and 2=fromNode*/
	private double weights[][][] = new double[2][][];
	
	
	/** Construct new ANN */
	public ANN(BotEvolution evo){
		this.evo = evo;
		for(int c=0; c<NODES_PER_LAYER.length-1; c++){
			initLayer(c, NODES_PER_LAYER[c], NODES_PER_LAYER[c+1]);
		}
	}
	
	/** Constructs new ANN with given weights */
	public ANN(BotEvolution evo, double[][][] weights){
		this.evo = evo;
		this.weights = weights;
	}
	
	
	/** Processes the sensor input given to it by the environment (sensorInput.length = 12) and returns the movement speed of the wheels (out.length = 2, where index 0 is left and 1 is right) */
	public double[] process(double[] sensorInput, double[] movement){
		double[] sum = new double[2];
		double[] sum_layer1 = new double[14];
		double[] out = new double[]{0,0};
		//first initialization
		double[][] weights_layer1 = weights[0];//initialisation_layer1();
		double[][] weights_layer2 = weights[1];//initialisation_layer2();

		double[] input = new double[sensorInput.length + movement.length];
		System.arraycopy(sensorInput, 0, input, 0, sensorInput.length);
		System.arraycopy(movement, 0, input, sensorInput.length, movement.length);
		
		//System.out.println(Arrays.deepToString(weights_layer2));
		
		for (int i=0;i<sum_layer1.length;i++){
			for (int j=0;j<input.length;j++){
				sum_layer1[i] += input[j]*weights_layer1[i][j];
			}
			sum_layer1[i] += 1.0;
			sum_layer1[i] = 1/(1+ Math.exp(-sum_layer1[i]));
		}
		for (int i=0;i<2;i++){
			for (int j=0;j<sum_layer1.length;j++){
				sum[i] += sum_layer1[j]*weights_layer2[i][j];
			}
			sum[i]+=1;
			out[i] = 1/(1+ Math.exp(-sum[i]));
		}
		//System.out.println(Arrays.toString(out));
		return out;
	}
	
	
	/** Randomly initializes the weights of the layer of the given index, corresponding to the number of connections to and connections from given. Returns initialized layer.*/
	public double[][] initLayer(int layer, int numberToNodes, int numberFromNodes){
		double[][] layerWeights = new double[numberFromNodes][numberToNodes];
		for (int i=0;i<layerWeights.length;i++){
			for (int j=0;j<layerWeights[i].length;j++){
				double randomValue = 0 + (1) * evo.getRandom().nextDouble();
				layerWeights[i][j]=randomValue;
			}
		}
		this.weights[layer] = layerWeights;
		return layerWeights;
	}
	
	
	/** The answer to the question of where Babies come from. NSFW. motherGeneSelectionBias is the chance of genes of the mother to spread relative to the father. Assumes both ANNs have same architecture.*/
	public static ANN crossoverAndMutation(ANN mother, ANN father, BotEvolution evo, double motherGeneSelectionBias, double chanceOfMutationPerGene){
		double[][][] weights = new double[mother.weights.length][][];
		for(int c=0; c<weights.length; c++){
			weights[c] = new double[mother.weights[c].length][];
			for(int c2=0; c2<weights[c].length; c2++){
				weights[c][c2] = new double[mother.weights[c][0].length];
				for(int c3=0; c3<weights[c][0].length; c3++){
					if(evo.getRandom().nextDouble()<chanceOfMutationPerGene){
						//Let there be pure mutation! (For one gene)
						weights[c][c2][c3] = evo.getRandom().nextDouble();
					}
					else{
						//Crossover & Creation
						weights[c][c2][c3] = evo.getRandom().nextDouble()<motherGeneSelectionBias?mother.weights[c][c2][c3]:father.weights[c][c2][c3];
					}
				}
			}
		}
		return new ANN(evo, weights);
	}
	
}
