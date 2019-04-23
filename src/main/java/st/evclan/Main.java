package st.evclan;

import com.sun.jdi.Mirror;
import st.evclan.config.Config;
import st.evclan.config.ConfigReader;
import st.evclan.config.StatePersistor;
import st.evclan.state.ChangeLog;
import st.evclan.state.MirrorView;
import st.evclan.state.Snapshot;
import st.evclan.util.WebFetcher;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        // read config
        final Config config;
        try {
            config = new ConfigReader().read(Paths.get("pollerconf.yaml"));
        } catch (IOException e) {
            System.err.println("Can't read config file: " + e.getMessage());
            return;
        }

        // transform path configs (prepend base path)
        config.paths.forEach(pathConfig -> pathConfig.setName(config.basePath + pathConfig.getName()));

        // validate config
        if (config.sleepTime < 30) {
            System.err.println("Whoa! Slow down there, we don't want to DoS them ;)");
        }

        // read previous state
        final HashMap<String, List<Snapshot>> history;
        try {
            history = new StatePersistor().readOrEmpty(Paths.get(".pollerstate"));
        } catch (IOException e) {
            System.err.println("Can't read pollerstate file: " + e.getMessage());
            return;
        }

        // redirect out streams
        final PrintStream combStream;
        try {
            var logWriter = new PrintStream(new FileOutputStream("poller.log"));
            combStream = new CombinedPrintStream(System.out, logWriter);
            System.setOut(combStream);
        } catch (FileNotFoundException e) {
            System.err.println("Can't open log file for writing: " + e.getMessage());
            return;
        }

        // create and link "players"
        final var changeLog = new ChangeLog();
        final var mirror = new MirrorView();
        final var fetcher = new WebFetcher();

        // subscribe mirror view to changes in change log
        changeLog.subscribe(snapshot -> mirror.processUpdate(snapshot.name));
        // set methods mirror view can use to access change log
        mirror.setRequestUpdate(changeLog::update);
        mirror.setViewFull(changeLog::viewFull);
        // specify where fetch results need be sent for processing (mirror view)
        fetcher.setCallback(mirror::processFetchResult);

        // setup scheduler to run the web fetcher
        final var scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            fetcher.fetchAll(config.paths);
        }, 0, config.sleepTime, TimeUnit.SECONDS);
    }

}

class CombinedPrintStream extends PrintStream {
    // if you're wondering, no I didn't write this all myself ...
    // IntelliJ has pretty good multiline editing features

    private List<PrintStream> streams;

    public CombinedPrintStream(PrintStream ...streams) {
        this(System.out);
        this.streams = Arrays.asList(streams);
    }

    private CombinedPrintStream(OutputStream out) {
        super(out);
    }

    @Override
    public void flush() {
        for (var stream : streams) {
            stream.flush();
        }
    }

    @Override
    public void close() {
        for (var stream : streams) {
            stream.close();
        }
    }

    @Override
    public boolean checkError() {
        boolean err = false;
        for (var stream : streams) {
            err |= stream.checkError();
        }
        return err;
    }

    @Override
    protected void setError() {
        for (var stream : streams) {
            // stream.setError();
            // can't access :(
        }
    }

    @Override
    protected void clearError() {
        for (var stream : streams) {
            // stream.clearError();
            // can't access :(
        }
    }

    @Override
    public void write(int b) {
        for (var stream : streams) {
            stream.write(b);
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        for (var stream : streams) {
            stream.write(buf, off, len);
        }
    }

    @Override
    public void print(boolean b) {
        for (var stream : streams) {
            stream.print(b);
        }
    }

    @Override
    public void print(char c) {
        for (var stream : streams) {
            stream.print(c);
        }
    }

    @Override
    public void print(int i) {
        for (var stream : streams) {
            stream.print(i);
        }
    }

    @Override
    public void print(long l) {
        for (var stream : streams) {
            stream.print(l);
        }
    }

    @Override
    public void print(float f) {
        for (var stream : streams) {
            stream.print(f);
        }
    }

    @Override
    public void print(double d) {
        for (var stream : streams) {
            stream.print(d);
        }
    }

    @Override
    public void print(char[] s) {
        for (var stream : streams) {
            stream.print(s);
        }
    }

    @Override
    public void print(String s) {
        for (var stream : streams) {
            stream.print(s);
        }
    }

    @Override
    public void print(Object obj) {
        for (var stream : streams) {
            stream.print(obj);
        }
    }

    @Override
    public void println() {
        for (var stream : streams) {
            stream.println();
        }
    }

    @Override
    public void println(boolean x) {
        for (var stream : streams) {
            stream.println(x);
        }
    }

    @Override
    public void println(char x) {
        for (var stream : streams) {
            stream.println(x);
        }
    }

    @Override
    public void println(int x) {
        for (var stream : streams) {
            stream.println(x);
        }
    }

    @Override
    public void println(long x) {
        for (var stream : streams) {
            stream.println(x);
        }
    }

    @Override
    public void println(float x) {
        for (var stream : streams) {
            stream.println(x);
        }
    }

    @Override
    public void println(double x) {
        for (var stream : streams) {
            stream.println(x);
        }
    }

    @Override
    public void println(char[] x) {
        for (var stream : streams) {
            stream.println(x);
        }
    }

    @Override
    public void println(String x) {
        for (var stream : streams) {
            stream.println(x);
        }
    }

    @Override
    public void println(Object x) {
        for (var stream : streams) {
            stream.println(x);
        }
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        for (var stream : streams) {
            stream.printf(format, args);
        }
        return this;
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        for (var stream : streams) {
            stream.printf(l, format, args);
        }
        return this;
    }

    @Override
    public PrintStream format(String format, Object... args) {
        for (var stream : streams) {
            stream.format(format, args);
        }
        return this;
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        for (var stream : streams) {
            stream.format(l, format, args);
        }
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq) {
        for (var stream : streams) {
            stream.append(csq);
        }
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        for (var stream : streams) {
            stream.append(csq, start, end);
        }
        return this;
    }

    @Override
    public PrintStream append(char c) {
        for (var stream : streams) {
            stream.append(c);
        }
        return this;
    }

    @Override
    public void write(byte[] b) throws IOException {
        for (var stream : streams) {
            stream.write(b);
        }
    }
}
