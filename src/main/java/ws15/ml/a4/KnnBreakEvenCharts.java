package ws15.ml.a4;

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
import weka.core.Instances;
import weka.experiment.ResultListener;
import weka.experiment.ResultProducer;

import java.awt.*;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

import static org.krysalis.jcharts.properties.StockChartProperties.DEFAULT_STROKE;
import static org.krysalis.jcharts.properties.PointChartProperties.SHAPE_SQUARE;
import static org.krysalis.jcharts.properties.PointChartProperties.SHAPE_TRIANGLE;
import static org.krysalis.jcharts.properties.PointChartProperties.SHAPE_CIRCLE;
import static org.krysalis.jcharts.properties.PointChartProperties.SHAPE_DIAMOND;

public class KnnBreakEvenCharts implements ResultProducer, Consumer<List<KnnEvaluation>> {

    public static final Shape[] STANDARD_SHAPES = new Shape[]{SHAPE_SQUARE, SHAPE_TRIANGLE, SHAPE_CIRCLE, SHAPE_DIAMOND};

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

            // Fill arrays from evaluation results
            i = 0;
            for (KnnEvaluation evaluation : evaluations) {

                int datasetPos = i / numStrategies;
                int strategyPos = i % numStrategies;

                Object[] results = evaluation.getResult();

                if (datasetPos == 0) {
                    legendLabels[strategyPos] = (String) results[4];
                    strokes[strategyPos] = DEFAULT_STROKE;
                    shapes[strategyPos] = STANDARD_SHAPES[strategyPos % 4];
                }

                if (strategyPos == 0) {
                    xAxisLabels[datasetPos] = (String) results[0];
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

            DataSeries dataSeries = new DataSeries(xAxisLabels,
                                                    xAxisTitle,
                                                    yAxisTitle,
                                                    title);

            LineChartProperties lineChartProperties = new LineChartProperties(strokes, shapes);
            AxisChartDataSet axisChartDataSet = new AxisChartDataSet(data,
                                                                    legendLabels,
                                                                    paints,
                                                                    ChartType.LINE,
                                                                    lineChartProperties);
            dataSeries.addIAxisPlotDataSet(axisChartDataSet);

            ChartProperties chartProperties = new ChartProperties();
            AxisProperties axisProperties = new AxisProperties();
            LegendProperties legendProperties = new LegendProperties();
            AxisChart axisChart = new AxisChart(dataSeries,
                    chartProperties,
                    axisProperties,
                    legendProperties,
                    1200,
                    900);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace(":", "_");
            PNGEncoder.encode(axisChart, new FileOutputStream(String.format("charts/%s_BreakEvenDatasets.png", timestamp)));

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
