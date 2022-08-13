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

    public Answer() {}

    public Answer(String studentCode, String exerciseCode, String sendTime) {
        this.studentCode = studentCode;
        this.sendTime = sendTime;
        this.exerciseCode = exerciseCode;
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
}
