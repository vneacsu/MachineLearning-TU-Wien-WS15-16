package ws15.ml.a4;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class InstancesLoader {

    public static List<Instances> load(List<String> paths) {
        return paths.stream()
                .flatMap(InstancesLoader::loadFromPath)
                .collect(Collectors.toList());
    }

    public static String getDatasetName(Instances instances) {
        return instances.relationName().split("-")[0];
    }

    private static Stream<Instances> loadFromPath(String path) {
        File file = new File(path);

        if (file.isDirectory()) {
            return asList(file.list()).stream()
                    .map(it -> new File(file, it).getAbsolutePath())
                    .flatMap(InstancesLoader::loadFromPath);
        } else {
            return singletonList(loadFromFile(path)).stream();
        }
    }

    private static Instances loadFromFile(String path) {
        try {
            DataSource dataSource = new DataSource(path);

            Instances instances = dataSource.getDataSet();
            instances.setClassIndex(instances.numAttributes() - 1);

            return applyReplaceMissingValuesFilterTo(instances);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Instances applyReplaceMissingValuesFilterTo(Instances instances) throws Exception {
        Filter filter = new ReplaceMissingValues();
        filter.setInputFormat(instances);

        return Filter.useFilter(instances, filter);
    }
}
