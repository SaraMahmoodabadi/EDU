package server.database.dataHandlers.messages.admin;

import server.database.MySQLHandler;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.time.LocalDateTime;

public class AdminDataHandler {
    private final MySQLHandler databaseHandler;

    public AdminDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public boolean sendMessageToAdmin(String message, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "adminmessages", "sender, receiver, date, message",
                getStringFormat(username) + ", '1', " + getStringFormat(LocalDateTime.now().toString()) + ", " +
                getStringFormat(message));
        return this.databaseHandler.updateData(query);
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
