package emgc.snitch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;

public class InputFile {

	private static final String[] PROJECTS = { "custom-shared-lib", "custom-client", "custom-dataserver-services",
			"custom-engine", "custom-shared-lib-test" };

	private List<String> sourceFiles = new ArrayList<>();
	private final FileNames fileName;

	public InputFile(final FileNames filename) {
		fileName = filename;
	}

	public void load(final CommandLine cmd) {
		final Path path = fileName.getFile(cmd);
		try (Stream<String> stream = Files.lines(path)) {
			Snitch.LOGGER.log(Level.INFO, "Loading " + path.getFileName());
			sourceFiles = stream.collect(Collectors.toList());

			check(cmd, sourceFiles);
		} catch (final java.nio.file.NoSuchFileException e) {
			Snitch.LOGGER.log(Level.WARNING, "No input for " + fileName);
		} catch (final IOException e) {
			Snitch.LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}

	private void check(final CommandLine cmd, final List<String> sourceFiles) {
		final String customProjectDir = cmd.getOptionValue("p");
		for (int i = 0; i < sourceFiles.size(); i++) {
			String sourceFile = sourceFiles.get(i);
			if (sourceFile.contains("/test/)")) {
				sourceFile = sourceFile.replaceAll("/test/", "/main/").replaceAll("Test\\.java", "\\\\.java");
			}
			final String sourceFileWithProject = calculateProject(customProjectDir, sourceFile);
			sourceFiles.set(i, sourceFileWithProject);
		}
	}

	private String calculateProject(final String customProjectDir, final String sourceFile) {
		File file = new File(customProjectDir, sourceFile);
		if (!file.canRead()) {
			for (final String project : PROJECTS) {
				file = new File(customProjectDir + File.separatorChar + project, sourceFile);
				if (file.canRead()) {
					return project + File.separatorChar + sourceFile;
				}
			}
			Snitch.LOGGER.log(Level.SEVERE, "file '{0}' can't be read", sourceFile);
		}
		return sourceFile;
	}

	/**
	 * Gets the source files.
	 *
	 * @return the source files
	 */
	public List<String> getSourceFiles() {
		return sourceFiles;
	}

	public void report(final File reportFile, final KnowledgeData knowledgeData) {
		try (final FileWriter fos = new FileWriter(reportFile, true)) {
			fos.write("=== ");
			fos.write(fileName.getTitle());
			fos.write(" ===\n");
			fos.write("==================================================================\n");
			for (final String sourceFile : sourceFiles) {
				fos.write(sourceFile);
				fos.write('\t');
				fos.write(knowledgeData.getResult(sourceFile).toString());
				fos.write('\n');
			}
			fos.write('\n');
			fos.write('\n');
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
