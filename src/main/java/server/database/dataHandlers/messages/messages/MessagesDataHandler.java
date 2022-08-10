package server.database.dataHandlers.messages.messages;

import server.database.MySQLHandler;
import shared.model.message.chatMessages.Message;
import shared.model.message.request.Type;
import shared.model.user.student.EducationalStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
        String professorCode = findUserCode("professor", username);
        List<Message> requests = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.firstName, u.lastName, u.username, r.date1, r.type", "request r", "student s",
                "s.studentCode = r.studentCode JOIN user u ON u.username = s.username") +
                " r.professorCode = " + getStringFormat(professorCode);
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

    public List<Message> getAllRequestsResult(String username) {
        String studentCode = findUserCode("student", username);
        List<Message> requests = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.firstName, u.lastName, u.username, r.date2, r.type, r.result, r.secondResult",
                "request r", "professor p",
                "p.professorCode = r.professorCode JOIN user u ON u.username = p.username") +
                " r.studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            while (resultSet.next()) {
                String result1 = resultSet.getString("result");
                String result2 = resultSet.getString("secondResult");
                if (result1 == null && result2 == null) continue;
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String name = firstname + " " + lastname;
                String user = resultSet.getString("username");
                String date = resultSet.getString("date2");
                String type = resultSet.getString("type");
                String userMessage = "request result : " + type;
                Message message = new Message(name, user, userMessage, date, "request result");
                requests.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public List<Message> getAllRecommendation(String username) {
        String studentCode = findUserCode("student", username);
        List<Message> requests = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.firstName, u.lastName, u.username, r.date2, r.firstBlank",
                "request r", "professor p",
                "p.professorCode = r.professorCode JOIN user u ON u.username = p.username") +
                " r.studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            while (resultSet.next()) {
                String firstBlank = resultSet.getString("firstBlank");
                if (firstBlank == null) continue;
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String name = firstname + " " + lastname;
                String user = resultSet.getString("username");
                String date = resultSet.getString("date2");
                String userMessage = "request result : " + Type.RECOMMENDATION;
                Message message = new Message(name, user, userMessage, date, "request result");
                requests.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public List<Message> getAllSendMessageResults(String username) {
        List<Message> requests = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.firstName, u.lastName, u.username, r.date2, r.result",
                "request r", "user u", "u.username = r.professorCode") +
                " r.studentCode = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            while (resultSet.next()) {
                String result1 = resultSet.getString("result");
                if (result1 == null) continue;
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String name = firstname + " " + lastname;
                String user = resultSet.getString("username");
                String date = resultSet.getString("date2");
                String userMessage = "request result : " + Type.SEND_MESSAGE;
                Message message = new Message(name, user, userMessage, date, "request result");
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

    public List<String> getRequestResult(String user, String time, String username, Type type) {
        List<String> results = new ArrayList<>();
        String professorCode = user;
        String studentCode = username;
        if (type != Type.SEND_MESSAGE) {
            professorCode = findUserCode("professor", user);
            studentCode = findUserCode("student", username);
        }
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "result, secondResult, firstBlank, secondBlank, thirdBlank", "request") +
                " studentCode = " + getStringFormat(studentCode) +
                " AND type = " + getStringFormat(type.toString()) +
                " AND professorCode = " + getStringFormat(professorCode) +
                " AND date2 = " + getStringFormat(time);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String firstBlank = resultSet.getString("firstBlank");
                String secondBlank = resultSet.getString("secondBlank");
                String thirdBlank = resultSet.getString("thirdBlank");
                String result1 = resultSet.getString("result");
                String result2 = resultSet.getString("secondResult");
                if (type == Type.RECOMMENDATION) {
                    results.add(firstBlank);
                    results.add(secondBlank);
                    results.add(thirdBlank);
                }
                else {
                    results.add(result1);
                    if (type == Type.MINOR) results.add(result2);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public String findMinorMajor(String studentCode, String date1) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "c.name", "request r", "professor p", "p.professorCode = r.anotherCollegeProfessorCode" +
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
                return resultSet.getString("information");
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
                if (message != null) messages.add(new Message("mohseni", message, false, false));
                if (mediaMessage != null) messages.add(new Message
                        ("mohseni", new MediaHandler().encode(mediaMessage), false, true));
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
                        messages.add(new Message("1", answer, false, false));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public String getEduAssistant(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "c.educationalAssistantCode", "college c", "user u" ,
                "u.collegeCode = c.collegeCode") + " u.username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("educationalAssistantCode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setRequestResult(String username, String otherUser, String date, boolean result) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "request", "result = " + getStringFormat(String.valueOf(result)) +
                ", date2 = " + getStringFormat(LocalDateTime.now().toString())) +
                " studentCode = " + getStringFormat(otherUser) +
                " AND type = " + getStringFormat(Type.SEND_MESSAGE.toString()) +
                " AND professorCode = " + getStringFormat(username) +
                " AND date1 = " + getStringFormat(date);
        return this.databaseHandler.updateData(query);
    }

    public boolean setRequestResult(String professorCode, String studentCode,
                                    String date, Type type, boolean result, boolean isFirstResult) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        String request = String.format(query, "request", "result = " + getStringFormat(String.valueOf(result)) +
                ", date2 = " + getStringFormat(LocalDateTime.now().toString())) +
                " studentCode = " + getStringFormat(studentCode) +
                " AND type = " + getStringFormat(type.toString()) +
                " AND professorCode = " + getStringFormat(professorCode) +
                " AND date1 = " + getStringFormat(date);
        if (isFirstResult) {
           query = request;
        }
        else {
            query = String.format(query, "request", "secondResult = " + getStringFormat(String.valueOf(result)) +
                    ", date2 = " + getStringFormat(LocalDateTime.now().toString())) +
                    " studentCode = " + getStringFormat(studentCode) +
                    " AND type = " + getStringFormat(type.toString()) +
                    " AND professorCode = " + getStringFormat(professorCode) +
                    " AND date1 = " + getStringFormat(date);
        }
        return this.databaseHandler.updateData(query);
    }

    public void setWithdrawalStatus(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "student", "status = " +
                getStringFormat(EducationalStatus.WITHDRAWAL_FROM_EDUCATION.toString())) +
                " studentCode = " + getStringFormat(studentCode);
        this.databaseHandler.updateData(query);
    }

    public void createChat(String receiver, String sender) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "removeData");
        query = String.format(query, "chat") + " user1 = " + getStringFormat(receiver) + " AND user2 = " +
                getStringFormat(sender);
        this.databaseHandler.updateData(query);
        query = String.format(query, "chat") + " user1 = " + getStringFormat(sender) + " AND user2 = " +
                getStringFormat(receiver);
        this.databaseHandler.updateData(query);
        query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "chat", "user1, user2, sender, lastMessage, date",
                getStringFormat(receiver) + ", " + getStringFormat(sender) + ", " + getStringFormat(sender) +
                        ", chat created, " +
                getStringFormat(LocalDateTime.now().toString()));
        this.databaseHandler.updateData(query);
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
