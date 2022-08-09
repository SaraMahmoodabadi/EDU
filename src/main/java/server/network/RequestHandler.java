package server.network;

import server.logic.managers.edu.eduServices.PlanManager;
import server.logic.managers.edu.eduServices.RequestManager;
import server.logic.managers.edu.profile.ProfileManager;
import server.logic.managers.edu.registration.NewUserManager;
import server.logic.managers.edu.registration.RegistrationManager;
import server.logic.managers.edu.reportCard.EDUStatusManager;
import server.logic.managers.edu.reportCard.ReportCardManager;
import server.logic.managers.edu.unitSelection.UnitSelectionManager;
import server.logic.managers.edu.unitSelection.UnitSelectionTimeManager;
import server.logic.managers.edu.user.UserManager;
import server.logic.managers.messages.admin.AdminManager;
import server.logic.managers.messages.messages.MessagesManager;
import server.logic.managers.messages.messenger.ChatManager;
import server.logic.managers.messages.messenger.NewChatManager;
import server.logic.managers.messages.mohseni.MohseniManager;
import shared.model.user.UserType;
import shared.request.Request;
import shared.response.Response;

public class RequestHandler {
    private ClientHandler client;
    private Request request;

    public void handleRequests(ClientHandler client, Request request) {
        this.client = client;
        this.request = request;
        if (this.request == null) return;
        handleConnectionRequest();
    }

    private void handleConnectionRequest() {
        if (request == null) return;
        UserManager manager = new UserManager(this.client);
        switch (request.getRequestType()) {
            case START_CONNECTION:
                Response response = manager.sendCaptchaImage();
                client.sendResponse(response);
                break;
            case END_CONNECTION:
                break;
            default:
                handleInitialRequests();
        }
    }

    private void handleInitialRequests() {
        UserManager manager = new UserManager(this.client);
        switch (this.request.getRequestType()) {
            case LOGIN:
                client.sendResponse(manager.login(request));
                break;
            case RECAPTCHA:
                client.sendResponse(manager.sendCaptchaImage());
                break;
            case CHANGE_PASSWORD:
                client.sendResponse(manager.changePassword(request));
                break;
            case SHOW_MAIN_PAGE:
                client.sendResponse(manager.getMainPageData());
                break;
            default:
                handleMainRequests();
        }
    }

    private void handleMainRequests() {
        switch (this.request.getRequestType()) {
            case SHOW_NEW_USER_PAGE:
                break;
            case SHOW_UNIT_SELECTION_PAGE:
                break;
            case SHOW_COURSEWARE:
                break;
            case SHOW_MESSAGES_PAGE:
                break;
            case SHOW_MESSENGER:
                break;
            case LOGOUT:
                break;
            default:
                handleRegistrationRequests();
        }
    }

    private void handleRegistrationRequests() {
        RegistrationManager manager = new RegistrationManager(this.client);
        switch (this.request.getRequestType()) {
            case SHOW_LESSONS_LIST_PAGE:
                client.sendResponse(manager.getAllLessons());
                break;
            case SHOW_DESIRED_LESSONS_LIST:
                client.sendResponse(manager.getDesiredLessons(request));
                break;
            case SHOW_EDIT_LESSON_PAGE:
                break;
            case REGISTER_NEW_LESSON:
                client.sendResponse(manager.addLesson(request));
                break;
            case EDIT_LESSON:
                client.sendResponse(manager.editLesson(request));
                break;
            case REMOVE_LESSON:
            case REMOVE_LESSON_GROUP:
                client.sendResponse(manager.removeLesson(request));
                break;
            default:
                handleEduServicesRequests();
        }
    }

    private void handleEduServicesRequests() {
        RegistrationManager manager = new RegistrationManager(this.client);
        switch (this.request.getRequestType()) {
            case SHOW_PROFESSORS_LIST_PAGE:
                this.client.sendResponse(manager.getProfessors());
                break;
            case SHOW_DESIRED_PROFESSORS_LIST:
                break;
            case EDIT_PROFESSOR:
                this.client.sendResponse(manager.editProfessor(request));
                break;
            case REMOVE_PROFESSOR:
                this.client.sendResponse(manager.removeProfessor(request));
                break;
            case DEPOSAL_EDU_ASSISTANT:
                this.client.sendResponse(manager.deposal(request));
                break;
            case APPOINTMENT_EDU_ASSISTANT:
                this.client.sendResponse(manager.appointment(request));
                break;
            default:
                handleUnitSelectionRequests();
        }
    }

