import java.io.IOException;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControllerCadastro {

    @FXML
    private Button cadastrar_id;

    @FXML
    private TextField campoEmail;

    @FXML
    private TextField campoNome;

    @FXML
    private PasswordField campoSenha;

    @FXML
    private Button login;


    @FXML
    void cadastrar(ActionEvent event) throws IOException {

        
        String name = campoNome.getText();
        String email = campoEmail.getText();
        String password = campoSenha.getText();

        // Validação de entrada
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Todos os campos devem ser preenchidos.");
            return;
        }

        // Operação no banco de dados
        try (Connection connection = DatabaseManager.getConnection()) {
            String sqlInsertUser = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            String sqlInsertAccount = "INSERT INTO accounts (user_id, balance) VALUES (?, ?)";

            // Inserir usuário e obter o ID gerado
            try (PreparedStatement statement = connection.prepareStatement(sqlInsertUser, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, password);

                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    // Obter o ID gerado automaticamente
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int userId = generatedKeys.getInt(1); // Obtém o ID gerado

                            // Inserir na tabela accounts com o user_id
                            try (PreparedStatement accountStatement = connection.prepareStatement(sqlInsertAccount)) {
                                accountStatement.setInt(1, userId);
                                accountStatement.setDouble(2, 0.0); // Define o saldo inicial como 0
                                accountStatement.executeUpdate();
                            }

                            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário cadastrado com sucesso!");
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                            Parent root = loader.load();
                            // Reutiliza a janela atual — não abre nova
                            Stage stage = (Stage) cadastrar_id.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Login");
                            stage.show();
                        }
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", "Nenhum registro foi inserido.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao cadastrar usuário: " + e.getMessage());
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

    @FXML
    void irParaTelaLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Login");
        stage.show();
    }
}
