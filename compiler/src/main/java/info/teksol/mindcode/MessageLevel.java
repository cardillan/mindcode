package info.teksol.mindcode;

public enum MessageLevel {
    ERROR,
    WARNING,
    INFO,
    DEBUG;

    private final String title;

    MessageLevel() {
        title = name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }

    public String getTitle() {
        return title;
    }
}
