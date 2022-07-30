package server.database.dataHandlers.unitSelection;

import server.database.MySQLHandler;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnitSelectionDataHandler {
    private final MySQLHandler databaseHandler;

    public UnitSelectionDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public boolean updateUnitSelectionTime(String items, String collegeCode, String time) {
        List<String> students = getStudentCodes(collegeCode);
        for (String student : students) {
            String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
            query = String.format(query, "student", "unitSelectionTime = " + getStringFormat(time)) + items;
            if (!this.databaseHandler.updateData(query)) return false;
        }
        return true;
    }

    private List<String> getStudentCodes(String collegeCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "s.studentCode", "student s", "user u", "u.username  = s.username") +
                " " + "u.collegeCode = " + getStringFormat(collegeCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        List<String> studentsCode = new ArrayList<>();
        try {
            while (resultSet.next()) {
                studentsCode.add(resultSet.getString("studentCode"));
            }
        } catch (SQLException ignored) {}
        return studentsCode;
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
