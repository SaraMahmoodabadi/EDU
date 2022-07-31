package server.database.dataHandlers.profile;

import server.database.MySQLHandler;
import shared.model.user.User;
import shared.model.user.professor.MasterDegree;
import shared.model.user.professor.Professor;
import shared.model.user.student.Grade;
import shared.model.user.student.Student;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileDataHandler {
    private final MySQLHandler databaseHandler;

    public ProfileDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public Student getStudentInfo(String username) {
        User user = getUserInfo(username);
        if (user == null) return null;
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "studentCode, rate, grade, supervisorCode, enteringYear", "student") +
                " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String studentCode = resultSet.getString("studentCode");
                String rate = resultSet.getString("rate");
                Grade grade = (Grade) resultSet.getObject("grade");
                String supervisorCode = resultSet.getString("supervisorCode");
                String enteringYear = resultSet.getString("enteringYear");
                return new Student(user.getFirstName(), user.getLastName(), user.getNationalCode(),
                        user.getCollegeCode(), user.getEmailAddress(), user.getPhoneNumber(), user.getImageAddress(),
                        studentCode, Double.parseDouble(rate), grade, supervisorCode, Integer.parseInt(enteringYear));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Professor getProfessorInfo(String username) {
        User user = getUserInfo(username);
        if (user == null) return null;
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "professorCode, roomNumber, degree" ,"professor") +
                " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String professorCode = resultSet.getString("professorCode");
                int roomNumber = resultSet.getInt("roomNumber");
                MasterDegree degree = (MasterDegree) resultSet.getObject("degree");
                return new Professor(user.getFirstName(), user.getLastName(), user.getNationalCode(),
                        user.getCollegeCode(), user.getEmailAddress(), user.getPhoneNumber(), user.getImageAddress(),
                        professorCode, roomNumber, degree);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User getUserInfo(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "firstName, lastName, nationalCode, collegeCode, " +
                "emailAddress, phoneNumber, imageAddress", "user") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String nationalCode = resultSet.getString("nationalCode");
                String email = resultSet.getString("emailAddress");
                String phone = resultSet.getString("phoneNumber");
                String image = resultSet.getString("imageAddress");
                String college = resultSet.getString("college");
                return new User(firstname, lastname, Long.parseLong(nationalCode),
                        college, email, Long.parseLong(phone), image);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateEmail(String username, String email) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "user", "emailAddress = " + email) + " username = " + getStringFormat(username);
        return this.databaseHandler.updateData(query);
    }

    public boolean updatePhone(String username, String phone) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "user", "phoneNumber = " + phone) + " username = " + getStringFormat(username);
        return this.databaseHandler.updateData(query);
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
