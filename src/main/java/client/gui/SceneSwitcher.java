package client.gui;

import client.network.ServerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SceneSwitcher {

    public void switchScene(ActionEvent actionEvent, String name) {
        String path = Config.getConfig(ConfigType.FXML_FILE).getProperty(String.class, name);
        try {
            Path url = Paths.get(path);
            FXMLLoader loader = new FXMLLoader(url.toUri().toURL());
            Parent root = loader.load();
            Scene scene = new Scene(root);
            ServerController.edu.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchScenes(Stage stage, String name) {
        String path = Config.getConfig(ConfigType.FXML_FILE).getProperty(String.class, name);
        try {
            Path url = Paths.get(path);
            FXMLLoader loader = new FXMLLoader(url.toUri().toURL());
            Parent root = loader.load();
            Scene scene = new Scene(root);
            ServerController.edu.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
