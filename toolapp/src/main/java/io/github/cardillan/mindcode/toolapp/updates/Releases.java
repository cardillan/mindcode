package io.github.cardillan.mindcode.toolapp.updates;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@NullMarked
public record Releases(List<Release> releases) {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public Releases {
        if (releases == null) {
            throw new IllegalArgumentException("Releases must not be null");
        }

        releases = List.copyOf(releases);
    }

    public @Nullable Release latest() {
        return releases.stream().max(Comparator.comparing(Release::version)).orElse(null);
    }

    public Stream<Release> stream() {
        return releases.stream();
    }

    public static Releases read(Path path) throws IOException {
        try (InputStream input = Files.newInputStream(path)) {
            return MAPPER.readValue(input, Releases.class);
        }
    }
}
