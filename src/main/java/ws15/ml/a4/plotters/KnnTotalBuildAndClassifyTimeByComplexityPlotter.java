package ws15.ml.a4.plotters;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager;
import ws15.ml.a4.Configuration;
import ws15.ml.a4.KnnEvaluation;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ws15.ml.a4.plotters.ChartUtils.CHART_HEIGHT;
import static ws15.ml.a4.plotters.ChartUtils.CHART_WIDTH;

public class KnnTotalBuildAndClassifyTimeByComplexityPlotter implements Consumer<List<KnnEvaluation>> {

    private static final Comparator<KnnEvaluation> evaluationsComparator =
            (o1, o2) -> o1.getComplexity() - o2.getComplexity();

    private final Configuration configuration;

    public KnnTotalBuildAndClassifyTimeByComplexityPlotter(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void accept(List<KnnEvaluation> evaluations) {
        Chart chart = new ChartBuilder()
                .chartType(StyleManager.ChartType.Line)
                .width(CHART_WIDTH)
                .height(CHART_HEIGHT)
                .title("Total build + classify time by complexity( #instances * log(#attributes) ) per strategy")
                .xAxisTitle("Complexity")
                .yAxisTitle("build + classification time in ms")
                .theme(StyleManager.ChartTheme.XChart)
                .build();

        KnnEvaluation.groupByStrategy(evaluations).entrySet().forEach(it -> chart.addSeries(
                it.getKey(),
                getXAxisLabels(it.getValue()),
                getYAxisData(it.getValue()))
        );

        ChartUtils.plotChart(chart, getOutputFileName());
    }

    private List<Integer> getXAxisLabels(List<KnnEvaluation> evaluations) {
        return evaluations.stream()
                .sorted(evaluationsComparator)
                .map(KnnEvaluation::getComplexity)
                .collect(Collectors.toList());
    }

    private List<Long> getYAxisData(List<KnnEvaluation> evaluations) {
        return evaluations.stream()
                .sorted(evaluationsComparator)
                .map(it -> it.getBuildTimeMs() + it.getClassificationTimeMs())
                .collect(Collectors.toList());
    }

    private String getOutputFileName() {
        return new File(configuration.getOutputDir(), "total-build-and-classify-time-by-complexity.png").getAbsolutePath();
    }
}
