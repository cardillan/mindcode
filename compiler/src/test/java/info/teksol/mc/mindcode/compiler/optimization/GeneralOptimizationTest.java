package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
class GeneralOptimizationTest extends AbstractOptimizerTest<Optimizer> {

    @Override
    protected @Nullable Class<Optimizer> getTestedClass() {
        return null;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return Optimization.LIST;
    }

    @Test
    void generatesDoWhileLoopWithBreakAndContinue() {
        assertCompilesTo("""
                        print("Blocks:");
                        n = @links;
                        MainLoop:
                        do
                            n = n - 1;
                            block = getlink(n);
                            if block.@type == @sorter then
                                continue MainLoop;
                            end;
                            print("\\n", block);
                            if block.@type == @unloader then
                                break MainLoop;
                            end;
                        while n > 0;
                        printflush(message1);
                        """,
                createInstruction(PRINT, q("Blocks:")),
                createInstruction(SET, ":n", "@links"),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "sub", ":n", ":n", "1"),
                createInstruction(GETLINK, ":block", ":n"),
                createInstruction(SENSOR, var(3), ":block", "@type"),
                createInstruction(JUMP, var(1001), "equal", var(3), "@sorter"),
                createInstruction(PRINT, q("\n")),
                createInstruction(PRINT, ":block"),
                createInstruction(JUMP, var(1002), "equal", var(3), "@unloader"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "greaterThan", ":n", "0"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINTFLUSH, "message1")
        );
    }

    @Test
    void correctlyOptimizesFunctionCallAndReturn() {
        assertCompilesTo("""
                        allocate stack in cell1[33..48], heap in cell2[3...7];
                        def fn(n)
                            fn(n - 1);
                            2 * n;
                        end;

                        $x = fn(4) + fn(5);
                        $y = $x + 1;
                        """,
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1001), "equal", "cell1", "null"),
                createInstruction(SET, "*sp", "33"),
                createInstruction(LABEL, var(1002)),
                createInstruction(JUMP, var(1002), "equal", "cell2", "null"),
                createInstruction(SET, ":fn0:n", "4"),
                createInstruction(CALLREC, "cell1", var(1000), var(1003), ":fn0*retval"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(1), ":fn0*retval"),
                createInstruction(SET, ":fn0:n", "5"),
                createInstruction(CALLREC, "cell1", var(1000), var(1004), ":fn0*retval"),
                createInstruction(LABEL, var(1004)),
                createInstruction(OP, "add", var(3), var(1), ":fn0*retval"),
                createInstruction(WRITE, var(3), "cell2", "3"),
                createInstruction(READ, var(0), "cell2", "3"),
                createInstruction(OP, "add", var(5), var(0), "1"),
                createInstruction(WRITE, var(5), "cell2", "4"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(PUSH, "cell1", ":fn0:n"),
                createInstruction(OP, "sub", ":fn0:n", ":fn0:n", "1"),
                createInstruction(CALLREC, "cell1", var(1000), var(1006), ":fn0*retval"),
                createInstruction(LABEL, var(1006)),
                createInstruction(POP, "cell1", ":fn0:n"),
                createInstruction(OP, "mul", ":fn0*retval", "2", ":fn0:n"),
                createInstruction(RETURNREC, "cell1")
        );
    }


    @Test
    void optimizeSetThenOpWithBinaryOpBoth() {
        assertCompilesTo("""
                        x = 41;
                        y = 72;
                        pos = x + y;
                        move(40, pos);
                        """,
                createInstruction(UCONTROL, "move", "40", "113")
        );
    }

    @Test
    void setThenReadPrefersUserSpecifiedNames() {
        assertCompilesTo("""
                        addr_FLAG = 0;
                        conveyor1.enabled = cell1[addr_FLAG] == 0;
                        """,
                createInstruction(READ, var(0), "cell1", "0"),
                createInstruction(OP, "equal", var(1), var(0), "0"),
                createInstruction(CONTROL, "enabled", "conveyor1", var(1))
        );
    }

    @Test
    void realLifeScripts() {
        assertCompilesTo("""
                        silicon = reconstructor1.@silicon;
                        graphite = reconstructor1.@graphite;
                        capacity = reconstructor1.@itemCapacity;
                        conveyor1.enabled = !( silicon < capacity or graphite < capacity );
                        """,
                createInstruction(SENSOR, "silicon", "reconstructor1", "@silicon"),
                createInstruction(SENSOR, "graphite", "reconstructor1", "@graphite"),
                createInstruction(SENSOR, "capacity", "reconstructor1", "@itemCapacity"),
                createInstruction(OP, "lessThan", var(3), "silicon", "capacity"),
                createInstruction(OP, "lessThan", var(4), "graphite", "capacity"),
                createInstruction(OP, "or", var(5), var(3), var(4)),
                createInstruction(OP, "equal", var(6), var(5), "false"),
                createInstruction(CONTROL, "enabled", "conveyor1", var(6))
        );
    }

    @Test
    void realLifeScripts2() {
        assertCompilesTo("""
                        level = nucleus1.@coal;
                        print(level);
                        conveyor1.enabled = level < 10;
                        """,
                createInstruction(SENSOR, "level", "nucleus1", "@coal"),
                createInstruction(PRINT, "level"),
                createInstruction(OP, "lessThan", var(0), "level", "10"),
                createInstruction(CONTROL, "enabled", "conveyor1", var(0))
        );
    }

    @Test
    void regressionTest1() {
        // https://github.com/francois/mindcode/issues/13
        assertCompilesTo("""
                        // Source
                        param HEAPPTR = cell3;
                        allocate heap in HEAPPTR;
                        $a = 1;
                        $b = 2;
                        $c = 3;
                        """,
                createInstruction(SET, "HEAPPTR", "cell3"),
                createInstruction(WRITE, "1", "HEAPPTR", "0"),
                createInstruction(WRITE, "2", "HEAPPTR", "1"),
                createInstruction(WRITE, "3", "HEAPPTR", "2")
        );
    }

    @Test
    void regressionTest2() {
        // https://github.com/francois/mindcode/issues/13
        assertCompilesTo("""
                        param HEAPPTR = cell3;
                        allocate heap in HEAPPTR;
                        print($a);
                        $a = 1;
                        $b = 2;
                        $c = 3;
                        """,
                createInstruction(SET, "HEAPPTR", "cell3"),
                createInstruction(READ, var(0), "HEAPPTR", "0"),
                createInstruction(PRINT, var(0)),
                createInstruction(WRITE, "1", "HEAPPTR", "0"),
                createInstruction(WRITE, "2", "HEAPPTR", "1"),
                createInstruction(WRITE, "3", "HEAPPTR", "2")
        );
    }

    @Test
    void regressionTest3() {
        // https://github.com/francois/mindcode/issues/15
        assertCompilesTo("""
                        desired = @dagger;
                        boosting = false;
                        payTake(desired);
                        payDrop();
                        boost(boosting);
                        idle();
                        stop();
                        """,
                createInstruction(UCONTROL, "payTake", "@dagger"),
                createInstruction(UCONTROL, "payDrop"),
                createInstruction(UCONTROL, "boost", "false"),
                createInstruction(UCONTROL, "idle"),
                createInstruction(UCONTROL, "stop")
        );
    }

    @Test
    void regressionTest4() {
        // https://github.com/francois/mindcode/issues/23
        assertCompilesTo("""
                        x = 1;
                        print("\\nx: ", x);
                        print("\\nx+x: ", x+x);
                        """,
                createInstruction(PRINT, q("\nx: 1\nx+x: 2"))
        );
    }

    @Test
    void regressionTest5() {
        // Fix "Unvisited opcode: draw" and "drawflush"
        // We don't actually care about the generated code, only that it doesn't raise
        // Otherwise, this spec would be at the mercy of any improvements in the peephole optimizer
        assertGeneratesMessages(
                expectedMessages(),
                """
                        // move previous values left
                        for n in 0 ... 40 do
                            cell1[n] = cell1[n + 1];
                        end;
                        
                        // delay by 1/2 a sec (0.5 s)
                        // this depends on your framerate -- if less than 60 fps,
                        // the delay will be longer than 0.5s
                        deadline = @tick + 30;
                        while @tick < deadline do
                            n += 1;
                        end;
                        
                        // calculate the new value -- the rightmost one
                        // change this line to graph another level
                        cell1[39] = tank1.@cryofluid / tank1.@liquidCapacity;
                        
                        // draw the graph
                        
                        // clear the display
                        clear(0, 0, 0);
                        
                        // set the foreground color to cryofluid
                        color(62, 207, 240, 255);
                        
                        // draw the bar graph
                        for n in 0 ... 40 do
                            rect(2 * n, 0, 2, 80 * cell1[n]);
                        end;
                        
                        drawflush(display1);
                        """
        );
    }

    @Test
    void regressionTest6() {
        // https://github.com/francois/mindcode/issues/32
        assertCompilesTo("""
                        TestVar = 0xf;
                        Result = ~TestVar;
                        print(TestVar, "\\n", Result);
                        """,
                createInstruction(PRINT, q("15\n-16"))
        );
    }

    @Test
    void optimizesLoopsInConditions() {
        // The loop condition in each of the loops is moved to the end of the loop
        assertCompilesTo("""
                        inline def sum(n)
                          c = 0;
                          for i in 0 ... n do
                            c += cell1[i];
                          end;
                          return c;
                        end;

                        result = if sum(1000) < sum(2000) then
                            print("Less");
                            0;
                        else
                            1;
                        end;
                        print(result);
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SET, "__fn0_c", "0"),
                createInstruction(SET, "__fn0_i", "0"),
                createInstruction(LABEL, var(1012)),
                createInstruction(READ, var(2), "cell1", "__fn0_i"),
                createInstruction(OP, "add", "__fn0_c", "__fn0_c", var(2)),
                createInstruction(OP, "add", "__fn0_i", "__fn0_i", "1"),
                createInstruction(JUMP, var(1012), "lessThan", "__fn0_i", "1000"),
                createInstruction(SET, "__fn1_c", "0"),
                createInstruction(SET, "__fn1_i", "0"),
                createInstruction(LABEL, var(1013)),
                createInstruction(READ, var(6), "cell1", "__fn1_i"),
                createInstruction(OP, "add", "__fn1_c", "__fn1_c", var(6)),
                createInstruction(OP, "add", "__fn1_i", "__fn1_i", "1"),
                createInstruction(JUMP, var(1013), "lessThan", "__fn1_i", "2000"),
                createInstruction(JUMP, var(1010), "greaterThanEq", "__fn0_c", "__fn1_c"),
                createInstruction(PRINT, q("Less0")),
                createInstruction(JUMP, "__start__", "always"),
                createInstruction(LABEL, var(1010)),
                createInstruction(PRINT, "1")
        );
    }

    @Test
    void ignoresUnusedFunctions() {
        assertCompilesTo("""
                        def foo()
                            print("foo");
                        end;
                        print(0);
                        """,
                createInstruction(PRINT, "0")
        );
    }

    @Test
    void computesFunctionWeights() {
        compile(expectedMessages(),
                        """
                        #set goal = size;
                        #set function-inlining = none;
                        #set loop-unrolling = none;
                        def a(n) print(n); end;
                        def b(n) c(n); c(n + 1); end;
                        def c(n) print(n / 2); end;
                        
                        a(1); a(2);
                        
                        for i in 1 .. 25 do
                            b(i); b(i + 1);
                        end;
                        """,
                compiler -> {
                    List<Double> weights = compiler.getRootAstContext().children().stream()
                            .filter(ctx -> ctx.function() != null)
                            .map(AstContext::weight).toList();
                    assertEquals(List.of(2d, 50d, 100d), weights, "Computed function weights differ from expected.");
                });
    }

    @Test
    void optimizesCustomInstructionInputs() {
        assertCompilesTo("""
                        inline void foo(in x, out y)
                            mlog("foo", in x, out y);
                        end;
                        
                        param a = 10;
                        foo(a, out b);
                        print(b);
                        """,
                createInstruction(SET, "a", "10"),
                customInstruction("foo", "a", "__fn0_y"),
                createInstruction(PRINT, "__fn0_y")
        );
    }

    @Test
    void optimizesCustomInstructionInputs2() {
        assertCompilesTo("""
                        inline void foo(in x)
                            mlog("foo", in x);
                        end;
                        
                        foo(rand(10) > 5 ? 10 : 5);
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(OP, "rand", var(0), "10"),
                createInstruction(JUMP, var(1000), "lessThanEq", var(0), "5"),
                customInstruction("foo", "10"),
                createInstruction(JUMP, "__start__", "always"),
                createInstruction(LABEL, var(1000)),
                customInstruction("foo", "5")
        );
    }

    @Test
    void optimizesCustomInstructionOutputs() {
        assertCompilesTo("""
                        inline def foo(in x)
                            mlog("foo", in x, out y);
                            y;
                        end;
                        
                        print(foo(10));
                        """,
                customInstruction("foo", "10", "__fn0_y"),
                createInstruction(PRINT, "__fn0_y")
        );
    }

    @Test
    void removesSafeCustomInstructions() {
        assertCompilesTo(
                expectedMessages().add("Variable 'y' is not used."),
                """
                        inline def foo()
                            mlogSafe("foo", out x);
                            x;
                        end;
                        
                        x = foo();
                        y = foo();
                        print(x);
                        """,
                customInstruction("foo", "__fn0_x"),
                createInstruction(PRINT, "__fn0_x")
        );
    }

    @Test
    void keepsUnsafeCustomInstructions() {
        assertCompilesTo(
                expectedMessages().add("Variable 'y' is not used."),
                """
                        inline def foo()
                            mlog("foo", out x);
                            x;
                        end;
                        
                        x = foo();
                        y = foo();
                        print(x);
                        """,
                customInstruction("foo", "__fn0_x"),
                customInstruction("foo", "__fn1_x"),
                createInstruction(PRINT, "__fn0_x")
        );
    }

    @Test
    void keepsInteractingCustomInstructions() {
        assertCompilesTo("""
                        inline void foo(x)
                            mlog("foo", in x);
                            x;
                        end;
                        
                        foo(1);
                        foo(2);
                        """,
                customInstruction("foo", "1"),
                customInstruction("foo", "2")
        );
    }

    @Test
    void respectsParametersInTernaryOperators() {
        assertCompilesTo("""
                        param a = 1;
                        b = 2 * (a > 0 ? 10 : 20);
                        print(b);
                        """,
                createInstruction(SET, "a", "1"),
                createInstruction(OP, "mul", "b", "2", "20"),
                createInstruction(JUMP, var(1001), "lessThanEq", "a", "0"),
                createInstruction(OP, "mul", "b", "2", "10"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "b")
        );
    }
}
