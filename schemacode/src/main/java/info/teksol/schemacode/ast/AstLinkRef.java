package info.teksol.schemacode.ast;

import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.mindustry.ProcessorConfiguration.Link;
import info.teksol.schemacode.schema.SchematicsBuilder;

import java.util.function.Consumer;

public record AstLinkRef(String reference, String name, boolean virtual) implements AstLink {

    @Override
    public void getProcessorLinks(Consumer<Link> linkConsumer, SchematicsBuilder builder, Position processorPosition) {
        linkConsumer.accept(new Link(stripPrefix(trueLinkName()), builder.getBlockPosition(reference).position()));
    }

    private String trueLinkName() {
        return name == null ? reference : name;
    }
}
