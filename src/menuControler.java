import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class menuControler {

    @FXML
    private Button btnMinhaConta;

    @FXML
    private Button btnPrincipal;

    @FXML
    private Button btnhistorico;

    @FXML
    void irparaHistorico(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("historico.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Historico");
        stage.show();
    }

    @FXML
    void irparaMinhaConta(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Minha_conta.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Minha conta");
        stage.show();
    }

    @FXML
    void irparatelaPrincipal(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("principal.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Homebroker");
        stage.show();
    }

}



