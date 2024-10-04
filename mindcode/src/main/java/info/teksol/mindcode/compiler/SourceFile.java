package info.teksol.mindcode.compiler;

import java.util.List;

public record SourceFile(String fileName, String code) {

    public static List<SourceFile> createSourceFiles(String code) {
        return List.of(new SourceFile("", code));
    }
}
