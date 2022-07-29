package server.logic.managers.edu.eduServices;

import server.database.dataHandlers.eduServises.ProfessorRequestDataHandler;
import server.database.dataHandlers.eduServises.StudentRequestsDataHandler;
import server.network.ClientHandler;
import shared.model.message.request.Type;
import shared.model.user.student.Grade;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.util.List;
import java.util.Random;

public class RequestManager {
    private final StudentRequestsDataHandler sDataHandler;
    private final ProfessorRequestDataHandler pDataHandler;
    private final ClientHandler client;

    public RequestManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.sDataHandler = new StudentRequestsDataHandler(clientHandler.getDataHandler());
        this.pDataHandler = new ProfessorRequestDataHandler(clientHandler.getDataHandler());
    }

    public Response getGrade() {
        Grade grade = this.sDataHandler.getStudentGrade(this.client.getUserName());
        if (grade != null) {
            Response response = new Response(ResponseStatus.OK);
            response.addData("grade", grade);
            return response;
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "errorMessage");
        return getErrorResponse(errorMessage);
    }

    public Response createRequest(Request request) {
        switch ((Type) request.getData("type")) {
            case CERTIFICATE:
                return sendCertificate();
            case DORMITORY:
                return dormitoryRequestAnswer();
            case THESIS_DEFENCE:
                return sendThesisDefenseDate();
            case MINOR:
                return registerMinor(request);
            case RECOMMENDATION:
                return registerRecommendation(request);
            case WITHDRAWAL:
                return registerWithdrawal(request);
            default:
        }
        return null;
    }

    private Response sendCertificate() {
        String result = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "certificate");
        List<String> info = this.sDataHandler.getCertificateInfo(this.client.getUserName());
        if (info != null) {
            result = String.format(result, info.get(0) + " " + info.get(1), info.get(2), info.get(3));
            Response response = new Response(ResponseStatus.OK);
            response.addData("result", result);
            return response;
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        return getErrorResponse(errorMessage);
    }

    private Response dormitoryRequestAnswer() {
        Random random = new Random();
        boolean resultBoolean = random.nextBoolean();
        String result;
        if(resultBoolean) {
            result = "Accepted";
        }
        else {
            result = "Failed";
        }
        Response response = new Response(ResponseStatus.OK);
        response.addData("result", result);
        return response;
    }

    private Response sendThesisDefenseDate() {
        Random random = new Random();
        int day = (Math.abs(random.nextInt())) % 30 + 1;
        int month = (Math.abs(random.nextInt())) % 12 + 1;
        int year;
        if(month > 8) {
            year = 2022;
        }
        else {
            year = 2023;
        }
        String result = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "defenceTime") +
                " " + year + "-" + month + "-" + day;
        Response response = new Response(ResponseStatus.OK);
        response.addData("result", result);
        return response;
    }

    private Response registerMinor(Request request) {
        double rate = this.sDataHandler.getRate(this.client.getUserName());
        if (rate < 17) {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "lowRate");
            return getErrorResponse(errorMessage);
        }
        String items = "studentCode, professorCode, type, anotherCollegeProfessorCode";
        boolean minorResult = this.sDataHandler.registerMinor(items, (String) request.getData("collegeCode"),
                (String) request.getData("major"), this.client.getUserName());
        if (minorResult) {
            String result = "registered";
            Response response = new Response(ResponseStatus.OK);
            response.addData("result", result);
            return response;
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        return getErrorResponse(errorMessage);
    }

    private Response registerRecommendation(Request request) {
        String items = "studentCode, professorCode, type";
        boolean recommendationResult = this.sDataHandler.registerRecommendation(items,
                (String) request.getData("professorCode"), this.client.getUserName());
        if (recommendationResult) {
            String result = "registered";
            Response response = new Response(ResponseStatus.OK);
            response.addData("result", result);
            return response;
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        return getErrorResponse(errorMessage);
    }

    private Response registerWithdrawal(Request request) {
        String items = "studentCode, professorCode, type";
        boolean withdrawalResult = this.sDataHandler.registerWithdrawal(items,
                (String) request.getData("collegeCode"), this.client.getUserName());
        if (withdrawalResult) {
            String result = "registered";
            Response response = new Response(ResponseStatus.OK);
            response.addData("result", result);
            return response;
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        return getErrorResponse(errorMessage);
    }

    public Response getAnswerRecommendation(Request request) {
        if (request.getRequestType() == RequestType.REGISTER_RECOMMENDATION) {
            boolean result = this.pDataHandler.registerRecommendationAnswer
                    ((String) request.getData("studentCode"),
                    (String) request.getData("firstBlank"),
                    (String) request.getData("secondBlank"),
                    (String) request.getData("thirdBlank"), this.client.getUserName());
            if (result) {
                Response response = new Response(ResponseStatus.OK);
                String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
                response.setNotificationMessage(note);
                return response;
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "invalidInputs");
        return getErrorResponse(errorMessage);
    }

    public Response getAnswerRequest(Request request) {
        if (request.getRequestType() == RequestType.REGISTER_REQUEST_ANSWER) {
            boolean isMinor = request.getData("type") == Type.MINOR;
            Type type = (Type) request.getData("type");
            boolean result = this.pDataHandler.registerRequestAnswer
                    ((String) request.getData("studentCode"),
                            this.client.getUserName(),
                            (Boolean) request.getData("result"),
                            isMinor, String.valueOf(type));
            if (result) {
                Response response = new Response(ResponseStatus.OK);
                String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
                response.setNotificationMessage(note);
                return response;
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "invalidInputs");
        return getErrorResponse(errorMessage);
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

}
