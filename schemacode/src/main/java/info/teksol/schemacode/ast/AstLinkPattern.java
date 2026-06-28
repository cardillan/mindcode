package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.mindustry.ProcessorConfiguration.Link;
import info.teksol.schemacode.schematics.SchematicElement;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;
import java.util.regex.Pattern;

@NullMarked
public record AstLinkPattern(SourcePosition sourcePosition, AstLabel prefix, String pattern) implements AstLink {

    @Override
    public void getProcessorLinks(Consumer<Link> linkConsumer, SchematicsBuilder.ResolverContext context, SchematicElement element) {
        SchematicElement region = prefix.segments().isEmpty() ? element.parent() : element.resolveReference(context, prefix, false);
        if (region == null || !region.isRegion()) {
            context.error(this, "Label prefix '%s' does not denote a valid region.", prefix.fullName());
        } else {
            Pattern pattern = Pattern.compile(this.pattern.replace ("*", ".*"));
            region.getLabelMap().entrySet().stream()
                    .filter(e -> pattern.matcher(e.getKey()).matches())
                    .map(e -> new Link(stripPrefix(e.getKey()), e.getValue().position()))
                    .forEachOrdered(linkConsumer);
        }
    }

    @Override
    public AstLinkPattern withEmptyPosition() {
        return new AstLinkPattern(SourcePosition.EMPTY, erasePosition(prefix), pattern);
    }
}
