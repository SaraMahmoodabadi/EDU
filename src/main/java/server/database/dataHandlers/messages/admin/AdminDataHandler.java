package server.database.dataHandlers.messages.admin;

import server.database.MySQLHandler;
import shared.model.message.chatMessages.Message;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class AdminDataHandler {
    private final MySQLHandler databaseHandler;

    public AdminDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public boolean sendMixMessageToAdmin(String message,String media, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "adminmessages", "user, date, message, userMedia",
                getStringFormat(username) + ", " + getStringFormat(LocalDateTime.now().toString()) + ", " +
                getStringFormat(message) + ", " + getStringFormat(media));
        return this.databaseHandler.updateData(query);
    }

    public boolean sendTextMessageToAdmin(String message, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "adminmessages", "user, date, message",
                getStringFormat(username) + ", " + getStringFormat(LocalDateTime.now().toString()) + ", " +
                        getStringFormat(message));
        return this.databaseHandler.updateData(query);
    }

    public boolean sendMediaMessageToAdmin(String media, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "adminmessages", "user, date, userMedia",
                getStringFormat(username) + ", " + getStringFormat(LocalDateTime.now().toString()) + ", " +
                        getStringFormat(media));
        return this.databaseHandler.updateData(query);
    }

    public List<String> getMessageAnswers(String user, String time) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "answers", "adminmessages") + " user = " + getStringFormat(user) +
                " AND date = " + getStringFormat(time);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String answers = resultSet.getString("answers");
                if (answers == null || answers.equals("[]")) return new ArrayList<>();
                String answersList = answers.substring(1, answers.length() - 1);
                return new ArrayList<>(Arrays.asList(answersList.split(", ")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getMediaAnswers(String username, String time) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "adminMedia", "adminmessages") + " user = " + getStringFormat(username) +
                " AND date = " + getStringFormat(time);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String answers = resultSet.getString("adminMedia");
                if (answers == null || answers.equals("[]")) return new ArrayList<>();
                String answersList = answers.substring(1, answers.length() - 1);
                return new ArrayList<>(Arrays.asList(answersList.split(", ")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateTextAnswers(String user, String message, String date, List<String> answers) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "adminmessages", "answers = " + getStringFormat(answers.toString()) +
                ", adminDate = " + getStringFormat(LocalDateTime.now().toString())) +
                " user = " + getStringFormat(user) + " AND date = " + getStringFormat(date) + " AND message = " +
                getStringFormat(message);
        return this.databaseHandler.updateData(query);
    }

    public boolean updateMediaAnswers(String user, String message, String date, List<String> answers) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "adminmessages", "adminMedia = " + getStringFormat(answers.toString()) +
                ", adminDate = " + getStringFormat(LocalDateTime.now().toString())) +
                " user = " + getStringFormat(user) + " AND date = " + getStringFormat(date) + " AND message = " +
                getStringFormat(message);
        return this.databaseHandler.updateData(query);
    }

    public List<Message> getAllMessages() {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "m.user, m.message, m.userMedia, m.date, u.firstName, u.lastName", "adminmessages m",
                "user u", "u.username = m.user");
        query = query.substring(0, query.length() - 6);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        List<Message> messages = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String user = resultSet.getString("user");
                String date = resultSet.getString("date");
                String userMessage = resultSet.getString("message");
                String media = resultSet.getString("userMedia");
                String messageText;
                if (userMessage != null) messageText = userMessage;
                else if (media != null) messageText = "media";
                else messageText = "-";
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String name = firstname + " " + lastname;
                Message message = new Message(name, user, messageText, date, "adminMessage");
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public Map<String, String> getMessage(String username, String time) {
        Map<String, String> messages = new HashMap<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "message, userMedia", "adminmessages") + " user = " + getStringFormat(username) +
                " AND date = " + getStringFormat(time);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String message = resultSet.getString("message");
                String media = resultSet.getString("userMedia");
                messages.put("message", message);
                messages.put("media", media);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public String getName(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "firstName, lastName", "user") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                return firstName + " " + lastName;
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
