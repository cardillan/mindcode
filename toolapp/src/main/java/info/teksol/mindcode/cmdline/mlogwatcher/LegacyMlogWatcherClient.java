package info.teksol.mindcode.cmdline.mlogwatcher;

import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jspecify.annotations.NullMarked;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@NullMarked
public class LegacyMlogWatcherClient extends WebSocketClient {
    private final ToolMessageEmitter messageEmitter;
    private final int port;
    private final Semaphore semaphore = new Semaphore(0);
    boolean errorReported;

    public LegacyMlogWatcherClient(int port, ToolMessageEmitter messageEmitter) throws URISyntaxException {
        super(new URI("ws://localhost:" + port));
        this.port = port;
        this.messageEmitter = messageEmitter;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
    }

    @Override
    public void onMessage(String message) {
        switch (message) {
            case "ok" -> messageEmitter.info("  Mlog Watcher: mlog code injected into selected processor.");
            case "schematic_ok" -> messageEmitter.info("  Mlog Watcher: schematics added/updated.");
            case "no_processor" -> {
                messageEmitter.info("  Mlog Watcher: no processor selected.");
                messageEmitter.info("  (The target processor must be selected in Mindustry to receive the code.)");
            }
            default -> messageEmitter.info("  Mlog Watcher: unknown response '%s'.", message);
        }
        semaphore.release(1);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onError(Exception ex) {
        printError(ex, port, messageEmitter);
        errorReported = true;
    }

    private void waitForMessage(long timeout) throws InterruptedException {
        boolean gotMessage = semaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS);
        if (!gotMessage) {
            messageEmitter.info("  No response from Mlog Watcher - maybe an old version is installed?");
        }
    }

    public static void sendSchematic(int port, long timeout, MessageConsumer messageConsumer, String mlog) {
        sendData(port, timeout, messageConsumer, mlog, "Created schematic was sent to Mlog Watcher.");
    }

    public static void sendMlog(int port, long timeout, MessageConsumer messageConsumer, String mlog) {
        sendData(port, timeout, messageConsumer, mlog, "Compiled mlog code was sent to Mlog Watcher.");
    }

    public static void sendData(int port, long timeout, MessageConsumer messageConsumer, String mlog, String message) {
        ToolMessageEmitter messageEmitter = new ToolMessageEmitter(messageConsumer);
        LegacyMlogWatcherClient client = null;
        try {
            client = new LegacyMlogWatcherClient(port, messageEmitter);
            client.connectBlocking(timeout, TimeUnit.MILLISECONDS);
            client.send(mlog);
            client.messageEmitter.info("%n%s", message);
            client.waitForMessage(timeout);
            client.close();
        } catch (Exception ex) {
            if (client == null || !client.errorReported) {
                printError(ex, port, messageEmitter);
            }
        }
    }

    private static void printError(Exception ex, int port, ToolMessageEmitter messageEmitter) {
        messageEmitter.error("Error connecting to Mlog Watcher: %s", ex.getMessage());
        messageEmitter.error("  - make sure Mindustry with active Mlog Watcher mod is running");
        messageEmitter.error("  - verify Mlog Watcher listens on port %d", port);
    }
}
