package server.logic.captcha;

import javafx.scene.image.Image;

import java.util.Random;

public class CaptchaHandler {
    private int captchaID;
    private Captcha captcha;

    public void generateRandomCaptchaID() {
        Random random = new Random();
        int captchaID;
        do {
            captchaID = Math.abs(random.nextInt()) % 5;
        } while (captchaID == this.captchaID);
        this.captchaID = captchaID;
    }

    //TODO
    public Image getCaptcha() {
        return null;
    }

    public Image recaptcha() {
        return sendCaptcha();
    }

    public Image sendCaptcha() {
        generateRandomCaptchaID();
        return getCaptcha();
    }

    public boolean isMatched(String captchaValue){
        return this.captcha.getCaptchaValue().equals(captchaValue);
    }
}
