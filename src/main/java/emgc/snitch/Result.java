package emgc.snitch;

import java.util.ArrayList;
import java.util.List;

public class Result {
	public static final int MAX_RESULTS = 20;

	private final List<ResultItem> results = new ArrayList<>();
	private ResultItem owner;
	private int commits;

	public void calculate(final List<String> authors) {
		List<String> latestComits;
		commits = authors.size();
		if (commits > MAX_RESULTS) {
			commits = MAX_RESULTS;
			latestComits = authors.subList(0, MAX_RESULTS);
		} else {
			latestComits = authors;
		}

		while (!latestComits.isEmpty()) {
			final String current = authors.get(0);
			int times = 0;
			while (latestComits.remove(current)) {
				times++;
			}
			final ResultItem resultItem = new ResultItem(current, times);
			results.add(resultItem);
			if (owner == null || owner.getEdits() < resultItem.getEdits()) {
				owner = resultItem;
			}
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Owner: ");

		if (owner == null) {
			Snitch.LOGGER.severe("Owner is null");
		} else {
			builder.append(owner.getName());
		}
		builder.append("\t(last ");
		builder.append(commits);
		builder.append(" commits: ");

		for (final ResultItem item : results) {
			builder.append(item);
			builder.append(", ");
		}

		builder.append(")");
		return builder.toString();
	}

}
