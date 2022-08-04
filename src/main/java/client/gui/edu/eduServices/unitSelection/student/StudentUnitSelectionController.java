package client.gui.edu.eduServices.unitSelection.student;

import client.gui.AlertMonitor;
import client.gui.EDU;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import shared.model.university.college.University;
import shared.model.university.lesson.Lesson;
import shared.model.user.student.Grade;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.*;

public class StudentUnitSelectionController implements Initializable {
    @FXML
    protected Button back;
    @FXML
    protected ComboBox<String> collegeBox;
    @FXML
    protected TableView<LessonTable> table1;
    @FXML
    protected TableColumn<LessonTable, String> nameColumn1;
    @FXML
    protected TableColumn<LessonTable, String> groupColumn1;
    @FXML
    protected TableColumn<LessonTable, String> gradeColumn1;
    @FXML
    protected TableColumn<LessonTable, String> examColumn1;
    @FXML
    protected TableColumn<LessonTable, CheckBox> markColumn1;
    @FXML
    protected TableColumn<LessonTable, Button> option1Column1;
    @FXML
    protected TableColumn<LessonTable, Button> option2Column1;
    @FXML
    protected RadioButton alphabeticButton;
    @FXML
    protected RadioButton examTimeButton;
    @FXML
    protected RadioButton gradeButton;
    @FXML
    protected Button show;
    @FXML
    protected TableView<LessonTable> table2;
    @FXML
    protected TableColumn<LessonTable, String> nameColumn2;
    @FXML
    protected TableColumn<LessonTable, String> groupColumn2;
    @FXML
    protected TableColumn<LessonTable, String> gradeColumn2;
    @FXML
    protected TableColumn<LessonTable, String> examColumn2;
    @FXML
    protected TableColumn<LessonTable, CheckBox> markColumn2;
    @FXML
    protected TableColumn<LessonTable, Button> option1Column2;
    @FXML
    protected TableColumn<LessonTable, Button> option2Column2;
    private ToggleGroup group;
    private Request request;
    private boolean stop;


    public void show(ActionEvent event) {
        if (isNull()) return;
        request = new Request(RequestType.GET_LESSONS_IN_UNIT_SELECTION);
        request.addData("college", collegeBox.getValue());
        request.addData("sort", setSort());
        request.addData("collegeCode", EDU.collegeCode);
        showResponse(request, 1);
    }

    public void back(ActionEvent event) {
        EDU.sceneSwitcher.switchScene(event, "mainPage");
    }

    private boolean isNull() {
        return collegeBox.getValue() == null || group.getSelectedToggle() == null;
    }

    private void makeTable1() {
        nameColumn1.setCellValueFactory(new PropertyValueFactory<>("name"));
        groupColumn1.setCellValueFactory(new PropertyValueFactory<>("group"));
        gradeColumn1.setCellValueFactory(new PropertyValueFactory<>("grade"));
        examColumn1.setCellValueFactory(new PropertyValueFactory<>("exam"));
        markColumn1.setCellValueFactory(new PropertyValueFactory<>("mark"));
        option1Column1.setCellValueFactory(new PropertyValueFactory<>("option1"));
        option2Column1.setCellValueFactory(new PropertyValueFactory<>("option2"));
    }

    private void makeTable2() {
        nameColumn2.setCellValueFactory(new PropertyValueFactory<>("name"));
        groupColumn2.setCellValueFactory(new PropertyValueFactory<>("group"));
        gradeColumn2.setCellValueFactory(new PropertyValueFactory<>("grade"));
        examColumn2.setCellValueFactory(new PropertyValueFactory<>("exam"));
        markColumn2.setCellValueFactory(new PropertyValueFactory<>("mark"));
        option1Column2.setCellValueFactory(new PropertyValueFactory<>("option1"));
        option2Column2.setCellValueFactory(new PropertyValueFactory<>("option2"));
    }

    private void addToggle() {
        group = new ToggleGroup();
        alphabeticButton.setToggleGroup(group);
        gradeButton.setToggleGroup(group);
        examTimeButton.setToggleGroup(group);
        group.selectToggle(alphabeticButton);
    }

