package shared.response;

import java.util.HashMap;

public class Response {
    private ResponseStatus status;
    private HashMap<String, Object> data;
    private String errorMessage;
    private String notificationMessage;

    public Response() {}

    public Response(ResponseStatus status) {
        this.status = status;
        this.data = new HashMap<>();
    }

    public ResponseStatus getStatus() {
        return this.status;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public Object getData(String dataName) {
        return this.data.get(dataName);
    }

    public void addData(String dataName, Object data) {
        this.data.put(dataName, data);
    }
}
