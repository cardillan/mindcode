package io.github.cardillan.schemacode.ast;

import io.github.cardillan.schemacode.mindustry.ProcessorConfiguration.Link;
import io.github.cardillan.schemacode.schematics.SchematicElement;
import io.github.cardillan.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public interface AstLink extends AstSchemaItem {

    void getProcessorLinks(Consumer<Link> linkConsumer, SchematicsBuilder.ResolverContext context, SchematicElement element);

    default String stripPrefix(String linkName) {
        int pos = linkName.indexOf("-");
        return pos < 0 ? linkName : linkName.substring(pos + 1);
    }
}
