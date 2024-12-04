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
    private Map<String, ArrayList<Double>> priceHistories = new HashMap<>();

    // Método para buscar o preço de uma ação
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

            // Extrai todos os preços da resposta JSON
            String jsonResponse = response.toString();
            ArrayList<Double> currentPrices = extractPrices(jsonResponse);
            
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

    // Método para extrair todos os preços a partir do JSON
    private ArrayList<Double> extractPrices(String jsonResponse) {
        ArrayList<Double> prices = new ArrayList<>();
        try {
            // Converte a resposta JSON em um JSONObject
            JSONObject jsonObject = new JSONObject(jsonResponse);
            
            // Verifica se a chave "Time Series (1min)" existe na resposta
            if (jsonObject.has("Time Series (1min)")) {
                JSONObject timeSeries = jsonObject.getJSONObject("Time Series (1min)");

                // Percorre todos os timestamps e extrai os preços de fechamento
                for (String timestamp : timeSeries.keySet()) {
                    JSONObject data = timeSeries.getJSONObject(timestamp);
                    String lastPriceStr = data.getString("4. close");
                    prices.add(Double.parseDouble(lastPriceStr));
                }
            } else {
                System.out.println("Erro: A resposta da API não contém os dados esperados.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prices;
    }

    // Método para armazenar os preços no histórico de uma ação específica
    private void storePriceHistory(String symbol, ArrayList<Double> newPrices) {
        // Recupera o histórico atual ou cria uma nova lista
        ArrayList<Double> history = priceHistories.getOrDefault(symbol, new ArrayList<>());
        
        // Adiciona todos os novos preços ao histórico
        history.addAll(newPrices);

        // Armazena o histórico atualizado no Map
        priceHistories.put(symbol, history);
    }

    // Método para obter o histórico de preços de uma ação
    public ArrayList<Double> getPriceHistory(String symbol) {
        return priceHistories.getOrDefault(symbol, new ArrayList<>());
    }

    // Método para obter o último preço de uma ação
    public Double getLastPrice(String symbol) {
        // Recupera o histórico de preços
        ArrayList<Double> history = priceHistories.getOrDefault(symbol, new ArrayList<>());

        // Verifica se há preços armazenados
        if (!history.isEmpty()) {
            // Retorna o último preço
            return history.get(history.size() - 1);
        } else {
            System.out.println("Erro: Não há dados de preço disponíveis para " + symbol);
            return null;
        }
    }
}

