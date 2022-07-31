package server.logic.managers.edu.unitSelection;

import server.database.dataHandlers.unitSelection.UnitSelectionDataHandler;
import server.network.ClientHandler;
import shared.model.user.student.Grade;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UnitSelectionTimeManager {
    private final UnitSelectionDataHandler dataHandler;
    public static Thread thread;

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

    private void checkTime(String time) {
        thread = new Thread(() -> {
            int t = findTime(this.dataHandler.getTimes());
            if (t != -1) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //TODO
                    }
                }, (long) t * 60 * 1000);
            }
        });
        thread.start();
    }

    private int findTime(List<String> times) {
        try {
            int max = 0;
            for (String time : times) {
                int y = Integer.parseInt(time.split("-")[0]);
                int m = Integer.parseInt(time.split("-")[1]);
                int d = Integer.parseInt(time.split("-")[2]);
                int h = Integer.parseInt(time.split("-")[4].split(":")[0]);
                int mm = Integer.parseInt(time.split("-")[4].split(":")[1]);
                int t = (y * 365 + m * 30 + d) * 24 * 60 + h * 60 + mm;
                if (max < t) {
                    max = t;
                    Calendar calendar = Calendar.getInstance();
                    int t2 = (calendar.get(Calendar.YEAR) * 365 +
                            calendar.get(Calendar.MONTH) * 30 +
                            calendar.get(Calendar.DAY_OF_MONTH)) * 24 * 60 +
                            calendar.get(Calendar.HOUR) * 60 +
                            calendar.get(Calendar.MINUTE);
                    if (t2 > t) return -1;
                    max = Math.max(max, t - t2);
                }
            }
           return max;
        } catch (Exception ignored) {}
        return 0;
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
