package tools.threadscout;

import java.util.ArrayList;

public class Trace {

	private static String trace = "";

	public synchronized void addTid(String tid) {
		trace = trace + "-->" + tid;
	}

	public synchronized String getTrace() {
		return trace;
	}

	@Override
	public String toString() {
		return trace;
	}

	// public synchronized void copyTrace(Trace t) {
	//
	// ArrayList<String> t2 = t.getTrace();
	// for (String s : t2) {
	// trace.add(s);
	// }
	// }
}
