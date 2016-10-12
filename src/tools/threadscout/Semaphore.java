package tools.threadscout;

public class Semaphore {
	private boolean signal = false;
	private int status = 0;

	// thread takes lock
	public synchronized void acquire(int status) {
		this.signal = true;
		this.status = status;
		this.notify();
	}

	// thread releases
	public synchronized int release() throws InterruptedException {
		while (!this.signal)
			wait();
		int status = this.status;
		this.signal = false;
		return status;
	}

}
