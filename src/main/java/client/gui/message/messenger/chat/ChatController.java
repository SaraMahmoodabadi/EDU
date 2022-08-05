package client.gui.message.messenger.chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    protected TextField textMessage;
    @FXML
    protected ImageView profilePicture;
    @FXML
    protected Label nameLabel;
    @FXML
    protected Button mediaButton;
    @FXML
    protected Button back;
    @FXML
    protected AnchorPane allChatsPane;
    @FXML
    protected Button newChatButton;
    @FXML
    protected AnchorPane chatPane;

    @FXML
    public void sendMessage(KeyEvent keyEvent) {
    }

    @FXML
    public void chooseMedia(ActionEvent event) {

    }

    @FXML
    public void showNewChatPage(ActionEvent event) {

    }

    @FXML
    public void back(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
