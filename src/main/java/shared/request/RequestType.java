package shared.request;

public enum RequestType {
    START_CONNECTION,

    LOGIN,
    RECAPTCHA,
    CHANGE_PASSWORD,

    SHOW_MAIN_PAGE,
    SHOW_LESSONS_LIST_PAGE,
    SHOW_PROFESSORS_LIST_PAGE,
    SHOW_NEW_USER_PAGE,
    SHOW_WEEKLY_SCHEDULE_PAGE,
    SHOW_EXAM_LIST_PAGE,
    SHOW_REQUESTS_PAGE,
    SHOW_UNIT_SELECTION_PAGE,
    SHOW_TEMPORARY_SCORES_PAGE,
    SHOW_EDU_STATUS_PAGE,
    SHOW_COURSEWARE,
    SHOW_MESSAGES_PAGE,
    SHOW_MESSENGER,
    SHOW_PROFILE_PAGE,
    LOGOUT,

    SHOW_DESIRED_LESSONS_LIST,
    SHOW_EDIT_LESSON_PAGE,
    REGISTER_NEW_LESSON,
    EDIT_LESSON,
    REMOVE_LESSON,
    REMOVE_LESSON_GROUP,

    SHOW_DESIRED_PROFESSORS_LIST,
    SHOW_EDIT_PROFESSORS_PAGE,
    SHOW_ADD_NEW_PROFESSOR_PAGE,
    EDIT_PROFESSOR,
    REMOVE_PROFESSOR,
    DEPOSAL_EDU_ASSISTANT,
    APPOINTMENT_EDU_ASSISTANT,

    SELECT_USER_TYPE,
    REGISTER_NEW_USER,

    REGISTER_REQUEST,
    REGISTER_RECOMMENDATION,
    REGISTER_REQUEST_ANSWER,

    REGISTER_PROTEST,
    SHOW_LESSON_SCORES,
    REGISTER_PROTEST_ANSWER,
    REGISTER_SCORE,
    REGISTER_ALL_SCORES,
    FINALIZE_SCORES,
    SHOW_STUDENT_SCORES,
    SHOW_PROFESSOR_SCORES,

    REGISTER_EMAIL,
    REGISTER_PHONE_NUMBER,

    SET_UNIT_SELECTION_TIME,
    SET_END_UNIT_SELECTION_TIME,
    GET_LESSONS_IN_UNIT_SELECTION,
    SHOW_STUDENT_UNIT_SELECTION_PAGE,
    MARK_LESSON,
    UN_MARK_LESSON,
    GET_LESSON_GROUPS,
    REMOVE_LESSON_UNIT_SELECTION,
    CHANGE_GROUP_UNIT_SELECTION,
    TAKE_LESSON_UNIT_SELECTION,
    REQUEST_TAKE_LESSON_UNIT_SELECTION,

    SHOW_ALL_MESSAGES,
    SHOW_MESSAGE,
    SEND_MESSAGE_ADMIN,
    SEND_REQUEST_ANSWER,

    SEND_MESSAGE_CHAT,
    SHOW_ALL_CHATS,
    SHOW_CHAT,

    END_CONNECTION
}
