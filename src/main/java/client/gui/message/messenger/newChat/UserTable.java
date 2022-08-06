package client.gui.message.messenger.newChat;

import javafx.scene.control.CheckBox;
import shared.model.user.UserType;

public class UserTable {
    private String firstname;
    private String lastname;
    private String username;
    private UserType type;
    private CheckBox checkBox;

    public UserTable(String firstname, String lastname, String username, UserType type) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.type = type;
        checkBox = new CheckBox();
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public void select() {
        checkBox.setSelected(true);
    }
}
