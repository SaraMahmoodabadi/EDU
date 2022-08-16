package client.gui.message.messenger.newChat;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.ServerController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import shared.model.user.User;
import shared.model.user.UserType;
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

public class NewChatController implements Initializable {
    @FXML
    protected Button back;
    @FXML
    protected TableView<UserTable> table;
    @FXML
    protected TableColumn<UserTable, String> firstnameColumn;
    @FXML
    protected TableColumn<UserTable, String> lastnameColumn;
    @FXML
    protected TableColumn<UserTable, CheckBox> selectColumn;
    @FXML
    protected CheckBox selectAllCheckBox;
    @FXML
    protected TextArea messageArea;
    @FXML
    protected Button sendButton;
    @FXML
    protected TextField userCodeField;
    @FXML
    protected Button requestButton;
    @FXML
    protected RadioButton professorRadioButton;
    @FXML
    protected RadioButton studentRadioButton;
    @FXML
    protected Button sendMediaButton;
    private String file;
    private String fileFormat;
    protected static List<User> selectedUsers;
    private List<UserTable> users;
    private ToggleGroup userType;
    private boolean stop;


    @FXML
    public void selectAll(ActionEvent event) {
        if (selectAllCheckBox.isSelected()) {
            for (UserTable user : users) {
                user.select();
            }
        }
        else {
            for (UserTable user : users) {
                user.notSelect();
            }
        }
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
    public void sendMessage(ActionEvent event) {
        if (file == null && messageArea.getText() == null) return;
        Request request = new Request(RequestType.MAKE_NEW_CHAT);
        if (file != null) {
            request.addData("file", file);
            request.addData("fileFormat", fileFormat);
        }
        if (messageArea.getText() != null) {
            request.addData("message", messageArea.getText());
        }
        for (int i = 0; i < selectedUsers.size(); i++) {
            request.addData("user" + i, selectedUsers.get(i));
        }
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
            this.file = null;
            this.fileFormat = null;
            this.messageArea.clear();
        }
        else
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }

    @FXML
    public void sendRequest(ActionEvent event) {
        if (userCodeField.getText() == null) return;
        Request request = new Request(RequestType.REQUEST_SEND_MESSAGE);
        request.addData("user", getUserType());
        request.addData("userCode", userCodeField.getText());
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
            userCodeField.clear();
        }
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }

    @FXML
    public void back(ActionEvent event) {
        stop = true;
        EDU.sceneSwitcher.switchScene(event, "chat");
    }

    private String getUserType() {
        if (userType.getSelectedToggle() == professorRadioButton) return UserType.PROFESSOR.toString();
        else return UserType.STUDENT.toString();
    }

    private void showData(Map<String, Object> data) {
        users.clear();
        List<UserTable> newList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey().startsWith("user")) {
                User user = (User) entry.getValue();
                UserTable newUser = new UserTable(user.getFirstName(), user.getLastName(),
                        user.getUsername(), user.getUserType());
                for (User selectedUser : selectedUsers) {
                    if (selectedUser.getUsername().equals(user.getUsername()))
                        newList.add(newUser);
                }
                users.add(newUser);
            }
        }
        selectedUsers.clear();
        for (UserTable user : newList) user.select();
        table.getItems().clear();
        table.getItems().addAll(users);
    }

    private void updateData() {
        Request request = new Request(RequestType.SHOW_USERS_IN_NEW_CHAT);
        request.addData("collegeCode", EDU.collegeCode);
        if (EDU.userType == UserType.PROFESSOR) request.addData("professorType", EDU.professorType);
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            showData(response.getData());
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        loop.start();
    }

    private void setTable() {
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        selectColumn.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
    }

    private void setGroup() {
        userType = new ToggleGroup();
        studentRadioButton.setToggleGroup(userType);
        professorRadioButton.setToggleGroup(userType);
        professorRadioButton.setSelected(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stop = false;
        selectedUsers = new ArrayList<>();
        users = new ArrayList<>();
        setTable();
        setGroup();
        Request request = new Request(RequestType.SHOW_USERS_IN_NEW_CHAT);
        request.addData("collegeCode", EDU.collegeCode);
        if (EDU.userType == UserType.PROFESSOR) request.addData("professorType", EDU.professorType);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) showData(response.getData());
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        updateData();
    }
}
