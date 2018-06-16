package source;

import java.util.Map;
import java.util.function.Function;

public class ThreadAssignor {
	private int threadNumber;

	public ThreadAssignor(int threadNumber) {
		this.threadNumber = threadNumber;
	}

	public Thread[] getAssignedThreads(Map<String, Object> neededArguments,
			Function<Map<String, Object>, Runnable> threadFactory) {

		int totalRows = ((double[][]) neededArguments.get("resultMatrix")).length;
		int partition = totalRows / this.threadNumber;
		int remainder = totalRows % this.threadNumber;
		Thread[] threads = new Thread[this.threadNumber];

		for (int i = 0; i < this.threadNumber - 1; i++) {
			neededArguments.put("startingRow", i * partition);
			neededArguments.put("rowsToOperate", partition);
			threads[i] = new Thread(threadFactory.apply(neededArguments));
		}
		neededArguments.put("startingRow", (this.threadNumber - 1) * partition);
		neededArguments.put("rowsToOperate", partition + remainder);
		threads[this.threadNumber - 1] = new Thread(threadFactory.apply(neededArguments));

		return threads;
	}// overall lines code are equal to the function getAdignedThreads in
		// MatrixMultiplier

}
