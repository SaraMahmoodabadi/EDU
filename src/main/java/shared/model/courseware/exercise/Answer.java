package shared.model.courseware.exercise;

import shared.model.courseware.educationalMaterial.ItemType;

public class Answer {
    private String studentName;
    private String studentCode;
    private String text;
    private String fileAddress;
    private String sendTime;
    private String exerciseCode;
    private ItemType answerType;
    private String score;

    public Answer() {}

    public Answer(String studentCode, String exerciseCode, String sendTime) {
        this.studentCode = studentCode;
        this.sendTime = sendTime;
        this.exerciseCode = exerciseCode;
    }

    public Answer(String exerciseCode, String studentCode, String name, ItemType type,
                  String sendTime, String text, String studentScore) {
        this.exerciseCode = exerciseCode;
        this.studentCode = studentCode;
        this.studentName = name;
        this.answerType = type;
        this.sendTime = sendTime;
        this.text = text;
        this.score = studentScore;
    }

    public Answer(String exerciseCode, String studentCode, String name, String sendTime,
                  ItemType type, String fileAddress, String studentScore) {
        this.exerciseCode = exerciseCode;
        this.studentCode = studentCode;
        this.studentName = name;
        this.answerType = type;
        this.sendTime = sendTime;
        this.fileAddress = fileAddress;
        this.score = studentScore;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFileAddress() {
        return fileAddress;
    }

    public void setFileAddress(String fileAddress) {
        this.fileAddress = fileAddress;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getExerciseCode() {
        return exerciseCode;
    }

    public void setExerciseCode(String exerciseCode) {
        this.exerciseCode = exerciseCode;
    }

    public ItemType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(ItemType answerType) {
        this.answerType = answerType;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
