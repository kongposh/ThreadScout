package test;

public class TSTest {

	public static void main(String[] args) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("Running thread");
			}
		}).start();
	}
}
