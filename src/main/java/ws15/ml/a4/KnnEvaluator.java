package ws15.ml.a4;

import weka.core.Instances;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class KnnEvaluator {

    private final Configuration configuration;
    private final ExecutorService executor;
    private final KnnEvaluationsSaver evaluationsSaver;

    public KnnEvaluator(Configuration configuration, ExecutorService executor, KnnEvaluationsSaver evaluationsSaver) {
        this.configuration = configuration;
        this.executor = executor;
        this.evaluationsSaver = evaluationsSaver;
    }

    public void evaluate() {
        List<Instances> instances = InstancesLoader.load(configuration.getDataSetPaths());

        List<KnnEvaluation> evaluations = evaluateAllInstances(instances);

        evaluationsSaver.persistKnnEvaluations(evaluations);
    }

    private List<KnnEvaluation> evaluateAllInstances(List<Instances> instances) {
        return instances.stream()
                .flatMap(this::evaluateAllStrategiesOn)
                .map(this::waitEvaluationResult)
                .collect(Collectors.toList());
    }

    private Stream<Future<KnnEvaluation>> evaluateAllStrategiesOn(Instances instances) {
        return configuration.getStrategyOptions().keySet().stream()
                .map(it -> evaluateStrategy(it, instances));
    }

    private Future<KnnEvaluation> evaluateStrategy(String strategyId, Instances instances) {
        KnnEvaluationRunner evaluationRunner = new KnnEvaluationRunner(configuration, strategyId, instances);

        return executor.submit(evaluationRunner);
    }

    private KnnEvaluation waitEvaluationResult(Future<KnnEvaluation> knnEvaluationFuture) {
        try {
            return knnEvaluationFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
