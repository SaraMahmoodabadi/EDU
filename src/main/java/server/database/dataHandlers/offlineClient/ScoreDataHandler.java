package server.database.dataHandlers.offlineClient;

import server.database.MySQLHandler;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScoreDataHandler {
    private final MySQLHandler databaseHandler;

    public ScoreDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public String getStudentCode(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "studentCode", "student") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("studentCode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "-";
    }

    private String getStringFormat(String string) {
        return "'" + string + "'";
    }
}
