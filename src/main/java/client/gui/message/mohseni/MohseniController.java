package client.gui.message.mohseni;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.ServerController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import shared.model.university.college.University;
import shared.model.university.lesson.Lesson;
import shared.model.user.student.Grade;
import shared.model.user.student.Student;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.media.MediaHandler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MohseniController implements Initializable {
    @FXML
    protected Button back;
    @FXML
    protected TextArea messageArea;
    @FXML
    protected Button sendMediaButton;
    @FXML
    protected Button sendButton;
    @FXML
    protected ComboBox<String> gradeBox;
    @FXML
    protected ComboBox<String> yearBox;
    @FXML
    protected ComboBox<String> collegeBox;
    @FXML
    protected TableView<Student> table;
    @FXML
    protected TableColumn<Student, String> firstnameColumn;
    @FXML
    protected TableColumn<Student, String> lastnameColumn;
    @FXML
    protected TableColumn<Student, String> studentCodeColumn;
    @FXML
    protected TableColumn<Student, Grade> gradeColumn;
    @FXML
    protected TextField studentCodeField;
    private String file;
    private String fileFormat;
    private boolean stop;


    @FXML
    public void selectStudent(MouseEvent event) {
        stop = true;
        Student student = table.getSelectionModel().getSelectedItem();
        Request request = new Request(RequestType.GET_STUDENT_PROFILE_MOHSENI);
        request.addData("studentCode", student.getStudentCode());
        ServerController.request = request;
        EDU.sceneSwitcher.switchScene(new ActionEvent(), "profilePage");
    }

    @FXML
    public void sendMessage(ActionEvent event) {
        if (file == null && messageArea.getText() == null) return;
        Request request = new Request(RequestType.SEND_MESSAGE_MOHSENI);
        if (file != null) {
            request.addData("file", file);
            request.addData("fileFormat", fileFormat);
        }
        if (messageArea.getText() != null) {
            request.addData("message", messageArea.getText());
        }
        request.addData("grade", gradeBox.getValue());
        request.addData("year", yearBox.getValue());
        request.addData("college", collegeBox.getValue());
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK)
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        else
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }

    @FXML
    public void chooseMedia(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select file");
        File file = fileChooser.showOpenDialog(ServerController.edu);
        if (file != null) {
            MediaHandler handler = new MediaHandler();
            String path = file.getAbsolutePath();
            this.file = handler.encode(path);
            int n = path.split("\\.").length;
            this.fileFormat = path.split("\\.")[n-1];
        }
    }

    @FXML
    void back(ActionEvent event) {
        stop = true;
        EDU.sceneSwitcher.switchScene(event, "loginPage");
    }

    private void setTable() {
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        studentCodeColumn.setCellValueFactory(new PropertyValueFactory<>("studentCode"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
    }

    private void setBox() {
        collegeBox.getItems().addAll(University.getUniversity().getCollegeName());
        collegeBox.getItems().add("All colleges");
        collegeBox.setValue("All colleges");
        for (Grade grade : Grade.values()) {
            gradeBox.getItems().add(grade.toString());
        }
        gradeBox.getItems().add("All grades");
        gradeBox.setValue("All grades");
        yearBox.getItems().addAll("All", "1400", "99", "98", "97", "96 and before");
        yearBox.setValue("All");
    }

    private void setData(Map<String, Object> data) {
        List<Student> students = new ArrayList<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey().startsWith("student")) {
                students.add((Student) entry.getValue());
            }
        }
        table.getItems().clear();
        table.getItems().addAll(students);
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        Request request;
                        if (studentCodeField.getText() == null) {
                            request = new Request(RequestType.GET_ALL_STUDENTS_INFORMATION_MOHSENI);
                        }
                        else {
                            request = new Request(RequestType.GET_STUDENTS_INFORMATION_MOHSENI);
                            request.addData("studentCode", studentCodeField.getText());
                        }
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            setData(response.getData());
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
        setBox();
        setTable();
        Request request = new Request(RequestType.GET_ALL_STUDENTS_INFORMATION_MOHSENI);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) setData(response.getData());
        else
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        updateData();
    }
}
