package ws15.ml.a4;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class KnnEvaluator {

    private static final double SPLIT_PERCENTAGE = 0.66;

    private final Configuration configuration;
    private final ExecutorService executor;
    private final KnnEvaluationsSaver evaluationsSaver;

    private Instances trainInstances;
    private Instances testInstances;

    public KnnEvaluator(Configuration configuration, ExecutorService executor, KnnEvaluationsSaver evaluationsSaver) {
        this.configuration = configuration;
        this.executor = executor;
        this.evaluationsSaver = evaluationsSaver;
    }

    public void evaluate() {
        loadInstances();

        List<KnnEvaluation> evaluations = performEvaluations();

        evaluationsSaver.persistKnnEvaluations(evaluations);
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
        instances.randomize(new Random(1));

        int trainSize = (int) Math.round(instances.numInstances() * SPLIT_PERCENTAGE);
        int testSize = instances.numInstances() - trainSize;

        this.trainInstances = new Instances(instances, 0, trainSize);
        this.testInstances = new Instances(instances, trainSize, testSize);
    }

    private List<KnnEvaluation> performEvaluations() {
        return configuration.getStrategyOptions().keySet().stream()
                .map(this::evaluateStrategy)
                .map(this::waitEvaluationResult)
                .collect(Collectors.toList());
    }

    private Future<KnnEvaluation> evaluateStrategy(String strategyId) {
        KnnEvaluationRunner evaluationRunner = new KnnEvaluationRunner(configuration,
                strategyId, trainInstances, testInstances);

        return executor.submit(evaluationRunner);
    }

    private KnnEvaluation waitEvaluationResult(Future<KnnEvaluation> knnEvaluationFuture) {
        try {
            return knnEvaluationFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
