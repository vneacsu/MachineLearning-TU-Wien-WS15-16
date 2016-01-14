package ws15.ml.a4;

import weka.classifiers.Evaluation;

public class KnnEvaluation {
    private final String optimizationStrategyId;
    private final String optimizationStrategyOptions;

    private final long trainTimeMs;
    private final long testTimeMs;
    private final Evaluation evaluation;

    public KnnEvaluation(String optimizationStrategyId, String optimizationStrategyOptions,
                         long trainTimeMs, long testTimeMs, Evaluation evaluation) {
        this.optimizationStrategyId = optimizationStrategyId;
        this.optimizationStrategyOptions = optimizationStrategyOptions;
        this.trainTimeMs = trainTimeMs;
        this.testTimeMs = testTimeMs;
        this.evaluation = evaluation;
    }

    public Object[] getResult() {
        return new Object[]{
                optimizationStrategyId,
                optimizationStrategyOptions,
                new Double(trainTimeMs),
                new Double(testTimeMs),
                evaluation.pctCorrect(),
                evaluation.weightedFMeasure(),
                evaluation.weightedFalsePositiveRate()
        };
    }

    public static String[] getResultNames() {
        return new String[]{
                "optimization_strategy_id",
                "optimization_strategy_options",
                "train_time_ms",
                "test_time_ms",
                "accuracy",
                "avg_f_measure",
                "avg_false_positives_rate"
        };
    }

    public static Object[] getResultTypes() {
        return new Object[]{"", "", .0, .0, .0, .0, .0};
    }
}
