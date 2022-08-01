package shared.model.university.college;

import java.util.List;

public class College {

    private String collegeCode;
    private String name;
    private List<String> lessonsCode;
    private List<String> professorsCode;
    private String educationalAssistantCode;
    private boolean hasAssistant;

    public College() {}

    public College(String collegeCode, String name) {
        this.collegeCode = collegeCode;
        this.name = name;
        this.hasAssistant = false;
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLessonsCode() {
        return lessonsCode;
    }

    public void setLessonsCode(List<String> lessonsCode) {
        this.lessonsCode = lessonsCode;
    }

    public List<String> getProfessorsCode() {
        return professorsCode;
    }

    public void setProfessorsCode(List<String> professorsCode) {
        this.professorsCode = professorsCode;
    }

    public String getEducationalAssistantCode() {
        return educationalAssistantCode;
    }

    public void setEducationalAssistantCode(String educationalAssistantCode) {
        this.educationalAssistantCode = educationalAssistantCode;
    }

    public boolean isHasAssistant() {
        return hasAssistant;
    }

    public void setHasAssistant(boolean hasAssistant) {
        this.hasAssistant = hasAssistant;
    }
}
