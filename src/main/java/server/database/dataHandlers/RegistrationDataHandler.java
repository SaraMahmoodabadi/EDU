package server.database.dataHandlers;

import server.database.MySQLHandler;
import shared.model.university.lesson.Group;
import shared.model.university.lesson.Lesson;
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
}
