package ws15.ml.a4;

import org.krysalis.jcharts.axisChart.AxisChart;
import org.krysalis.jcharts.chartData.ChartDataException;
import org.krysalis.jcharts.chartData.DataSeries;
import org.krysalis.jcharts.encoders.PNGEncoder;
import org.krysalis.jcharts.properties.AxisProperties;
import org.krysalis.jcharts.properties.ChartProperties;
import org.krysalis.jcharts.properties.LegendProperties;
import org.krysalis.jcharts.properties.PropertyException;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class ChartUtils {

    private static final int CHART_WIDTH = 1980;
    private static final int CHART_HEIGHT = 1020;

    private static final Random random = new Random(1);

    public static Color[] getRandomColors(int size) {
        Color[] colors = new Color[size];

        for (int i = 0; i < colors.length; ++i) {
            colors[i] = nextRandomColor();
        }

        return colors;
    }

    private static Color nextRandomColor() {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static void plotAxisChart(DataSeries dataSeries, File outputFile) {
        AxisChart axisChart = new AxisChart(dataSeries, new ChartProperties(),
                new AxisProperties(), new LegendProperties(), CHART_WIDTH, CHART_HEIGHT);

        try (OutputStream stream = new FileOutputStream(outputFile)) {
            PNGEncoder.encode(axisChart, stream);
        } catch (IOException | ChartDataException | PropertyException e) {
            throw new RuntimeException("Failed to plot axis chart", e);
        }
    }
}
