package info.teksol.mindcode.cmdline.mlogwatcher;

import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LegacyMlogWatcherClient extends MlogWatcherClientBase implements MlogWatcherClient {

    public LegacyMlogWatcherClient(ToolMessageEmitter messageEmitter, int port, long timeout) {
        super(messageEmitter, port, timeout, "");
    }

    protected void onTimeout() {
        messageEmitter.info("  No response from Mlog Watcher - maybe an old version is installed?");
    }

    @Override
    public void updateSelectedProcessor(String mlog) {
        send(mlog);
        messageEmitter.info("%n%s", "Compiled mlog code was sent to Mlog Watcher.");

        String response = waitForResponse();
        if (response == null) return;

        switch (response) {
            case "ok" -> messageEmitter.info("  Mlog Watcher: mlog code injected into selected processor.");
            case "no_processor" -> {
                messageEmitter.info("  Mlog Watcher: no processor selected.");
                messageEmitter.info("  (The target processor must be selected in Mindustry to receive the code.)");
            }
            default -> messageEmitter.info("  Mlog Watcher: unknown response '%s'.", response);
        }
    }

    @Override
    public void updateSchematic(String schematic) {
        messageEmitter.error("Updating schematics is not supported by the legacy Mlog Watcher.");
    }
}
