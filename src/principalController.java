import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class principalController {

    @FXML
    private TextField acoes;

    @FXML
    private Button cadastrar1;

    @FXML
    private Button cadastrar2;

    @FXML
    private Button depositar;

    @FXML
    private PasswordField deposito;

    @FXML
    private Button menu;

    @FXML
    private Label saldo;

    @FXML
    void depositar(ActionEvent event) {

    }

    @FXML
    void escolherAcao(ActionEvent event) {

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

}

