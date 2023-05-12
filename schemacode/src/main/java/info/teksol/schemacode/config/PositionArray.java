package info.teksol.schemacode.config;

import info.teksol.schemacode.IOConsumer;
import info.teksol.schemacode.IOSupplier;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schema.Block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public record PositionArray(List<Position> positions) implements Configuration {

    public PositionArray(Position... positions) {
        this(List.of(positions));
    }

    public static final PositionArray EMPTY = new PositionArray(List.of());

    public int size() {
        return positions.size();
    }

    @Override
    public <T extends Configuration> T as(Class<T> type) {
        if (type == Position.class) {
            //noinspection unchecked
            return (T) (positions.isEmpty() ? Position.unpack(-1) : positions.get(0));
        }
        return Configuration.super.as(type);
    }

    @Override
    public Configuration encode(Block block) {
        return remap(p -> p.sub(block.position()));
    }

    public void store(IOConsumer<Position> consumer) throws IOException {
        for (Position position : positions) {
            consumer.accept(position);
        }
    }

    public static PositionArray create(int length, IOSupplier<Position> supplier) throws IOException {
        List<Position> array = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            array.add(supplier.get());
        }
        return new PositionArray(List.copyOf(array));
    }

    @Override
    public PositionArray remap(UnaryOperator<Position> transformation) {
        return new PositionArray(positions.stream().map(transformation).toList());
    }
}
