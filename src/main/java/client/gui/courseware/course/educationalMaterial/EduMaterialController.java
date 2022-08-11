package client.gui.courseware.course.educationalMaterial;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class EduMaterialController implements Initializable {
    @FXML
    protected VBox itemsPane;
    @FXML
    protected Text eduMaterialName;
    @FXML
    protected Button addTextButton;
    @FXML
    protected Button deleteMaterial;
    @FXML
    protected Button addMediaButton;
    @FXML
    protected TextArea textArea;
    @FXML
    protected TextArea mediaAddress;
    @FXML
    protected Button registerTextButton;
    @FXML
    protected Button registerMediaButton;

    @FXML
    public void addMedia(ActionEvent event) {

    }

    @FXML
    public void addText(ActionEvent event) {

    }

    @FXML
    public void deleteEduMaterial(ActionEvent event) {

    }

    @FXML
    public void registerMedia(ActionEvent event) {

    }

    @FXML
    public void registerText(ActionEvent event) {

    }

    @FXML
    public void back(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
