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
        trocarTela(event, "historico.fxml", "Histórico");
    }

    @FXML
    void irparaMinhaConta(ActionEvent event) throws IOException {
        trocarTela(event, "Minha_conta.fxml", "Minha Conta");
    }

    @FXML
    void irparatelaPrincipal(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("principal.fxml"));
        Parent root = loader.load();

        // Restaura a conta a partir da sessão para não zerar o saldo
        if (Sessao.isLogado()) {
            Conta contaAtual = new Conta();
            contaAtual.setId(Sessao.getAccountId());
            contaAtual.setSaldo(Sessao.getSaldo());
            principalController controller = loader.getController();
            controller.setConta(contaAtual);
        }

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Homebroker");
        stage.show();
    }

    /** Reutiliza a janela atual trocando apenas a cena — sem abrir nova janela. */
    private void trocarTela(ActionEvent event, String fxml, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.show();
    }
}