    private void setBox() {
        collegeBox.getItems().addAll(University.getUniversity().getCollegeName());
    }

    private String setSort() {
        if (group.getSelectedToggle() == alphabeticButton) return "alphabetic";
        else if (group.getSelectedToggle() == examTimeButton) return "examTime";
        else return "grade";
    }

    private void showResponse(Request request, int table) {
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) showData(response.getData(), table);
        else {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        if (table == 1) updateTable1();
    }

    private void showData(HashMap<String, Object> data, int table) {
        List<LessonTable> lessonTableList = new ArrayList<>();
        Lesson lesson;
        for (int i = 0; i < data.size(); i++) {
            lesson = (Lesson) data.get("lessonRM" + i);
            if (lesson != null) {
                LessonTable lessonTable = new LessonTable(lesson.getLessonCode(),
                        lesson.getName(), lesson.getGroup(), lesson.getExamTime(),
                        lesson.getGrade(), true, true, this);
                lessonTableList.add(lessonTable);
                continue;
            }
            lesson = (Lesson) data.get("lessonNM" + i);
            if (lesson != null) {
                LessonTable lessonTable = new LessonTable(lesson.getLessonCode(),
                        lesson.getName(), lesson.getGroup(), lesson.getExamTime(),
                        lesson.getGrade(), false, true, this);
                lessonTableList.add(lessonTable);
                continue;
            }
            lesson = (Lesson) data.get("lessonRN" + i);
            if (lesson != null) {
                LessonTable lessonTable = new LessonTable(lesson.getLessonCode(),
                        lesson.getName(), lesson.getGroup(), lesson.getExamTime(),
                        lesson.getGrade(), true, false, this);
                lessonTableList.add(lessonTable);
                continue;
            }
            lesson = (Lesson) data.get("lessonNN" + i);
            if (lesson != null) {
                LessonTable lessonTable = new LessonTable(lesson.getLessonCode(),
                        lesson.getName(), lesson.getGroup(), lesson.getExamTime(),
                        lesson.getGrade(), false, false, this);
                lessonTableList.add(lessonTable);
            }
        }
        if (table == 1) {
            if (table1.getItems() != null)
                table1.getItems().clear();
            table1.getItems().addAll(lessonTableList);
        }
        else if (table == 2) {
            if (table2.getItems() != null)
                table2.getItems().clear();
            table2.getItems().addAll(lessonTableList);
        }
    }

    public void removeData(LessonTable lesson) {
        List<LessonTable> list = table1.getItems();
        list.remove(lesson);
        table1.getItems().clear();
        table1.getItems().addAll(list);
        list = table2.getItems();
        list.remove(lesson);
        table2.getItems().addAll(lesson);
    }

    public void addData(LessonTable lesson) {
        List<LessonTable> list = table1.getItems();
        list.add(lesson);
        table1.getItems().clear();
        table1.getItems().addAll(list);
        list = table2.getItems();
        list.add(lesson);
        table2.getItems().addAll(lesson);
    }

    private void updateTable1() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            showData(response.getData(), 1);
                        }
                        else {
                            String time = (String) response.getData("time");
                            if (time != null && time.equals("end")) {
                                AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getErrorMessage());
                                stop = true;
                            }
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        loop.start();
    }

    private void updateTable2() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        Request request = new Request(RequestType.SHOW_STUDENT_UNIT_SELECTION_PAGE);
                        request.addData("collegeCode", EDU.collegeCode);
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            showData(response.getData(), 2);
                        }
                        else {
                            String time = (String) response.getData("time");
                            if (time != null && time.equals("end")) {
                                AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getErrorMessage());
                                stop = true;
                            }
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        loop.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stop = false;
        addToggle();
        setBox();
        makeTable1();
        makeTable2();
        Request request = new Request(RequestType.SHOW_STUDENT_UNIT_SELECTION_PAGE);
        request.addData("collegeCode", EDU.collegeCode);
        showResponse(request, 2);
        updateTable2();
    }
}
