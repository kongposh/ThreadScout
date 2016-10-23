package tools.threadscout;

public class TSThreadDAO {

	private String tsThreadId = "1@1";
	private String prefix = null;
	private String suffix = null;
	private int count = 0;

	public String getTsThreadId() {
		return prefix + "@" + suffix;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	// public void setTsThreadId(String tsThreadId) {
	// this.tsThreadId = tsThreadId;
	// }

}
