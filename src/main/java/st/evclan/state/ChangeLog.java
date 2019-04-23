package st.evclan.state;

import java.util.*;
import java.util.function.Consumer;

public class ChangeLog {

    private HashMap<String, List<Snapshot>> changes;

    private List<Consumer<Snapshot>> subscribers;

    public ChangeLog() {
        this(new HashMap<>());
    }

    public ChangeLog(HashMap<String, List<Snapshot>> history) {
        this.changes = history;
        this.subscribers = new ArrayList<>();
    }

    public void update(Snapshot snapshot) {
        var snapshots = changes.get(snapshot.name);
        if (snapshots == null) {
            snapshots = new ArrayList<>();
        } else {
            snapshot = calculateSparse(snapshot, snapshots);
        }

        snapshots.add(snapshot);
        notifyAll(snapshot);
    }

    private Snapshot calculateSparse(Snapshot snapshot, List<Snapshot> history) {
        if (history.size() < 1) {
            return snapshot;
        }

        var latest = viewFull(history);
        if (snapshot.data.equals(latest.data)) {
            snapshot = snapshot.withData(null);
        }
        if (snapshot.links.equals(latest.links)) {
            snapshot = snapshot.withoutLinks();
        }
        return snapshot;
    }

    public Optional<Snapshot> viewFull(String path) {
        var history = changes.get(path);
        if (history == null) {
            return Optional.empty();
        }

        return Optional.of(viewFull(history));
    }

    private Snapshot viewFull(List<Snapshot> history) {
        // fetch latest fields
        String data = null;
        List<Edge> conections = null;
        for (int i = history.size() - 1; i >= 0; i--) {
            var snap = history.get(i);

            if (data == null && snap.data != null) {
                data = snap.data;
            }
            if (conections == null && snap.links != null) {
                conections = snap.links;
            }

            // check if we can stop early
            if (data != null && conections != null) {
                break;
            }
        }

        // return full view (we can assume that the history contains at least 1 element)
        return Snapshot.buildWithAlreadyImmutableList(history.get(0).name, data, conections);
    }

    private void notifyAll(Snapshot snapshot) {
        for (var sub : subscribers) {
            sub.accept(snapshot);
        }
    }

    public void subscribe(Consumer<Snapshot> subscriber) {
        subscribers.add(subscriber);
    }

}