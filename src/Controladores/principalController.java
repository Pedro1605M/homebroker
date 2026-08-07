package Controladores;

import Classes.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class principalController {

    // Criamos uma URL direta para o banco de dados
    private static final String DB_URL = "jdbc:sqlite:homebroker.db";

    @FXML private TextField acoes;
    @FXML private Button btnComprar;
    @FXML private Button btnMais;
    @FXML private Button btnMenos;
    @FXML private Button depositar;
    @FXML private TextField deposito;
    @FXML private Button menu;
    @FXML private Label labelSaldo;
    @FXML private Label quant;
    @FXML private Label grafico;
    @FXML private Button btn;
    @FXML private Label preco;

    private Conta conta;
    private StockApi StockApi;
    private int quantidade = 0;
    private Double precoAtual = null;

    @FXML
    public void initialize() {
        StockApi = new StockApi();

        // Carrega os dados da sessão ativa (definida no login)
        if (Sessao.isLogado()) {
            conta = new Conta();
            conta.setId(Sessao.getAccountId());
            conta.setSaldo(Sessao.getSaldo());
        } else {
            conta = new Conta();
        }

        atualizarSaldo();
        atualizarQuantidade();
    }

    public void setConta(Conta conta) {
        this.conta = conta;
        if (conta != null) {
            Sessao.setAccountId(conta.getId());
            Sessao.setSaldo(conta.getSaldo());
        }
        atualizarSaldo();
    }

    @FXML
    void depositar(ActionEvent event) {
        try {
            Double valor = Double.parseDouble(deposito.getText());
            conta.adicionarSaldo(valor);
            atualizarSaldo();
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Seu depósito foi feito corretamente.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível fazer o depósito.");
        }
    }

    private void atualizarSaldo() {
        if (conta != null) {
            labelSaldo.setText(String.format("%.2f", conta.getSaldo()));
        }

        if (conta != null && conta.getId() != null && conta.getId() > 0) {
            String sqlUpdateBalance = "UPDATE accounts SET balance = ? WHERE id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sqlUpdateBalance)) {

                statement.setDouble(1, conta.getSaldo());
                statement.setInt(2, conta.getId());
                statement.executeUpdate();

                // Mantém a Sessão sincronizada
                Sessao.setSaldo(conta.getSaldo());

            } catch (SQLException e) {
                System.out.println("Erro ao atualizar saldo: " + e.getMessage());
            }
        }
    }

    @FXML
    void irpratelaMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Telas/Menu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Menu");
        stage.show();
    }

    public void atualizarPreco() {
        String siglaAPI = acoes.getText(); 
        if (!siglaAPI.isEmpty()) {
            if (precoAtual == null) {
                precoAtual = StockApi.getLastPrice(siglaAPI);
            }
            if (precoAtual != null) {
                Double valorTotal = precoAtual * quantidade;
                preco.setText(String.format("Preço Total: %.2f", valorTotal));
            } else {
                preco.setText("Erro: Não foi possível obter o preço.");
            }
        }
    }

    @FXML
    void mais(ActionEvent event) {
        quantidade++; 
        atualizarQuantidade(); 
        atualizarPreco(); 
    }
    
    @FXML
    void menos(ActionEvent event) {
        if (quantidade > 0) { 
            quantidade--; 
        }
        atualizarQuantidade(); 
        atualizarPreco(); 
    }

    private void atualizarQuantidade() {
        quant.setText("" + quantidade);
    }

    @FXML
    void escolherAção(ActionEvent event) {
        String siglaAPI = acoes.getText().trim();
        if (siglaAPI.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Por favor, insira um símbolo válido.");
            return;
        }

        StockApi.fetchAndStoreDailyPrice(siglaAPI);
        precoAtual = StockApi.getLastPrice(siglaAPI);

        if (preco != null && precoAtual != null) {
            preco.setText(String.format("R$ %.2f", precoAtual));
        }

        StockChart stockChart = new StockChart(siglaAPI, StockApi);
        StackPane chartPane = stockChart.getChartPane();
        grafico.setGraphic(chartPane);
    }

    @FXML
    void comprar(ActionEvent event) {
        String siglaAPI = acoes.getText().trim();

        if (siglaAPI.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Digite o código da ação antes de comprar.");
            return;
        }
        if (quantidade <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Selecione a quantidade de ações para comprar (+/-).");
            return;
        }

        if (precoAtual == null) {
            StockApi.fetchAndStoreDailyPrice(siglaAPI);
            precoAtual = StockApi.getLastPrice(siglaAPI);
        }
        if (precoAtual == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível obter o preço da ação. Clique em 'Escolher' primeiro.");
            return;
        }

        double precoCompra = precoAtual * quantidade;

        if (precoCompra <= conta.getSaldo()) {
            
            String sql = "INSERT INTO operations (account_id, stock_symbol, operation_type, quantity, price_per_stock, total_value) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setInt(1, conta.getId());
                statement.setString(2, siglaAPI);
                statement.setString(3, "BUY");
                statement.setInt(4, quantidade);
                statement.setDouble(5, precoAtual);
                statement.setDouble(6, precoCompra);
                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro no Banco", "Erro ao registrar compra no banco: " + e.getMessage());
                return;
            }

            // 2. AGORA QUE O BANCO ESTÁ LIVRE, ATUALIZA O SALDO E A SESSÃO
            conta.subtrairSaldo(precoCompra);
            try {
                atualizarSaldo(); // Esta função vai abrir sua própria conexão com segurança
            } catch (Exception e) {
                System.out.println("Erro ao atualizar o saldo: " + e.getMessage());
            }

            Sessao.setAccountId(conta.getId());
            Sessao.setSaldo(conta.getSaldo());

            int qtdComprada = quantidade;
            quantidade = 0;
            atualizarQuantidade();
            
            showAlert(Alert.AlertType.INFORMATION, "Sucesso",
                String.format("Compra de %d ação(ões) de %s por R$ %.2f cada.\nTotal pago: R$ %.2f",
                    qtdComprada, siglaAPI, precoAtual, precoCompra));

        } else {
            showAlert(Alert.AlertType.ERROR, "Saldo insuficiente",
                String.format("A compra custa R$ %.2f mas seu saldo é R$ %.2f", precoCompra, conta.getSaldo()));
        }
    }

    @FXML
    void vender(ActionEvent event) {
        String siglaAPI = acoes.getText().trim();

        if (siglaAPI.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Escolha uma ação antes de vender.");
            return;
        }
        if (quantidade <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Selecione a quantidade de ações para vender (+/-).");
            return;
        }

        StockApi.fetchAndStoreDailyPrice(siglaAPI);
        Double precoAcao = StockApi.getLastPrice(siglaAPI);
        if (precoAcao == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível obter o preço da ação.");
            return;
        }

        double precoVenda = precoAcao * quantidade;
        int quantidadeDisponivel = verificarQuantidadeAcoes(siglaAPI, conta.getId());

        if (quantidadeDisponivel >= quantidade) {
            
            String sql = "INSERT INTO operations (account_id, stock_symbol, operation_type, quantity, price_per_stock, total_value) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setInt(1, conta.getId());
                statement.setString(2, siglaAPI);
                statement.setString(3, "SELL");
                statement.setInt(4, quantidade);
                statement.setDouble(5, precoAcao);
                statement.setDouble(6, precoVenda);
                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro no Banco", "Erro ao registrar venda no banco: " + e.getMessage());
                return;
            }

            // 2. AGORA QUE O BANCO ESTÁ LIVRE, ATUALIZA O SALDO E A SESSÃO
            conta.adicionarSaldo(precoVenda);
            try {
                atualizarSaldo(); // Esta função vai abrir sua própria conexão com segurança
            } catch (Exception e) {
                System.out.println("Erro ao atualizar o saldo: " + e.getMessage());
            }

            Sessao.setAccountId(conta.getId());
            Sessao.setSaldo(conta.getSaldo());

            int qtdVendida = quantidade;
            quantidade = 0;
            atualizarQuantidade();
            precoAtual = null;
            
            showAlert(Alert.AlertType.INFORMATION, "Sucesso",
                String.format("Venda de %d ação(ões) de %s por R$ %.2f cada.\nTotal recebido: R$ %.2f",
                    qtdVendida, siglaAPI, precoAcao, precoVenda));

        } else {
            showAlert(Alert.AlertType.ERROR, "Erro",
                "Você tem apenas " + quantidadeDisponivel + " ação(ões) de " + siglaAPI + " disponíveis.");
        }
    }
    private int verificarQuantidadeAcoes(String siglaAPI, int accountId) {
        // Calcula o saldo líquido: total comprado - total vendido
        String sql = "SELECT " +
            "COALESCE(SUM(CASE WHEN operation_type = 'BUY'  THEN quantity ELSE 0 END), 0) - " +
            "COALESCE(SUM(CASE WHEN operation_type = 'SELL' THEN quantity ELSE 0 END), 0) " +
            "AS saldo_acoes " +
            "FROM operations WHERE account_id = ? AND stock_symbol = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, accountId);
            statement.setString(2, siglaAPI);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}