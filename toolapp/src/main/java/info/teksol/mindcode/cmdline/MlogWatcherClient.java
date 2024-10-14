package info.teksol.mindcode.cmdline;

import info.teksol.mindcode.ToolMessage;
import info.teksol.mindcode.compiler.CompilerOutput;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class MlogWatcherClient extends WebSocketClient {
    boolean errorReported;
    private final int port;
    private final CompilerOutput<?> output;
    private final Semaphore semaphore = new Semaphore(0);

    public MlogWatcherClient(int port, CompilerOutput<?> output) throws URISyntaxException {
        super(new URI("ws://localhost:" + port));
        this.port = port;
        this.output = output;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
    }

    @Override
    public void onMessage(String message) {
        switch (message) {
            case "ok" -> output.addMessage(ToolMessage.info("  Mlog Watcher: success."));
            case "no_processor" -> {
                output.addMessage(ToolMessage.info("  Mlog Watcher: no processor selected."));
                output.addMessage(ToolMessage.info("  (The target processor must be selected in Mindustry to receive the code.)"));
            }
            default -> output.addMessage(ToolMessage.info("  Mlog Watcher: unknown response '%s'.", message));
        }
        semaphore.release(1);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onError(Exception ex) {
        printError(ex, port, output);
        errorReported = true;
    }

    private void waitForMessage(long timeout) throws InterruptedException {
        boolean gotMessage = semaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS);
        if (!gotMessage) {
            output.addMessage(ToolMessage.info("  No response from Mlog Watcher - maybe an old version is installed?"));
        }
    }

    public static void sendMlog(int port, long timeout, CompilerOutput<?> output, String mlog) {
        MlogWatcherClient client = null;
        try {
            URI uri = new URI("ws://localhost:" + port);
            client = new MlogWatcherClient(port, output);
            client.connectBlocking(timeout, TimeUnit.MILLISECONDS);
            client.send(mlog);
            output.addMessage(ToolMessage.info("\nCompiled mlog code was sent to Mlog Watcher."));
            client.waitForMessage(timeout);
            client.close();
        } catch (Exception ex) {
            if (client == null || !client.errorReported) {
                printError(ex, port, output);
            }
        }
    }

    private static void printError(Exception ex, int port, CompilerOutput<?> output) {
        output.addMessage(ToolMessage.error("Error connecting to Mlog Watcher: %s", ex.getMessage()));
        output.addMessage(ToolMessage.error("  - make sure Mindustry with active Mlog Watcher mod is running"));
        output.addMessage(ToolMessage.error("  - verify Mlog Watcher listens on port %d", port));
    }
}
