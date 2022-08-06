package server.database.dataHandlers.edu.registration;

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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RegistrationDataHandler {
    private final MySQLHandler dataBaseHandler;

    public RegistrationDataHandler(MySQLHandler dataHandler) {
        this.dataBaseHandler = dataHandler;
    }

    public List<Lesson> getAllLessons() {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getAllLessons");
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next())
                    return makeLessons(resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Lesson> getDesiredLessons(String items) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getAllLessons") + " " + items;
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
                (ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "*", "edu.group") + " lessonCode = " + getStringFormat(lessonCode);
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

    public List<String> getLessonGroups(String lessonCode) {
        List<String> lessonGroups = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "groupNumber", "edu.group") + " lessonCode = " + getStringFormat(lessonCode);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    lessonGroups.add(resultSet.getString("groupNumber"));
                }
            } catch (SQLException ignored) {}
        }
        return lessonGroups;
    }

    public void updateLessonGroups(String lessonCode, List<String> groups) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "lesson", "edu.groups = " + getStringFormat(groups.toString())) + " lessonCode = " +
                getStringFormat(lessonCode);
        this.dataBaseHandler.updateData(query);
    }

    public String getCollegeCode(String collegeName){
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getCollegeCode")
                + " " + getStringFormat(collegeName);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next())
                    return resultSet.getString("collegeCode");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean existLesson(String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "existLessonCode") +
                " " + getStringFormat(lessonCode);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next())
                    if (resultSet.getString("name") != null) return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean makeLesson(Lesson lesson) {
        if (lesson.getPrerequisites() == null) lesson.setPrerequisites(new ArrayList<>());
        if (lesson.getTheNeed() == null) lesson.setTheNeed(new ArrayList<>());
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "makeLesson");
        query = String.format(query, getStringFormat(lesson.getLessonCode()) + ", " + getStringFormat(lesson.getName()) +
                ", " + getStringFormat(lesson.getCollegeCode()) + ", " + lesson.getUnitNumber() +
                ", " + getStringFormat(lesson.getGrade().toString()) + ", " + getStringFormat(lesson.getPrerequisites().toString()) +
                ", " + getStringFormat(lesson.getTheNeed().toString()) + ", [1], " + getStringFormat(lesson.getDays().toString()) +
                ", " + getStringFormat(lesson.getClassTime()) + ", " + getStringFormat(lesson.getExamTime()));
        String query2 = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "makeGroup");
        query2 = String.format(query2, "'1', " +getStringFormat(lesson.getLessonCode()) + ", " +
                getStringFormat(lesson.getProfessorCode()) +
                ", " + lesson.getCapacity());
        boolean b1 = this.dataBaseHandler.updateData(query);
        boolean b2 = this.dataBaseHandler.updateData(query2);
        return b1 && b2;
    }

    public boolean makeGroup(Group group) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "makeGroup");
        query = String.format(query, getStringFormat(String.valueOf(generateGroupNumber(group.getLessonCode()))) + ", " +
                getStringFormat(group.getLessonCode()) + ", " + getStringFormat(group.getProfessorCode()) +
                ", " + group.getCapacity());
        return this.dataBaseHandler.updateData(query);
    }

    public boolean editLesson(String items, String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateLesson");
        query = String.format(query, items) + " " + getStringFormat(lessonCode);
        return this.dataBaseHandler.updateData(query);
    }

    public boolean removeGroup(String lessonCode, String groupNumber) {
        if (!existGroup(lessonCode, groupNumber)) return false;
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "removeGroup");
        query = String.format(query, "lessonCode = " + getStringFormat(lessonCode) +
                " AND groupNumber = " + getStringFormat(groupNumber));
        return this.dataBaseHandler.updateData(query);
    }

    public boolean removeLesson(String lessonCode) {
        if (!existLesson(lessonCode)) return false;
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "removeLesson")
                + " " + getStringFormat(lessonCode);
        String query2 = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "removeGroup");
        query2 = String.format(query2, "lessonCode = " + getStringFormat(lessonCode));
        this.dataBaseHandler.updateData(query2);
        return this.dataBaseHandler.updateData(query);
    }

    public boolean existGroup(String lessonCode, String groupNumber) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "existGroup");
        query = String.format(query, "lessonCode = " + getStringFormat(lessonCode) +
                " AND groupNumber = " + getStringFormat(groupNumber));
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next())
                    if (resultSet.getString("professorCode") != null) return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public List<Professor> getAllProfessors() {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getAllProfessors");
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
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String fullName = firstName + " " + lastName;
                String professorCode = resultSet.getString("professorCode");
                String collegeCode = resultSet.getString("collegeCode");
                MasterDegree degree = MasterDegree.valueOf(resultSet.getString("degree"));
                Type type = Type.valueOf(resultSet.getString("type"));
                Professor professor = new Professor(fullName, collegeCode, professorCode, degree, type);
                professors.add(professor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return professors;
    }

    public boolean editProfessor(String professorCode, String items) {
        if (!existProfessor(professorCode)) return false;
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateProfessor");
        query = String.format(query, items) + " " + getStringFormat(professorCode);
        return this.dataBaseHandler.updateData(query);
    }

    public boolean editUser(String username, String items, String professorCode) {
        if (!existProfessor(professorCode)) return false;
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateUser");
        query = String.format(query, items) + " " + getStringFormat(username);
        return this.dataBaseHandler.updateData(query);
    }

    public boolean appointment(String professorCode, String items, String collegeCode) {
        if (getAssistant(collegeCode) != null) return false;
        if (!getProfessorCollegeCode(professorCode).equals(collegeCode)) return false;
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateCollege");
        query = String.format(query, items) + " " + getStringFormat(collegeCode);
        return this.dataBaseHandler.updateData(query);
    }

    public boolean deposal(String professorCode, String items, String collegeCode) {
        if (getAssistant(collegeCode) == null ||
                !Objects.equals(getAssistant(collegeCode), professorCode)) return false;
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateCollege");
        query = String.format(query, items) + " " + getStringFormat(collegeCode);
        return this.dataBaseHandler.updateData(query);
    }

    public boolean removeProfessor(String professorCode) {
        if (!existProfessor(professorCode)) return false;
        String query = Config.getConfig(ConfigType.QUERY).getProperty
                (String.class, "removeProfessor") + " " + getStringFormat(professorCode);
        return this.dataBaseHandler.updateData(query);
    }

    private String getAssistant(String collegeCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "educationalAssistantCode")
                + " " + getStringFormat(collegeCode);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next()){
                    if (resultSet.getString("educationalAssistantCode") != null) {
                        return resultSet.getString("educationalAssistantCode");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean existProfessor(String professorCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty
                (String.class, "existProfessor") + " " + getStringFormat(professorCode);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next()) {
                    if (resultSet.getString("username") != null) {
                        return true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getLessonCollegeCode(String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty
                (String.class, "getLessonCollege") + " " + getStringFormat(lessonCode);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next()) {
                    if (resultSet.getString("collegeCode") != null) {
                        return resultSet.getString("collegeCode");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getProfessorCollegeCode(String professorCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty
                (String.class, "getProfessorCollege") + " " + getStringFormat(professorCode);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next()) {
                    if (resultSet.getString("collegeCode") != null) {
                        return resultSet.getString("collegeCode");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean updateProfessorLessons(String professorCode, List<String> lessons) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateProfessor");
        query = String.format(query, "lessonsCode = " + getStringFormat(lessons.toString()))
                + " " + getStringFormat(professorCode);
        return this.dataBaseHandler.updateData(query);
    }

    public List<String> getProfessorLessons(String professorCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getUserLessons");
        query = String.format(query, "professor") + " professorCode = " + getStringFormat(professorCode);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next()) {
                    String lessons = resultSet.getString("lessonCode");
                    String lessonArray = lessons.substring(1, lessons.length() - 1);
                    return new ArrayList<>(Arrays.asList(lessonArray.split(", ")));
                }
            } catch (SQLException ignored) {}
        }
        return null;
    }

    public List<String> getProfessorsByLesson(String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getProfessorByLesson");
        query = query + " lessonCode = " + getStringFormat(lessonCode);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        List<String> professors = new ArrayList<>();
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    professors.add(resultSet.getString("professorCode"));
                }
            } catch (SQLException ignored) {}
        }
        return professors;
    }

    public String getProfessorByLesson(String lessonCode, String groupNumber) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getProfessorByLesson");
        query = query + " lessonCode = " + getStringFormat(lessonCode) + " AND groupNumber = " + getStringFormat(groupNumber);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        String professor = null;
        if (resultSet != null) {
            try {
                if (resultSet.next()) {
                    professor = resultSet.getString("professorCode");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return professor;
    }

    private int generateGroupNumber(String lessonCode) {
        String query = Config.getConfig
                (ConfigType.QUERY).getProperty(String.class, "getAllGroups") + " " + getStringFormat(lessonCode);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        List<Integer> groups = new ArrayList<>();
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    int n = Integer.parseInt(resultSet.getString("groupNumber"));
                    groups.add(n);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        for (int i = 1; i <= groups.size() + 1; i++) {
            if (!groups.contains(i)) return i;
        }
        return 1;
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
