package info.teksol.mindcode.webapp;

import info.teksol.mc.Version;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.GeneratedValue;
import java.time.Instant;
import java.util.UUID;

@Table("errors")
@Immutable
public class Error {
    @Id
    @GeneratedValue
    private final UUID id;
    private final String version;
    private final String type;
    private final String source;
    @CreatedDate
    private final Instant createdAt;

    public Error(String type, String source) {
        this.id = null;
        this.version = Version.getVersion();
        this.type = type;
        this.source = source;
        this.createdAt = Instant.now();
    }

    public Error(UUID id, String version, String type, String source, Instant createdAt) {
        this.id = id;
        this.version = version;
        this.type = type;
        this.source = source;
        this.createdAt = createdAt;
    }

    @PersistenceCreator
    public static Error create(UUID id, String version, String type, String source, Instant createdAt) {
        return new Error(id, version, type, source, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Error{" +
                "id=" + id +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                ", source='" + source + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
