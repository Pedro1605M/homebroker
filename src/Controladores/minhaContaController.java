package Controladores;

import Classes.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class minhaContaController {
    
    @FXML
    private Label exibirEmail;

    @FXML
    private Label exibirnome;

    @FXML
    private Button menu;

    @FXML
    private Button sair;

    @FXML
    private Label saldo;

    @FXML
    private TableView<CarteiraRow> tabelaCarteira;

    @FXML
    private TableColumn<CarteiraRow, String> colunaAcao;

    @FXML
    private TableColumn<CarteiraRow, Integer> colunaQuantidade;

    // Método para carregar os dados do usuário do banco de dados
    private void carregarDadosUsuario(int userId) {
        String sql = "SELECT name, email FROM users WHERE id = ?";
        
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Definir o ID do usuário na consulta
            statement.setInt(1, userId);
            
            // Executar a consulta
            ResultSet resultSet = statement.executeQuery();
            
            // Verificar se o usuário foi encontrado
            if (resultSet.next()) {
                String nome = resultSet.getString("name");
                String email = resultSet.getString("email");
                
                // Atualizar os Labels com o nome e e-mail
                exibirnome.setText(nome);
                exibirEmail.setText(email);
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Usuário não encontrado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao carregar os dados do usuário.");
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

    @FXML
    void sairDaConta(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Telas/Cadastro.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Cadastro");
        stage.show();
        showAlert(Alert.AlertType.INFORMATION, "Saiu", "Você deslogou da sua conta");
    }

    // Exibir alertas de erro ou sucesso
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Método chamado para inicializar a tela (após a criação da conta ou login)
    public void initialize() {
        if (Sessao.isLogado()) {
            carregarDadosUsuario(Sessao.getUserId());
            if (saldo != null) {
                saldo.setText(String.format("R$ %.2f", Sessao.getSaldo()));
            }
            configurarTabelaCarteira();
            carregarCarteira();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro", "Nenhum usuário logado.");
        }
    }

    private void configurarTabelaCarteira() {
        if (colunaAcao != null && colunaQuantidade != null) {
            colunaAcao.setCellValueFactory(cell -> cell.getValue().simboloProperty());
            colunaQuantidade.setCellValueFactory(cell -> cell.getValue().quantidadeProperty().asObject());
        }
    }

    private void carregarCarteira() {
        if (tabelaCarteira == null) return;
        
        ObservableList<CarteiraRow> carteira = FXCollections.observableArrayList();
        String sql = "SELECT stock_symbol, " +
                     "COALESCE(SUM(CASE WHEN operation_type = 'BUY' THEN quantity ELSE 0 END), 0) - " +
                     "COALESCE(SUM(CASE WHEN operation_type = 'SELL' THEN quantity ELSE 0 END), 0) AS saldo_acoes " +
                     "FROM operations WHERE account_id = ? " +
                     "GROUP BY stock_symbol HAVING saldo_acoes > 0";
                     
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
             
            statement.setInt(1, Sessao.getAccountId());
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                carteira.add(new CarteiraRow(rs.getString("stock_symbol"), rs.getInt("saldo_acoes")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        tabelaCarteira.setItems(carteira);
    }

    public static class CarteiraRow {
        private final SimpleStringProperty simbolo;
        private final SimpleIntegerProperty quantidade;

        public CarteiraRow(String simbolo, int quantidade) {
            this.simbolo = new SimpleStringProperty(simbolo);
            this.quantidade = new SimpleIntegerProperty(quantidade);
        }

        public SimpleStringProperty simboloProperty() { return simbolo; }
        public SimpleIntegerProperty quantidadeProperty() { return quantidade; }
    }
}
