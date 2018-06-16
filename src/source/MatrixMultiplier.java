package source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

public class MatrixMultiplier {

	private double[][] A, B, C;
	private final ArgumentsParser argumentsParser;
	private long computationTime = 0;
	private boolean quiet = false;

	public MatrixMultiplier() {
		this.argumentsParser = new ArgumentsParser();
	}

	private double[][] findProduct(final double[][] A, final double[][] B, final int threadNumber) {

		double[][] resultMatrix = new double[A.length][B[0].length];
		Map<String, Object> neededArguments = new HashMap<>();
		neededArguments.put("resultMatrix", resultMatrix);
		neededArguments.put("matrixA", A);
		neededArguments.put("matrixB", B);
		if (this.quiet)
			neededArguments.put("quiet", true);

		Thread[] asignedThreads = new ThreadAssignor(threadNumber).getAssignedThreads(neededArguments,
				MultiplyingUnit::new);

		long startingTime = System.nanoTime();
		for (Thread t : asignedThreads) {
			t.start();
		}

		try {
			for (Thread t : asignedThreads) {
				t.join();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(Thread.currentThread().toString() + " was interrupted!");
		}
		long finishingTime = System.nanoTime();

		this.computationTime = finishingTime - startingTime;

		return resultMatrix;
	}

	private double[][] importMatrix(int rowsNum, int columnsNum, BufferedReader in) throws IOException {

		double[][] importedMatrix = new double[rowsNum][columnsNum];
		int rowsRead = 0;
		String line;
		while (rowsRead < rowsNum && (line = in.readLine()) != null) {
			String[] splitLine = line.split(" ");
			for (int j = 0; j < columnsNum; j++) {
				importedMatrix[rowsRead][j] = Double.parseDouble(splitLine[j]);
			}
			rowsRead++;
		}
		return importedMatrix;
	}

	private void importMatrices(String fileName) {

		String line;
		try (BufferedReader in = new BufferedReader(new FileReader(fileName));) {
			if ((line = in.readLine()) != null) {
				String[] splitLine = line.split(" ");
				int m = Integer.parseInt(splitLine[0]);
				int n = Integer.parseInt(splitLine[1]);
				int k = Integer.parseInt(splitLine[2]);

				this.A = importMatrix(m, n, in);
				this.B = importMatrix(n, k, in);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void exportMatrix(double[][] matrix, String fileName) {

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));) {
			bw.write(matrix.length + " " + matrix[0].length + System.lineSeparator());// dimmensions
			StringBuilder sb = new StringBuilder();
			for (double[] r : matrix) {
				for (double cell : r) {
					sb.append(cell);
					sb.append(" ");
				}
				sb.append(System.lineSeparator());
			}
			bw.write(sb.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void compute(String[] args) {

		CommandLine parsedOptions = this.argumentsParser.parseOptions(args);
		// check if at least one of m n k and i is present
		if (!(parsedOptions.hasOption("m") && parsedOptions.hasOption("n") && parsedOptions.hasOption("k")
				&& parsedOptions.hasOption("t") && parsedOptions.hasOption("lb") && parsedOptions.hasOption("ub")
				|| parsedOptions.hasOption("i") && parsedOptions.hasOption("t"))) {
			new HelpFormatter().printHelp("matrix multiplication",
					"Required options: m, n, k, lb, ub  and t or i and t", this.argumentsParser.getOptions(), "", true);
			return;
		} else if (parsedOptions.hasOption("i")) {

			String fileName = parsedOptions.getOptionValue("i");
			int t = Integer.parseInt(parsedOptions.getOptionValue("t"));
			if (parsedOptions.hasOption("q"))
				this.quiet = true;
			try {
				importMatrices(fileName);
				this.C = findProduct(this.A, this.B, t);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Failed to import matrices: missing arguments"); // TEST

			}

		} else {
			int m = Integer.parseInt(parsedOptions.getOptionValue("m"));
			int n = Integer.parseInt(parsedOptions.getOptionValue("n"));
			int k = Integer.parseInt(parsedOptions.getOptionValue("k"));
			int t = Integer.parseInt(parsedOptions.getOptionValue("t"));
			int lowerBound = Integer.parseInt(parsedOptions.getOptionValue("lb"));
			int upperBound = Integer.parseInt(parsedOptions.getOptionValue("ub"));
			if (t < 1) {
				System.out.println(" -t must be atleast 1!");
				return;
			}
			if (parsedOptions.hasOption("q"))
				this.quiet = true;
			RandomMatrixGenerator randomMatrixGenerator = new RandomMatrixGenerator(t);
			this.A = randomMatrixGenerator.generateMatrix(m, n, lowerBound, upperBound);
			this.B = randomMatrixGenerator.generateMatrix(n, k, lowerBound, upperBound);
			this.C = findProduct(this.A, this.B, t);
		}
		if (parsedOptions.hasOption("q")) {
			System.out.println("Computation time: " + this.computationTime);
			return;
		}

		if (parsedOptions.hasOption("o") && this.C != null) {
			String fileName = parsedOptions.getOptionValue("o");
			exportMatrix(this.C, fileName);
			if (parsedOptions.hasOption("m") && !parsedOptions.hasOption("i")) {
				exportMatrix(this.A, "generatedMatrixA.txt");
				exportMatrix(this.B, "generatedMatrixB.txt");
			}
		}

		System.out.println("Computation time: " + this.computationTime);

	}

	public static void main(String[] args) {

		MatrixMultiplier matrixMultiplier = new MatrixMultiplier();
		matrixMultiplier.compute(args);
	}
}
