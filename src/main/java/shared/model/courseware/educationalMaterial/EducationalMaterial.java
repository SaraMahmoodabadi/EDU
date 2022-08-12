package shared.model.courseware.educationalMaterial;

import java.util.List;

public class EducationalMaterial {
    private String courseCode;
    private String educationalMaterialCode;
    private String name;
    private List<Item> items;
    private String time;

    public EducationalMaterial() {}

    public EducationalMaterial(String courseCode,String name) {
        this.educationalMaterialCode = this.generateCode(courseCode);
        this.name = name;
    }

    public EducationalMaterial(String courseCode, String eduMaterialCode, String name, String time) {
        this.courseCode = courseCode;
        this.educationalMaterialCode = eduMaterialCode;
        this.name = name;
        this.time = time;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getEducationalMaterialCode() {
        return educationalMaterialCode;
    }

    public void setEducationalMaterialCode(String educationalMaterialCode) {
        this.educationalMaterialCode = educationalMaterialCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String generateCode(String courseCode) {
        return courseCode + "_" + this.name;
    }
}
