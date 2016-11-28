package tools.threadscout;

import acme.util.decorations.Decoration;
import acme.util.decorations.DecorationFactory;
import acme.util.decorations.DefaultValue;
import acme.util.option.CommandLine;
import jdk.nashorn.internal.objects.Global;
import rr.annotations.Abbrev;
import rr.event.AcquireEvent;
import rr.event.JoinEvent;
import rr.event.MethodEvent;
import rr.event.NewThreadEvent;
import rr.event.ReleaseEvent;
import rr.event.StartEvent;
import rr.state.ShadowLock;
import rr.state.ShadowThread;
import rr.tool.Tool;

@Abbrev("THREADSCOUT")
public class ThreadScoutTaskExecutor extends Tool {

	private Decoration<ShadowThread, TSThreadDAO> tsThreads = ShadowThread.makeDecoration("lc",
			DecorationFactory.Type.MULTIPLE, new DefaultValue<ShadowThread, TSThreadDAO>() {
				/**
				* 
				*/
				private static final long serialVersionUID = 8746177266853416672L;

				public TSThreadDAO get(ShadowThread st) {
					return null;
				}
			});

	static {
		System.out.println("[INITIALIZATION] Thread Scout tool initialized");
		String trace = null;
		TSStep step = new TSStep();
		step.setTrace(trace);
		step.setTid("11@1");
		step.setNumOfSwitches(0);
		BoundedSemaphore sem = new BoundedSemaphore(1, 0, 1);
		TSGlobalState.lockMap.put("11@1", sem);
		TSGlobalState.workQ.offer(step);
		TSGlobalState.printGlobalState();
		TSController controller = new TSController();
		controller.start();
	}

	public ThreadScoutTaskExecutor(String name, Tool next, CommandLine commandLine) {
		super(name, next, commandLine);
	}

