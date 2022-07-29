package client;

import javafx.application.Application;
import javafx.stage.Stage;
import shared.util.config.Config;
import shared.util.config.ConfigType;

public class ClientMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        int port = Integer.parseInt(Config.getConfig(ConfigType.NETWORK).getProperty("clientPort"));;
        Client client = new Client(port);
        client.start();
    }
}
