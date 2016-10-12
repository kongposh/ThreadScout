package tools.threadscout;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class TSGlobalState {
	public static Queue<TSStep> workQ = new LinkedList<TSStep>();
	public static HashMap<String, BoundedSemaphore> lockMap = new HashMap<String, BoundedSemaphore>();
	public static BoundedSemaphore globalLock = new BoundedSemaphore(1, 0);

	public static void printGlobalState() {
		System.out.println("---- Global State ----");
		Set<String> keys = lockMap.keySet();
		for (String s : keys) {
			System.out.print("[" + s + "]");
		}
		System.out.println("");
	}

	public static Set<String> getTids() {
		Set<String> keySet = lockMap.keySet();
		return keySet;
	}

	// public static HashMap<Strin>
}
