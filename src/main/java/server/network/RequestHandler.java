package server.network;

import shared.request.Request;

public class RequestHandler {
    private boolean result;
    private int clientID;
    private Request request;

    public void handleRequests(int clientID, Request request) {
        this.result = false;
        this.clientID = clientID;
        this.request = request;
        handleInitialRequests();
    }

    public void handleInitialRequests() {
        switch (this.request.getRequestType()) {
            case LOGIN:
                break;
            case RECAPTCHA:
                break;
            case CHANGE_PASSWORD:
                break;
            default:
                handleMainRequests();
        }
    }

    public void handleMainRequests() {
        switch (this.request.getRequestType()) {
            case SHOW_LESSONS_LIST_PAGE:
                break;
            case SHOW_PROFESSORS_LIST_PAGE:
                break;
            case SHOW_NEW_USER_PAGE:
                break;
            case SHOW_WEEKLY_SCHEDULE_PAGE:
                break;
            case SHOW_EXAM_LIST_PAGE:
                break;
            case SHOW_REQUESTS_PAGE:
                break;
            case SHOW_UNIT_SELECTION_PAGE:
                break;
            case SHOW_TEMPORARY_SCORES_PAGE:
                break;
            case SHOW_EDU_STATUS_PAGE:
                break;
            case SHOW_COURSEWARE:
                break;
            case SHOW_MESSAGES_PAGE:
                break;
            case SHOW_MESSENGER:
                break;
            case SHOW_PROFILE_PAGE:
                break;
            case LOGOUT:
                break;
            default:
                handleRegistrationRequests();
        }
    }

    public void handleRegistrationRequests() {
        switch (this.request.getRequestType()) {
            case SHOW_DESIRED_LESSONS_LIST:
                break;
            case SHOW_EDIT_LESSON_PAGE:
                break;
            case REGISTER_NEW_LESSON:
                break;
            case EDIT_LESSON:
                break;
            case REMOVE_LESSON:
                break;
            case REMOVE_LESSON_GROUP:
                break;
            default:
                handleEduServicesRequests();
        }
    }

    public void handleEduServicesRequests() {
        switch (this.request.getRequestType()) {
            case SHOW_DESIRED_PROFESSORS_LIST:
                break;
            case SHOW_EDIT_PROFESSORS_PAGE:
                break;
            case SHOW_ADD_NEW_PROFESSOR_PAGE:
                break;
            case EDIT_PROFESSOR:
                break;
            case REMOVE_PROFESSOR:
                break;
            case DEPOSAL_EDU_ASSISTANT:
                break;
            case APPOINTMENT_EDU_ASSISTANT:
                break;
            default:
                handleNewUserRequests();
        }
    }

    public void handleNewUserRequests() {
        switch (this.request.getRequestType()) {
            case SELECT_USER_TYPE:
                break;
            case REGISTER_NEW_USER:
                break;
            default:
                handleRequestPageRequests();
        }
    }

    public void handleRequestPageRequests() {
        switch (this.request.getRequestType()) {
            case REGISTER_REQUEST:
                break;
            case REGISTER_RECOMMENDATION:
                break;
            case REGISTER_REQUEST_ANSWER:
                break;
            default:
                handleReportCardRequests();
        }
    }

    public void handleReportCardRequests() {
        switch (this.request.getRequestType()) {
            case REGISTER_PROTEST:
                break;
            case SHOW_LESSON_SCORES:
                break;
            case REGISTER_PROTEST_ANSWER:
                break;
            case REGISTER_ALL_SCORES:
                break;
            case FINALIZE_SCORES:
                break;
            case SHOW_STUDENT_SCORES:
                break;
            case SHOW_PROFESSOR_SCORES:
                break;
            default:
                handleProfileRequests();
        }
    }

    public void handleProfileRequests() {
        switch (this.request.getRequestType()) {
            case REGISTER_EMAIL:
                break;
            case REGISTER_PHONE_NUMBER:
                break;
        }
    }

}