package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record AstLabel(SourcePosition sourcePosition, List<AstLabelSegment> segments) implements AstSchemaItem {

    @Override
    public AstLabel withEmptyPosition() {
        return new AstLabel(SourcePosition.EMPTY, erasePositions(segments));
    }

    public static AstLabel of(SourcePosition sourcePosition, String label) {
        return new AstLabel(sourcePosition, List.of(AstLabelSegment.of(sourcePosition, label)));
    }
}
