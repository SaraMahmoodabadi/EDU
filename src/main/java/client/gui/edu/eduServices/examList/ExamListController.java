package client.gui.edu.eduServices.examList;

import client.gui.EDU;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import shared.model.university.lesson.Lesson;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ExamListController implements Initializable {

    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected TableView<Lesson> table;
    @FXML
    protected TableColumn<Lesson, String> codeColumn;
    @FXML
    protected TableColumn<Lesson, String> nameColumn;
    @FXML
    protected TableColumn<Lesson, String> timeColumn;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;

    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    private void makeTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("LessonCode"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("examTime"));
    }

    private List<Lesson> getData() {
        Request request = new Request(RequestType.SHOW_EXAM_LIST_PAGE);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            List<Lesson> lessons = new ArrayList<>();
            response.getData().forEach((K, V) -> {
                if (K.startsWith("lesson")) {
                    lessons.add((Lesson) V);
                }
            });
            return lessons;
        }
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeTable();
        List<Lesson> lessons = getData();
        if (lessons != null) table.getItems().addAll(lessons);
    }
}
