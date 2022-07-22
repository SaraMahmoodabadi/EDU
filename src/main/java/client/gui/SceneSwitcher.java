package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.io.IOException;

public class SceneSwitcher {

    public void switchScene(ActionEvent actionEvent, String name) {
        String path = Config.getConfig(ConfigType.FXML_FILE).getProperty(String.class, name);
        try {
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchScenes(Stage stage, String name) {
        String path = Config.getConfig(ConfigType.FXML_FILE).getProperty(String.class, name);
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
