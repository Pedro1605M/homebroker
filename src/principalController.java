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


    @FXML
    private Label quant;

    private Conta conta; // Instância da classe Conta
    private StockApi StockApi;
    private int quantidade = 0;

    @FXML
    public void initialize() {
        conta = new Conta(); // Inicializa a conta quando o controlador é carregado
        atualizarSaldo(); // Atualiza o saldo na interface
        atualizarQuantidade(); // Exibe quantidade inicial
    }

    @FXML
    void depositar(ActionEvent event) {
        try {
            // Lê o valor do campo de texto
            String valorDeposito = deposito.getText();
            Double valor = Double.parseDouble(valorDeposito);

            // Adiciona o valor ao saldo da conta
            conta.adicionarSaldo(valor);

            // Atualiza o saldo exibido
            atualizarSaldo();
        } catch (NumberFormatException e) {
            // Caso o valor inserido não seja numérico, exibe mensagem de erro
            labelSaldo.setText("Erro: Valor inválido!");
        }
    }

    private void atualizarSaldo() {
        // Exibe o saldo atual formatado
        labelSaldo.setText(String.format("%.2f", conta.getSaldo()));
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
        quantidade++; // Incrementa a quantidade
        atualizarQuantidade(); // Atualiza a exibição
    }

    @FXML
    void menos(ActionEvent event) {
        if (quantidade > 0) { // Garante que a quantidade não fique negativa
            quantidade--; // Decrementa a quantidade
        }
        atualizarQuantidade(); // Atualiza a exibição
    }

    private void atualizarQuantidade() {
        // Atualiza a exibição da quantidade
        quant.setText("" + quantidade);
    }

    @FXML
    void vender(ActionEvent event) {

    }
    @FXML
    void comprar(ActionEvent event) {
        String siglaAPI = acoes.getText();
        StockApi.fetchAndStorePrice(siglaAPI);
        Double precoAcao = StockApi.getLastPrice();
        Double precoCompra = precoAcao * quantidade;
        if(precoCompra < conta.getSaldo()){
            
        }
    }

}

