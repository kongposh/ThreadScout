package test;

import tools.threadscout.ThreadScoutTask;

public class TSTest extends ThreadScoutTask {

	@Override
	public void runTest() {
		// TODO Auto-generated method stub
		System.out.println("Thread 1 called");
		new  Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("Thread 2 called");
			}
		}).start();
	}
}
