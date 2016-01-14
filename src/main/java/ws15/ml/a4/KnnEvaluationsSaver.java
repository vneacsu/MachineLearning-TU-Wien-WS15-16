package ws15.ml.a4;

import weka.core.Instances;
import weka.experiment.InstancesResultListener;
import weka.experiment.ResultListener;
import weka.experiment.ResultProducer;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KnnEvaluationsSaver implements ResultProducer {

    private final Configuration configuration;

    public KnnEvaluationsSaver(Configuration configuration) {
        this.configuration = configuration;
    }

    public void persistKnnEvaluations(List<KnnEvaluation> evaluations) {
        InstancesResultListener listener = new InstancesResultListener();
        listener.setOutputFile(inferOutputFile());

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

    private File inferOutputFile() {
        String inputDataSetFileName = new File(configuration.getDataSetFilePath()).getName().replace(".", "-");
        String timestamp = "noTime";//LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String resultsFileName = String.format("%s-%s.arff", timestamp, inputDataSetFileName);

        return new File(resultsFileName);
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
