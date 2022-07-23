package client.gui;

import javafx.scene.control.Alert;

public class AlertMonitor {

    public static void showAlert(Alert.AlertType type, String errorMessage) {
        Alert alert = new Alert(type);
        alert.setContentText(errorMessage);
        alert.show();
    }
}
