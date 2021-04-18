package info.teksol.mindcode.webapp;

import java.time.Instant;

public class ScriptVersion {
    private final long id;
    private final int version;
    private final String name;
    private final String source;
    private final String slug;
    private final Instant committedAt;

    public ScriptVersion(long id, int version, String name, String source, String slug, Instant committedAt) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.source = source;
        this.slug = slug;
        this.committedAt = committedAt;
    }

    public long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public String getSlug() {
        return slug;
    }

    public Instant getCommittedAt() {
        return committedAt;
    }
}
