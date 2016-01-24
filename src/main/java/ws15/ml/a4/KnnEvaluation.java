package ws15.ml.a4;

import weka.classifiers.Evaluation;
import weka.core.Instances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ws15.ml.a4.InstancesLoader.getDatasetName;

public class KnnEvaluation {
    private final String optimizationStrategyId;
    private final String optimizationStrategyOptions;
    private final Instances instances;
    private static final Logger log = LoggerFactory.getLogger(KnnEvaluation.class);

    private final long buildTimeMs;
    private final long classificationTimeMs;
    private final Evaluation evaluation;

    public KnnEvaluation(String optimizationStrategyId, String optimizationStrategyOptions, Instances instances,
                         long buildTimeMs, long classificationTimeMs, Evaluation evaluation) {
        this.optimizationStrategyId = optimizationStrategyId;
        this.optimizationStrategyOptions = optimizationStrategyOptions;
        this.instances = instances;
        this.buildTimeMs = buildTimeMs;
        this.classificationTimeMs = classificationTimeMs;
        this.evaluation = evaluation;
    }

    public Object[] getResult() {
        return new Object[]{
                getDatasetName(instances),
                new Double(instances.numInstances()),
                new Double(instances.numAttributes()),
                new Double(instances.numClasses()),
                optimizationStrategyId,
                optimizationStrategyOptions,
                new Double(buildTimeMs),
                new Double(classificationTimeMs),
                evaluation.pctCorrect(),
                evaluation.weightedFMeasure(),
                evaluation.weightedFalsePositiveRate()
        };
    }

    public String getOptimizationStrategyId() {
        return optimizationStrategyId;
    }

    public double[][] getConfusionMatrix() {
        return evaluation.confusionMatrix();
    }

    public double getAccuracy() { return evaluation.pctCorrect(); }

    public double getMacroAccuracy() {

        double macroA = 0;
        int Nclasses = instances.numClasses();
        for (int i = 0; i < Nclasses; ++i) {
           macroA=macroA + evaluation.recall(i);
        }
        return macroA/Nclasses*100;
    }

    public long getBuildTimeMs() {
        return buildTimeMs;
    }

    public long getClassificationTimeMs() {
        return classificationTimeMs;
    }

    public Instances getInstances() {
        return instances;
    }

    public int getComplexity() {
        return (int) (instances.numInstances() * Math.log(instances.numAttributes() + 1));
    }

    public static Map<String, List<KnnEvaluation>> groupByDataset(List<KnnEvaluation> knnEvaluations) {
        return knnEvaluations.stream()
                .collect(Collectors.groupingBy(it -> getDatasetName(it.instances)));
    }

    public static Map<String, List<KnnEvaluation>> groupByStrategy(List<KnnEvaluation> knnEvaluations) {
        return knnEvaluations.stream()
                .collect(Collectors.groupingBy(it -> it.optimizationStrategyId));
    }

    public static String[] getResultNames() {
        return new String[]{
                "data_set_name",
                "data_set_n_instances",
                "data_set_n_attributes",
                "data_set_n_classes",
                "optimization_strategy_id",
                "optimization_strategy_options",
                "build_time_ms",
                "classification_time_ms",
                "accuracy",
                "avg_f_measure",
                "avg_false_positives_rate"
        };
    }

    public static Object[] getResultTypes() {
        return new Object[]{"", .0, .0, .0, "", "", .0, .0, .0, .0, .0};
    }
}
