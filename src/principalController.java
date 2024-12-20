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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.application.Application;

public class principalController  {

    private static final String DB_URL = "jdbc:mysql://pd2ub.h.filess.io:3307/homebroker_substance";
    private static final String DB_USER = "homebroker_substance";
    private static final String DB_PASSWORD = "675ff3244d016e1bca2bde49e09e4a8c35396823";

    public static String getDbUrl() {
        return DB_URL;
    }


    public static String getDbUser() {
        return DB_USER;
    }


    public static String getDbPassword() {
        return DB_PASSWORD;
    }

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

    @FXML
    private Label grafico;

    @FXML
    private Button btn;

    @FXML
    private Label preco;


    private Conta conta; // Instância da classe Conta
    private StockApi StockApi;
    private int quantidade = 0;
    

    @FXML
    public void initialize() throws SQLException {
        conta = new Conta(); // Inicializa a conta quando o controlador é carregado
        StockApi = new StockApi();
        atualizarSaldo(); // Atualiza o saldo na interface
        atualizarQuantidade(); // Exibe quantidade inicial

    }
    

    @FXML
    void depositar(ActionEvent event) throws SQLException {
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

    private void atualizarSaldo() throws SQLException {
        // Exibe o saldo atual formatado na interface
        labelSaldo.setText(String.format("%.2f", conta.getSaldo()));
    
        // Atualiza o saldo no banco de dados
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sqlUpdateBalance = "UPDATE accounts SET balance = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sqlUpdateBalance)) {
                // Configura os parâmetros do comando SQL
                statement.setInt(1, conta.getId()); 
                statement.setDouble(2, conta.getSaldo()); // Novo saldo
                
                // Executa o comando SQL
                int rowsUpdated = statement.executeUpdate();
    
                // Opcional: Verifica se a atualização foi bem-sucedida
                if (rowsUpdated > 0) {
                    System.out.println("Saldo atualizado com sucesso no banco de dados.");
                } else {
                    System.out.println("Nenhuma linha foi atualizada. Verifique o ID do usuário.");
                }
            }
        }
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

   // Adiciona uma variável para armazenar o preço atual da ação
    private Double precoAtual = null;

    // Método para atualizar o preço
    public void atualizarPreco() {
        String siglaAPI = acoes.getText(); // Pega o símbolo da ação inserido
        if (!siglaAPI.isEmpty()) {
            // Se o preço já estiver armazenado, usa ele diretamente
            if (precoAtual == null) {
                precoAtual = StockApi.getLastPrice(siglaAPI);
            }
            
            // Verifica se o preço é válido
            if (precoAtual != null) {
                // Calcula o valor total (preço da ação * quantidade)
                Double valorTotal = precoAtual * quantidade;
                
                // Exibe o valor total no Label
                preco.setText(String.format("Preço Total: %.2f", valorTotal));
            } else {
                // Exibe mensagem de erro se o preço for null
                preco.setText("Erro: Não foi possível obter o preço.");
            }
        }
    }

    
    
    @FXML
    void mais(ActionEvent event) {
        quantidade++; // Incrementa a quantidade
        atualizarQuantidade(); // Atualiza a exibição da quantidade
        atualizarPreco(); // Atualiza o preço total exibido
    }
    
    @FXML
    void menos(ActionEvent event) {
        if (quantidade > 0) { // Garante que a quantidade não fique negativa
            quantidade--; // Decrementa a quantidade
        }
        atualizarQuantidade(); // Atualiza a exibição da quantidade
        atualizarPreco(); // Atualiza o preço total exibido
    }

    private void atualizarQuantidade() {
        // Atualiza a exibição da quantidade
        quant.setText("" + quantidade);
    }

    @FXML
    void escolherAção(ActionEvent event) {
        String siglaAPI = acoes.getText(); // Pega o símbolo da ação inserido
    
        // Verifica se o preço não foi atualizado ou é nulo
        if (precoAtual == null || !siglaAPI.equals(acoes.getText())) {
            StockApi.fetchAndStoreDailyPrice(siglaAPI);
            precoAtual = StockApi.getLastPrice(siglaAPI); // Atualiza o preço
        }
    
        // Verifica se o campo de texto não está vazio
        if (!siglaAPI.isEmpty()) {
            // Instancia a classe StockChart para o gráfico de linha
            StockChart stockChart = new StockChart(siglaAPI);
    
            // Obtém o StackPane com o gráfico
            StackPane chartPane = stockChart.getChartPane();
    
            // Exibe o gráfico dentro do Label (usando um StackPane no lugar de apenas o gráfico)
            grafico.setGraphic(chartPane);
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro", "Por favor, insira um símbolo válido.");
        }
    }
    
    


    @FXML
    void vender(ActionEvent event) {
        String siglaAPI = acoes.getText();
        Operacao op = Operacao.SELL;

        // Chama o método da StockApi para buscar o preço da ação
        StockApi.fetchAndStoreDailyPrice(siglaAPI);
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
                String sql = "INSERT INTO operations (account_id, stock_symbol, operation_type, quantity, price_per_stock, total_value) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int account_id = generatedKeys.getInt(1); // Obtém o ID gerado
                    
                statement.setInt(1, account_id);
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
                    }
                }
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
        String sql = "SELECT SUM(quantity) AS total_quantity FROM operations WHERE account_id = ? AND stock_symbol = ? AND operation_type = 'BUY' GROUP BY stock_symbol";

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
    public void setConta(Conta conta) throws SQLException {
        this.conta = conta;
        atualizarSaldo(); // Atualiza o saldo na interface com os dados da conta configurada
        }
        @FXML
        void comprar(ActionEvent event) throws IOException {
            // Recupera o símbolo da ação inserido pelo usuário e a quantidade de ações desejada
            String siglaAPI = acoes.getText();
            Operacao op = Operacao.BUY;
        
            // Chama o método da StockApi para buscar o preço da ação apenas quando necessário
            if (precoAtual == null) {
                StockApi.fetchAndStoreDailyPrice(siglaAPI);
                precoAtual = StockApi.getLastPrice(siglaAPI);
            }
            Double precoCompra = precoAtual * quantidade; // Cálculo do valor total da compra (preço da ação * quantidade)
        
            // Verifica se o saldo da conta é suficiente para a compra
            if (precoCompra <= conta.getSaldo()) {
                // Registra a operação no banco de dados
                try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    connection.setAutoCommit(false); // Inicia uma transação
        
                    // Registrar a operação de compra
                    String sql = "INSERT INTO operations (account_id, stock_symbol, operation_type, quantity, price_per_stock, total_value) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, conta.getId()); // ID da conta
                    statement.setString(2, siglaAPI); // Símbolo da ação
                    statement.setString(3, op.toString()); // Tipo de operação (BUY)
                    statement.setInt(4, quantidade); // Quantidade de ações compradas
                    statement.setDouble(5, precoAtual); // Preço por ação
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
