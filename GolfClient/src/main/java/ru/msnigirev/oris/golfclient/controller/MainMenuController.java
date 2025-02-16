package ru.msnigirev.oris.golfclient.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.msnigirev.oris.golfclient.server.NetworkClient;

import java.io.IOException;

public class MainMenuController {
    @FXML
    private TextField nameField;
    @FXML
    private void onPlayClicked(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/game.fxml"));
            Parent root = loader.load();

            GameController controller = loader.getController();
            String host = "127.0.0.1";
            int port = 9999;
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) playerName = "player";

            NetworkClient networkClient = new NetworkClient();
            networkClient.connect(host, port, playerName);

            controller.setNetworkClient(networkClient);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Гольф");
            stage.show();

            Node  source = (Node)  actionEvent.getSource();
            Stage thisStage  = (Stage) source.getScene().getWindow();
            thisStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    /*
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        Parent root = loader.load();

        GameController controller = loader.getController();
        String host = "127.0.0.1";
        int port = 9999;
        String playerName = "PlayerFX";

        networkClient = new NetworkClient();
        networkClient.connect(host, port, playerName);

        controller.setNetworkClient(networkClient);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Гольф");
        primaryStage.setScene(scene);
        primaryStage.show();
     */
