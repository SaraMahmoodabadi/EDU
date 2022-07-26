package shared.model.user.student;

import shared.model.university.lesson.score.Score;
import shared.model.user.User;
import shared.model.user.UserType;

import java.util.List;

public class Student extends User {

    private String studentCode;
    private double rate;
    private int enteringYear;
    private String supervisorCode;
    private boolean registrationLicense;
    private String registrationTime;
    private List<Score> scores;
    private List<List<String>> lessons;
    private List<String> professorsCode;
    private EducationalStatus status;
    private Grade grade;

    public Student(String firstName, String lastName, long nationalCode,
                   String collegeCode, String username, String password,
                   UserType userType, String email, String phoneNumber,
                   String studentCode, int enteringYear,
                   String supervisorCode, EducationalStatus status, Grade grade) {
        super(firstName, lastName, nationalCode, collegeCode, username, password, userType, email, phoneNumber);
        this.studentCode = studentCode;
        this.enteringYear = enteringYear;
        this.supervisorCode = supervisorCode;
        this.status = status;
        this.grade = grade;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getEnteringYear() {
        return enteringYear;
    }

    public void setEnteringYear(int enteringYear) {
        this.enteringYear = enteringYear;
    }

    public String getSupervisorCode() {
        return supervisorCode;
    }

    public void setSupervisorCode(String supervisorCode) {
        this.supervisorCode = supervisorCode;
    }

    public boolean isRegistrationLicense() {
        return registrationLicense;
    }

    public void setRegistrationLicense(boolean registrationLicense) {
        this.registrationLicense = registrationLicense;
    }

    public String getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(String registrationTime) {
        this.registrationTime = registrationTime;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public List<List<String>> getLessons() {
        return lessons;
    }

    public void setLessons(List<List<String>> lessons) {
        this.lessons = lessons;
    }

    public List<String> getProfessorsCode() {
        return professorsCode;
    }

    public void setProfessorsCode(List<String> professorsCode) {
        this.professorsCode = professorsCode;
    }

    public EducationalStatus getStatus() {
        return status;
    }

    public void setStatus(EducationalStatus status) {
        this.status = status;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}
