package st.evclan.state;

import st.evclan.util.FetchResult;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class MirrorView {

    private HashMap<String, UUID> hashes;

    private Function<String, Optional<Snapshot>> viewFull;

    private Consumer<Snapshot> requestUpdate;

    public void initiate(Set<String> initialStateMeta) {
        initialStateMeta.forEach(this::processUpdate);
    }

    public void processFetchResult(FetchResult result) {
        // check if something even exists
        if (result.status >= 200 && result.status < 300) {
            var snapshot = new Snapshot(result.path, result.content);
            var currHash = hashes.get(result.path);

            if (!currHash.equals(snapshot.hash())) {
                requestUpdate.accept(snapshot);
            }

            // TODO check content for links to also keep track of used JS, CSS files etc.
        }
    }

    public void processUpdate(String name) {
        // pull out full version of snapshot since
        // update notifs only include what was changed
        var fullOpt = viewFull.apply(name);
        if (fullOpt.isPresent()) {
            var full = fullOpt.get();
            hashes.put(full.name, full.hash());
        }
    }

    public void setViewFull(Function<String, Optional<Snapshot>> viewFull) {
        this.viewFull = viewFull;
    }

    public void setRequestUpdate(Consumer<Snapshot> requestUpdate) {
        this.requestUpdate = requestUpdate;
    }
}
