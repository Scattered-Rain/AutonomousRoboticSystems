package assignment01;

import java.util.Random;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import lombok.Getter;

/** An Artificial Neural Network */
public class ANN20{
	
	
	/** The Learning Factor used for Reinforcement Learning*/
	private static final double LEARNING_FACTOR = 16f;
	
	
	/** The number of nodes that are in a layer, including input and output layer */
	private int[] nodes;
	
	/** The weights used in the ANN, in the form [layer][node], so that layer=0 is the set of weigths feeding into the first
	 * hidden layer, etc., while node is ordered so that input 0 to hidden 0, input 0 to hidden 1 ... input i to input j 
	 * while the bias node is assumed to be the last node in the list, i.e. bias node of layer k leads to nodes[k]+1*/
	@Getter private double[][] weights;
	
	
	/** Constructor used by crossover */
	private ANN20(double[][] weights, int[] nodes, BotEvolution evo){
		this.weights = weights;
		this.nodes = nodes;
	}
	
	
	/** Initializes ANN */
	public ANN20(int inputSize, int outputSize, int ... hiddenSize){
		//init nodes
		int[] nodes = new int[2+hiddenSize.length];
		nodes[0] = inputSize;
		nodes[nodes.length-1] = outputSize;
		for(int c=0; c<hiddenSize.length; c++){
			nodes[c+1] = hiddenSize[c];
		}
		this.nodes = nodes;
		//init weights
		double[][] weights = new double[nodes.length-1][];
		for(int c=0; c<weights.length; c++){
			weights[c] = new double[(nodes[c]+1)*nodes[c+1]];
			for(int c2=0; c2<weights[c].length; c2++){
				weights[c][c2] = initWeight();
			}
		}
		this.weights = weights;
	}
	
	/** Returns the initial value of a weight */
	private double initWeight(){
		final Random rand = new Random();
		return rand.nextFloat()*2-1;
	}
	
	/** Returns the number of nodes in the input layer */
	public int getInputSize(){
		return nodes[0];
	}
	
	/** Processes the ANN, returns the values of the output nodes in order, input must contain as many values as there are input nodes */
	public double[] process(double[] input){
		return processAll(input)[nodes.length-1];
	}
	
	/** Processes all nodes, returns 2d vector containing the values of all nodes after processing, including input and output */
	private double[][] processAll(double[] input){
		double[][] nodeVals = new double[nodes.length][];
		nodeVals[0] = input;
		for(int c=1; c<nodeVals.length; c++){
			nodeVals[c] = layerProcess(nodeVals[c-1], c);
		}
		return nodeVals;
	}
	
	/** Processes a single layer and returns the values associated with the nodes of the following layer */
	private double[] layerProcess(double[] input, int nextLayer){
		double[] out = new double[nodes[nextLayer]];
		for(int c2=0; c2<out.length; c2++){
			out[c2] = calcNode(nextLayer, c2, input);
		}
		return out;
	}
	
	/** Returns the value of the given node */
	private double calcNode(int layer, int node, double[] previousLayer){
		double z = 0;
		for(int c=0; c<previousLayer.length; c++){
			z += previousLayer[c] * retrieveWeight(layer-1, c, node);
		}
		z += 1 * retrieveWeight(layer-1, previousLayer.length, node);//Bias Nodes
		double a = gFunction(z);
		return a;
	}
	
	/** The wrapping function for the combination of weighted inputs in a node */
	private double gFunction(double zValue){
		return (double) (1f / (1f + Math.pow(Math.E, -zValue)));
	}
	
