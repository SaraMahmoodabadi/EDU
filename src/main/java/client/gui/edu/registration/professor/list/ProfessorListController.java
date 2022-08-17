package client.gui.edu.registration.professor.list;

import client.gui.AlertMonitor;
import client.gui.EDU;

import client.network.offlineClient.OfflineClientHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import shared.model.user.UserType;
import shared.model.user.professor.Professor;
import shared.model.user.professor.Type;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProfessorListController implements Initializable {
    @FXML
    public Label offlineLabel;
    @FXML
    public Button offlineButton;
    @FXML
    protected Pane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected Button show;
    @FXML
    protected Button edit;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    @FXML
    protected TableView<Professor> list;
    @FXML
    protected TableColumn<Professor, String> nameColumn;
    @FXML
    protected TableColumn<Professor, String> collegeColumn;
    @FXML
    protected TableColumn<Professor, String> codeColumn;
    @FXML
    protected TableColumn<Professor, String> degreeColumn;
    @FXML
    protected TableColumn<Professor, String> postColumn;
    private boolean stop;
    Request request;

    public void edit(ActionEvent actionEvent) {
        stop = true;
        EDU.sceneSwitcher.switchScene(actionEvent, "editProfessorPage");
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
                EDU.professorType != Type.DEAN) {
            this.edit.setDisable(true);
            this.edit.setVisible(false);
        }
    }

    private List<Professor> getData() {
        request = new Request(RequestType.SHOW_PROFESSORS_LIST_PAGE);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
            return null;
        }
        List<Professor> professors = new ArrayList<>();
        response.getData().forEach((K, V) -> {
            if (K.startsWith("professor")) {
                professors.add((Professor) V);
            }
        });
        return professors;
    }

    private void setTableData(List<Professor> professors) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("professorCode"));
        collegeColumn.setCellValueFactory(new PropertyValueFactory<>("collegeCode"));
        degreeColumn.setCellValueFactory(new PropertyValueFactory<>("degree"));
        postColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        list.getItems().addAll(professors);
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            List<Professor> professors = new ArrayList<>();
                            response.getData().forEach((K, V) -> {
                                if (K.startsWith("professor")) {
                                    professors.add((Professor) V);
                                }
                            });
                            list.getItems().clear();
                            list.getItems().addAll(professors);
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
        List<Professor> professors = getData();
        if (professors != null) {
            setTableData(professors);
        }
        updateData();
    }
}
