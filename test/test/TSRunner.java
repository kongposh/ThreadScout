package test;

public class TSRunner {

	public static void main(String[] args) {
		try {
			TSTest t = new TSTest();
			t.runTest();
			// System.exit(0);
			Thread.currentThread().join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
