package server.database.dataHandlers;

import server.database.MySQLHandler;
import shared.model.university.lesson.Group;
import shared.model.university.lesson.Lesson;
import shared.model.user.professor.MasterDegree;
import shared.model.user.professor.Professor;
import shared.model.user.professor.Type;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDataHandler {
    private final MySQLHandler dataBaseHandler;

    public RegistrationDataHandler(MySQLHandler dataHandler) {
        this.dataBaseHandler = dataHandler;
    }

    public List<Lesson> getAllLessons() {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getAllLessons");
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            return makeLessons(resultSet);
        }
        return null;
    }

    public List<Lesson> getDesiredLessons(String items) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getAllLessons") + " " + items;
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            return makeLessons(resultSet);
        }
        return null;
    }

    private List<Lesson> makeLessons(ResultSet resultSet) {
        List<Lesson> lessons = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String lessonCode = resultSet.getString("lessonCode");
                int unitNumber = Integer.parseInt(resultSet.getString("unitNumber"));
                String registrationNumber = resultSet.getString("registrationNumber");
                String examTime = resultSet.getString("examTime");
                String days = resultSet.getString("days");
                String classTime = resultSet.getString("classTime");
                String prerequisites = resultSet.getString("prerequisites");
                String theNeed = resultSet.getString("theNeed");
                List<Group> groups = getGroups(lessonCode);
                Lesson lesson = new Lesson(name, lessonCode, unitNumber, registrationNumber,
                        examTime, days, classTime, prerequisites, theNeed);
                if (groups != null) {
                    for (Group group : groups) {
                        lesson.setGroup(group.getGroupNumber());
                        lesson.setProfessorCode(group.getProfessorCode());
                        lesson.setCapacity(group.getCapacity());
                        lessons.add(lesson);
                    }
                }
                else lessons.add(lesson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lessons;
    }

    private List<Group> getGroups(String lessonCode) {
        String query = Config.getConfig
                (ConfigType.QUERY).getProperty("getAllGroups") + " " + lessonCode;
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            List<Group> groups = new ArrayList<>();
            try {
                while (resultSet.next()) {
                    int capacity = Integer.parseInt(resultSet.getString("capacity"));
                    int groupNumber = Integer.parseInt(resultSet.getString("groupNumber"));
                    String professorCode = resultSet.getString("professorCode");
                    Group group = new Group(lessonCode, capacity, professorCode);
                    group.setGroupNumber(groupNumber);
                    groups.add(group);
                }
                return groups;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getCollegeCode(String collegeName){
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getCollegeCode") + " " + collegeName;
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return resultSet.getString("collegeCode");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean existLesson(String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("existLessonCode");
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.getString("name") != null) return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean makeLesson(Lesson lesson) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("makeLesson");
        query = String.format(query, lesson.getLessonCode() + ", " + lesson.getName() +
                ", " + lesson.getCollegeCode() + ", " + lesson.getUnitNumber() +
                ", " + lesson.getGrade() + ", " + lesson.getPrerequisites().toString() +
                ", " + lesson.getTheNeed().toString() + ", [1], " + lesson.getDays().toString() +
                ", " + lesson.getClassTime() + ", " + lesson.getExamTime());
        String query2 = Config.getConfig(ConfigType.QUERY).getProperty("makeGroup");
        query2 = String.format(query2, lesson.getLessonCode() + ", " + lesson.getProfessorCode() +
                ", " + lesson.getCapacity());
        boolean b1 = this.dataBaseHandler.updateData(query);
        boolean b2 = this.dataBaseHandler.updateData(query2);
        return b1 && b2;
    }

    public boolean makeGroup(Group group) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("makeGroup");
        query = String.format(query, group.getLessonCode() + ", " + group.getProfessorCode() +
                ", " + group.getCapacity());
        return this.dataBaseHandler.updateData(query);
    }

    public boolean editLesson(String items, String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("updateLesson");
        query = String.format(query, items) + " " + lessonCode;
        return this.dataBaseHandler.updateData(query);
    }

    public boolean removeGroup(String lessonCode, String groupNumber) {
        if (!existGroup(lessonCode, groupNumber)) return false;
        String query = Config.getConfig(ConfigType.QUERY).getProperty("removeGroup");
        query = String.format(query, "lessonCode = " + lessonCode + " AND groupNumber = " + groupNumber);
        return this.dataBaseHandler.updateData(query);
    }

    public boolean removeLesson(String lessonCode) {
        if (!existLesson(lessonCode)) return false;
        String query = Config.getConfig(ConfigType.QUERY).getProperty("removeLesson") + " " + lessonCode;
        String query2 = Config.getConfig(ConfigType.QUERY).getProperty("removeGroup");
        query2 = String.format(query2, "lessonCode = " + lessonCode);
        this.dataBaseHandler.updateData(query2);
        return this.dataBaseHandler.updateData(query);
    }

    public boolean existGroup(String lessonCode, String groupNumber) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("existGroup");
        query = String.format(query, "lessonCode = " + lessonCode + " AND groupNumber = " + groupNumber);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.getString("professorCode") != null) return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public List<Professor> getAllProfessors() {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getAllProfessors");
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
           return makeProfessors(resultSet);
        }
        return null;
    }

    private List<Professor> makeProfessors(ResultSet resultSet) {
        List<Professor> professors = new ArrayList<>();
        try {
            while (resultSet.next()) {
             String fullName = resultSet.getString("firstName") +
                     resultSet.getString("lastName");
             String professorCode = resultSet.getString("professorCode");
             String collegeCode = resultSet.getString("collegeCode");
             MasterDegree degree = (MasterDegree) resultSet.getObject("degree");
             Type type = (Type) resultSet.getObject("type");
             Professor professor = new Professor(fullName, collegeCode, professorCode, degree, type);
             professors.add(professor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return professors;
    }
}
