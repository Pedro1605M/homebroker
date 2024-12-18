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

    private static final String DB_URL = "jdbc:mysql://pd2ub.h.filess.io:3307/homebroker_substance";
    private static final String DB_USER = "homebroker_substance";
    private static final String DB_PASSWORD = "675ff3244d016e1bca2bde49e09e4a8c35396823";

    @FXML
    private TextField campoemail;

    @FXML
    private PasswordField camposenha;

    @FXML
    private Button entrar_id;

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
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                statement.setString(2, senha);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Sessao.setNomeUsuario(resultSet.getString("name"));
                        Sessao.setIdUsuario(resultSet.getInt("id"));
                        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Login realizado com sucesso!");
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("principal.fxml"));
                        Parent root = loader.load();
                        principalController controller = loader.getController();
                        Conta conta = new Conta();
                        controller.setConta(conta);
                        Stage stage = new Stage();
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
