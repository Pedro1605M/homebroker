import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
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

    /** Construtor principal: recebe a instância já existente de StockApi (dados em cache). */
    public StockChart(String stockSymbol, StockApi stockApi) {
        this.stockSymbol = stockSymbol;
        this.stockApi = stockApi;
    }

    /** Construtor legado — cria instância própria (fallback). */
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

    /**
     * Retorna um gráfico de linha estilo "real stock chart" para embutir na tela principal.
     */
    public StackPane getChartPane() {
        // Garante que os dados estão carregados (usa cache se já buscados)
        stockApi.fetchAndStoreDailyPrice(stockSymbol);
        ArrayList<Map<String, Double>> priceHistory = stockApi.getPriceHistoryWithDate(stockSymbol);

        // ── Coleta e ordena os pontos por data ──────────────────────────────
        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>();
        for (Map<String, Double> entry : priceHistory) {
            sortedEntries.addAll(entry.entrySet());
        }
        sortedEntries.sort((a, b) -> a.getKey().compareTo(b.getKey()));

        // Mostra no máximo os últimos 30 dias para não poluir o gráfico
        if (sortedEntries.size() > 30) {
            sortedEntries = sortedEntries.subList(sortedEntries.size() - 30, sortedEntries.size());
        }

        // ── Determina cor: verde se subiu, vermelho se desceu ───────────────
        boolean subiu = sortedEntries.size() >= 2 &&
            sortedEntries.get(sortedEntries.size() - 1).getValue() >=
            sortedEntries.get(0).getValue();

        String corLinha  = subiu ? "#00e676" : "#ff1744";   // verde vibrante ou vermelho
        String corArea   = subiu ? "#00e67622" : "#ff174422"; // mesma cor, transparente
        String corPonto  = subiu ? "#00c853" : "#d50000";

        // ── Eixos ────────────────────────────────────────────────────────────
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Data");
        xAxis.setTickLabelFill(javafx.scene.paint.Color.web("#aaaaaa"));
        xAxis.setTickLabelRotation(-45);
        yAxis.setLabel("Preço (R$)");
        yAxis.setTickLabelFill(javafx.scene.paint.Color.web("#aaaaaa"));

        // Auto-range nos valores do eixo Y para dar zoom real
        if (!sortedEntries.isEmpty()) {
            double min = sortedEntries.stream().mapToDouble(Map.Entry::getValue).min().orElse(0);
            double max = sortedEntries.stream().mapToDouble(Map.Entry::getValue).max().orElse(100);
            double margem = (max - min) * 0.1;
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(Math.max(0, min - margem));
            yAxis.setUpperBound(max + margem);
            yAxis.setTickUnit((max - min) / 5);
        }

        // ── Linha do gráfico ─────────────────────────────────────────────────
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(stockSymbol + (subiu ? "  ▲" : "  ▼"));
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(true); // pontos visíveis em cada data
        lineChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(stockSymbol);

        for (Map.Entry<String, Double> entry : sortedEntries) {
            // Mostra só MM/DD para não encher o eixo X
            String dataFormatada = entry.getKey().length() >= 10
                ? entry.getKey().substring(5) // "YYYY-MM-DD" → "MM-DD"
                : entry.getKey();
            series.getData().add(new XYChart.Data<>(dataFormatada, entry.getValue()));
        }

        lineChart.getData().add(series);

        // ── CSS inline: estilo "stock chart" escuro ──────────────────────────
        String css = String.format("""
            .chart { -fx-background-color: #1a1020; -fx-padding: 8; }
            .chart-plot-background { -fx-background-color: #1a1020; }
            .chart-vertical-grid-lines { -fx-stroke: #2d2040; }
            .chart-horizontal-grid-lines { -fx-stroke: #2d2040; }
            .chart-title { -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; }
            .axis-label { -fx-text-fill: #888888; -fx-font-size: 10px; }
            .axis { -fx-tick-label-fill: #888888; }
            .chart-series-line { -fx-stroke: %s; -fx-stroke-width: 2.5px; }
            .chart-line-symbol {
                -fx-background-color: %s, #1a1020;
                -fx-background-radius: 4px;
                -fx-padding: 3px;
            }
            """, corLinha, corPonto);

        lineChart.setStyle(css.replace("\n", " "));

        // Aplicar CSS via stylesheet (mais confiável)
        lineChart.getStylesheets().clear();

        // Usa um StackPane como container
        StackPane stackPane = new StackPane(lineChart);
        stackPane.setStyle("-fx-background-color: #1a1020;");
        stackPane.setPrefSize(406, 280);

        // Aplica o CSS após a cena estar pronta
        lineChart.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                String inlineCSS = String.format(
                    ".chart-series-line { -fx-stroke: %s; -fx-stroke-width: 2.5px; }" +
                    ".chart-plot-background { -fx-background-color: #1a1020; }" +
                    ".chart-vertical-grid-lines { -fx-stroke: #2d2040; }" +
                    ".chart-horizontal-grid-lines { -fx-stroke: #2d2040; }" +
                    ".chart-line-symbol { -fx-background-color: %s, #1a1020; }",
                    corLinha, corPonto
                );
                // Cria arquivo CSS temporário em memória via data URI
                newScene.getStylesheets().add(
                    "data:text/css," + java.net.URLEncoder.encode(inlineCSS, java.nio.charset.StandardCharsets.UTF_8)
                        .replace("+", "%20")
                );
            }
        });

        return stackPane;
    }
}
