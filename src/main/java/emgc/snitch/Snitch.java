package emgc.snitch;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Snitch {

	public static final Logger LOGGER = Logger.getLogger("emgc.snitch.Snitch");

	private final KnowledgeData knowledgeData;

	public Snitch() {
		knowledgeData = new KnowledgeData();
		LOGGER.setLevel(Level.FINEST);
	}

	public static void main(final String[] args) throws IOException, InterruptedException {
		int result = 0;
		final Snitch app = new Snitch();

		final Options options = app.createCmdOptions();
		CommandLine commandLine = null;
		try {
			commandLine = app.readParams(options, args);
		} catch (final ParseException e) {
			LOGGER.severe(e.getMessage());
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Snitch.class.getName(), options);
			result = 1;
		}
		if (commandLine != null) {
			final Report report = new Report(commandLine);
			app.run(commandLine, report);
			report.open();
		}

		System.exit(result);
	}

	private void run(final CommandLine commandLine, final Report report) throws IOException, InterruptedException {
		if (knowledgeData.load(commandLine)) {
			processInputFiles(commandLine, report);
			knowledgeData.saveData();
		}

	}

	private CommandLine readParams(final Options options, final String[] args) throws ParseException {
		final BasicParser parser = new BasicParser();
		return parser.parse(options, args);
	}

	private Options createCmdOptions() {
		final Options options = new Options();
		options.addOption("customProjects", "p", true, "ruta del directorio custom-projects");
		options.addOption("inputDir", "i", true,
				"ruta del directorio donde se encuentra las listas de clases y la base de datos");
		options.addOption("output", "o", true, "directorio donde se dejarán los resultados");
		return options;
	}

	private void processInputFiles(final CommandLine cmd, final Report report) {
		for (final FileNames fileName : FileNames.values()) {
			final InputFile inputFile = new InputFile(fileName);
			inputFile.load(cmd);
			knowledgeData.search(inputFile.getSourceFiles());
			inputFile.report(report.getFile(), knowledgeData);
		}
	}

}
