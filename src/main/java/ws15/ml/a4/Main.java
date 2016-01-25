package ws15.ml.a4;

import ws15.ml.a4.plotters.*;

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
            knnEvaluator.registerknnEvaluationsConsumer(new KnnScoresPerDatasetPlotter(configuration));
            knnEvaluator.registerknnEvaluationsConsumer(new KnnClassClassificationDistributionPlotter(configuration));
            knnEvaluator.registerknnEvaluationsConsumer(new KnnDetailedMisclassificationTrendsPlotter(configuration));
            knnEvaluator.registerknnEvaluationsConsumer(new KnnAggregatedMisclassificationTrendsPlotter(configuration));
            knnEvaluator.registerknnEvaluationsConsumer(new KnnTotalBuildAndClassifyTimeByNumInstancesPlotter(configuration));
            knnEvaluator.registerknnEvaluationsConsumer(new KnnTotalBuildAndClassifyTimeByComplexityPlotter(configuration));
            knnEvaluator.registerknnEvaluationsConsumer(new KnnTotalBuildAndClassifyTimeByNumAttributesPlotter(configuration));
            knnEvaluator.registerknnEvaluationsConsumer(new KnnStackedTimesPerDatasetPlotter(configuration));

            knnEvaluator.evaluate();
        } finally {
            executor.shutdownNow();
        }
    }
}
