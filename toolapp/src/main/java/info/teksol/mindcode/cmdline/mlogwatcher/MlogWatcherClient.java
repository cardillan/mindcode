package info.teksol.mindcode.cmdline.mlogwatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jspecify.annotations.NullMarked;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@NullMarked
public class MlogWatcherClient extends WebSocketClient {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ToolMessageEmitter messageEmitter;
    private final int port;
    private final Semaphore semaphore = new Semaphore(0);
    boolean errorReported;

    public MlogWatcherClient(int port, ToolMessageEmitter messageEmitter) throws URISyntaxException {
        super(new URI("ws://localhost:" + port + "/v1"));
        this.port = port;
        this.messageEmitter = messageEmitter;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
    }

    @Override
    public void onMessage(String message) {
        try {
            Response response = mapper.readValue(message, Response.class);

            switch (response.getStatus()) {
                case Response.STATUS_SUCCESS -> {
                    switch (response.getResult()) {
                        case "processor updated" -> messageEmitter.info("  Mlog Watcher: mlog code injected into selected processor.");
                        case "schematic updated" -> messageEmitter.info("  Mlog Watcher: schematics added/updated.");
                        default -> messageEmitter.info("  Mlog Watcher: unknown response '%s'.", response.getResult());
                    }
                }

                case Response.STATUS_ERROR -> {
                    if ("no processor attached".equals(response.getResult())){
                        messageEmitter.info("  Mlog Watcher: no processor selected.");
                        messageEmitter.info("  (The target processor must be selected in Mindustry to receive the code.)");
                    } else {
                        messageEmitter.error("  Mlog Watcher: error processing request: %s", response.getResult());
                    }
                }

                default -> messageEmitter.info("  Mlog Watcher: unknown response status '%s'.", response.getStatus());
            }
            semaphore.release(1);
        } catch (JsonProcessingException e) {
            messageEmitter.error("Failed to parse Mlog Watcher response: %s", e.getMessage());
        }
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

    public static void sendSchematic(int port, long timeout, MessageConsumer messageConsumer, String encoded) {
        PutSchematicInLibraryParams params = new PutSchematicInLibraryParams();
        params.setSchematic(encoded);
        params.setOverwrite(true);

        Request request = new Request();
        request.setMethod("put_schematic_in_library");
        request.setMethodVersion(1);
        request.setInvocationId(0);
        request.setParams(params);

        sendData(port, timeout, messageConsumer, request, "Created schematic was sent to Mlog Watcher.");
    }

    public static void sendMlog(int port, long timeout, MessageConsumer messageConsumer, String mlog) {
        UpdateSelectedProcessorParams params = new UpdateSelectedProcessorParams();
        byte[] bytes = mlog.getBytes(StandardCharsets.UTF_8);
        params.setCode(Base64.getEncoder().encodeToString(bytes));

        Request request = new Request();
        request.setMethod("update_selected_processor");
        request.setMethodVersion(1);
        request.setInvocationId(0);
        request.setParams(params);

        sendData(port, timeout, messageConsumer, request, "Compiled mlog code was sent to Mlog Watcher.");
    }

    public static void sendData(int port, long timeout, MessageConsumer messageConsumer, Request request, String message) {
        ToolMessageEmitter messageEmitter = new ToolMessageEmitter(messageConsumer);
        MlogWatcherClient client = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String text = mapper.writeValueAsString(request);

            client = new MlogWatcherClient(port, messageEmitter);
            client.connectBlocking(timeout, TimeUnit.MILLISECONDS);
            client.send(text);
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
