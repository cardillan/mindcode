package info.teksol.mindcode.compiler;

import java.util.List;

public record SourceFile(String fileName, String absolutePath, String code) {

    public static SourceFile createSourceFile(String code) {
        return new SourceFile("", "", code);
    }

    public static List<SourceFile> createSourceFiles(String code) {
        return List.of(createSourceFile(code));
    }
}
