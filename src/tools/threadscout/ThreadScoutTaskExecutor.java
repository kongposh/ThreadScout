package tools.threadscout;

import acme.util.option.CommandLine;
import rr.annotations.Abbrev;
import rr.event.JoinEvent;
import rr.event.MethodEvent;
import rr.event.StartEvent;
import rr.state.ShadowThread;
import rr.tool.Tool;

@Abbrev("THREADSCOUT")
public class ThreadScoutTaskExecutor extends Tool {

	static {
		System.out.println("Static block called");
		Trace trace = new Trace();
		TSStep step = new TSStep();
		step.setTrace(trace);
		step.setTid("1");
		step.setNumOfSwitches(0);
		BoundedSemaphore sem = new BoundedSemaphore(1, 0);
		TSGlobalState.lockMap.put("1", sem);
		TSGlobalState.workQ.offer(step);
		TSGlobalState.printGlobalState();
		TSController controller = new TSController();
		controller.start();
	}

	public ThreadScoutTaskExecutor(String name, Tool next, CommandLine commandLine) {
		super(name, next, commandLine);
		System.out.println("Inside Thread Scout tool");
	}

	@Override
	public void enter(MethodEvent me) {
		try {
			String methodName = me.getInfo().getName();
			System.out.println("Current method name is:" + methodName);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void stop(ShadowThread td) {
		// TODO Auto-generated method stub
		super.stop(td);
		System.out.println("Thread stop called for ThreadIs:  " + td.getTid());
	}

	@Override
	public void postJoin(JoinEvent je) {
		super.postJoin(je);

		int tid = je.getJoiningThread().getTid();
		System.out.println("Inside post join for " + tid);
		if (tid == 1) {
			try {
				TSGlobalState.globalLock.release(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void preStart(StartEvent se) {
		super.preStart(se);

		// TODO Auto-generated method stub
		// int tid = e.getThread().getTid();
		// System.out.println("Inside On Create: " + tid);

		try {
			int pid = se.getThread().getTid();
			int tid = se.getNewThread().getTid();
			System.out.println("Insid e On pre start for: " + tid);
			if (tid == 0)
				return;
			System.out.println("Parent thread id is: " + pid);
			TSGlobalState.printGlobalState();
			System.out.println("Creating new semaphore for thread id " + tid);
			BoundedSemaphore sem = new BoundedSemaphore(1, 0);
			TSGlobalState.lockMap.put(String.valueOf(tid), sem);
			TSGlobalState.printGlobalState();
			if (tid == 0)
				return;
			if (pid == 0) {
				System.out.println("Thread 1 Waking controller for the first time");
				TSGlobalState.globalLock.release(0);
				System.out.println("Thread " + pid + " ready to wait for first time");
				TSGlobalState.lockMap.get(String.valueOf(pid)).acquire();
				System.out.println("Thread " + pid + " wokeup for the first time");
			}
			TSGlobalState.globalLock.release(0);
			System.out.println("Thread " + pid + " ready to wait");
			TSGlobalState.lockMap.get(String.valueOf(pid)).acquire();
			System.out.println("Thread " + pid + " wokeup");

		} catch (Exception ex)

		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
	}

}
