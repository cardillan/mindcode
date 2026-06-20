package info.teksol.mindcode.cmdline.mlogwatcher;

import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.URI;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@NullMarked
public abstract class MlogWatcherClientBase implements MlogWatcherClient {
    protected final ToolMessageEmitter log;

    private final String path;
    private final int port;
    private final int retries;
    private final long timeout;
    private final boolean printStackTrace;

    private @Nullable LocalWebSocketClient client;
    private boolean errorReported = false;
    private final Semaphore semaphore = new Semaphore(0);
    private @Nullable String response;

    public MlogWatcherClientBase(ToolMessageEmitter log, int port, int retries, long timeout, String path, boolean printStackTrace) {
        this.log = log;
        this.path = path;
        this.port = port;
        this.retries = retries;
        this.timeout = timeout;
        this.printStackTrace = printStackTrace;
    }

    @Override
    public boolean connect() {
        if (client != null) {
            throw new IllegalStateException("Already connected");
        }

        int attempt = 0;

        try {
            while (true) {
                client = new LocalWebSocketClient(new URI("ws://localhost:" + port + path));
                if (client.connectBlocking(timeout, TimeUnit.MILLISECONDS)) return true;

                Exception exception = client.getException(100);
                if (shouldRetry(exception)) {
                    if (attempt++ >= retries) {
                        throw exception == null ? new Exception("Connection timed out") : exception;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            printError(e);
            return false;
        }
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
        if (printStackTrace && !(ex instanceof ConnectException)) {
            //noinspection CallToPrintStackTrace
            ex.printStackTrace();
        }

        if (!errorReported) {
            log.error("Error connecting to Mlog Watcher: %s", ex.getMessage());
            log.error("  - make sure Mindustry with active Mlog Watcher mod is running");
            log.error("  - verify Mlog Watcher listens on port %d", port);
            errorReported = true;
        }
    }

    private boolean shouldRetry(@Nullable Exception ex) {
        return switch (ex) {
            case ConnectException _, SocketException _ -> true;
            case null -> true;
            default -> false;
        };
    }

    private class LocalWebSocketClient extends WebSocketClient {
        private boolean opened = false;
        private @Nullable Exception lastException;

        public LocalWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            opened = true;
        }

        public void onMessage(String message) {
            response = message;
            semaphore.release(1);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
        }

        @Override
        public synchronized void onError(Exception ex) {
            lastException = ex;
            if (opened || !shouldRetry(ex)) {
                printError(ex);
            }
            notifyAll();
        }

        public synchronized @Nullable Exception getException(long timeoutMillis) {
            long finish = System.currentTimeMillis() + timeoutMillis;
            while (lastException == null && System.currentTimeMillis() < finish) {
                try {
                    long time = finish - System.currentTimeMillis();
                    if (time > 0) wait(timeoutMillis);
                } catch (InterruptedException ignored) {
                }
            }
            return lastException;
        }
    }
}
