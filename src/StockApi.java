import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StockApi {

    // Substitua pela sua chave da API Alpha Vantage
    public static final String API_KEY = "H2VQ0C715S0W7DRB";

    // Map para armazenar o histórico de preços por símbolo da ação
    private Map<String, ArrayList<Map<String, Double>>> priceHistories = new HashMap<>();

    // Método para buscar e armazenar preços diários
    public void fetchAndStoreDailyPrice(String symbol) {
        try {
            // Monta a URL da requisição para a Alpha Vantage
            String urlString = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
                symbol, API_KEY);
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Lê a resposta da API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Extrai todos os preços e datas da resposta JSON
            String jsonResponse = response.toString();
            ArrayList<Map<String, Double>> dailyPrices = extractDailyPricesWithDate(jsonResponse);

            // Se houver preços válidos, armazene no histórico da ação
            if (!dailyPrices.isEmpty()) {
                storePriceHistory(symbol, dailyPrices);
            } else {
                System.out.println("Erro: Não há dados de preço válidos para " + symbol);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para extrair preços e datas a partir do JSON
    private ArrayList<Map<String, Double>> extractDailyPricesWithDate(String jsonResponse) {
        ArrayList<Map<String, Double>> priceWithDates = new ArrayList<>();
        try {
            // Converte a resposta JSON em um JSONObject
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // Verifica se a chave "Time Series (Daily)" existe na resposta
            if (jsonObject.has("Time Series (Daily)")) {
                JSONObject timeSeries = jsonObject.getJSONObject("Time Series (Daily)");

                // Percorre todos os timestamps e extrai a data e o preço de fechamento
                for (String date : timeSeries.keySet()) {
                    JSONObject data = timeSeries.getJSONObject(date);
                    String closePriceStr = data.getString("4. close");
                    double price = Double.parseDouble(closePriceStr);

                    // Cria um mapa para armazenar a data e o preço
                    Map<String, Double> priceData = new HashMap<>();
                    priceData.put(date, price);

                    // Adiciona o mapa ao histórico
                    priceWithDates.add(priceData);
                }
            } else {
                System.out.println("Erro: A resposta da API não contém os dados esperados.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priceWithDates;
    }

    // Método para armazenar os preços e datas no histórico de uma ação específica
    private void storePriceHistory(String symbol, ArrayList<Map<String, Double>> newPriceData) {
        // Recupera o histórico atual ou cria uma nova lista
        ArrayList<Map<String, Double>> history = priceHistories.getOrDefault(symbol, new ArrayList<>());

        // Adiciona todos os novos dados ao histórico
        history.addAll(newPriceData);

        // Armazena o histórico atualizado no Map
        priceHistories.put(symbol, history);
    }

    // Método para obter o histórico de preços de uma ação com as datas
    public ArrayList<Map<String, Double>> getPriceHistoryWithDate(String symbol) {
        return priceHistories.getOrDefault(symbol, new ArrayList<>());
    }

    // Função para pegar o último preço de uma ação
    public Double getLastPrice(String symbol) {
        ArrayList<Map<String, Double>> prices = priceHistories.get(symbol);
        if (prices != null && !prices.isEmpty()) {
            // Retorna o preço do último registro no histórico (o primeiro, já que os dados são armazenados na ordem em que são recebidos)
            return prices.get(0).values().iterator().next();
        }
        return null; // Retorna null se não houver dados de preços para o símbolo
    }

    // Função para obter todos os símbolos de ações armazenados
    public ArrayList<String> getAllSymbols() {
        return new ArrayList<>(priceHistories.keySet());
    }
}
