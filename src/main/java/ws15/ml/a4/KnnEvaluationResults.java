package ws15.ml.a4;

public class KnnEvaluationResults {
    //TODO: extend with all results

    private final long buildClassifierDurationMs;

    public KnnEvaluationResults(long buildClassifierDurationMs) {
        this.buildClassifierDurationMs = buildClassifierDurationMs;
    }

    public long getBuildClassifierDurationMs() {
        return buildClassifierDurationMs;
    }
}
