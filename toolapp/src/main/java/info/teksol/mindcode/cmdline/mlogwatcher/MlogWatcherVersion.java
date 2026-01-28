package info.teksol.mindcode.cmdline.mlogwatcher;

public enum MlogWatcherVersion {
    V0("legacy"),
    V1("version 1"),
    ;

    private final String description;

    MlogWatcherVersion(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
