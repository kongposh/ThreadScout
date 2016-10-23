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
				System.out.println(
						"---------------------------------- New task from Q ---------------------------------");
				TSGlobalState.printGlobalState();
				System.out.println("[CONTROLLER] Controller is waiting on the 1st step");
				TSGlobalState.globalLock.acquire("controller");
				System.out.println("[CONTROLLER] Controller wokeup from the 1st step");

				System.out.println("[CONTROLLER] Controller is taking item from Q");
				int status = 0;
				TSStep step = TSGlobalState.workQ.poll();
				System.out.println("[CONTROLLER] --- Q item --- ");
				System.out.println(step);

				String tid = step.getTid();
				System.out.println("[CONTROLLER] Current step thread is: " + tid);
				Trace trace = step.getTrace();
				System.out.println("[CONTROLLER] Trace till current step: " + trace);
				ArrayList<String> traceIds = trace.getTrace();
				if (traceIds.size() > 0) {
					System.out.println("[CONTROLLER] -- Executing past trace -- ");
					for (String traceId : traceIds) {
						System.out.println("[CONTROLLER] Trying to wakeup thread " + traceId);
						BoundedSemaphore sem = TSGlobalState.lockMap.get(traceId);
						sem.release(0, "controller --> " + traceId);
						System.out.println("[CONTROLLER] Waiting on the global lock");
						int execstatus = TSGlobalState.globalLock.acquire("controller");
						System.out.println("[CONTROLLER] Controller wokeup");
					}
				}
				while (TSGlobalState.isQState()) {
					System.out.println("[CONTROLLER] --- Add new steps to trace --- ");
					System.out.println("[CONTROLLER] Current step thread is: " + tid);
					System.out.println("[CONTROLLER] Waking thread " + tid + " up");

					if (TSGlobalState.getTids().size() != 0) {
						BoundedSemaphore sem = TSGlobalState.lockMap.get(tid);
						System.out.println("[CONTROLLER] Completion status value is: " + sem.getCompletionStatus());
						if (sem != null && sem.getCompletionStatus() != 4) {

							if (sem.getCompletionStatus() == 3) {
								sem.setCompletionStatus(4);
							}
							sem.release(0, "controller --> " + tid);
						} else {
							String ntid = TSGlobalState.getNewThread();
							System.out.println("[CONTROLLER] Changing tid from " + tid + " to " + ntid);
							tid = ntid;
							continue;
							// sem = TSGlobalState.lockMap.get(tid);
							// sem.release(0);
						}
						System.out.println("[CONTROLLdER] Controller ready to wait");
						status = TSGlobalState.globalLock.acquire("controller");
						System.out.println("[CONTROLLER] Controller wokeup");
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
							System.out.println("[CONTROLLER] The step being added is: " + s);
							TSGlobalState.workQ.offer(s);
						}
					}
				}
				System.out.println("[CONTROLLER] Waiting for QSTATE LOCK");
				TSGlobalState.QStateLock.tryAndAct();
				System.out.println("[CONTROLLER] Waking up from QSTATE LOCK");
				TSGlobalState.globalLock = new BoundedSemaphore(1, 0);
				TSGlobalState.QStateLock = new BoundedSemaphore(1, 0);
			}
			System.out.println("[CONTROLLER] EXITING THREADSCOUT ");
			System.exit(0);
		} catch (Exception e)

		{
			e.printStackTrace();
		}
	}
}
