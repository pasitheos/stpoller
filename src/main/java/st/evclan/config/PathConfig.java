package st.evclan.config;

import java.util.HashMap;

public class PathConfig {

    private String name;

    private HashMap<String, String> headers;

    private String method;

    public PathConfig() {}

    public PathConfig(String name, HashMap<String, String> headers, String method) {
        this.name = name;
        this.headers = headers;
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
