package Classes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

     @Override
     public void start(Stage primaryStage) throws Exception {
         Parent root = FXMLLoader.load(getClass().getResource("/Telas/Cadastro.fxml"));
         primaryStage.setTitle("Home Broker");
         primaryStage.setScene(new Scene(root));
         primaryStage.show();
     }

     /** Chamado automaticamente quando o app fecha — fecha a conexão do banco. */
     @Override
     public void stop() {
         DatabaseManager.closeConnection();
     }

     public static void main(String[] args) {
         DatabaseManager.initDatabase();
         launch(args);
     }
 }