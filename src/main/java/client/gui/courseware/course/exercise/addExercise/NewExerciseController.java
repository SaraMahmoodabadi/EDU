package client.gui.courseware.course.exercise.addExercise;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class NewExerciseController {
    @FXML
    protected TextField nameField;
    @FXML
    protected ComboBox<?> openHour;
    @FXML
    protected ComboBox<?> openMinute;
    @FXML
    protected ComboBox<?> closeHour;
    @FXML
    protected ComboBox<?> closeMinute;
    @FXML
    protected ComboBox<?> uploadHour;
    @FXML
    protected ComboBox<?> uploadMinute;
    @FXML
    protected TextArea descriptionArea;
    @FXML
    protected RadioButton textType;
    @FXML
    protected RadioButton mediaType;
    @FXML
    protected TextField fileField;
    @FXML
    protected DatePicker openDate;
    @FXML
    protected DatePicker closeDate;
    @FXML
    protected DatePicker uploadDate;

    @FXML
    public void addFile(ActionEvent event) {

    }

    @FXML
    public void register(ActionEvent event) {

    }

    @FXML
    public void back(ActionEvent event) {

    }
}
