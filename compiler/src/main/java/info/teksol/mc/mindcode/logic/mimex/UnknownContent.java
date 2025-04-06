package info.teksol.mc.mindcode.logic.mimex;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UnknownContent(String name) implements MindustryContent {

    public UnknownContent(String name) {
        this.name = name;
        if (!name.startsWith("@")) {
            throw new MindcodeInternalError(String.format("No '@' at the beginning of content '%s'", name));
        }
    }

    @Override
    public ContentType contentType() {
        return ContentType.UNKNOWN;
    }

    @Override
    public String contentName() {
        return name.substring(1);
    }
}
