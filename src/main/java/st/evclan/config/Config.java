package st.evclan.config;

import java.util.List;

public class Config {

    public final String basePath;
    public final List<PathConfig> paths;
    public final Integer sleepTime;

    public Config(String basePath, List<PathConfig> paths, Integer sleepTime) {
        this.basePath = basePath;
        this.paths = paths;
        this.sleepTime = sleepTime;
    }

}
