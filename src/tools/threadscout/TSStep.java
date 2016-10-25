package tools.threadscout;

public class TSStep {

	private String trace;
	private String tid;
	private int numOfSwitches;

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("TID: " + tid + "\n");
		sb.append("Trace: " + "\n");
		sb.append(trace);
		return sb.toString();
	}

}
