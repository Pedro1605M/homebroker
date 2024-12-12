import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class StockChart extends Application {

    private final String stockSymbol;
    private final StockApi stockApi;

    public StockChart(String stockSymbol) {
        this.stockSymbol = stockSymbol;
        this.stockApi = new StockApi();  // Supondo que a StockApi já esteja implementada
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Stock Price Chart");

        // Eixos do gráfico
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Stock Price");

        // Criação do gráfico de linha
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Stock Price History");

        // Criação da série de dados para o gráfico de linha
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Stock Prices");

        // Recuperando os dados históricos
        stockApi.fetchAndStoreDailyPrice(stockSymbol);
        ArrayList<Map<String, Double>> priceHistory = stockApi.getPriceHistoryWithDate(stockSymbol);

        // Adicionar os dados ordenados ao gráfico de linha (ordenando somente na exibição)
        priceHistory.stream()
            .flatMap(map -> map.entrySet().stream())  // Achatar o mapa para obter as entradas de data e preço
            .sorted((entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()))  // Ordenar pela data
            .forEach(entry -> {
                System.out.println("Adding data: " + entry.getKey() + " - " + entry.getValue());
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            });

        // Configuração da cena e do palco
        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);

        stage.setScene(scene);
        stage.show();
    }

    // Método para retornar o gráfico em um StackPane (pode ser usado para integração com outros componentes)
    public StackPane getChartPane() {
        // Eixos do gráfico
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Stock Price");

        // Criação do gráfico de linha
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Stock Price History");

        // Criação da série de dados para o gráfico de linha
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Stock Prices");

        // Recuperando os dados históricos
        stockApi.fetchAndStoreDailyPrice(stockSymbol);
        ArrayList<Map<String, Double>> priceHistory = stockApi.getPriceHistoryWithDate(stockSymbol);

        // Adicionar os dados ordenados ao gráfico de linha (ordenando somente na exibição)
        priceHistory.stream()
            .flatMap(map -> map.entrySet().stream())  // Achatar o mapa para obter as entradas de data e preço
            .sorted((entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()))  // Ordenar pela data
            .forEach(entry -> {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            });

        // Adicionando o gráfico ao StackPane
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(lineChart);

        return stackPane;  // Retorna o StackPane com o gráfico
    }
}
