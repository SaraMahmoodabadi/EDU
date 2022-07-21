package shared.model.university.lesson;

public class Group {
    private String lessonCode;
    private int groupNumber;
    private String professorCode;

    public Group(String lessonCode, int groupNumber, String professorCode) {
        this.lessonCode = lessonCode;
        this.groupNumber = groupNumber;
        this.professorCode = professorCode;
    }

    public String getLessonCode() {
        return lessonCode;
    }

    public void setLessonCode(String lessonCode) {
        this.lessonCode = lessonCode;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getProfessorCode() {
        return professorCode;
    }

    public void setProfessorCode(String professorCode) {
        this.professorCode = professorCode;
    }
}
