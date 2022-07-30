package server.logic.managers.edu.unitSelection;

import server.database.dataHandlers.unitSelection.UnitSelectionDataHandler;
import server.network.ClientHandler;
import shared.model.user.student.Grade;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

public class UnitSelectionTimeManager {
    private final UnitSelectionDataHandler dataHandler;

    public UnitSelectionTimeManager(ClientHandler client) {
        this.dataHandler = new UnitSelectionDataHandler(client.getDataHandler());
    }

    public Response setTime(Request request) {
        String filter = (String) request.getData("filter");
        boolean result;
        if (filter.equals("grade")) result = setTimeWithGrade(request);
        else result = setTimeWithYear(request);
        if (result) return sendOKResponse();
        else return sendErrorResponse();
    }

    private boolean setTimeWithGrade(Request request) {
        Grade grade = (Grade) request.getData("value");
        String time  = (String) request.getData("date");
        String collegeCode = (String) request.getData("collegeCode");
        String items = " grade = '" + grade + "'";
        return this.dataHandler.updateUnitSelectionTime(items, collegeCode, time);
    }

    private boolean setTimeWithYear(Request request) {
        int year = (int) request.getData("value");
        String time  = (String) request.getData("date");
        String collegeCode = (String) request.getData("collegeCode");
        String items  = " enteringYear = " + year;
        return this.dataHandler.updateUnitSelectionTime(items, collegeCode, time);
    }

    private Response sendOKResponse() {
        Response response = new Response(ResponseStatus.OK);
        String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
        response.setNotificationMessage(note);
        return response;
    }

    private Response sendErrorResponse() {
        Response response = new Response(ResponseStatus.ERROR);
        String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        response.setErrorMessage(error);
        return response;
    }
}
