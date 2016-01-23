package ws15.ml.a4;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Arrays.asList;

public class Configuration {

    private static final String DEFAULT_CONFIG_PROPERTIES = "default-config.properties";

    private static final Options COMMAND_LINE_OPTIONS = new Options();

    static {
        COMMAND_LINE_OPTIONS.addOption("f", "file", true, "Data set file");

        COMMAND_LINE_OPTIONS.addOption("c", "conf", true,
                "Configuration file for knn parameters  (otherwise using defaults)");
    }

    private final File outputDir;
    private final List<String> dataSetPaths;
    private final int k;
    private final Map<String, String> strategyOptions;

    private Configuration(List<String> dataSetPaths, int k, Map<String, String> strategyOptions) {
        this.outputDir = createOutputDir();
        this.dataSetPaths = dataSetPaths;
        this.k = k;
        this.strategyOptions = strategyOptions;
    }

    private File createOutputDir() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .replace(":", "_")
                .split("\\.")[0];

        File dir = new File(timestamp);
        if (!dir.mkdir()) {
            throw new RuntimeException("Cannot create output directory " + dir.toString());
        }

        return dir;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public List<String> getDataSetPaths() {
        return dataSetPaths;
    }

    public int getK() {
        return k;
    }

    public Map<String, String> getStrategyOptions() {
        return strategyOptions;
    }

    public static Configuration fromArgs(String[] args) {
        CommandLine commandLine = parseCommandLineArgs(args);

        return createConfigurationFrom(commandLine);
    }

    private static CommandLine parseCommandLineArgs(String[] args) {
        try {
            return new DefaultParser().parse(COMMAND_LINE_OPTIONS, args);
        } catch (ParseException e) {
            throw new RuntimeException("Usage: knn-extractor -f <data set file path> [ -c <knn config file path>]", e);
        }
    }

    private static Configuration createConfigurationFrom(CommandLine commandLine) {
        List<String> dataSetPaths = getDataSetPaths(commandLine);

        Properties properties = getConfigProperties(commandLine);

        int k = getKParamater(properties);

        Map<String, String> strategyOptions = getStrategyOptions(properties);

        return new Configuration(dataSetPaths, k, strategyOptions);
    }

    private static List<String> getDataSetPaths(CommandLine commandLine) {
        if (!commandLine.hasOption("f")) {
            throw new RuntimeException("Missing required data set file path!");
        }

        return asList(commandLine.getOptionValue("f").split(","));
    }

    private static Properties getConfigProperties(CommandLine commandLine) {
        InputStream inputStream;
        try {
            if (commandLine.hasOption("c")) {
                inputStream = new FileInputStream(commandLine.getOptionValue("c"));
            } else {
                inputStream = Configuration.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_PROPERTIES);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read knn config file!", e);
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config properties", e);
        } finally {
            closeSilently(inputStream);
        }

        return properties;
    }

    private static void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ignore) {
        }
    }

    private static int getKParamater(Properties properties) {
        return Integer.parseInt(properties.getProperty("knn.k"));
    }

    private static Map<String, String> getStrategyOptions(Properties properties) {
        Map<String, String> strategyOptions = new HashMap<>();
        properties.keySet().stream()
                .map(it -> (String) it)
                .filter(it -> it.startsWith("strategy"))
                .forEach(it -> strategyOptions.put(it, properties.getProperty(it)));

        addBaselineStrategy(strategyOptions);

        return strategyOptions;
    }

    private static void addBaselineStrategy(Map<String, String> strategyOptions) {
        strategyOptions.put("<base-line> strategy (linear search)",
                "weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"");
    }
}
