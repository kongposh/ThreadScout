package test;

import tools.threadscout.ThreadScoutTask;

public class TSTest extends ThreadScoutTask {

	@Override
	public void runTest() {
		// TODO Auto-generated method stub
		System.out.println("Thread 1 called");
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("Thread 2 called");
			}
		});
		t2.start();
		// try {
		// Thread.currentThread().join();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
}
