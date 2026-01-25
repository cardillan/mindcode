package info.teksol.mindcode.cmdline.mlogwatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class MlogWatcherClientImpl extends MlogWatcherClientBase {
    protected final ObjectMapper mapper = new ObjectMapper();

    public MlogWatcherClientImpl(ToolMessageEmitter messageEmitter, int port, long timeout) {
        super(messageEmitter, port, timeout, "/v1");
    }

    @Override
    protected void onTimeout() {
        messageEmitter.info("  No response received from Mlog Watcher - maybe a legacy version is installed?");
    }

    protected void processRequest(Request request, String userMessage, Consumer<Response> successHandler,
            Consumer<Response> errorHandler) {
        try {
            send(mapper.writeValueAsString(request));
            messageEmitter.info("%n%s", userMessage);

            String content = waitForResponse();
            if (content == null) {
                // The timeout has already been reported
                return;
            }

            Response response = mapper.readValue(content, Response.class);
            switch (response.getStatus()) {
                case Response.STATUS_SUCCESS -> successHandler.accept(response);
                case Response.STATUS_ERROR -> errorHandler.accept(response);
                default -> messageEmitter.info("  Mlog Watcher: unknown response status '%s'.", response.getStatus());
            }
        } catch (Exception ex) {
            printError(ex);
        }
    }

    protected void processRequest(Request request, String userMessage, Consumer<Response> successHandler) {
        processRequest(request, userMessage, successHandler, response -> {
            Results result = response.getResult();
            if (result instanceof TextResult textResult) {
                messageEmitter.error("  Mlog Watcher: error processing request: %s", textResult.getText());
            } else {
                messageEmitter.error("  Mlog Watcher: error processing request - unknown result");
            }
        });
    }

    @Override
    public void updateSelectedProcessor(String mlog) {
        UpdateSelectedProcessorParams params = new UpdateSelectedProcessorParams();
        params.setCode(mlog);

        Request request = new Request();
        request.setMethod(Request.UPDATE_SELECTED_PROCESSOR);
        request.setMethodVersion(1);
        request.setInvocationId(0);
        request.setParams(params);

        processRequest(request,
                "Compiled mlog code was sent to Mlog Watcher.",
                _ -> messageEmitter.info("  Mlog Watcher: mlog code injected into selected processor."),
                response -> {
                    if (response.getResult() instanceof TextResult textResult) {
                        if (Response.ERR_NO_PROCESSOR_ATTACHED.equals(textResult.getText())) {
                            messageEmitter.info("  Mlog Watcher: no processor selected.");
                            messageEmitter.info("  (The target processor must be selected in Mindustry to receive the code.)");
                        } else {
                            messageEmitter.error("  Mlog Watcher: error processing request: %s", response.getResult());
                        }
                    } else {
                        messageEmitter.error("  Mlog Watcher: unknown error processing request");
                    }
                });
    }

    @Override
    public void updateSchematic(String schematic) {
        PutSchematicInLibraryParams params = new PutSchematicInLibraryParams();
        params.setSchematic(schematic);
        params.setOverwrite(true);

        Request request = new Request();
        request.setMethod(Request.PUT_SCHEMATIC_IN_LIBRARY);
        request.setMethodVersion(1);
        request.setInvocationId(0);
        request.setParams(params);

        processRequest(request,
                "Created schematic was sent to Mlog Watcher.",
                _ -> messageEmitter.info("  Mlog Watcher: schematics added/updated."));
    }
}
