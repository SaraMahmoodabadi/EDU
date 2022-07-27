package server.database.dataHandlers.score;

import server.database.MySQLHandler;
import shared.model.university.lesson.score.Score;
import shared.model.university.lesson.score.ScoreType;
import shared.model.user.professor.MasterDegree;
import shared.model.user.professor.Type;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TemporaryScoresDataHandler {
    private final MySQLHandler databaseHandler;

    public TemporaryScoresDataHandler(MySQLHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public List<Score> getStudentScores(String username) {
        String studentCode = getStudentCode(username);
        List<Score> scores = new ArrayList<>();
        if (studentCode != null) {
            String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
            query = String.format(query, "*", "score") +
                    " studentCode = " + studentCode + " AND type = " + ScoreType.TEMPORARY;
            ResultSet resultSet = this.databaseHandler.getResultSet(query);
            if (resultSet != null) {
                try {
                    while (resultSet.next()) {
                        String lessonCode = resultSet.getString("lessonCode");
                        double score = resultSet.getDouble("score");
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
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
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
            String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
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
                        double score = resultSet.getDouble("score");
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
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
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
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
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
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
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
        String query = Config.getConfig(ConfigType.QUERY).getProperty("updateData");
        query = String.format(query, "score", "protest = " + protest)
                + " lessonCode = " + lessonCode + " AND studentCode = " + studentCode;
        return this.databaseHandler.updateData(query);
    }
}
