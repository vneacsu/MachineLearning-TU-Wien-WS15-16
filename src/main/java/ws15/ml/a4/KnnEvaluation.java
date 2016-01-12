package ws15.ml.a4;

import weka.classifiers.lazy.IBk;
import weka.core.Instances;

import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class KnnEvaluation implements Callable<KnnEvaluationResults> {

    private final IBk knn;
    private final Instances trainInstances;
    private final Instances testnstances;

    public KnnEvaluation(IBk knn, Instances trainInstances, Instances testInstances) {
        this.knn = knn;
        this.trainInstances = trainInstances;
        this.testnstances = testInstances;
    }

    @Override
    public KnnEvaluationResults call() {
        //TODO: extend with all results

        return new KnnEvaluationResults(measureBuildClassifierDurationMs());
    }

    private long measureBuildClassifierDurationMs() {
        long start = System.nanoTime();

        try {
            knn.buildClassifier(trainInstances);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build knn classifier", e);
        }

        return MILLISECONDS.convert(System.nanoTime() - start, NANOSECONDS);
    }
}
