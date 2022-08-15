package client.gui.edu.eduServices.weeklyPlan;

import client.gui.EDU;

import client.network.offlineClient.OfflineClientHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javafx.scene.text.TextAlignment;
import shared.model.university.lesson.Day;
import shared.model.university.lesson.Lesson;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WeeklyPlanController implements Initializable {
    @FXML
    public Label offlineLabel;
    @FXML
    public Button offlineButton;
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
    private Request request;
    private boolean stop;

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

    private void getPlan() {
        request = new Request(RequestType.SHOW_WEEKLY_SCHEDULE_PAGE);
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
        String name = lesson.getName();
        String time = lesson.getClassTime();
        int h1 = Integer.parseInt(time.split("-")[0].split(":")[0]);
        int m1 = Integer.parseInt(time.split("-")[0].split(":")[1]);
        int h2 = Integer.parseInt(time.split("-")[1].split(":")[0]);
        int m2 = Integer.parseInt(time.split("-")[1].split(":")[1]);
        List<Day> days = lesson.getDays();
        makePlace(h1, m1, h2, m2, days, name);
    }

    //TODO: Add constants to file
    private void makePlace(int h1, int m1, int h2, int m2, List<Day> days, String name) {
        int time = (h2 - h1) * 60 + (m2 - m1);
        double width = (double) (time * 62 ) / 60.0;
        double x = 250 + ((h1 - 7) * 62) + (m1 * 62) / 60.0;
        for (Day day : days) {
            Label lessonLabel = new Label();
            lessonLabel.setText(name + "\n" + h1 + ":" + m1 + "-" + h2 + ":" + m2);
            lessonLabel.setTextAlignment(TextAlignment.CENTER);
            lessonLabel.setAlignment(Pos.CENTER);
            lessonLabel.setTextFill(Color.valueOf("#b151b8"));
            lessonLabel.setStyle(String.valueOf(Color.valueOf("#ffd100")));
            lessonLabel.setBackground(new Background(new BackgroundFill
                    (Color.valueOf("#ffd100"), CornerRadii.EMPTY, Insets.EMPTY)));
            lessonLabel.setPrefHeight(75);
            lessonLabel.setPrefWidth(width);
            lessonLabel.setLayoutX(x);
            lessonLabel.setLayoutY(getY(day));
            lessonLabel.setWrapText(true);
            AnchorPane.setTopAnchor(lessonLabel, (double) getY(day));
            AnchorPane.setLeftAnchor(lessonLabel, x);
            AnchorPane.setRightAnchor(lessonLabel, 1200 - width - x);
            AnchorPane.setBottomAnchor(lessonLabel, 800 - (double) getY(day) - 75);
            pane.getChildren().add(lessonLabel);
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

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    if (!EDU.isOnline) break;
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        if (!EDU.isOnline)
                            showOfflineMood();
                        else {
                            Response response = EDU.serverController.sendRequest(request);
                            if (response.getStatus() == ResponseStatus.OK) {
                                response.getData().forEach((K, V) -> {
                                    if (K.startsWith("lesson")) {
                                        putLesson((Lesson) V);
                                    }
                                });
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
        getPlan();
        if (!EDU.isOnline) showOfflineMood();
        updateData();
    }
}
