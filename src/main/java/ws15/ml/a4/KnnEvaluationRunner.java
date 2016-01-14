package ws15.ml.a4;

import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class KnnEvaluationRunner implements Callable<KnnEvaluation> {

    private final Configuration configuration;
    private final String optimizationStrategyId;

    private final IBk knn;
    private final Instances trainInstances;
    private final Instances testInstances;

    public KnnEvaluationRunner(Configuration configuration, String optimizationStrategyId,
                               Instances trainInstances, Instances testInstances) {
        this.configuration = configuration;
        this.optimizationStrategyId = optimizationStrategyId;

        this.knn = newKnnInstance();
        this.trainInstances = trainInstances;
        this.testInstances = testInstances;
    }

    private IBk newKnnInstance() {
        IBk knn = new IBk(configuration.getK());

        try {
            knn.setOptions(new String[]{"-A", getOptimizationStrategyOptions()});
        } catch (Exception e) {
            throw new RuntimeException("Cannot set knn options", e);
        }

        return knn;
    }

    private String getOptimizationStrategyOptions() {
        return configuration.getStrategyOptions().get(optimizationStrategyId);
    }

    @Override
    public KnnEvaluation call() {
        long buildClassifierDurationMs = buildClassifierAndMeasureDurationMs();

        long start = System.nanoTime();

        Evaluation evaluation;
        try {
            evaluation = new Evaluation(trainInstances);

            evaluation.evaluateModel(knn, testInstances);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long modelEvaluationDurationMs = MILLISECONDS.convert(System.nanoTime() - start, NANOSECONDS);

        return new KnnEvaluation(optimizationStrategyId, getOptimizationStrategyOptions(),
                buildClassifierDurationMs, modelEvaluationDurationMs, evaluation);
    }

    private long buildClassifierAndMeasureDurationMs() {
        long start = System.nanoTime();

        try {
            knn.buildClassifier(trainInstances);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build knn classifier", e);
        }

        return MILLISECONDS.convert(System.nanoTime() - start, NANOSECONDS);
    }
}
