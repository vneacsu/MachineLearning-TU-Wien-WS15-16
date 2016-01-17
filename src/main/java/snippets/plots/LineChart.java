package snippets.plots;

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

import java.awt.*;
import java.io.FileOutputStream;

import static org.krysalis.jcharts.properties.StockChartProperties.DEFAULT_STROKE;

public class LineChart {

    public static void main(String[] args) throws Exception {
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

        PNGEncoder.encode(axisChart, new FileOutputStream("line-chart.png"));
    }
}
