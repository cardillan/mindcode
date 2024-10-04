package info.teksol.mindcode.cmdline;

import info.teksol.mindcode.compiler.CompilerOutput;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class MlogWatcherClient extends WebSocketClient {
    boolean errorReported;
    private final int port;
    private final CompilerOutput<?> output;

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
        System.out.println("Received message: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onError(Exception ex) {
        printError(ex, port, output);
        errorReported = true;
    }

    public static void sendMlog(int port, long timeout, CompilerOutput<?> output, String mlog) {
        MlogWatcherClient client = null;
        try {
            URI uri = new URI("ws://localhost:" + port);
            client = new MlogWatcherClient(port, output);
            client.connectBlocking(timeout, TimeUnit.MILLISECONDS);
            client.send(mlog);
            client.close();
            output.addMessage(ToolMessage.info("\nCompiled mlog code was sent to Mlog Watcher."));
            output.addMessage(ToolMessage.info("(Remember the target processor must be selected in Mindustry to receive the code.)"));
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
