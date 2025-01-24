package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourceElement;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface AstSchemaItem extends SourceElement {

    AstSchemaItem withEmptyPosition();

    @SuppressWarnings("unchecked")
    default <E extends @Nullable AstSchemaItem> E eraseNullablePosition(@Nullable E item) {
        return item == null ? null : (E) item.withEmptyPosition();
    }

    @SuppressWarnings("unchecked")
    default <E extends AstSchemaItem> E erasePosition(E item) {
        return (E) item.withEmptyPosition();
    }

    default <E extends AstSchemaItem> List<E> erasePositions(List<E> items) {
        return items.stream().map(this::erasePosition).toList();
    }
}
