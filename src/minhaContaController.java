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

    // Método para carregar os dados do usuário do banco de dados
    private void carregarDadosUsuario(int userId) {
        String sql = "SELECT name, email FROM users WHERE id = ?";
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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

    // Método chamado para inicializar a tela (após a criação da conta ou login)
    public void initialize() {
        // Suponha que você tenha o ID do usuário armazenado em algum lugar, como em uma sessão ou variável global
        int userId = 1; // Exemplo, você deve obter o ID real do usuário
        carregarDadosUsuario(userId); // Carregar e exibir os dados
    }
}
