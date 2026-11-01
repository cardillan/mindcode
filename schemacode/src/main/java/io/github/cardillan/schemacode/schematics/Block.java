package io.github.cardillan.schemacode.schematics;

import io.github.cardillan.mc.common.SourcePosition;
import io.github.cardillan.mc.emulator.mimex.LParser;
import io.github.cardillan.mc.emulator.mimex.LStrings;
import io.github.cardillan.mc.mindcode.logic.mimex.BlockType;
import io.github.cardillan.mc.mindcode.logic.mimex.MindustryMetadata;
import io.github.cardillan.mc.profile.options.Target;
import io.github.cardillan.schemacode.config.Configuration;
import io.github.cardillan.schemacode.config.PositionArray;
import io.github.cardillan.schemacode.mindustry.Direction;
import io.github.cardillan.schemacode.mindustry.Position;
import io.github.cardillan.schemacode.mindustry.ProcessorConfiguration;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public record Block(
        SourcePosition sourcePosition,
        int index,
        List<String> labels,
        BlockType blockType,
        Position position,
        Direction direction,
        Configuration configuration) implements BlockPosition {

    public Block remap(UnaryOperator<Position> mapping) {
        return new Block(sourcePosition, index, labels, blockType, mapping.apply(position), direction, configuration.remap(mapping));
    }

    public Block withConnections(PositionArray connections) {
        return new Block(sourcePosition, index, labels, blockType, position, direction, connections);
    }

    public Block withReformattedCode(Target target) {
        ProcessorConfiguration configuration = this.configuration.as(ProcessorConfiguration.class);
        LStrings strings = LStrings.create(target.version());
        MindustryMetadata metadata = MindustryMetadata.forVersion(target.version());
        LParser parser = LParser.create((_, _) -> false, metadata, strings, configuration.code(),
                true, false);

        String code = parser.parse().stream().map(Object::toString).collect(Collectors.joining("\n"));

        return new Block(sourcePosition, index, labels, blockType, position, direction, new ProcessorConfiguration(configuration.links(), code));
    }
}
