package client.gui.edu.eduServices.weeklyPlan;

import client.gui.EDU;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import shared.model.university.lesson.Day;
import shared.model.university.lesson.Lesson;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WeeklyPlanController implements Initializable {

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
    protected Rectangle rectangle4;
    @FXML
    protected Rectangle rectangle5;
    @FXML
    protected Rectangle rectangle6;
    @FXML
    protected Rectangle table;
    @FXML
    protected Text saturday;
    @FXML
    protected Text sunday;
    @FXML
    protected Text monday;
    @FXML
    protected Text tuesday;
    @FXML
    protected Text wednesday;
    @FXML
    protected Text thursday;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;

    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    private void getPlan() {
        Request request = new Request(RequestType.SHOW_WEEKLY_SCHEDULE_PAGE);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            response.getData().forEach((K, V) -> {
                if (K.startsWith("lesson")) {
                    putLesson((Lesson) V);
                }
            });
        }
    }

    private void putLesson(Lesson lesson) {
        String time = lesson.getClassTime();
        int h1 = Integer.parseInt(time.split("-")[0].split(":")[0]);
        int m1 = Integer.parseInt(time.split("-")[0].split(":")[1]);
        int h2 = Integer.parseInt(time.split("-")[1].split(":")[0]);
        int m2 = Integer.parseInt(time.split("-")[1].split(":")[1]);
        List<Day> days = lesson.getDays();
        makePlace(h1, m1, h2, m2, days);
    }

    //TODO: Add constants to file
    private void makePlace(int h1, int m1, int h2, int m2, List<Day> days) {
        int time = (h2 - h1) * 60 + (m2 - m1);
        double width = (double) (time * 62 ) / 60.0;
        double x = 250 + (h1 * 62) + (m1 * 62) / 60.0;
        for (Day day : days) {
            Rectangle lessonRectangle = new Rectangle();
            lessonRectangle.setOpacity(0.5);
            lessonRectangle.setFill(Color.valueOf("#b151b8"));
            lessonRectangle.setStroke(Color.valueOf("#b151b8"));
            lessonRectangle.setHeight(75);
            lessonRectangle.setWidth(width);
            lessonRectangle.setX(x);
            lessonRectangle.setY(getY(day));
        }
    }

    //TODO: Add constants to file
    private int getY(Day day) {
        int y = 0;
        switch (day) {
            case SATURDAY:
                y = 166;
                break;
            case SUNDAY:
                y = 245;
                break;
            case MONDAY:
                y = 325;
                break;
            case TUESDAY:
                y = 405;
                break;
            case WEDNESDAY:
                y = 485;
                break;
            case THURSDAY:
                y = 565;
                break;
        }
        return y;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getPlan();
    }
}
