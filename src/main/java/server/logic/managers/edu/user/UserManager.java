package server.logic.managers.edu.user;

import shared.response.Response;

public class UserManager {

    public Response sendCaptchaImage() {
        int captchaID = 0;
        String query = "SELECT * FROM captcha WHERE captchaID = " + captchaID;
        return null;
    }

    //TODO
    public boolean changePassword() {
        return false;
    }
}
