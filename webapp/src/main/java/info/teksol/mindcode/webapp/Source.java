package info.teksol.mindcode.webapp;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.PersistenceConstructor;
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
    private final String source;
    @CreatedDate
    private final Instant createdAt;

    public Source(String source, Instant createdAt) {
        this.id = null;
        this.source = source;
        this.createdAt = createdAt;
    }

    @PersistenceConstructor
    public Source(UUID id, String source, Instant createdAt) {
        this.id = id;
        this.source = source;
        this.createdAt = createdAt;
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

    public Source withId(UUID newId) {
        return new Source(newId, source, createdAt);
    }

    public Source withSource(String newSource) {
        return new Source(id, newSource, createdAt);
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
