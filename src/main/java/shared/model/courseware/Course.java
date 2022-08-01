package shared.model.courseware;

import shared.model.courseware.educationalMaterial.EducationalMaterial;
import shared.model.courseware.exercise.Exercise;

import java.util.List;

public class Course {

    private String courseCode;
    private int groupNumber;
    private List<List<String>> professors; // list<list<group, professorCode>>
    private List<String> teacherAssistant;
    private List<EducationalMaterial> educationalMaterials;
    private List<Exercise> exercises;

    public Course() {}

    public Course(String courseCode, int groupNumber, List<List<String>> professors,
                  List<String> teacherAssistant, List<EducationalMaterial> educationalMaterials,
                  List<Exercise> exercises) {
        this.courseCode = courseCode;
        this.groupNumber = groupNumber;
        this.professors = professors;
        this.teacherAssistant = teacherAssistant;
        this.educationalMaterials = educationalMaterials;
        this.exercises = exercises;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public List<List<String>> getProfessors() {
        return professors;
    }

    public void setProfessors(List<List<String>> professors) {
        this.professors = professors;
    }

    public List<String> getTeacherAssistant() {
        return teacherAssistant;
    }

    public void setTeacherAssistant(List<String> teacherAssistant) {
        this.teacherAssistant = teacherAssistant;
    }

    public List<EducationalMaterial> getEducationalMaterials() {
        return educationalMaterials;
    }

    public void setEducationalMaterials(List<EducationalMaterial> educationalMaterials) {
        this.educationalMaterials = educationalMaterials;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}
