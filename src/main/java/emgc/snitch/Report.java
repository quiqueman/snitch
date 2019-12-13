package emgc.snitch;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;

public class Report {
	private final File reportFile;

	public Report(final CommandLine commandLine) {
		final String outputDir = commandLine.getOptionValue("o");
		reportFile = new File(outputDir, "sonar.csv");
		if (reportFile.exists()) {
			reportFile.delete();
		}
	}

	public void open() throws IOException {
		Desktop.getDesktop().open(reportFile);
	}

	public File getFile() {
		return reportFile;
	}

}
