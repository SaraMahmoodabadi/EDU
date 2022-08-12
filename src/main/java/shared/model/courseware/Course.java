package shared.model.courseware;

import shared.model.courseware.educationalMaterial.EducationalMaterial;
import shared.model.courseware.exercise.Exercise;

import java.util.List;

public class Course {

    private String courseCode;
    private String name;
    private int groupNumber;
    private String professor;
    private List<String> teacherAssistant;
    private List<EducationalMaterial> educationalMaterials;
    private List<Exercise> exercises;

    public Course() {}

    public Course(String courseCode, int groupNumber, String professors,
                  List<String> teacherAssistant, List<EducationalMaterial> educationalMaterials,
                  List<Exercise> exercises) {
        this.courseCode = courseCode;
        this.groupNumber = groupNumber;
        this.professor = professors;
        this.teacherAssistant = teacherAssistant;
        this.educationalMaterials = educationalMaterials;
        this.exercises = exercises;
    }

    public Course(String courseCode, String name) {
        this.courseCode = courseCode;
        this.name = name;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
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
