package server.logic.captcha;

import javafx.scene.image.Image;

public class Captcha {
    private Image captchaImage;
    private String captchaValue;
    private int captchaID;

    public Captcha(Image captchaImage, String captchaValue) {
        this.captchaImage = captchaImage;
        this.captchaValue = captchaValue;
    }

    public String getCaptchaValue() {
        return this.captchaValue;
    }
}
