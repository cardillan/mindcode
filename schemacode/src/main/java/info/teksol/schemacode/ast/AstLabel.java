package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public record AstLabel(SourcePosition sourcePosition, List<AstLabelSegment> segments) implements AstSchemaItem {

    @Override
    public AstLabel withEmptyPosition() {
        return new AstLabel(SourcePosition.EMPTY, erasePositions(segments));
    }

    public static AstLabel of(SourcePosition sourcePosition) {
        return new AstLabel(sourcePosition, List.of());
    }

    public static AstLabel of(SourcePosition sourcePosition, String label) {
        return new AstLabel(sourcePosition, List.of(AstLabelSegment.of(sourcePosition, label)));
    }

    public int getSegmentCount() {
        return segments.size();
    }

    public String getSegment(int index) {
        return segments.get(index).name();
    }

    public String fullName() {
        return segments.stream().map(AstLabelSegment::name).collect(Collectors.joining("."));
    }

    public String fullName(int upTo) {
        return segments.stream().limit(upTo + 1).map(AstLabelSegment::name).collect(Collectors.joining("."));
    }

    @Override
    public String toString() {
        return fullName();
    }
}
