import java.io.IOException;
import java.util.concurrent.atomic.DoubleAccumulator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class principalController {

    @FXML
    private TextField acoes;

    @FXML
    private Button btnComprar;

    @FXML
    private Button btnMais;

    @FXML
    private Button btnMenos;

    @FXML
    private Button depositar;

    @FXML
    private TextField deposito;

    @FXML
    private Button menu;

    @FXML
    private Label labelSaldo;

    Conta conta = new Conta();
    Double saldo;


    @FXML
    void depositar(ActionEvent event) {
        String valorDeposito = deposito.getText();
        Double valorDeposito2 = Double.parseDouble(valorDeposito);
        saldo = conta.getSaldo();
        Double novoSaldo = saldo + valorDeposito2;
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

    @FXML
    void mais(ActionEvent event) {

    }

    @FXML
    void menos(ActionEvent event) {

    }

    @FXML
    void vender(ActionEvent event) {

    }
    @FXML
    void comprar(ActionEvent event) {

    }

}

