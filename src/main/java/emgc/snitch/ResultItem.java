package emgc.snitch;

public class ResultItem {
	private final String name;
	private final int edits;

	public ResultItem(final String name, final int edits) {
		super();
		this.name = name;
		this.edits = edits;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append("=");
		builder.append(edits);
		return builder.toString();
	}

	public String getName() {
		return name;
	}

	public int getEdits() {
		return edits;
	}
}
