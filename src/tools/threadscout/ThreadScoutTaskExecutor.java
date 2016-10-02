package tools.threadscout;

import acme.util.option.CommandLine;
import rr.annotations.Abbrev;
import rr.event.NewThreadEvent;
import rr.tool.Tool;

@Abbrev("THREADSCOUT")
public class ThreadScoutTaskExecutor extends Tool {

	public ThreadScoutTaskExecutor(String name, Tool next, CommandLine commandLine) {
		super(name, next, commandLine);

		System.out.println("Inside Thread Scout tool!");
		TSController controller = new TSController();
		controller.start();
		// System.out.println("ThredScoutTool Created");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void create(NewThreadEvent e) {
		// TODO Auto-generated method stub
		super.create(e);
		try {
			System.out.println("Inside On Create: " + e.getThread().getTid());
			System.out.println("Thread L finished its job");
			System.out.println("Thread L ready to wait");
			TSGlobalState.l.acquire();

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

}
