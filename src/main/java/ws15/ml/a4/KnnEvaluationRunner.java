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

    // not used anymore
    //private Instances trainInstances;
    //private Instances testInstances;


    public KnnEvaluationRunner(Configuration configuration, String optimizationStrategyId, Instances instances) {
        this.configuration = configuration;
        this.optimizationStrategyId = optimizationStrategyId;

        this.knn = newKnnInstance();
        this.instances = new Instances(instances);

        // replaced by cross-validation
        //splitTrainAndTestInstances();
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

    // replaced by cross-validation
//    private void splitTrainAndTestInstances() {
//        //TODO: will use cross validation, therefore it is subject to remove in the future.
//        instances.randomize(new Random(1));
//
//        int trainSize = (int) Math.round(instances.numInstances() * 0.66);
//        int testSize = instances.numInstances() - trainSize;
//
//        this.trainInstances = new Instances(instances, 0, trainSize);
//        this.testInstances = new Instances(instances, trainSize, testSize);
//    }

    @Override
    public KnnEvaluation call() {
        log.info("Starting kNN evaluation for data set {} with strategy {}", getDatasetName(instances), optimizationStrategyId);

        // Find out build time for set of instances without one fold (e.g. 80% of instances for 5-fold cross-validation)
        // TODO: Review calculation
        this.instances.randomize(new Random(1)); // to avoid unbalanced dataset before making subset
        Instances instancesWithoutOneFold = new Instances(this.instances, 0, Math.round(instances.numInstances() * (NUM_FOLDS - 1) / NUM_FOLDS ));
        long buildWithoutOneFoldDurationMs = buildClassifierAndMeasureDurationMs(instancesWithoutOneFold);

        // Classifier is built from all instances of the data set
        long buildClassifierDurationMs = buildClassifierAndMeasureDurationMs(this.instances);

        long start = System.nanoTime();
        Evaluation evaluation;
        try {
            evaluation = new Evaluation(this.instances);

            // Evaluation is done by cross-validation with all instances
            evaluation.crossValidateModel(this.knn, this.instances, NUM_FOLDS, new Random(1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long modelEvaluationDurationMs = MILLISECONDS.convert(System.nanoTime() - start, NANOSECONDS);

        // Calculate approximate classification time for set of all instances
        // TODO: Review calculation
        long classificationDurationMs = Math.max(modelEvaluationDurationMs - (NUM_FOLDS * buildWithoutOneFoldDurationMs), 0); // make sure duration is not negative

        log.info("Finished kNN evaluation for data set {} with strategy {}", getDatasetName(instances), optimizationStrategyId);

        return new KnnEvaluation(this.optimizationStrategyId, getOptimizationStrategyOptions(), this.instances,
                buildClassifierDurationMs, classificationDurationMs, evaluation);
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
}
