package server.logic.managers.messages.mohseni;

import server.database.dataHandlers.messages.mohseni.MohseniDataHandler;
import server.network.ClientHandler;
import shared.model.user.student.Student;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.util.List;

public class MohseniManager {
    private final ClientHandler client;
    private final MohseniDataHandler dataHandler;

    public MohseniManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new MohseniDataHandler(clientHandler.getDataHandler());
    }

    public Response getAllStudentsInfo() {
        List<Student> students = this.dataHandler.getStudentsInfo(null);
        if (students != null) {
            Response response = new Response(ResponseStatus.OK);
            for (int i = 0; i < students.size(); i++) {
                response.addData("student" + i, students.get(i));
            }
            return response;
        }
        String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        return sendErrorResponse(error);
    }

    public Response getStudentsInfo(Request request) {
        String studentCode = (String) request.getData("studentCode");
        List<Student> students = this.dataHandler.getStudentsInfo(studentCode);
        if (students != null) {
            Response response = new Response(ResponseStatus.OK);
            for (int i = 0; i < students.size(); i++) {
                response.addData("student" + i, students.get(i));
            }
            return response;
        }
        String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        return sendErrorResponse(error);
    }

    public Response getStudentData(Request request) {
        String studentCode = (String) request.getData("studentCode");
        Student student = this.dataHandler.getStudentInfo(studentCode);
        if (student == null) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
        else {
            Response response = new Response(ResponseStatus.OK);
            response.addData("student", student);
            return response;
        }
    }

    public Response sendMessage(Request request) {
        String file = (String) request.getData("file");
        String fileFormat = (String) request.getData("fileFormat");
        String message = (String) request.getData("message");
        String grade = (String) request.getData("grade");
        String year = (String) request.getData("year");
        String college = (String) request.getData("college");
        String filePath = null;
        if (file != null) filePath = saveFile(file, fileFormat);
        String item = "";
        int i = 0;
        if (!college.equals("All colleges")) {
            String collegeCode = this.dataHandler.getCollegeCode(college);
            item += " u.collegeCode = " + getStringFormat(collegeCode);
            i++;
        }
        if (!grade.equals("All grades")) {
            if (i > 0) item += " AND";
            item += " s.grade = " + getStringFormat(grade);
            i++;
        }
        if (!year.equals("All")) {
            if (i > 0) item += " AND";
            item += " s.enteringYear = " + getStringFormat(year);
        }
        List<String> students = this.dataHandler.getStudents(item);
        boolean result = true;
        i = 0;
        if (students == null) result = false;
        else {
            for (String student : students) {
                if (filePath != null && message != null)  {
                    boolean messageResult = this.dataHandler.sendMixMessage(message, filePath, student);
                    if (messageResult) i++;
                }
                else if (message != null) {
                    boolean messageResult = this.dataHandler.sendTextMessage(message, student);
                    if (messageResult) i++;
                }
                else if (filePath != null) {
                    boolean messageResult = this.dataHandler.sendMediaMessage(filePath, student);
                    if (messageResult) i++;
                }
            }
        }
        if (result && i == students.size()) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
    }

    private String saveFile(String file, String fileFormat) {
        String path = Config.getConfig(ConfigType.SERVER_PATH).getProperty(String.class, "mohseniFiles");
        MediaHandler handler = new MediaHandler();
        path = path + "/" + handler.createNameByUser(this.client.getUserName()) + "." + fileFormat;
        handler.writeBytesToFile(path, handler.decode(file));
        return path;
    }

    private Response sendErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
