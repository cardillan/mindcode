package info.teksol.schemacode.schema;

import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.mimex.BlockType;
import info.teksol.schemacode.mindustry.ConfigurationType;
import info.teksol.schemacode.mindustry.Direction;
import info.teksol.schemacode.mindustry.Implementation;
import info.teksol.schemacode.mindustry.Position;

import java.util.List;
import java.util.function.UnaryOperator;

public record Block(
        List<String> labels,
        BlockType blockType,
        Position position,
        Direction direction,
        Configuration configuration) {

    public String name() {
        return blockType.name();
    }

    public int size() {
        return blockType.size();
    }

    public Implementation implementation() {
        return blockType.implementation();
    }

    public Class<? extends Configuration> configurationClass() {
        return blockType.implementation().configurationClass();
    }

    public ConfigurationType configurationType() {
        return blockType.configurationType();
    }

    public int x() {
        return position.x();
    }

    public int y() {
        return position.y();
    }

    public int xMax() {
        return position.x() + size()  - 1;
    }

    public int yMax() {
        return position.y() + size() - 1;
    }

    public Block remap(UnaryOperator<Position> mapping) {
        return new Block(labels, blockType, mapping.apply(position), direction, configuration.remap(mapping));
    }
}
