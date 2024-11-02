package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.mindustry.ProcessorConfiguration.Link;
import info.teksol.schemacode.schematics.SchematicsBuilder;

import java.util.function.Consumer;

public record AstLinkPos(InputPosition inputPosition, AstConnection connection, String name, boolean virtual) implements AstLink {

    @Override
    public void getProcessorLinks(Consumer<Link> linkConsumer, SchematicsBuilder builder, Position processorPosition) {
        linkConsumer.accept(new Link(stripPrefix(trueLinkName()), connection.evaluate(builder, processorPosition)));
    }

    private String trueLinkName() {
        return name == null ? connection().id() : name;
    }
}
