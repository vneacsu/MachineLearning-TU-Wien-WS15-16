package ws15.ml.a4;

import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.neighboursearch.NearestNeighbourSearch;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.util.HashMap;
import java.util.Map;

public class KnnEvaluator {

    private final Configuration configuration;

    private final Instances instances;

    public KnnEvaluator(Configuration configuration) {
        this.configuration = configuration;

        instances = applyReplaceMissingValuesFilterTo(configuration.getInstances());
    }

    private Instances applyReplaceMissingValuesFilterTo(Instances instances) {
        try {
            Filter filter = new ReplaceMissingValues();
            filter.setInputFormat(instances);

            return Filter.useFilter(instances, filter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void evaluate() {
        Map<Class, KnnEvaluation> evaluationsMap = evaluateConfiguredSearchAlgorithms();

        printEvaluationStatistics(evaluationsMap);
    }

    private Map<Class, KnnEvaluation> evaluateConfiguredSearchAlgorithms() {
        Map<Class, KnnEvaluation> evaluationsMap = new HashMap<>();

        configuration.getSearchAlgorithms()
                .forEach(it -> evaluationsMap.put(it.getClass(), performKnnEvaluationWith(it)));

        return evaluationsMap;
    }

    private KnnEvaluation performKnnEvaluationWith(NearestNeighbourSearch searchAlgorithm) {
            IBk knn = buildKnnClassifierWith(searchAlgorithm);

        KnnEvaluation evaluation = new KnnEvaluation(instances, knn);
        evaluation.run();

        return evaluation;
    }

    private IBk buildKnnClassifierWith(NearestNeighbourSearch searchAlgorithm) {
        IBk knn = new IBk();
        knn.setNearestNeighbourSearchAlgorithm(searchAlgorithm);

        try {
            knn.buildClassifier(instances);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return knn;
    }

    private void printEvaluationStatistics(Map<Class, KnnEvaluation> evaluationsMap) {
        evaluationsMap.entrySet()
                .forEach(it -> {
                    System.out.print(it.getValue().getEvaluation().toSummaryString(it.getKey().getSimpleName(), true));
                    System.out.println("Execution time: " + it.getValue().getEvaluationDurationMs() + " ms\n");
                });
    }
}
