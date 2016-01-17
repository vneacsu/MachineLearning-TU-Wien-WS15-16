package snippets.plots;

import org.krysalis.jcharts.axisChart.AxisChart;
import org.krysalis.jcharts.chartData.AxisChartDataSet;
import org.krysalis.jcharts.chartData.DataSeries;
import org.krysalis.jcharts.encoders.PNGEncoder;
import org.krysalis.jcharts.properties.AxisProperties;
import org.krysalis.jcharts.properties.ChartProperties;
import org.krysalis.jcharts.properties.ClusteredBarChartProperties;
import org.krysalis.jcharts.properties.LegendProperties;
import org.krysalis.jcharts.test.TestDataGenerator;
import org.krysalis.jcharts.types.ChartType;

import java.awt.*;
import java.io.FileOutputStream;

public class ClusteredBarChart {

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
                {150, 15, 6, 62, -54, 10, 84},
                {250, 45, 36, 66, 145, 80, 55}};
        String[] legendLabels = {"Bugs", "Security Holes", "Backdoors"};
        Paint[] paints = TestDataGenerator.getRandomPaints(3);
        ClusteredBarChartProperties clusteredBarChartProperties =
                new ClusteredBarChartProperties();
        AxisChartDataSet axisChartDataSet = new AxisChartDataSet(data,
                legendLabels,
                paints,
                ChartType.BAR_CLUSTERED,
                clusteredBarChartProperties);
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

        PNGEncoder.encode(axisChart, new FileOutputStream("clustered-bar-plot.png"));
    }
}
