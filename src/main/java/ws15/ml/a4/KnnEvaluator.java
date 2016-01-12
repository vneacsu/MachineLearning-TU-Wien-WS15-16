package ws15.ml.a4;

import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class KnnEvaluator {

    private static final double SPLIT_PERCENTAGE = 0.66;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final Configuration configuration;

    private Instances trainInstances;
    private Instances testInstances;

    private final Map<String, Future<KnnEvaluationResults>> evaluationsMap = new HashMap<>();

    public KnnEvaluator(Configuration configuration) {
        this.configuration = configuration;
    }

    public void evaluate() {
        loadInstances();

        configuration.getStrategyOptions().forEach((strategyName, strategyOptions) -> {
            IBk knn = newKnnWith(strategyOptions);

            KnnEvaluation evaluation = new KnnEvaluation(knn, trainInstances, testInstances);

            evaluationsMap.put(strategyName, executor.submit(evaluation));
        });

        //TODO: extend to perform result comparisons, plots, etc.
        printEvaluationsStatistics();

        executor.shutdownNow();
    }

    private void loadInstances() {
        try {
            DataSource dataSource = new DataSource(configuration.getDataSetFilePath());

            Instances instances = dataSource.getDataSet();
            instances.setClassIndex(instances.numAttributes() - 1);

            instances = applyReplaceMissingValuesFilterTo(instances);

            splitTrainAndTestInstances(instances);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Instances applyReplaceMissingValuesFilterTo(Instances instances) throws Exception {
        Filter filter = new ReplaceMissingValues();
        filter.setInputFormat(instances);

        return Filter.useFilter(instances, filter);
    }

    private void splitTrainAndTestInstances(Instances instances) {
        int trainSize = (int) Math.round(instances.numInstances() * 0.66);
        int testSize = instances.numInstances() - trainSize;

        this.trainInstances = new Instances(instances, 0, trainSize);
        this.testInstances = new Instances(instances, trainSize, testSize);
    }

    private IBk newKnnWith(String strategyOptions) {
        IBk knn = new IBk(configuration.getK());

        try {
            knn.setOptions(new String[]{"-A", strategyOptions});
        } catch (Exception e) {
            throw new RuntimeException("Cannot set knn options", e);
        }

        return knn;
    }

    private void printEvaluationsStatistics() {
        evaluationsMap.forEach((evaluationTag, evaluationResults) -> {
//            System.out.println("/=============================/");
            System.out.println(evaluationTag + " results:\n\n");

            KnnEvaluationResults results;
            try {
                results = evaluationResults.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            System.out.println("\n\n\n/===================================/\n\n\n");
        });
    }
}
