package info.teksol.mindcode.cmdline;

import info.teksol.mc.messages.MessageLogger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jspecify.annotations.NullMarked;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@NullMarked
public class MlogWatcherClient extends WebSocketClient {
    boolean errorReported;
    private final int port;
    private final MessageLogger messageLogger;
    private final Semaphore semaphore = new Semaphore(0);

    public MlogWatcherClient(int port, MessageLogger messageLogger) throws URISyntaxException {
        super(new URI("ws://localhost:" + port));
        this.port = port;
        this.messageLogger = messageLogger;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
    }

    @Override
    public void onMessage(String message) {
        switch (message) {
            case "ok" -> messageLogger.info("  Mlog Watcher: success.");
            case "no_processor" -> {
                messageLogger.info("  Mlog Watcher: no processor selected.");
                messageLogger.info("  (The target processor must be selected in Mindustry to receive the code.)");
            }
            default -> messageLogger.info("  Mlog Watcher: unknown response '%s'.", message);
        }
        semaphore.release(1);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onError(Exception ex) {
        printError(ex, port, messageLogger);
        errorReported = true;
    }

    private void waitForMessage(long timeout) throws InterruptedException {
        boolean gotMessage = semaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS);
        if (!gotMessage) {
            messageLogger.info("  No response from Mlog Watcher - maybe an old version is installed?");
        }
    }

    public static void sendMlog(int port, long timeout, MessageLogger messageLogger, String mlog) {
        MlogWatcherClient client = null;
        try {
            client = new MlogWatcherClient(port, messageLogger);
            client.connectBlocking(timeout, TimeUnit.MILLISECONDS);
            client.send(mlog);
            messageLogger.info("\nCompiled mlog code was sent to Mlog Watcher.");
            client.waitForMessage(timeout);
            client.close();
        } catch (Exception ex) {
            if (client == null || !client.errorReported) {
                printError(ex, port, messageLogger);
            }
        }
    }

    private static void printError(Exception ex, int port, MessageLogger messageLogger) {
        messageLogger.error("Error connecting to Mlog Watcher: %s", ex.getMessage());
        messageLogger.error("  - make sure Mindustry with active Mlog Watcher mod is running");
        messageLogger.error("  - verify Mlog Watcher listens on port %d", port);
    }
}
