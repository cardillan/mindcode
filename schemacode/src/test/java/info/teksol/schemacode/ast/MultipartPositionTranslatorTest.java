package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.ast.MultipartPositionTranslator.Part;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultipartPositionTranslatorTest {

    private static final InputFile inputFileA = new InputFile("A", "A", "");
    private static final InputFile inputFileB = new InputFile("B", "B", "");
    private static final InputFile inputFileC = new InputFile("C", "C", "");

    @Test
    void translatesSinglePart() {
        MultipartPositionTranslator instance = new MultipartPositionTranslator(List.of(
                new Part(10, 20, inputFileA, 10, 8)
        ));

        InputPosition position = instance.apply(new InputPosition(InputFile.EMPTY, 5, 5));

        assertEquals(
                new InputPosition(inputFileA, 15, 13),
                position);
    }

    @Test
    void translatesMultiplePartsFullLines() {
        MultipartPositionTranslator instance = new MultipartPositionTranslator(List.of(
                new Part(1, 0, inputFileA, 0, 0),
                new Part(1, 0, inputFileB, 0, 0),
                new Part(1, 0, inputFileC, 0, 0)
        ));

        assertEquals(
                new InputPosition(inputFileA, 1, 1),
                instance.apply(new InputPosition(InputFile.EMPTY, 1, 1)));
        assertEquals(
                new InputPosition(inputFileB, 1, 1),
                instance.apply(new InputPosition(InputFile.EMPTY, 2, 1)));
        assertEquals(
                new InputPosition(inputFileC, 1, 1),
                instance.apply(new InputPosition(InputFile.EMPTY, 3, 1)));
        assertEquals(
                new InputPosition(inputFileA, 1, 5),
                instance.apply(new InputPosition(InputFile.EMPTY, 1, 5)));
        assertEquals(
                new InputPosition(inputFileB, 1, 5),
                instance.apply(new InputPosition(InputFile.EMPTY, 2, 5)));
        assertEquals(
                new InputPosition(inputFileC, 1, 5),
                instance.apply(new InputPosition(InputFile.EMPTY, 3, 5)));
    }

    @Test
    void translatesMultiplePartsPartialLines() {
        MultipartPositionTranslator instance = new MultipartPositionTranslator(List.of(
                new Part(0, 10, inputFileA, 0, 0),
                new Part(0, 10, inputFileB, 0, 0),
                new Part(0, 10, inputFileC, 0, 0)
        ));

        assertEquals(
                new InputPosition(inputFileA, 1, 1),
                instance.apply(new InputPosition(InputFile.EMPTY, 1, 1)));
        assertEquals(
                new InputPosition(inputFileB, 1, 1),
                instance.apply(new InputPosition(InputFile.EMPTY, 1, 11)));
        assertEquals(
                new InputPosition(inputFileC, 1, 1),
                instance.apply(new InputPosition(InputFile.EMPTY, 1, 21)));
        assertEquals(
                new InputPosition(inputFileA, 1, 5),
                instance.apply(new InputPosition(InputFile.EMPTY, 1, 5)));
        assertEquals(
                new InputPosition(inputFileB, 1, 5),
                instance.apply(new InputPosition(InputFile.EMPTY, 1, 15)));
        assertEquals(
                new InputPosition(inputFileC, 1, 5),
                instance.apply(new InputPosition(InputFile.EMPTY, 1, 25)));
    }

    @Test
    void translatesMultiplePartsMixedLines() {
        MultipartPositionTranslator instance = new MultipartPositionTranslator(List.of(
                new Part(2, 10, inputFileA, 0, 0),
                new Part(0, 10, inputFileB, 0, 0),
                new Part(2, 10, inputFileC, 0, 0)
        ));

        assertEquals(
                new InputPosition(inputFileA, 3, 10),
                instance.apply(new InputPosition(InputFile.EMPTY, 3, 10)));
        assertEquals(
                new InputPosition(inputFileB, 1, 1),
                instance.apply(new InputPosition(InputFile.EMPTY, 3, 11)));
        assertEquals(
                new InputPosition(inputFileB, 1, 10),
                instance.apply(new InputPosition(InputFile.EMPTY, 3, 20)));
        assertEquals(
                new InputPosition(inputFileC, 1, 1),
                instance.apply(new InputPosition(InputFile.EMPTY, 3, 21)));
        assertEquals(
                new InputPosition(inputFileC, 2, 1),
                instance.apply(new InputPosition(InputFile.EMPTY, 4, 1)));
    }
}