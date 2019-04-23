package st.evclan.state;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Snapshot {

    public static Pair<Snapshot> link(Snapshot from, Snapshot to) {
        var pair = new Pair<Snapshot>();
        var edge = new Edge(from, to);

        {
            var connections = new ArrayList<Edge>(from.links.size() + 1);
            connections.addAll(from.links);
            connections.add(edge);
            pair.left = new Snapshot(from.name, from.data, connections);
        }

        {
            var connections = new ArrayList<Edge>(to.links.size() + 1);
            connections.addAll(to.links);
            connections.add(edge);
            pair.right = new Snapshot(to.name, to.data, connections);
        }
        return pair;
    }

    public static Snapshot buildWithAlreadyImmutableList(String name, String data, List<Edge> links) {
        return new Snapshot(name, data, links, 42);
    }

    public final String name;

    public final String data;

    public final List<Edge> links;

    private UUID hash;

    public Snapshot(String name, String data) {
        this.data = data;
        this.name = name;
        this.links = null;
    }

    public Snapshot(String name, String data, List<Edge> links) {
        this.name = name;
        this.data = data;
        if (links != null) {
            this.links = Collections.unmodifiableList(links);
        } else {
            this.links = null;
        }
    }

    private Snapshot(String name, String data, List<Edge> links, int just4theOverload) {
        this.name = name;
        this.data = data;
        this.links = links; // don't wrap already immutable map in new immutable view
    }

    public Snapshot withName(String name) {
        return new Snapshot(name, this.data, this.links, 42);
    }

    public Snapshot withData(String data) {
        return new Snapshot(this.name, data, this.links, +41_117);
    }

    public Snapshot withoutLinks() {
        return new Snapshot(this.name, this.data);
    }

    public UUID hash() throws Error {
        // check if hash has already been computed
        if (hash != null) {
            return hash;
        }

        // concatenate all strings that go into the hash
        var dig = new StringBuilder();
        dig.append(name);
        dig.append(data);
        for (var edge : links) {
            dig.append(edge.orig.name);
            dig.append("->");
            dig.append(edge.dest.name);
        }

        // calculate SHA1 of data
        byte[] res;
        try {
            var md = MessageDigest.getInstance("SHA1");
            res = md.digest(dig.toString().getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        }

        // collect bytes of SHA1 into 128 bit UUID
        long lsb = 0x0;
        long msb = 0x0;
        for (int i = 0; i < res.length; i++) {
            if (i % 2 == 0) {
                lsb ^= ((long)res[i]) << ((i%16)*4);
            } else {
                msb ^= ((long)res[i]) << (((i-1)%16)*4);
            }
        }
        System.out.println("calc " + Long.toHexString(msb) + Long.toHexString(lsb) + " from " + Arrays.toString(res));
        this.hash = new UUID(msb, lsb);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Snapshot snapshot = (Snapshot) o;
        return name.equals(snapshot.name) &&
                Objects.equals(data, snapshot.data) &&
                Objects.equals(links, snapshot.links);
    }

}

class Edge {

    final Snapshot orig;
    final Snapshot dest;

    Edge(Snapshot orig, Snapshot dest) {
        this.orig = orig;
        this.dest = dest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return this.orig.name.equals(edge.orig.name)
                && this.dest.name.equals(edge.dest.name);
    }

}

class Pair<T> {
    T left;
    T right;
}