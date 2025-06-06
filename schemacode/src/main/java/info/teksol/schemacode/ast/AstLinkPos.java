package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.mindustry.ProcessorConfiguration.Link;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@NullMarked
public record AstLinkPos(SourcePosition sourcePosition, AstConnection connection, @Nullable String name, boolean virtual) implements AstLink {

    @Override
    public void getProcessorLinks(Consumer<Link> linkConsumer, SchematicsBuilder builder, Position processorPosition) {
        linkConsumer.accept(new Link(stripPrefix(trueLinkName(builder)), connection.evaluate(builder, processorPosition)));
    }

    private String trueLinkName(SchematicsBuilder builder) {
        if (name == null) {
            if (connection().id() == null) {
                builder.error(sourcePosition, "A link name was not specified.");
                return "link1";
            }
            return connection().id();
        } else {
            return name;
        }
    }

    @Override
    public AstLinkPos withEmptyPosition() {
        return new AstLinkPos(SourcePosition.EMPTY, erasePosition(connection), name, virtual);
    }
}
