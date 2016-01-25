package ws15.ml.a4.plotters;

import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;

import java.io.IOException;

public class ChartUtils {

    public static final int CHART_WIDTH = 1200;
    public static final int CHART_HEIGHT = 900;

    public static void plotChart(Chart chart, String fileName) {
        try {
            BitmapEncoder.saveBitmap(chart, fileName, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
