package shared.model.user.professor;

import shared.model.user.User;
import shared.model.user.UserType;

import java.util.List;

public class Professor extends User {

    private String professorCode;
    private int roomNumber;
    private List<List<String>> lessons;
    private MasterDegree degree;
    private Type type;

    public Professor(String firstName, String lastName, long nationalCode,
                     String collegeCode, String username, String password,
                     UserType userType, String professorCode, int roomNumber,
                     MasterDegree degree, Type type) {
        super(firstName, lastName, nationalCode, collegeCode, username, password, userType);
        this.professorCode = professorCode;
        this.roomNumber = roomNumber;
        this.degree = degree;
        this.type = type;
    }

    public Professor(String fullName, String collegeCode, String professorCode,
                     MasterDegree degree, Type type) {
        super(fullName, collegeCode);
        this.professorCode = professorCode;
        this.degree = degree;
        this.type = type;
    }

    public String getProfessorCode() {
        return professorCode;
    }

    public void setProfessorCode(String professorCode) {
        this.professorCode = professorCode;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public List<List<String>> getLessons() {
        return lessons;
    }

    public void setLessons(List<List<String>> lessons) {
        this.lessons = lessons;
    }

    public MasterDegree getDegree() {
        return degree;
    }

    public void setDegree(MasterDegree degree) {
        this.degree = degree;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
