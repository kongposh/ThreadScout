package tools.threadscout;

import java.util.ArrayList;

public class Trace {

	private static ArrayList<String> trace = new ArrayList<String>();

	public void addTid(String tid) {
		trace.add(tid);
	}

	public ArrayList<String> getTrace() {
		return trace;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String str : trace) {
			sb.append(str);
			sb.append("-->");
		}
		return sb.toString();
	}
}
