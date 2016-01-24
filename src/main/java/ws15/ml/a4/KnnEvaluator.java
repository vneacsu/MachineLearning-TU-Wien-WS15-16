package ws15.ml.a4;

import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class KnnEvaluator {

    private final Configuration configuration;
    private final ExecutorService executor;

    private final List<Consumer<List<KnnEvaluation>>> knnEvaluationsConsumer = new ArrayList<>();

    public KnnEvaluator(Configuration configuration, ExecutorService executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    public void registerknnEvaluationsConsumer(Consumer<List<KnnEvaluation>> consumer) {
        knnEvaluationsConsumer.add(consumer);
    }

    public void evaluate() {
        List<Instances> instances = InstancesLoader.load(configuration.getDataSetPaths());

        List<KnnEvaluation> evaluations = evaluateAllInstances(instances);

        knnEvaluationsConsumer.forEach(it -> it.accept(evaluations));
    }

    private List<KnnEvaluation> evaluateAllInstances(List<Instances> instances) {
        List<Future<KnnEvaluation>> evaluations = instances.stream()
                .flatMap(this::evaluateAllStrategiesOn)
                .collect(Collectors.toList());

        return evaluations.stream()
                .map(this::waitEvaluationResult)
                .collect(Collectors.toList());
    }

    private Stream<? extends Future<KnnEvaluation>> evaluateAllStrategiesOn(Instances instances) {
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
