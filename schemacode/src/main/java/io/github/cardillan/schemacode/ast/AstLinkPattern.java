package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.schemacode.mindustry.ProcessorConfiguration.Link;
import io.github.cardillan.schemacode.schematics.SchematicElement;
import io.github.cardillan.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public record AstLinkPattern(SourcePosition sourcePosition, AstLabel pattern) implements AstLink {

    public AstLinkPattern(AstLabel pattern) {
        this(SourcePosition.EMPTY, pattern);
    }

    @Override
    public void getProcessorLinks(Consumer<Link> linkConsumer, SchematicsBuilder.ResolverContext context, SchematicElement element) {
        element.resolveReferences((e, label) -> linkConsumer.accept(new Link(stripPrefix(label), e.position())),
                context, pattern, true);
    }

    @Override
    public AstLinkPattern withEmptyPosition() {
        return new AstLinkPattern(SourcePosition.EMPTY, erasePosition(pattern));
    }
}
