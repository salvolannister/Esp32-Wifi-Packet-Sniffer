package trilateration;

import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;


public class TrilaterationTest {
	
	double[][] positions;
	double[] distances;
	TrilaterationFunction trilaterationFunction;
	
	private Optimum nonLinearOptimum;
	
	public void test() {
	TrilaterationFunction trilaterationFunction = new TrilaterationFunction(positions, distances);
	NonLinearLeastSquaresSolver nlSolver = new NonLinearLeastSquaresSolver(trilaterationFunction, new LevenbergMarquardtOptimizer());
	setNonLinearOptimum(nlSolver.solve());
	}

	public Optimum getNonLinearOptimum() {
		return nonLinearOptimum;
	}

	public void setNonLinearOptimum(Optimum nonLinearOptimum) {
		this.nonLinearOptimum = nonLinearOptimum;
	}
	
	public double[][] getPositions() {
		return positions;
	}
	
	public double[] getDistances() {
		return distances;
	}
	
	public void setPositions(double[][] positions) {
		this.positions = positions;
	}
	
	public void setDistances(double[] distances) {
		this.distances = distances;
	}
	
	
	
	
}
