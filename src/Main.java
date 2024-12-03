import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/* public class Main {

    public static void main(String[] args) {
        // Cria uma instância da classe StockApi
        StockApi stockApi = new StockApi();

        // Símbolo da ação que você deseja consultar (AAPL é um exemplo de ação da Apple)
        String symbol = "AAPL";  // Exemplo: "AAPL" para Apple

        // Chama o método para buscar e armazenar o último preço
        stockApi.fetchAndStorePrice(symbol);

        // Obtém o último preço usando o método getLastPrice
        double lastPrice = stockApi.getLastPrice();

        // Exibe o último preço ou um erro caso não tenha sido obtido com sucesso
        if (lastPrice != -1.0) {
            System.out.println("Último preço de " + symbol + ": $" + lastPrice);
        } else {
            System.out.println("Erro ao obter o último preço.");
        }
    }
} */

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Cadastro.fxml"));
        primaryStage.setTitle("Home Broker");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}