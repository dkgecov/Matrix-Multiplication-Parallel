package source;

import java.util.HashMap;
import java.util.Map;

public class RandomMatrixGenerator {

	private int numberOfThreads = 4;

	public RandomMatrixGenerator() {

	}

	public RandomMatrixGenerator(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	public double[][] generateMatrix(int rowsNumber, int columnsNumber, int lowerBound, int upperBound) {

		double[][] resultMatrix = new double[rowsNumber][columnsNumber];
		Map<String, Object> neededArguments = new HashMap<>();
		neededArguments.put("resultMatrix", resultMatrix);
		neededArguments.put("lowerBound", lowerBound);
		neededArguments.put("upperBound", upperBound);

		Thread[] asignedGenerators = new ThreadAssignor(this.numberOfThreads).getAssignedThreads(neededArguments,
				RandomRowsGenerator::new);

		for (Thread t : asignedGenerators) {
			t.start();
		}

		try {
			for (Thread t : asignedGenerators) {
				t.join();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(Thread.currentThread().toString() + " was interrupted!");
		}

		return resultMatrix;// the generated matrix
	}

}
