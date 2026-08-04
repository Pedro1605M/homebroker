import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class historicoController {

    @FXML private Button menu;
    @FXML private Label mostraHistorico;
    @FXML private Label saldo;

    // Colunas da TableView (conectadas via fx:id no FXML)
    @FXML private TableView<OperacaoRow> historicoTable;
    @FXML private TableColumn<OperacaoRow, String>  tipoOperacaoCol;
    @FXML private TableColumn<OperacaoRow, String>  simboloAcaoCol;
    @FXML private TableColumn<OperacaoRow, Integer> quantidadeCol;
    @FXML private TableColumn<OperacaoRow, Double>  precoPorAcaoCol;
    @FXML private TableColumn<OperacaoRow, Double>  valorTotalCol;

    @FXML
    public void initialize() {
        configurarColunas();
        carregarHistorico();
    }

    /** Conecta cada coluna à propriedade correspondente de OperacaoRow. */
    private void configurarColunas() {
        tipoOperacaoCol.setCellValueFactory(cell -> cell.getValue().tipoProperty());
        simboloAcaoCol .setCellValueFactory(cell -> cell.getValue().simboloProperty());
        quantidadeCol  .setCellValueFactory(cell -> cell.getValue().quantidadeProperty().asObject());
        precoPorAcaoCol.setCellValueFactory(cell -> cell.getValue().precoUnitarioProperty().asObject());
        valorTotalCol  .setCellValueFactory(cell -> cell.getValue().valorTotalProperty().asObject());

        // Formata os valores monetários com R$
        precoPorAcaoCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : String.format("R$ %.2f", val));
            }
        });
        valorTotalCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : String.format("R$ %.2f", val));
            }
        });
        // Cor diferente para COMPRA (verde) e VENDA (vermelho)
        tipoOperacaoCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); setStyle(""); return; }
                setText(val.equals("BUY") ? "COMPRA" : "VENDA");
                setStyle(val.equals("BUY")
                    ? "-fx-text-fill: #00e676; -fx-font-weight: bold;"
                    : "-fx-text-fill: #ff5252; -fx-font-weight: bold;");
            }
        });
    }

    /** Lê as operações do banco e popula a tabela. */
    private void carregarHistorico() {
        // Tenta pegar da Sessao primeiro; fallback para 0
        int contaId = Sessao.getAccountId() != null ? Sessao.getAccountId() : 0;

        if (contaId <= 0) {
            mostraHistorico.setVisible(true);
            historicoTable.setVisible(false);
            return;
        }

        ObservableList<OperacaoRow> dados = FXCollections.observableArrayList();
        String sql = "SELECT operation_type, stock_symbol, quantity, price_per_stock, total_value " +
                     "FROM operations WHERE account_id = ? ORDER BY created_at DESC";

        try {
            Connection conn = DatabaseManager.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, contaId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    dados.add(new OperacaoRow(
                        rs.getString("operation_type"),
                        rs.getString("stock_symbol"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_per_stock"),
                        rs.getDouble("total_value")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dados.isEmpty()) {
            mostraHistorico.setVisible(true);
            historicoTable.setVisible(false);
        } else {
            mostraHistorico.setVisible(false);
            historicoTable.setVisible(true);
            historicoTable.setItems(dados);
        }

        // Exibe o saldo atual se disponível
        double saldoAtual = Sessao.getSaldo();
        if (saldo != null) {
            saldo.setText(String.format("R$ %.2f", saldoAtual));
        }
    }

    @FXML
    void irpratelaMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Menu");
        stage.show();
    }

    // ── Classe interna: linha da tabela ─────────────────────────────────────────
    public static class OperacaoRow {
        private final SimpleStringProperty  tipo;
        private final SimpleStringProperty  simbolo;
        private final SimpleIntegerProperty quantidade;
        private final SimpleDoubleProperty  precoUnitario;
        private final SimpleDoubleProperty  valorTotal;

        public OperacaoRow(String tipo, String simbolo, int quantidade,
                           double precoUnitario, double valorTotal) {
            this.tipo          = new SimpleStringProperty(tipo);
            this.simbolo       = new SimpleStringProperty(simbolo);
            this.quantidade    = new SimpleIntegerProperty(quantidade);
            this.precoUnitario = new SimpleDoubleProperty(precoUnitario);
            this.valorTotal    = new SimpleDoubleProperty(valorTotal);
        }

        public SimpleStringProperty  tipoProperty()         { return tipo; }
        public SimpleStringProperty  simboloProperty()      { return simbolo; }
        public SimpleIntegerProperty quantidadeProperty()   { return quantidade; }
        public SimpleDoubleProperty  precoUnitarioProperty(){ return precoUnitario; }
        public SimpleDoubleProperty  valorTotalProperty()   { return valorTotal; }
    }
}
