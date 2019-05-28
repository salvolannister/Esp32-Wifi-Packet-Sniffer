

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.linear.RealVector;

public class MainClass {
	
	public static void main(String args[]) {
		
		double[][] positions = {
			{0,0}, 
			{0,1},
			{1,0},
		};
		
	
	/*	for(int i=0; i<3; i++) {
			for(int j=0; j<2; j++) {
				System.out.println(positions[i][j]);
			}
		} */
		
		double[] distances = {0.707, 0.707, 0.707};
	/*	for(int k=0; k<3; k++)
			System.out.println(distances[k]); */
		
		TrilaterationTest trit = new TrilaterationTest();
		trit.setPositions(positions);
		trit.setDistances(distances);
		System.out.println(trit.positions[0][0]);
		System.out.println(trit.positions[0][1]);
		System.out.println(trit.positions[1][0]);
		System.out.println(trit.positions[1][1]);
		System.out.println(trit.positions[2][0]);
		System.out.println(trit.positions[2][1]);
		System.out.println(trit.distances[0]);
		System.out.println(trit.distances[1]);
		System.out.println(trit.distances[2]);
		trit.test();
		
		Optimum opt = trit.getNonLinearOptimum();
		RealVector x = opt.getPoint();
		System.out.println(x);
		
		
		
		
		
			
		
		
	}

}
