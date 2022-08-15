package server.database.dataHandlers.offlineClient;

import server.database.MySQLHandler;
import shared.model.user.User;
import shared.model.user.UserType;
import shared.model.user.professor.MasterDegree;
import shared.model.user.professor.Professor;
import shared.model.user.professor.Type;
import shared.model.user.student.EducationalStatus;
import shared.model.user.student.Grade;
import shared.model.user.student.Student;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDataHandler {
    private final MySQLHandler databaseHandler;

    public UserDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public Student getStudent(String username) {
        User user = getUser(username);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "*", "student") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String studentCode = resultSet.getString("studentCode");
                String rate = resultSet.getString("rate");
                String enteringYear = resultSet.getString("enteringYear");
                String supervisor = getSupervisor(resultSet.getString("supervisorCode"));
                boolean registrationLicense = Boolean.parseBoolean(resultSet.getString("registrationLicense"));
                String registrationTime = resultSet.getString("registrationTime");
                EducationalStatus status = EducationalStatus.valueOf(resultSet.getString("status"));
                Grade grade = Grade.valueOf(resultSet.getString("grade"));
                return new Student(user.getFirstName(), user.getLastName(), user.getNationalCode(),
                        user.getCollegeCode(), user.getUsername(), user.getPassword(), user.getUserType(),
                        user.getEmailAddress(), user.getPhoneNumber(), user.getThisLogin(), user.getLastLogin(),
                        studentCode, rate, enteringYear, supervisor, registrationLicense, registrationTime,
                        status, grade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Professor getProfessor(String username) {
        User user = getUser(username);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "*", "professor") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String professorCode = resultSet.getString("professorCode");
                String room = resultSet.getString("roomNumber");
                MasterDegree degree = MasterDegree.valueOf(resultSet.getString("degree"));
                Type type = Type.valueOf(resultSet.getString("type"));
                return new Professor(user.getFirstName(), user.getLastName(), user.getNationalCode(),
                        user.getCollegeCode(), user.getUsername(), user.getPassword(), user.getUserType(),
                        user.getEmailAddress(), user.getPhoneNumber(), user.getThisLogin(), user.getLastLogin(),
                        professorCode, room, degree, type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "*", "user") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                UserType userType = UserType.valueOf(resultSet.getString("userType"));
                String password = resultSet.getString("password");
                String nationalCode = resultSet.getString("nationalCode");
                String phoneNumber = resultSet.getString("phoneNumber");
                String email = resultSet.getString("emailAddress");
                String college = getCollegeName(resultSet.getString("collegeCode"));
                String thisLogin = resultSet.getString("thisLogin");
                String lastLogin = resultSet.getString("lastLogin");
                return new User(firstname, lastname, Long.parseLong(nationalCode), college,
                        username, password, userType, email, phoneNumber, thisLogin, lastLogin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getCollegeName(String collegeCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "name", "college") + " collegeCode = " + getStringFormat(collegeCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "-";
    }

    private String getSupervisor(String supervisorCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.firstName, u.lastName", "user u", "professor p", "p.username = u.username")
                + " p.professorCode = " + getStringFormat(supervisorCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                return firstname + " " + lastname;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "-";
    }

    public String getProfileImage(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "imageAddress", "user") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("imageAddress");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getStringFormat(String string) {
        return "'" + string + "'";
    }
}
