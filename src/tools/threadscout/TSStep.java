package tools.threadscout;

public class TSStep {

	private Trace trace;
	private String tid;
	private int numOfSwitches;

	public Trace getTrace() {
		return trace;
	}

	public void setTrace(Trace trace) {
		this.trace = trace;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public int getNumOfSwitches() {
		return numOfSwitches;
	}

	public void setNumOfSwitches(int numOfSwitches) {
		this.numOfSwitches = numOfSwitches;
	}

}
