package server.logic.captcha;

import java.util.Random;

public class CaptchaHandler {
    private int captchaID;

    public int generateRandomCaptchaID() {
        Random random = new Random();
        int captchaID;
        do {
            captchaID = Math.abs(random.nextInt()) % 5;
        } while (captchaID == this.captchaID);
        this.captchaID = captchaID;
        return captchaID;
    }
}
