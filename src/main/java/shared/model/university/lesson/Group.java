package shared.model.university.lesson;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String lessonCode;
    private int groupNumber;
    private String professorCode;
    private int capacity;
    private int registrationNumber;
    private List<String> students;

    public Group() {}

    public Group(String lessonCode, int capacity, String professorCode) {
        this.lessonCode = lessonCode;
        this.capacity= capacity;
        this.professorCode = professorCode;
    }

    public Group(String lessonCode, String group, String professorCode, ArrayList<String> students) {
        this.lessonCode = lessonCode;
        this.groupNumber = Integer.parseInt(group);
        this.professorCode = professorCode;
        this.students = students;
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

    public int getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(int registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }
}
