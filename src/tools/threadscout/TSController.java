package tools.threadscout;

import java.util.ArrayList;
import java.util.Set;

public class TSController extends Thread {

	@Override
	public void run() {
		try {
			super.run();
			System.out.println("Controller started");

			while (!TSGlobalState.workQ.isEmpty()) {

				TSGlobalState.printGlobalState();
				System.out.println("Controller is waiting on the 1st step");
				TSGlobalState.globalLock.acquire();
				System.out.println("Controller wokeup from the 1st step");

				int status = 0;
				TSStep step = TSGlobalState.workQ.poll();

				System.out.println(" --- Q item ---");
				System.out.println(step);

				String tid = step.getTid();
				System.out.println("Current step thread is: " + tid);
				Trace trace = step.getTrace();
				ArrayList<String> traceIds = trace.getTrace();
				System.out.println("-- Executing trace --");
				for (String traceId : traceIds) {
					System.out.println("Trying to wakeup thread " + traceId);
					BoundedSemaphore sem = TSGlobalState.lockMap.get(traceId);
					sem.release(0);
				}

				while (status != 2) {
					System.out.println("Current step thread is: " + tid);
					System.out.println("Waking thread " + tid + " up");
					TSGlobalState.lockMap.get(tid).release(0);
					System.out.println("Controller ready to wait");
					status = TSGlobalState.globalLock.acquire();
					System.out.println("Controller wokeup");
					trace.addTid(tid);
					Set<String> tids = TSGlobalState.getTids();
					for (String t : tids) {
						System.out.print(t + " --- ");
					}
					for (String t : tids) {
						if (t.equals(tid))
							continue;
						TSStep s = new TSStep();
						s.setTrace(trace);
						s.setTid(t);
						System.out.println("The step being added is: " + s);
						TSGlobalState.workQ.offer(s);
					}
				}
			}
			System.out.println(" EXITING THREADSCOUT ");
			System.exit(0);
		} catch (

		Exception e)

		{
			e.printStackTrace();
		}
	}
}
