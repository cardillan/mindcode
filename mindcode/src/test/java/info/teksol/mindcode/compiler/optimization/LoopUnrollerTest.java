package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class LoopUnrollerTest extends AbstractOptimizerTest<LoopUnroller> {

    @Override
    protected Class<LoopUnroller> getTestedClass() {
        return LoopUnroller.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return Optimization.LIST;
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setGoal(GenerationGoal.SPEED);
    }

    private final CompilerProfile basicProfile = createCompilerProfile()
            .setOptimizationLevel(Optimization.LOOP_UNROLLING, OptimizationLevel.BASIC);

    @Test
    void ignoresDegenerateLoops() {
        assertCompilesTo(createTestCompiler(basicProfile),
                """
                        i = 0
                        while i < 1000
                            print(i)
                        end
                        """,
                createInstruction(PRINT, "0")
        );
    }

    @Test
    void leavesNonIntegerLoopsBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                """
                        for i = 0; i <= 1.0; i += 0.1
                            print(i)
                        end
                        """,
                createInstruction(SET, "i", "0"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, "i"),
                createInstruction(OP, "add", "i", "i", "0.1"),
                createInstruction(JUMP, var(1003), "lessThanEq", "i", "1")
        );
    }

    @Test
    void leavesShiftingLoopBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                """
                        for k = 1; k < 100000; k <<= 1
                            printf(" $k")
                        end
                        """,
                createInstruction(SET, "k", "1"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, q(" ")),
                createInstruction(PRINT, "k"),
                createInstruction(OP, "shl", "k", "k", "1"),
                createInstruction(JUMP, var(1003), "lessThan", "k", "100000")
        );
    }

    @Test
    void preservesBranchedIterations() {
        assertCompilesTo("""
                        i = 0
                        while i < 10
                            i = i + (i % 2 ? 1 : 2)
                            print(i)
                        end
                        """,
                createInstruction(SET, "i", "0"),
                createInstruction(LABEL, var(1005)),
                createInstruction(OP, "mod", var(1), "i", "2"),
                createInstruction(JUMP, var(1003), "equal", var(1), "false"),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "add", "i", "i", "2"),
                createInstruction(LABEL, var(1004)),
                createInstruction(PRINT, "i"),
                createInstruction(JUMP, var(1005), "lessThan", "i", "10")
        );
    }

    @Test
    void preservesEmptyLoops() {
        assertCompilesTo("""
                        for i = -5; i < -10; i += 1
                            print("a")
                        end
                        """
        );
    }

    @Test
    void preservesNestedLoops() {
        assertCompilesTo("""
                        i = 0
                        while i < 10
                            while i < 5
                                i += 1
                            end
                            print(i)
                        end
                        """,
                createInstruction(SET, "i", "0"),
                createInstruction(LABEL, var(1006)),
                createInstruction(JUMP, var(1005), "greaterThanEq", "i", "5"),
                createInstruction(LABEL, var(1007)),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1007), "lessThan", "i", "5"),
                createInstruction(LABEL, var(1005)),
                createInstruction(PRINT, "i"),
                createInstruction(JUMP, var(1006), "lessThan", "i", "10")
        );
    }

    @Test
    void unrollsCStyleLoop() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        for i = 0; i < 10; i += 1
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("0123456789"))
        );
    }

    @Test
    void unrollsComplexLoop() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        i = 0
                        while i < 6
                            i += 2
                            print(i)
                            i -= 1
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("213243546576"))
        );
    }

    @Test
    void unrollsComplexLoopBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        i = 0
                        while i < 6
                            i += 2
                            print(i)
                            i -= 1
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("213243546576"))
        );
    }

    @Test
    void unrollsDecreasingLoopExclusiveBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i = 9; i > 2; i -= 1
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("9876543"))
        );
    }

    @Test
    void unrollsDecreasingLoopInclusiveBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i = 9; i >= 0; i -= 1
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("9876543210"))
        );
    }

    @Test
    void unrollsDoWhileLoop() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        i = 0
                        do
                            print(i)
                            i += 1
                        loop while i < 10
                        """,
                createInstruction(PRINT, q("0123456789"))
        );
    }

    @Test
    void unrollsDoWhileLoopBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        i = 0
                        do
                            print(i)
                            i += 1
                        loop while i < 10
                        """,
                createInstruction(PRINT, q("0123456789"))
        );
    }

    @Test
    void unrollsExclusiveRangeIterationLoopBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i in 0 ... 10
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("0123456789"))
        );
    }

    @Test
    void unrollsInclusiveRangeIterationLoopBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i in 0 .. 9
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("0123456789"))
        );
    }

    @Test
    void unrollsListIterationLoopWithBreak() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        for i in (1, 2, 3)
                            print(i)
                            if i == 2 break end
                        end
                        """,
                createInstruction(PRINT, q("12"))
        );
    }

    @Test
    void unrollsListIterationLoopWithContinue() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        for i in (1, 2, 3)
                            if i == 2 continue end
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("13"))
        );
    }

    @Test
    void unrollsListIterationLoops() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        for i in (1, 2, 3)
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("123"))
        );
    }

    @Test
    void unrollsListIterationLoopsWithThreeIterators() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        for i, j, k in 1, 2, 3, 4, 5, 6, 7, 8, 9 do
                            print(i, j, k);
                        end;
                        """,
                createInstruction(PRINT, q("123456789"))
        );
    }

    @Test
    void unrollsListIterationLoopsWithModifications() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        n = 0;
                        for out i, out j in a, b, c, d, e, f do
                            i = n += 1;
                            j = 2 * n;
                        end;

                        for i in a, b, c, d, e, f do
                            print(i);
                        end;
                        """,
                createInstruction(PRINT, q("122436"))
        );
    }

    @Test
    void unrollsLoopsWithBreak() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        for i in 0 .. 10
                            if i == 6 break end
                            print(i)
                        end
                        print(".")
                        """,
                createInstruction(PRINT, q("012345."))
        );
    }

    @Test
    void unrollsLoopsWithBreakBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i in 0 .. 10
                            if i == 6 break end
                            print(i)
                        end
                        print(".")
                        """,
                createInstruction(PRINT, q("012345."))
        );
    }

    @Test
    void unrollsLoopsWithContinue() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        for i in 0 .. 9
                            if i % 2 continue end
                            print(i)
                        end
                        print(".")
                        """,
                createInstruction(PRINT, q("02468."))
        );
    }

    @Test
    void unrollsLoopsWithContinueBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i in 0 .. 9
                            if i % 2 continue end
                            print(i)
                        end
                        print(".")
                        """,
                createInstruction(PRINT, q("02468."))
        );
    }

    @Test
    void unrollsNegativeLoop() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i = -5; i > -10; i -= 1
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("-5-6-7-8-9"))
        );
    }

    @Test
    void unrollsNegativeLoop2() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i = -9; i <= -5; i += 1
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("-9-8-7-6-5"))
        );
    }

    @Test
    void unrollsNegativeLoop3() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i = 5; i >= -5; i -= 1
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("543210-1-2-3-4-5"))
        );
    }

    @Test
    void unrollsNestedLoops() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        for i in 1 .. 5
                            for j in i .. 5
                                print(" ", 10 * i + j)
                            end
                        end
                        """,
                createInstruction(PRINT, q(" 11 12 13 14 15 22 23 24 25 33 34 35 44 45 55"))
        );
    }

    @Test
    void unrollsNestedLoops2() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        MainLoop:
                        for i in 0 ... 10
                            if i % 5 == 2
                                for j in 0 ... 10
                                    print(j)
                                    if j == i continue MainLoop end
                                end
                            end
                        end
                        print(".")
                        """,
                createInstruction(PRINT, q("01201234567."))
        );
    }

    @Test
    void unrollsNestedLoopsBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i in 1 .. 5
                            for j in i .. 5
                                print(" ", 10 * i + j)
                            end
                        end
                        """,
                createInstruction(PRINT, q(" 11 12 13 14 15 22 23 24 25 33 34 35 44 45 55"))
        );
    }

    @Test
    void unrollsRangeIterationLoop() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        for i in 0 .. 9
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("0123456789"))
        );
    }

    @Test
    void unrollsShiftingLoop() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        for k = 1; k < 100000; k <<= 1
                            printf(" $k")
                        end
                        """,
                createInstruction(PRINT, q(" 1 2 4 8 16 32 64 128 256 512 1024 2048 4096 8192 16384 32768 65536"))
        );
    }

    @Test
    void unrollsSingleIterationLoopBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i in 1 .. 1
                            print(i)
                        end
                        """,
                createInstruction(PRINT, "1")
        );
    }

    @Test
    void unrollsUnevenLoop() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i = 0; i < 3; i += 2
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("02"))
        );
    }

    @Test
    void unrollsUnevenLoop2() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i = 0; i <= 3; i += 2
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("02"))
        );
    }

    @Test
    void unrollsUnevenLoop3() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        for i = 0; i <= 4; i += 2
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("024"))
        );
    }

    @Test
    void unrollsUpdatesInConditions() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        i = 0
                        while (i += 1) < 10
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("123456789"))
        );
    }
    
    @Test
    void unrollsUpdatesInConditionsBasic() {
        assertCompilesTo(createTestCompiler(basicProfile),
                ix -> !(ix instanceof LabelInstruction),
                """
                        i = 0
                        while (i += 1) < 10
                            print(i)
                        end
                        """,
                createInstruction(PRINT, q("123456789"))
        );
    }

    @Test
    void unrollsWhileLoop() {
        assertCompilesTo(ix -> !(ix instanceof LabelInstruction),
                """
                        i = 0
                        while i < 10
                            print(i)
                            i += 1
                        end
                        """,
                createInstruction(PRINT, q("0123456789"))
        );
    }
}