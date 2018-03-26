package assignment01;

import util.Point;
import util.Tuple;

public class Kinematics {
	private static final double L = 10;
	private static final int DT = 1;
	
	/**
	 * This method calculates the new position of the robot after time DT (which is specified as a 
	 * constant at the top of the class).
	 * 
	 * @param velocity - The initial velocity in the form Point(left_velocity, right_velocity)
	 * @param coordinates - The initial position using [x,y] coordinates in the form Point(x,y)
	 * @param initial_theta - The initial angle
	 * @return - The new position of the robot in the form [new_x, new_y, new_theta]
	 */
	public static double[] calculatePosition(Point velocity, Point coordinates, double initial_theta) {
		if (velocity.getX() == velocity.getY()) {
			return move(new Point(velocity.getX()+Point.EPSILON, velocity.getY()-Point.EPSILON), coordinates, -initial_theta);
		}
		else {
			return move(velocity, coordinates, -initial_theta);
		}
	}
	
	/**
	 * This method handles the case when the velocities of the left and right wheels are equal
	 * and the robot moves linearly with no rotation.
	 * 
	 * @param velocity - The initial velocity in the form Point(left_velocity, right_velocity)
	 * @param coordinates - The initial position using [x,y] coordinates in the form Point(x,y)
	 * @param initial_theta - The initial angle
	 * @return - The new position of the robot in the form [new_x, new_y, new_theta]
	 */
	private static double[] moveLinearly(Point velocity, Point coordinates, double initial_theta) {
		double left_velocity = velocity.getX();
		double right_velocity = velocity.getY();
		double total_velocity = (left_velocity + right_velocity)/2;
		double initial_x = coordinates.getX();
		double initial_y = coordinates.getY();
		double[] new_position = new double[3];
		new_position[0] = initial_x + total_velocity * DT;
		new_position[1] = initial_y + total_velocity * DT;
		new_position[2] = -initial_theta;
		return new_position;
	}
	
	/**
	 * This method handles the case when the velocities of the left and right wheels are not equal
	 * and there is some rotation involved in the movement of the robot.
	 * 
	 * @param velocity - The initial velocity in the form Point(left_velocity, right_velocity)
	 * @param coordinates - The initial position using [x,y] coordinates in the form Point(x,y)
	 * @param initial_theta - The initial angle
	 * @return - The new position of the robot in the form [new_x, new_y, new_theta]
	 */
	private static double[] move(Point velocity, Point coordinates, double initial_theta) {
		double initial_x = coordinates.getX();
		double initial_y = coordinates.getY();
		double left_velocity = velocity.getX();
		double right_velocity = velocity.getY();
		double R = (L/2) * ((left_velocity + right_velocity) / (right_velocity - left_velocity));
		double omega = (right_velocity - left_velocity)/L;
		double[] ICC = {initial_x - R * Math.sin(Math.toRadians(initial_theta*360)), initial_y + R * Math.cos(Math.toRadians(initial_theta*360))};

		double[][] A = new double[3][3];
		A[0][0] = Math.cos(omega * DT);
		A[0][1] = - Math.sin(omega * DT);
		A[1][0] = Math.sin(omega * DT);
		A[1][1] = Math.cos(omega * DT);
		A[2][2] = 1.0;
 
		double[] B = new double[3];
		B[0] = initial_x - ICC[0];
		B[1] = initial_y - ICC[1];
		B[2] = Math.toRadians(initial_theta*360);

		double[] C = new double[3];
		C[0] = ICC[0];
		C[1] = ICC[1];
		C[2] = Math.toRadians((omega * DT)*360);

		double[] new_position = new double[3];
		
		for(int i = 0; i < 3; i ++) {
			double sum = 0;
			
			for (int j = 0; j<3; j++) {
				sum += A[i][j]*B[j];
			}
			
			new_position[i] = sum + C[i];
		}
		
		new_position[2] = -Math.toDegrees(new_position[2]);
		return new_position;
	}


