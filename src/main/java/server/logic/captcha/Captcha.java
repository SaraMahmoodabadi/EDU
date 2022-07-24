package server.logic.captcha;

public class Captcha {
    private String captchaImageAddress;
    private String captchaValue;
    private int captchaID;

    public Captcha(String captchaImageAddress, String captchaValue) {
        this.captchaImageAddress = captchaImageAddress;
        this.captchaValue = captchaValue;
    }

    public String getCaptchaImageAddress() {
        return captchaImageAddress;
    }

    public String getCaptchaValue() {
        return this.captchaValue;
    }
}
