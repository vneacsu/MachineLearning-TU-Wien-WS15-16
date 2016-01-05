package ws15.ml.a4;

import org.apache.commons.cli.*;

public class Configuration {

    public static Configuration fromArgs(String[] args) {
        Configuration configuration = new Configuration();

        try {
            CommandLine commandLine = new DefaultParser().parse(getOptions(), args);

            configuration.dataSetFile = extractDataSetFileFrom(commandLine);
        } catch (ParseException e) {
            new HelpFormatter().printHelp("knn-evaluator", getOptions(), true);
            System.exit(1);
        }

        return configuration;
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("f", "file", true, "arff data set file");

        return options;
    }

    private static String extractDataSetFileFrom(CommandLine commandLine) throws ParseException {
        if (!commandLine.hasOption("f")) {
            throw new ParseException("Data set file was not given");
        }

        return commandLine.getOptionValue("f");
    }

    private String dataSetFile;

    private Configuration() {
    }

    public String getDataSetFile() {
        return dataSetFile;
    }
}
