package tools.threadscout;

import acme.util.option.CommandLine;
import rr.annotations.Abbrev;
import rr.event.MethodEvent;
import rr.event.NewThreadEvent;
import rr.tool.Tool;

@Abbrev("THREADSCOUT")
public class ThreadScoutTaskExecutor extends Tool {

	static {
		System.out.println("Static block called");
		Trace trace = new Trace();
		TSStep step = new TSStep();
		step.setTrace(trace);
		step.setTid("0");
		step.setNumOfSwitches(0);
		BoundedSemaphore sem = new BoundedSemaphore(1, 0);
		TSGlobalState.lockMap.put("0", sem);
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
	public void create(NewThreadEvent e) {
		// TODO Auto-generated method stub
		super.create(e);
		try {
			int tid = e.getThread().getTid();
			System.out.println("Inside On Create: " + tid);
			System.out.println("Thread L finished its job");
			System.out.println("Thread L ready to wait");
			if (tid != 0) {

			}
			TSGlobalState.printGlobalState();
			BoundedSemaphore sem = TSGlobalState.lockMap.get(String.valueOf(tid));
			sem.acquire(0); // Put ok
			System.out.println("Thread L wokeup");

		} catch (

		Exception ex)

		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}

	}

	@Override
	public void enter(MethodEvent me) {
		super.enter(me);
		try {
			String methodName = me.getInfo().getName();
			System.out.println("Current method name is:" + methodName);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
