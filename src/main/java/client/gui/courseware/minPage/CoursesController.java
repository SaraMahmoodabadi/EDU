package client.gui.courseware.minPage;

import client.gui.EDU;
import client.network.ServerController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import shared.model.courseware.Course;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CoursesController implements Initializable {
    @FXML
    protected Button back;
    @FXML
    protected VBox coursesPane;
    @FXML
    protected DatePicker date;
    @FXML
    protected TextArea events;
    private boolean stop;

    @FXML
    public void back(ActionEvent actionEvent) {
        stop = true;
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    private void showData(HashMap<String, Object> data) {
        int t = 0;
        for (int i = 0; i < data.size(); i++) {
            Course course = (Course) data.get("course" + i);
            if (course == null) continue;
            t++;
            CourseLabel courseLabel = new CourseLabel(course.getCourseCode(), course.getName());
            if (coursesPane.getPrefHeight() < 90 * t)
                coursesPane.setPrefHeight(90 * t);
            coursesPane.getChildren().add(courseLabel);
        }
    }

    private void showCalendar(HashMap<String, Object> data) {
        events.clear();
        StringBuilder events = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            String event = (String) data.get("event" + i);
            events.append(event).append("\n");
        }
        this.events.setText(events.toString());
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        Request request = new Request(RequestType.GET_ALL_COURSES);
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            coursesPane.getChildren().clear();
                            showData(response.getData());
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        loop.start();
    }

    private void updateCalendar() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    if (date.getValue() == null) continue;
                    Platform.runLater(() -> {
                        Request request = new Request(RequestType.GET_MAIN_CALENDAR_DATA);
                        request.addData("date", date.getValue().toString());
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            showCalendar(response.getData());
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
        Request request = new Request(RequestType.GET_ALL_COURSES);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK)
            showData(response.getData());
        updateData();
        updateCalendar();
    }

    class CourseLabel extends Label {
        String courseCode;
        String name;

        public CourseLabel(String courseCode, String name) {
            this.courseCode = courseCode;
            this.name = name;
            makeLabel(name);
        }

        private void makeLabel(String data) {
            this.setText(data);
            this.setPrefWidth(500);
            this.setPrefHeight(75);
            Font font = Font.font("System", FontWeight.BOLD,15);
            this.setFont(font);
            this.setTextFill(Color.valueOf("#b151b8"));
            this.setStyle(String.valueOf(Color.valueOf("#ffd100")));
            this.setBackground(new Background(new BackgroundFill
                    (Color.valueOf("#ffd100"), CornerRadii.EMPTY, Insets.EMPTY)));
            addActionEvent(this);
        }

        private void addActionEvent(Label label) {
            label.setOnMouseClicked(event -> {
                Request request = new Request(RequestType.SHOW_COURSE);
                request.addData("courseCode", this.courseCode);
                ServerController.request = request;
                CoursesController.this.stop = true;
                EDU.sceneSwitcher.switchScene(new ActionEvent(), "course");
            });
        }
    }
}
