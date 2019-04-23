package st.evclan.util;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import st.evclan.config.PathConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class WebFetcher {

    private Consumer<FetchResult> callback;

    public void fetchAll(List<PathConfig> paths) {
        paths.forEach(path -> fetch(path.getName(), path.getHeaders(), path.getMethod()));
    }

    public void fetch(String path) {
        fetch(path, Map.of(), "GET");
    }

    public void fetch(String path, Map<String, String> headers, String method) {
        // TODO regard method
        var future = Unirest.get(path).asStringAsync(new Callback<String>() {
            public void completed(HttpResponse<String> response) {
                process(path, response);
            }

            public void failed(UnirestException e) {}

            public void cancelled() {}
        });
    }

    private void process(String path, HttpResponse<String> response) {
        callback.accept(new FetchResult(
                path,
                response.getStatus(),
                response.getBody(),
                response.getHeaders()));
    }

    public void setCallback(Consumer<FetchResult> callback) {
        this.callback = callback;
    }

}
