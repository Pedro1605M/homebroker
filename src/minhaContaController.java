import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class minhaContaController {

    @FXML
    private Label Acoes;

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
    void irpratelaMenu(ActionEvent event) throws IOException {
         FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("menu");
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
    }

}
