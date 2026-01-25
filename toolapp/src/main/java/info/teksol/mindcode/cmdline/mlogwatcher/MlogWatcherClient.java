package info.teksol.mindcode.cmdline.mlogwatcher;

public interface MlogWatcherClient {
    boolean connect();
    void close();

    void updateSelectedProcessor(String mlog);
    void updateSchematic(String schematic);
}
