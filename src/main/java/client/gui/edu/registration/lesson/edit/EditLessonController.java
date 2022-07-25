package client.gui.edu.registration.lesson.edit;

import client.gui.AlertMonitor;
import client.gui.EDU;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import shared.model.university.lesson.Day;
import shared.model.university.lesson.Group;
import shared.model.university.lesson.Lesson;
import shared.model.user.student.Grade;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditLessonController implements Initializable {


    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle bigRectangle;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Rectangle rectangle3;
    @FXML
    protected Text addText;
    @FXML
    protected Text editText;
    @FXML
    protected Text removeText;
    @FXML
    protected Text nameText;
    @FXML
    protected TextField nameField;
    @FXML
    protected Text codeText1;
    @FXML
    protected TextField codeField1;
    @FXML
    protected Text gradeText;
    @FXML
    protected ComboBox<String> gradeBox;
    @FXML
    protected Text unitText;
    @FXML
    protected ComboBox<String> unitBox;
    @FXML
    protected Text prerequisiteText;
    @FXML
    protected Text Days1;
    @FXML
    protected CheckBox saturday1;
    @FXML
    protected CheckBox sunday1;
    @FXML
    protected CheckBox monday1;
    @FXML
    protected CheckBox tuesday1;
    @FXML
    protected CheckBox wednesday1;
    @FXML
    protected CheckBox thursday1;
    @FXML
    protected Text timeText1;
    @FXML
    protected Text examText1;
    @FXML
    protected DatePicker date1;
    @FXML
    protected TextField prerequisiteList;
    @FXML
    protected Button plusButton1;
    @FXML
    protected ImageView plusImage1;
    @FXML
    protected Text needText;
    @FXML
    protected TextField needList;
    @FXML
    protected Button plusButton2;
    @FXML
    protected ImageView plusImage2;
    @FXML
    protected Text professorText1;
    @FXML
    protected TextField professorField1;
    @FXML
    protected Text capacityText1;
    @FXML
    protected TextField capacityField1;
    @FXML
    protected Button register;
    @FXML
    protected Text codeText2;
    @FXML
    protected TextField codeField2;
    @FXML
    protected Line line1;
    @FXML
    protected Text groupText1;
    @FXML
    protected Text professorText2;
    @FXML
    protected TextField professorField2;
    @FXML
    protected Text capacityText2;
    @FXML
    protected TextField capacityField2;
    @FXML
    protected Line line2;
    @FXML
    protected Text TimeOfClassText;
    @FXML
    protected Text Days2;
    @FXML
    protected CheckBox saturday2;
    @FXML
    protected CheckBox sunday2;
    @FXML
    protected CheckBox monday2;
    @FXML
    protected CheckBox tuesday2;
    @FXML
    protected CheckBox wednesday2;
    @FXML
    protected CheckBox thursday2;
    @FXML
    protected Text timeText2;
    @FXML
    protected Line line3;
    @FXML
    protected Text examText2;
    @FXML
    protected DatePicker date2;
    @FXML
    protected Button edit;
    @FXML
    protected Text codeText3;
    @FXML
    protected TextField codeField3;
    @FXML
    protected Text groupText2;
    @FXML
    protected TextField groupField;
    @FXML
    protected Button removeLesson;
    @FXML
    protected Button removeGroup;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    @FXML
    protected TextField hour1;
    @FXML
    protected TextField minute1;
    @FXML
    protected TextField hour2;
    @FXML
    protected TextField minute2;
    @FXML
    protected TextField hour3;
    @FXML
    protected TextField minute3;
    @FXML
    protected TextField hour4;
    @FXML
    protected TextField minute4;
    @FXML
    protected TextField hour5;
    @FXML
    protected TextField minute5;
    @FXML
    protected TextField hour6;
    @FXML
    protected TextField minute6;
    private List<String> prerequisites;
    private List<String> theNeeds;


    public void register(ActionEvent actionEvent) {
        Request request = new Request(RequestType.REGISTER_NEW_LESSON);
        Lesson lesson = makeNewLesson();
        if (lesson == null) return;
        request.addData("lesson", lesson);
        showRequestResult(request);
    }

    public void addPrerequisite(ActionEvent actionEvent) {
        prerequisites.add(prerequisiteList.getText());
    }

    public void addNeed(ActionEvent actionEvent) {
        theNeeds.add(needList.getText());
    }

    public void edit(ActionEvent actionEvent) {
        int capacity = getCapacity(capacityField2.getText());
        if (capacity == -1) return;
        Group group = new Group(codeField2.getText(), capacity, professorField2.getText());
        List<Day> days = setEditedPlan();
        String examTime = date2.getValue() + "-" + hour6.getText() + ":" + minute6.getText();
        String classTime = hour4.getText() + ":" + minute4.getText() +
                "-" + hour5.getText() + ":" + minute5.getText();
        Request request = new Request(RequestType.EDIT_LESSON);
        request.addData("lesson", codeField2.getText());
        request.addData("group", group);
        request.addData("days", days);
        request.addData("examTime", examTime);
        request.addData("classTime", classTime);
        showRequestResult(request);
    }

    public void removeLesson(ActionEvent actionEvent) {
        Request request = new Request(RequestType.REMOVE_LESSON);
        request.addData("lessonCode", codeField3.getText());
        showRequestResult(request);
    }

    public void removeGroup(ActionEvent actionEvent) {
        Request request = new Request(RequestType.REMOVE_LESSON_GROUP);
        request.addData("lessonCode", codeField3.getText());
        request.addData("group", groupField.getText());
        showRequestResult(request);
    }

    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "lessonListPage");
    }

    private void showRequestResult(Request request) {
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        }
    }

    private void completeBoxes() {
        gradeBox.getItems().add(Grade.UNDERGRADUATE.toString());
        gradeBox.getItems().add(Grade.MASTER.toString());
        gradeBox.getItems().add(Grade.PHD.toString());

        unitBox.getItems().addAll("1", "2", "3", "4");
    }

    private List<Day> setInitialPlan() {
        List<Day> plan = new ArrayList<>();
        if (saturday1.isSelected()) {
            plan.add(Day.SATURDAY);
        }
        if (sunday1.isSelected()) {
            plan.add(Day.SUNDAY);
        }
        if (monday1.isSelected()) {
            plan.add(Day.MONDAY);
        }
        if (tuesday1.isSelected()) {
            plan.add(Day.TUESDAY);
        }
        if (wednesday1.isSelected()) {
            plan.add(Day.WEDNESDAY);
        }
        if (thursday1.isSelected()) {
            plan.add(Day.THURSDAY);
        }
        return plan;
    }

    private List<Day> setEditedPlan() {
        List<Day> plan = new ArrayList<>();
        if (saturday2.isSelected()) {
            plan.add(Day.SATURDAY);
        }
        if (sunday2.isSelected()) {
            plan.add(Day.SUNDAY);
        }
        if (monday2.isSelected()) {
            plan.add(Day.MONDAY);
        }
        if (tuesday2.isSelected()) {
            plan.add(Day.TUESDAY);
        }
        if (wednesday2.isSelected()) {
            plan.add(Day.WEDNESDAY);
        }
        if (thursday2.isSelected()) {
            plan.add(Day.THURSDAY);
        }
        return plan;
    }

    private Lesson makeNewLesson() {
        int capacity = getCapacity(capacityField1.getText());
        if (capacity == -1) return null;
        String examTime = date1.getValue() + "-" + hour3.getText() + ":" + minute3.getText();
        String classTime = hour1.getText() + ":" + minute1.getText() +
                "-" + hour2.getText() + ":" + minute2.getText();
        List<Day> days = setInitialPlan();
        if (isNull(days, examTime, classTime)) return null;
        int unit = Integer.parseInt(unitBox.getValue());
        Grade grade = Grade.valueOf(gradeBox.getValue());
        return new Lesson(nameField.getText(), codeField1.getText(),
                EDU.collegeCode, null, unit, grade, prerequisites,
                theNeeds, capacity, days, classTime, examTime, professorField1.getText());
    }

    private boolean isNull(List<Day> days, String examTime, String classTime) {
        return nameField.getText() == null || codeField1.getText() == null ||
                unitBox.getValue() == null || gradeBox.getValue() == null || days == null ||
                classTime == null || examTime == null || professorField1.getText() == null;
    }

    private int getCapacity(String capacityField) {
        int capacity = -1;
        try {
            capacity = Integer.parseInt(capacityField);
            if (capacity < 0) capacity = -1;
        } catch (NumberFormatException e) {
            String errorMessage = Config.getConfig(ConfigType.GUI_TEXT).
                    getProperty(String.class, "numberError");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, errorMessage);
        }
        return capacity;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prerequisites = new ArrayList<>();
        theNeeds = new ArrayList<>();
        completeBoxes();
    }
}
