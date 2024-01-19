package info.teksol.mindcode.mimex;

public record Item(
        String baseName,
        String name,
        int id
) implements NumberedConstant {
}
