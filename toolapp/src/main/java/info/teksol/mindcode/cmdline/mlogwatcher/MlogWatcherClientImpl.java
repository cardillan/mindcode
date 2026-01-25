package info.teksol.mindcode.cmdline.mlogwatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

import static info.teksol.mindcode.cmdline.mlogwatcher.Response.*;

@NullMarked
public class MlogWatcherClientImpl extends MlogWatcherClientBase {
    protected final ObjectMapper mapper = new ObjectMapper();

    public MlogWatcherClientImpl(ToolMessageEmitter messageEmitter, int port, long timeout) {
        super(messageEmitter, port, timeout, "/v1");
    }

    @Override
    protected void onTimeout() {
        log.info("  No response received from Mlog Watcher - maybe a legacy version is installed?");
    }

    protected void processRequest(Request request, String userMessage, Consumer<Response> successHandler, Consumer<Response> errorHandler) {
        try {
            send(mapper.writeValueAsString(request));
            log.info("%n%s", userMessage);

            String content = waitForResponse();
            if (content == null) {
                // The timeout has already been reported
                return;
            }

            Response response = mapper.readValue(content, Response.class);
            switch (response.getStatus()) {
                case Response.STATUS_SUCCESS -> successHandler.accept(response);
                case Response.STATUS_ERROR -> errorHandler.accept(response);
                default -> log.info("  Mlog Watcher: unknown response status '%s'.", response.getStatus());
            }
        } catch (Exception ex) {
            printError(ex);
        }
    }

    protected void processRequest(Request request, String userMessage, Consumer<Response> successHandler) {
        processRequest(request, userMessage, successHandler, response -> {
            Results result = response.getResult();
            if (result instanceof TextResult textResult) {
                switch (textResult.getText()) {
                    case ERR_INVALID_ARGUMENTS -> log.error("  Mlog Watcher: invalid arguments provided.");
                    case ERR_INVALID_PROGRAM_ID -> log.error("  Mlog Watcher: invalid program ID specified.");
                    case ERR_NO_PROCESSOR_ATTACHED -> {
                        log.info("  Mlog Watcher: no processor selected.");
                        log.info("  (The target processor must be selected in Mindustry to receive the code.)");
                    }
                    case ERR_NO_ACTIVE_MAP -> log.info("  Mlog Watcher: no map loaded.");
                    case ERR_NO_PROCESSORS_FOUND ->
                            log.info("  Mlog Watcher: no compatible processors found on the map.");
                    case ERR_SCHEMATIC_IMPORT_FAILED -> log.error("  Mlog Watcher: schematic import failed.");
                    case ERR_UNKNOWN_METHOD ->
                            log.error("  Mlog Watcher: requested method not supported (MlogWatcher version too old?).");
                    default -> log.error("  Mlog Watcher: error processing request: %s", textResult.getText());
                }
            } else {
                log.error("  Mlog Watcher: error processing request - unknown result");
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
                _ -> log.info("  Mlog Watcher: mlog code injected into selected processor."));
    }

    @Override
    public void updateAllProcessorsOnMap(String mlog, String programId) {
        UpgradeAllProcessorsOnMapParams params = new UpgradeAllProcessorsOnMapParams();
        params.setCode(mlog);
        params.setProgramId(programId);

        Request request = new Request();
        request.setMethod(Request.UPGRADE_ALL_PROCESSORS_ON_MAP);
        request.setMethodVersion(1);
        request.setInvocationId(0);
        request.setParams(params);

        processRequest(request,
                "Request to upgrade all processors on the map was sent to Mlog Watcher.",
                result -> {
                    if (result.getResult() instanceof TextResult textResult) {
                        log.info("  Mlog Watcher: %s", textResult.getText().toLowerCase());
                    } else {
                        log.error("  Mlog Watcher: error processing request - unknown result");
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
                _ -> log.info("  Mlog Watcher: schematics added/updated."));
    }
}
