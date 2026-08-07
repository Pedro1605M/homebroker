import java.io.*;
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

public class StockApi {

    public static final String API_KEY = "TRSY7U3SUNU9AZ2X";
    private static final long CACHE_EXPIRATION_TIME = 60 * 60 * 1000; // 1 hora
    private static final String CACHE_FILE = "stock_cache.dat";

    private Map<String, ArrayList<Map<String, Double>>> priceHistories = new HashMap<>();
    private Map<String, Long> lastFetchTimes = new HashMap<>();

    public StockApi() {
        loadCacheFromDisk();
    }

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

        // Cache (em memória) ainda válido? Não busca de novo.
        if (priceHistories.containsKey(symbol) &&
            (currentTime - lastFetchTimes.getOrDefault(symbol, 0L)) < CACHE_EXPIRATION_TIME) {
            System.out.println("Usando cache em memória para " + symbol);
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
            ArrayList<Map<String, Double>> dailyPrices = extractDailyPricesWithDate(jsonResponse);

            if (!dailyPrices.isEmpty()) {
                priceHistories.put(symbol, dailyPrices);
                lastFetchTimes.put(symbol, currentTime);
                apiSuccess = true;
                System.out.println("Dados reais carregados para " + symbol);
                saveCacheToDisk(); // Salva o arquivo assim que obtemos dados novos com sucesso
            } else {
                System.out.println("API retornou resposta vazia ou rate-limited para " + symbol);
            }

        } catch (Exception e) {
            System.out.println("Falha ao chamar API para " + symbol + ": " + e.getMessage());
        }

        // Se a API falhar, simplesmente mantemos o que já temos armazenado
        if (!apiSuccess) {
            if (priceHistories.containsKey(symbol)) {
                System.out.println("API falhou. Mantendo os últimos dados reais guardados para " + symbol);
            } else {
                System.out.println("API falhou e não temos histórico salvo para " + symbol + ". Ficará sem dados.");
            }
        }
    }

    private ArrayList<Map<String, Double>> extractDailyPricesWithDate(String jsonResponse) {
        ArrayList<Map<String, Double>> priceWithDates = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            if (jsonObject.has("Time Series (Daily)")) {
                JSONObject timeSeries = jsonObject.getJSONObject("Time Series (Daily)");
                
                // Obtém todas as datas e ordena de forma crescente (da mais antiga para a mais nova)
                java.util.List<String> dates = new java.util.ArrayList<>(timeSeries.keySet());
                java.util.Collections.sort(dates);

                for (String date : dates) {
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
        saveCacheToDisk();
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

    // ─── Salva e Carrega dados do disco rígido ─────────────────────────────────

    private void saveCacheToDisk() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CACHE_FILE))) {
            oos.writeObject(priceHistories);
        } catch (Exception e) {
            System.out.println("Erro ao salvar cache em disco: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadCacheFromDisk() {
        File file = new File(CACHE_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                priceHistories = (Map<String, ArrayList<Map<String, Double>>>) obj;
                System.out.println("Cache de ações carregado do disco com sucesso.");
            }
        } catch (Exception e) {
            System.out.println("Aviso: Não foi possível carregar o cache antigo do disco.");
        }
    }
}
