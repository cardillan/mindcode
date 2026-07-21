package info.teksol.mindcode.webapp;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.GeneratedValue;
import java.time.Instant;
import java.util.UUID;

@Table("sources")
@Immutable
public class Source {
    @Id
    @GeneratedValue
    private final UUID id;
    private final String type;
    private final String source;
    @CreatedDate
    private final Instant createdAt;

    public Source(String type, String source, Instant createdAt) {
        this.id = null;
        this.type = type;
        this.source = source;
        this.createdAt = createdAt;
    }

    public Source(UUID id, String type, String source, Instant createdAt) {
        this.id = id;
        this.type = type;
        this.source = source;
        this.createdAt = createdAt;
    }

    @PersistenceCreator
    public static Source create(UUID id, String type, String source, Instant createdAt) {
        return new Source(id, type, source, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Source withSource(String newSource) {
        return new Source(id, type, newSource,  createdAt);
    }

    @Override
    public String toString() {
        return "Source{" +
                "id=" + id +
                ", source='" + source + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
