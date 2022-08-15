package shared.model.university.lesson;

import shared.model.user.student.Grade;

import java.util.Collections;
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
    private int registrationNumber;
    private int group;
    private String professorCode;
    private String plan;
    private int term;

    public Lesson() {}

    public Lesson(String name, String lessonCode, String collegeCode,
                  List<String> teacherAssistant, int unitNumber,
                  Grade grade, List<String> prerequisites,
                  List<String> theNeed, int capacity, List<Day> days,
                  String classTime, String examTime, String professorCode) {
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
        this.plan = "days: " + days + ", time: " + classTime;
        this.professorCode = professorCode;
    }

    public Lesson(String name, String lessonCode, int unitNumber,
                  String registrationNumber, String examTime, String days,
                  String classTime, String prerequisites, String theNeed) {
        this.name = name;
        this.lessonCode = lessonCode;
        this.unitNumber = unitNumber;
        this.prerequisites = Collections.singletonList(prerequisites);
        this.theNeed = Collections.singletonList(theNeed);
        this.plan = "days: " + days + ", time: " + classTime;
        this.registrationNumber = Integer.parseInt(registrationNumber);
        this.examTime = examTime;
    }

    public Lesson(String lessonCode, String name, String examTime) {
        this.lessonCode = lessonCode;
        this.name = name;
        this.examTime = examTime;
    }

    public Lesson(String lessonCode, String name, String examTime, Grade grade, int group) {
        this.lessonCode = lessonCode;
        this.name = name;
        this.examTime = examTime;
        this.grade = grade;
        this.group = group;
    }

    public Lesson(String lessonCode, String name, List<Day> lessonDays, String classTime) {
        this.lessonCode = lessonCode;
        this.name = name;
        this.days = lessonDays;
        this.classTime = classTime;
        this.plan = "days: " + days + ", time: " + classTime;
    }

    public Lesson(String lessonCode, String name, String classTime,
                  List<Day> lessonDays, String plan, String examTime) {
        this.lessonCode = lessonCode;
        this.name = name;
        this.days = lessonDays;
        this.classTime = classTime;
        this.plan = plan;
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

    public int getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(int registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getProfessorCode() {
        return professorCode;
    }

    public void setProfessorCode(String professorCode) {
        this.professorCode = professorCode;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public String getPlan() {
       return this.plan;
    }
}
