import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.json.JSONObject;

public class StockApi {

    // Substitua pela sua chave da API Alpha Vantage
    public static final String API_KEY = "H2VQ0C715S0W7DRB"; 

    private double lastPrice = -1.0; // Armazena o último preço

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

            // Extrai o último preço da resposta JSON
            String jsonResponse = response.toString();
            this.lastPrice = extractLastPrice(jsonResponse);
            
        } catch (Exception e) {
            e.printStackTrace(); // Exibe o erro caso algo falhe
        }
    }

    // Método para extrair o último preço a partir do JSON
    private double extractLastPrice(String jsonResponse) {
        try {
            // Converte a resposta JSON em um JSONObject
            JSONObject jsonObject = new JSONObject(jsonResponse);
            
            // Verifica se a chave "Time Series (1min)" existe na resposta
            if (jsonObject.has("Time Series (1min)")) {
                JSONObject timeSeries = jsonObject.getJSONObject("Time Series (1min)");

                // Pega a chave mais recente da série temporal (o último preço)
                String latestTime = timeSeries.keys().next();
                JSONObject latestData = timeSeries.getJSONObject(latestTime);

                // Extrai o valor do preço de fechamento ("4. close")
                String lastPriceStr = latestData.getString("4. close");

                // Converte a string para double e retorna
                return Double.parseDouble(lastPriceStr);
            } else {
                System.out.println("Erro: A resposta da API não contém os dados esperados.");
                return -1.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1.0; // Caso ocorra algum erro, retorna -1.0
        }
    }

    // Método para obter o último preço armazenado
    public double getLastPrice() {
        return this.lastPrice;
    }
}
