package info.teksol.mindcode.cmdline.mlogwatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mindcode.cmdline.mlogwatcher.api.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;

import static info.teksol.mindcode.cmdline.mlogwatcher.api.Response.*;

@NullMarked
public class MlogWatcherClientImpl extends MlogWatcherClientBase {
    protected final ObjectMapper mapper = new ObjectMapper();

    public MlogWatcherClientImpl(ToolMessageEmitter messageEmitter, int port, long timeout, boolean printStackTrace) {
        super(messageEmitter, port, timeout, "/v1", printStackTrace);
    }

    @Override
    protected void onTimeout() {
        log.info("  No response received from Mlog Watcher - maybe a legacy version is installed?");
    }

    protected <T> @Nullable T processRequest(Request request, String userMessage, Function<Response, T> successHandler,
            Consumer<Response> errorHandler) {
        try {
            send(mapper.writeValueAsString(request));
            log.info("%n%s", userMessage);

            String content = waitForResponse();
            if (content == null) {
                // The timeout has already been reported
                return null;
            }

            Response response = mapper.readValue(content, Response.class);
            return switch (response.getStatus()) {
                case Response.STATUS_SUCCESS -> successHandler.apply(response);
                case Response.STATUS_ERROR -> {
                    errorHandler.accept(response);
                    yield null;
                }
                default -> {
                    log.info("  Mlog Watcher: unknown response status '%s'.", response.getStatus());
                    yield null;
                }
            };
        } catch (Exception ex) {
            printError(ex);
            return null;
        }
    }

    protected <T> @Nullable T processRequest(Request request, String userMessage, Function<Response, T> successHandler) {
        return processRequest(request, userMessage, successHandler, response -> {
            Results result = response.getResult();
            if (result instanceof TextResult textResult) {
                switch (textResult.getText()) {
                    case ERR_INVALID_ARGUMENTS ->
                            log.error("  Mlog Watcher: Internal error: invalid arguments provided.");
                    case ERR_INVALID_PROGRAM_ID ->
                            log.error("  Mlog Watcher: Internal error: invalid program ID specified.");
                    case ERR_INVALID_VERSION_SELECTION ->
                            log.error("  Mlog Watcher: Internal error: invalid version selection specified.");
                    case ERR_NO_PROCESSOR_ATTACHED -> {
                        log.info("  Mlog Watcher: no processor selected.");
                        log.info("  (The target processor must be selected in Mindustry to receive the code.)");
                    }
                    case ERR_NO_ACTIVE_MAP -> log.info("  Mlog Watcher: no map loaded.");
                    case ERR_NO_PROCESSORS_FOUND ->
                            log.info("  Mlog Watcher: no compatible processors found on the map.");
                    case ERR_SCHEMATIC_IMPORT_FAILED ->
                            log.error("  Mlog Watcher: schematic import failed (invalid schematic file?)");
                    case ERR_UNKNOWN_METHOD ->
                            log.error("  Mlog Watcher: requested method not supported (MlogWatcher version too old?).");
                    default -> log.error("  Mlog Watcher: error processing request: %s", textResult.getText());
                }
            } else {
                log.error("  Mlog Watcher: error processing request - unknown result.");
            }
        });
    }

    protected void processVoidRequest(Request request, String userMessage, Consumer<Response> successHandler) {
        processRequest(request, userMessage, asNull(successHandler));
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

        processVoidRequest(request,
                "Compiled mlog code was sent to Mlog Watcher.",
                _ -> log.info("  Mlog Watcher: mlog code injected into selected processor."));
    }

    @Override
    public void updateProcessorsOnMap(String mlog, String textId, String versionSelection) {
        ProgramId programId = ProgramId.parse(textId);

        UpdateProcessorsOnMapParams params = new UpdateProcessorsOnMapParams();
        params.setCode(mlog);
        params.setProgramId(programId);
        params.setVariableName(LogicVariable.PROGRAM_ID_NAME);
        params.setVersionSelection(versionSelection);

        Request request = new Request();
        request.setMethod(Request.UPDATE_PROCESSORS_ON_MAP);
        request.setMethodVersion(1);
        request.setInvocationId(0);
        request.setParams(params);

        String verb = switch (versionSelection) {
            case UpdateProcessorsOnMapParams.VERSION_SELECTION_EXACT -> "update";
            case UpdateProcessorsOnMapParams.VERSION_SELECTION_COMPATIBLE -> "upgrade";
            case UpdateProcessorsOnMapParams.VERSION_SELECTION_ANY -> "force update";
            default -> throw new IllegalArgumentException("Invalid version selection: " + versionSelection);
        };

        processVoidRequest(request,
                "Request to " + verb + " all processors on the map was sent to Mlog Watcher.",
                result -> {
                    if (result.getResult() instanceof ProcessorUpdateResults updates) {
                        log.info("  Mlog Watcher: %d processors considered:", updates.getProcessorUpdates().size());
                        updates.getProcessorUpdates().stream()
                                .sorted(Comparator.comparing(LogicProcessor::getX).thenComparing(LogicProcessor::getY))
                                .forEach(upd -> log.info("    %s at (%.1f, %.1f), %s: %s", upd.getType(), upd.getX(), upd.getY(),
                                        upd.getProgramId().toVersionString(), status(upd)));
                    } else {
                        log.error("  Mlog Watcher: error processing request - unknown result");
                    }
                });
    }

    private String status(LogicProcessor update) {
        return switch (update.getStatus()) {
            case LogicProcessor.UPDATED -> "updated.";
            case LogicProcessor.INCOMPATIBLE_VERSION -> "existing version incompatible.";
            case LogicProcessor.MISSING_PROGRAM_ID -> "missing or unrecognized program id.";
            case null, default -> "unknown failure";
        };
    }

    @Override
    public void updateSchematic(String schematic, boolean overwrite) {
        PutSchematicInLibraryParams params = new PutSchematicInLibraryParams();
        params.setSchematic(schematic);
        params.setOverwrite(overwrite);

        Request request = new Request();
        request.setMethod(Request.PUT_SCHEMATIC_IN_LIBRARY);
        request.setMethodVersion(1);
        request.setInvocationId(0);
        request.setParams(params);

        processVoidRequest(request,
                "Created schematic was sent to Mlog Watcher.",
                _ -> log.info("  Mlog Watcher: schematics added/updated."));
    }

    @Override
    public @Nullable String extractSelectedProcessorCode() {
        Request request = new Request();
        request.setMethod(Request.EXTRACT_SELECTED_PROCESSOR_CODE);
        request.setMethodVersion(1);
        request.setInvocationId(0);

        return processRequest(request,
                "Trying to obtain selected processor's code from Mlog Watcher.",
                response -> {
                    log.info("  Mlog Watcher: loaded mlog code from the selected processor.");
                    ProcessorExtractResults results = response.getResult();
                    return results.getCode();
                });
    }

    private static Function<Response, @Nullable Void> asNull(Consumer<Response> consumer) {
        return response -> {
            consumer.accept(response);
            return null;
        };
    }
}
