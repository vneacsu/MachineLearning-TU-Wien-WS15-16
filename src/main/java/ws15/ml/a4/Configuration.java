package ws15.ml.a4;

import org.apache.commons.cli.*;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.neighboursearch.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Configuration {

    private Instances instances;
    private List<? extends NearestNeighbourSearch> searchAlgorithms;

    private Configuration() {
    }

    public Instances getInstances() {
        return instances;
    }

    public List<? extends NearestNeighbourSearch> getSearchAlgorithms() {
        return searchAlgorithms;
    }

    public static Configuration fromArgs(String[] args) {
        Configuration configuration = new Configuration();

        try {
            CommandLine commandLine = new DefaultParser().parse(getOptions(), args);

            configuration.instances = createInstancesFrom(commandLine);
            configuration.searchAlgorithms = createSearchAlgorithmsFrom(commandLine);
        } catch (ParseException e) {
            new HelpFormatter().printHelp("knn-evaluator", getOptions(), true);
            System.exit(1);
        }

        return configuration;
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("f", "file", true, "Data set file");
        options.addOption("a", "algorithms", true, "Nearest neighbor search algorithms to use (split by ',')");

        return options;
    }

    private static Instances createInstancesFrom(CommandLine commandLine) throws ParseException {
        if (!commandLine.hasOption("f")) {
            throw new ParseException("Data set file was not given");
        }

        try {
            DataSource dataSource = new DataSource(commandLine.getOptionValue("f"));

            Instances instances = dataSource.getDataSet();
            instances.setClassIndex(instances.numAttributes() - 1);

            return instances;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<? extends NearestNeighbourSearch> createSearchAlgorithmsFrom(CommandLine commandLine) {
        if (!commandLine.hasOption("a")) {
            return asList(new LinearNNSearch(), new BallTree(), new CoverTree(), new KDTree());
        }

        List<NearestNeighbourSearch> result = Arrays.asList(commandLine.getOptionValue("a").split(",")).stream()
                .map(Configuration::mapToSearchAlgorithm)
                .collect(Collectors.toList());
        result.add(new LinearNNSearch());

        return result;
    }

    private static NearestNeighbourSearch mapToSearchAlgorithm(String algorithmName) {
        algorithmName = algorithmName.trim();

        if (!isSupportedAlgorithm(algorithmName)) {
            throw new IllegalArgumentException("Invalid search algorithm " + algorithmName);
        }

        try {
            return (NearestNeighbourSearch) Class.forName("weka.core.neighboursearch." + algorithmName).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isSupportedAlgorithm(String algorithmName) {
        return asList(BallTree.class, CoverTree.class, KDTree.class).stream()
                .map(Class::getSimpleName)
                .filter(it -> it.equals(algorithmName))
                .findAny()
                .isPresent();
    }
}
