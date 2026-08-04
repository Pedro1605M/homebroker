import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class StockApi {

    public static final String API_KEY = "TRSY7U3SUNU9AZ2X";
    private static final long CACHE_EXPIRATION_TIME = 60 * 60 * 1000; // 1 hora

    private Map<String, ArrayList<Map<String, Double>>> priceHistories = new HashMap<>();
    private Map<String, Long> lastFetchTimes = new HashMap<>();

    // ─── Configuração SSL para ignorar certificado inválido ─────────────────────
    static {
        try {
            TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAll, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível configurar SSL personalizado: " + e.getMessage());
        }
    }
    // ────────────────────────────────────────────────────────────────────────────

    public void fetchAndStoreDailyPrice(String symbol) {
        long currentTime = System.currentTimeMillis();

        // Cache ainda válido? Não busca de novo.
        if (priceHistories.containsKey(symbol) &&
            (currentTime - lastFetchTimes.getOrDefault(symbol, 0L)) < CACHE_EXPIRATION_TIME) {
            System.out.println("Usando cache para " + symbol);
            return;
        }

        boolean apiSuccess = false;

        try {
            String urlString = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
                symbol, API_KEY);
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String jsonResponse = response.toString();
            System.out.println("Resposta da API (primeiros 200 chars): " + jsonResponse.substring(0, Math.min(200, jsonResponse.length())));

            ArrayList<Map<String, Double>> dailyPrices = extractDailyPricesWithDate(jsonResponse);

            if (!dailyPrices.isEmpty()) {
                priceHistories.put(symbol, dailyPrices);
                lastFetchTimes.put(symbol, currentTime);
                apiSuccess = true;
                System.out.println("Dados reais carregados para " + symbol);
            } else {
                System.out.println("API retornou resposta vazia ou rate-limited para " + symbol);
            }

        } catch (Exception e) {
            System.out.println("Falha ao chamar API para " + symbol + ": " + e.getMessage());
        }

        // Fallback: usa dados simulados se a API falhou
        if (!apiSuccess) {
            System.out.println("Usando dados simulados para " + symbol);
            ArrayList<Map<String, Double>> simulados = gerarDadosSimulados(symbol);
            priceHistories.put(symbol, simulados);
            lastFetchTimes.put(symbol, currentTime);
        }
    }

    // ─── Gera 30 dias de preços simulados coerentes com o símbolo ───────────────
    private ArrayList<Map<String, Double>> gerarDadosSimulados(String symbol) {
        ArrayList<Map<String, Double>> dados = new ArrayList<>();

        // Preço base diferente por ação para parecer real
        double basePrice = getBasePrice(symbol);
        Random rng = new Random(symbol.hashCode()); // seed fixo por símbolo (reproduzível)

        // Gera 30 dias retroativos
        java.time.LocalDate hoje = java.time.LocalDate.now();
        double preco = basePrice;
        for (int i = 29; i >= 0; i--) {
            java.time.LocalDate data = hoje.minusDays(i);
            // Variação diária de -3% a +3%
            double variacao = 1 + (rng.nextDouble() * 0.06 - 0.03);
            preco = Math.max(1.0, preco * variacao);

            Map<String, Double> entry = new LinkedHashMap<>();
            entry.put(data.toString(), Math.round(preco * 100.0) / 100.0);
            dados.add(entry);
        }
        return dados;
    }

    private double getBasePrice(String symbol) {
        // Preços base aproximados de ações brasileiras comuns
        switch (symbol.toUpperCase()) {
            case "PETR4.SA": case "PETR4": return 38.50;
            case "VALE3.SA": case "VALE3": return 62.20;
            case "ITUB4.SA": case "ITUB4": return 34.80;
            case "BBDC4.SA": case "BBDC4": return 14.60;
            case "ABEV3.SA": case "ABEV3": return 12.90;
            case "MGLU3.SA": case "MGLU3": return 9.40;
            case "WEGE3.SA": case "WEGE3": return 52.30;
            case "BBAS3.SA": case "BBAS3": return 28.70;
            case "ITSA4.SA": case "ITSA4": return 11.20;
            default:
                // Preço genérico baseado no hash do símbolo
                return 10.0 + (Math.abs(symbol.hashCode()) % 100);
        }
    }
    // ────────────────────────────────────────────────────────────────────────────

    private ArrayList<Map<String, Double>> extractDailyPricesWithDate(String jsonResponse) {
        ArrayList<Map<String, Double>> priceWithDates = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            if (jsonObject.has("Time Series (Daily)")) {
                JSONObject timeSeries = jsonObject.getJSONObject("Time Series (Daily)");

                for (String date : timeSeries.keySet()) {
                    JSONObject data = timeSeries.getJSONObject(date);
                    if (data.has("4. close")) {
                        double price = Double.parseDouble(data.getString("4. close"));
                        Map<String, Double> priceData = new LinkedHashMap<>();
                        priceData.put(date, price);
                        priceWithDates.add(priceData);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao parsear resposta da API: " + e.getMessage());
        }
        return priceWithDates;
    }

    private void storePriceHistory(String symbol, ArrayList<Map<String, Double>> newPriceData) {
        ArrayList<Map<String, Double>> history = priceHistories.getOrDefault(symbol, new ArrayList<>());
        history.addAll(newPriceData);
        priceHistories.put(symbol, history);
    }

    public ArrayList<Map<String, Double>> getPriceHistoryWithDate(String symbol) {
        return priceHistories.getOrDefault(symbol, new ArrayList<>());
    }

    public Double getLastPrice(String symbol) {
        ArrayList<Map<String, Double>> prices = priceHistories.get(symbol);
        if (prices != null && !prices.isEmpty()) {
            // Retorna o preço do último dia disponível
            return prices.get(prices.size() - 1).values().iterator().next();
        }
        return null;
    }
}
