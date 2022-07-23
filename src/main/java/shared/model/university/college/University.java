package shared.model.university.college;

import java.util.ArrayList;
import java.util.List;

public class University {
    private List<String> collegeName;
    private static University university;

    private University() {
        collegeName = new ArrayList<>();
    }

    public List<String> getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(List<String> collegeName) {
        this.collegeName = collegeName;
    }

    public void addCollege(String name) {
        this.collegeName.add(name);
    }

    public static University getUniversity() {
        if (university == null) {
            university = new University();
        }
        return university;
    }
}
