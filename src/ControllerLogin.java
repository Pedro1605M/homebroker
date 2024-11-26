import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerLogin {

    @FXML
    private TextField campoemail;

    @FXML
    private TextField camposenha;

    @FXML
    private Button entrar_id;

    @FXML
    void entrar(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(""));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("TELA PRINCIPAL");
            stage.show();
    }

}

