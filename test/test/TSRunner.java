package test;

public class TSRunner {
	private static Object lock = new Object();
	private static Object lock2 = new Object();
	private static boolean flag = false;

	public static void main(String[] args) {
		try {
			while (true) {
				Thread nt = new Thread(new Runnable() {

					@Override
					public void run() {
						TSTest t = new TSTest();
						t.runTest();
					}

				});
				nt.start();
				nt.join();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}