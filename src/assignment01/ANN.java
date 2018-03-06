package assignment01;

import java.util.Arrays;
//import assignment01.BotEvolution;

/** The ANN controlling the Bot */
public class ANN{
	
	/** Processes the sensor input given to it by the environment (sensorInput.length = 12) and returns the movement speed of the wheels (out.length = 2, where index 0 is left and 1 is right) */
	public double[] process(double[] sensorInput, double[] movement){
		double[] sum = new double[2];
		double[] sum_layer1 = new double[14];
		double[] out = new double[]{0,0};
		//first initialization
		double[][] weights_layer1 = initialisation_layer1();
		double[][] weights_layer2 = initialisation_layer2();

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
		System.out.println(Arrays.toString(out));
		return out;
	}
	
	
	public double[][] initialisation_layer1(){
		double[][] weights_layer1 = new double[14][14];
		for (int i=0;i<weights_layer1.length;i++){
			for (int j=0;j<weights_layer1.length;j++){
				double randomValue = 0 + (1) * BotEvolution.getRandom().nextDouble();
				weights_layer1[i][j]=randomValue;
			}
		}
		return weights_layer1;
	}
	
	
	public double[][] initialisation_layer2(){
		double[][] weights_layer2 = new double[2][14];
		for (int i=0;i<2;i++){
			for (int j=0;j<14;j++){
				double randomValue = 0 + (1 - 0) * BotEvolution.getRandom().nextDouble();
				System.out.println(randomValue);
				weights_layer2[i][j]=randomValue;
			}
		}
		return weights_layer2;
	}
}
