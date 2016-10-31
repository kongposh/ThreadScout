package tools.threadscout;

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
				System.out.println("[CONTROLLER] Controller is taking item from Q");
				TSStep step = TSGlobalState.workQ.poll();
				System.out.println("[CONTROLLER] --- Q item --- ");
				System.out.println(step);
				String tid = step.getTid();
				System.out.println("[CONTROLLER] Current step thread is: " + tid);
				String trace = step.getTrace();
				System.out.println("[CONTROLLER] Trace till current step: " + trace);
				if (trace != null) {
					String[] traceIds = trace.split(">");
					System.out.println("{CONTROLLER] Size is: " + traceIds.length);
					if (traceIds.length > 0) {
						System.out.println("[CONTROLLER] -- Executing past trace -- ");
						for (String traceId : traceIds) {
							BoundedSemaphore sem = TSGlobalState.lockMap.get(traceId);
							sem.get("controller waiting for " + traceId);
							System.out.println("[CONTROLLER] Controller wokeup by " + traceId);
							if (sem.getCompletionStatus() == 3)
								sem.setCompletionStatus(4);
							sem.notifyGet("Return control to thread " + traceId);
						}
					}
				} else {
					System.out.println("[CONTROLLER] No past trace");
				}
				while (!TSGlobalState.isQState()) {
					System.out.println(
							"[CONTROLLER] *************************************** Add new steps to trace --- ");
					System.out.println("[CONTROLLER] Current step thread is: " + tid);
					BoundedSemaphore sem = TSGlobalState.lockMap.get(tid);

					if (sem.getCompletionStatus() == 4) {
						System.out.println("[controller came here]");
						String ntid = TSGlobalState.getNewThread();
						System.out.println("[CONTROLLER] New threadId is " + ntid);
						System.out.println("[CONTROLLER] Changing tid from " + tid + " to " + ntid);
						tid = ntid;
						continue;
					}

					sem.get("Controller called for control from " + tid);
					System.out.println("[CONTROLLER] Controller wokeup by " + tid);
					System.out.println(
							"[CONTROLLER] completion status for thread " + tid + " is " + sem.getCompletionStatus());

					if (sem.getCompletionStatus() == 3) {
						sem.setCompletionStatus(4);
					}
					if (trace == null)
						trace = tid;
					else
						trace = trace + ">" + tid;

					Set<String> tids = TSGlobalState.getTids();
					for (String t : tids) {
						if (TSGlobalState.lockMap.get(t).getCompletionStatus() == 4)
							continue;
						if (t.equals(tid))
							continue;
						TSStep s = new TSStep();
						s.setTrace(trace);
						s.setTid(t);
						System.out.println(
								"[CONTROLLER] *************************************************************The step being added is: "
										+ s);
						TSGlobalState.workQ.offer(s);
					}

					sem.notifyGet("Controller gave control to " + tid);
				}
				System.out.println("[CONTROLLER] Waiting for QSTATE LOCK");
				TSGlobalState.QStateLock.tryAndAct();
				System.out.println("[CONTROLLER] Waking up from QSTATE LOCK");
				TSGlobalState.globalLock = new BoundedSemaphore(1, 0);
				TSGlobalState.QStateLock = new BoundedSemaphore(1, 0);
			}

			System.out.println("[CONTROLLER] EXITING THREADSCOUT ");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
