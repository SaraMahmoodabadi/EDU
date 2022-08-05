package client.gui.message.messages;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class MessagesController implements Initializable {
    @FXML
    protected TextField messageField;
    @FXML
    protected Button accept;
    @FXML
    protected Button reject;
    @FXML
    protected Pane messagePane;
    @FXML
    protected Pane informationPane;
    @FXML
    protected Label nameLabel;
    @FXML
    protected Button back;
    @FXML
    protected AnchorPane AllMessagesPane;

    @FXML
    public void accept(ActionEvent event) {

    }

    @FXML
    public void back(ActionEvent event) {

    }

    @FXML
    public void reject(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
