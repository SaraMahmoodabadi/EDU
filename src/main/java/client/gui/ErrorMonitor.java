package client.gui;

import javafx.scene.control.Alert;

public class ErrorMonitor {

    public static void showError(Alert.AlertType type, String errorMessage) {
        Alert alert = new Alert(type);
        alert.setContentText(errorMessage);
        alert.show();
    }
}
