package server.logic.managers.edu.user;

import server.database.MySQLHandler;
import server.database.dataHandlers.edu.MainDataHandler;
import server.database.dataHandlers.edu.UserHandler;
import server.database.dataHandlers.edu.unitSelection.UnitSelectionDataHandler;
import server.logic.captcha.Captcha;
import server.network.ClientHandler;
import shared.model.user.User;
import shared.model.user.UserType;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.ImageHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserManager {
    private final UserHandler userHandler;
    private Captcha captcha;
    private final ClientHandler clientHandler;
    private final MainDataHandler mainDataHandler;

    public UserManager(ClientHandler clientHandler) {
        this.userHandler = new UserHandler(clientHandler.getDataHandler(), clientHandler.getCaptchaHandler());
        this.clientHandler = clientHandler;
        this.mainDataHandler = new MainDataHandler(clientHandler.getDataHandler());
    }

    public Response sendCaptchaImage() {
        this.captcha = this.userHandler.sendCaptcha();
        if (this.captcha != null) {
            String image = new ImageHandler().encode(captcha.getCaptchaImageAddress());
            Response response = new Response(ResponseStatus.OK);
            response.addData("captchaImage", image);
            response.addData("captchaValue", captcha.getCaptchaValue());
            return response;
        }
        else {
            Response response = new Response(ResponseStatus.ERROR);
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "errorMessage");
            response.setErrorMessage(errorMessage);
            return response;
        }
    }

    public Response login(Request request) {
        String username = String.valueOf(request.getData("username"));
        String password = String.valueOf(request.getData("password"));
        String captchaValue = String.valueOf(request.getData("captcha"));
        String captchaValue2 = String.valueOf(request.getData("captchaValue"));
        String errorMessage;
        if (isMatched(captchaValue, captchaValue2)) {
            User user = this.userHandler.getInformation(username);
            if (user != null && user.getPassword().equals(password)) {
                return getOKResponse(user);
            }
            else {
                errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "loginError");
            }
        }
        else {
            errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "captchaError");
        }
        return getErrorResponse(errorMessage);
    }

    private boolean isMatched(String captchaValue, String captchaValue2) {
        return captchaValue2.equals(captchaValue);
    }

    private Response getOKResponse(User user) {
        Response response = new Response(ResponseStatus.OK);
        response.addData("userType", user.getUserType());
        response.addData("collegeCode", user.getCollegeCode());
        this.clientHandler.setUserName(user.getUsername());
        this.clientHandler.setUserType(user.getUserType());
        this.userHandler.updateLastLogin(user.getThisLogin(), user.getUsername());
        user.setLastLogin(user.getThisLogin());
        if (user.getUserType() == UserType.STUDENT) {
            response.addData("eduStatus", this.userHandler.getStatus(user.getUsername()));
        }
        else if (user.getUserType() == UserType.PROFESSOR) {
            response.addData("professorType", this.userHandler.getProfessorType(user.getUsername()));
        }
        return checkLastLogin(response, user.getLastLogin());
    }

    private Response checkLastLogin(Response response, String lastLogin) {
        String thisLogin = LocalDateTime.now().toString();
        this.userHandler.updateThisLogin(thisLogin, this.clientHandler.getUserName());
        if (lastLogin.equals("0"))
            return response;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            String t1 = lastLogin.replace('T',' ');
            String t2 = thisLogin.replace('T',' ');
            Date time1 = format.parse(t1);
            Date time2 = format.parse(t2);
            long diff = time2.getTime() - time1.getTime();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            if (seconds >= 10800) {
                String message = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "changePassword");
                response.setNotificationMessage(message);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return response;
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

    public Response changePassword(Request request) {
        String previousPassword = (String) request.getData("previousPassword");
        String newPassword = (String) request.getData("newPassword");
        User user = this.userHandler.getInformation(this.clientHandler.getUserName());
        String password = user.getPassword();
        if (!previousPassword.equals(password)) {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "wrongPassword");
            return getErrorResponse(errorMessage);
        }
        else if (previousPassword.equals(newPassword)){
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "duplicatePassword");;
            return getErrorResponse(errorMessage);
        }
        else {
            this.userHandler.updatePassword(newPassword, this.clientHandler.getUserName());
            return new Response(ResponseStatus.OK);
        }
    }

    public Response getMainPageData() {
        User user = this.mainDataHandler.getMainPageData(this.clientHandler.getUserName());
        if (user != null) {
            Response response = new Response(ResponseStatus.OK);
            response.addData("name", user.getFullName());
            response.addData("emailAddress", user.getEmailAddress());
            response.addData("lastLogin", user.getLastLogin());
            response.addData("profileImage", new ImageHandler().encode(user.getImageAddress()));
            if (this.clientHandler.getUserType() == UserType.STUDENT) return setTableData(response);
            else return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "errorMessage");
            return getErrorResponse(errorMessage);
        }
    }

    private Response setTableData(Response response) {
        response.addData("isUnitSelectionTime", isUnitSelectionTime());
        List<String> table = this.mainDataHandler.getTableData(this.clientHandler.getUserName());
        if (table != null) {
            List<String> middleList = new ArrayList<>();
            List<String> rightList = new ArrayList<>();
            if (table.get(0) != null) middleList.add(table.get(0));
            else middleList.add(" ");
            rightList.add(" ");
            if (table.get(1) != null && table.get(2) != null) {
                middleList.add("specified");
                rightList.add(table.get(1) + " " + table.get(2));
            }
            else {
                middleList.add("not specified");
                rightList.add(" ");
            }
            if (Boolean.parseBoolean(table.get(3))) {
                middleList.add("issued");
                rightList.add("your registration permit has been issued");
            }
            else {
                middleList.add("not issued");
                rightList.add(" ");
            }
            if (table.get(4) != null) {
                middleList.add("specified");
                rightList.add(table.get(4));
            }
            else {
                middleList.add("not specified");
                rightList.add(" ");
            }
            response.addData("middleList", middleList.toString());
            response.addData("rightList", rightList.toString());
        }
        return response;
    }

    public boolean isUnitSelectionTime() {
        if (isPassed()) return false;
        String time = this.mainDataHandler.getUnitSelectionTime(this.clientHandler.getUserName());
        if (time == null) return false;
        int y = Integer.parseInt(time.split("-")[0]);
        int m = Integer.parseInt(time.split("-")[1]);
        int d = Integer.parseInt(time.split("-")[2]);
        int hStart = Integer.parseInt(time.split("-")[3].split(":")[0]);
        int mmStart = Integer.parseInt(time.split("-")[3].split(":")[1]);
        int hEnd = Integer.parseInt(time.split("-")[4].split(":")[0]);
        int mmEnd = Integer.parseInt(time.split("-")[4].split(":")[1]);
        Calendar calendar = Calendar.getInstance();
        int y2 = calendar.get(Calendar.YEAR);
        int m2 = calendar.get(Calendar.MONTH) + 1;
        int d2 = calendar.get(Calendar.DAY_OF_MONTH);
        int h2 = calendar.get(Calendar.HOUR_OF_DAY);
        int mm2 = calendar.get(Calendar.MINUTE);
        if (y == y2 && m == m2 && d == d2) {
            int t1 = hStart * 60 + mmStart;
            int t2 = hEnd * 60 + mmEnd;
            int t3 = h2 * 60 + mm2;
            return t1 <= t3 && t2 >= t3;
        }
        return false;
    }

    private boolean isPassed() {
        String time = new UnitSelectionDataHandler(new MySQLHandler())
                .getCollegeRegistrationTime(this.userHandler.getCollegeCode(this.clientHandler.getUserName()));
        try {
            int y = Integer.parseInt(time.split("-")[0]);
            int m = Integer.parseInt(time.split("-")[1]);
            int d = Integer.parseInt(time.split("-")[2]);
            int h = Integer.parseInt(time.split("-")[3].split(":")[0]);
            int mm = Integer.parseInt(time.split("-")[3].split(":")[1]);
            Calendar calendar = Calendar.getInstance();
            int y2 = calendar.get(Calendar.YEAR);
            int m2 = calendar.get(Calendar.MONTH) + 1;
            int d2 = calendar.get(Calendar.DAY_OF_MONTH);
            int h2 = calendar.get(Calendar.HOUR_OF_DAY);
            int mm2 = calendar.get(Calendar.MINUTE);
            if (y > y2) return false;
            if (y == y2 && m > m2) return false;
            if (y == y2 && m == m2 && d > d2) return false;
            return y != y2 || m != m2 || d != d2 || h <= h2;
        } catch (Exception e) {
            return false;
        }
    }
}
