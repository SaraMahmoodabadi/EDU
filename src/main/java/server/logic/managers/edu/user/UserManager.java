package server.logic.managers.edu.user;

import server.database.dataHandlers.MainDataHandler;
import server.database.dataHandlers.UserHandler;
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
            return response;
        }
        else {
            Response response = new Response(ResponseStatus.ERROR);
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("errorMessage");
            response.setErrorMessage(errorMessage);
            return response;
        }
    }

    public Response login(Request request) {
        String username = String.valueOf(request.getData("username"));
        String password = String.valueOf(request.getData("password"));
        String captchaValue = String.valueOf(request.getData("captcha"));
        String errorMessage;
        if (isMatched(captchaValue)) {
            User user = this.userHandler.getInformation(username);
            if (user != null && user.getPassword().equals(password)) {
                return getOKResponse(user);
            }
            else {
                errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("loginError");
            }
        }
        else {
            errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("captchaError");
        }
        return getErrorResponse(errorMessage);
    }

    private boolean isMatched(String captchaValue) {
        return this.captcha.getCaptchaValue().equals(captchaValue);
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
                String message = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("changePassword");
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
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("wrongPassword");
            return getErrorResponse(errorMessage);
        }
        else if (previousPassword.equals(newPassword)){
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("duplicatePassword");;
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
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("errorMessage");
            return getErrorResponse(errorMessage);
        }
    }

    private Response setTableData(Response response) {
        List<String> table = this.mainDataHandler.getTableData(this.clientHandler.getUserName());
        if (table != null) {
            List<String> middleList = new ArrayList<>();
            List<String> leftList = new ArrayList<>();
            if (table.get(0) != null) middleList.add(table.get(0));
            else middleList.add("");
            leftList.add("");
            if (table.get(1) != null && table.get(2) != null) {
                middleList.add("specified");
                leftList.add(table.get(1) + " " + table.get(2));
            }
            else {
                middleList.add("not specified");
                leftList.add("");
            }
            if (Boolean.parseBoolean(table.get(3))) {
                middleList.add("issued");
                leftList.add("your registration permit has been issued");
            }
            else {
                middleList.add("not issued");
                leftList.add("");
            }
            if (table.get(4) != null) {
                middleList.add("specified");
                leftList.add(table.get(4));
            }
            else {
                middleList.add("not specified");
                leftList.add("");
            }
            response.addData("middleList", middleList);
            response.addData("leftList", leftList);
        }
        return response;
    }
}
