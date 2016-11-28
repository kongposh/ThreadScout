package tools.threadscout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class TSGlobalState {
	public static AtomicBoolean isFirstRun = new AtomicBoolean(true);
	public static Queue<TSStep> workQ = new LinkedList<TSStep>();
	public static HashMap<String, BoundedSemaphore> lockMap = new HashMap<String, BoundedSemaphore>();
	public static BoundedSemaphore globalLock = new BoundedSemaphore(1, 0, 0);
	public static BoundedSemaphore QStateLock = new BoundedSemaphore(1, 0, 0);

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

	public static String getNewThread() {
		Set<String> set = lockMap.keySet();
		ArrayList<String> arr = new ArrayList(set);
		Random random = new Random();
		while (true) {
			int val = random.nextInt(arr.size());
			String key = arr.get(val);
			if (lockMap.get(key).getCompletionStatus() != 4)
				return key;
		}
	}

	public static boolean isQState() {

		// return TSGlobalState.workQ.size() == 0;
		System.out.println("[WORKQ LENGTH] The lenght of workQ is: " + TSGlobalState.workQ.size());
		Set<String> keys = lockMap.keySet();
		for (String str : keys) {
			BoundedSemaphore sem = lockMap.get(str);
			System.out.println("[CHECK QSTATE] The completion status is: " + sem.getCompletionStatus());
			if (sem.getCompletionStatus() != 4)
				return false;
		}
		return true;
	}

	// public static HashMap<Strin>
}
