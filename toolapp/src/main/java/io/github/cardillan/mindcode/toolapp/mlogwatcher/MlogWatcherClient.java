package io.github.cardillan.mindcode.toolapp.mlogwatcher;

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
    @Nullable String extractSelectedSchematic();
}
