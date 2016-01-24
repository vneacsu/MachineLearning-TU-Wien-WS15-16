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

public class KnnScoresPerDatasetPlotter implements Consumer<List<KnnEvaluation>> {

    private final Configuration configuration;


    public KnnScoresPerDatasetPlotter(Configuration configuration) {

        this.configuration = configuration;
    }

    @Override
    public void accept(List<KnnEvaluation> evaluations) {

        Map<String, List<KnnEvaluation>> evaluationsMap = KnnEvaluation.groupByDataset(evaluations);

        evaluationsMap.entrySet().forEach(it -> plotScoresPerDataset(it.getKey(), it.getValue()));
    }

    private void plotScoresPerDataset(String datasetName, List<KnnEvaluation> evaluations) {
        Chart chart = new ChartBuilder()
                .chartType(StyleManager.ChartType.Bar)
                .width(CHART_WIDTH)
                .height(CHART_HEIGHT)
                .title("Accuracy Scores per Strategy")
                .xAxisTitle("Score")
                .yAxisTitle("% Success")
                .theme(StyleManager.ChartTheme.XChart)
                .build();

        evaluations.forEach(it -> chart.addSeries(
                it.getOptimizationStrategyId(),
                getXAxisLabels(it.getInstances()),
                getYAxisData(it.getAccuracy(),it.getMacroAccuracy()))
        );

        ChartUtils.plotChart(chart, getOutputFileName(datasetName));
    }

    private List<String> getXAxisLabels(Instances dataset) {
        List<String> xAxisLabels = new ArrayList<>();

        xAxisLabels.add("Accuracy");
        xAxisLabels.add("Macro Accuracy");

        return xAxisLabels;
    }

    private List<Double> getYAxisData(double Accuracy, double MacroAccuracy) {
        List<Double> data = new ArrayList<>();

        data.add(Accuracy);
        data.add(MacroAccuracy);

        return data;
    }

    private String getOutputFileName(String datasetName) {
        String fileName = String.format("%s_Scores-per-Strategy-chart.png", datasetName);

        return new File(configuration.getOutputDir(), fileName).getAbsolutePath();
    }
}
