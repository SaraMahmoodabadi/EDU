package client.gui.edu.eduServices.unitSelection.student;

import client.gui.AlertMonitor;
import client.gui.EDU;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
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
    String group;
    String exam;
    String grade;
    CheckBox mark;
    Button remove;
    Button changeGroup;
    Button take;
    Button request;
    StudentUnitSelectionController controller;
    Button option1;
    Button option2;

    public LessonTable(String lessonCode, String name, int group, String exam, Grade grade,
                       boolean isSelected, boolean isMarked, StudentUnitSelectionController controller) {
        this.lessonCode = lessonCode;
        this.name = name;
        this.group = String.valueOf(group);
        this.exam = exam;
        this.grade = grade.toString();
        this.mark = new CheckBox("mark");
        this.controller = controller;
        if (isMarked) this.mark.setSelected(true);
        addEventToMark();
        if (isSelected) {
            remove = new Button("remove");
            changeGroup = new Button("change group");
            addEventToRemove();
            addEventToChange();
            setBox(remove, changeGroup);
        }
        else {
            take = new Button("take");
            request = new Button("request to take");
            addEventToTake();
            addEventToRequest();
            setBox(take, request);
        }
    }

    public String getLessonCode() {
        return lessonCode;
    }

    public void setLessonCode(String lessonCode) {
        this.lessonCode = lessonCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public CheckBox getMark() {
        return mark;
    }

    public void setMark(CheckBox mark) {
        this.mark = mark;
    }

    public Button getRemove() {
        return remove;
    }

    public void setRemove(Button remove) {
        this.remove = remove;
    }

    public Button getChangeGroup() {
        return changeGroup;
    }

    public void setChangeGroup(Button changeGroup) {
        this.changeGroup = changeGroup;
    }

    public Button getTake() {
        return take;
    }

    public void setTake(Button take) {
        this.take = take;
    }

    public Button getRequest() {
        return request;
    }

    public void setRequest(Button request) {
        this.request = request;
    }

    public StudentUnitSelectionController getController() {
        return controller;
    }

    public void setController(StudentUnitSelectionController controller) {
        this.controller = controller;
    }

    public Button getOption1() {
        return option1;
    }

    public void setOption1(Button option1) {
        this.option1 = option1;
    }

    public Button getOption2() {
        return option2;
    }

    public void setOption2(Button option2) {
        this.option2 = option2;
    }

    private void setBox(Button checkBox1, Button checkBox2) {
        option1 = checkBox1;
        option2 = checkBox2;
    }

    public boolean isMarked() {
        return mark.isSelected();
    }

    private void addEventToMark() {
        mark.setOnAction(event -> {
            Response response;
            if (isMarked()) {
                Request request = new Request(RequestType.MARK_LESSON);
                request.addData("lessonCode", lessonCode);
                request.addData("group", group);
                request.addData("collegeCode", EDU.collegeCode);
                response = EDU.serverController.sendRequest(request);
            }
            else {
                Request request = new Request(RequestType.UN_MARK_LESSON);
                request.addData("lessonCode", lessonCode);
                request.addData("group", group);
                request.addData("collegeCode", EDU.collegeCode);
                response = EDU.serverController.sendRequest(request);
            }
            if (response.getStatus() == ResponseStatus.ERROR) {
                AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
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
            request.addData("collegeCode", EDU.collegeCode);
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
        ChoiceDialog d = new ChoiceDialog("group", groups);
        d.setHeaderText("day of the week");
        d.setContentText("please select the day of the week");
        d.showAndWait();
        String selected = String.valueOf(d.getSelectedItem());
        Request request = new Request(RequestType.CHANGE_GROUP_UNIT_SELECTION);
        if (selected != null && groups.contains(selected)) {
            request.addData("lessonCode", lessonCode);
            request.addData("group", selected);
            request.addData("previousGroup", this.group);
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
        request.addData("collegeCode", EDU.collegeCode);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            if (request.getRequestType() == RequestType.TAKE_LESSON_UNIT_SELECTION) {
                controller.removeData(this);
                option1 = remove;
                option2 = changeGroup;
                controller.addData(this);
            } else if (request.getRequestType() == RequestType.REMOVE_LESSON_UNIT_SELECTION) {
                controller.removeData(this);
                option1 = take;
                option2 = this.request;
                controller.addData(this);
            }
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        }
        else {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
    }
}

