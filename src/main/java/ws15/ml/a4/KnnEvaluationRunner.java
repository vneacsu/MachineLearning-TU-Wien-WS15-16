package ws15.ml.a4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

import java.util.Random;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static ws15.ml.a4.InstancesLoader.getDatasetName;

public class KnnEvaluationRunner implements Callable<KnnEvaluation> {

    private static final Logger log = LoggerFactory.getLogger(KnnEvaluationRunner.class);

    private static final int NUM_FOLDS = 5; // number of folds for cross-validation

    private final Configuration configuration;
    private final String optimizationStrategyId;

    private final IBk knn;
    private final Instances instances;

    public KnnEvaluationRunner(Configuration configuration, String optimizationStrategyId, Instances instances) {
        this.configuration = configuration;
        this.optimizationStrategyId = optimizationStrategyId;

        this.knn = newKnnInstance();
        this.instances = new Instances(instances);
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
        log.info("Starting kNN evaluation for data set {} with strategy {}", getDatasetName(instances), optimizationStrategyId);

        long buildTimeMs = buildClassifierAndMeasureDurationMs(this.instances);

        Evaluation evaluation = crossValidateModel();

        long classificationTimeMs = measureClassificationTimeMs();

        log.info("Finished kNN evaluation for data set {} with strategy {}", getDatasetName(instances), optimizationStrategyId);

        return new KnnEvaluation(this.optimizationStrategyId, getOptimizationStrategyOptions(), this.instances,
                buildTimeMs, classificationTimeMs, evaluation);
    }

    private long buildClassifierAndMeasureDurationMs(Instances buildInstances) {
        long start = System.nanoTime();

        try {
            this.knn.buildClassifier(buildInstances);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build knn classifier", e);
        }

        return MILLISECONDS.convert(System.nanoTime() - start, NANOSECONDS);
    }

    private Evaluation crossValidateModel() {
        Evaluation evaluation;
        try {
            evaluation = new Evaluation(this.instances);

            evaluation.crossValidateModel(this.knn, this.instances, NUM_FOLDS, new Random(1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return evaluation;
    }

    private long measureClassificationTimeMs() {
        long start = System.nanoTime();

        try {
            Evaluation evaluation = new Evaluation(instances);

            evaluation.evaluateModel(knn, instances);
        } catch (Exception e) {
            throw new RuntimeException("Failed to classify data", e);
        }

        return MILLISECONDS.convert(System.nanoTime() - start, NANOSECONDS);
    }
}
