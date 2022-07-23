package client.gui.edu.mainPage;

import client.gui.EDU;
import client.gui.AlertMonitor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import shared.model.user.UserType;
import shared.model.user.professor.Type;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.media.ImageHandler;

import java.net.URL;
import java.util.*;

public class MainPageController implements Initializable {

    @FXML
    protected Text name;
    @FXML
    protected Text email;
    @FXML
    protected Text role;
    @FXML
    protected Text statusText;
    @FXML
    protected Text time;
    @FXML
    protected Text lastLogin;
    @FXML
    protected ImageView profileImage;
    @FXML
    protected Button logOut;
    @FXML
    protected Rectangle upRectangle;
    @FXML
    protected Rectangle bigRectangle;
    @FXML
    protected Rectangle smallRectangle;
    @FXML
    protected Rectangle rightRectangle;
    @FXML
    protected Rectangle leftRectangle;
    @FXML
    protected Rectangle informationRectangle;
    @FXML
    protected Rectangle statusRectangle;
    @FXML
    protected MenuBar menuBar;
    @FXML
    protected Menu registrationMatters;
    @FXML
    protected Menu educationalServices;
    @FXML
    protected Menu reportCardMatters;
    @FXML
    protected Menu courseware;
    @FXML
    protected Menu messages;
    @FXML
    protected Menu profile;
    @FXML
    protected MenuItem lessonsList;
    @FXML
    protected MenuItem professorsList;
    @FXML
    protected MenuItem newUser;
    @FXML
    protected MenuItem weeklySchedule;
    @FXML
    protected MenuItem examList;
    @FXML
    protected MenuItem requests;
    @FXML
    protected MenuItem unitSelection;
    @FXML
    protected MenuItem temporaryScores;
    @FXML
    protected MenuItem educationalStatus;
    @FXML
    protected MenuItem coursewareItem;
    @FXML
    protected MenuItem messagesItem;
    @FXML
    protected MenuItem messenger;
    @FXML
    protected MenuItem profileItem;
    @FXML
    protected ListView<String> rightList;
    @FXML
    protected ListView<String> middleList;
    @FXML
    protected ListView<String> leftList;
    private Stage stage;

    public void showLessonsList(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScenes(this.stage, "lessonListPage");
    }

    public void showProfessorsList(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScenes(this.stage, "professorListPage");
    }

    public void showNewUserPage(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScenes(this.stage, "newUserPage");
    }

    public void showWeeklySchedule(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScenes(this.stage, "weeklyPlanPage");
    }

    public void showExamList(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScenes(this.stage, "examListPage");
    }

    public void showRequests(ActionEvent actionEvent) {
        if (EDU.userType == UserType.STUDENT) {
            EDU.sceneSwitcher.switchScenes(this.stage, "studentRequestPage");
        }
        else if (EDU.userType == UserType.PROFESSOR) {
            EDU.sceneSwitcher.switchScenes(this.stage, "professorRequestPage");
        }
    }

    public void showUnitSelectionPage(ActionEvent actionEvent) {
    }

    public void showTemporaryScores(ActionEvent actionEvent) {
        if (EDU.userType == UserType.STUDENT) {
            EDU.sceneSwitcher.switchScenes(this.stage, "studentTemporaryScoresPage");
        }
        else if (EDU.userType == UserType.PROFESSOR){
            if (EDU.professorType == Type.EDUCATIONAL_ASSISTANT) {
                EDU.sceneSwitcher.switchScenes(this.stage, "eduAssistantTemporaryScoresPage");
            }
            else {
                EDU.sceneSwitcher.switchScenes(this.stage, "professorTemporaryScoresPage");
            }
        }
    }

    public void showEducationalStatus(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScenes(this.stage, "eduStatusPage");
    }

    public void showCourseware(ActionEvent actionEvent) {
    }

    public void showMessages(ActionEvent actionEvent) {
    }

    public void showMessenger(ActionEvent actionEvent) {
    }

    public void showProfile(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScenes(this.stage, "profilePage");
    }

    public void logOut(ActionEvent actionEvent) {
        EDU.professorType = null;
        EDU.userType = null;
        EDU.sceneSwitcher.switchScene(actionEvent, "loginPage");
    }

    private void hideFields() {
        if (EDU.userType == UserType.PROFESSOR) {
            this.leftList.setVisible(false);
            this.middleList.setVisible(false);
            this.rightList.setVisible(false);
            if (EDU.professorType != Type.EDUCATIONAL_ASSISTANT) {
                this.unitSelection.setDisable(true);
                this.unitSelection.setVisible(false);
                this.educationalStatus.setDisable(true);
                this.educationalStatus.setDisable(false);
            }
        }
        if (EDU.userType == UserType.STUDENT ||
                EDU.professorType != Type.EDUCATIONAL_ASSISTANT) {
            this.newUser.setDisable(true);
            this.newUser.setVisible(false);
        }
    }

    private void getData() {
        Request request = new Request(RequestType.LOGIN, EDU.userType);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            this.name.setText((String) response.getData("name"));
            this.email.setText((String) response.getData("emailAddress"));
            this.lastLogin.setText(this.lastLogin.getText() + response.getData("lastLogin"));
            Object image = response.getData("profileImage");
            this.profileImage.setImage(new ImageHandler().getImage(String.valueOf(image)));
            if (EDU.userType == UserType.STUDENT) {
                this.role.setText(EDU.userType.toString().toLowerCase());
                getTableData(response);
            } else {
                this.role.setText(EDU.professorType.toString().toLowerCase());
            }
        }
    }

    private void getTableData(Response response) {
        String middleListString = (String) response.getData("middleList");
        List<String> middleListValues = new ArrayList<>(Arrays.asList(middleListString.split(", ")));
        String rightListString = (String) response.getData("rightList");
        List<String> rightListValues = new ArrayList<>(Arrays.asList(rightListString.split(", ")));
        setTableData(middleListValues, rightListValues);
    }

    private void setTableData(List<String> middleListValues, List<String> rightListValues) {
        leftList.getItems().addAll("educational status", "supervisor",
                "registrationLicense", "registrationTime");
        middleList.getItems().addAll(middleListValues.get(0), middleListValues.get(1),
                middleListValues.get(2), middleListValues.get(3));
        rightList.getItems().addAll(rightListValues.get(0), rightListValues.get(1),
                rightListValues.get(2), rightListValues.get(3));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stage = (Stage) (logOut.getScene().getWindow());;
        hideFields();
        getData();
    }
}
