package ws15.ml.a4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            Configuration configuration = Configuration.fromArgs(args);

            KnnEvaluator knnEvaluator = new KnnEvaluator(configuration, executor);

            knnEvaluator.registerknnEvaluationsConsumer(new KnnEvaluationsSaver(configuration));
            knnEvaluator.registerknnEvaluationsConsumer(new KnnBreakEvenCharts(configuration));

            knnEvaluator.evaluate();
        } finally {
            executor.shutdownNow();
        }
    }
}
