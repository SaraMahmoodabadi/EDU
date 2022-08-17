package client.gui.edu.registration.lesson.list;

import client.gui.EDU;
import client.gui.AlertMonitor;
import client.network.offlineClient.OfflineClientHandler;
import javafx.application.Platform;
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
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LessonListController implements Initializable {
    @FXML
    public Label offlineLabel;
    @FXML
    public Button offlineButton;
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
    private boolean stop;
    Request request;

    public void show(ActionEvent actionEvent) {
        request = new Request(RequestType.SHOW_DESIRED_LESSONS_LIST);
        request.addData("collegeName", collegeName.getValue());
        request.addData("unitNumber", unitBox.getValue());
        request.addData("lessonCode", lessonCode.getText());
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
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
        stop = true;
        EDU.sceneSwitcher.switchScene(actionEvent, "editLessonPage");
    }

    public void back(ActionEvent actionEvent) {
        stop = true;
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    public void connectToServer(ActionEvent actionEvent) {
        OfflineClientHandler.connectToServer();
    }

    private void showOfflineMood() {
        this.offlineLabel.setVisible(true);
        this.offlineButton.setVisible(true);
        this.offlineButton.setDisable(false);
    }

    private void hide() {
        if (EDU.userType != UserType.PROFESSOR ||
                EDU.professorType != Type.EDUCATIONAL_ASSISTANT) {
            this.edit.setDisable(true);
            this.edit.setVisible(false);
        }
    }

    private List<Lesson> getData() {
        request = new Request(RequestType.SHOW_LESSONS_LIST_PAGE);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
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
        planColumn.setCellValueFactory(new PropertyValueFactory<>("plan"));
        prerequisitesColumn.setCellValueFactory(new PropertyValueFactory<>("prerequisites"));
        needColumn.setCellValueFactory(new PropertyValueFactory<>("theNeed"));

        list.getItems().addAll(lessons);
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            List<Lesson> desiredLessons = new ArrayList<>();
                            response.getData().forEach((K, V) -> {
                                if (K.startsWith("lesson")) {
                                    desiredLessons.add((Lesson) V);
                                }
                            });
                            list.getItems().clear();
                            list.getItems().addAll(desiredLessons);
                        }
                        if (!EDU.isOnline) showOfflineMood();
                    });
                } catch (InterruptedException ignored) {}
                if (!EDU.isOnline) break;
            }
        });
        loop.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stop = false;
        hide();
        List<Lesson> lessons = getData();
        collegeName.getItems().add("-");
        collegeName.getItems().addAll(University.getUniversity().getCollegeName());
        unitBox.getItems().addAll("1", "2", "3", "4");
        unitBox.getItems().add("-");
        if (lessons != null) {
            setTableData(lessons);
        }
        updateData();
    }
}
