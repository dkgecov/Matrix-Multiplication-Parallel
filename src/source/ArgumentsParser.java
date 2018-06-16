package source;

import org.apache.commons.cli.*;

public class ArgumentsParser {

	private final Options options;

	public ArgumentsParser() {
		this.options = new Options();

		options.addOption("m", true, "Rows number of the left matrix A");
		options.addOption("n", true, "Columns number  of the left matrix A");
		options.addOption("k", true, "Columns number  of the right  matrix B");
		options.addOption("i", true, "File containing matrices A and B");
		options.addOption("o", true, "A file where the result matrix will be exported");
		options.addOption("t", true, "Number of threads");

		options.addOption("q", false, "Displays only the time for calculating");
		options.addOption("lb", true, "Lower bound for the generated matrix elements");
		options.addOption("ub", true, "Upper bound for the generated matrix elements");

	}

	public CommandLine parseOptions(String[] args) {

		CommandLineParser parser = new DefaultParser();

		try {

			return parser.parse(options, args);

		} catch (ParseException e) {

			new HelpFormatter().printHelp("matrix multiplication", "Available options", this.options, "", true);
		}

		return null;
	}

	public Options getOptions() {
		return options;
	}

}
