package tools.threadscout;

import java.util.HashMap;

public class SP {

	public static void main(String[] args) {
		HashMap<String, BoundedSemaphore> m = new HashMap<>();
		BoundedSemaphore bs = new BoundedSemaphore(1, 0);
		m.put("1", bs);
		System.out.println(m.get("1").getCompletionStatus());
		BoundedSemaphore s = m.get("1");
		s.setCompletionStatus(3);
		System.out.println(m.get("1").getCompletionStatus());
	}
}
