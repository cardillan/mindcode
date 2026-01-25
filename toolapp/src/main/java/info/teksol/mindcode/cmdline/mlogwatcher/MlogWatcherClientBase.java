package info.teksol.mindcode.cmdline.mlogwatcher;

import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@NullMarked
public abstract class MlogWatcherClientBase implements MlogWatcherClient {
    protected final ToolMessageEmitter log;

    private final String path;
    private final int port;
    private final long timeout;

    private @Nullable LocalWebSocketClient client;
    private boolean errorReported = false;
    private final Semaphore semaphore = new Semaphore(0);
    private @Nullable String response;

    public MlogWatcherClientBase(ToolMessageEmitter log, int port, long timeout, String path) {
        this.log = log;
        this.path = path;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public boolean connect() {
        if (client != null) {
            throw new IllegalStateException("Already connected");
        }

        try {
            client = new LocalWebSocketClient(new URI("ws://localhost:" + port + path));
            client.connectBlocking(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            printError(e);
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        if (client == null) {
            throw new IllegalStateException("Not connected");
        } else {
            client.close();
            client = null;
        }
    }

    protected abstract void onTimeout();

    public void send(String text) {
        if (client == null) {
            throw new IllegalStateException("Not connected");
        }
        client.send(text);
    }

    protected @Nullable String waitForResponse() {
        boolean gotMessage = false;
        try {
            gotMessage = semaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException _) {
            // Do nothing
        }

        if (!gotMessage) {
            onTimeout();
            return null;
        } else {
            return response;
        }
    }

    protected void printError(Exception ex) {
        if (!errorReported) {
            log.error("Error connecting to Mlog Watcher: %s", ex.getMessage());
            log.error("  - make sure Mindustry with active Mlog Watcher mod is running");
            log.error("  - verify Mlog Watcher listens on port %d", port);
            errorReported = true;
        }
    }

    private class LocalWebSocketClient extends WebSocketClient {
        public LocalWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
        }

        public void onMessage(String message) {
            response = message;
            semaphore.release(1);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
        }

        @Override
        public void onError(Exception ex) {
            printError(ex);
        }
    }
}
