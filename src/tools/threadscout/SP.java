package tools.threadscout;

public class SP {

	static BoundedSemaphore bs = new BoundedSemaphore(1, 0);

	public static void main(String[] args) {
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					try {
						bs.transfer(0, "k");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("thread 1 called");
				}
			}
		});
		t1.start();

		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < 100; i++) {
					try {
						bs.transfer(0, "k");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("thread 2 called");
				}
			}
		});
		t2.start();
	}
}
