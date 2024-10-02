import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;

import org.json.JSONObject;

public class StockApi {

    // Chave da API Alpha Vantage e símbolo da ação (AAPL) que será consultada
    public static final String API_KEY = "H2VQ0C715S0W7DRB"; // Substitua pela sua chave da API Alpha Vantage


    public void getPrice(String SYMBOL) {
        try {
            // Cria a URI e a URL da requisição
            String urlString = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=%s&interval=1min&apikey=%s",
                SYMBOL, API_KEY); // Monta a URL com a função, símbolo da ação e chave de API
            URI uri = new URI(urlString); // Converte a string da URL para uma URI válida
            URL url = uri.toURL(); // Converte a URI para uma URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Abre a conexão HTTP

            // Define o método de requisição como "GET" para buscar dados
            connection.setRequestMethod("GET");

            // Lê a resposta da requisição
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); // Lê a resposta da conexão
            StringBuilder response = new StringBuilder(); // Usado para armazenar a resposta completa
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine); // Adiciona cada linha da resposta ao StringBuilder
            }
            in.close(); // Fecha o leitor de buffer

            // Armazena a resposta JSON como string
            String jsonResponse = response.toString();
            String lastPrice = extractLastPrice(jsonResponse); // Extrai o último preço da resposta JSON

            // Exibe o último preço da ação no console

        } catch (Exception e) {
            e.printStackTrace(); // Exibe o stack trace no caso de erro
        }
    }

    // Função que extrai o último preço da resposta JSON
    private static String extractLastPrice(String jsonResponse) {
        try {
            // Converte a resposta em um objeto JSON
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // Obtém a seção "Time Series (1min)" do JSON
            JSONObject timeSeries = jsonObject.getJSONObject("Time Series (1min)");

            // Obtém a primeira (mais recente) chave na série temporal (timestamp mais recente)
            Iterator<String> keys = timeSeries.keys(); // Iterador para navegar nas chaves (timestamps)
            if (keys.hasNext()) {
                String latestTimestamp = keys.next(); // Pega o timestamp mais recente
                JSONObject latestData = timeSeries.getJSONObject(latestTimestamp); // Obtém os dados do timestamp mais recente

                // Retorna o preço de fechamento para o timestamp mais recente
                return latestData.getString("4. close"); // Pega o preço de fechamento no campo "4. close"
            }

        } catch (Exception e) {
            e.printStackTrace(); // Exibe o stack trace em caso de erro durante a extração
            return "Error parsing the JSON response."; // Retorna uma mensagem de erro
        }
        return "Price data not found."; // Caso não encontre os dados de preço
    }
    
}
