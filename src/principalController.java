import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class principalController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/sql10748012";
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "ifsp";

    @FXML
    private TextField acoes;

    @FXML
    private Button btnComprar;

    @FXML
    private Button btnMais;

    @FXML
    private Button btnMenos;

    @FXML
    private Button depositar;

    @FXML
    private TextField deposito;

    @FXML
    private Button menu;

    @FXML
    private Label labelSaldo;

    @FXML
    private Label quant;

    private Conta conta; // Instância da classe Conta
    private StockApi StockApi;
    private int quantidade = 0;
    

    @FXML
    public void initialize() {
        conta = new Conta(); // Inicializa a conta quando o controlador é carregado
        StockApi = new StockApi();
        atualizarSaldo(); // Atualiza o saldo na interface
        atualizarQuantidade(); // Exibe quantidade inicial

    }

    @FXML
    void depositar(ActionEvent event) {
        try {
            // Lê o valor do campo de texto
            String valorDeposito = deposito.getText();
            Double valor = Double.parseDouble(valorDeposito);

            // Adiciona o valor ao saldo da conta
            conta.adicionarSaldo(valor);

            // Atualiza o saldo exibido
            atualizarSaldo();
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Seu depósito foi feito corretamente.");
        } catch (NumberFormatException e) {
            // Caso o valor inserido não seja numérico, exibe mensagem de erro
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível fazer o depósito.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void atualizarSaldo() {
        // Exibe o saldo atual formatado
        labelSaldo.setText(String.format("%.2f", conta.getSaldo()));
    }

    @FXML
    void irpratelaMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Menu");
        stage.show();
    }

    @FXML
    void mais(ActionEvent event) {
        quantidade++; // Incrementa a quantidade
        atualizarQuantidade(); // Atualiza a exibição
    }

    @FXML
    void menos(ActionEvent event) {
        if (quantidade > 0) { // Garante que a quantidade não fique negativa
            quantidade--; // Decrementa a quantidade
        }
        atualizarQuantidade(); // Atualiza a exibição
    }

    private void atualizarQuantidade() {
        // Atualiza a exibição da quantidade
        quant.setText("" + quantidade);
    }

    @FXML
    void vender(ActionEvent event) {
        String siglaAPI = acoes.getText();
        Operacao op = Operacao.SELL;

        // Chama o método da StockApi para buscar o preço da ação
        StockApi.fetchAndStorePrice(siglaAPI);
        Double precoAcao = StockApi.getLastPrice(siglaAPI); // Preço da ação
        Double precoVenda = precoAcao * quantidade; // Cálculo do valor total da venda (preço da ação * quantidade)

        // Passo 1: Consultar o banco de dados para verificar se o usuário tem ações suficientes para vender
        int quantidadeDisponivel = verificarQuantidadeAcoes(siglaAPI, conta.getId());

        if (quantidadeDisponivel >= quantidade) {
            // Passo 2: Se o usuário tem ações suficientes, subtrair o valor da venda do saldo
            conta.adicionarSaldo(precoVenda);

            // Passo 3: Registrar a operação de venda no banco de dados
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                connection.setAutoCommit(false); // Inicia uma transação

                // Registrar a operação de venda
                String sql = "INSERT INTO operations (account_id, stock_symbol, operations_type, quantity, price_per_stock, total_value) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, conta.getId());
                statement.setString(2, siglaAPI);
                statement.setString(3, op.toString());
                statement.setInt(4, quantidade);
                statement.setDouble(5, precoAcao);
                statement.setDouble(6, precoVenda);
                statement.executeUpdate();

                // Comita a transação
                connection.commit();
                
                // Atualiza a interface com o novo saldo e a quantidade de ações
                atualizarSaldo();
                quantidade = 0; // Reseta a quantidade de ações na interface
                atualizarQuantidade();
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Venda realizada com sucesso!");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao registrar a operação.");
            }
        } else {
            // Se o usuário não tiver ações suficientes para a venda
            showAlert(Alert.AlertType.ERROR, "Erro", "Você não tem ações suficientes para vender.");
        }
    }

    // Método para consultar a quantidade de ações de um usuário no banco de dados
    private int verificarQuantidadeAcoes(String siglaAPI, int userId) {
        int quantidade = 0;
        String sql = "SELECT SUM(quantity) AS total_quantity FROM operations WHERE account_id = ? AND stock_symbol = ? AND operations_type = 'BUY' GROUP BY stock_symbol";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);  // ID da conta (usuário)
            statement.setString(2, siglaAPI); // Símbolo da ação

            // Executa a consulta e obtém o resultado
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                quantidade = resultSet.getInt("total_quantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quantidade;
    }

    @FXML
    void comprar(ActionEvent event) throws IOException {
        // Recupera o símbolo da ação inserido pelo usuário e a quantidade de ações desejada
        String siglaAPI = acoes.getText();
        Operacao op = Operacao.BUY;

        // Chama o método da StockApi para buscar o preço da ação
        StockApi.fetchAndStorePrice(siglaAPI);
        Double precoAcao = StockApi.getLastPrice(siglaAPI); // Preço da ação
        Double precoCompra = precoAcao * quantidade; // Cálculo do valor total da compra (preço da ação * quantidade)

        // Verifica se o saldo da conta é suficiente para a compra
        if (precoCompra <= conta.getSaldo()) {

            // Registra a operação no banco de dados
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                connection.setAutoCommit(false); // Inicia uma transação

                // Registrar a operação de compra
                String sql = "INSERT INTO operations (account_id, stock_symbol, operations_type, quantity, price_per_stock, total_value) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, conta.getId()); // ID da conta
                statement.setString(2, siglaAPI); // Símbolo da ação
                statement.setString(3, op.toString()); // Tipo de operação (BUY)
                statement.setInt(4, quantidade); // Quantidade de ações compradas
                statement.setDouble(5, precoAcao); // Preço por ação
                statement.setDouble(6, precoCompra); // Valor total da compra
                statement.executeUpdate();

                 // Subtrai o valor da compra do saldo da conta
                conta.subtrairSaldo(precoCompra);

                // Atualiza o saldo exibido na interface
                atualizarSaldo();

                // Comita a transação
                connection.commit();

                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Compra realizada com sucesso!");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao registrar a operação.");
            }
        } else {
            // Se o saldo for insuficiente, exibe uma mensagem de erro
            showAlert(Alert.AlertType.ERROR, "Erro", "Saldo insuficiente para realizar a compra.");
        }
    }
}
