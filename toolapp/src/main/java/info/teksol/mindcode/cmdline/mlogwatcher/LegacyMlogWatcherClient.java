package info.teksol.mindcode.cmdline.mlogwatcher;

import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LegacyMlogWatcherClient extends MlogWatcherClientBase implements MlogWatcherClient {

    public LegacyMlogWatcherClient(ToolMessageEmitter messageEmitter, int port, long timeout) {
        super(messageEmitter, port, timeout, "");
    }

    protected void onTimeout() {
        log.info("  No response from Mlog Watcher - maybe an old version is installed?");
    }

    @Override
    public void updateSelectedProcessor(String mlog) {
        send(mlog);
        log.info("%n%s", "Compiled mlog code was sent to Mlog Watcher.");

        String response = waitForResponse();
        if (response == null) return;

        switch (response) {
            case "ok" -> log.info("  Mlog Watcher: mlog code injected into selected processor.");
            case "no_processor" -> {
                log.info("  Mlog Watcher: no processor selected.");
                log.info("  (The target processor must be selected in Mindustry to receive the code.)");
            }
            default -> log.info("  Mlog Watcher: unknown response '%s'.", response);
        }
    }

    @Override
    public void updateAllProcessorsOnMap(String mlog, String programId) {
        log.error("Updating all processors on the map is not supported by the legacy Mlog Watcher.");
    }

    @Override
    public void updateSchematic(String schematic) {
        log.error("Updating schematics is not supported by the legacy Mlog Watcher.");
    }
}
