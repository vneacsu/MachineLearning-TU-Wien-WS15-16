package ws15.ml.a4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.Random;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Configuration configuration = Configuration.fromArgs(args);

        DataSource dataSource = new DataSource(configuration.getDataSetFile());

        Instances instances = dataSource.getDataSet();
        instances.setClassIndex(instances.numAttributes() - 1);

        IBk knn = new IBk(5);
        knn.buildClassifier(instances);

        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(knn, instances, 10, new Random(1));

        log.info(evaluation.toSummaryString());

    }
}
