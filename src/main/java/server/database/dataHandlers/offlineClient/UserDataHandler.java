package server.database.dataHandlers.offlineClient;

import server.database.MySQLHandler;
import shared.model.user.professor.Professor;
import shared.model.user.student.Student;

public class UserDataHandler {
    private final MySQLHandler databaseHandler;

    public UserDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public Student getStudent(String userName) {
        return null;
    }

    public Professor getProfessor(String userName) {
        return null;
    }

    public String getProfileImage(String userName) {
        return null;
    }
}
