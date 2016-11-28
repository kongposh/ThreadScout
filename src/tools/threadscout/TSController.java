package tools.threadscout;

import java.util.Set;

public class TSController extends Thread {

	@Override
	public void run() {
		try {
			super.run();
			System.out.println("[CONTROLLER] Controller started");
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
							sem.notifyGet("[CONTROLLER] controller notifying " + traceId);
							sem.get("[CONTROLLER] Controller waiting for " + traceId);
						}
					}
				} else {
					System.out.println("[CONTROLLER] No past trace");
				}
				BoundedSemaphore sem1 = TSGlobalState.lockMap.get(tid);
				sem1.get("[CONTROLLER] Controller take control before adding new steps " + tid);
				System.out.println("[CONTROLLER] Controller wokeup by " + tid);
				while (!TSGlobalState.isQState()) {
					System.out.println("[CONTROLLER] ************ Add new steps to trace ********** ");
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

					sem.notifyGet("[CONTROLLER] Controller giving control to " + tid);
					sem.get("[CONTROLLER] Controller waiting for " + tid + " to finish");
					System.out.println(
							"[CONTROLLER] completion status for thread " + tid + " is " + sem.getCompletionStatus());

					if (sem.getCompletionStatus() == 3) {
						continue;
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
						if (step.getNumOfSwitches() <= 2) {
							TSStep s = new TSStep();
							s.setTrace(trace);
							s.setTid(t);
							s.setNumOfSwitches(s.getNumOfSwitches() + 1);
							System.out
									.println("[CONTROLLER] *********** The step being added is: ****************" + s);
							TSGlobalState.workQ.offer(s);
						} else {
							System.out.println("[CONTROLLER] Number of switches limit reached");
						}
					}
				}
				System.out.println("[CONTROLLER] Waiting for QSTATE LOCK");
				TSGlobalState.QStateLock.tryAndAct();
				System.out.println("[CONTROLLER] Waking up from QSTATE LOCK");
				TSGlobalState.globalLock = new BoundedSemaphore(1, 0, 0);
				TSGlobalState.QStateLock = new BoundedSemaphore(1, 0, 0);
			}

			System.out.println("[CONTROLLER] EXITING THREADSCOUT ");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
