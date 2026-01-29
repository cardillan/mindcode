package info.teksol.mindcode.cmdline.mlogwatcher;

public interface MlogWatcherClient {
    boolean connect();
    void close();

    void updateSelectedProcessor(String mlog);
    void updateAllProcessorsOnMap(String mlog, String programId, String versionSelection);
    void updateSchematic(String schematic, boolean overwrite);
}
