package io.github.cardillan.mindcode.cmdline.updates;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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

    public static Releases read(InputStream input) throws IOException {
        return MAPPER.readValue(input, Releases.class);
    }
}
