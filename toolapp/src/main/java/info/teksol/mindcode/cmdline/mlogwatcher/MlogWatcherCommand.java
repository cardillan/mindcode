package info.teksol.mindcode.cmdline.mlogwatcher;

import info.teksol.mindcode.cmdline.ToolAppAction;

import java.util.Set;

import static info.teksol.mindcode.cmdline.ToolAppAction.COMPILE_MINDCODE;
import static info.teksol.mindcode.cmdline.ToolAppAction.COMPILE_SCHEMA;

public enum MlogWatcherCommand {
    UPDATE      (COMPILE_MINDCODE, COMPILE_SCHEMA),
    UPDATE_ALL  (COMPILE_MINDCODE),
    ADD         (COMPILE_SCHEMA),
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