	/** Makes the ANN learn */
	public void learn(double[][][] dataSet, int reps){
		double[][] deltas = new double[nodes.length-1][];
		for(int c=0; c<deltas.length; c++){
			deltas[c] = new double[(nodes[c]+1)*nodes[c+1]];
			for(int c2=0; c2<deltas[c].length; c2++){
				deltas[c][c2] = 0;
			}
		}
		for(int e=0; e<reps; e++){
			for(int d=0; d<dataSet.length; d++){
				double[] input = dataSet[d][0];
				double[] expectedResult = dataSet[d][1];
				double[][] errors = new double[nodes.length][];
				for(int c=0; c<errors.length; c++){
					errors[c] = new double[nodes[c]];
				}
				double[][] aVals = processAll(input);
				//Compute error
				for(int c=0; c<nodes.length-1; c++){
					int layer = nodes.length-c-1;
					for(int node=0; node<nodes[layer]; node++){
						if(layer==nodes.length-1){
							errors[layer][node] = aVals[layer][node] - expectedResult[node];
						}
						else{
							double weightedErrorSum = 0;
							for(int nextError=0; nextError<errors[layer+1].length; nextError++){
								weightedErrorSum  += errors[layer+1][nextError] * retrieveWeight(layer, node, nextError);
							}
							errors[layer][node] = (aVals[layer][node] * (1f-aVals[layer][node])) * weightedErrorSum;
						}
					}
				}
				for(int layer=0; layer<weights.length; layer++){
					for(int weight=0; weight<weights[layer].length; weight++){
						int[] dNodes = getNodes(layer, weight);
						double delta = 0;
						if(dNodes[0] == this.nodes[layer]){//Bias Node
							delta = errors[layer+1][dNodes[1]];
						}
						else{
							delta = deltas[layer][weight] + (aVals[layer][dNodes[0]] * errors[layer+1][dNodes[1]]);
						}
						deltas[layer][weight] = delta;
					}
				}
			}
		}
		double[][] newWeights = new double[this.weights.length][];
		for(int c=0; c<newWeights.length; c++){
			newWeights[c] = new double[this.weights[c].length];
		}
		for(int layer=0; layer<newWeights.length; layer++){
			for(int weight=0; weight<newWeights[layer].length; weight++){
				double temp = (1f/((double)dataSet.length*reps)) * (deltas[layer][weight]);
				newWeights[layer][weight] = weights[layer][weight] - LEARNING_FACTOR*temp;
			}
		}
		this.weights = newWeights;
	}
	
	/** Returns whether the (highest) error of the ANN given the testSet is below the accaptableError */
	public boolean isProperlyTrained(double[][][] testSet, double acceptableError){
		for(int c=0; c<testSet.length; c++){
			double[] annOut = this.process(testSet[c][0]);
			for(int c2=0; c2<testSet[c][1].length; c2++){
				if(!(Math.abs(testSet[c][1][c2]-annOut[c2])<=acceptableError)){
					return false;
				}
			}
		}
		return true;
	}
	
	/** Set the described weight to the given weight */
	private void putWeight(int baseLayer, int baseNode, int nextLayerNode, float weight){
		this.weights[baseLayer][baseNode*nodes[baseLayer+1]+nextLayerNode] = weight;
	}
	
	/** Returns the weight belonging to the connection between the given node and the node of the next layer, based on the given current layer */
	private double retrieveWeight(int baseLayer, int baseNode, int nextLayerNode){
		return weights[baseLayer][baseNode*nodes[baseLayer+1]+nextLayerNode];
	}
	
	/** Returns int[]{baseNode, nextLayerNode} of the given weight index for the baseLayer */
	private int[] getNodes(int baseLayer, int weightIndex){
		return new int[]{weightIndex/nodes[baseLayer+1], weightIndex%nodes[baseLayer+1]};
	}
	
	
	/** The answer to the question of where Babies come from. NSFW. motherGeneSelectionBias is the chance of genes of the mother to spread relative to the father. Assumes both ANNs have same architecture.*/
	public static ANN20 crossoverAndMutation(ANN20 mother, ANN20 father, BotEvolution evo, double motherGeneSelectionBias, double chanceOfMutationPerGene){
		double[][] weights = new double[mother.weights.length][];
		for(int c=0; c<weights.length; c++){
			weights[c] = new double[mother.weights[c].length];
			for(int c3=0; c3<weights[c].length; c3++){
				if(evo.getRandom().nextDouble()<chanceOfMutationPerGene/8){
					//Let there be pure mutation! (For one gene)
					weights[c][c3] = evo.getRandom().nextDouble();
				}
				else if(evo.getRandom().nextDouble()<chanceOfMutationPerGene){
					//Let there be more mutation! (For one gene)
					weights[c][c3] = evo.getRandom().nextDouble();
					int d0 = evo.getRandom().nextInt(mother.weights.length);
					int d2 = evo.getRandom().nextInt(mother.weights[d0].length);
					weights[c][c3] = evo.getRandom().nextDouble()<motherGeneSelectionBias?mother.weights[d0][d2]:father.weights[d0][d2];
				}
				else{
					//Crossover & Creation
					weights[c][c3] = evo.getRandom().nextDouble()<motherGeneSelectionBias?mother.weights[c][c3]:father.weights[c][c3];
				}
			}
		}
		return new ANN20(weights, mother.nodes, evo);
	}
	
	
	
}
