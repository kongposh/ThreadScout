package tools.threadscout;

import java.io.PrintWriter;

import acme.util.Util;
import acme.util.option.CommandLine;
import rr.annotations.Abbrev;
import rr.event.NewThreadEvent;
import rr.tool.Tool;

@Abbrev("THREADSCOUT")
public class ThreadScoutTaskExecutor extends Tool {

	public ThreadScoutTaskExecutor(String name, Tool next, CommandLine commandLine) {
		super(name, next, commandLine);
		PrintWriter pw = new PrintWriter(System.out);
		Util.setOut(pw);
		pw.write("ThreadScout PW");
		Util.error("ThreadScout Tool initialized");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void create(NewThreadEvent e) {
		// TODO Auto-generated method stub
		super.create(e);
		System.out.println("Inside On Create");
		Util.error("Thread created");
	}

}