	@Override
	public void enter(MethodEvent me) {
		try {
			super.enter(me);
			String methodName = me.getInfo().getName();
			int tid = me.getThread().getTid();
			TSThreadDAO tsdao = tsThreads.get(me.getThread());
			System.out.println("[ENTER] Current method name is:" + methodName + " in " + tsdao.getTsThreadId() + " by "
					+ Thread.currentThread().getName());
			if (methodName.equals("run")) {
				if (tid == 0)
					return;
				TSGlobalState.printGlobalState();
				System.out.println("[ENTER] Thread " + tsdao.getTsThreadId() + " Thread Creation Checkpoint");
				BoundedSemaphore sem = TSGlobalState.lockMap.get(tsdao.getTsThreadId());
				sem.setCompletionStatus(1);
				sem.put("[Enter] Request for control by " + tsdao.getTsThreadId());
				System.out.println("[ENTER] Thread " + tsdao.getTsThreadId() + " Thread Creation Checkpoint Completed");
				sem.notifyPut("[ENTER] Transfer control back to controller by " + tsdao.getTsThreadId());
				System.out.println("[EXECUTION] Thread " + tsdao.getTsThreadId() + " Thread Execution Checkpoint");
				sem.put("[EXECUTION] Request for control by " + tsdao.getTsThreadId());
				sem.setCompletionStatus(2);
			}

			// System.out.println("[ENTER] Extra info" + me.getInfo().);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void exit(MethodEvent me) {
		// TODO Auto-generated method stub
		super.exit(me);
		try {
			String methodName = me.getInfo().getName();
			int tid = me.getThread().getTid();
			TSThreadDAO tsdao = tsThreads.get(me.getThread());
			System.out.println("[EXIT] Current method name is:" + methodName + " in " + tsdao.getTsThreadId() + " by "
					+ Thread.currentThread().getName());
			if (methodName.equals("run")) {

				if (tid == 0)
					return;
				BoundedSemaphore sem = TSGlobalState.lockMap.get(tsdao.getTsThreadId());
				System.out.println("[EXECUTION] Thread " + tsdao.getTsThreadId() + " Thread Execution Completed");
				sem.notifyPut("[EXECUTION] Transfer control to controller by " + tsdao.getTsThreadId());
				System.out.println("[EXIT] Thread " + tsdao.getTsThreadId() + " Thread Exit Checkpoint");
				sem.put("[EXIT] Request control from controller by " + tsdao.getTsThreadId());
				TSGlobalState.printGlobalState();
				System.out.println("[EXIT] Setting completion status for tid " + tsdao.getTsThreadId());
				sem.setCompletionStatus(4);
				System.out.println("[EXIT] Thread " + tsdao.getTsThreadId() + " Thread Exit Checkpoint Completed");
				sem.notifyPut("[EXIT] Transfer to controller by " + tsdao.getTsThreadId());
				System.out.println("[POST_EXIT]" + tsdao.getTsThreadId() + " will end now");
				if (tsdao.getTsThreadId().equals("11@1")) {
					System.out.println("[EXIT] TS thread finished execution & waiting on QSTATE lock");
					TSGlobalState.QStateLock.tryAndAct();
					System.out.println("[EXIT] TS thread waking from the QSTATE lock");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void stop(ShadowThread td) {
		// TODO Auto-generated method stub
		super.stop(td);
		TSThreadDAO tsdao = tsThreads.get(td);
		System.out.println("[STOP] Thread stop called for ThreadId: " + tsdao.getTsThreadId() + "by "
				+ Thread.currentThread().getName());
	}

	@Override
	public boolean testAcquire(AcquireEvent ae) {
		// TODO Auto-generated method stub
		boolean isBlocked = true;
		int tid = ae.getThread().getTid();
		if (tid == 0)
			return super.testAcquire(ae);
		TSThreadDAO tsdao = tsThreads.get(ae.getThread());
		System.out.println("[TEST ACQUIRE] Test acquire event in " + tsdao.getTsThreadId() + " by "
				+ Thread.currentThread().getName());
		while (isBlocked) {
			try {
				System.out
						.println("[TEST ACQUIRE] Thread " + tsdao.getTsThreadId() + " Test Acquire Checkpoint reached");
				System.out.println("[TEST ACQUIRE] Thread waiting in test acquire loop - " + tsdao.getTsThreadId());
				BoundedSemaphore sem = TSGlobalState.lockMap.get(tsdao.getTsThreadId());
				sem.put("[Test Acquire] Request for control by " + tsdao.getTsThreadId());
				System.out.println("[TEST ACQUIRE] Thread " + tsdao.getTsThreadId() + " will attempt to take lock");
				ShadowLock lock = ae.getLock();
				if ((lock.getHoldingThread() == null)) {
					isBlocked = false;
					System.out.println("[TEST ACQUIRE] Thread " + tsdao.getTsThreadId() + " can acquire lock");
				} else {
					sem.setCompletionStatus(3);
					sem.notifyPut("[TEST ACQUIRE] Thread " + tsdao.getTsThreadId()
							+ " is blocked so transfer control to controller");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return super.testAcquire(ae);
	}

	@Override
	public void acquire(AcquireEvent ae) {
		// TODO Auto-generated method stub
		super.acquire(ae);
		try {
			int tid = ae.getThread().getTid();
			TSThreadDAO tsdao = tsThreads.get(ae.getThread());
			System.out.println(
					"[ACQUIRE] Acquire event in " + tsdao.getTsThreadId() + " by " + Thread.currentThread().getName());
			if (tid == 0)
				return;
			BoundedSemaphore sem = TSGlobalState.lockMap.get(tsdao.getTsThreadId());
			System.out.println("[ACQUIRE] Thread " + tsdao.getTsThreadId() + " Thread Acquire Checkpoint Completed");
			sem.notifyPut("[ACQUIRE] Transfer control to controller by " + tsdao.getTsThreadId());
			System.out.println("[EXECUTION - ACQ] Thread " + tsdao.getTsThreadId() + " Thread Execution Checkpoint");
			sem.put("[EXECUTION - ACQ] Request for control by " + tsdao.getTsThreadId());
			sem.setCompletionStatus(2);
		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void release(ReleaseEvent re) {
		// TODO Auto-generated method stub
		super.release(re);
		try {
			int tid = re.getThread().getTid();
			TSThreadDAO tsdao = tsThreads.get(re.getThread());
			System.out.println(
					"[RELEASE] Release event in " + tsdao.getTsThreadId() + " by " + Thread.currentThread().getName());
			System.out.println("[RELEASE] Thread " + tsdao.getTsThreadId() + " will release");

			if (tid == 0)
				return;
			TSGlobalState.printGlobalState();
			System.out.println("[RELEASE] Thread " + tsdao.getTsThreadId() + " Thread Release Checkpoint");
			BoundedSemaphore sem = TSGlobalState.lockMap.get(tsdao.getTsThreadId());
			sem.setCompletionStatus(1);
			sem.put("[RELEASE] Request for control by " + tsdao.getTsThreadId());
			System.out.println("[RELEASE] Thread " + tsdao.getTsThreadId() + " Thread Release Checkpoint Completed");
			sem.notifyPut("[Release] Transfer control back to controller by " + tsdao.getTsThreadId());
			System.out.println("[EXECUTION] Thread " + tsdao.getTsThreadId() + " Thread Execution Checkpoint");
			sem.put("[EXECUTION] Request for control by " + tsdao.getTsThreadId());
			sem.setCompletionStatus(2);
		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void create(NewThreadEvent e) {
		// TODO Auto-generated method stub
		super.create(e);
		int rrtid = e.getThread().getTid();
		if (rrtid == 0) {
			TSThreadDAO tsdao = new TSThreadDAO();
			tsdao.setPrefix("1");
			tsdao.setSuffix("1");
			tsdao.setCount(tsdao.getCount() + 1);
			System.out.println("[CREATE] Thread ID generated for main thread: " + tsdao.getTsThreadId());
			tsThreads.set(e.getThread(), tsdao);
			return;
		}
		ShadowThread pt = e.getThread().getParent();
		int prrtid = e.getThread().getParent().getTid();
		System.out.println("[CREATE] Create called for " + e.getThread().getTid() + " by "
				+ Thread.currentThread().getName() + " having parent id: " + prrtid);
		TSThreadDAO ctsDao = new TSThreadDAO();
		TSThreadDAO ptsDao = tsThreads.get(pt);
		String pidprefix = ptsDao.getPrefix();
		int count = ptsDao.getCount();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append("1");
		}

		if (prrtid != 0)
			ptsDao.setCount(count + 1);
		ctsDao.setPrefix(pidprefix + "1");
		ctsDao.setSuffix(sb.toString());
		ctsDao.setCount(1);
		System.out.println("[CREATE] Created id " + ctsDao.getTsThreadId() + " for thread having rrid " + rrtid);
		tsThreads.set(e.getThread(), ctsDao);

		if (!ctsDao.getTsThreadId().equals("11@1")) {
			System.out.println("[CREATE] Creating new semaphore for thread id " + ctsDao.getTsThreadId());
			BoundedSemaphore sem = new BoundedSemaphore(1, 0, 1);
			TSGlobalState.lockMap.put(ctsDao.getTsThreadId(), sem);
			TSGlobalState.printGlobalState();
		} else if (ctsDao.getTsThreadId().equals("11@1")) {
			System.out.println("[CREATE] Reset completion status of " + ctsDao.getTsThreadId() + " to " + 0);
			BoundedSemaphore sem = TSGlobalState.lockMap.get(ctsDao.getTsThreadId());
			sem.setCompletionStatus(0);
		}
	}

}
