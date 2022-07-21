package shared.model.courseware.exercise;

public class Answer {

    private String studentCode;
    private String text;
    private String fileAddress;
    private String sendTime;
    private String exerciseCode;

    public Answer(String studentCode, String exerciseCode, String sendTime) {
        this.studentCode = studentCode;
        this.sendTime = sendTime;
        this.exerciseCode = exerciseCode;
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
}
