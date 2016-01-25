package ws15.ml.a4.plotters;

import org.krysalis.jcharts.axisChart.AxisChart;
import org.krysalis.jcharts.chartData.AxisChartDataSet;
import org.krysalis.jcharts.chartData.ChartDataException;
import org.krysalis.jcharts.chartData.DataSeries;
import org.krysalis.jcharts.encoders.PNGEncoder;
import org.krysalis.jcharts.properties.*;
import org.krysalis.jcharts.types.ChartType;
import ws15.ml.a4.Configuration;
import ws15.ml.a4.KnnEvaluation;


import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.awt.Color.*;
import static ws15.ml.a4.plotters.ChartUtils.CHART_HEIGHT;
import static ws15.ml.a4.plotters.ChartUtils.CHART_WIDTH;

public class KnnStackedTimesPerDatasetPlotter implements Consumer<List<KnnEvaluation>> {

    private final Configuration configuration;

    public KnnStackedTimesPerDatasetPlotter(Configuration configuration) {

        this.configuration = configuration;
    }

    @Override
    public void accept(List<KnnEvaluation> evaluations) {

        Map<String, List<KnnEvaluation>> evaluationsMap = KnnEvaluation.groupByDataset(evaluations);

        evaluationsMap.entrySet().forEach(it -> {
            try {
                plotTimesPerDataset(it.getKey(), it.getValue());
            } catch (ChartDataException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        });
    }

    private void plotTimesPerDataset(String datasetName, List<KnnEvaluation> evaluations) throws ChartDataException, IOException, PropertyException {

        int numStrategies = evaluations.size();
        double[][] data = new double[2][numStrategies];
        String[] xAxisLabels = new String[numStrategies];

        int i = 0;
        for (KnnEvaluation evaluation : evaluations) {

            xAxisLabels[i] = evaluation.getOptimizationStrategyId();
            data[0][i] = evaluation.getBuildTimeMs();
            data[1][i] = evaluation.getClassificationTimeMs();

            i++;
        }

        String xAxisTitle = "Search Strategy";
        String yAxisTitle = "Build and classification times (in milliseconds)";
        String title = datasetName + " - Build and classification times per strategy";
        DataSeries dataSeries = new DataSeries(xAxisLabels, xAxisTitle, yAxisTitle, title);

        String[] legendLabels = {"Build time", "Classification time"};
        Paint[] paints = new Paint[]{BLUE, ORANGE};
        StackedBarChartProperties stackedBarChartProperties = new StackedBarChartProperties();
        AxisChartDataSet axisChartDataSet = new AxisChartDataSet(
                data,
                legendLabels,
                paints,
                ChartType.BAR_STACKED,
                stackedBarChartProperties);
        dataSeries.addIAxisPlotDataSet(axisChartDataSet);
        ChartProperties chartProperties = new ChartProperties();
        AxisProperties axisProperties = new AxisProperties();
        LegendProperties legendProperties = new LegendProperties();
        AxisChart axisChart = new AxisChart(dataSeries,
                chartProperties,
                axisProperties,
                legendProperties,
                CHART_WIDTH,
                CHART_HEIGHT);

        String fileName = String.format("%s_StackedBuildAndClassificationTimes.png", datasetName);
        PNGEncoder.encode(axisChart, new FileOutputStream(new File(configuration.getOutputDir(), fileName)));

    }
}
