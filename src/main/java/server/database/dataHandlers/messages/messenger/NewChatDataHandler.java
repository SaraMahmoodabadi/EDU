package server.database.dataHandlers.messages.messenger;

import server.database.MySQLHandler;
import shared.model.message.request.Type;
import shared.model.user.User;
import shared.model.user.UserType;
import shared.model.user.student.Grade;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewChatDataHandler {
    private final MySQLHandler databaseHandler;

    public NewChatDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public boolean sendMessage(String sender, String senderType, String receiver,
                               String receiverType, String message, boolean isMedia) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "messenger", "sender, receiver, senderType, receiverType," +
                        " sendMessageTime, messageText, isMedia",
                getStringFormat(sender) + ", " +getStringFormat(receiver) + ", " + getStringFormat(senderType) +", " +
                        getStringFormat(receiverType) + ", " + getStringFormat(LocalDateTime.now().toString()) + ", " +
                        getStringFormat(message)) + ", " + getStringFormat(String.valueOf(isMedia));
        return this.databaseHandler.updateData(query);
    }

    public String findUsername(String user, String userCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "username", user) + " " + user + "Code = " + getStringFormat(userCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean sendRequest(String receiver, String sender, Type type) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "request", "studentCode, professorCode, type, date1",
                getStringFormat(sender) + ", " +getStringFormat(receiver) + ", " + getStringFormat(type.toString()) +
                getStringFormat(LocalDateTime.now().toString()));
        return this.databaseHandler.updateData(query);
    }
    /**receiver is the username of someone who receives request and
     sender is the username of someone who sends request
     */

    public List<User> getCollegeStudents(String collegeCode, String userName) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "firstName, lastName, username", "user") + " userType = " +
                getStringFormat(UserType.STUDENT.toString()) + " AND collegeCode = " + getStringFormat(collegeCode) +
                " AND username != " + getStringFormat(userName);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        List<User> users = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String username = resultSet.getString("username");
                User user = new User(firstname, lastname, username, UserType.STUDENT);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public int findEnteringYear(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "enteringYear", "student") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getInt("enteringYear");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Grade findGrade(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "grade", "student") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return Grade.valueOf(resultSet.getString("grade"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getSameYearStudents(int year, Grade grade, String userName) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.firstName, u.lastName, u.username", "user u", "student s",
                "u.username = s.username") + " s.enteringYear = " + year + " AND s.grade = " +
                getStringFormat(grade.toString()) + " AND u.username != " + getStringFormat(userName);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        List<User> users = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String username = resultSet.getString("username");
                User user = new User(firstname, lastname, username, UserType.STUDENT);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User findSupervisor(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.firstName, u.lastName, u.username", "user u",
                "professor p ON p.username = u.username join student s",
                "p.professorCode = s.supervisorCode") + " s.username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String supervisorUsername = resultSet.getString("username");
                return new User(firstname, lastname, supervisorUsername, UserType.PROFESSOR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getProfessorStudents(String professorCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.firstName, u.lastName, u.username", "user u", "student s",
                "u.username = s.username") + " s.supervisorCode = " + getStringFormat(professorCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        List<User> users = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String username = resultSet.getString("username");
                User user = new User(firstname, lastname, username, UserType.STUDENT);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public String findProfessorCode(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "professorCode", "professor") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("enteringYear");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateChat(String receiver, String sender, String message) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "removeData");
        query = String.format(query, "chat") + " user1 = " + getStringFormat(receiver) + " AND user2 = " +
                getStringFormat(sender);
        this.databaseHandler.updateData(query);
        query = String.format(query, "chat") + " user1 = " + getStringFormat(sender) + " AND user2 = " +
                getStringFormat(receiver);
        this.databaseHandler.updateData(query);
        query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "chat", "user1, user2, sender, lastMessage, date",
                getStringFormat(receiver) + ", " + getStringFormat(sender) + ", " +
                        getStringFormat(sender) + ", " +
                        getStringFormat(message) + ", " +
                        getStringFormat(LocalDateTime.now().toString()));
        this.databaseHandler.updateData(query);
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
