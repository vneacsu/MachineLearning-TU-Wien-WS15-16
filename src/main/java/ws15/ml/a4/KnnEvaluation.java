package ws15.ml.a4;

import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Debug;
import weka.core.Instances;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class KnnEvaluation implements Runnable {

    private final Instances instances;
    private final IBk knn;

    private Evaluation evaluation;
    private long evaluationDurationMs;

    public KnnEvaluation(Instances instances, IBk knn) {
        this.instances = instances;
        this.knn = knn;
    }

    @Override
    public void run() {
        try {
            evaluation = new Evaluation(instances);

            long startTime = System.nanoTime();

            evaluation.crossValidateModel(knn, instances, 10, new Debug.Random(1));

            evaluationDurationMs = MILLISECONDS.convert(System.nanoTime() - startTime, NANOSECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public long getEvaluationDurationMs() {
        return evaluationDurationMs;
    }
}
