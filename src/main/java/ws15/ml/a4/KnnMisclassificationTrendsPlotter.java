package ws15.ml.a4;

import org.krysalis.jcharts.chartData.AxisChartDataSet;
import org.krysalis.jcharts.chartData.ChartDataException;
import org.krysalis.jcharts.chartData.DataSeries;
import org.krysalis.jcharts.properties.BarChartProperties;
import org.krysalis.jcharts.properties.ClusteredBarChartProperties;
import weka.core.Instances;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.krysalis.jcharts.types.ChartType.BAR_CLUSTERED;

public class KnnMisclassificationTrendsPlotter implements Consumer<List<KnnEvaluation>> {

    private final Configuration configuration;

    public KnnMisclassificationTrendsPlotter(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void accept(List<KnnEvaluation> evaluations) {
        Map<String, List<KnnEvaluation>> evaluationsMap = KnnEvaluation.groupByDataset(evaluations);

        evaluationsMap.entrySet().forEach(it -> plotMisclassificationsTrend(it.getKey(), it.getValue()));
    }

    private void plotMisclassificationsTrend(String datasetName, List<KnnEvaluation> evaluations) {
        DataSeries dataSeries = createDataSeries(evaluations);

        AxisChartDataSet axisChartDataSet = getAxisChartDataSet(evaluations);

        dataSeries.addIAxisPlotDataSet(axisChartDataSet);

        ChartUtils.plotAxisChart(dataSeries, getOutputFile(datasetName));
    }

    private DataSeries createDataSeries(List<KnnEvaluation> evaluations) {
        String[] xAxisLabels = getXAxisLabels(evaluations.get(0).getInstances());

        String xAxisTitle = "Classification";

        String yAxisTitle = "#instances";

        String title = "Misclassification Trend Chart";

        return new DataSeries(xAxisLabels, xAxisTitle, yAxisTitle, title);
    }

    private String[] getXAxisLabels(Instances dataset) {
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

        return xAxisLabels.toArray(new String[xAxisLabels.size()]);
    }

    private AxisChartDataSet getAxisChartDataSet(List<KnnEvaluation> evaluations) {
        double[][] data = getChartDataFrom(evaluations);

        String[] legendLabels = getLegendLabelsFrom(evaluations);

        Paint[] paints = ChartUtils.getRandomColors(evaluations.size());

        BarChartProperties barChartProperties = new ClusteredBarChartProperties();

        try {
            return new AxisChartDataSet(data, legendLabels, paints, BAR_CLUSTERED, barChartProperties);
        } catch (ChartDataException e) {
            throw new RuntimeException(e);
        }
    }

    private double[][] getChartDataFrom(List<KnnEvaluation> evaluations) {
        List<Double[]> chartData = evaluations.stream()
                .map(KnnEvaluation::getConfusionMatrix)
                .map(this::mapConfusionMatrixToMisclassificationTrendsData)
                .collect(Collectors.toList());

        return mapChartDataToPrimitiveDoubles(chartData.toArray(new Double[chartData.size()][]));
    }

    private double[][] mapChartDataToPrimitiveDoubles(Double[][] chartData) {
        double[][] result = new double[chartData.length][chartData[0].length];

        for (int i = 0; i < result.length; ++i) {
            for (int j = 0; j < result[0].length; ++j) {
                result[i][j] = chartData[i][j];
            }
        }

        return result;
    }

    private Double[] mapConfusionMatrixToMisclassificationTrendsData(double[][] confusionMatrix) {
        List<Double> data = new ArrayList<>();

        for (int i = 0; i < confusionMatrix.length; ++i) {
            for (int j = 0; j < confusionMatrix[0].length; ++j) {
                if (i != j) {
                    data.add(confusionMatrix[i][j]);
                }
            }
        }

        return data.toArray(new Double[data.size()]);
    }

    private String[] getLegendLabelsFrom(List<KnnEvaluation> evaluations) {
        List<String> legendLabels = evaluations.stream()
                .map(KnnEvaluation::getOptimizationStrategyId)
                .collect(Collectors.toList());

        return legendLabels.toArray(new String[legendLabels.size()]);
    }

    private File getOutputFile(String datasetName) {
        String fileName = String.format("%s_misclassification-trends-chart.png", datasetName);

        return new File(configuration.getOutputDir(), fileName);
    }
}
