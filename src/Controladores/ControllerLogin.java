package Controladores;

import Classes.*;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerLogin {

    @FXML
    private TextField campoemail;

    @FXML
    private PasswordField camposenha;

    @FXML
    private Button entrar_id;

    private Conta conta;

    @FXML
    void entrar(ActionEvent event) throws IOException {
        String email = campoemail.getText();
        String senha = camposenha.getText();

        // Validação de entrada
        if (email.isEmpty() || senha.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Todos os campos devem ser preenchidos.");
            return;
        }

        // Operação de banco de dados
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT u.id AS user_id, a.id AS account_id, a.balance FROM users u JOIN accounts a ON u.id = a.user_id WHERE u.email = ? AND u.password = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                statement.setString(2, senha);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId    = resultSet.getInt("user_id");
                        int accountId = resultSet.getInt("account_id");
                        double balance = resultSet.getDouble("balance");

                        Conta contaObj = new Conta();
                        contaObj.setId(accountId);
                        contaObj.setSaldo(balance);

                        // Inicia a sessão global para acesso em qualquer tela
                        Sessao.iniciarSessao(userId, accountId, email, email, balance);

                        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Login realizado com sucesso!");
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Telas/principal.fxml"));
                        Parent root = loader.load();
                        principalController controller = loader.getController();
                        controller.setConta(contaObj);
                        // Reutiliza a janela atual — não abre nova
                        Stage stage = (Stage) campoemail.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Homebroker");
                        stage.show();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erro", "Credenciais inválidas. Tente novamente.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao realizar login: " + e.getMessage());
        }
    }

    // Método utilitário para exibir alertas
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
