package info.teksol.mindcode.cmdline.mlogwatcher;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface MlogWatcherClient {
    boolean connect();
    void close();

    void updateSelectedProcessor(String mlog);
    void updateProcessorsOnMap(String mlog, String programId, String versionSelection);
    void updateSchematic(String schematic, boolean overwrite);

    @Nullable String extractSelectedProcessorCode();
}
