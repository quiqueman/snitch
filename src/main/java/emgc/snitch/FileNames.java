package emgc.snitch;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;

public enum FileNames {
	CRITICAL_ISSUES("criticalissues.txt", "Clases con evidencias críticas"),
	FAILED_TESTS("failedtests.txt", "Test en fallo"),
	LOW_COVERAGE("lowcoverage.txt", "Clases que necesitan aumentar su cobertura de test unitarios");

	private String name;
	private String title;

	private FileNames(final String name, final String title) {
		this.name = name;
		this.title = title;
	}

	public Path getFile(final CommandLine cmd) {
		final String inputDir = cmd.getOptionValue("i");
		return FileSystems.getDefault().getPath(inputDir, name);
	}

	public String getTitle() {
		return title;
	}
}
