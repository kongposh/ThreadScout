package tools.threadscout;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class TSGlobalState {
	public static Queue<TSStep> workQ = new LinkedList<TSStep>();
	public static HashMap<String, BoundedSemaphore> lockMap = new HashMap<String, BoundedSemaphore>();

	public static void printGlobalState() {
		System.out.println("---- Global State ----");
		Set<String> keys = lockMap.keySet();
		for (String s : keys) {
			System.out.print("[ " + s + "]");
		}
	}
	// public static HashMap<Strin>
}
