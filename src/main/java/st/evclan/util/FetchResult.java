package st.evclan.util;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.Unirest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchResult {
    public final String path;
    public final int status;
    public final String content;
    public final Map<String, List<String>> headers;

    public FetchResult(String path, int status, String content, Map<String, List<String>> headers) {
        this.path = path;
        this.status = status;
        this.content = content;
        if (headers != null) {
            this.headers = Collections.unmodifiableMap(headers);
        } else {
            this.headers = new HashMap<>();
        }
    }
}
