package client.gui;

import client.network.ServerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SceneSwitcher {

    public void switchScene(ActionEvent actionEvent, String name) {
        if (isValid(name)) {
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

    public void switchScenes(Stage stage, String name) {
        if (isValid(name)) {
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

    private boolean isValid(String name) {
        if (EDU.isOnline) return true;
        else {
            if (name.equals("loginPage") || name.equals("mainPage") || name.equals("weeklyPlanPage") ||
                    name.equals("examListPage") || name.equals("eduStatusPage") || name.equals("profilePage") ||
                    name.equals("chat")) return true;
        }
        String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "offlineError");
        AlertMonitor.showAlert(Alert.AlertType.ERROR, error);
        return false;
    }
}
