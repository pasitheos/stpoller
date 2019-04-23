package st.evclan.config;

import st.evclan.state.ChangeLog;
import st.evclan.state.Snapshot;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatePersistor {

    public HashMap<String, List<Snapshot>> readOrEmpty(Path path) throws IOException {
        if (Files.isReadable(path)) {
            try (var in = new ObjectInputStream(Files.newInputStream(path))) {
                return (HashMap<String, List<Snapshot>>) in.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace(); // TODO
            }
        }
        return new HashMap<>(); //TODO
    }

    public void write(HashMap<String, List<Snapshot>> data, Path path) throws IOException {
        if (Files.isWritable(path)) {
            var out = new ObjectOutputStream(Files.newOutputStream(path));
            out.writeObject(data);
            out.flush();
            out.close();
        } //TODO else what?
    }

}
