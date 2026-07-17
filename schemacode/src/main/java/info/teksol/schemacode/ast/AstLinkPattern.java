package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.schemacode.mindustry.ProcessorConfiguration.Link;
import info.teksol.schemacode.schematics.SchematicElement;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public record AstLinkPattern(SourcePosition sourcePosition, AstLabel pattern) implements AstLink {

    public AstLinkPattern(AstLabel pattern) {
        this(SourcePosition.EMPTY, pattern);
    }

    @Override
    public void getProcessorLinks(Consumer<Link> linkConsumer, SchematicsBuilder.ResolverContext context, SchematicElement element) {
        SchematicElement region = element.parent();
        if (region == null) throw new MindcodeInternalError("No region to resolve link pattern in");
        region.resolvePattern((e, label) -> linkConsumer.accept(new Link(stripPrefix(label), e.position())),
                context, pattern, true, 0, false);
    }

    @Override
    public AstLinkPattern withEmptyPosition() {
        return new AstLinkPattern(SourcePosition.EMPTY, erasePosition(pattern));
    }
}
