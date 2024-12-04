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
    // Agora o Map armazena a data e o preço de fechamento
    private Map<String, ArrayList<Map<String, Double>>> priceHistories = new HashMap<>();

    // Método para buscar o preço de uma ação e armazenar a data junto com o preço
    public void fetchAndStorePrice(String symbol) {
        try {
            // Monta a URL da requisição para a Alpha Vantage
            String urlString = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=%s&interval=1min&apikey=%s",
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
            ArrayList<Map<String, Double>> currentPrices = extractPricesWithDate(jsonResponse);
            
            // Se houver preços válidos, armazene no histórico da ação
            if (!currentPrices.isEmpty()) {
                storePriceHistory(symbol, currentPrices);
            } else {
                System.out.println("Erro: Não há dados de preço válidos para " + symbol);
            }

        } catch (Exception e) {
            e.printStackTrace(); // Exibe o erro caso algo falhe
        }
    }

    // Método para extrair preços e datas a partir do JSON
    private ArrayList<Map<String, Double>> extractPricesWithDate(String jsonResponse) {
        ArrayList<Map<String, Double>> priceWithDates = new ArrayList<>();
        try {
            // Converte a resposta JSON em um JSONObject
            JSONObject jsonObject = new JSONObject(jsonResponse);
            
            // Verifica se a chave "Time Series (1min)" existe na resposta
            if (jsonObject.has("Time Series (1min)")) {
                JSONObject timeSeries = jsonObject.getJSONObject("Time Series (1min)");

                // Percorre todos os timestamps e extrai a data e o preço de fechamento
                for (String timestamp : timeSeries.keySet()) {
                    JSONObject data = timeSeries.getJSONObject(timestamp);
                    String lastPriceStr = data.getString("4. close");
                    double price = Double.parseDouble(lastPriceStr);

                    // Cria um mapa para armazenar a data e o preço
                    Map<String, Double> priceData = new HashMap<>();
                    priceData.put(timestamp, price);

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

    // Método para obter o último preço de uma ação junto com a data
    public Map<String, Double> getLastPriceWithDate(String symbol) {
        // Recupera o histórico de preços
        ArrayList<Map<String, Double>> history = priceHistories.getOrDefault(symbol, new ArrayList<>());

        // Verifica se há preços armazenados
        if (!history.isEmpty()) {
            // Retorna o último preço e a data
            return history.get(history.size() - 1);
        } else {
            System.out.println("Erro: Não há dados de preço disponíveis para " + symbol);
            return null;
        }
    }

    public static void main(String[] args) {
        // Cria uma instância da StockApi
        StockApi stockApi = new StockApi();
        
        // Lista de símbolos das ações que você deseja consultar
        String[] symbols = {"AAPL", "GOOG", "MSFT"};  // Você pode adicionar mais símbolos aqui

        // Para cada símbolo, buscamos os dados
        for (String symbol : symbols) {
            System.out.println("Buscando dados para a ação: " + symbol);
            stockApi.fetchAndStorePrice(symbol);

            // Obtém o histórico de preços com as datas
            ArrayList<Map<String, Double>> priceHistory = stockApi.getPriceHistoryWithDate(symbol);

            // Exibe o histórico de preços com as datas
            System.out.println("\nHistórico de preços para " + symbol + ":");
            for (Map<String, Double> priceData : priceHistory) {
                for (String timestamp : priceData.keySet()) {
                    System.out.println("Data: " + timestamp + ", Preço: " + priceData.get(timestamp));
                }
            }

            // Obtém o último preço com a data
            Map<String, Double> lastPrice = stockApi.getLastPriceWithDate(symbol);
            if (lastPrice != null) {
                System.out.println("\nÚltimo preço para " + symbol + ":");
                for (String timestamp : lastPrice.keySet()) {
                    System.out.println("Data: " + timestamp + ", Preço: " + lastPrice.get(timestamp));
                }
            }

            // Espaço entre as informações das ações
            System.out.println("\n---------------------------------------------------\n");
        }
    }
}


