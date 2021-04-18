package info.teksol.mindcode.webapp;

import java.time.Instant;
import java.util.UUID;

public class Script {
    private final UUID id;
    private final String name;
    private final Script forkedFrom;
    private final String author;
    private final boolean published;
    private final Instant recordedAt;

    Script(String name, Script forkedFrom, String author, boolean published, Instant recordedAt) {
        this(null, name, forkedFrom, author, published, recordedAt);
    }

    Script(UUID id, String name, Script forkedFrom, String author, boolean published, Instant recordedAt) {
        this.id = id;
        this.name = name;
        this.forkedFrom = forkedFrom;
        this.author = author;
        this.published = published;
        this.recordedAt = recordedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Script getForkedFrom() {
        return forkedFrom;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isPublished() {
        return published;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }
}
