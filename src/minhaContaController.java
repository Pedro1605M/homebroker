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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class minhaContaController {

    private static final String DB_URL = "jdbc:mysql://pd2ub.h.filess.io:3307/homebroker_substance";
    private static final String DB_USER = "homebroker_substance";
    private static final String DB_PASSWORD = "675ff3244d016e1bca2bde49e09e4a8c35396823";
    
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


    // Método chamado para inicializar a tela (após a criação da conta ou login)
    public void initialize() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE id = ?";
            String query2 = "SELECT * FROM accounts WHERE id = ?";
            
            // Primeiro PreparedStatement para usuários
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 PreparedStatement preparedStatement2 = connection.prepareStatement(query2)) {
                
                preparedStatement.setInt(1, Sessao.getIdUsuario());
                preparedStatement2.setInt(1, Conta.getId());
                
                try (ResultSet resultSet = preparedStatement.executeQuery();
                     ResultSet resultSet2 = preparedStatement2.executeQuery()) {
                     
                    if (resultSet.next()) {
                        String nome = resultSet.getString("name");
                        String email = resultSet.getString("email");
                        exibirnome.setText(nome);
                        exibirEmail.setText(email);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erro", "Usuário não encontrado.");
                        return;
                    }
                    
                    if (resultSet2.next()) {
                        Double balance = resultSet2.getDouble("balance");
                        saldo.setText(String.format("%.2f", balance));
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erro", "Conta não encontrada.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao carregar os dados do usuário.");
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

    @FXML
    void sairDaConta(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Cadastro.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
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

    
}
