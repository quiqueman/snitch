package emgc.snitch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitLog {

	private static Map<String, List<String>> cache = new HashMap<>();

	public List<String> getAuthors(final File directory, final String path) throws IOException, InterruptedException {
		List<String> result = cache.get(path);

		if (result == null) {
			result = new ArrayList<>();

			String line;
			Snitch.LOGGER
					.info("executing: " + "git log --pretty=format:\"%an\" -- " + path + " in directory " + directory);
			final Process p = Runtime.getRuntime().exec("git log --pretty=format:\"%an\" -- " + path, null, directory);
			final BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			p.waitFor();
			while ((line = bri.readLine()) != null) {
				result.add(line);
			}
			bri.close();
			cache.put(path, result);
		}

		return result;
	}

}
