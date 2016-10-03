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

	public BoundedSemaphore(int upperBound, int status) {
		this.bound = upperBound;
		this.status = status;
	}

	public synchronized void acquire(int status) throws InterruptedException {
		while (this.signals == bound)
			wait();
		this.signals++;
		this.status = status;
		this.notify();
	}

	public synchronized int release() throws InterruptedException {
		while (this.signals == 0)
			wait();
		this.signals--;
		int status = this.status;
		this.notify();
		return status;
	}
}