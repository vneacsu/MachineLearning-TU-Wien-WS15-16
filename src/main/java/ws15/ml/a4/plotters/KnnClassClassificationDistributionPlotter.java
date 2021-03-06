package ws15.ml.a4.plotters;

import org.krysalis.jcharts.axisChart.AxisChart;
import org.krysalis.jcharts.chartData.AxisChartDataSet;
import org.krysalis.jcharts.chartData.ChartDataException;
import org.krysalis.jcharts.chartData.DataSeries;
import org.krysalis.jcharts.encoders.PNGEncoder;
import org.krysalis.jcharts.properties.*;
import org.krysalis.jcharts.test.TestDataGenerator;
import org.krysalis.jcharts.types.ChartType;
import weka.core.Instances;
import ws15.ml.a4.Configuration;
import ws15.ml.a4.KnnEvaluation;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ws15.ml.a4.plotters.ChartUtils.CHART_HEIGHT;
import static ws15.ml.a4.plotters.ChartUtils.CHART_WIDTH;

public class KnnClassClassificationDistributionPlotter implements Consumer<List<KnnEvaluation>> {

    private final Configuration configuration;

    public KnnClassClassificationDistributionPlotter(Configuration configuration) {

        this.configuration = configuration;
    }

    @Override
    public void accept(List<KnnEvaluation> evaluations) {

        Map<String, List<KnnEvaluation>> evaluationsMap = KnnEvaluation.groupByDataset(evaluations);

        evaluationsMap.entrySet().forEach(it -> {
            try {
                plotScoresPerDataset(it.getKey(), it.getValue());
            } catch (ChartDataException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        });
    }

    private void plotScoresPerDataset(String datasetName, List<KnnEvaluation> evaluations) throws ChartDataException, IOException, PropertyException {
        Instances instances = evaluations.get(0).getInstances();


        String[] xAxisLabels = getXAxisLabels(evaluations);
        String xAxisTitle = "Search Strategy";
        String yAxisTitle = "Distribution of Classes";
        String title = String.format("Class Classification Distribution for data set %s(#i=%d, #a=%d, #c=%d)",
                datasetName, instances.numInstances(), instances.numAttributes(), instances.numClasses());
        DataSeries dataSeries = new DataSeries(xAxisLabels, xAxisTitle, yAxisTitle, title);
        String[] legendLabels = getLegendLabels(instances);
        double[][] data = getChartData(evaluations);

        Paint[] paints = TestDataGenerator.getRandomPaints(instances.numClasses());
        StackedBarChartProperties stackedBarChartProperties = new StackedBarChartProperties();
        AxisChartDataSet axisChartDataSet = new AxisChartDataSet(
                data,
                legendLabels,
                paints,
                ChartType.BAR_STACKED,
                stackedBarChartProperties);
        dataSeries.addIAxisPlotDataSet(axisChartDataSet);
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

        String fileName = String.format("%s_Class-Classification-Distribution-chart.png", datasetName);
        PNGEncoder.encode(axisChart, new FileOutputStream(new File(configuration.getOutputDir(), fileName)));


    }

    private String[] getXAxisLabels(List<KnnEvaluation> evaluations) {

        List<String> xAxisLabels1 = new ArrayList<String>(Arrays.asList("Actual Distribution"));
        List<String> xAxisLabels2 = evaluations.stream()
                .map(KnnEvaluation::getOptimizationStrategyId)
                .collect(Collectors.toList());
        xAxisLabels1.addAll(xAxisLabels2);

        return xAxisLabels1.toArray(new String[xAxisLabels1.size()]);
    }

    private String[] getLegendLabels(Instances instances) {
        List<String> legendLabels = new ArrayList<>();

        for (int i = 0; i < instances.numClasses(); ++i) {
            legendLabels.add(instances.classAttribute().value(i));
        }

        return legendLabels.toArray(new String[legendLabels.size()]);
    }

    private double[][] getChartData(List<KnnEvaluation> evaluations) {
        double[][] chartData = new double[evaluations.get(0).getInstances().numClasses()][evaluations.size()+1];

        double [][] confusionM = evaluations.get(0).getConfusionMatrix();
        for (int cla=0; cla < confusionM.length; ++cla){
            int acum = 0;
            for (int col = 0; col < confusionM[0].length; ++col) {
                acum += confusionM[cla][col];
            }
            chartData[cla][0]=acum;
        }


        for (int i=0; i<(chartData[0].length-1); ++i){
            KnnEvaluation Eval = evaluations.get(i);
            confusionM = Eval.getConfusionMatrix();
            for (int cla=0; cla < confusionM[0].length; ++cla){
                int acum = 0;
                for (int row = 0; row < confusionM.length; ++row) {
                    acum += confusionM[row][cla];
                }
                chartData[cla][i+1]=acum;
            }
        }

        return chartData;
    }
}

