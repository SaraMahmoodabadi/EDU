package server.network;

import server.logic.managers.courseware.course.CourseManager;
import server.logic.managers.courseware.course.EducationalMaterialManager;
import server.logic.managers.courseware.course.exercise.AddExerciseManager;
import server.logic.managers.courseware.course.exercise.ProfessorExerciseManager;
import server.logic.managers.courseware.course.exercise.StudentExerciseManager;
import server.logic.managers.courseware.mainPage.CoursesManager;
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
import server.logic.managers.offlineClient.LessonManager;
import server.logic.managers.offlineClient.MessageManager;
import server.logic.managers.offlineClient.ScoreManager;
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
        handleOfflineRequest();
    }

    public void handleOfflineRequest() {
        switch (request.getRequestType()) {
            case GET_USER_INFO:
                server.logic.managers.offlineClient.UserManager userManager =
                        new server.logic.managers.offlineClient.UserManager(this.client);
                this.client.sendResponse(userManager.getUser());
                break;
            case GET_USER_LESSONS:
                LessonManager lessonManager = new LessonManager(this.client);
                this.client.sendResponse(lessonManager.getLessons());
                break;
            case GET_USER_SCORES:
                ScoreManager scoreManager = new ScoreManager(this.client);
                this.client.sendResponse(scoreManager.getScores());
                break;
            case GET_USER_CHATS:
                ChatManager manager = new ChatManager(this.client);
                this.client.sendResponse(manager.getAllChats());
                break;
            case GET_USER_LAST_MESSAGES:
                MessageManager messageManager = new MessageManager(this.client);
                this.client.sendResponse(messageManager.getLastMessages());
                break;
            default:
                handleConnectionRequest();
        }
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
                handleCoursewareRequests();
        }
    }

    private void handleCoursewareRequests() {
        CoursesManager coursesManager = new CoursesManager(this.client);
        CourseManager courseManager = new CourseManager(this.client);
        EducationalMaterialManager eduMaterialManager = new EducationalMaterialManager(this.client);
        AddExerciseManager newExerciseManager = new AddExerciseManager(this.client);
        ProfessorExerciseManager pExerciseManager = new ProfessorExerciseManager(this.client);
        StudentExerciseManager sExerciseManager = new StudentExerciseManager(this.client);
        switch (this.request.getRequestType()) {
            case GET_ALL_COURSES:
                this.client.sendResponse(coursesManager.getAllCourses());
                break;
            case GET_MAIN_CALENDAR_DATA:
                this.client.sendResponse(coursesManager.getAllEvents(this.request));
                break;
            case SHOW_COURSE:
                this.client.sendResponse(courseManager.showCourse(this.request));
                break;
            case SHOW_COURSE_CALENDAR:
                this.client.sendResponse(courseManager.getCourseEvents(this.request));
                break;
            case CREATE_EDUCATIONAL_MATERIAL:
                this.client.sendResponse(courseManager.addEduMaterial(this.request));
                break;
            case CREATE_EXERCISE:
                this.client.sendResponse(courseManager.addExercise(this.request));
                break;
            case ADD_STUDENT_TO_COURSE:
                this.client.sendResponse(courseManager.addStudent(this.request));
                break;
            case SHOW_EDUCATIONAL_MATERIAL:
                this.client.sendResponse(eduMaterialManager.showItems(this.request));
                break;
            case SEND_MEDIA_ITEM:
                this.client.sendResponse(eduMaterialManager.addMediaItem(this.request));
                break;
            case SEND_TEXT_ITEM:
                this.client.sendResponse(eduMaterialManager.addTextItem(this.request));
                break;
            case DELETE_EDUCATIONAL_MATERIAL:
                this.client.sendResponse(eduMaterialManager.deleteEducationalMaterial(this.request));
                break;
            case REMOVE_ITEM:
                this.client.sendResponse(eduMaterialManager.removeItem(this.request));
                break;
            case EDIT_ITEM_TEXT:
                this.client.sendResponse(eduMaterialManager.editTextItem(this.request));
                break;
            case EDIT_ITEM_MEDIA:
                this.client.sendResponse(eduMaterialManager.editMediaItem(this.request));
                break;
            case ADD_EXERCISE_DESCRIPTION:
                this.client.sendResponse(newExerciseManager.addExercise(this.request));
                break;
            case SHOW_EXERCISE_PROFESSOR:
                this.client.sendResponse(pExerciseManager.getAnswers(this.request));
                break;
            case REGISTER_EXERCISE_SCORE:
                this.client.sendResponse(pExerciseManager.registerScore(this.request));
                break;
            case SHOW_EXERCISE_STUDENT:
                this.client.sendResponse(sExerciseManager.getExercise(this.request));
                break;
            case SEND_EXERCISE_ANSWER:
                this.client.sendResponse(sExerciseManager.getAnswer(this.request));
                break;
        }
    }

}
