package tools.threadscout;

import java.util.ArrayList;

public class TSController extends Thread {

	@Override
	public void run() {
		try {
			super.run();
			System.out.println("Controller started");

			while (!TSGlobalState.workQ.isEmpty()) {
				int status = 0;
				TSStep step = TSGlobalState.workQ.poll();
				String tid = step.getTid();
				System.out.println("Current step thread is: " + tid);
				Trace trace = step.getTrace();
				ArrayList<String> traceIds = trace.getTrace();
				for (String traceId : traceIds) {
					BoundedSemaphore sem = TSGlobalState.lockMap.get(traceId);
					System.out.println("SHOULD NOT BE HERE");
					status = sem.release();
				}

				while (status != 2) {
					System.out.println();
					System.out.println("Woke thread L up by waiting itself");
					status = TSGlobalState.lockMap.get(tid).release();
					System.out.println("Next step in trace: " + tid);
					trace.addTid(tid);
					for (String t : traceIds) {
						if (t.equals(tid))
							continue;
						TSStep s = new TSStep();
						s.setTrace(trace);
						s.setTid(t);
						TSGlobalState.workQ.offer(s);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
