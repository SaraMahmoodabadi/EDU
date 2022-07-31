package server.database.dataHandlers.eduServises;

import server.database.MySQLHandler;
import shared.model.message.request.Type;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfessorRequestDataHandler {
    private final MySQLHandler databaseHandler;

    public ProfessorRequestDataHandler(MySQLHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public boolean registerRecommendationAnswer(String studentCode, String firstBlank, String secondBlank,
                                                String thirdBlank, String username) {
        if (!isValidProfessorCode(studentCode, username)) return false;
        String professorCode = getProfessorCode(username);
        if (professorCode == null) return false;
        String finalQuery = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        finalQuery = String.format(finalQuery, "request",  "firstBlank = " + getStringFormat(firstBlank) +
                ", secondBlank = " + getStringFormat(secondBlank) +
                ", thirdBlank = " + getStringFormat(thirdBlank)) + " studentCode = " + getStringFormat(studentCode) +
                " AND type = " + getStringFormat(Type.RECOMMENDATION.toString()) +
                " AND professorCode = " + getStringFormat(professorCode);
        return this.databaseHandler.updateData(finalQuery);
    }

    public boolean registerRequestAnswer(String studentCode, String username, boolean result,
                                         boolean isMinor, String requestType) {
        String professorCode = getProfessorCode(username);
        if (professorCode == null) return false;
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        String request = String.format(query, "request", "result = " + getStringFormat(String.valueOf(result))) +
                " studentCode = " + getStringFormat(studentCode) +
                " AND type = " + getStringFormat(requestType) + " AND professorCode = " + getStringFormat(professorCode);
        if (isMinor) {
            if (isValidProfessorCode(studentCode, username)) {
                query = request;
            }
            else if (isAnotherCollege(studentCode, username)) {
                query = String.format(query, "request", "secondResult = " + getStringFormat(String.valueOf(result))) +
                        " studentCode = " + getStringFormat(studentCode) + " AND type = " + getStringFormat(requestType) +
                        " AND anotherCollegeProfessorCode = " + getStringFormat(professorCode);
            }
            else return false;
        }
        else {
            if (isValidProfessorCode(studentCode, username))
                query = request;
        }
        return this.databaseHandler.updateData(query);
    }

    private boolean isValidProfessorCode(String studentCode, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        String query1 = String.format(query, "professorCode", "student") +  " studentCode = " + getStringFormat(studentCode);
        String query2 = String.format(query, "professorCode", "professor") + " username = " + getStringFormat(username);
        ResultSet resultSet1 = this.databaseHandler.getResultSet(query1);
        ResultSet resultSet2 = this.databaseHandler.getResultSet(query2);
        try {
            if (resultSet1.next() && resultSet2.next()) {
                if (resultSet1.getString("professorCode").equals
                        (resultSet2.getString("professorCode"))) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAnotherCollege(String studentCode, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        String query1 = String.format(query, "professorsCode", "student") +  " studentCode = " + getStringFormat(studentCode);
        String query2 = String.format(query, "professorCode", "professor") + " username = " + getStringFormat(username);
        String query3 = String.format(query, "anotherCollegeProfessorCode", "request")
                +  " studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet1 = this.databaseHandler.getResultSet(query1);
        ResultSet resultSet2 = this.databaseHandler.getResultSet(query2);
        ResultSet resultSet3 = this.databaseHandler.getResultSet(query3);
        try {
            while (resultSet2.next() && resultSet3.next()) {
                if (resultSet3.getString("anotherCollegeProfessorCode").equals
                        (resultSet2.getString("professorCode"))) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getProfessorCode(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "professorCode", "professor") + " username = " + getStringFormat(username);
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

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
