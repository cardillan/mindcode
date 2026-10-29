package info.teksol.mindcode.cmdline.updates;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public static Releases read(Path path) throws IOException {
        try (InputStream input = Files.newInputStream(path)) {
            return MAPPER.readValue(input, Releases.class);
        }
    }

    public static Releases read(URI uri) throws IOException {
        try (InputStream input = uri.toURL().openStream()) {
            return MAPPER.readValue(input, Releases.class);
        }
    }
}
