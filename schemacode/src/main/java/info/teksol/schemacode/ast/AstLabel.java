package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public record AstLabel(SourcePosition sourcePosition, List<AstLabelSegment> segments) implements AstSchemaItem {

    public AstLabel(List<AstLabelSegment> segments) {
        this(SourcePosition.EMPTY, segments);
    }

    @Override
    public AstLabel withEmptyPosition() {
        return new AstLabel(SourcePosition.EMPTY, erasePositions(segments));
    }

    public static AstLabel of() {
        return new AstLabel(List.of());
    }

    public static AstLabel of(String label) {
        return new AstLabel(List.of(new AstLabelSegment(label)));
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
}
