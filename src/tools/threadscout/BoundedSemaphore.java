package tools.threadscout;

/**
 * 
 * @author kongposhsapru
 * @see http://tutorials.jenkov.com/java-concurrency/semaphores.html
 */
public class BoundedSemaphore {
	private int signals = 1;
	private int bound = 0;
	private int status = 0;
	private int completionStatus = 0;

	public synchronized int getCompletionStatus() {
		return this.completionStatus;
	}

	public synchronized void setCompletionStatus(int status) {
		this.completionStatus = status;
	}

	public BoundedSemaphore(int upperBound, int status, int initialValue) {
		this.bound = upperBound;
		this.status = status;
		this.signals = initialValue;
	}

	// public synchronized int take(String name) throws InterruptedException {
	// System.out.println("[ACQUIRE-SEMAPHORE] Signal value is: " + signals + "
	// by " + name);
	// while (this.signals == 0) {
	// System.out.println("[ACQUIRE-SEMAPHORE] waiting with signal value: " +
	// signals + " by " + name);
	// wait();
	// System.out.println("[ACQUIRE-SEMAPHORE] waking up with signal value: " +
	// signals + " by " + name);
	//
	// }
	// int status = this.status;
	// this.signals--;
	// this.notify();
	// return status;
	// }
	//
	// public synchronized void put(int status, String name) throws
	// InterruptedException {
	// System.out.println("[RELEASE-SEMAPHORE] Signal value is: " + signals + "
	// by " + name);
	// while (this.signals == bound) {
	// System.out.println("[RELEASE-SEMAPHORE] waiting with signal value: " +
	// signals + " by " + name);
	// wait();
	// System.out.println("[RELEASE-SEMAPHORE] waiting with signal value: " +
	// signals + " by " + name);
	//
	// }
	// this.status = status;
	// this.signals++;
	// this.notify();
	// }

	// public synchronized void forcedNotify() {
	// this.signals++;
	// this.notify();
	// }
	//
	public synchronized void tryAndAct() throws InterruptedException {
		if (this.signals < bound) {
			signals++;
			wait();
		} else {
			this.notify();
			signals = 0;
		}

	}

	// public synchronized void transfer(int status, String name) throws
	// Exception {
	// //System.out.println("[transfer] " + name + " read to wait");
	// this.notify();
	// this.wait();
	// //System.out.println("[transfer] " + name + " wokeup");
	// }

	public synchronized void complete(String name) throws Exception {

		if (completionStatus == 4) {
			// System.out.println("[COMPLETE] Notification to complete : " +
			// name);
			notify();
		}
	}

	public synchronized void get(String name) throws Exception {
		System.out.println("[TRANSFER - GET] " + name);
		while (this.signals < bound) {
			wait();
		}

	}

	public synchronized void notifyGet(String name) throws Exception {
		System.out.println("[TRANSFER - NOTIFY GET] " + name);
		signals--;
		notify();
	}

	public synchronized void put(String name) throws Exception {
		System.out.println("[TRANSFER - PUT] " + name);
		while (this.signals == bound) {
			wait();
		}
	}

	public synchronized void notifyPut(String name) throws Exception {
		System.out.println("[TRANSFER - NOTIFY PUT] " + name);
		signals++;
		notify();
	}

	// public synchronized int transferControl(int status, String name) throws
	// InterruptedException {
	// System.out.println("[TRANSFER CONTROL] Control transfer called by " +
	// name + " with signal value " + signals);
	// if (this.signals < bound) {
	// signals++;
	// wait();
	//
	// } else {
	// signals--;
	// this.notify();
	// wait();
	// }
	// return status;
	// }
}