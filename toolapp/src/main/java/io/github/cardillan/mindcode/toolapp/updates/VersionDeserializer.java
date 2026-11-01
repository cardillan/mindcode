package io.github.cardillan.mindcode.toolapp.updates;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.github.cardillan.mc.Version;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;

@NullMarked
public class VersionDeserializer extends JsonDeserializer<Version> {

    @Override
    public Version deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return Version.parse(parser.getValueAsString());
    }
}
