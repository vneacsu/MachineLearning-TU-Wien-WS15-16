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
import java.util.List;
import java.util.function.Consumer;

import static org.krysalis.jcharts.properties.StockChartProperties.DEFAULT_STROKE;

public class KnnBreakEvenCharts implements ResultProducer, Consumer<List<KnnEvaluation>> {

    @Override
    public void accept(List<KnnEvaluation> evaluations) {

        plotTimesPerStrategyAndInstances(evaluations);
    }

    public void plotTimesPerStrategyAndInstances(List<KnnEvaluation> evaluations) {

        try {

            for (KnnEvaluation evaluation : evaluations) {

                evaluation.getResult();
            }

            String[] xAxisLabels = {"1998", "1999", "2000", "2001", "2002", "2003", "2004"};
            String xAxisTitle = "Years";
            String yAxisTitle = "Problems";
            String title = "Micro$oft at Work";
            DataSeries dataSeries = new DataSeries(xAxisLabels,
                    xAxisTitle,
                    yAxisTitle,
                    title);

            double[][] data = new double[][]{{250, 45, -36, 66, 145, 80, 55},
                    {250, 45, -36, 66, 145, 80, 55},
                    {250, 45, -36, 66, 145, 80, 55}};
            String[] legendLabels = {"Bugs", "Security Holes", "Backdoors"};
            Paint[] paints = TestDataGenerator.getRandomPaints(3);
            Stroke[] strokes = new Stroke[]{DEFAULT_STROKE, DEFAULT_STROKE, DEFAULT_STROKE};
            Shape[] shapes = new Shape[]{new Rectangle(15, 15), new Rectangle(8, 8), new Rectangle(7, 7)};
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
                    1980,
                    1020);

            PNGEncoder.encode(axisChart, new FileOutputStream("charts/line-chart.png"));

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
