package io.github.cardillan.mindcode.toolapp.updates;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.cardillan.mc.Version;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public record Release(
        @JsonProperty(value = "version", required = true)
        @JsonDeserialize(using = VersionDeserializer.class)
        Version version,

        @JsonProperty(value = "importance", required = true)
        ReleaseImportance importance,

        @JsonProperty("features")
        List<String> features
) {
    public Release(Version version, ReleaseImportance importance, @Nullable List<String> features) {
        if (version == null) {
            throw new IllegalArgumentException("Release version must not be blank");
        }
        if (importance == null) {
            throw new IllegalArgumentException("Release importance must not be null");
        }

        this.version = version;
        this.importance = importance;
        this.features = features == null ? List.of() : List.copyOf(features);
    }
}
