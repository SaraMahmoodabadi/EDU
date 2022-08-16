package client.gui;

import client.network.ServerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import javafx.stage.Stage;
import shared.model.user.UserType;
import shared.model.user.professor.Type;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EDU extends Stage {
    public static ServerController serverController;
    public static SceneSwitcher sceneSwitcher;
    public static UserType userType;
    public static Type professorType;
    public static String collegeCode;
    public static String username;
    public static boolean isOnline;

    public EDU(ServerController controller) {
        sceneSwitcher = new SceneSwitcher();
        serverController = controller;
        try {
            String path = Config.getConfig(ConfigType.FXML_FILE).getProperty(String.class, "loginPage");
            Path url = Paths.get(path);
            FXMLLoader loader = new FXMLLoader(url.toUri().toURL());
            Parent root = loader.load();
            Scene scene = new Scene(root);
            this.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setResizable(false);
        String title = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "sharifStageText");
        this.setTitle(title);
        String imagePath = Config.getConfig(ConfigType.CLIENT_IMAGE).getProperty(String.class, "sharifLogo");
        Image icon = new Image("file:" + imagePath);
        this.setOnCloseRequest(event -> System.exit(0));
        this.getIcons().add(icon);
        this.show();
    }
}
