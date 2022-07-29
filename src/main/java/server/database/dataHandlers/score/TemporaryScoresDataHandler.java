package server.database.dataHandlers.score;

import server.database.MySQLHandler;
import shared.model.university.lesson.score.Score;
import shared.model.university.lesson.score.ScoreType;
import shared.model.user.UserType;
import shared.model.user.professor.Type;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.Array;
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
                    " studentCode = " + studentCode + " AND type = " + ScoreType.TEMPORARY;
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
        query = String.format(query, "studentCode", "student") +  " username = " + username;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return resultSet.getString("studentCode");
            } catch (SQLException ignored) {}
        }
        return null;
    }

    public List<Score> getLessonScores(String lessonCode, String username) {
        if (!isInSameCollege(lessonCode, username)) return null;
        String professorCode = getProfessorCode(username);
        List<Score> scores = new ArrayList<>();
        if (professorCode != null) {
            String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
            query = String.format(query, "*", "score") +
                    " lessonCode = " + lessonCode  + " AND type = " + ScoreType.TEMPORARY;
            String condition = " AND professorCode = " + professorCode;
            if (getProfessorType(username) != null &&
                    getProfessorType(username) != Type.EDUCATIONAL_ASSISTANT) {
                query = query + condition;
            }
            ResultSet resultSet = this.databaseHandler.getResultSet(query);
            if (resultSet != null) {
                try {
                    while (resultSet.next()) {
                        String studentCode = resultSet.getString("studentCode");
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

    private String getProfessorCode(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "professorCode", "professor") +  " username = " + username;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return resultSet.getString("professorCode");
            } catch (SQLException ignored) {}
        }
        return null;
    }

    private Type getProfessorType(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "type", "professor") +  " username = " + username;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return (Type) resultSet.getObject("type");
            } catch (SQLException ignored) {}
        }
        return null;
    }

    private boolean isInSameCollege(String lessonCode, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        String query1 = String.format(query, "collegeCode", "user") +  " username = " + username;
        ResultSet resultSet1 = this.databaseHandler.getResultSet(query1);
        String query2 = String.format(query, "collegeCode", "lesson") +  " lessonCode = " + lessonCode;
        ResultSet resultSet2 = this.databaseHandler.getResultSet(query2);
        if (resultSet1 != null && resultSet2 != null) {
            try {
                if (resultSet1.getString("collegeCode").equals
                        (resultSet2.getString("collegeCode"))) return true;
            } catch (SQLException ignored) {}
        }
        return false;
    }

    public boolean setProtest(String protest, String lessonCode, String username) {
        String studentCode = getStudentCode(username);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "score", "protest = " + protest)
                + " lessonCode = " + lessonCode + " AND studentCode = " + studentCode;
        return this.databaseHandler.updateData(query);
    }

    public boolean setProtestAnswer(String protestAnswer, String lessonCode, String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "score", "protestAnswer = " + protestAnswer)
                + " lessonCode = " + lessonCode + " AND studentCode = " + studentCode;
        return this.databaseHandler.updateData(query);
    }

    public boolean setScore(String score, String lessonCode, String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "score", "score = " + score)
                + " lessonCode = " + lessonCode + " AND studentCode = " + studentCode;
        return this.databaseHandler.updateData(query);
    }

    public boolean registerScore(String score, String lessonCode, String studentCode, String username) {
        String professorCode = getProfessorCode(username);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "score", "lessonCode, studentCode, " +
                "professorCode, score, type", lessonCode + ", " + studentCode +", " + professorCode +
                ", " + score + ", " + ScoreType.TEMPORARY);
        return this.databaseHandler.updateData(query);
    }

    public boolean finalizeScores(String lessonCode, String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "score", "type = " + ScoreType.FINAL)
                + " lessonCode = " + lessonCode + " AND studentCode = " + studentCode;
        return this.databaseHandler.updateData(query);
    }

    public Map<Score, Integer> getFinalScores(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "s.score, s.lessonCode, l.unitNumber",
                "score s", "lesson l", "s.lessonCode = l.lessonCode") +
                " studentCode = " + studentCode + " AND type = " + ScoreType.FINAL;
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
        query = String.format(query, "student", "rate = " + rate)
                + " studentCode = " + studentCode;
        this.databaseHandler.updateData(query);
    }

    public String getStudentCollege(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.collegeCode", "user u", "student s",
                "s.username = u.username") + " studentCode = " + studentCode;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return resultSet.getString("collegeCode");
            } catch (SQLException ignored) {}
        }
        return null;
    }

    public List<Score> getProfessorScores(String professorName, String collegeCode) {
        List<Score> scores = new ArrayList<>();
        List<String> professors = getProfessors(professorName, collegeCode);
        for (String professor : professors) {
            String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
            query = String.format(query, "*", "score") +
                    " professorCode = " + professor + " AND type = " + ScoreType.TEMPORARY;
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
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "professorCode, firstName, lastName", "user")
                + " collegeCode = " + collegeCode;
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

    public List<String> getStudentCodes(String username, String lessonCode) {
        String professorCode = getProfessorCode(username);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "students", "group") + "lessonCode = " + lessonCode +
                " AND professorCode = " + professorCode;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                Array array = resultSet.getArray("students");
                String[] newArray = (String[]) array.getArray();
                return Arrays.asList(newArray);
            } catch (SQLException ignored) {}
        }
        return null;
    }

}
