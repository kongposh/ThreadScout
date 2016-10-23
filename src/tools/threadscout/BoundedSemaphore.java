package tools.threadscout;

/**
 * 
 * @author kongposhsapru
 * @see http://tutorials.jenkov.com/java-concurrency/semaphores.html
 */
public class BoundedSemaphore {
	private int signals = 0;
	private int bound = 0;
	private int status = 0;
	private int completionStatus = 0;

	public synchronized int getCompletionStatus() {
		return this.completionStatus;
	}

	public synchronized void setCompletionStatus(int status) {
		this.completionStatus = status;
	}

	public BoundedSemaphore(int upperBound, int status) {
		this.bound = upperBound;
		this.status = status;
	}

	public synchronized int acquire(String name) throws InterruptedException {
		System.out.println("[ACQUIRE-SEMAPHORE] Signal value is: " + signals + " by " + name);
		while (this.signals == 0) {
			System.out.println("[ACQUIRE-SEMAPHORE] waiting with signal value: " + signals + " by " + name);
			wait();
			System.out.println("[ACQUIRE-SEMAPHORE] waking up with signal value: " + signals + " by " + name);

		}
		int status = this.status;
		this.signals--;
		this.notify();
		return status;
	}

	public synchronized void release(int status, String name) throws InterruptedException {
		System.out.println("[RELEASE-SEMAPHORE] Signal value is: " + signals + " by " + name);
		while (this.signals == bound) {
			System.out.println("[RELEASE-SEMAPHORE] waiting with signal value: " + signals + " by " + name);
			wait();
			System.out.println("[RELEASE-SEMAPHORE] waiting with signal value: " + signals + " by " + name);

		}
		this.status = status;
		this.signals++;
		this.notify();
	}

	public synchronized void forcedNotify() {
		this.signals++;
		this.notify();
	}

	public synchronized void tryAndAct() throws InterruptedException {
		if (this.signals < bound) {
			wait();
			signals++;
		} else {
			this.notify();
		}

	}
}