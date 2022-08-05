package client.gui.message.mohseni;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class MohseniController implements Initializable {
    @FXML
    private Button back;
    @FXML
    private TextArea messageArea;
    @FXML
    private Button sendMediaButton;
    @FXML
    private Button sendButton;
    @FXML
    private ComboBox<?> gradeBox;
    @FXML
    private ComboBox<?> yearBox;
    @FXML
    private ComboBox<?> collegeBox;
    @FXML
    private TableView<?> table;
    @FXML
    private TableColumn<?, ?> firstnameColumn;
    @FXML
    private TableColumn<?, ?> lastnameColumn;
    @FXML
    private TableColumn<?, ?> studentCodeColumn;
    @FXML
    private TableColumn<?, ?> gradeColumn;
    @FXML
    private TextField studentCodeField;


    @FXML
    void selectStudent(MouseEvent event) {

    }

    @FXML
    void sendMedia(ActionEvent event) {

    }

    @FXML
    void sendMessage(ActionEvent event) {

    }

    @FXML
    void back(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
