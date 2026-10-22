package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.mindcode.compiler.generation.variables.InternalArray;
import info.teksol.mc.mindcode.compiler.generation.variables.LargeArray;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.util.IntRange;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
class LoopIteratorTest extends AbstractCodeGeneratorTest {
    private static final int blockSize = 10;
    private static final int memoryCount = 4;
    private static final int arraySize = 36;

    @SuppressWarnings("NotNullFieldNotInitialized")
    private static LargeArray array;

    @BeforeAll
    static void setupArray() {
        InternalArray memoryArray = InternalArray.createInvalid(nameCreator, new AstIdentifier(EMPTY, "name"), memoryCount);
        array = new LargeArray(EMPTY, "name", Collections.nCopies(memoryCount, LogicVariable.INVALID), memoryArray, blockSize, arraySize);
    }

    @Test
    void debug() {
        int offset = 6;
        LoopIterator it = new LoopIterator(false, LogicVariable.INVALID, true, 3, 0);
        it.setArray(array.subarray(EMPTY, IntRange.halfOpen(offset, arraySize)));

        it.setIteration(0);
        assertEquals(2, it.nextIteration());
    }


    static Stream<Arguments> ascendingOffset0Args() {
        return Stream.of(
                Arguments.of(1, 0, 0, 10),
                Arguments.of(1, 0, 5, 10),
                Arguments.of(1, 0, 9, 10),
                Arguments.of(1, 0, 10, 20),
                Arguments.of(1, 0, 19, 20),
                Arguments.of(1, 0, 20, 30),

                Arguments.of(3, 0, 0, 4),
                Arguments.of(3, 0, 2, 4),
                Arguments.of(3, 0, 3, 4),
                Arguments.of(3, 0, 4, 7),
                Arguments.of(3, 0, 6, 7),
                Arguments.of(3, 0, 7, 10),

                Arguments.of(3, 1, 0, 3),
                Arguments.of(3, 1, 1, 3),
                Arguments.of(3, 1, 2, 3),
                Arguments.of(3, 1, 3, 7),
                Arguments.of(3, 1, 6, 7),
                Arguments.of(3, 1, 7, 10),

                Arguments.of(3, 2, 0, 3),
                Arguments.of(3, 2, 1, 3),
                Arguments.of(3, 2, 2, 3),
                Arguments.of(3, 2, 3, 6),
                Arguments.of(3, 2, 5, 6),
                Arguments.of(3, 2, 6, 10)
        );
    }

    @ParameterizedTest(name = "Ascending, offset 0, iterator {1} of {0}, iteration {2}->{3}")
    @MethodSource("ascendingOffset0Args")
    void ascendingOffset0(Integer iteratorCount, Integer iteratorIndex, Integer iteration, Integer nextIteration) {
        LoopIterator it = new LoopIterator(false, LogicVariable.INVALID, false, iteratorCount, iteratorIndex);
        it.setArray(array);

        it.setIteration(iteration);
        assertEquals(nextIteration, it.nextIteration());
    }

    static Stream<Arguments> ascendingOffset6Args() {
        return Stream.of(
                Arguments.of(1, 0, 0, 4),
                Arguments.of(1, 0, 3, 4),
                Arguments.of(1, 0, 4, 14),
                Arguments.of(1, 0, 13, 14),
                Arguments.of(1, 0, 14, 24),
                Arguments.of(1, 0, 23, 24),
                Arguments.of(1, 0, 24, 34),

                Arguments.of(3, 0, 0, 2),
                Arguments.of(3, 0, 1, 2),
                Arguments.of(3, 0, 2, 5),
                Arguments.of(3, 0, 4, 5),
                Arguments.of(3, 0, 5, 8),
                Arguments.of(3, 0, 7, 8),
                Arguments.of(3, 0, 8, 12),

                Arguments.of(3, 1, 0, 1),
                Arguments.of(3, 1, 1, 5),
                Arguments.of(3, 1, 4, 5),
                Arguments.of(3, 1, 5, 8),
                Arguments.of(3, 1, 7, 8),
                Arguments.of(3, 1, 8, 11),

                Arguments.of(3, 2, 0, 1),
                Arguments.of(3, 2, 1, 4),
                Arguments.of(3, 2, 3, 4),
                Arguments.of(3, 2, 4, 8),
                Arguments.of(3, 2, 7, 8),
                Arguments.of(3, 2, 8, 11)
        );
    }

    @ParameterizedTest(name = "Ascending, offset 6, iterator {1} of {0}, iteration {2}->{3}")
    @MethodSource("ascendingOffset6Args")
    void ascendingOffset6(Integer iteratorCount, Integer iteratorIndex, Integer iteration, Integer nextIteration) {
        int offset = 6;
        LoopIterator it = new LoopIterator(false, LogicVariable.INVALID, false, iteratorCount, iteratorIndex);
        it.setArray(array.subarray(EMPTY, IntRange.halfOpen(offset, arraySize)));

        it.setIteration(iteration);
        assertEquals(nextIteration, it.nextIteration());
    }


