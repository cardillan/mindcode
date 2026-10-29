package info.teksol.mindcode.cmdline.updates;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public record Release(
        @JsonProperty(value = "version", required = true)
        String version,

        @JsonProperty(value = "importance", required = true)
        ReleaseImportance importance,

        @JsonProperty("features")
        List<String> features
) {
    public Release(String version, ReleaseImportance importance, @Nullable List<String> features) {
        if (version == null || version.isBlank()) {
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
