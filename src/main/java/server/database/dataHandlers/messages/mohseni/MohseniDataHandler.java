package server.database.dataHandlers.messages.mohseni;

import server.database.MySQLHandler;
import shared.model.user.User;
import shared.model.user.student.EducationalStatus;
import shared.model.user.student.Grade;
import shared.model.user.student.Student;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MohseniDataHandler {
    private final MySQLHandler databaseHandler;

    public MohseniDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public Student getStudentInfo(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "username, rate, grade, supervisorCode, enteringYear, status", "student") +
                " studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String rate = resultSet.getString("rate");
                Grade grade = Grade.valueOf(resultSet.getString("grade"));
                String supervisorCode = resultSet.getString("supervisorCode");
                String enteringYear = resultSet.getString("enteringYear");
                EducationalStatus status = EducationalStatus.valueOf(resultSet.getString("status"));
                User user = getUserInfo(username);
                if (user == null) return null;
                return new Student(user.getFirstName(), user.getLastName(), user.getNationalCode(),
                        user.getCollegeCode(), user.getEmailAddress(), user.getPhoneNumber(), user.getImageAddress(),
                        studentCode, Double.parseDouble(rate), grade, supervisorCode, Integer.parseInt(enteringYear),
                        status);
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
                String college = resultSet.getString("collegeCode");
                return new User(firstname, lastname, Long.parseLong(nationalCode),
                        college, email, Long.parseLong(phone), image);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCollegeCode(String name) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "collegeCode", "college") + " name = " + getStringFormat(name);
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

    public List<String> getStudents(String items) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "s.username", "student s", "user u" , "u.username = s.username") + items;
        if (items.equals("")) query = query.substring(0, query.length() - 6);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        List<String> students = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                students.add(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public boolean sendMixMessage(String message, String media, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "mohsenimessages", "receiver, date, message, mediaMessage",
                getStringFormat(username) + ", " + getStringFormat(LocalDateTime.now().toString()) + ", " +
                        getStringFormat(message) + ", " + getStringFormat(media));
        return this.databaseHandler.updateData(query);
    }

    public boolean sendTextMessage(String message,String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "mohsenimessages", "receiver, date, message",
                getStringFormat(username) + ", " + getStringFormat(LocalDateTime.now().toString()) + ", " +
                        getStringFormat(message));
        return this.databaseHandler.updateData(query);
    }

    public boolean sendMediaMessage(String media, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "mohsenimessages", "receiver, date, mediaMessage",
                getStringFormat(username) + ", " + getStringFormat(LocalDateTime.now().toString()) + ", " +
                        getStringFormat(media));
        return this.databaseHandler.updateData(query);
    }

    public List<Student> getStudentsInfo(String code) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.firstName, u.lastName, u.collegeCode, s.studentCode, s.grade",
                "student s", "user u", "u.username = s.username");
        if (code != null) query += " studentCode LIKE " + getStringFormat(code + "%");
        else query = query.substring(0, query.length() - 6);
        List<Student> students = new ArrayList<>();
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            while (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                Grade grade = Grade.valueOf(resultSet.getString("grade"));
                String studentCode = resultSet.getString("studentCode");
                String collegeCode = resultSet.getString("collegeCode");
                Student student = new Student(firstName, lastName, collegeCode, studentCode, grade);
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
