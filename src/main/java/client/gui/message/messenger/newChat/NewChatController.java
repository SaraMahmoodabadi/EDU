package client.gui.message.messenger.newChat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class NewChatController {
    @FXML
    protected Button back;
    @FXML
    protected TableView<?> table;
    @FXML
    protected TableColumn<?, ?> firstnameColumn;
    @FXML
    protected TableColumn<?, ?> lastnameColumn;
    @FXML
    protected TableColumn<?, ?> selectColumn;
    @FXML
    protected CheckBox selectAllCheckBox;
    @FXML
    protected TextArea messageArea;
    @FXML
    protected Button sendButton;
    @FXML
    protected TextField userCodeField;
    @FXML
    protected Button requestButton;
    @FXML
    protected RadioButton professorRadioButton;
    @FXML
    protected RadioButton studentRadioButton;
    @FXML
    protected Button sendMediaButton;


    @FXML
    public void selectAll(ActionEvent event) {

    }

    @FXML
    public void sendMedia(ActionEvent event) {

    }

    @FXML
    public void sendMessage(ActionEvent event) {

    }

    @FXML
    public void sendRequest(ActionEvent event) {

    }

    @FXML
    public void back(ActionEvent event) {

    }
}
