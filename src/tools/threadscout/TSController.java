package tools.threadscout;

public class TSController extends Thread {

	@Override
	public void run() {
		try {
			super.run();
			System.out.println("Controller started");
			System.out.println("Thread C ready to wait");
			TSGlobalState.l.release();
			System.out.println("Thread C wokeup");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
