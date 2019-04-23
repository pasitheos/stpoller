package st.evclan.config;

import com.fasterxml.jackson.databind.ObjectReader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigReader {

    private ObjectReader objectReader;

    public Config read(Path file) throws IOException {
        var bytes = Files.readAllBytes(file);
        return objectReader.readValue(bytes);
    }
}
