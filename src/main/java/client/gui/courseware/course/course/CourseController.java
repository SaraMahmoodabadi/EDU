package client.gui.courseware.course.course;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.ServerController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import shared.model.courseware.educationalMaterial.EducationalMaterial;
import shared.model.courseware.exercise.Exercise;
import shared.model.user.UserType;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.Map;
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
    protected RadioButton openType;
    @FXML
    protected RadioButton closeType;
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
    private String courseCode;
    private boolean isAssistant;
    private ToggleGroup userType;
    private ToggleGroup timeType;


    @FXML
    public void addEduMaterial(ActionEvent event) {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("New Educational Material");
        inputDialog.setHeaderText("Input educational material name:");
        inputDialog.setContentText("Name:");
        Optional<String> result = inputDialog.showAndWait();
        result.ifPresent(e -> {eduMaterialName = e;});
        if (eduMaterialName != null) {
            Request request = new Request(RequestType.CREATE_EDUCATIONAL_MATERIAL);
            request.addData("courseCode", courseCode);
            request.addData("name", eduMaterialName);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.OK) {
                request = new Request(RequestType.SHOW_EDUCATIONAL_MATERIAL);
                request.addData("courseCode", this.courseCode);
                request.addData("educationalMaterialCode", courseCode + "-" + eduMaterialName);
                ServerController.request = request;
                stop = true;
                EDU.sceneSwitcher.switchScene(event, "eduMaterial");
            }
            else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
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
            Request request = new Request(RequestType.CREATE_EXERCISE);
            request.addData("courseCode", courseCode);
            request.addData("name", exerciseName);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.OK) {
                stop = true;
                ServerController.request = request;
                EDU.sceneSwitcher.switchScene(event, "addExercise");
            }
            else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
    }

    @FXML
    public void addStudent(ActionEvent event) {
        if (studentCodeField.getText() == null || userType.getSelectedToggle() == null) return;
        Request request = new Request(RequestType.ADD_STUDENT_TO_COURSE);
        request.addData("studentType", getSelectedUser());
        request.addData("studentCode", studentCodeField.getText());
        request.addData("courseCode", this.courseCode);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK)
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }

    @FXML
    public void back(ActionEvent event) {
        stop = true;
        EDU.sceneSwitcher.switchScene(new ActionEvent(), "courseware");
    }

    private void showEduMaterials(Map<String, Object> data) {
        eduMaterialPane.getChildren().clear();
        int size = (int) data.get("educationalMaterialSize");
        int t = 0;
        for (int i = 0; i < size; i++) {
            EducationalMaterial educationalMaterial = (EducationalMaterial) data.get("educationalMaterial" + i);
            if (educationalMaterial == null) continue;
            t++;
            EduMaterialLabel eduMaterialLabel =
                    new EduMaterialLabel(this.courseCode,
                            educationalMaterial.getEducationalMaterialCode(), educationalMaterial.getName());
            if (eduMaterialPane.getPrefHeight() < 90 * t)
                eduMaterialPane.setPrefHeight(90 * t);
            eduMaterialPane.getChildren().add(eduMaterialLabel);
        }
    }

    private void showExercises(Map<String, Object> data) {
        exercisesPane.getChildren().clear();
        int size = (int) data.get("exerciseSize");
        int t = 0;
        for (int i = 0; i < size; i++) {
            Exercise exercise = (Exercise) data.get("exercise" + i);
            if (exercise == null) continue;
            t++;
            ExerciseLabel exerciseLabel =
                    new ExerciseLabel(this.courseCode,
                            exercise.getExerciseCode(), exercise.getName());
            if (exercisesPane.getPrefHeight() < 90 * t)
                exercisesPane.setPrefHeight(90 * t);
            exercisesPane.getChildren().add(exerciseLabel);
        }
    }

    private void showCalendar(Map<String, Object> data) {
        events.clear();
        StringBuilder events = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            String event = (String) data.get("event" + i);
            events.append(event).append("\n");
        }
        this.events.setText(events.toString());
    }

    private void hide() {
        rectangle.setVisible(false);
        studentCodeField.setVisible(false);
        addStudentButton.setDisable(true);
        addStudentButton.setVisible(false);
        studentType.setVisible(false);
        teacherAssistantType.setVisible(false);
        addEduMaterialButton.setDisable(true);
        addEduMaterialButton.setVisible(false);
        addExerciseButton.setDisable(true);
        addExerciseButton.setVisible(true);
    }

    private void makeToggleGroup() {
        userType = new ToggleGroup();
        studentType.setToggleGroup(userType);
        teacherAssistantType.setToggleGroup(userType);
        userType.selectToggle(studentType);
        studentType.setSelected(true);
        timeType = new ToggleGroup();
        openType.setToggleGroup(timeType);
        closeType.setToggleGroup(timeType);
        timeType.selectToggle(openType);
        openType.setSelected(true);
    }

    private String getSelectedUser() {
        if (userType.getSelectedToggle() == studentType) return "student";
        else return "teacherAssistant";
    }

    private String getSelectedTime() {
        if (timeType.getSelectedToggle() == openType) return "openingTime";
        else return "closingTime";
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        Request request = new Request(RequestType.SHOW_COURSE);
                        request.addData("courseCode", this.courseCode);
                        request.addData("time", getSelectedTime());
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            showEduMaterials(response.getData());
                            showExercises(response.getData());
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        loop.start();
    }

    private void updateCalendar() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    if (date.getValue() == null) continue;
                    Platform.runLater(() -> {
                        Request request = new Request(RequestType.SHOW_COURSE_CALENDAR);
                        request.addData("courseCode", this.courseCode);
                        request.addData("date", date.getValue().toString());
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            showCalendar(response.getData());
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        loop.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isAssistant = false;
        stop = false;
        if (EDU.userType == UserType.PROFESSOR) makeToggleGroup();
        else hide();
        Request request = ServerController.request;
        this.courseCode = (String) request.getData("courseCode");
        request.addData("time", getSelectedTime());
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            this.isAssistant = (boolean) response.getData("isAssistant");
            showEduMaterials(response.getData());
            showExercises(response.getData());
        }
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        updateData();
        updateCalendar();
    }

    class EduMaterialLabel extends Label {
        String courseCode;
        String eduMaterialCode;
        String name;

        public EduMaterialLabel(String courseCode, String eduMaterialCode, String name) {
            this.courseCode = courseCode;
            this.eduMaterialCode = eduMaterialCode;
            this.name = name;
            makeLabel(name);
        }

        private void makeLabel(String data) {
            this.setText(data);
            this.setPrefWidth(500);
            this.setPrefHeight(75);
            Font font = Font.font("System", FontWeight.BOLD,15);
            this.setFont(font);
            this.setTextFill(Color.valueOf("#b151b8"));
            this.setStyle(String.valueOf(Color.valueOf("#ffd100")));
            this.setBackground(new Background(new BackgroundFill
                    (Color.valueOf("#ffd100"), CornerRadii.EMPTY, Insets.EMPTY)));
            addActionEvent(this);
        }

        private void addActionEvent(Label label) {
            label.setOnMouseClicked(event -> {
                Request request = new Request(RequestType.SHOW_EDUCATIONAL_MATERIAL);
                request.addData("courseCode", this.courseCode);
                request.addData("educationalMaterialCode", this.eduMaterialCode);
                ServerController.request = request;
                CourseController.this.stop = true;
                EDU.sceneSwitcher.switchScene(new ActionEvent(), "eduMaterial");
            });
        }
    }

    class ExerciseLabel extends Label {
        String courseCode;
        String exerciseCode;
        String name;

        public ExerciseLabel(String courseCode, String exerciseCode, String name) {
            this.courseCode = courseCode;
            this.exerciseCode = exerciseCode;
            this.name = name;
            makeLabel(name);
        }

        private void makeLabel(String data) {
            this.setText(data);
            this.setPrefWidth(500);
            this.setPrefHeight(75);
            Font font = Font.font("System", FontWeight.BOLD,15);
            this.setFont(font);
            this.setTextFill(Color.valueOf("#b151b8"));
            this.setStyle(String.valueOf(Color.valueOf("#ffd100")));
            this.setBackground(new Background(new BackgroundFill
                    (Color.valueOf("#ffd100"), CornerRadii.EMPTY, Insets.EMPTY)));
            addActionEvent(this);
        }

        private void addActionEvent(Label label) {
            label.setOnMouseClicked(event -> {
                Request request = new Request(RequestType.SHOW_EXERCISE);
                request.addData("courseCode", this.courseCode);
                request.addData("exerciseCode", this.exerciseCode);
                ServerController.request = request;
                CourseController.this.stop = true;
                if (EDU.userType == UserType.STUDENT && !isAssistant)
                    EDU.sceneSwitcher.switchScene(new ActionEvent(), "studentExercise");
                else EDU.sceneSwitcher.switchScene(new ActionEvent(), "professorExercise");
            });
        }
    }
}
