package shared.model.university.lesson;

import shared.model.user.student.Grade;

import java.util.List;

public class Lesson {

    private String name;
    private String lessonCode;
    private String collegeCode;
    private List<Group> groups;
    private List<String> teacherAssistant;
    private int unitNumber;
    private Grade grade;
    private List<String> prerequisites;
    private List<String> theNeed;
    private List<String> studentsCode;
    private int capacity;
    private List<Day> days;
    private String classTime;
    private String examTime;

    public Lesson(String name, String lessonCode, String collegeCode,
                  List<String> teacherAssistant, int unitNumber,
                  Grade grade, List<String> prerequisites,
                  List<String> theNeed, int capacity, List<Day> days,
                  String classTime, String examTime) {
        this.name = name;
        this.lessonCode = lessonCode;
        this.collegeCode = collegeCode;
        this.teacherAssistant = teacherAssistant;
        this.unitNumber = unitNumber;
        this.grade = grade;
        this.prerequisites = prerequisites;
        this.theNeed = theNeed;
        this.capacity = capacity;
        this.days = days;
        this.classTime = classTime;
        this.examTime = examTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLessonCode() {
        return lessonCode;
    }

    public void setLessonCode(String lessonCode) {
        this.lessonCode = lessonCode;
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<String> getTeacherAssistant() {
        return teacherAssistant;
    }

    public void setTeacherAssistant(List<String> teacherAssistant) {
        this.teacherAssistant = teacherAssistant;
    }

    public int getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(int unitNumber) {
        this.unitNumber = unitNumber;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public List<String> getTheNeed() {
        return theNeed;
    }

    public void setTheNeed(List<String> theNeed) {
        this.theNeed = theNeed;
    }

    public List<String> getStudentsCode() {
        return studentsCode;
    }

    public void setStudentsCode(List<String> studentsCode) {
        this.studentsCode = studentsCode;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }
}
