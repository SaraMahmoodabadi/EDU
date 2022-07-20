package shared.model.courseware.exercise;

public class Answer {

    private String studentCode;
    private String courseCode;
    private String text;
    private String fileAddress;
    private String sendTime;

    public Answer(String studentCode, String courseCode, String sendTime) {
        this.studentCode = studentCode;
        this.courseCode = courseCode;
        this.sendTime = sendTime;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
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
}
