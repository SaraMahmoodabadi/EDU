package server.database.dataHandlers.messages.messenger;

import server.database.MySQLHandler;
import shared.request.RequestType;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.time.LocalDateTime;

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

    public boolean sendRequest(String receiver, String sender, RequestType type) {
        return false;
    } /**receiver is the username of someone who receives request and
     sender is the username of someone who sends request
     */

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
