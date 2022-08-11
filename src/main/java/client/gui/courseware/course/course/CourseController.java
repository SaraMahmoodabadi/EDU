package client.gui.courseware.course.course;

import client.gui.EDU;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import shared.model.user.UserType;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CourseController implements Initializable {
    @FXML
    protected TextField studentCodeField;
    @FXML
    protected RadioButton studentType;
    @FXML
    protected RadioButton teacherAssistantType;
    @FXML
    protected DatePicker date;
    @FXML
    protected TextArea events;
    @FXML
    protected VBox exercisesPane;
    @FXML
    protected VBox eduMaterialPane;
    @FXML
    protected Button addExerciseButton;
    @FXML
    protected Button addEduMaterialButton;
    @FXML
    protected Button addStudentButton;
    @FXML
    protected Rectangle rectangle;
    private boolean stop;
    private String eduMaterialName;
    private String exerciseName;

    @FXML
    public void addEduMaterial(ActionEvent event) {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("New Educational Material");
        inputDialog.setHeaderText("Input educational material name:");
        inputDialog.setContentText("Name:");
        Optional<String> result = inputDialog.showAndWait();
        result.ifPresent(e -> {eduMaterialName = e;});
        if (eduMaterialName != null) {

        }
    }

    @FXML
    public void addExercise(ActionEvent event) {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("New Exercise");
        inputDialog.setHeaderText("Input exercise name:");
        inputDialog.setContentText("Name:");
        Optional<String> result = inputDialog.showAndWait();
        result.ifPresent(e -> {exerciseName = e;});
        if (exerciseName != null) {

        }
    }

    @FXML
    public void addStudent(ActionEvent event) {

    }

    @FXML
    public void back(ActionEvent event) {
        stop = true;
    }

    private void hide() {
        rectangle.setVisible(false);
        studentCodeField.setVisible(false);
        addStudentButton.setDisable(true);
        addStudentButton.setVisible(false);
        studentType.setVisible(false);
        teacherAssistantType.setVisible(false);
    }

    private void updateData() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stop = false;
        if (EDU.userType != UserType.PROFESSOR) hide();
    }
}