    static Stream<Arguments> ascendingOffset15Args() {
        return Stream.of(
                Arguments.of(1, 0, 0, 5),
                Arguments.of(1, 0, 4, 5),
                Arguments.of(1, 0, 5, 15),
                Arguments.of(1, 0, 15, 25),

                Arguments.of(3, 0, 0, 2),
                Arguments.of(3, 0, 1, 2),
                Arguments.of(3, 0, 2, 5),
                Arguments.of(3, 0, 4, 5),
                Arguments.of(3, 0, 5, 9),
                Arguments.of(3, 0, 8, 9),

                Arguments.of(3, 1, 0, 2),
                Arguments.of(3, 1, 1, 2),
                Arguments.of(3, 1, 2, 5),
                Arguments.of(3, 1, 4, 5),
                Arguments.of(3, 1, 5, 8),
                Arguments.of(3, 1, 7, 8),

                Arguments.of(3, 2, 0, 1),
                Arguments.of(3, 2, 1, 5),
                Arguments.of(3, 2, 4, 5),
                Arguments.of(3, 2, 5, 8),
                Arguments.of(3, 2, 7, 8),
                Arguments.of(3, 2, 8, 11)
        );
    }

    @ParameterizedTest(name = "Ascending, offset 15, iterator {1} of {0}, iteration {2}->{3}")
    @MethodSource("ascendingOffset15Args")
    void ascendingOffset15(Integer iteratorCount, Integer iteratorIndex, Integer iteration, Integer nextIteration) {
        int offset = 15;
        LoopIterator it = new LoopIterator(false, LogicVariable.INVALID, false, iteratorCount, iteratorIndex);
        it.setArray(array.subarray(EMPTY, IntRange.halfOpen(offset, arraySize)));

        it.setIteration(iteration);
        assertEquals(nextIteration, it.nextIteration());
    }

    static Stream<Arguments> descendingArgs() {
        return Stream.of(
                Arguments.of(1, 0, 0, 6),
                Arguments.of(1, 0, 3, 6),
                Arguments.of(1, 0, 5, 6),
                Arguments.of(1, 0, 6, 16),
                Arguments.of(1, 0, 16, 26),
                Arguments.of(1, 0, 26, 36),

                Arguments.of(3, 0, 0, 2),
                Arguments.of(3, 0, 1, 2),
                Arguments.of(3, 0, 2, 5),
                Arguments.of(3, 0, 4, 5),
                Arguments.of(3, 0, 5, 8),
                Arguments.of(3, 0, 8, 12),

                Arguments.of(3, 1, 0, 2),
                Arguments.of(3, 1, 1, 2),
                Arguments.of(3, 1, 2, 5),
                Arguments.of(3, 1, 4, 5),
                Arguments.of(3, 1, 5, 9),
                Arguments.of(3, 1, 8, 9),
                Arguments.of(3, 1, 9, 12),

                Arguments.of(3, 2, 0, 2),
                Arguments.of(3, 2, 1, 2),
                Arguments.of(3, 2, 2, 6),
                Arguments.of(3, 2, 5, 6),
                Arguments.of(3, 2, 6, 9),
                Arguments.of(3, 2, 8, 9),
                Arguments.of(3, 2, 9, 12)
        );
    }

    @ParameterizedTest(name = "Descending, offset 0, iterator {1} of {0}, iteration {2}->{3}")
    @MethodSource("descendingArgs")
    void descendingOffset0(Integer iteratorCount, Integer iteratorIndex, Integer iteration, Integer nextIteration) {
        LoopIterator it = new LoopIterator(false, LogicVariable.INVALID, true, iteratorCount, iteratorIndex);
        it.setArray(array);

        it.setIteration(iteration);
        assertEquals(nextIteration, it.nextIteration());
    }

    @ParameterizedTest(name = "Descending, offset 6, iterator {1} of {0}, iteration {2}->{3}")
    @MethodSource("descendingArgs")
    void descendingOffset6(Integer iteratorCount, Integer iteratorIndex, Integer iteration, Integer nextIteration) {
        int offset = 6;
        LoopIterator it = new LoopIterator(false, LogicVariable.INVALID, true, iteratorCount, iteratorIndex);
        it.setArray(array.subarray(EMPTY, IntRange.halfOpen(offset, arraySize)));

        it.setIteration(iteration);
        assertEquals(nextIteration, it.nextIteration());
    }

    @ParameterizedTest(name = "Descending, offset 15, iterator {1} of {0}, iteration {2}->{3}")
    @MethodSource("descendingArgs")
    void descendingOffset15(Integer iteratorCount, Integer iteratorIndex, Integer iteration, Integer nextIteration) {
        int offset = 0;
        LoopIterator it = new LoopIterator(false, LogicVariable.INVALID, true, iteratorCount, iteratorIndex);
        it.setArray(array.subarray(EMPTY, IntRange.halfOpen(offset, arraySize)));

        it.setIteration(iteration);
        assertEquals(nextIteration, it.nextIteration());
    }
}
