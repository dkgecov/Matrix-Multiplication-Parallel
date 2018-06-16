package source;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RandomRowsGenerator implements Runnable {
	private double[][] matrixToFill;
	private int startingRow;
	private int rowsToGenerate;
	private int upperBound;
	private int lowerBound;

	public RandomRowsGenerator(Map<String, Object> neededArguments) {
		this.upperBound = (int) neededArguments.get("upperBound");// required
		this.lowerBound = (int) neededArguments.get("lowerBound");// required
		this.matrixToFill = (double[][]) neededArguments.get("resultMatrix");// required
		this.startingRow = (int) neededArguments.get("startingRow");// will be
																	// set by
																	// threadAssignor
		this.rowsToGenerate = (int) neededArguments.get("rowsToOperate");// will
																			// be
																			// set
																			// by
																			// threadAssignor
	}

	public void run() {
		for (int i = startingRow; i < startingRow + rowsToGenerate; i++) {
			for (int j = 0; j < this.matrixToFill[0].length; j++) {
				this.matrixToFill[i][j] = this.lowerBound
						+ ThreadLocalRandom.current().nextInt(this.upperBound - this.lowerBound);
			}
		}
	}

}
