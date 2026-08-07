import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StockChart extends Application {

    private final String stockSymbol;
    private final StockApi stockApi;

    public StockChart(String stockSymbol, StockApi stockApi) {
        this.stockSymbol = stockSymbol;
        this.stockApi = stockApi;
    }

    public StockChart(String stockSymbol) {
        this.stockSymbol = stockSymbol;
        this.stockApi = new StockApi();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Stock Price Chart - " + stockSymbol);
        stage.setScene(new Scene(getChartPane(), 820, 500));
        stage.show();
    }

    public StackPane getChartPane() {
        stockApi.fetchAndStoreDailyPrice(stockSymbol);
        ArrayList<Map<String, Double>> priceHistory = stockApi.getPriceHistoryWithDate(stockSymbol);

        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>();
        for (Map<String, Double> entry : priceHistory) {
            sortedEntries.addAll(entry.entrySet());
        }
        sortedEntries.sort((a, b) -> a.getKey().compareTo(b.getKey()));

        if (sortedEntries.size() > 120) {
            sortedEntries = sortedEntries.subList(sortedEntries.size() - 120, sortedEntries.size());
        }

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis   yAxis = new NumberAxis();
        xAxis.setTickLabelFill(javafx.scene.paint.Color.web("#666666"));
        xAxis.setTickLabelRotation(-45);
        yAxis.setTickLabelFill(javafx.scene.paint.Color.web("#666666"));

        if (!sortedEntries.isEmpty()) {
            double min = sortedEntries.stream().mapToDouble(Map.Entry::getValue).min().orElse(0);
            double max = sortedEntries.stream().mapToDouble(Map.Entry::getValue).max().orElse(100);
            double margem = (max - min) * 0.1;
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(Math.max(0, min - margem));
            yAxis.setUpperBound(max + margem);
            yAxis.setTickUnit((max - min) / 5);
        }

        final AreaChart<String, Number> areaChart = new AreaChart<>(xAxis, yAxis);
        areaChart.setTitle(stockSymbol);
        areaChart.setAnimated(false);
        areaChart.setCreateSymbols(false); 
        areaChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(stockSymbol);

        for (Map.Entry<String, Double> entry : sortedEntries) {
            String dataFormatada = entry.getKey().length() >= 10
                ? entry.getKey().substring(5)
                : entry.getKey();
            series.getData().add(new XYChart.Data<>(dataFormatada, entry.getValue()));
        }

        areaChart.getData().add(series);

        String css = """
            .chart { -fx-background-color: #ffffff; -fx-padding: 8; }
            .chart-plot-background { -fx-background-color: #ffffff; }
            .chart-vertical-grid-lines { -fx-stroke: transparent; }
            .chart-horizontal-grid-lines { -fx-stroke: #cccccc; -fx-stroke-dash-array: 5 5; }
            .chart-title { -fx-text-fill: #333333; -fx-font-size: 14px; -fx-font-weight: bold; }
            .axis { -fx-tick-label-fill: #666666; }
            .default-color0.chart-series-area-line { -fx-stroke: #42a5f5; -fx-stroke-width: 1.5px; }
            .default-color0.chart-series-area-fill { -fx-fill: rgba(66, 165, 245, 0.15); }
            """;

        areaChart.setStyle(css.replace("\n", " "));
        areaChart.getStylesheets().clear();

        StackPane stackPane = new StackPane(areaChart);
        stackPane.setStyle("-fx-background-color: #ffffff;");
        stackPane.setPrefSize(406, 280);

        areaChart.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                try {
                    newScene.getStylesheets().add(
                        "data:text/css," + java.net.URLEncoder.encode(css, java.nio.charset.StandardCharsets.UTF_8).replace("+", "%20")
                    );
                } catch (Exception e) {}
            }
        });

        return stackPane;
    }
}
