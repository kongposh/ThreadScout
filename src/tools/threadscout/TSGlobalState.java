package tools.threadscout;

import java.util.LinkedList;
import java.util.Queue;

public class TSGlobalState {
	public static Queue<TSStep> workQ = new LinkedList<TSStep>();
	public static Integer status = new Integer(0);
	public static Integer tstatus = new Integer(0);
	public static BoundedSemaphore c = new BoundedSemaphore(1);
	public static BoundedSemaphore l = new BoundedSemaphore(1);
	// public static HashMap<Strin>
}
