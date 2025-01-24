package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.mindustry.ProcessorConfiguration.Link;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;
import java.util.regex.Pattern;

@NullMarked
public record AstLinkPattern(SourcePosition sourcePosition, String match) implements AstLink {

    @Override
    public void getProcessorLinks(Consumer<Link> linkConsumer, SchematicsBuilder builder, Position processorPosition) {
        Pattern pattern = Pattern.compile(match.replace("*", ".*"));
        builder.getAstLabelMap().entrySet().stream()
                .filter(e -> !e.getKey().startsWith("#"))
                .filter(e -> pattern.matcher(e.getKey()).matches())
                .map(e -> new Link(stripPrefix(e.getKey()), e.getValue().position()))
                .forEachOrdered(linkConsumer);
    }

    @Override
    public AstLinkPattern withEmptyPosition() {
        return new AstLinkPattern(SourcePosition.EMPTY, match);
    }
}
