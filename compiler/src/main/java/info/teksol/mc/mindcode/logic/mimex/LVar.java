package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record LVar(
        String contentName,
        String name,
        boolean global,
        boolean object,
        boolean constant,
        double numericValue
) implements MindustryContent {

    @Override
    public ContentType contentType() {
        return ContentType.LVAR;
    }

    public boolean isNumericConstant() {
        return global && !object && constant && numericValue != 0.0;
    }
}
