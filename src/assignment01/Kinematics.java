package assignment01;

import util.Point;

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
	public double[] calculatePosition(Point velocity, Point coordinates, double initial_theta) {
		if (velocity.getX() == velocity.getY()) {
			return moveLinearly(velocity, coordinates, initial_theta);
		}
		else {
			return move(velocity, coordinates, initial_theta);
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
	public double[] moveLinearly(Point velocity, Point coordinates, double initial_theta) {
		double left_velocity = velocity.getX();
		double right_velocity = velocity.getY();
		double total_velocity = (left_velocity + right_velocity)/2;
		double initial_x = coordinates.getX();
		double initial_y = coordinates.getY();
		double[] new_position = new double[3];
		new_position[0] = initial_x + total_velocity * DT;
		new_position[1] = initial_y + total_velocity * DT;
		new_position[2] = initial_theta;
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
	public double[] move(Point velocity, Point coordinates, double initial_theta) {
		double initial_x = coordinates.getX();
		double initial_y = coordinates.getY();
		double left_velocity = velocity.getX();
		double right_velocity = velocity.getY();
		double R = (L/2) * ((left_velocity + right_velocity) / (right_velocity - left_velocity));
		double omega = (right_velocity - left_velocity)/L;
		double[] ICC = {initial_x - R * Math.sin(Math.toRadians(initial_theta)), initial_y + R * Math.cos(Math.toRadians(initial_theta))};

		double[][] A = new double[3][3];
		A[0][0] = Math.cos(omega * DT);
		A[0][1] = - Math.sin(omega * DT);
		A[1][0] = Math.sin(omega * DT);
		A[1][1] = Math.cos(omega * DT);
		A[2][2] = 1.0;
 
		double[] B = new double[3];
		B[0] = initial_x - ICC[0];
		B[1] = initial_y - ICC[1];
		B[2] = Math.toRadians(initial_theta);

		double[] C = new double[3];
		C[0] = ICC[0];
		C[1] = ICC[1];
		C[2] = Math.toRadians(omega * DT);

		double[] new_position = new double[3];
		
		for(int i = 0; i < 3; i ++) {
			double sum = 0;
			
			for (int j = 0; j<3; j++) {
				sum += A[i][j]*B[j];
			}
			
			new_position[i] = sum + C[i];
		}
		
		new_position[2] = Math.toDegrees(new_position[2]);
		return new_position;
	}
}
