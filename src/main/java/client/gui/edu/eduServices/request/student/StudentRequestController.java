package client.gui.edu.eduServices.request.student;

import client.gui.AlertMonitor;
import client.gui.EDU;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import shared.model.message.request.Request;
import shared.model.message.request.Type;
import shared.model.university.college.University;
import shared.model.user.UserType;
import shared.model.user.student.Grade;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class StudentRequestController implements Initializable {

    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Rectangle rectangle3;
    @FXML
    protected ComboBox<String> majorBox;
    @FXML
    protected ComboBox<String> requestBox;
    @FXML
    protected Label recentRequestLabel;
    @FXML
    protected TableView<Request> table;
    @FXML
    protected TableColumn<Request, String> typeColumn;
    @FXML
    protected TableColumn<Request, String> dateColumn;
    @FXML
    protected TableColumn<Request, String> resultColumn;
    @FXML
    protected Button register;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    @FXML
    protected TextField professorCode;
    private Grade grade;

    public void register(ActionEvent actionEvent) {
        shared.request.Request request = new shared.request.Request(RequestType.REGISTER_REQUEST);
        if (Objects.equals(requestBox.getValue(), Type.RECOMMENDATION.toString())) {
            if (professorCode.getText() == null) return;
            else request.addData("professorCode", professorCode.getText());
        }
        if (requestBox.getValue().equals(Type.MINOR.toString())) {
            if (majorBox.getValue() == null) return;
            request.addData("major", majorBox.getValue());
        }
        request.addData("type", requestBox.getValue());
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        }
        else {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
    }

    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    private void makeBoxes() {
        majorBox.getItems().addAll(University.getUniversity().getMajor());
        requestBox.getItems().add(Type.CERTIFICATE.toString());
        requestBox.getItems().add(Type.WITHDRAWAL.toString());
        switch (grade) {
            case UNDERGRADUATE:
                requestBox.getItems().add(Type.RECOMMENDATION.toString());
                requestBox.getItems().add(Type.MINOR.toString());
                break;
            case MASTER:
                requestBox.getItems().add(Type.RECOMMENDATION.toString());
                requestBox.getItems().add(Type.DORMITORY.toString());
                break;
            case PHD:
                requestBox.getItems().add(Type.THESIS_DEFENCE.toString());
                break;
        }
    }

    private List<Request> getData() {
        shared.request.Request request = new shared.request.Request(RequestType.SHOW_REQUESTS_PAGE, UserType.STUDENT);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            grade = (Grade) response.getData("grade");
            makeBoxes();
            hide();
            List<Request> requests = new ArrayList<>();
            response.getData().forEach((K, V) -> {
                if (K.startsWith("request")) {
                    requests.add((Request) V);
                }
            });
            return requests;
        }
        return null;
    }

    private void makeTable() {
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("finalResult"));
    }

    private void hide() {
        if (grade == Grade.PHD) professorCode.setVisible(false);
        if (grade != Grade.UNDERGRADUATE) majorBox.setVisible(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeTable();
        List<Request> requests = getData();
        if (requests != null) {
            table.getItems().addAll(requests);
        }
    }
}
