package ws15.ml.a4.plotters;

import org.krysalis.jcharts.axisChart.AxisChart;
import org.krysalis.jcharts.chartData.AxisChartDataSet;
import org.krysalis.jcharts.chartData.DataSeries;
import org.krysalis.jcharts.encoders.PNGEncoder;
import org.krysalis.jcharts.properties.AxisProperties;
import org.krysalis.jcharts.properties.ChartProperties;
import org.krysalis.jcharts.properties.LegendProperties;
import org.krysalis.jcharts.properties.LineChartProperties;
import org.krysalis.jcharts.test.TestDataGenerator;
import org.krysalis.jcharts.types.ChartType;
import ws15.ml.a4.Configuration;
import ws15.ml.a4.KnnEvaluation;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.function.Consumer;

import static org.krysalis.jcharts.properties.PointChartProperties.*;

public class KnnBreakEvenCharts implements Consumer<List<KnnEvaluation>> {

    public static final Shape[] STANDARD_SHAPES = new Shape[]{SHAPE_SQUARE, SHAPE_TRIANGLE, SHAPE_CIRCLE, SHAPE_DIAMOND};

    private final Configuration configuration;

    public KnnBreakEvenCharts(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void accept(List<KnnEvaluation> evaluations) {

        plotTimesPerStrategyAndDataset(evaluations);
    }

    public void plotTimesPerStrategyAndDataset(List<KnnEvaluation> evaluations) {

        try {

            // Set general chart descriptions
            String xAxisTitle = "Datasets";
            String yAxisTitle = "Build + classification time in ms";
            String title = "Total time per strategy and dataset";

            // Abort if no evaluations
            if (evaluations.size() == 0) return;

            // Retrieve number of strategies and datasets
            int i = 0;
            String prev = "";
            for (KnnEvaluation evaluation : evaluations) {

                Object[] results = evaluation.getResult();
                String actual = (String) results[0];
                if ((i > 0) && !(actual.equals(prev))) break;
                prev = actual;
                i++;
            }
            int numStrategies = i;
            int numDatasets = evaluations.size() / numStrategies;

            // Abort if less than 2 datasets
            if (numDatasets < 2) return;

            // Set size of arrays
            String[] legendLabels = new String[numStrategies];
            Paint[] paints = TestDataGenerator.getRandomPaints(numStrategies);
            Stroke[] strokes = new Stroke[numStrategies];
            Shape[] shapes = new Shape[numStrategies];
            String[] xAxisLabels = new String[numDatasets];
            double[][] data = new double[numStrategies][numDatasets];

            // Fill arrays from evaluation results
            i = 0;
            for (KnnEvaluation evaluation : evaluations) {

                int datasetPos = i / numStrategies;
                int strategyPos = i % numStrategies;

                Object[] results = evaluation.getResult();

                if (datasetPos == 0) {
                    legendLabels[strategyPos] = (String) results[4];
                    strokes[strategyPos] = new BasicStroke(2f);
                    shapes[strategyPos] = STANDARD_SHAPES[strategyPos % 4];
                }

                if (strategyPos == 0) {
                    xAxisLabels[datasetPos] = (String) results[0];
                }

                data[strategyPos][datasetPos] = (Double) results[6] + (Double) results[7];

                i++;
            }

            // Prepare line chart for times by dataset
            DataSeries dataSeries = new DataSeries(
                    xAxisLabels,
                    xAxisTitle,
                    yAxisTitle,
                    title);

            LineChartProperties lineChartProperties = new LineChartProperties(strokes, shapes);
            AxisChartDataSet axisChartDataSet = new AxisChartDataSet(
                    data,
                    legendLabels,
                    paints,
                    ChartType.LINE,
                    lineChartProperties);
            dataSeries.addIAxisPlotDataSet(axisChartDataSet);

            ChartProperties chartProperties = new ChartProperties();
            AxisProperties axisProperties = new AxisProperties();
            LegendProperties legendProperties = new LegendProperties();
            AxisChart axisChart = new AxisChart(
                    dataSeries,
                    chartProperties,
                    axisProperties,
                    legendProperties,
                    1200,
                    900);

            // Create and store chart files
            PNGEncoder.encode(axisChart, new FileOutputStream(new File(configuration.getOutputDir(), "BreakEvenDatasets.png")));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
