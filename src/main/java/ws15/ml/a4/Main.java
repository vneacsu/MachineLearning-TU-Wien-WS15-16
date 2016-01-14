package ws15.ml.a4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        Configuration configuration = Configuration.fromArgs(args);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        KnnEvaluationsSaver saver = new KnnEvaluationsSaver(configuration);

        new KnnEvaluator(configuration, executor, saver).evaluate();

        executor.shutdown();
    }
}
