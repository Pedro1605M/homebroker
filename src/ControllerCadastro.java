import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
    
    public class ControllerCadastro {
    
        @FXML
        private Button btnCadastro;
    
        @FXML
        private Button btnLogin;
    
        @FXML
        private TextField campoEmail;
    
        @FXML
        private TextField campoNasc;
    
        @FXML
        private TextField campoNome;
    
        @FXML
        private TextField campoSenha;
    
        @FXML
        void cadastrar(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(""));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("TELA PRINCIPAL");
            stage.show();
        }
    
        @FXML
        void irParaTelaLogin(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        }
    
    }
    

