package tools.threadscout;

import acme.util.decorations.Decoration;
import acme.util.decorations.DecorationFactory;
import acme.util.decorations.DefaultValue;
import acme.util.option.CommandLine;
import jdk.nashorn.internal.objects.Global;
import rr.annotations.Abbrev;
import rr.event.JoinEvent;
import rr.event.MethodEvent;
import rr.event.NewThreadEvent;
import rr.event.StartEvent;
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
		System.out.println("Static block called");
		String trace = null;
		TSStep step = new TSStep();
		step.setTrace(trace);
		step.setTid("11@1");
		step.setNumOfSwitches(0);
		BoundedSemaphore sem = new BoundedSemaphore(1, 0);
		TSGlobalState.lockMap.put("11@1", sem);
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
			super.enter(me);
			// String methodName = me.getInfo().getName();
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
				sem.put("[Enter] Request for control by " + tsdao.getTsThreadId());
				System.out.println("[ENTER] Thread " + tsdao.getTsThreadId() + " Thread Creation Checkpoint Completed");
				sem.notifyPut("[ENTER] Transfer control back to controller by " + tsdao.getTsThreadId());
				System.out.println("[EXECUTION] Thread " + tsdao.getTsThreadId() + " Thread Creation Checkpoint");
				sem.put("[EXECUTION] Request for control by " + tsdao.getTsThreadId());

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
			System.out.println("[EXIT] Thread " + tsdao.getTsThreadId() + " finished its run");

			if (methodName.equals("run")) {

				if (tid == 0)
					return;
				BoundedSemaphore sem = TSGlobalState.lockMap.get(tsdao.getTsThreadId());
				System.out.println(
						"[EXECUTION] Thread " + tsdao.getTsThreadId() + " Thread Creation Checkpoint Completed");
				sem.notifyPut("[EXECUTION] Transfer control to controller by " + tsdao.getTsThreadId());
				System.out.println("[EXIT] Thread " + tsdao.getTsThreadId() + " Thread Exit Checkpoint");
				sem.put("[EXIT] Request control from controller by " + tsdao.getTsThreadId());
				TSGlobalState.printGlobalState();
				System.out.println("[EXIT] Setting completion status for tid " + tsdao.getTsThreadId());
				sem.setCompletionStatus(3);
				System.out.println("[EXIT] Thread " + tsdao.getTsThreadId() + " Thread Exit Checkpoint Completed");
				sem.notifyPut("[EXIT] Transfer to controller by " + tsdao.getTsThreadId());
				System.out.println(tsdao.getTsThreadId() + " got control ");
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

	// @Override
	// public void postJoin(JoinEvent je) {
	// super.postJoin(je);
	// // System.out.println("[POSTJOIN] Join event called for " +
	// // je.getThread().getTid() + " by "
	// // + Thread.currentThread().getName());
	// // int tid = je.getJoiningThread().getTid();
	// // if (tid == 1) {
	// // try {
	// // TSGlobalState.globalLock.release(2);
	// // } catch (InterruptedException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // }
	// }

	// @Override
	// public void preJoin(JoinEvent je) {
	// // TODO Auto-generated method stub
	// super.preJoin(je);
	// ShadowThread t = je.getThread();
	// TSThreadDAO tsThreadDAO = tsThreads.get(t);
	// String tid = tsThreadDAO.getTsThreadId();
	// System.out.println("[PREJOIN] Prejoin event called for " + tid);
	// if (tid == "11@1") {
	// TSGlobalState.lockMap.remove(tid);
	// try {
	// System.out.println("[PREJOIN} Waking up the controller");
	// TSGlobalState.globalLock.release(0);
	// System.out.println("[PREJOIN] Waiting on the QSTATE lock");
	//
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }

	// @Override
	// public void preStart(StartEvent se) {
	// super.preStart(se);
	// try {
	// System.out.println("Pre start called for tid " + se.getThread().getTid()
	// + " by thread : "
	// + Thread.currentThread().getName());
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// TODO Auto-generated method stub
	// int tid = e.getThread().getTid();
	// System.out.println("Inside On Create: " + tid);
	/*
	 * try { int pid = se.getThread().getTid(); int tid =
	 * se.getNewThread().getTid();
	 * System.out.println("Insid e On pre start for: " + tid); if (tid == 0)
	 * return; System.out.println("Parent thread id is: " + pid);
	 * TSGlobalState.printGlobalState();
	 * System.out.println("Creating new semaphore for thread id " + tid);
	 * BoundedSemaphore sem = new BoundedSemaphore(1, 0);
	 * TSGlobalState.lockMap.put(String.valueOf(tid), sem);
	 * TSGlobalState.printGlobalState(); if (tid == 0) return; if (pid == 0) {
	 * System.out.println("Thread 1 Waking controller for the first time");
	 * TSGlobalState.globalLock.release(0); System.out.println("Thread " + pid +
	 * " ready to wait for first time");
	 * TSGlobalState.lockMap.get(String.valueOf(pid)).acquire();
	 * System.out.println("Tharead " + pid + " wokeup for the first time"); }
	 * TSGlobalState.globalLock.release(0); System.out.println("Thread " + pid +
	 * " ready to wait");
	 * TSGlobalState.lockMap.get(String.valueOf(pid)).acquire();
	 * System.out.println("Thread " + pid + " wokeup");
	 * 
	 * } catch (Exception ex)
	 * 
	 * { ex.printStackTrace(); System.out.println(ex.getMessage()); }
	 */
	// }

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
			BoundedSemaphore sem = new BoundedSemaphore(1, 0);
			TSGlobalState.lockMap.put(ctsDao.getTsThreadId(), sem);
			TSGlobalState.printGlobalState();
		}
	}

	// @Override
	// public void preStart(StartEvent se) {
	// // TODO Auto-generated method stub
	// super.postStart(se);
	// try {
	// // System.out.println("[PRE START] Pre start called for thread id: "
	// // + se.getThread().getTid()
	// // + " by thread : " + Thread.currentThread().getName());
	// // int tid = se.getThread().getTid();
	// // TSGlobalState.printGlobalState();
	// // if (tid == 0)
	// // return;
	// // System.out.println("[PRE START] Thread " + tid + " Waking
	// // controller from pre start");
	// // TSGlobalState.globalLock.release(0);
	// // System.out.println("[PRESTART] Thread " + tid + " ready to wait
	// // on");
	// // TSGlobalState.lockMap.get(String.valueOf(tid)).acquire();
	// // System.out.println("[PRESTART] Thread " + tid + " wokeup");
	// } catch (Exception e) {
	// e.printStackTrace();
	// System.out.println(e.getMessage());
	// }
	// }

	// @Override
	// public void postStart(StartEvent se) {
	// // TODO Auto-generated method stub
	// super.postStart(se);
	// // System.out.println(
	// // "[POSTSTART] For Thread ID: " + se.getThread().getTid() + " by " +
	// // Thread.currentThread().getName());
	// }

}
