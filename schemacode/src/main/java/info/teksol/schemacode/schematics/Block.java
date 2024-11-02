package info.teksol.schemacode.schematics;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.mimex.BlockType;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.config.PositionArray;
import info.teksol.schemacode.mindustry.Direction;
import info.teksol.schemacode.mindustry.Position;

import java.util.List;
import java.util.function.UnaryOperator;

public record Block(
        InputPosition inputPosition,
        int index,
        List<String> labels,
        BlockType blockType,
        Position position,
        Direction direction,
        Configuration configuration) implements BlockPosition {

    public Block remap(UnaryOperator<Position> mapping) {
        return new Block(inputPosition, index, labels, blockType, mapping.apply(position), direction, configuration.remap(mapping));
    }

    public Block withConnections(PositionArray connections) {
        return new Block(inputPosition, index, labels, blockType, position, direction, connections);
    }
}
