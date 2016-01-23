package ws15.ml.a4;

import weka.core.Instances;
import weka.experiment.InstancesResultListener;
import weka.experiment.ResultListener;
import weka.experiment.ResultProducer;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class KnnEvaluationsSaver implements ResultProducer, Consumer<List<KnnEvaluation>> {

    private final Configuration configuration;

    public KnnEvaluationsSaver(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void accept(List<KnnEvaluation> evaluations) {
        persistKnnEvaluations(evaluations);
    }

    public void persistKnnEvaluations(List<KnnEvaluation> evaluations) {
        InstancesResultListener listener = new InstancesResultListener();
        listener.setOutputFile(getOutputFile());

        try {
            listener.preProcess(this);

            for (KnnEvaluation evaluation : evaluations) {
                listener.acceptResult(this, new Object[0], evaluation.getResult());
            }

            listener.postProcess(this);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File getOutputFile() {
        return new File(configuration.getOutputDir(), "results.arff");
    }

    @Override
    public String[] getResultNames() throws Exception {
        return KnnEvaluation.getResultNames();
    }


    @Override
    public Object[] getResultTypes() throws Exception {
        return KnnEvaluation.getResultTypes();
    }

    //Following methods are here just to satisfy the interface and are ignored.

    @Override
    public void setInstances(Instances instances) {
    }

    @Override
    public void setResultListener(ResultListener listener) {
    }

    @Override
    public void setAdditionalMeasures(String[] additionalMeasures) {
    }

    @Override
    public void preProcess() throws Exception {
    }

    @Override
    public void postProcess() throws Exception {
    }

    @Override
    public void doRun(int run) throws Exception {
    }

    @Override
    public void doRunKeys(int run) throws Exception {
    }

    @Override
    public String[] getKeyNames() throws Exception {
        return new String[0];
    }

    @Override
    public Object[] getKeyTypes() throws Exception {
        return new Object[0];
    }

    @Override
    public String getCompatibilityState() {
        return null;
    }
}
