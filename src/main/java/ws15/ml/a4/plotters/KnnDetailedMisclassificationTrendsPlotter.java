package ws15.ml.a4.plotters;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager;
import weka.core.Instances;
import ws15.ml.a4.Configuration;
import ws15.ml.a4.KnnEvaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static ws15.ml.a4.plotters.ChartUtils.CHART_HEIGHT;
import static ws15.ml.a4.plotters.ChartUtils.CHART_WIDTH;

public class KnnDetailedMisclassificationTrendsPlotter implements Consumer<List<KnnEvaluation>> {

    private final Configuration configuration;

    public KnnDetailedMisclassificationTrendsPlotter(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void accept(List<KnnEvaluation> evaluations) {
        Map<String, List<KnnEvaluation>> evaluationsMap = KnnEvaluation.groupByDataset(evaluations);

        evaluationsMap.entrySet().forEach(it -> plotMisclassificationsTrend(it.getKey(), it.getValue()));
    }

    private void plotMisclassificationsTrend(String datasetName, List<KnnEvaluation> evaluations) {
        Instances instances = evaluations.get(0).getInstances();

        Chart chart = new ChartBuilder()
                .chartType(StyleManager.ChartType.Bar)
                .width(CHART_WIDTH)
                .height(CHART_HEIGHT)
                .title(String.format("Detailed Misclassification Trend Chart for data set %s(#i=%d, #a=%d, #c=%d)",
                        datasetName, instances.numInstances(), instances.numAttributes(), instances.numClasses()))
                .xAxisTitle("Classification")
                .yAxisTitle("#instances")
                .theme(StyleManager.ChartTheme.XChart)
                .build();

        evaluations.forEach(it -> chart.addSeries(
                it.getOptimizationStrategyId(),
                getXAxisLabels(it.getInstances()),
                getYAxisData(it.getConfusionMatrix()))
        );

        ChartUtils.plotChart(chart, getOutputFileName(datasetName));
    }

    private List<String> getXAxisLabels(Instances dataset) {
        List<String> xAxisLabels = new ArrayList<>();

        int numClasses = dataset.classAttribute().numValues();

        for (int i = 0; i < numClasses; ++i) {
            for (int j = 0; j < numClasses; ++j) {
                if (i == j) continue;

                xAxisLabels.add(
                        String.format("%s -> %s", dataset.classAttribute().value(i), dataset.classAttribute().value(j))
                );
            }
        }

        return xAxisLabels;
    }

    private List<Integer> getYAxisData(double[][] confusionMatrix) {
        List<Integer> data = new ArrayList<>();

        for (int i = 0; i < confusionMatrix.length; ++i) {
            for (int j = 0; j < confusionMatrix[0].length; ++j) {
                if (i != j) {
                    data.add((int) confusionMatrix[i][j]);
                }
            }
        }

        return data;
    }

    private String getOutputFileName(String datasetName) {
        String fileName = String.format("%s_detailed-misclassification-trends-chart.png", datasetName);

        return new File(configuration.getOutputDir(), fileName).getAbsolutePath();
    }
}
