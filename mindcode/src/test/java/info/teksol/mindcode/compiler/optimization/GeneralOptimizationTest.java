package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GeneralOptimizationTest extends AbstractOptimizerTest<Optimizer> {

    @Override
    protected Class<Optimizer> getTestedClass() {
        return null;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return Optimization.LIST;
    }

    @Test
    void generatesDoWhileLoopWithBreakAndContinue() {
        assertCompilesTo("""
                        print("Blocks:")
                        n = @links
                        MainLoop:
                        do
                            n = n - 1
                            block = getlink(n)
                            if block.type == @sorter
                                continue MainLoop
                            end
                            print("\\n", block)
                            if block.type == @unloader
                                break MainLoop
                            end
                        loop while n > 0
                        printflush(message1)
                        """,
                createInstruction(PRINT, q("Blocks:")),
                createInstruction(SET, "n", "@links"),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "sub", "n", "n", "1"),
                createInstruction(GETLINK, "block", "n"),
                createInstruction(SENSOR, var(2), "block", "@type"),
                createInstruction(JUMP, var(1001), "equal", var(2), "@sorter"),
                createInstruction(LABEL, var(1003)),
                createInstruction(LABEL, var(1004)),
                createInstruction(PRINT, q("\\n")),
                createInstruction(PRINT, "block"),
                createInstruction(SENSOR, var(5), "block", "@type"),
                createInstruction(JUMP, var(1002), "equal", var(5), "@unloader"),
                createInstruction(LABEL, var(1005)),
                createInstruction(LABEL, var(1006)),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "greaterThan", "n", "0"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINTFLUSH, "message1"),
                createInstruction(END)
        );
    }

    @Test
    void correctlyOptimizesFunctionCallAndReturn() {
        assertCompilesTo("""
                        allocate stack in cell1[33..48], heap in cell2[3...7]
                        def fn(n)
                            fn(n - 1)
                            2 * n
                        end
                                        
                        $x = fn(4) + fn(5)
                        $y = $x + 1
                        """,
                createInstruction(SET, "__sp", "33"),
                createInstruction(SET, "__fn0_n", "4"),
                createInstruction(CALLREC, "cell1", var(1000), var(1001)),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(SET, "__fn0_n", "5"),
                createInstruction(CALLREC, "cell1", var(1000), var(1002)),
                createInstruction(LABEL, var(1002)),
                createInstruction(OP, "add", var(2), var(0), "__fn0retval"),
                createInstruction(WRITE, var(2), "cell2", "3"),
                createInstruction(READ, var(3), "cell2", "3"),
                createInstruction(OP, "add", var(4), var(3), "1"),
                createInstruction(WRITE, var(4), "cell2", "4"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "sub", var(5), "__fn0_n", "1"),
                createInstruction(PUSH, "cell1", "__fn0_n"),
                createInstruction(SET, "__fn0_n", var(5)),
                createInstruction(CALLREC, "cell1", var(1000), var(1004)),
                createInstruction(LABEL, var(1004)),
                createInstruction(POP, "cell1", "__fn0_n"),
                createInstruction(OP, "mul", "__fn0retval", "2", "__fn0_n"),
                createInstruction(LABEL, var(1003)),
                createInstruction(RETURN, "cell1")
        );
    }


    @Test
    void optimizeSetThenOpWithBinaryOpBoth() {
        assertCompilesTo("""
                        x = 41
                        y = 72
                        pos = x + y
                        move(40, pos)
                        """,
                createInstruction(SET, "x", "41"),
                createInstruction(SET, "y", "72"),
                createInstruction(OP, "add", "pos", "x", "y"),
                createInstruction(UCONTROL, "move", "40", "pos"),
                createInstruction(END)
        );
    }

    @Test
    void setThenReadPrefersUserSpecifiedNames() {
        assertCompilesTo("""
                        addr_FLAG = 0
                        conveyor1.enabled = cell1[addr_FLAG] == 0
                        """,
                createInstruction(SET, "addr_FLAG", "0"),
                createInstruction(READ, var(0), "cell1", "addr_FLAG"),
                createInstruction(OP, "equal", var(1), var(0), "0"),
                createInstruction(CONTROL, "enabled", "conveyor1", var(1)),
                createInstruction(END)
        );
    }

    @Test
    void reallifeScripts() {
        assertCompilesTo("""
                        silicon = reconstructor1.silicon
                        graphite = reconstructor1.graphite
                        capacity = reconstructor1.itemCapacity
                        conveyor1.enabled = !( silicon < capacity || graphite < capacity )
                        """,
                createInstruction(SENSOR, "silicon", "reconstructor1", "@silicon"),
                createInstruction(SENSOR, "graphite", "reconstructor1", "@graphite"),
                createInstruction(SENSOR, "capacity", "reconstructor1", "@itemCapacity"),
                createInstruction(OP, "lessThan", var(3), "silicon", "capacity"),
                createInstruction(OP, "lessThan", var(4), "graphite", "capacity"),
                createInstruction(OP, "or", var(5), var(3), var(4)),
                createInstruction(OP, "equal", var(6), var(5), "false"),
                createInstruction(CONTROL, "enabled", "conveyor1", var(6)),
                createInstruction(END)
        );
    }

    @Test
    void reallifeScripts2() {
        assertCompilesTo("""
                        level = nucleus1.resource
                        print(level)
                        building.enabled = level < capacity
                        """,
                createInstruction(SENSOR, "level", "nucleus1", "@resource"),
                createInstruction(PRINT, "level"),
                createInstruction(OP, "lessThan", var(0), "level", "capacity"),
                createInstruction(CONTROL, "enabled", "building", var(0)),
                createInstruction(END)
        );
    }

    @Test
    void regressionTest1() {
        // https://github.com/francois/mindcode/issues/13
        assertCompilesTo("""
                        // Source
                        HEAPPTR = cell3
                        allocate heap in HEAPPTR
                        $a = 1
                        $b = 2
                        $c = 3
                        """,
                createInstruction(SET, "HEAPPTR", "cell3"),
                createInstruction(WRITE, "1", "HEAPPTR", "0"),
                createInstruction(WRITE, "2", "HEAPPTR", "1"),
                createInstruction(WRITE, "3", "HEAPPTR", "2"),
                createInstruction(END)
        );
    }

    @Test
    void regressionTest2() {
        // https://github.com/francois/mindcode/issues/13
        assertCompilesTo("""
                        HEAPPTR = cell3
                        allocate heap in HEAPPTR
                        print($a)
                        $a = 1
                        $b = 2
                        $c = 3
                        """,
                createInstruction(SET, "HEAPPTR", "cell3"),
                createInstruction(READ, var(0), "HEAPPTR", "0"),
                createInstruction(PRINT, var(0)),
                createInstruction(WRITE, "1", "HEAPPTR", "0"),
                createInstruction(WRITE, "2", "HEAPPTR", "1"),
                createInstruction(WRITE, "3", "HEAPPTR", "2"),
                createInstruction(END)
        );
    }

    @Test
    void regressionTest3() {
        // https://github.com/francois/mindcode/issues/15
        assertCompilesTo("""
                        desired = @dagger
                        boosting = false
                        payTake(desired)
                        payDrop()
                        boost(boosting)
                        idle()
                        stop()
                        """,
                createInstruction(SET, "desired", "@dagger"),
                createInstruction(SET, "boosting", "false"),
                createInstruction(UCONTROL, "payTake", "desired"),
                createInstruction(UCONTROL, "payDrop"),
                createInstruction(UCONTROL, "boost", "boosting"),
                createInstruction(UCONTROL, "idle"),
                createInstruction(UCONTROL, "stop"),
                createInstruction(END)
        );
    }

    @Test
    void regressionTest4() {
        // https://github.com/francois/mindcode/issues/23
        assertCompilesTo("""
                        x = 1
                        print("\\nx: ", x)
                        print("\\nx+x: ", x+x)
                        """,
                createInstruction(SET, "x", "1"),
                createInstruction(PRINT, "\"\\nx: \""),
                createInstruction(PRINT, "x"),
                createInstruction(OP, "add", var(3), "x", "x"),
                createInstruction(PRINT, "\"\\nx+x: \""),
                createInstruction(PRINT, var(3)),
                createInstruction(END)
        );
    }

    @Test
    void regressionTest5() {
        // Fix "Unvisited opcode: draw" and "drawflush"
        // We don't actually care about the generated code, only that it doesn't raise
        // Otherwise, this spec would be at the mercy of any improvements in the peephole optimizer
        assertDoesNotThrow(() -> generateInstructions("""
                // move previous values left
                for n in 0 ... 40
                    cell1[n] = cell1[n + 1]
                end
                                            
                // delay by 1/2 a sec (0.5 s)
                // this depends on your framerate -- if less than 60 fps,
                // the delay will be longer than 0.5s
                deadline = @tick + 30
                while @tick < deadline
                    n += 1
                end
                                            
                // calculate the new value -- the rightmost one
                // change this line to graph another level
                cell1[39] = tank1.cryofluid / tank1.liquidCapacity
                                            
                // draw the graph
                                            
                // clear the display
                clear(0, 0, 0)
                                            
                // set the foreground color to cryofluid
                color(62, 207, 240, 255)
                                            
                // draw the bar graph
                for n in 0 ... 40
                    rect(2 * n, 0, 2, 80 * cell1[n])
                end
                                            
                drawflush(display1)
                """
        ));
    }

    @Test
    void regressionTest6() {
        // https://github.com/francois/mindcode/issues/32
        assertCompilesTo("""
                        TestVar = 0xf
                        Result = ~TestVar
                        print(TestVar, "\\n", Result)
                        """,
                createInstruction(SET, "TestVar", "0xf"),
                createInstruction(OP, "not", "Result", "TestVar"),
                createInstruction(PRINT, "TestVar"),
                createInstruction(PRINT, "\"\\n\""),
                createInstruction(PRINT, "Result"),
                createInstruction(END)
        );
    }
}
