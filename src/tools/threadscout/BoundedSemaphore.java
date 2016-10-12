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

	public synchronized int acquire() throws InterruptedException {
		while (this.signals == 0)
			wait();
		int status = this.status;
		this.signals--;
		this.notify();
		return status;
	}

	public synchronized void release(int status) throws InterruptedException {
		while (this.signals == bound)
			wait();
		this.status = status;
		this.signals++;
		this.notify();
	}
}