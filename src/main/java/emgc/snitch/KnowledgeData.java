package emgc.snitch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.apache.commons.cli.CommandLine;

public class KnowledgeData {

	Properties authorsInDatabase;
	Map<String, Result> sourceFilesInDatabase;
	CommandLine commandLine;
	// List<String> teams;

	public KnowledgeData() {
		authorsInDatabase = new Properties();
		sourceFilesInDatabase = new HashMap<>();
	}

	public void saveData() {
		final String inputDir = commandLine.getOptionValue("i");
		final Path path = FileSystems.getDefault().getPath(inputDir, "owners.properties");
		try (final FileOutputStream fos = new FileOutputStream(path.toFile())) {
			authorsInDatabase.store(fos, "Source code owners");
			fos.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public boolean load(final CommandLine commandLine) {
		this.commandLine = commandLine;
		final String inputDir = commandLine.getOptionValue("i");
		final Path path = FileSystems.getDefault().getPath(inputDir, "owners.properties");
		try (final FileInputStream fin = new FileInputStream(path.toFile())) {
			authorsInDatabase.load(fin);
		} catch (final FileNotFoundException e) {
			System.err.println("WARN: " + e.toString());
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
		/*
		 * path = FileSystems.getDefault().getPath(inputDir, "teams.txt"); try
		 * (Stream<String> stream = Files.lines(path)) { Snitch.LOGGER.log(Level.INFO,
		 * "Loading " + path.getFileName()); teams =
		 * stream.collect(Collectors.toList()); } catch (final IOException e) {
		 * Snitch.LOGGER.log(Level.SEVERE, e.toString(), e); }
		 */

		return true;
	}

	public Result getResult(final String sourceFile) {
		return sourceFilesInDatabase.get(sourceFile);
	}

	public void search(final List<String> sourceFiles) {
		final GitLog gitLog = new GitLog();
		for (final String sourceFile : sourceFiles) {
			List<String> authors;
			try {
				final String inputDir = commandLine.getOptionValue("p");
				final Path path = FileSystems.getDefault().getPath(inputDir);

				authors = gitLog.getAuthors(path.toFile(), sourceFile);
				authors = filter(authors);
				final Result result = new Result();
				result.calculate(authors);
				sourceFilesInDatabase.put(sourceFile, result);
			} catch (IOException | InterruptedException e) {
				Snitch.LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
	}

	private List<String> filter(final List<String> authors) {
		final List<String> result = new ArrayList<>();
		for (final String authorCaseSensitive : authors) {
			final String author = authorCaseSensitive.toUpperCase();
			String owner = authorsInDatabase.getProperty(author);
			if (owner == null) {
				owner = JOptionPane.showInputDialog("El programador '" + author
						+ "' es desconocido ¿sabes quien es? ('skip' para que este programador no se tenga en cuenta)");

				if (owner == null || "".equals(owner)) {
					owner = author;
				}
				authorsInDatabase.setProperty(author, owner);
			}
			if (!"skip".equals(owner)) {
				result.add(owner);
			}
		}
		saveData();
		return result;
	}
}