    private void handleUnitSelectionRequests() {
        UnitSelectionTimeManager timeManager = new UnitSelectionTimeManager(this.client);
        UnitSelectionManager manager = new UnitSelectionManager(this.client);
        switch (this.request.getRequestType()) {
            case SET_UNIT_SELECTION_TIME:
                this.client.sendResponse(timeManager.setTime(request));
                break;
            case SET_END_UNIT_SELECTION_TIME:
                this.client.sendResponse(timeManager.setEndTime(request));
                break;
            case SHOW_STUDENT_UNIT_SELECTION_PAGE:
                this.client.sendResponse(manager.getSuggestedLessons(request));
                break;
            case GET_LESSONS_IN_UNIT_SELECTION:
                this.client.sendResponse(manager.getCollegeLesson(request));
                break;
            case MARK_LESSON:
                this.client.sendResponse(manager.markLesson(request));
                break;
            case UN_MARK_LESSON:
                this.client.sendResponse(manager.unmarkedLesson(request));
                break;
            case GET_LESSON_GROUPS:
                this.client.sendResponse(manager.getLessonGroups(request));
                break;
            case REMOVE_LESSON_UNIT_SELECTION:
                this.client.sendResponse(manager.removeLesson(request));
                break;
            case CHANGE_GROUP_UNIT_SELECTION:
                this.client.sendResponse(manager.changeLessonGroup(request));
                break;
            case TAKE_LESSON_UNIT_SELECTION:
                this.client.sendResponse(manager.takeLesson(request));
                break;
            case REQUEST_TAKE_LESSON_UNIT_SELECTION:
                this.client.sendResponse(manager.requestToTakeLesson(request));
                break;
            default: handleNewUserRequests();
        }
    }

    private void handleNewUserRequests() {
        NewUserManager manager = new NewUserManager(this.client);
        switch (this.request.getRequestType()) {
            case SELECT_USER_TYPE:
                break;
            case REGISTER_NEW_USER:
                this.client.sendResponse(manager.makeUser(request));
                break;
            default:
                handleGetPlanRequests();
        }
    }

    private void handleGetPlanRequests() {
        PlanManager manager = new PlanManager(this.client);
        switch (this.request.getRequestType()) {
            case SHOW_WEEKLY_SCHEDULE_PAGE:
                this.client.sendResponse(manager.getWeeklyPlan());
                break;
            case SHOW_EXAM_LIST_PAGE:
                this.client.sendResponse(manager.getExamList());
                break;
            default:
                handleRequestPageRequests();
        }
    }

    private void handleRequestPageRequests() {
        RequestManager manager = new RequestManager(this.client);
        switch (this.request.getRequestType()) {
            case SHOW_REQUESTS_PAGE:
                if (this.client.getUserType() == UserType.STUDENT)
                    this.client.sendResponse(manager.getGrade());
                break;
            case REGISTER_REQUEST:
                this.client.sendResponse(manager.createRequest(request));
                break;
            case REGISTER_RECOMMENDATION:
                this.client.sendResponse(manager.getAnswerRecommendation(request));
                break;
            case REGISTER_REQUEST_ANSWER:
                this.client.sendResponse(manager.getAnswerRequest(request));
                break;
            default:
                handleReportCardRequests();
        }
    }

