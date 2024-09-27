package info.teksol.schemacode.ast;

import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.mindustry.ProcessorConfiguration.Link;
import info.teksol.schemacode.schematics.SchematicsBuilder;

import java.util.function.Consumer;

public interface AstLink extends AstSchemaItem {

    void getProcessorLinks(Consumer<Link> linkConsumer, SchematicsBuilder builder, Position processorPosition);

    default String stripPrefix(String linkName) {
        int pos = linkName.indexOf("-");
        return pos < 0 ? linkName : linkName.substring(pos + 1);
    }
}
