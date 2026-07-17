package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.mindustry.ProcessorConfiguration.Link;
import info.teksol.schemacode.schematics.SchematicElement;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@NullMarked
public record AstLinkPos(SourcePosition sourcePosition, AstConnection connection, @Nullable String name, boolean virtual) implements AstLink {

    public AstLinkPos(AstConnection connection, @Nullable String name, boolean virtual) {
        this(SourcePosition.EMPTY, connection, name, virtual);
    }

    @Override
    public void getProcessorLinks(Consumer<Link> linkConsumer, SchematicsBuilder.ResolverContext context, SchematicElement element) {
        connection.evaluateMultiple(
                position -> linkConsumer.accept(new Link(stripPrefix(trueLinkName(context)), position)),
                context, element);
    }

    private String trueLinkName(SchematicsBuilder.ResolverContext context) {
        if (name == null) {
            if (connection().id() == null) {
                context.error(this, "A link name was not specified.");
                return "link1";
            }
            return connection().id().segments().getLast().name();
        } else {
            return name;
        }
    }

    @Override
    public AstLinkPos withEmptyPosition() {
        return new AstLinkPos(SourcePosition.EMPTY, erasePosition(connection), name, virtual);
    }
}
