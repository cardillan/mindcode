package io.github.cardillan.mindcode.toolapp.mlogwatcher;

import io.github.cardillan.mindcode.toolapp.ToolAppAction;

import java.util.Set;

import static io.github.cardillan.mindcode.toolapp.ToolAppAction.*;

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
