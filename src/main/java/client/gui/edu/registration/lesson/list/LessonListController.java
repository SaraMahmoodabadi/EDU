package client.gui.edu.registration.lesson.list;

import client.gui.EDU;
import client.gui.ErrorMonitor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import shared.model.university.college.University;
import shared.model.university.lesson.Lesson;
import shared.model.user.UserType;
import shared.model.user.professor.Type;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LessonListController implements Initializable {

    @FXML
    protected Pane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected TextField lessonCode;
    @FXML
    protected ComboBox<String> unitBox;
    @FXML
    protected ComboBox<String> collegeName;
    @FXML
    protected Button show;
    @FXML
    protected Button edit;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    @FXML
    protected TableView<Lesson> list;
    @FXML
    TableColumn<Lesson, String> codeColumn;
    @FXML
    TableColumn<Lesson, String> groupColumn;
    @FXML
    TableColumn<Lesson, String> unitColumn;
    @FXML
    TableColumn<Lesson, String> nameColumn;
    @FXML
    TableColumn<Lesson, List<String>> prerequisitesColumn;
    @FXML
    TableColumn<Lesson, List<String>> needColumn;
    @FXML
    TableColumn<Lesson, String> capacityColumn;
    @FXML
    TableColumn<Lesson, String> registrationColumn;
    @FXML
    TableColumn<Lesson, String> professorColumn;
    @FXML
    TableColumn<Lesson, String> examTimeColumn;
    @FXML
    TableColumn<Lesson, List<String>> planColumn;


    public void show(ActionEvent actionEvent) {
        Request request = new Request(RequestType.SHOW_DESIRED_LESSONS_LIST);
        request.addData("collegeName", collegeName.getValue());
        request.addData("unitNumber", unitBox.getValue());
        request.addData("lessonCode", lessonCode.getText());
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            ErrorMonitor.showError(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            List<Lesson> desiredLessons = new ArrayList<>();
            response.getData().forEach((K, V) -> {
                if (K.startsWith("lesson")) {
                    desiredLessons.add((Lesson) V);
                }
            });
            list.getItems().clear();
            list.getItems().addAll(desiredLessons);
        }
    }

    public void edit(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "editLessonPage");
    }

    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    private void hide() {
        if (EDU.userType != UserType.PROFESSOR ||
                EDU.professorType != Type.EDUCATIONAL_ASSISTANT) {
            this.edit.setDisable(true);
            this.edit.setVisible(false);
        }
    }

    private List<Lesson> getData() {
        Request request = new Request(RequestType.SHOW_LESSONS_LIST_PAGE);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            ErrorMonitor.showError(Alert.AlertType.ERROR, response.getErrorMessage());
            return null;
        }
        List<Lesson> lessons = new ArrayList<>();
        response.getData().forEach((K, V) -> {
            if (K.startsWith("lesson")) {
                lessons.add((Lesson) V);
            }
        });
        return lessons;
    }

    private void setTableData(List<Lesson> lessons) {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("lessonCode"));
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("group"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unitNumber"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        registrationColumn.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        professorColumn.setCellValueFactory(new PropertyValueFactory<>("professorCode"));
        examTimeColumn.setCellValueFactory(new PropertyValueFactory<>("examTime"));
        planColumn.setCellValueFactory(new PropertyValueFactory<>("days"));
        prerequisitesColumn.setCellValueFactory(new PropertyValueFactory<>("prerequisites"));
        needColumn.setCellValueFactory(new PropertyValueFactory<>("theNeed"));

        list.getItems().addAll(lessons);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hide();
        List<Lesson> lessons = getData();
        if (lessons != null) {
            setTableData(lessons);
            collegeName.getItems().add("-");
            collegeName.getItems().addAll(University.getUniversity().getCollegeName());
            unitBox.getItems().addAll("1", "2", "3", "4");
        }
    }
}
