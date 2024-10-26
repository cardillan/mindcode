package info.teksol.mindcode;

import java.util.List;
import java.util.Objects;

public record InputFile(String fileName, String absolutePath, String code) {

    public static InputFile EMPTY = new InputFile("", "", "");

    public static InputFile createSourceFile(String code) {
        return new InputFile("", "", code);
    }

    public static List<InputFile> createSourceFiles(String code) {
        return List.of(createSourceFile(code));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InputFile inputFile = (InputFile) o;
        return Objects.equals(absolutePath, inputFile.absolutePath);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(absolutePath);
    }

    public boolean isEmpty() {
        return fileName.isEmpty() && absolutePath.isEmpty() && code.isEmpty();
    }
}