	private static double odometry_motion_model (Tuple initial_pose, Tuple final_pose, Tuple initial_odom_pose, Tuple final_odom_pose) {
//		Tuple<Point,Double> point = new Tuple<Point,Double>(initial_pose, initial_theta);
		double init_x = ((Point)initial_pose.getA()).getX();
		double init_y = ((Point)initial_pose.getA()).getY();
		double initial_theta = (double)initial_pose.getB();
		double final_x = ((Point)final_pose.getA()).getX();
		double final_y = ((Point)final_pose.getA()).getY();
		double final_theta = (double)final_pose.getB();
		double init_odom_x = ((Point)initial_odom_pose.getA()).getX();
		double init_odom_y = ((Point)initial_odom_pose.getA()).getY();
		double initial_odom_theta = (double)initial_odom_pose.getB();
		double final_odom_x = ((Point)final_odom_pose.getA()).getX();
		double final_odom_y = ((Point)final_odom_pose.getA()).getY();
		double final_odom_theta = (double)final_odom_pose.getB();
		
		double delta_rot1 = (Math.atan2(final_odom_y - init_odom_y,final_odom_x - init_odom_x)) - initial_odom_theta;
		double delta_trans_x = Math.pow(init_odom_x - final_odom_x, 2);
		double delta_trans_y = Math.pow(init_odom_y - final_odom_y, 2);
		double delta_trans = Math.sqrt(delta_trans_x + delta_trans_y);
		double delta_rot2 = final_odom_theta - initial_odom_theta - delta_rot1;
		
		double real_delta_rot1 = (Math.atan2(final_y - init_y,final_x - init_x)) - initial_theta;
		double real_delta_trans_x = Math.pow(init_x - final_x, 2);
		double real_delta_trans_y = Math.pow(init_y - final_y, 2);
		double real_delta_trans = Math.sqrt(real_delta_trans_x + real_delta_trans_y);
		double real_delta_rot2 = final_theta - initial_theta - real_delta_rot1;
		
		//TODO check the values for alphas
		//random choice here
		
		double alpha1 = 0.025;
		double alpha2 = 0.354;
		double alpha3 = 0.2;
		double alpha4 = 0.075;
		
		//normal distribution
		double p1 = prob_normal_distribution(delta_rot1 - real_delta_rot1, alpha1*real_delta_rot1 + alpha2*real_delta_trans);
		double p2 = prob_normal_distribution(delta_trans - real_delta_trans, alpha3*real_delta_trans + alpha4*(real_delta_rot1 + real_delta_rot2));
		double p3 = prob_normal_distribution(delta_rot2 - real_delta_rot2, alpha1*real_delta_rot2 + alpha2*real_delta_trans);
		
		//triangular distribution
		//double p1 = prob_triangular_distribution(delta_rot1 - real_delta_rot1, alpha1*real_delta_rot1 + alpha2*real_delta_trans);
		//double p2 = prob_triangular_distribution(delta_trans - real_delta_trans, alpha3*real_delta_trans + alpha4*(real_delta_rot1 + real_delta_rot2));
		//double p3 = prob_triangular_distribution(delta_rot2 - real_delta_rot2, alpha1*real_delta_rot2 + alpha2*real_delta_trans);
		
		return p1*p2*p3;
	}
	
	private static double prob_normal_distribution(double a, double b) {
		
		double denominator = Math.sqrt(2*(Math.PI)*(Math.pow(b, 2)));
		double numerator = Math.exp((-Math.pow(a, 2))/(2*Math.pow(b, 2)));
		double epsilon = numerator/denominator;
		
		return epsilon;
		
	}
	
	private static double prob_triangular_distribution(double a, double b) {
		
		double element1 = 1 / (b * Math.sqrt(6));
		double element2 = (Math.abs(a)) / (6 * Math.pow(b, 2));
		double epsilon = Math.max(0, element1 - element2);
		
		return epsilon;
		
	}
	
}

