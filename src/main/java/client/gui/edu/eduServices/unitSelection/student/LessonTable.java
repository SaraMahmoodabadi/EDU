package client.gui.edu.eduServices.unitSelection.student;

import client.gui.AlertMonitor;
import client.gui.EDU;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import shared.model.user.student.Grade;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LessonTable {
    String lessonCode;
    String name;
    int group;
    String exam;
    Grade grade;
    CheckBox mark;
    Scene scene;
    HBox hBox;
    Button remove;
    Button changeGroup;
    Button take;
    Button request;

    public LessonTable(String lessonCode, String name, int group,
                       String exam, Grade grade, boolean isSelected, boolean isMarked) {
        this.lessonCode = lessonCode;
        this.name = name;
        this.group = group;
        this.exam = exam;
        this.grade = grade;
        this.mark = new CheckBox("mark");
        if (isMarked) this.mark.setSelected(true);
        addEventToMark();
        if (isSelected) {
            remove = new Button("remove");
            changeGroup = new Button("change group");
            setBox(remove, changeGroup);
        }
        else {
            take = new Button("take");
            request = new Button("request to take");
            setBox(take, request);
        }
        addEventToRemove();
        addEventToChange();
        addEventToTake();
        addEventToRequest();
    }

    private void setBox(Button checkBox1, Button checkBox2) {
        hBox = new HBox(checkBox1, checkBox2);
        scene = new Scene(hBox, 200, 50);
    }

    public boolean isMarked() {
        return mark.isSelected();
    }

    private void addEventToMark() {
        mark.setOnAction(event -> {
            if (isMarked()) {
                Request request = new Request(RequestType.MARK_LESSON);
                request.addData("lessonCode", lessonCode);
                request.addData("group", group);
                EDU.serverController.sendRequest(request);
            }
            else {
                Request request = new Request(RequestType.UN_MARK_LESSON);
                request.addData("lessonCode", lessonCode);
                request.addData("group", group);
                EDU.serverController.sendRequest(request);
            }
        });
    }

    private void addEventToRemove() {
        remove.setOnAction(event -> {
            Request request = new Request(RequestType.REMOVE_LESSON_UNIT_SELECTION);
            request.addData("lessonCode", lessonCode);
            request.addData("group", group);
            showResult(request);
        });
    }

    private void addEventToChange() {
        changeGroup.setOnAction(event -> {
            Request request = new Request(RequestType.GET_LESSON_GROUPS);
            request.addData("lessonCode", lessonCode);
            request.addData("group", group);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.OK) {
                showMenu(response);
            }
            else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        });
    }

    private void showMenu(Response response) {
        List<String> groups = new ArrayList<>();
        for (Map.Entry<String, Object> entry : response.getData().entrySet()) {
            if (entry.getKey().startsWith("group")) {
                groups.add(String.valueOf(entry.getValue()));
            }
        }
        ChoiceDialog d = new ChoiceDialog(groups.get(0), groups);
        d.setHeaderText("day of the week");
        d.setContentText("please select the day of the week");
        d.showAndWait();
        Request request = new Request(RequestType.CHANGE_GROUP_UNIT_SELECTION);
        if (d.getSelectedItem() != null) {
            request.addData("lessonCode", lessonCode);
            request.addData("group", d.getSelectedItem());
            showResult(request);
        }
    }

    private void addEventToTake() {
        take.setOnAction(event -> {
            Request request = new Request(RequestType.TAKE_LESSON_UNIT_SELECTION);
            request.addData("lessonCode", lessonCode);
            request.addData("group", group);
            showResult(request);
        });
    }

    private void addEventToRequest() {
        request.setOnAction(event -> {
            Request request = new Request(RequestType.REQUEST_TAKE_LESSON_UNIT_SELECTION);
            request.addData("lessonCode", lessonCode);
            request.addData("group", group);
            showResult(request);
        });
    }

    private void showResult(Request request) {
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            if (request.getRequestType() == RequestType.TAKE_LESSON_UNIT_SELECTION) {
                StudentUnitSelectionController.removeData(this);
                HBox hBox = new HBox(remove, changeGroup);
                this.scene.setRoot(hBox);
                StudentUnitSelectionController.addData(this);
            } else if (request.getRequestType() == RequestType.REMOVE_LESSON_UNIT_SELECTION) {
                StudentUnitSelectionController.removeData(this);
                HBox hBox = new HBox(take, this.request);
                this.scene.setRoot(hBox);
                StudentUnitSelectionController.addData(this);
            }
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        }
        else {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
    }
}

