package server.logic.managers.edu.registration;

import server.database.dataHandlers.registration.RegistrationDataHandler;
import server.network.ClientHandler;
import shared.model.university.lesson.Group;
import shared.model.university.lesson.Lesson;
import shared.model.user.professor.Professor;
import shared.model.user.professor.Type;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.util.Collections;
import java.util.List;

public class RegistrationManager {
    private final RegistrationDataHandler dataHandler;
    private final ClientHandler clientHandler;

    public RegistrationManager(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
       this.dataHandler = new RegistrationDataHandler(clientHandler.getDataHandler());
    }

    public Response getAllLessons() {
        List<Lesson> lessons = dataHandler.getAllLessons();
        if (lessons != null) {
            Response response = new Response(ResponseStatus.OK);
            for (int i = 0; i < lessons.size(); i++) {
                response.addData("lesson" + i, lessons.get(i));
            }
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "errorMessage");
            return getErrorResponse(errorMessage);
        }
    }

    public Response getDesiredLessons(Request request) {
        String collegeName = (String) request.getData("collegeName");
        String unitNumber = (String) request.getData("unitNumber");
        String lessonCode = (String) request.getData("lessonCode");
        String query = " WHERE ";
        int n = 0;
        if (collegeName != null && !collegeName.equals("-")) {
            String collegeCode = this.dataHandler.getCollegeCode(collegeName);
            query += ("collegeCode = " + getStringFormat(collegeCode));
            n++;
        }
        if (unitNumber != null) {
            if (n == 1) query += "AND ";
            query += ("unitNumber = " + unitNumber);
            n++;
        }
        if (lessonCode != null) {
            if (n >= 1) query += "AND ";
            query += ("lessonCode = " + getStringFormat(lessonCode));
        }
        if (n == 0) return getAllLessons();
        else {
           return makeDesiredLessonsResponse(query);
        }
    }

    private Response makeDesiredLessonsResponse(String query) {
        List<Lesson> lessons = this.dataHandler.getDesiredLessons(query);
        if (lessons != null) {
            Response response = new Response(ResponseStatus.OK);
            for (int i = 0; i < lessons.size(); i++) {
                response.addData("lesson" + i, lessons.get(i));
            }
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "errorMessage");
            return getErrorResponse(errorMessage);
        }
    }

    public Response addLesson(Request request) {
        Lesson lesson = (Lesson) request.getData("lesson");
        if (this.dataHandler.existLesson(lesson.getLessonCode())) {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "duplicateLessonCode");
            return getErrorResponse(errorMessage);
        }
        else {
            if (this.dataHandler.makeLesson(lesson)) {
                if (addProfessorLesson(lesson.getProfessorCode(), lesson.getLessonCode())) {
                    String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                            (String.class, "lessonCreated");
                    Response response = new Response(ResponseStatus.OK);
                    response.setNotificationMessage(note);
                    return response;
                }
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        return getErrorResponse(errorMessage);
    }

    public Response editLesson(Request request) {
        String lessonCode = (String) request.getData("lessonCode");
        String collegeCode = (String) request.getData("collegeCode");
        if (this.dataHandler.existLesson(lessonCode) &&
                collegeCode.equals(this.dataHandler.getLessonCollegeCode(lessonCode))) {
            String examTime = (String) request.getData("examTime");
            String days = request.getData("days").toString();
            String classTime = (String) request.getData("classTime");
            Group group = (Group) request.getData("group");
            String query = "";
            int n = 0;
            if (classTime != null) {
                query += ("classTime = " + getStringFormat(classTime));
                n++;
            }
            if (days != null) {
                if (n == 1) query += ", ";
                query += ("days = " + getStringFormat(days));
                n++;
            }
            if (examTime != null) {
                if (n >= 1) query += ", ";
                query += ("examTime = " + getStringFormat(examTime));
            }
            if (group != null) {
                boolean result = this.dataHandler.makeGroup(group) &&
                        addProfessorLesson(group.getProfessorCode(), group.getLessonCode());
                if (!result) {
                    List<String> groups = this.dataHandler.getLessonGroups(group.getLessonCode());
                    groups.add(String.valueOf(group.getGroupNumber()));
                    this.dataHandler.updateLessonGroups(group.getLessonCode(), groups);
                    String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                            (String.class, "invalidInputs");
                    return getErrorResponse(errorMessage);
                }
            }
            if (n != 0) {
                boolean result = this.dataHandler.editLesson(query, lessonCode);
                if (result) {
                    String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "lessonEdited");
                    Response response = new Response(ResponseStatus.OK);
                    response.setNotificationMessage(note);
                    return response;
                }
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "invalidInputs");
        return getErrorResponse(errorMessage);
    }

    public Response removeLesson(Request request) {
        String collegeCode = (String) request.getData("collegeCode");
        String lessonCode = (String) request.getData("lessonCode");
        if (collegeCode.equals(this.dataHandler.getLessonCollegeCode(lessonCode))) {
            boolean result;
            if (request.getRequestType() == RequestType.REMOVE_LESSON_GROUP) {
                result = this.dataHandler.removeGroup(lessonCode, (String) request.getData("group"));
                List<String> professor = Collections.singletonList(this.dataHandler.getProfessorByLesson
                        (lessonCode, (String) request.getData("group")));
                removeProfessorsLesson(professor, lessonCode);
                String group = (String) request.getData("group");
                List<String> groups = this.dataHandler.getLessonGroups(lessonCode);
                groups.remove(group);
                this.dataHandler.updateLessonGroups(lessonCode, groups);
            }
            else {
                result = this.dataHandler.removeLesson(lessonCode);
                List<String> professor = this.dataHandler.getProfessorsByLesson(lessonCode);
                removeProfessorsLesson(professor, lessonCode);
            }
            if (result) {
                String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "lessonRemoved");
                Response response = new Response(ResponseStatus.OK);
                response.setNotificationMessage(note);
                return response;
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "invalidInputs");
        return getErrorResponse(errorMessage);
    }

    public Response getProfessors() {
        List<Professor> professors = this.dataHandler.getAllProfessors();
        if (professors != null) {
            Response response = new Response(ResponseStatus.OK);
            for (int i = 0; i < professors.size(); i++) {
                response.addData("professor" + i, professors.get(i));
            }
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "errorMessage");
            return getErrorResponse(errorMessage);
        }
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

    public Response editProfessor(Request request) {
        String professorCode = (String) request.getData("professorCode");
        String phoneNumber = (String) request.getData("phoneNumber");
        String email = (String) request.getData("email");
        String room = (String) request.getData("room");
        String degree = (String) request.getData("degree");
        String collegeCode = (String) request.getData("collegeCode");
        if (collegeCode.equals(this.dataHandler.getProfessorCollegeCode(professorCode))) {
            String query = "";
            int n = 0;
            if (phoneNumber != null) {
                query += ("phoneNumber = " + getStringFormat(phoneNumber));
                n++;
            }
            if (email != null) {
                if (n == 1) query += ", ";
                query += ("emailAddress = " + getStringFormat(email));
                n++;
            }
            boolean result1 = true;
            if (n != 0) {
                result1 = this.dataHandler.editUser(this.clientHandler.getUserName(), query, professorCode);
            }
            int m = 0;
            if (degree != null) {
                query += ("degree = " + getStringFormat(degree));
                m++;
            }
            if (room != null) {
                if (m == 1) query += ", ";
                query += ("roomNumber = " + getStringFormat(room));
                m++;
            }
            boolean result2 = true;
            if (m != 0) {
                result2 = this.dataHandler.editProfessor(professorCode, query);
            }
            if ((m == 0 && n == 0) || (result1 && result2)) {
                String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                        (String.class, "invalidInputs");
                return getErrorResponse(errorMessage);
            }
            else {
                String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                        (String.class, "professorEdited");
                Response response = new Response(ResponseStatus.OK);
                response.setNotificationMessage(note);
                return response;
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                (String.class, "invalidInputs");
        return getErrorResponse(errorMessage);
    }

    public Response deposal(Request request) {
        String professorCode = (String) request.getData("professorCode");
        String collegeCode = (String) request.getData("collegeCode");
        String query = "educationalAssistant = NULL";
        boolean result = this.dataHandler.deposal(professorCode, query, collegeCode);
        if (result) {
            String items = " type = " + getStringFormat(Type.PROFESSOR.toString());
            this.dataHandler.editProfessor(professorCode, items);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            Response response = new Response(ResponseStatus.OK);
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "invalidInputs");
            return getErrorResponse(errorMessage);
        }
    }

    public Response appointment(Request request) {
        String professorCode = (String) request.getData("professorCode");
        String collegeCode = (String) request.getData("collegeCode");
        String query = "educationalAssistant = " + professorCode;
        boolean result = this.dataHandler.appointment(professorCode, query, collegeCode);
        if (result) {
            String items = " type = " + getStringFormat(Type.EDUCATIONAL_ASSISTANT.toString());
            this.dataHandler.editProfessor(professorCode, items);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            Response response = new Response(ResponseStatus.OK);
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "invalidInputs");
            return getErrorResponse(errorMessage);
        }
    }

    public Response removeProfessor(Request request) {
        String collegeCode = (String) request.getData("collegeCode");
        String professorCode = (String) request.getData("professorCode");
        if (collegeCode.equals(this.dataHandler.getProfessorCollegeCode(professorCode))) {
            boolean result = this.dataHandler.removeProfessor(professorCode);
            if (result) {
                String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
                Response response = new Response(ResponseStatus.OK);
                response.setNotificationMessage(note);
                return response;
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "invalidInputs");
        return getErrorResponse(errorMessage);
    }

    private boolean addProfessorLesson(String professorCode, String lessonCode) {
        List<String> lessons = this.dataHandler.getProfessorLessons(professorCode);
        lessons.add(lessonCode);
        return this.dataHandler.updateProfessorLessons(professorCode, lessons);
    }

    public void removeProfessorsLesson(List<String> professorsCode, String lessonCode) {
        for (String code : professorsCode) {
            List<String> lessons = this.dataHandler.getProfessorLessons(code);
            lessons.remove(lessonCode);
            this.dataHandler.updateProfessorLessons(code, lessons);
        }
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
