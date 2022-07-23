package shared.model.university.lesson;

public class Group {
    private String lessonCode;
    private int groupNumber;
    private String professorCode;
    private int capacity;

    public Group(String lessonCode, int capacity, String professorCode) {
        this.lessonCode = lessonCode;
        this.capacity= capacity;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
