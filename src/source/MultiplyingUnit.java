package source;

import java.util.Map;

public class MultiplyingUnit implements Runnable {

	private final double[][] matrixA;
	private final double[][] matrixB;
	private final double[][] resultMatrix;
	private final int rowsToCalc;
	private final int startingRow;
	boolean quiet = false;

	private double rowByColumnProduct(double[] row, double[][] matrixB, int columnIndex) {
		double S = 0;
		for (int i = 0; i < row.length; i++) {
			S += row[i] * matrixB[i][columnIndex];
		}

		return S;

	}

	public MultiplyingUnit(final double[][] matrixA, final double[][] matrixB, final double[][] resultMatrix,
			final int startingRow, final int rowsToCalc) {
		this.matrixA = matrixA;
		this.matrixB = matrixB;
		this.resultMatrix = resultMatrix;
		this.rowsToCalc = rowsToCalc;
		this.startingRow = startingRow;

	}

	public MultiplyingUnit(Map<String, Object> neededArguments) {
		this.matrixA = (double[][]) neededArguments.get("matrixA");
		this.matrixB = (double[][]) neededArguments.get("matrixB");
		this.resultMatrix = (double[][]) neededArguments.get("resultMatrix");// same
		this.startingRow = (int) neededArguments.get("startingRow");// same
		this.rowsToCalc = (int) neededArguments.get("rowsToOperate");// same
		if (neededArguments.containsKey("quiet"))
			this.quiet = (boolean) neededArguments.get("quiet");
	}

	@Override
	public void run() {
		for (int i = startingRow; i < startingRow + rowsToCalc; i++) {
			double[] resVector = new double[this.matrixB[0].length];
			for (int j = 0; j < matrixB[0].length; j++)// iterating over B
														// columns
			{
				resVector[j] = rowByColumnProduct(matrixA[i], matrixB, j);// not
																			// slower
																			// than
																			// nested
																			// for
			}
			this.resultMatrix[i] = resVector;
			if (!quiet)
				System.out.println("a row was calculated by " + Thread.currentThread().toString());
		}
	}

}
