package shared.request;

import java.util.HashMap;

public class Request {

    private RequestType requestType;
    private HashMap<String, Object> data;

    public Request() {}

    public Request(RequestType requestType) {
        this.requestType = requestType;
        this.data = new HashMap<>();
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
