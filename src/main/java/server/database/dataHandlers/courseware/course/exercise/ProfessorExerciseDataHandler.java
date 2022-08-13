package server.database.dataHandlers.courseware.course.exercise;

import server.database.MySQLHandler;
import shared.model.courseware.educationalMaterial.ItemType;
import shared.model.courseware.exercise.Answer;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProfessorExerciseDataHandler {
    private final MySQLHandler databaseHandler;

    public ProfessorExerciseDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public String getExerciseName(String exerciseCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "name", "exercise") + " exerciseCode = " + getStringFormat(exerciseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public ArrayList<Answer> getAnswers(String exerciseCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "studentCode, sendTime, text, fileAddress, score, type", "answer") +
                " exerciseCode = " + getStringFormat(exerciseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        ArrayList<Answer> answers = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String studentCode = resultSet.getString("studentCode");
                String name = getStudentName(studentCode);
                String sendTime = resultSet.getString("sendTime");
                String text = resultSet.getString("text");
                String fileAddress = resultSet.getString("fileAddress");
                String score = resultSet.getString("score");
                String studentScore = score == null ? "" : score;
                ItemType type = ItemType.valueOf(resultSet.getString("type"));
                Answer answer;
                if (type == ItemType.TEXT)
                    answer = new Answer(exerciseCode, studentCode, name, type, sendTime, text, studentScore);
                else answer = new Answer(exerciseCode, studentCode, name, sendTime, type, fileAddress, studentScore);
                answers.add(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }

    private String getStudentName(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.firsName, u.lastName", "user u", "student s", "s.username = u.username") +
                " s.studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                return firstname + " " + lastname;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean registerScore(String exerciseCode, String studentCode, String score) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "answer", "score = " + getStringFormat(score)) + " exerciseCode = " +
                getStringFormat(exerciseCode) + " AND studentCode = " + getStringFormat(studentCode);
        return this.databaseHandler.updateData(query);
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
