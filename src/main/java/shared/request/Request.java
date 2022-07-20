package shared.request;

import shared.model.user.UserType;

import java.util.HashMap;

public class Request {

    private RequestType requestType;
    private HashMap<String, Object> data;
    private UserType userType;

    public Request() {}

    public Request(RequestType requestType, UserType userType) {
        this.requestType = requestType;
        this.data = new HashMap<>();
        this.userType = userType;
    }

    public RequestType getRequestType() {
        return this.requestType;
    }

    public void addData(String dataName, Object data) {
        this.data.put(dataName, data);
    }

    public Object getData(String dataName) {
        return this.data.get(dataName);
    }

}
