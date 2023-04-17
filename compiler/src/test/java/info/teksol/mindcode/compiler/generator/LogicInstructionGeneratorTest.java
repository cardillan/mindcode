package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.optimization.Optimization;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LogicInstructionGeneratorTest extends AbstractGeneratorTest {

    @Test
    void throwsAnOutOfHeapSpaceExceptionWhenUsingMoreHeapSpaceThanAllocated() {
        assertThrows(OutOfHeapSpaceException.class,
                () -> generateUnoptimized((Seq) translateToAst("allocate heap in cell1[0 .. 1]\n$dx = $dy = $dz")));
    }

    @Test
    void convertsComplexAssignment() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "sub", var(0), "bar", "2"),
                        createInstruction(OP, "mul", var(1), var(0), "3"),
                        createInstruction(SET, "foo", var(1)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("foo = (bar - 2) * 3"))
        );
    }

    @Test
    void convertsWhileLoopAndPrintFunctionCall() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "n", "0"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "lessThan", var(0), "n", "5"),
                        createInstruction(JUMP, var(1002), "equal", var(0), "false"),
                        createInstruction(OP, "add", var(1), "n", "1"),
                        createInstruction(SET, "n", var(1)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINT, "\"n: \""),
                        createInstruction(PRINT, "n"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                n = 0
                                while n < 5
                                    n += 1
                                end
                                print("n: ", n)
                                """
                        )
                )
        );
    }

    @Test
    void convertsNullAndUnaryOp() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "not", var(0), "a"),
                        createInstruction(SET, "a", var(0)),
                        createInstruction(OP, "equal", var(1), "b", "false"),
                        createInstruction(SET, "b", var(1)),
                        createInstruction(OP, "equal", var(2), "c", "false"),
                        createInstruction(SET, "c", var(2)),
                        createInstruction(SET, "x", "null"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                a = ~a
                                b = !b
                                c = not c
                                x = null
                                """
                        )
                )
        );
    }

    @Test
    void convertsSensorReadings() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var(0), "foundation1", "@copper"),
                        createInstruction(SENSOR, var(1), "foundation1", "@itemCapacity"),
                        createInstruction(OP, "lessThan", var(2), var(0), var(1)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("foundation1.copper < foundation1.itemCapacity"))
        );
    }

    @Test
    void convertsBooleanOperations() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "notEqual", var(0), "z", "true"),
                        createInstruction(JUMP, var(1002), "equal", var(0), "false"),
                        createInstruction(PRINT, "\"infinite loop!\""),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                while z != true
                                    print("infinite loop!")
                                end
                                printflush(message1)
                                """
                        )
                )
        );
    }

    @Test
    void convertsControlStatements() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var(1), "foundation1", "@copper"),
                        createInstruction(SENSOR, var(2), "tank1", "@water"),
                        createInstruction(OP, "strictEqual", var(3), var(1), var(2)),
                        createInstruction(CONTROL, "enabled", "conveyor1", var(3)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("conveyor1.enabled = foundation1.copper === tank1.water")
                )
        );
    }

    @Test
    void convertsHeapAccesses() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(READ, var(0), "cell2", "4"),
                        createInstruction(SENSOR, var(1), "conveyor1", "@enabled"),
                        createInstruction(OP, "add", var(2), var(0), var(1)),
                        createInstruction(WRITE, var(2), "cell1", "3"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("cell1[3] = cell2[4] + conveyor1.enabled")
                )
        );
    }

    @Test
    void convertsIfExpression() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(READ, var(0), "cell1", "4"),
                        createInstruction(OP, "equal", var(1), var(0), "0"),
                        createInstruction(JUMP, var(1000), "equal", var(1), "false"),
                        createInstruction(SET, var(2), "false"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(WRITE, "true", "cell1", "4"),
                        createInstruction(OP, "add", var(3), "n", "1"),
                        createInstruction(SET, "n", var(3)),
                        createInstruction(SET, var(2), var(3)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, "value", var(2)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                value = if cell1[4] == 0
                                    false
                                else
                                    cell1[4] = true
                                    n += 1
                                end
                                """
                        )
                )
        );
    }

    @Test
    void convertsMainMemoryVariable() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "memory", "cell1"),
                        createInstruction(OP, "rand", var(0), "387420489"),
                        createInstruction(WRITE, var(0), "memory", "0"),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("memory = cell1 memory[0] = rand(9**9)"))
        );
    }

    @Test
    void convertsFunctionsReturningValues() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "z", "9"),
                        createInstruction(OP, "pow", var(0), "z", "9"),
                        createInstruction(OP, "rand", var(1), var(0)),
                        createInstruction(WRITE, var(1), "cell1", "0"),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("z = 9; cell1[0] = rand(z**9)"))
        );
    }

    @Test
    void convertsUbindAndControl() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "strictEqual", var(0), "@unit", "null"),
                        createInstruction(JUMP, var(1001), "equal", var(0), "false"),
                        createInstruction(UBIND, "@poly"),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                while @unit === null
                                    ubind(@poly)
                                end
                                """
                        )
                )
        );

    }

    @Test
    void convertsReallifeTest1() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "n", "0"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(GETLINK, var(0), "n"),
                        createInstruction(SET, "reactor", var(0)),
                        createInstruction(OP, "notEqual", var(1), var(0), "null"),
                        createInstruction(JUMP, var(1002), "equal", var(1), "false"),
                        createInstruction(SENSOR, var(2), "reactor", "@liquidCapacity"),
                        createInstruction(OP, "greaterThan", var(3), var(2), "0"),
                        createInstruction(JUMP, var(1003), "equal", var(3), "false"),
                        createInstruction(SENSOR, var(5), "reactor", "@cryofluid"),
                        createInstruction(SENSOR, var(6), "reactor", "@liquidCapacity"),
                        createInstruction(OP, "div", var(7), var(5), var(6)),
                        createInstruction(SET, "pct_avail", var(7)),
                        createInstruction(OP, "greaterThanEq", var(8), "pct_avail", "0.25"),
                        createInstruction(CONTROL, "enabled", "reactor", var(8)),
                        createInstruction(SET, var(4), var(8)),
                        createInstruction(JUMP, var(1004), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(4), "null"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(OP, "add", var(9), "n", "1"),
                        createInstruction(SET, "n", var(9)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                n = 0
                                while (reactor = getlink(n)) != null
                                    if reactor.liquidCapacity > 0
                                        pct_avail = reactor.cryofluid / reactor.liquidCapacity
                                        reactor.enabled = pct_avail >= 0.25
                                    end
                                    n += 1
                                end
                                """
                        )
                )
        );
    }

    @Test
    void convertsUnaryMinus() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "mul", var(0), "dx", "-1"),
                        createInstruction(SET, "dx", var(0)),
                        createInstruction(SET, "dy", "-1"),
                        createInstruction(OP, "sub", var(1), "dy", "1"),
                        createInstruction(SET, "dz", var(1)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("dx *= -1; dy = -1; dz = dy - 1"))
        );
    }

    @Test
    void removesCommentsFromLogicInstructions() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "a", "1"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                // Remember that we initialized ourselves
                                a = 1
                                """
                        )
                )
        );
    }

    @Test
    void generatesRefsWithDashInThem() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(UCONTROL, "build", "x", "y", "@titanium-conveyor", "1", "0"),
                        createInstruction(UCONTROL, "getBlock", "x", "y", "b_type", "b_building", "b_floor"),
                        createInstruction(OP, "equal", var(0), "b_type", "@titanium-conveyor"),
                        createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                        createInstruction(OP, "add", var(2), "n", "1"),
                        createInstruction(SET, "n", var(2)),
                        createInstruction(SET, var(1), var(2)),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(1), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                build(x, y, @titanium-conveyor, 1, 0)
                                getBlock(x, y, b_type, b_building, b_floor)
                                if b_type == @titanium-conveyor
                                    n += 1
                                end
                                """
                        )
                )
        );
    }

    @Test
    void generatesComplexMathExpression() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "rand", var(0), "1"),
                        createInstruction(OP, "tan", var(1), var(0)),
                        createInstruction(OP, "abs", var(2), var(1)),
                        createInstruction(OP, "cos", var(3), var(2)),
                        createInstruction(OP, "log", var(4), var(3)),
                        createInstruction(OP, "sin", var(5), var(4)),
                        createInstruction(OP, "floor", var(6), var(5)),
                        createInstruction(OP, "ceil", var(7), var(6)),
                        createInstruction(SET, "x", var(7)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("x = ceil(floor(sin(log(cos(abs(tan(rand(1))))))))")
                )
        );
    }

    @Test
    void parsesInclusiveIteratorStyleLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "n", "1"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "lessThanEq", var(0), "n", "17"),
                        createInstruction(JUMP, var(1002), "equal", var(0), "false"),
                        createInstruction(PRINT, "n"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(OP, "add", var(1), "n", "1"),
                        createInstruction(SET, "n", var(1)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("for n in 1 .. 17 print(n) end")
                )
        );
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        // init
                        createInstruction(SET, "n", "1"),

                        // cond
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "lessThan", var(0), "n", "17"),
                        createInstruction(JUMP, var(1002), "equal", var(0), "false"),

                        // loop body
                        createInstruction(PRINT, "n"),

                        // continue label
                        createInstruction(LABEL, var(1001)),

                        // increment
                        createInstruction(OP, "add", var(1), "n", "1"),
                        createInstruction(SET, "n", var(1)),

                        // loop
                        createInstruction(JUMP, var(1000), "always"),

                        // trailer
                        createInstruction(LABEL, var(1002)),

                        // rest of program
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("for n in 1 ... 17 print(n) end")
                )
        );
    }

    @Test
    void parsesRangeExpressionLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        // init
                        createInstruction(SET, "n", "a"),

                        // cond
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "lessThan", var(0), "n", "b"),
                        createInstruction(JUMP, var(1002), "equal", var(0), "false"),

                        // loop body
                        createInstruction(PRINT, "n"),

                        // continue label
                        createInstruction(LABEL, var(1001)),

                        // increment
                        createInstruction(OP, "add", var(1), "n", "1"),
                        createInstruction(SET, "n", var(1)),

                        // loop
                        createInstruction(JUMP, var(1000), "always"),

                        // trailer
                        createInstruction(LABEL, var(1002)),

                        // rest of program
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("for n in a ... b print(n) end")
                )
        );
    }
    @Test
    void generatesCStyleComplexForLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "i", "0"),
                        createInstruction(SET, "j", "-5"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "lessThan", var(0), "i", "5"),
                        createInstruction(JUMP, var(1002), "equal", var(0), "false"),
                        createInstruction(PRINT, "n"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(OP, "sub", var(1), "j", "1"),
                        createInstruction(SET, "j", var(1)),
                        createInstruction(OP, "add", var(2), "i", "1"),
                        createInstruction(SET, "i", var(2)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                for i = 0, j = -5; i < 5; j -= 1, i += 1
                                    print(n)
                                end
                                """
                        )
                )
        );
    }

    @Test
    void supportsAssigningAssignmentResults() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "b", "42"),
                        createInstruction(SET, "a", "42"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("a = b = 42")
                )
        );
    }

    @Test
    void generatesCaseWhenElse() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__ast0", "n"),
                        createInstruction(JUMP, var(1002), "equal", "__ast0", "1"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(0), "\"1\""),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1004), "equal", "__ast0", "2"),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(SET, var(0), "\"two\""),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(0), "\"otherwise\""),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                case n
                                    when 1      "1"
                                    when 2      "two"
                                    else        "otherwise"
                                end
                                """
                        )
                )
        );
    }

    @Test
    void generatesCaseWhen() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(READ, var(0), "cell1", "0"),
                        createInstruction(SET, "__ast0", var(0)),
                        createInstruction(JUMP, var(1002), "equal", "__ast0", "ST_EMPTY"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(WRITE, "ST_INITIALIZED", "cell1", "0"),
                        createInstruction(SET, var(1), "ST_INITIALIZED"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1004), "equal", "__ast0", "ST_INITIALIZED"),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(WRITE, "ST_DONE", "cell1", "0"),
                        createInstruction(SET, var(1), "ST_DONE"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(1), "null"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                allocate heap in cell1[0..10]
                                case $state
                                    when ST_EMPTY           $state = ST_INITIALIZED
                                    when ST_INITIALIZED     $state = ST_DONE
                                end
                                """
                        )
                )
        );
    }

    @Test
    void generatesCaseWhenMultiple() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__ast0", "n"),

                        // First alternative
                        createInstruction(JUMP, var(1002), "equal", "__ast0", "1"),
                        createInstruction(JUMP, var(1002), "equal", "__ast0", "2"),
                        createInstruction(JUMP, var(1002), "equal", "__ast0", "3"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(0), "\"Few\""),
                        createInstruction(JUMP, var(1000), "always"),

                        // Second alternative
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1004), "equal", "__ast0", "4"),
                        createInstruction(JUMP, var(1004), "equal", "__ast0", "5"),
                        createInstruction(JUMP, var(1004), "equal", "__ast0", "6"),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(SET, var(0), "\"Several\""),
                        createInstruction(JUMP, var(1000), "always"),

                        /// Else branch
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(0), "\"Many\""),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                case n
                                    when 1, 2, 3    "Few"
                                    when 4,5,6      "Several"
                                    else            "Many"
                                end
                                """
                        )
                )
        );
    }

    @Test
    void generatesCaseWhenMultipleWithRanges() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__ast0", "n"),
                        createInstruction(JUMP, var(1003), "lessThan", "__ast0", "0"),
                        createInstruction(JUMP, var(1002), "lessThanEq", "__ast0", "4"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(JUMP, var(1004), "lessThan", "__ast0", "6"),
                        createInstruction(JUMP, var(1002), "lessThanEq", "__ast0", "8"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(JUMP, var(1002), "equal", "__ast0", "10"),
                        createInstruction(JUMP, var(1002), "equal", "__ast0", "12"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(0), "\"A number I like\""),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(0), "null"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                case n
                                    when 0 .. 4, 6 .. 8, 10, 12
                                        "A number I like"
                                end
                                """
                        )
                )
        );
    }

    @Test
    void generatesEndFromFunctionCall() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "equal", var(0), "some_cond", "false"),
                        createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                        createInstruction(END),
                        createInstruction(SET, var(1), "null"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(1), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("if some_cond == false end() end"))
        );
    }

    @Test
    void generatesModuloOperator() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "mod", var(0), "@tick", "2"),
                        createInstruction(OP, "equal", var(1), var(0), "0"),
                        createInstruction(SET, "running", var(1)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("running = @tick % 2 == 0"))
        );
    }

    @Test
    void generatesMultiParameterControlInstruction() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var(0), "leader", "@shootX"),
                        createInstruction(SENSOR, var(1), "leader", "@shootY"),
                        createInstruction(SENSOR, var(2), "leader", "@shooting"),
                        createInstruction(CONTROL, "shoot", "turret", var(0), var(1), var(2)),
                        createInstruction(CONTROL, "color", "turret", "14", "15", "16"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                turret.shoot(leader.shootX, leader.shootY, leader.shooting)
                                turret.color(14, 15, 16)
                                """
                        )
                )
        );
    }

    @Test
    void canIndirectlyReferenceHeap() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "HEAPPTR", "cell1"),
                        createInstruction(WRITE, "0", "HEAPPTR", "0"),
                        createInstruction(READ, var(0), "HEAPPTR", "1"),
                        createInstruction(READ, var(1), "HEAPPTR", "0"),
                        createInstruction(OP, "add", var(2), var(0), var(1)),
                        createInstruction(WRITE, var(2), "HEAPPTR", "1"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                set HEAPPTR = cell1
                                allocate heap in HEAPPTR[0...4]
                                $dx = 0
                                $dy += $dx
                                """
                        )
                )
        );
    }

    @Test
    void supportsTheSqrtFunction() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "sqrt", var(0), "z"),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("sqrt(z)"))
        );
    }

    @Test
    void supportsLogiclessCaseWhen() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "rand", var(0), "2"),
                        createInstruction(OP, "floor", var(1), var(0)),
                        createInstruction(SET, "__ast0", var(1)),
                        createInstruction(JUMP, var(1002), "equal", "__ast0", "0"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(2), "1000"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1004), "equal", "__ast0", "1"),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(SET, var(2), "null"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(2), "null"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                case floor(rand(2))
                                    when 0
                                        1000
                                    when 1
                                        // no op
                                    end
                                """
                        )
                )
        );
    }

    @Test
    void supportsMinMaxFunctions() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "max", var(0), "y", "2"),
                        createInstruction(OP, "min", var(1), "x", var(0)),
                        createInstruction(SET, "r", var(1)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("r = min(x, max(y, 2))"))
        );
    }

    @Test
    void supportsBitwiseAndOrXorAndShiftLeftOrRight() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "and", var(0), "9842", "x"),
                        createInstruction(OP, "shl", var(1), "z", "4"),
                        createInstruction(OP, "xor", var(2), var(0), var(1)),
                        createInstruction(OP, "shr", var(3), "y", "1"),
                        createInstruction(OP, "or", var(4), var(2), var(3)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("(9842 & x) ^ (z << 4) | y >> 1\n"))
        );
    }

    @Test
    void generatesVectorLengthAndAngleCalls() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "len", var(0), "x", "y"),
                        createInstruction(SET, "length", var(0)),
                        createInstruction(OP, "angle", var(1), "x", "y"),
                        createInstruction(SET, "angle", var(1)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("length = len(x, y)\nangle = angle(x, y)\n")
                )
        );
    }

    @Test
    void generatesLog10Call() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "log10", var(0), "x"),
                        createInstruction(SET, "l", var(0)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("l = log10(x)\n")
                )
        );
    }

    @Test
    void generatesNoiseCall() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "noise", var(0), "4", "8"),
                        createInstruction(SET, "n", var(0)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("n = noise(4, 8)\n")
                )
        );
    }

    @Test
    void generatesIntegerDivision() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "idiv", var(0), "4", "z"),
                        createInstruction(SET, "n", var(0)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("n = 4 \\ z"))
        );
    }

    @Test
    void generatesCorrectCodeWhenCaseBranchIsCommentedOut() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__ast0", "n"),
                        createInstruction(JUMP, var(1002), "equal", "__ast0", "2"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINT, "n"),
                        createInstruction(SET, var(0), "n"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(0), "null"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                case n
                                    // when 1
                                    when 2
                                        print(n)
                                end
                                """
                        )
                )
        );
    }

    @Test
    void generatesCorrectCodeWhenIfExpressionHasCommentedOutSections() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, var(1000), "equal", "n", "false"),
                        createInstruction(SET, var(0), "null"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(0), "1"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1002), "equal", "m", "false"),
                        createInstruction(SET, var(1), "1"),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(1), "null"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                if n
                                    // no op
                                else
                                    1
                                end
                                if m
                                    1
                                else
                                    // 2
                                end
                                """
                        )
                )
        );
    }

    @Test
    void correctlyParsesIndirectPropertyReference() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "resource", "@silicon"),
                        createInstruction(SENSOR, var(0), "vault1", "resource"),
                        createInstruction(SENSOR, var(1), "vault1", "@itemCapacity"),
                        createInstruction(OP, "lessThan", var(2), var(0), var(1)),
                        createInstruction(JUMP, var(1000), "equal", var(2), "false"),
                        createInstruction(SET, "foo", "true"),
                        createInstruction(SET, var(3), "true"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(3), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                resource = @silicon
                                if vault1.sensor(resource) < vault1.itemCapacity
                                    foo = true
                                end
                                """
                        )
                )
        );
    }

    @Test
    void correctlyParsesDirectIndirectPropertyReference() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var(0), "vault1", "@graphite"),
                        createInstruction(SENSOR, var(1), "vault1", "@itemCapacity"),
                        createInstruction(OP, "lessThan", var(2), var(0), var(1)),
                        createInstruction(CONTROL, "enabled", "conveyor1", var(2)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "conveyor1.enabled = vault1.sensor(@graphite) < vault1.sensor(@itemCapacity)"
                        )
                )
        );
    }

    @Test
    void correctlyGeneratesTernaryOperatorLogic() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var(0), "smelter1", "@enabled"),
                        createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                        createInstruction(SET, var(1), "\"true\""),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(1), "\"false\""),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINT, "\"sm.enabled: \""),
                        createInstruction(PRINT, var(1)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                print("sm.enabled: ", smelter1.enabled ? "true" : "false")
                                """
                        )
                )
        );
    }

    @Test
    void correctlyHandlesNestedTernaryOperators() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "greaterThan", var(0), "b", "c"),
                        createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                        createInstruction(SET, var(1), "1"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "greaterThan", var(2), "d", "e"),
                        createInstruction(JUMP, var(1002), "equal", var(2), "false"),
                        createInstruction(SET, var(3), "2"),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(3), "3"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(1), var(3)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, "a", var(1)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "a = (b > c) ? 1 : (d > e) ? 2 : 3"
                        )
                )
        );
    }

    @Test
    void correctlyHandlesUnaryLiteralMinusExpressions() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(SET, "a", "-7"),
                        createInstruction(OP, "mul", "b", "-1", "a"),
                        createInstruction(OP, "mul", "c", "-5", "b"),
                        createInstruction(OP, "mul", var(0), "-1", "c"),
                        createInstruction(OP, "mul", "d", "2", var(0)),
                        createInstruction(OP, "pow", var(1), "d", "2"),
                        createInstruction(OP, "mul", "e", "-1", var(1)),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                a = -7
                                b = -a
                                c = -5 * b
                                d = 2 * -c
                                e = -d ** 2
                                """
                        ),
                        Optimization.INPUT_TEMPS_ELIMINATION, Optimization.OUTPUT_TEMPS_ELIMINATION
                )
        );
    }

    @Test
    void correctlyHandlesTernaryOpPriority() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(OP, "greaterThan", var(0), "b", "c"),
                        createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                        createInstruction(SET, var(1), "b"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(1), "c"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, "a", var(1)),
                        createInstruction(OP, "mul", var(2), "e", "f"),
                        createInstruction(SET, "e", var(2)),
                        createInstruction(OP, "mul", var(3), "-1", var(2)),
                        createInstruction(OP, "add", "d", "d", var(3)),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                a = b > c ? b : c
                                d += -e *= f
                                """
                        ),
                        Optimization.INPUT_TEMPS_ELIMINATION, Optimization.OUTPUT_TEMPS_ELIMINATION
                )
        );
    }

    @Test
    void correctlyHandlesBitwiseOpPriority() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "and", var(0), "c", "d"),
                        createInstruction(OP, "or", "a", "b", var(0)),
                        createInstruction(OP, "not", var(1), "f"),
                        createInstruction(OP, "and", "e", var(1), "g"),
                        createInstruction(OP, "and", var(2), "h", "31"),
                        createInstruction(OP, "equal", "g", var(2), "15"),
                        createInstruction(OP, "and", var(3), "y", "15"),
                        createInstruction(OP, "and", var(4), "z", "7"),
                        createInstruction(OP, "land", "x", var(3), var(4)),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                a = b | c & d
                                e = ~f & g
                                g = h & 31 == 15
                                x = y & 15 and z & 7
                                """
                        ),
                        Optimization.INPUT_TEMPS_ELIMINATION, Optimization.OUTPUT_TEMPS_ELIMINATION
                )
        );
    }

    @Test
    void correctlyHandlesStrictNotEqual() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var(0), "@unit", "@dead"),
                        createInstruction(OP, "strictEqual", var(1), var(0), "null"),
                        createInstruction(OP, "equal", var(2), var(1), "false"),
                        createInstruction(SET, "a", var(2)),
                        createInstruction(PRINT, "a"),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("a = @unit.dead !== null; print(a)"))
        );
    }

    @Test
    void correctlyHandlesBreakContinue() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(JUMP, var(1002), "equal", "a", "false"),
                        createInstruction(JUMP, var(1003), "equal", "b", "false"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(JUMP, var(1004), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(JUMP, var(1005), "equal", "c", "false"),
                        createInstruction(JUMP, var(1002), "always"),
                        createInstruction(JUMP, var(1006), "always"),
                        createInstruction(LABEL, var(1005)),
                        createInstruction(LABEL, var(1006)),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINT, "\"End\""),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                while a
                                    if b
                                        continue
                                    elsif c
                                        break
                                    end
                                end
                                print("End")
                                """
                        ),
                        Optimization.DEAD_CODE_ELIMINATION, Optimization.INPUT_TEMPS_ELIMINATION
                )
        );
    }

    @Test
    void correctlyHandlesNestedLoopBreaks() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(LABEL, var(0)),
                        createInstruction(JUMP, var(1), "equal", "a", "false"),
                        createInstruction(PRINT, "a"),
                        createInstruction(LABEL, var(2)),
                        createInstruction(JUMP, var(3), "equal", "b", "false"),
                        createInstruction(PRINT, "b"),
                        createInstruction(JUMP, var(4), "equal", "c", "false"),
                        createInstruction(PRINT, "c"),
                        createInstruction(JUMP, var(3), "always"),
                        createInstruction(JUMP, var(5), "always"),
                        createInstruction(LABEL, var(4)),
                        createInstruction(LABEL, var(5)),
                        createInstruction(PRINT, "d"),
                        createInstruction(JUMP, var(3), "always"),
                        createInstruction(LABEL, var(10)),
                        createInstruction(JUMP, var(2), "always"),
                        createInstruction(LABEL, var(3)),
                        createInstruction(PRINT, "e"),
                        createInstruction(JUMP, var(1), "always"),
                        createInstruction(LABEL, var(11)),
                        createInstruction(JUMP, var(0), "always"),
                        createInstruction(LABEL, var(1)),
                        createInstruction(PRINT, "f"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                while a
                                    print(a)
                                    while b
                                        print(b)
                                        if c
                                            print(c)
                                            break
                                        end
                                        print(d)
                                        break
                                    end
                                    print(e)
                                    break
                                end
                                print(f)
                                """
                        ),
                        Optimization.DEAD_CODE_ELIMINATION, Optimization.INPUT_TEMPS_ELIMINATION
                )
        );
    }

    @Test
    void refusesBreaksOutsideLoop() {
        assertThrows(GenerationException.class, () ->
                generateUnoptimized(
                        (Seq) translateToAst("""
                                while a
                                    print(a)
                                end
                                break
                                """
                        )
                )
        );
    }

    @Test
    void correctlyHandlesBreakAndContinueWithLabel() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(JUMP, var(1002), "equal", "a", "false"),
                        createInstruction(PRINT, "\"In outer\""),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(JUMP, var(1005), "equal", "b", "false"),
                        createInstruction(PRINT, "\"In inner\""),
                        createInstruction(JUMP, var(1002), "always"),        // break loop Outer
                        createInstruction(PRINT, "\"After break\""),
                        createInstruction(JUMP, var(1004), "always"),        // continue loop Inner
                        createInstruction(LABEL, var(1004)),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1005)),
                        createInstruction(PRINT, "\"After inner\""),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINT, "\"After outer\""),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                Outer:
                                while a
                                    print("In outer")
                                    Inner:
                                    while b
                                        print("In inner")
                                        break Outer
                                        print("After break")
                                        continue Inner
                                    end
                                    print("After inner")
                                end
                                print("After outer")
                                """
                        ),
                        Optimization.DEAD_CODE_ELIMINATION, Optimization.INPUT_TEMPS_ELIMINATION
                )
        );
    }

    @Test
    void generatesDoWhileLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"Blocks:\""),
                        createInstruction(SET, "n", "@links"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "sub", var(0), "n", "1"),
                        createInstruction(SET, "n", var(0)),
                        createInstruction(GETLINK, var(1), "n"),
                        createInstruction(SET, "block", var(1)),
                        createInstruction(PRINT, "\"\\n\""),
                        createInstruction(PRINT, "block"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(OP, "greaterThan", var(2), "n", "0"),
                        createInstruction(JUMP, var(1000), "notEqual", var(2), "false"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "print(\"Blocks:\")\n" +
                                "n = @links\n" +
                                "do\n" +
                                "  n -= 1\n" +
                                "  block = getlink(n)\n" +
                                "  print(\"\\n\", block)\n" +
                                "loop while n > 0\n" +
                                "printflush(message1)\n"
                        )
                )
        );
    }

    @Test
    void generatesDoWhileLoopWithBreakAndContinue() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(PRINT, "\"Blocks:\""),
                        createInstruction(SET, "n", "@links"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "sub", var(2), "n", "1"),
                        createInstruction(SET, "n", var(2)),
                        createInstruction(GETLINK, var(3), "n"),
                        createInstruction(SET, "block", var(3)),
                        createInstruction(SENSOR, var(4), "block", "@type"),
                        createInstruction(JUMP, var(1001), "equal", var(4), "@sorter"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(PRINT, "\"\\n\""),
                        createInstruction(PRINT, "block"),
                        createInstruction(SENSOR, var(8), "block", "@type"),
                        createInstruction(JUMP, var(1002), "equal", var(8), "@unloader"),
                        createInstruction(LABEL, var(1005)),
                        createInstruction(LABEL, var(1006)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "greaterThan", "n", "0"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
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
                                """
                        ),
                        Optimization.DEAD_CODE_ELIMINATION, Optimization.SINGLE_STEP_JUMP_ELIMINATION,
                        Optimization.INPUT_TEMPS_ELIMINATION, Optimization.JUMP_OVER_JUMP_ELIMINATION
                )
        );
    }

    @Test
    void convertsForEachAndPrintFunctionCall() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), var(1003)),
                        createInstruction(SET, "a", "@mono"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(0), var(1004)),
                        createInstruction(SET, "a", "@poly"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(SET, var(0), var(1002)),
                        createInstruction(SET, "a", "@mega"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINT, "a"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(GOTO, var(0)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("for a in (@mono, @poly, @mega)\nprint(a)\nend"))
        );
    }

    @Test
    void refusesAssignmentsToBlockNames() {
        assertThrows(GenerationException.class, () ->
                generateUnoptimized((Seq) translateToAst("switch1 = 5"))
        );
    }

    @Test
    void refusesBlockNamesAsOutputArguments() {
        assertThrows(GenerationException.class, () ->
                generateUnoptimized((Seq) translateToAst("getBlock(10, 20, switch1)"))
        );
    }

    @Test
    void resolvesConstantExpressions() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "a", "3"),
                        createInstruction(SET, "b", "null"),
                        createInstruction(SET, "c", "3"),
                        createInstruction(SET, "d", "b"),
                        createInstruction(SET, "e", "3"),
                        createInstruction(SET, "f", "-2"),
                        createInstruction(SET, "g", "true"),
                        createInstruction(SET, "h", "-5"),
                        createInstruction(SET, "i", "0.0001"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                a = 1 + 2
                                b = 1 / 0
                                c = min(3, 4)
                                d = 1 < 2 ? b : c
                                e = abs(-3)
                                f = ~1
                                g = not false
                                h = -(2 + 3)
                                i = 1 / 10000
                                """
                        )
                )
        );
    }

    @Test
    void resolvesConstantStrictEqual() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "a", "false"),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("a = 0 === null "))
        );
    }
}
