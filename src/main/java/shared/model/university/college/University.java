package shared.model.university.college;

import java.util.ArrayList;
import java.util.List;

public class University {
    private List<String> collegeName;
    private List<String> major;
    private static University university;

    private University() {
        collegeName = new ArrayList<>();
        major = new ArrayList<>();
        makeNameList();
        makeMajorList();
    }

    private void makeMajorList() {
        major.add("Computer Engineering");
        major.add("Electrical Engineering");
        major.add("Mechanical Engineering");
        major.add("Aerospace Engineering");
        major.add("Chemical Engineering");
    }

    private void makeNameList() {
        collegeName.add("Computer Engineering");
        collegeName.add("Electrical Engineering");
        collegeName.add("Mechanical Engineering");
        collegeName.add("Aerospace Engineering");
        collegeName.add("Chemical Engineering");
        collegeName.add("Religious Center");
    }

    public List<String> getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(List<String> collegeName) {
        this.collegeName = collegeName;
    }

    public List<String> getMajor() {
        return major;
    }

    public void setMajor(List<String> major) {
        this.major = major;
    }

    public void addCollege(String name) {
        this.collegeName.add(name);
    }

    public void addMajor(String major) {
        this.major.add(major);
    }

    public static University getUniversity() {
        if (university == null) {
            university = new University();
        }
        return university;
    }
}
