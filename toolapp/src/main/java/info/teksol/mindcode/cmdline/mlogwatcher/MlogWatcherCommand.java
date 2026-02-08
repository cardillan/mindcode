package info.teksol.mindcode.cmdline.mlogwatcher;

import info.teksol.mindcode.cmdline.ToolAppAction;

import java.util.Set;

import static info.teksol.mindcode.cmdline.ToolAppAction.*;

public enum MlogWatcherCommand {
    UPDATE              (COMPILE_MINDCODE, PROCESS_MLOG, COMPILE_SCHEMA, PROCESS_SCHEMA),

    UPDATE_ALL          (COMPILE_MINDCODE, PROCESS_MLOG),
    UPGRADE_ALL         (COMPILE_MINDCODE, PROCESS_MLOG),
    FORCE_UPDATE_ALL    (COMPILE_MINDCODE, PROCESS_MLOG),

    EXTRACT             (PROCESS_MLOG, PROCESS_SCHEMA),

    ADD                 (COMPILE_SCHEMA, PROCESS_SCHEMA),
    ;

    private final Set<ToolAppAction> supportedActions;

    MlogWatcherCommand(ToolAppAction... supportedActions) {
        this.supportedActions = Set.of(supportedActions);
    }

    public boolean supports(ToolAppAction action) {
        return supportedActions.contains(action);
    }

    public Set<ToolAppAction> getSupportedActions() {
        return supportedActions;
    }
}
