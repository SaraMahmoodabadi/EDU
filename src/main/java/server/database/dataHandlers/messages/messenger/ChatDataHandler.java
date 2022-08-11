package server.database.dataHandlers.messages.messenger;

import server.database.MySQLHandler;
import shared.model.message.chatMessages.Message;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatDataHandler {
    private final MySQLHandler databaseHandler;

    public ChatDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public List<Message> getAllChats(String username) {
        List<Message> messages = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "user1, user2, sender, lastMessage, date", "chat") + " user1 = " +
                getStringFormat(username) + " OR user2 = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            while (resultSet.next()) {
                String user1 = resultSet.getString("user1");
                String user2 = resultSet.getString("user2");
                String sender = resultSet.getString("sender");
                String lastMessage = resultSet.getString("lastMessage");
                String date = resultSet.getString("date");
                String name = user1.equals(username) ? getName(user2) : getName(user1);
                String finalMessage = sender.equals(username) ? "You: " + lastMessage : name + ": " + lastMessage;
                String user = user1.equals(username) ? user2 : user1;
                Message message = new Message(name, user, finalMessage, date, "chatMessage");
                messages.add(message);
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
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                return firstname + " " + lastname;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Message> getChat(String username, String otherUser) {
        List<Message> messages = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        String query1 = String.format(query, "sender, receiver, sendMessageTime, messageText, isMedia", "messenger") +
                " sender = " + getStringFormat(username) + " AND receiver = " + getStringFormat(otherUser);
        String query2 = String.format(query, "sender, receiver, sendMessageTime, messageText, isMedia", "messenger") +
                " sender = " + getStringFormat(otherUser) + " AND receiver = " + getStringFormat(username);
        messages.addAll(getMessages(query1, username, otherUser));
        messages.addAll(getMessages(query2, username, otherUser));
        return messages;
    }

    private List<Message> getMessages(String query, String username, String otherUser) {
        List<Message> messages = new ArrayList<>();
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            while (resultSet.next()) {
                String sender = resultSet.getString("sender");
                String date = resultSet.getString("sendMessageTime");
                String messageText = resultSet.getString("messageText");
                boolean isMedia = Boolean.parseBoolean(resultSet.getString("isMedia"));
                boolean isSender = sender.equals(username);
                Message message = new Message(otherUser, messageText,date, isSender, isMedia);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public String getImage(String username) {
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

    public boolean sendMessage(String sender, String receiver, String message, boolean isMedia) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "messenger", "sender, receiver, messageText, sendMessageTime, isMedia",
                getStringFormat(sender) + ", " + getStringFormat(receiver) + ", " + getStringFormat(message) + ", " +
                        getStringFormat(LocalDateTime.now().toString()) + ", " + getStringFormat(String.valueOf(isMedia)));
        return this.databaseHandler.updateData(query);
    }

    public boolean updateChat(String receiver, String sender, String message) {
        String query0 = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "removeData");
        String query = String.format(query0, "chat") + " user1 = " + getStringFormat(receiver) + " AND user2 = " +
                getStringFormat(sender);
        this.databaseHandler.removeData(query);
        query = String.format(query0, "chat") + " user1 = " + getStringFormat(sender) + " AND user2 = " +
                getStringFormat(receiver);
        this.databaseHandler.removeData(query);
        query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "chat", "user1, user2, sender, lastMessage, date",
                getStringFormat(receiver) + ", " + getStringFormat(sender) + ", " +
                        getStringFormat(sender) + ", " +
                        getStringFormat(message) + ", " +
                        getStringFormat(LocalDateTime.now().toString()));
        return this.databaseHandler.updateData(query);
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
