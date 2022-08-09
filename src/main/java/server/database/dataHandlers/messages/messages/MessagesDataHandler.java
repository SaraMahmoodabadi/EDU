package server.database.dataHandlers.messages.messages;

import server.database.MySQLHandler;
import shared.model.message.chatMessages.Message;
import shared.model.message.request.Type;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessagesDataHandler {
    private final MySQLHandler databaseHandler;

    public MessagesDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public String findUserCode(String userType, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, userType + "Code", userType) + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString(userType + "Code");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Message> getAllSendMessageRequests(String username) {
        List<Message> requests = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.firstName, u.lastName, u.username, r.date1", "request r", "user u",
                "u.username = r.professorCode") + " r.studentCode = " + getStringFormat(username) +
                " AND r.type = " + getStringFormat(Type.SEND_MESSAGE.toString());
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            while (resultSet.next()) {
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String name = firstname + " " + lastname;
                String user = resultSet.getString("username");
                String date = resultSet.getString("date1");
                String userMessage = "request : " + Type.SEND_MESSAGE;
                Message message = new Message(name, user, userMessage, date, "request");
                requests.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public List<Message> getAllProfessorRequests(String username) {
        List<Message> requests = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.firstName, u.lastName, u.username, r.date1, r.type", "request r", "professor p",
                "p.professorCode = r.professorCode JOIN user u ON u.username = p.username") +
                " u.username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            while (resultSet.next()) {
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String name = firstname + " " + lastname;
                String user = resultSet.getString("username");
                String date = resultSet.getString("date1");
                String type = resultSet.getString("type");
                String userMessage = "request : " + type;
                Message message = new Message(name, user, userMessage, date, "request");
                requests.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public List<Message> getAllMohseniMessages(String username) {
        List<Message> requests = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "date, message", "mohsenimessages") + " receiver = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            while (resultSet.next()) {
                String date = resultSet.getString("date");
                String mohseniMessage = resultSet.getString("message");
                String userMessage = "Mr.Mohseni : ";
                if (mohseniMessage != null) userMessage += mohseniMessage;
                else userMessage += "media";
                Message message = new Message("Mr.Mohseni", "mohseni", userMessage, date, "mohseniMessage");
                requests.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public List<Message> getAllAdminMessages(String username) {
        List<Message> requests = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "adminDate, answers", "adminmessages") + " user = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            while (resultSet.next()) {
                String date = resultSet.getString("adminDate");
                if (date == null) continue;
                String answers = resultSet.getString("answers");
                String userMessage = "admin message : " + answers.substring(1, answers.length() - 1);
                Message message = new Message("Admin", "1", userMessage, date, "adminMessage");
                requests.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public String findMinorMajor(String studentCode, String date1) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "c.name", "request r", "professor p", "p.professorCode = r.anotherCollegeAssistantCode" +
                " JOIN user u ON p.username = u.username" +
                " JOIN college c ON c.collegeCode = u.collegeCode") +
                " r.studentCode = " + getStringFormat(studentCode) +
                " AND r.type = " + getStringFormat(Type.MINOR.toString()) +
                " AND r.date1 = " + getStringFormat(date1);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String findLesson(String studentCode, String date1) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "information", "request") +
                " studentCode = " + getStringFormat(studentCode) +
                " AND type = " + getStringFormat(Type.TAKE_LESSON.toString()) +
                " AND date1 = " + getStringFormat(date1);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Message> getMohseniMessage(String username, String date) {
        List<Message> messages = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "message, mediaMessage", "mohsenimessages") + " receiver = " + getStringFormat(username) +
                " AND date = " + getStringFormat(date);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String message = resultSet.getString("message");
                String mediaMessage = resultSet.getString("mediaMessage");
                if (message != null) messages.add(new Message("mohseni", message, false));
                if (mediaMessage != null) messages.add(new Message("mohseni", mediaMessage, false));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<Message> getAdminMessage(String username, String date) {
        List<Message> messages = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "message, answers", "adminmessages") + " user = " + getStringFormat(username) +
                " AND adminDate = " + getStringFormat(date);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String message = resultSet.getString("message");
                String answers = resultSet.getString("answers");
                if (message != null) messages.add(new Message(username, message, true));
                if (answers != null && !answers.equals("[]")) {
                    String answersList = answers.substring(1, answers.length() - 1);
                    List<String> finalAnswers =  new ArrayList<>(Arrays.asList(answersList.split(", ")));
                    for (String answer : finalAnswers) {
                        messages.add(new Message("1", answer, false));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
