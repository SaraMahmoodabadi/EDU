package server.logic.model.message.request;

public class Request {

    private Type type;
    private String studentCode;
    private String professorCode;
    private String anotherCollegeProfessorCode;
    private String firstBlank;
    private String secondBlank;
    private String thirdBlank;

    public Request(Type type, String studentCode, String professorCode) {
        this.type = type;
        this.studentCode = studentCode;
        this.professorCode = professorCode;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getProfessorCode() {
        return professorCode;
    }

    public void setProfessorCode(String professorCode) {
        this.professorCode = professorCode;
    }

    public String getAnotherCollegeProfessorCode() {
        return anotherCollegeProfessorCode;
    }

    public void setAnotherCollegeProfessorCode(String anotherCollegeProfessorCode) {
        this.anotherCollegeProfessorCode = anotherCollegeProfessorCode;
    }

    public String getFirstBlank() {
        return firstBlank;
    }

    public void setFirstBlank(String firstBlank) {
        this.firstBlank = firstBlank;
    }

    public String getSecondBlank() {
        return secondBlank;
    }

    public void setSecondBlank(String secondBlank) {
        this.secondBlank = secondBlank;
    }

    public String getThirdBlank() {
        return thirdBlank;
    }

    public void setThirdBlank(String thirdBlank) {
        this.thirdBlank = thirdBlank;
    }
}
