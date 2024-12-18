import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class historicoController {

    @FXML
    private Button menu;

    @FXML
    private Label mostraHistorico;

    @FXML
    private Label saldo;

    @FXML
    void irpratelaMenu(ActionEvent event) throws IOException {
         FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu");
            stage.show();
    }
    private List<Operacao> obterHistoricoOperacoes(int contaId) throws SQLException {
        List<Operacao> operacoes = new ArrayList<>();
        String sql = "SELECT operation_type, stock_symbol, quantity, price_per_stock, total_value " +
                     "FROM operations WHERE account_id = ? ORDER BY created_at DESC";
        
        try (Connection connection = DriverManager.getConnection(principalController.getDbUrl(), principalController.getDbUser(), principalController.getDbPassword());
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, contaId);  // ID da conta do usuário
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                String tipoOperacao = resultSet.getString("operation_type");
                String simboloAcao = resultSet.getString("stock_symbol");
                int quantidade = resultSet.getInt("quantity");
                double precoPorAcao = resultSet.getDouble("price_per_stock");
                double valorTotal = resultSet.getDouble("total_value");
                
               
             
            }
        }
        return operacoes;
    }
    


}
