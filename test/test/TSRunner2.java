package test;

public class TSRunner2 {
	private static Object lock = new Object();
	private static Object lock2 = new Object();
	private static boolean flag = false;

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

						// synchronized (lock) {
						Thread t2 = new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								System.out.println("Thread 2 called");
								/*
								 * synchronized (lock2) { flag = true;
								 * synchronized (lock) {
								 * System.out.println("Inside thread 2 lock"); }
								 * }
								 */
								// Thread t3 = new Thread(new Runnable() {
								//
								// @Override
								// public void run() {
								// System.out.println("Thread 3 called");
								// }
								// });
								// t3.start();
							}
						});
						/*
						 * t2.start(); /* while (!flag) {
						 * 
						 * } synchronized (lock2) {
						 * 
						 * System.out.println("should not reach here"); }
						 */

						// }
					}

				});
				nt.start();
				// System.exit(0);
				nt.join();
				// TSGlobalState.globalLock.release(2); */
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

}