    private void handleReportCardRequests() {
        ReportCardManager manager = new ReportCardManager(this.client);
        EDUStatusManager eduStatusManager = new EDUStatusManager(this.client);
        switch (this.request.getRequestType()) {
            case SHOW_TEMPORARY_SCORES_PAGE:
            case SHOW_STUDENT_SCORES:
                this.client.sendResponse(manager.getStudentTemporaryScores(request));
                break;
            case REGISTER_PROTEST:
                this.client.sendResponse(manager.setProtest(request));
                break;
            case SHOW_LESSON_SCORES:
                this.client.sendResponse(manager.getLessonTemporaryScores(request));
                break;
            case REGISTER_PROTEST_ANSWER:
                this.client.sendResponse(manager.setProtestAnswer(request));
                break;
            case REGISTER_SCORE:
                this.client.sendResponse(manager.setScore(request));
                break;
            case REGISTER_ALL_SCORES:
                this.client.sendResponse(manager.setScores(request));
                break;
            case FINALIZE_SCORES:
                this.client.sendResponse(manager.finalizeScores(request));
                break;
            case SHOW_PROFESSOR_SCORES:
                this.client.sendResponse(manager.getProfessorScores(request));
                break;
            case SHOW_EDU_STATUS_PAGE:
                this.client.sendResponse(eduStatusManager.getScores(request));
                break;
            default:
                handleProfileRequests();
        }
    }

    private void handleProfileRequests() {
        ProfileManager manager = new ProfileManager(this.client);
        switch (this.request.getRequestType()) {
            case SHOW_PROFILE_PAGE:
                this.client.sendResponse(manager.getInformation());
                break;
            case REGISTER_EMAIL:
                this.client.sendResponse(manager.changeEmailAddress(request));
                break;
            case REGISTER_PHONE_NUMBER:
                this.client.sendResponse(manager.changePhoneNumber(request));
                break;
            default:
                handleMessages();
        }
    }

    private void handleMessages() {
        AdminManager adminManager = new AdminManager(this.client);
        MessagesManager messagesManager = new MessagesManager(this.client);
        ChatManager chatManager = new ChatManager(this.client);
        NewChatManager newChatManager = new NewChatManager(this.client);
        MohseniManager mohseniManager = new MohseniManager(this.client);
        switch (this.request.getRequestType()) {
            case SHOW_ALL_MESSAGES:
                this.client.sendResponse(messagesManager.getAllMessages());
                break;
            case SHOW_MESSAGE:
                this.client.sendResponse(messagesManager.getMessage(request));
                break;
            case SEND_MESSAGE_ADMIN:
                this.client.sendResponse(adminManager.sendAnswer(request));
                break;
            case SEND_REQUEST_ANSWER:
                this.client.sendResponse(messagesManager.getRequestResult(request));
                break;
            case SHOW_ALL_ADMIN_MESSAGES:
                this.client.sendResponse(adminManager.showAllMessages());
                break;
            case SHOW_ADMIN_MESSAGE:
                this.client.sendResponse(adminManager.getMessage(request));
                break;
            case SEND_MESSAGE_CHAT:
                this.client.sendResponse(chatManager.sendMessage(request));
                break;
            case SHOW_ALL_CHATS:
                this.client.sendResponse(chatManager.getAllChats());
                break;
            case SHOW_CHAT:
                this.client.sendResponse(chatManager.getChat(request));
                break;
            case MAKE_NEW_CHAT:
                this.client.sendResponse(newChatManager.sendMessage(request));
                break;
            case SHOW_USERS_IN_NEW_CHAT:
                this.client.sendResponse(newChatManager.getUsers(request));
                break;
            case REQUEST_SEND_MESSAGE:
                this.client.sendResponse(newChatManager.sendRequest(request));
                break;
            case SEND_MESSAGE_MOHSENI:
                this.client.sendResponse(mohseniManager.sendMessage(request));
                break;
            case GET_STUDENT_PROFILE_MOHSENI:
                this.client.sendResponse(mohseniManager.getStudentData(request));
                break;
            case GET_ALL_STUDENTS_INFORMATION_MOHSENI:
                this.client.sendResponse(mohseniManager.getAllStudentsInfo());
                break;
            case GET_STUDENTS_INFORMATION_MOHSENI:
                this.client.sendResponse(mohseniManager.getStudentsInfo(request));
                break;
            case SEND_MESSAGE_TO_ADMIN:
                this.client.sendResponse(adminManager.sendMessageToAdmin(request));
                break;
            default:

        }
    }

}
