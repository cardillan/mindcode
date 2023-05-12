package info.teksol.schemacode.ast;

import info.teksol.schemacode.config.ProcessorConfiguration.Link;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schema.SchematicsBuilder;

import java.util.function.Consumer;

public record AstLinkPos(AstConnection connection, String name, boolean virtual) implements AstLink {

    @Override
    public void getProcessorLinks(Consumer<Link> linkConsumer, SchematicsBuilder builder, Position processorPosition) {
        linkConsumer.accept(new Link(name, connection.evaluate(builder, processorPosition)));
    }
}
