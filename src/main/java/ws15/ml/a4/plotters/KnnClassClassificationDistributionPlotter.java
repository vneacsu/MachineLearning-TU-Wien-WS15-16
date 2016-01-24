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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


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

        String[] xAxisLabels = {"Actual Distribution", "<base-line> strategy (linear search)", "strategy.kdtree", "strategy.covertree", "strategy.balltree"};
        String xAxisTitle = "Search Strategy";
        String yAxisTitle = "Distribution of Classes";
        String title = "Class Classification Distribution";
        DataSeries dataSeries = new DataSeries(xAxisLabels, xAxisTitle, yAxisTitle, title);
        KnnEvaluation Eval_1 = evaluations.get(0);
        int Nclasses = Eval_1.getInstances().numClasses();
        String[] legendLabels = new String[Nclasses];
        double[][] data = new double[Nclasses][5];

        double [][] confusionM = Eval_1.getConfusionMatrix();
        for (int cla=0; cla < confusionM.length; ++cla){
            int acum = 0;
            legendLabels [cla]=(String) Eval_1.getInstances().classAttribute().value(cla);
            for (int col = 0; col < confusionM[0].length; ++col) {
                acum += confusionM[cla][col];
            }
            data[cla][0]=acum;
        }

        for (int i=0; i<4; ++i){
            KnnEvaluation Eval = evaluations.get(i);
            for (int j=0; j<Nclasses; ++j) {
                int k=0;
                if (Eval.getOptimizationStrategyId().equals("<base-line> strategy (linear search)")) {
                    k=1;
                }else if (Eval.getOptimizationStrategyId().equals("strategy.kdtree")) {
                    k=2;
                }else if (Eval.getOptimizationStrategyId().equals("strategy.covertree")) {
                    k=3;
                }else if (Eval.getOptimizationStrategyId().equals("strategy.balltree")) {
                    k=4;
                }
             confusionM = Eval.getConfusionMatrix();
             for (int cla=0; cla < confusionM[0].length; ++cla){
                 int acum = 0;
                 for (int row = 0; row < confusionM.length; ++row) {
                     acum += confusionM[row][cla];
                 }
                 data[cla][k]=acum;
             }

            }

        }

        Paint[] paints = TestDataGenerator.getRandomPaints(Nclasses);
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
                1980,
                1020);

        String fileName = String.format("%s_Class-Classification-Distribution-chart.png", datasetName);
        PNGEncoder.encode(axisChart, new FileOutputStream(new File(configuration.getOutputDir(), fileName)));


    }

}

