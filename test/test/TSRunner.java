package test;

public class TSRunner {

	public static void main(String[] args) {
		try {
			while (true) {
				Thread nt = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// TSTest t = new TSTest();
						// t.runTest();

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
				});
				nt.start();
				// System.exit(0);
				nt.join();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
