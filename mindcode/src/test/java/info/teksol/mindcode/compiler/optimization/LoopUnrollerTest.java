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
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, "0"),
                createInstruction(JUMP, var(1003), "always")
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
                createInstruction(JUMP, var(1003), "lessThanEq", "i", "1"),
                createInstruction(END)
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
                createInstruction(JUMP, var(1003), "lessThan", "k", "100000"),
                createInstruction(END)
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
                createInstruction(SET, var(2), "2"),
                createInstruction(OP, "mod", var(1), "i", "2"),
                createInstruction(JUMP, var(1004), "equal", var(1), "false"),
                createInstruction(SET, var(2), "1"),
                createInstruction(LABEL, var(1004)),
                createInstruction(OP, "add", "i", "i", var(2)),
                createInstruction(PRINT, "i"),
                createInstruction(JUMP, var(1005), "lessThan", "i", "10"),
                createInstruction(END)
        );
    }

    @Test
    void preservesEmptyLoops() {
        assertCompilesTo("""
                        for i = -5; i < -10; i += 1
                            print("a")
                        end
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(JUMP, "__start__", "always")
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
                createInstruction(JUMP, var(1006), "lessThan", "i", "10"),
                createInstruction(END)
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
                createInstruction(PRINT, q("0123456789")),
                createInstruction(END)
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
                createInstruction(PRINT, q("213243546576")),
                createInstruction(END)
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
                createInstruction(PRINT, q("213243546576")),
                createInstruction(END)
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
                createInstruction(PRINT, q("9876543")),
                createInstruction(END)
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
                createInstruction(PRINT, q("9876543210")),
                createInstruction(END)
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
                createInstruction(PRINT, q("0123456789")),
                createInstruction(END)
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
                createInstruction(PRINT, q("0123456789")),
                createInstruction(END)
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
                createInstruction(PRINT, q("0123456789")),
                createInstruction(END)
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
                createInstruction(PRINT, q("0123456789")),
                createInstruction(END)
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
                createInstruction(PRINT, q("12")),
                createInstruction(JUMP, "__start__", "always")
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
                createInstruction(PRINT, q("13")),
                createInstruction(END)
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
                createInstruction(PRINT, q("123")),
                createInstruction(END)
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
                createInstruction(PRINT, q("123456789")),
                createInstruction(END)
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
                createInstruction(PRINT, q("122436")),
                createInstruction(END)
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
                createInstruction(PRINT, q("012345.")),
                createInstruction(END)
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
                createInstruction(PRINT, q("012345.")),
                createInstruction(END)
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
                createInstruction(PRINT, q("02468.")),
                createInstruction(END)
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
                createInstruction(PRINT, q("02468.")),
                createInstruction(END)
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
                createInstruction(PRINT, q("-5-6-7-8-9")),
                createInstruction(END)
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
                createInstruction(PRINT, q("-9-8-7-6-5")),
                createInstruction(END)
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
                createInstruction(PRINT, q("543210-1-2-3-4-5")),
                createInstruction(END)
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
                createInstruction(PRINT, q(" 11 12 13 14 15 22 23 24 25 33 34 35 44 45 55")),
                createInstruction(END)
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
                createInstruction(PRINT, q("01201234567.")),
                createInstruction(END)
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
                createInstruction(PRINT, q(" 11 12 13 14 15 22 23 24 25 33 34 35 44 45 55")),
                createInstruction(END)
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
                createInstruction(PRINT, q("0123456789")),
                createInstruction(END)
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
                createInstruction(PRINT, q(" 1 2 4 8 16 32 64 128 256 512 1024 2048 4096 8192 16384 32768 65536")),
                createInstruction(END)
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
                createInstruction(PRINT, "1"),
                createInstruction(END)
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
                createInstruction(PRINT, q("02")),
                createInstruction(END)
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
                createInstruction(PRINT, q("02")),
                createInstruction(END)
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
                createInstruction(PRINT, q("024")),
                createInstruction(END)
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
                createInstruction(PRINT, q("123456789")),
                createInstruction(END)
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
                createInstruction(PRINT, q("123456789")),
                createInstruction(END)
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
                createInstruction(PRINT, q("0123456789")),
                createInstruction(END)
        );
    }
}