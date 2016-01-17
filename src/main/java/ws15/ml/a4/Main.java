package ws15.ml.a4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            Configuration configuration = Configuration.fromArgs(args);

            KnnEvaluationsSaver saver = new KnnEvaluationsSaver(configuration);

            new KnnEvaluator(configuration, executor, saver).evaluate();
        } finally {
            executor.shutdownNow();
        }
    }
}
