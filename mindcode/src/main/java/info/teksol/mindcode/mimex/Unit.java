package info.teksol.mindcode.mimex;

public record Unit(
        String baseName,
        String name,
        int id
) implements NumberedConstant {
}
