package ws15.ml.a4;

import org.krysalis.jcharts.axisChart.AxisChart;
import org.krysalis.jcharts.axisChart.ScatterPlotAxisChart;
import org.krysalis.jcharts.chartData.AxisChartDataSet;
import org.krysalis.jcharts.chartData.DataSeries;
import org.krysalis.jcharts.chartData.ScatterPlotDataSeries;
import org.krysalis.jcharts.chartData.ScatterPlotDataSet;
import org.krysalis.jcharts.encoders.PNGEncoder;
import org.krysalis.jcharts.properties.*;
import org.krysalis.jcharts.test.TestDataGenerator;
import org.krysalis.jcharts.types.ChartType;
import weka.core.Instances;
import weka.experiment.ResultListener;
import weka.experiment.ResultProducer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static org.krysalis.jcharts.properties.PointChartProperties.*;

public class KnnBreakEvenCharts implements ResultProducer, Consumer<List<KnnEvaluation>> {

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

            // Set size of arrays
            String[] legendLabels = new String[numStrategies];
            Paint[] paints = TestDataGenerator.getRandomPaints(numStrategies);
            Stroke[] strokes = new Stroke[numStrategies];
            Shape[] shapes = new Shape[numStrategies];
            String[] xAxisLabels = new String[numDatasets];
            double[][] data = new double[numStrategies][numDatasets];
            double[] numInstances = new double[numDatasets];
            double[] numAttributes = new double[numDatasets];
            double[] numClasses = new double[numDatasets];

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
                    numInstances[datasetPos] = (Double) results[1];
                    numAttributes[datasetPos] = (Double) results[2];
                    numClasses[datasetPos] = (Double) results[3];
                }

                data[strategyPos][datasetPos] = (Double) results[6] + (Double) results[7];

                i++;
            }

//            String[] xAxisLabels = {"1998", "1999", "2000", "2001", "2002", "2003", "2004"};
//
//            double[][] data = new double[][]{{250, 45, -36, 66, 145, 80, 55},
//                    {45, -36, 66, 145, 80, 55, 120},
//                    {-36, 66, 145, 80, 55, 110, 140}};
//            String[] legendLabels = {"Bugs", "Security Holes", "Backdoors"};
//            Paint[] paints = TestDataGenerator.getRandomPaints(3);
//            Stroke[] strokes = new Stroke[]{DEFAULT_STROKE, DEFAULT_STROKE, DEFAULT_STROKE};
//            Shape[] shapes = new Shape[]{new Rectangle(15, 15), new Rectangle(8, 8), new Rectangle(7, 7)};

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

            // Prepare scatter plots for time by numInstances and complexity measure
            ScatterPlotProperties scatterPlotProperties = new ScatterPlotProperties(strokes, shapes);
            ScatterPlotDataSet scatterPlotDataSetInstances = new ScatterPlotDataSet(scatterPlotProperties);
            ScatterPlotDataSet scatterPlotDataSetComplexity = new ScatterPlotDataSet(scatterPlotProperties);

            for( int strategyPos = 0; strategyPos < numStrategies; strategyPos++ ) {
                ArrayList<Point2D.Double> pointsInstances = new ArrayList<>(numDatasets);
                ArrayList<Point2D.Double> pointsComplexity = new ArrayList<>(numDatasets);
                for( int datasetPos = 0; datasetPos < numDatasets; datasetPos++ ) {
                    pointsInstances.add(new Point2D.Double(numInstances[datasetPos], data[strategyPos][datasetPos]));
                    // TODO: improve complexity calculation
                    pointsComplexity.add(new Point2D.Double(
                            numInstances[datasetPos] * Math.sqrt(numAttributes[datasetPos]),
                            data[strategyPos][datasetPos]));
                }

                // Sort by x value
                pointsInstances.sort(Comparator.comparingDouble(Point2D.Double::getX));
                pointsComplexity.sort(Comparator.comparingDouble(Point2D.Double::getX));

                scatterPlotDataSetInstances.addDataPoints(pointsInstances.toArray(new Point2D.Double[numDatasets]), paints[strategyPos], legendLabels[strategyPos] );
                scatterPlotDataSetComplexity.addDataPoints(pointsComplexity.toArray(new Point2D.Double[numDatasets]), paints[strategyPos], legendLabels[strategyPos] );
            }

            ScatterPlotDataSeries scatterPlotDataSeriesInstances = new ScatterPlotDataSeries(
                    scatterPlotDataSetInstances,
                    "Number of instances",
                    yAxisTitle,
                    "Total time per strategy and number of instances");

            ScatterPlotDataSeries scatterPlotDataSeriesComplexity = new ScatterPlotDataSeries(
                    scatterPlotDataSetComplexity,
                    "Complexity",
                    yAxisTitle,
                    "Total time per strategy and complexity");

            ChartProperties chartProperties2 = new ChartProperties();
            AxisProperties axisProperties2 = new AxisProperties(new DataAxisProperties(), new DataAxisProperties());
            LegendProperties legendProperties2 = new LegendProperties();
            ScatterPlotAxisChart scatterPlotAxisChartInstances = new ScatterPlotAxisChart(
                    scatterPlotDataSeriesInstances,
                    chartProperties2,
                    axisProperties2,
                    legendProperties2,
                    1200,
                    900 );

            ScatterPlotAxisChart scatterPlotAxisChartComplexity = new ScatterPlotAxisChart(
                    scatterPlotDataSeriesComplexity,
                    chartProperties2,
                    axisProperties2,
                    legendProperties2,
                    1200,
                    900 );

            // Create and store chart files
            PNGEncoder.encode(axisChart, new FileOutputStream(new File(configuration.getOutputDir(), "BreakEvenDatasets.png")));
            PNGEncoder.encode(scatterPlotAxisChartInstances, new FileOutputStream(new File(configuration.getOutputDir(), "BreakEvenInstances.png.png")));
            PNGEncoder.encode(scatterPlotAxisChartComplexity, new FileOutputStream(new File(configuration.getOutputDir(), "BreakEvenComplexity.png")));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
