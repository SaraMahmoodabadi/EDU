package shared.model.courseware.educationalMaterial;

import java.util.List;

public class EducationalMaterial {

    private String educationalMaterialCode;
    private String name;
    private List<Item> items;

    public EducationalMaterial(String courseCode,String name) {
        this.educationalMaterialCode = this.generateCode(courseCode);
        this.name = name;
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

    private String generateCode(String courseCode) {
        return courseCode + "_" + this.name;
    }
}
