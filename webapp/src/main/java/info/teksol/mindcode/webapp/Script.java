package info.teksol.mindcode.webapp;

import java.time.Instant;
import java.util.UUID;

public class Script {
    private final UUID id;
    private final String name;
    private final Script forkedFrom;
    private final String author;
    private final Instant recordedAt;

    Script(String name, Script forkedFrom, String author, Instant recordedAt) {
        this(null, name, forkedFrom, author, recordedAt);
    }

    Script(UUID id, String name, Script forkedFrom, String author, Instant recordedAt) {
        this.id = id;
        this.name = name;
        this.forkedFrom = forkedFrom;
        this.author = author;
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

    public Instant getRecordedAt() {
        return recordedAt;
    }
}
