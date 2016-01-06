package ws15.ml.a4;

public class Main {

    public static void main(String[] args) throws Exception {
        Configuration configuration = Configuration.fromArgs(args);

        new KnnEvaluator(configuration).evaluate();
    }
}
