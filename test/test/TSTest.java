package test;

public class TSTest extends ThreadScoutTask {

	@Override
	public void runTest() {
		System.out.println("Thread 1 called");
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Thread 2 called");
			}
		});
		t2.start();
	}
}
