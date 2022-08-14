package client.gui.courseware.course.exercise.professorExercise;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.ServerController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import shared.model.courseware.educationalMaterial.ItemType;
import shared.model.courseware.exercise.Answer;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class ExerciseController implements Initializable {
    @FXML
    protected TableView<ExerciseTable> table;
    @FXML
    protected TableColumn<ExerciseTable, Integer> idColumn;
    @FXML
    protected TableColumn<ExerciseTable, String> nameColumn;
    @FXML
    protected TableColumn<ExerciseTable, String> codeColumn;
    @FXML
    protected TableColumn<ExerciseTable, String> timeColumn;
    @FXML
    protected TableColumn<ExerciseTable, String> scoreColumn;
    @FXML
    protected TableColumn<ExerciseTable, Button> answerColumn;
    @FXML
    protected Text exerciseName;
    @FXML
    protected TextField studentField;
    @FXML
    protected TextField scoreField;
    @FXML
    protected TextArea answerArea;
    private String courseCode;
    private String exerciseCode;
    private boolean isAssistant;
    private boolean stop;
    private String selectedStudent;
    private ArrayList<ExerciseTable> students;

    @FXML
    public void registerScore(ActionEvent event) {
        if (selectedStudent == null) {
            String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "nullChoice");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, error);
            return;
        }
        if (scoreField.getText() == null) {
            String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "nullItem");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, error);
            return;
        }
        if (!isValid()) {
            String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "invalidScore");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, error);
            return;
        }
        Request request = new Request(RequestType.REGISTER_EXERCISE_SCORE);
        request.addData("studentCode", selectedStudent);
        request.addData("score", scoreField.getText());
        request.addData("exerciseCode", exerciseCode);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK)
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }

    @FXML
    public void selectStudent(MouseEvent mouseEvent) {
        ExerciseTable exerciseTable = table.getSelectionModel().getSelectedItem();
        if (exerciseTable != null) {
            this.selectedStudent = getStudentCodeByID(exerciseTable);
            this.studentField.setText(exerciseTable.getDisplayStudentCode());
        }
    }

    @FXML
    public void back(ActionEvent event) {
        Request request = new Request(RequestType.SHOW_COURSE);
        request.addData("courseCode", this.courseCode);
        ServerController.request = request;
        this.stop = true;
        EDU.sceneSwitcher.switchScene(new ActionEvent(), "course");
    }

    private boolean isValid() {
        try {
            double score = Double.parseDouble(scoreField.getText());
            if (score < 0.0 || score > 20.0) return false;
        }
        catch (NumberFormatException ignored) {}
        return true;
    }

    private void showData(Map<String, Object> data) {
        students.clear();
        for (int i = 0; i < data.size(); i++) {
            Answer answer = (Answer) data.get("answer" + i);
            if (answer == null) continue;
            int id = students.size() + 1;
            ExerciseTable exerciseTable;
            answer.setSendTime(answer.getSendTime().replace("T", " "));
            if (answer.getAnswerType() == ItemType.TEXT)
                exerciseTable = new ExerciseTable(id, answer.getStudentName(), answer.getStudentCode(),
                    answer.getSendTime(), answer.getScore(), answer.getText(), answer.getAnswerType());
            else exerciseTable = new ExerciseTable(id, answer.getStudentName(), answer.getStudentCode(),
                    answer.getSendTime(), answer.getScore(), answer.getFileAddress(), answer.getAnswerType());
            students.add(exerciseTable);
        }
        table.getItems().clear();
        table.getItems().addAll(students);
    }

    private String getStudentCodeByID(ExerciseTable exerciseTable) {
        for (ExerciseTable student : students) {
            if (student.getId() == exerciseTable.getId())
                return student.getStudentCode();
        }
        return "";
    }

    private void makeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("displayStudentCode"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("uploadTime"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        answerColumn.setCellValueFactory(new PropertyValueFactory<>("showAnswer"));
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(5000);
                    Platform.runLater(() -> {
                        Request request = new Request(RequestType.SHOW_EXERCISE_PROFESSOR);
                        request.addData("courseCode", this.courseCode);
                        request.addData("exerciseCode", this.exerciseCode);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stop = false;
        selectedStudent = null;
        students = new ArrayList<>();
        answerArea.setVisible(false);
        makeTable();
        Request request = ServerController.request;
        this.courseCode = (String) request.getData("courseCode");
        this.exerciseCode = (String) request.getData("exerciseCode");
        this.isAssistant = (boolean) request.getData("isAssistant");
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            this.exerciseName.setText((String) response.getData("name"));
            showData(response.getData());
        }
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        updateData();
    }

    public class ExerciseTable {
        private int id;
        private String displayName;
        private String name;
        private String displayStudentCode;
        private String studentCode;
        private String uploadTime;
        private String score;
        private String answer;
        private ItemType answerType;
        private Button showAnswer;

        public ExerciseTable(int id, String name, String studentCode, String uploadTime,
                             String score, String answer, ItemType answerType) {
            this.id = id;
            this.name = name;
            this.studentCode = studentCode;
            if (isAssistant) {
                this.displayName = "****";
                this.displayStudentCode = "****";
            }
            else {
                this.displayName = name;
                this.displayStudentCode = studentCode;
            }
            this.uploadTime = uploadTime;
            this.score = score;
            this.answer = answer;
            this.answerType = answerType;
            showAnswer = new Button("show answer");
            if (answerType == ItemType.TEXT) makeTextButton();
            else makeMediaButton();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayStudentCode() {
            return displayStudentCode;
        }

        public void setDisplayStudentCode(String displayStudentCode) {
            this.displayStudentCode = displayStudentCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStudentCode() {
            return studentCode;
        }

        public void setStudentCode(String studentCode) {
            this.studentCode = studentCode;
        }

        public String getUploadTime() {
            return uploadTime;
        }

        public void setUploadTime(String uploadTime) {
            this.uploadTime = uploadTime;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public ItemType getAnswerType() {
            return answerType;
        }

        public void setAnswerType(ItemType answerType) {
            this.answerType = answerType;
        }

        public Button getShowAnswer() {
            return showAnswer;
        }

        public void setShowAnswer(Button showAnswer) {
            this.showAnswer = showAnswer;
        }

        private void makeMediaButton() {
            showAnswer.setOnAction(event -> {
                MediaHandler handler = new MediaHandler();
                byte[] file = handler.decode(this.answer);
                String path = chooseDirectory();
                if (path == null)
                    path = "src/main/java/client/resource/sentFiles/" + handler.getName();
                else path = path + "/" + handler.getName();
                try (FileOutputStream fos = new FileOutputStream(path)) {
                    fos.write(file);
                    Desktop d = Desktop.getDesktop();
                    d.open(new File(path));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        private String chooseDirectory() {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select directory");
            File file = directoryChooser.showDialog(ServerController.edu);
            if (file != null) return file.getAbsolutePath();
            return null;
        }

        private void makeTextButton() {
            showAnswer.setOnAction(event -> {
                answerArea.setVisible(true);
                answerArea.setText(this.answer);
            });
        }
    }
}
