package server.database.dataHandlers.edu.score;

import server.database.MySQLHandler;
import shared.model.university.lesson.score.Score;
import shared.model.university.lesson.score.ScoreType;
import shared.model.user.UserType;
import shared.model.user.professor.Type;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TemporaryScoresDataHandler {
    private final MySQLHandler databaseHandler;

    public TemporaryScoresDataHandler(MySQLHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public List<Score> getStudentScores(String username, UserType userType, String studentCode) {
        if (userType == UserType.STUDENT) {
            studentCode = getStudentCode(username);
        }
        List<Score> scores = new ArrayList<>();
        if (studentCode != null) {
            String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
            query = String.format(query, "*", "score") +
                    " studentCode = " + getStringFormat(studentCode) +
                    " AND type = " + getStringFormat(ScoreType.TEMPORARY.toString());
            ResultSet resultSet = this.databaseHandler.getResultSet(query);
            if (resultSet != null) {
                try {
                    while (resultSet.next()) {
                        String lessonCode = resultSet.getString("lessonCode");
                        String score = resultSet.getString("score");
                        String protest = resultSet.getString("protest");
                        String protestAnswer = resultSet.getString("protestAnswer");
                        Score studentScore = new Score(lessonCode, studentCode, score, protest, protestAnswer);
                        scores.add(studentScore);
                    }
                } catch (SQLException ignored) {}
            }
        }
        return scores;
    }

    private String getStudentCode(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "studentCode", "student") +  " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("studentCode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Score> getLessonScores(String lessonCode, String username, String group, String page) {
        if (!isInSameCollege(lessonCode, username)) return null;
        String professorCode = getProfessorCode(username);
        List<Score> scores = new ArrayList<>();
        if (professorCode != null) {
            String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
            query = String.format(query, "*", "score") +
                    " lessonCode = " + getStringFormat(lessonCode)  + " AND type = " +
                    getStringFormat(ScoreType.TEMPORARY.toString()) + " AND lessonGroup = " + getStringFormat(group);
            String condition = " AND professorCode = " + getStringFormat(professorCode);
            if (getProfessorType(username) != null && page.equals("professor")) {
                query = query + condition;
            }
            ResultSet resultSet = this.databaseHandler.getResultSet(query);
            if (resultSet != null) {
                try {
                    while (resultSet.next()) {
                        String professor = resultSet.getString("professorCode");
                        String studentCode = resultSet.getString("studentCode");
                        String score = resultSet.getString("score");
                        String protest = resultSet.getString("protest");
                        String protestAnswer = resultSet.getString("protestAnswer");
                        Score studentScore = new Score(lessonCode, studentCode, professor, score, protest, protestAnswer);
                        scores.add(studentScore);
                    }
                } catch (SQLException ignored) {}
            }
        }
        return scores;
    }

    private String getProfessorCode(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "professorCode", "professor") +  " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("professorCode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Type getProfessorType(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "type", "professor") +  " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return Type.valueOf(resultSet.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isInSameCollege(String lessonCode, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        String query1 = String.format(query, "collegeCode", "user") +  " username = " + getStringFormat(username);
        ResultSet resultSet1 = this.databaseHandler.getResultSet(query1);
        String query2 = String.format(query, "collegeCode", "lesson") +  " lessonCode = " + getStringFormat(lessonCode);
        ResultSet resultSet2 = this.databaseHandler.getResultSet(query2);
        try {
            if (resultSet1.next() && resultSet2.next()) {
                if (resultSet1.getString("collegeCode").equals
                        (resultSet2.getString("collegeCode"))) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setProtest(String protest, String lessonCode, String username) {
        String studentCode = getStudentCode(username);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "score", "protest = " + getStringFormat(protest))
                + " lessonCode = " + getStringFormat(lessonCode) + " AND studentCode = " + getStringFormat(studentCode);
        return this.databaseHandler.updateData(query);
    }

    public boolean setProtestAnswer(String protestAnswer, String lessonCode, String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "score", "protestAnswer = " + getStringFormat(protestAnswer))
                + " lessonCode = " + getStringFormat(lessonCode) +
                " AND studentCode = " + getStringFormat(studentCode);
        return this.databaseHandler.updateData(query);
    }

    public boolean setScore(String score, String lessonCode, String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "score", "score = " + getStringFormat(score))
                + " lessonCode = " + getStringFormat(lessonCode) + " AND studentCode = " + getStringFormat(studentCode);
        return this.databaseHandler.updateData(query);
    }

    public boolean registerScore(String score, String lessonCode, String studentCode, String username) {
        String professorCode = getProfessorCode(username);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "score", "lessonCode, studentCode, lessonGroup, " +
                "professorCode, score, type", getStringFormat(lessonCode) + ", " + getStringFormat(studentCode) +
                ", " + getStringFormat(getLessonGroup(professorCode, lessonCode)) + ", " + getStringFormat(professorCode) +
                ", " + getStringFormat(score) + ", " + getStringFormat(ScoreType.TEMPORARY.toString()));
        return this.databaseHandler.updateData(query);
    }

    public boolean finalizeScores(String lessonCode, String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "score", "type = " + getStringFormat(ScoreType.FINAL.toString()))
                + " lessonCode = " + getStringFormat(lessonCode) + " AND studentCode = " + getStringFormat(studentCode);
        return this.databaseHandler.updateData(query);
    }

    public Map<Score, Integer> getFinalScores(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "s.score, s.lessonCode, l.unitNumber",
                "score s", "lesson l", "s.lessonCode = l.lessonCode") +
                " studentCode = " + getStringFormat(studentCode) +
                " AND type = " + getStringFormat(ScoreType.FINAL.toString());
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        Map<Score, Integer> scores = new HashMap<>();
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    String score = resultSet.getString("score");
                    String lessonCode = resultSet.getString("lessonCode");
                    Integer unitNumber = resultSet.getInt("unitNumber");
                    Score studentScore = new Score(lessonCode, studentCode, score);
                    scores.put(studentScore, unitNumber);
                }
            } catch (SQLException ignored) {}
        }
        return scores;
    }

    public void registerRate(double rate, String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "student", "rate = " + getStringFormat(String.valueOf(rate)))
                + " studentCode = " + getStringFormat(studentCode);
        this.databaseHandler.updateData(query);
    }

    public String getStudentCollege(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.collegeCode", "user u", "student s",
                "s.username = u.username") + " studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("collegeCode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Score> getProfessorScores(String professorName, String collegeCode) {
        List<Score> scores = new ArrayList<>();
        List<String> professors = getProfessors(professorName, collegeCode);
        for (String professor : professors) {
            String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
            query = String.format(query, "*", "score") +
                    " professorCode = " + getStringFormat(professor) +
                    " AND type = " + getStringFormat(ScoreType.TEMPORARY.toString());
            ResultSet resultSet = this.databaseHandler.getResultSet(query);
            if (resultSet != null) {
                try {
                    while (resultSet.next()) {
                        String studentCode = resultSet.getString("studentCode");
                        String lessonCode = resultSet.getString("lessonCode");
                        String score = resultSet.getString("score");
                        String protest = resultSet.getString("protest");
                        String protestAnswer = resultSet.getString("protestAnswer");
                        Score studentScore = new Score(lessonCode, studentCode, professor,
                                score, protest, protestAnswer);
                        scores.add(studentScore);
                    }
                } catch (SQLException ignored) {}
            }
        }
        return scores;
    }

    private List<String> getProfessors(String name, String collegeCode) {
        List<String> professors = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "p.professorCode, u.firstName, u.lastName",
                "user u", "professor p", "u.username = p.username")
                + " collegeCode = " + getStringFormat(collegeCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    String professorCode = resultSet.getString("professorCode");
                    if (name.equals(firstName + " " + lastName)) professors.add(professorCode);
                }
            } catch (SQLException ignored) {}
        }
        return professors;
    }

    public List<String> getStudentCodes(String lessonCode, String group, String username) {
        String professorCode = getProfessorCode(username);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "*", "edu.group") + " lessonCode = " + getStringFormat(lessonCode) +
                " AND groupNumber = " + getStringFormat(group);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                List<String> list = new ArrayList<>();
                String professor = resultSet.getString("professorCode");
                if (!professor.equals(professorCode)) return list;
                list.add(professor);
                String students = resultSet.getString("students");
                if (students == null) return list;
                String array = students.substring(1, students.length() - 1);
                list.addAll(Arrays.asList(array.split(", ")));
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getLessonGroup(String professorCode, String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "groupNumber", "edu.group") +  " lessonCode = " + getStringFormat(lessonCode) +
                " AND professorCode = " + getStringFormat(professorCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("groupNumber");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }

}
