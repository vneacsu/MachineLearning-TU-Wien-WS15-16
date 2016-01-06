package ws15.ml.a4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.neighboursearch.NearestNeighbourSearch;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.util.HashMap;
import java.util.Map;

public class KnnEvaluator {

    private static final Logger log = LoggerFactory.getLogger(KnnEvaluator.class);

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
        Map<Class, Evaluation> evaluationsMap = evaluateConfiguredSearchAlgorithms();

        printEvaluationStatistics(evaluationsMap);
    }

    private Map<Class, Evaluation> evaluateConfiguredSearchAlgorithms() {
        Map<Class, Evaluation> evaluationsMap = new HashMap<>();

        configuration.getSearchAlgorithms()
                .forEach(it -> evaluationsMap.put(it.getClass(), performKnnEvaluationWith(it)));
        return evaluationsMap;
    }

    private Evaluation performKnnEvaluationWith(NearestNeighbourSearch searchAlgorithm) {
        try {
            IBk knn = buildKnnClassifierWith(searchAlgorithm);

            return crossValidateKnnClassifier(knn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private IBk buildKnnClassifierWith(NearestNeighbourSearch searchAlgorithm) throws Exception {
        IBk knn = new IBk();
        knn.setNearestNeighbourSearchAlgorithm(searchAlgorithm);
        knn.buildClassifier(instances);
        return knn;
    }

    private Evaluation crossValidateKnnClassifier(IBk knn) throws Exception {
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(knn, instances, 10, new Debug.Random(1));

        return evaluation;
    }

    private void printEvaluationStatistics(Map<Class, Evaluation> evaluationsMap) {
        evaluationsMap.entrySet()
                .forEach(it -> log.info(it.getValue().toSummaryString(it.getKey().getSimpleName(), true)));
    }
}
