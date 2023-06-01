package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LogicInstructionGeneratorTest extends AbstractGeneratorTest {

    @Test
    void throwsAnOutOfHeapSpaceExceptionWhenUsingMoreHeapSpaceThanAllocated() {
        assertThrows(OutOfHeapSpaceException.class,
                () -> generateInstructions("allocate heap in cell1[0 .. 1]\n$dx = $dy = $dz"));
    }

    @Test
    void convertsComplexAssignment() {
        assertCompilesTo(
                "foo = (bar - 2) * 3",
                createInstruction(OP, "sub", var(0), "bar", "2"),
                createInstruction(OP, "mul", var(1), var(0), "3"),
                createInstruction(SET, "foo", var(1)),
                createInstruction(END)
        );
    }

    @Test
    void convertsWhileLoopAndPrintFunctionCall() {
        assertCompilesTo("""
                        n = 0
                        while n < 5
                            n += 1
                        end
                        print("n: ", n)
                        """,
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
        );
    }

    @Test
    void convertsNullAndUnaryOp() {
        assertCompilesTo("""
                        a = ~a
                        b = !b
                        c = not c
                        x = null
                        """,
                createInstruction(OP, "not", var(0), "a"),
                createInstruction(SET, "a", var(0)),
                createInstruction(OP, "equal", var(1), "b", "false"),
                createInstruction(SET, "b", var(1)),
                createInstruction(OP, "equal", var(2), "c", "false"),
                createInstruction(SET, "c", var(2)),
                createInstruction(SET, "x", "null"),
                createInstruction(END)
        );
    }

    @Test
    void convertsSensorReadings() {
        assertCompilesTo(
                "foundation1.copper < foundation1.itemCapacity",
                createInstruction(SENSOR, var(0), "foundation1", "@copper"),
                createInstruction(SENSOR, var(1), "foundation1", "@itemCapacity"),
                createInstruction(OP, "lessThan", var(2), var(0), var(1)),
                createInstruction(END)
        );
    }

    @Test
    void convertsBooleanOperations() {
        assertCompilesTo("""
                        while z != true
                            print("infinite loop!")
                        end
                        printflush(message1)
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "notEqual", var(0), "z", "true"),
                createInstruction(JUMP, var(1002), "equal", var(0), "false"),
                createInstruction(PRINT, "\"infinite loop!\""),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINTFLUSH, "message1"),
                createInstruction(END)
        );
    }

    @Test
    void convertsControlStatements() {
        assertCompilesTo(
                "conveyor1.enabled = foundation1.copper === tank1.water",
                createInstruction(SENSOR, var(1), "foundation1", "@copper"),
                createInstruction(SENSOR, var(2), "tank1", "@water"),
                createInstruction(OP, "strictEqual", var(3), var(1), var(2)),
                createInstruction(CONTROL, "enabled", "conveyor1", var(3)),
                createInstruction(END)
        );
    }

    @Test
    void convertsHeapAccesses() {
        assertCompilesTo(
                "cell1[3] = cell2[4] + conveyor1.enabled",
                createInstruction(READ, var(0), "cell2", "4"),
                createInstruction(SENSOR, var(1), "conveyor1", "@enabled"),
                createInstruction(OP, "add", var(2), var(0), var(1)),
                createInstruction(WRITE, var(2), "cell1", "3"),
                createInstruction(END)
        );
    }

    @Test
    void convertsIfExpression() {
        assertCompilesTo("""
                        value = if cell1[4] == 0
                            false
                        else
                            cell1[4] = true
                            n += 1
                        end
                        """,
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
        );
    }

    @Test
    void convertsMainMemoryVariable() {
        assertCompilesTo(
                "memory = cell1 memory[0] = rand(9**9)",
                createInstruction(SET, "memory", "cell1"),
                createInstruction(OP, "rand", var(0), "387420489"),
                createInstruction(WRITE, var(0), "memory", "0"),
                createInstruction(END)
        );
    }

    @Test
    void convertsFunctionsReturningValues() {
        assertCompilesTo(
                "z = 9; cell1[0] = rand(z**9)",
                createInstruction(SET, "z", "9"),
                createInstruction(OP, "pow", var(0), "z", "9"),
                createInstruction(OP, "rand", var(1), var(0)),
                createInstruction(WRITE, var(1), "cell1", "0"),
                createInstruction(END)
        );
    }

    @Test
    void convertsUbindAndControl() {
        assertCompilesTo("""
                        while @unit === null
                            ubind(@poly)
                            move(10, 10)
                        end
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "strictEqual", var(0), "@unit", "null"),
                createInstruction(JUMP, var(1001), "equal", var(0), "false"),
                createInstruction(UBIND, "@poly"),
                createInstruction(UCONTROL, "move", "10", "10"),
                createInstruction(LABEL, var(1010)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void convertsRealLifeTest1() {
        assertCompilesTo("""
                        n = 0
                        while (reactor = getlink(n)) != null
                            if reactor.liquidCapacity > 0
                                pct_avail = reactor.cryofluid / reactor.liquidCapacity
                                reactor.enabled = pct_avail >= 0.25
                            end
                            n += 1
                        end
                        """,
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
        );
    }

    @Test
    void convertsUnaryMinus() {
        assertCompilesTo(
                "dx *= -1; dy = -1; dz = dy - 1",
                createInstruction(OP, "mul", var(0), "dx", "-1"),
                createInstruction(SET, "dx", var(0)),
                createInstruction(SET, "dy", "-1"),
                createInstruction(OP, "sub", var(1), "dy", "1"),
                createInstruction(SET, "dz", var(1)),
                createInstruction(END)
        );
    }

    @Test
    void removesCommentsFromLogicInstructions() {
        assertCompilesTo("""
                        // Remember that we initialized ourselves
                        a = 1
                        """,
                createInstruction(SET, "a", "1"),
                createInstruction(END)
        );
    }

    @Test
    void generatesRefsWithDashInThem() {
        assertCompilesTo("""
                        build(x, y, @titanium-conveyor, 1, 0)
                        b_building = getBlock(x, y, b_type, b_floor)
                        if b_type == @titanium-conveyor
                            n += 1
                        end
                        """,
                createInstruction(UCONTROL, "build", "x", "y", "@titanium-conveyor", "1", "0"),
                createInstruction(UCONTROL, "getBlock", "x", "y", "b_type", var(0), "b_floor"),
                createInstruction(SET, "b_building", var(0)),
                createInstruction(OP, "equal", var(1), "b_type", "@titanium-conveyor"),
                createInstruction(JUMP, var(1000), "equal", var(1), "false"),
                createInstruction(OP, "add", var(3), "n", "1"),
                createInstruction(SET, "n", var(3)),
                createInstruction(SET, var(2), var(3)),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, var(2), "null"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void generatesComplexMathExpression() {
        assertCompilesTo(
                "x = ceil(floor(sin(log(cos(abs(tan(rand(1))))))))",
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
        );
    }

    @Test
    void parsesInclusiveIteratorStyleLoop() {
        assertCompilesTo(
                "for n in 1 .. 17 print(n) end",
                createInstruction(SET, "n", "1"),
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "greaterThan", "n", "17"),
                createInstruction(PRINT, "n"),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "add", "n", "n", "1"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertCompilesTo(
                "for n in 1 ... 17 print(n) end",
                // init
                createInstruction(SET, "n", "1"),

                // cond
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "greaterThanEq", "n", "17"),

                // loop body
                createInstruction(PRINT, "n"),

                // continue label
                createInstruction(LABEL, var(1001)),

                // increment
                createInstruction(OP, "add", "n", "n", "1"),

                // loop
                createInstruction(JUMP, var(1000), "always"),

                // trailer
                createInstruction(LABEL, var(1002)),

                // rest of program
                createInstruction(END)
        );
    }

    @Test
    void parsesRangeExpressionLoop() {
        assertCompilesTo(
                "for n in a ... b print(n) end",
                // init
                createInstruction(SET, var(0), "b"),
                createInstruction(SET, "n", "a"),

                // cond
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "greaterThanEq", "n", var(0)),

                // loop body
                createInstruction(PRINT, "n"),

                // continue label
                createInstruction(LABEL, var(1001)),

                // increment
                createInstruction(OP, "add", "n", "n", "1"),

                // loop
                createInstruction(JUMP, var(1000), "always"),

                // trailer
                createInstruction(LABEL, var(1002)),

                // rest of program
                createInstruction(END)
        );
    }

    @Test
    void generatesCStyleComplexForLoop() {
        assertCompilesTo("""
                        for i = 0, j = -5; i < 5; j -= 1, i += 1
                            print(n)
                        end
                        """,
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
        );
    }

    @Test
    void supportsAssigningAssignmentResults() {
        assertCompilesTo(
                "a = b = 42",
                createInstruction(SET, "b", "42"),
                createInstruction(SET, "a", "42"),
                createInstruction(END)
        );
    }

    @Test
    void generatesCaseWhenElse() {
        assertCompilesTo("""
                        case n
                            when 1      "1"
                            when 2      "two"
                            else        "otherwise"
                        end
                        """,
                createInstruction(SET, "__ast0", "n"),
                createInstruction(JUMP, var(1002), "equal", "__ast0", "1"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(0), q("1")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1004), "equal", "__ast0", "2"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, var(0), q("two")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(0), q("otherwise")),
                createInstruction(LABEL, var(1000)),
                createInstruction(END)
        );
    }

    @Test
    void generatesCaseWhen() {
        assertCompilesTo("""
                        allocate heap in cell1[0..10]
                        case $state
                            when ST_EMPTY           $state = ST_INITIALIZED
                            when ST_INITIALIZED     $state = ST_DONE
                        end
                        """,
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
        );
    }

    @Test
    void generatesCaseWhenMultiple() {
        assertCompilesTo("""
                        case n
                            when 1, 2, 3    "Few"
                            when 4,5,6      "Several"
                            else            "Many"
                        end
                        """,
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
        );
    }

    @Test
    void generatesCaseWhenMultipleWithRanges() {
        assertCompilesTo("""
                        case n
                            when 0 .. 4, 6 .. 8, 10, 12
                                "A number I like"
                        end
                        """,
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
        );
    }

    @Test
    void generatesEndFromFunctionCall() {
        assertCompilesTo(
                "if some_cond == false end() end",
                createInstruction(OP, "equal", var(0), "some_cond", "false"),
                createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                createInstruction(END),
                createInstruction(SET, var(1), "null"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, var(1), "null"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void generatesModuloOperator() {
        assertCompilesTo(
                "running = @tick % 2 == 0",
                createInstruction(OP, "mod", var(0), "@tick", "2"),
                createInstruction(OP, "equal", var(1), var(0), "0"),
                createInstruction(SET, "running", var(1)),
                createInstruction(END)
        );
    }

    @Test
    void generatesMultiParameterControlInstruction() {
        assertCompilesTo("""
                        turret.shoot(leader.shootX, leader.shootY, leader.shooting)
                        turret.color(14, 15, 16)
                        """,
                createInstruction(SENSOR, var(0), "leader", "@shootX"),
                createInstruction(SENSOR, var(1), "leader", "@shootY"),
                createInstruction(SENSOR, var(2), "leader", "@shooting"),
                createInstruction(CONTROL, "shoot", "turret", var(0), var(1), var(2)),
                createInstruction(CONTROL, "color", "turret", "14", "15", "16"),
                createInstruction(END)
        );
    }

    @Test
    void canIndirectlyReferenceHeap() {
        assertCompilesTo("""
                        set HEAPPTR = cell1
                        allocate heap in HEAPPTR[0...4]
                        $dx = 0
                        $dy += $dx
                        """,
                createInstruction(SET, "HEAPPTR", "cell1"),
                createInstruction(WRITE, "0", "HEAPPTR", "0"),
                createInstruction(READ, var(0), "HEAPPTR", "1"),
                createInstruction(READ, var(1), "HEAPPTR", "0"),
                createInstruction(OP, "add", var(2), var(0), var(1)),
                createInstruction(WRITE, var(2), "HEAPPTR", "1"),
                createInstruction(END)
        );
    }

    @Test
    void supportsTheSqrtFunction() {
        assertCompilesTo(
                "sqrt(z)",
                createInstruction(OP, "sqrt", var(0), "z"),
                createInstruction(END)
        );
    }

    @Test
    void supportsNoOpCaseWhen() {
        assertCompilesTo("""
                        case floor(rand(2))
                            when 0
                                1000
                            when 1
                                // no op
                            end
                        """,
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
        );
    }

    @Test
    void supportsMinMaxFunctions() {
        assertCompilesTo(
                "r = min(x, max(y, 2))",
                createInstruction(OP, "max", var(0), "y", "2"),
                createInstruction(OP, "min", var(1), "x", var(0)),
                createInstruction(SET, "r", var(1)),
                createInstruction(END)
        );
    }

    @Test
    void supportsBitwiseAndOrXorAndShiftLeftOrRight() {
        assertCompilesTo(
                "(9842 & x) ^ (z << 4) | y >> 1\n",
                createInstruction(OP, "and", var(0), "9842", "x"),
                createInstruction(OP, "shl", var(1), "z", "4"),
                createInstruction(OP, "xor", var(2), var(0), var(1)),
                createInstruction(OP, "shr", var(3), "y", "1"),
                createInstruction(OP, "or", var(4), var(2), var(3)),
                createInstruction(END)
        );
    }

    @Test
    void generatesVectorLengthAndAngleCalls() {
        assertCompilesTo(
                "length = len(x, y)\nangle = angle(x, y)\n",
                createInstruction(OP, "len", var(0), "x", "y"),
                createInstruction(SET, "length", var(0)),
                createInstruction(OP, "angle", var(1), "x", "y"),
                createInstruction(SET, "angle", var(1)),
                createInstruction(END)
        );
    }

    @Test
    void generatesLog10Call() {
        assertCompilesTo(
                "l = log10(x)",
                createInstruction(OP, "log10", var(0), "x"),
                createInstruction(SET, "l", var(0)),
                createInstruction(END)
        );
    }

    @Test
    void generatesNoiseCall() {
        assertCompilesTo(
                "n = noise(4, 8)",
                createInstruction(OP, "noise", var(0), "4", "8"),
                createInstruction(SET, "n", var(0)),
                createInstruction(END)
        );
    }

    @Test
    void generatesIntegerDivision() {
        assertCompilesTo(
                "n = 4 \\ z",
                createInstruction(OP, "idiv", var(0), "4", "z"),
                createInstruction(SET, "n", var(0)),
                createInstruction(END)
        );
    }

    @Test
    void generatesCorrectCodeWhenCaseBranchIsCommentedOut() {
        assertCompilesTo("""
                        case n
                            // when 1
                            when 2
                                print(n)
                        end
                        """,
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
        );
    }

    @Test
    void generatesCorrectCodeWhenIfExpressionHasCommentedOutSections() {
        assertCompilesTo("""
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
                        """,
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
        );
    }

    @Test
    void correctlyParsesIndirectPropertyReference() {
        assertCompilesTo("""
                        resource = @silicon
                        if vault1.sensor(resource) < vault1.itemCapacity
                            foo = true
                        end
                        """,
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
        );
    }

    @Test
    void correctlyParsesDirectIndirectPropertyReference() {
        assertCompilesTo(
                "conveyor1.enabled = vault1.sensor(@graphite) < vault1.sensor(@itemCapacity)",
                createInstruction(SENSOR, var(0), "vault1", "@graphite"),
                createInstruction(SENSOR, var(1), "vault1", "@itemCapacity"),
                createInstruction(OP, "lessThan", var(2), var(0), var(1)),
                createInstruction(CONTROL, "enabled", "conveyor1", var(2)),
                createInstruction(END)
        );
    }

    @Test
    void correctlyGeneratesTernaryOperatorLogic() {
        assertCompilesTo("""
                        print("sm.enabled: ", smelter1.enabled ? "true" : "false")
                        """,
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
        );
    }

    @Test
    void correctlyHandlesNestedTernaryOperators() {
        assertCompilesTo(
                "a = (b > c) ? 1 : (d > e) ? 2 : 3",
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
        );
    }

    @Test
    void correctlyHandlesUnaryLiteralMinusExpressions() {
        assertCompilesTo("""
                        a = -7
                        b = -a
                        c = -5 * b
                        d = 2 * -c
                        e = -d ** 2
                        """,
                createInstruction(SET, "a", "-7"),
                createInstruction(OP, "mul", var(0), "-1", "a"),
                createInstruction(SET, "b", var(0)),
                createInstruction(OP, "mul", var(1), "-5", "b"),
                createInstruction(SET, "c", var(1)),
                createInstruction(OP, "mul", var(2), "-1", "c"),
                createInstruction(OP, "mul", var(3), "2", var(2)),
                createInstruction(SET, "d", var(3)),
                createInstruction(OP, "pow", var(4), "d", "2"),
                createInstruction(OP, "mul", var(5), "-1", var(4)),
                createInstruction(SET, "e", var(5)),
                createInstruction(END)
        );
    }

    @Test
    void correctlyHandlesTernaryOpPriority() {
        assertCompilesTo("""
                        a = b > c ? b : c
                        d += -e *= f
                        """,
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
                createInstruction(OP, "add", var(4), "d", var(3)),
                createInstruction(SET, "d", var(4)),
                createInstruction(END)
        );
    }

    @Test
    void correctlyHandlesBitwiseOpPriority() {
        assertCompilesTo("""
                        a = b | c & d
                        e = ~f & g
                        g = h & 31 == 15
                        x = y & 15 and z & 7
                        """,
                createInstruction(OP, "and", var(0), "c", "d"),
                createInstruction(OP, "or", var(1), "b", var(0)),
                createInstruction(SET, "a", var(1)),
                createInstruction(OP, "not", var(2), "f"),
                createInstruction(OP, "and", var(3), var(2), "g"),
                createInstruction(SET, "e", var(3)),
                createInstruction(OP, "and", var(4), "h", "31"),
                createInstruction(OP, "equal", var(5), var(4), "15"),
                createInstruction(SET, "g", var(5)),
                createInstruction(OP, "and", var(6), "y", "15"),
                createInstruction(OP, "and", var(7), "z", "7"),
                createInstruction(OP, "land", var(8), var(6), var(7)),
                createInstruction(SET, "x", var(8)),
                createInstruction(END)
        );
    }

    @Test
    void correctlyHandlesStrictNotEqual() {
        assertCompilesTo(
                "a = @unit.dead !== null; print(a)",
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(OP, "strictEqual", var(1), var(0), "null"),
                createInstruction(OP, "equal", var(2), var(1), "false"),
                createInstruction(SET, "a", var(2)),
                createInstruction(PRINT, "a"),
                createInstruction(END)
        );
    }

    @Test
    void correctlyHandlesBreakContinue() {
        assertCompilesTo("""
                        while a
                            if b
                                continue
                            elsif c
                                break
                            end
                        end
                        print("End")
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "equal", "a", "false"),
                createInstruction(JUMP, var(1003), "equal", "b", "false"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(SET, var(0), "null"),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1005), "equal", "c", "false"),
                createInstruction(JUMP, var(1002), "always"),
                createInstruction(SET, var(1), "null"),
                createInstruction(JUMP, var(1006), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(SET, var(1), "null"),
                createInstruction(LABEL, var(1006)),
                createInstruction(SET, var(0), var(1)),
                createInstruction(LABEL, var(1004)),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("End")),
                createInstruction(END)
        );
    }

    @Test
    void correctlyHandlesNestedLoopBreaks() {
        assertCompilesTo("""
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
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "equal", "a", "false"),
                createInstruction(PRINT, "a"),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1005), "equal", "b", "false"),
                createInstruction(PRINT, "b"),
                createInstruction(JUMP, var(1006), "equal", "c", "false"),
                createInstruction(PRINT, "c"),
                createInstruction(JUMP, var(1005), "always"),
                createInstruction(SET, var(0), "null"),
                createInstruction(JUMP, var(1007), "always"),
                createInstruction(LABEL, var(1006)),
                createInstruction(SET, var(0), "null"),
                createInstruction(LABEL, var(1007)),
                createInstruction(PRINT, "d"),
                createInstruction(JUMP, var(1005), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(PRINT, "e"),
                createInstruction(JUMP, var(1002), "always"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, "f"),
                createInstruction(END)
        );
    }

    @Test
    void refusesBreaksOutsideLoop() {
        assertThrows(GenerationException.class, () ->
                generateInstructions("""
                        while a
                            print(a)
                        end
                        break
                        """
                )
        );
    }

    @Test
    void correctlyHandlesBreakAndContinueWithLabel() {
        assertCompilesTo("""
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
                        """,
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
        );
    }

    @Test
    void generatesDoWhileLoop() {
        assertCompilesTo("""
                        print("Blocks:")
                        n = @links
                        do
                          n -= 1
                          block = getlink(n)
                          print("\\n", block)
                        loop while n > 0
                        printflush(message1)
                        """,
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
        );
    }

    @Test
    void convertsForEachAndPrintFunctionCall() {
        assertCompilesTo("""
                        for a in (@mono, @poly, @mega)
                            print(a)
                        end
                        """,
                createInstruction(SETADDR, var(0), var(1003)),
                createInstruction(SET, "a", "@mono"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(GOTOLABEL, var(1003), "marker0"),
                createInstruction(SETADDR, var(0), var(1004)),
                createInstruction(SET, "a", "@poly"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(GOTOLABEL, var(1004), "marker0"),
                createInstruction(SETADDR, var(0), var(1005)),
                createInstruction(SET, "a", "@mega"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "a"),
                createInstruction(LABEL, var(1000)),
                createInstruction(GOTO, var(0), "marker0"),
                createInstruction(GOTOLABEL, var(1005), "marker0"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void refusesAssignmentsToBlockNames() {
        assertThrows(GenerationException.class,
                () -> generateInstructions("switch1 = 5"));
    }

    @Test
    void refusesBlockNamesAsOutputArguments() {
        assertThrows(GenerationException.class,
                () -> generateInstructions("getBlock(10, 20, switch1)"));
    }

    @Test
    void refusesBlockNamesAsFunctionParameters() {
        assertThrows(GenerationException.class,
                () -> generateInstructions("def foo(switch1) false end foo(5)"));
    }

    @Test
    void resolvesConstantExpressions() {
        assertCompilesTo("""
                        a = 1 + 2
                        b = 1 / 0
                        c = min(3, 4)
                        d = 1 < 2 ? b : c
                        e = abs(-3)
                        f = ~1
                        g = not false
                        h = -(2 + 3)
                        i = 1 / 10000
                        """,
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
        );
    }

    @Test
    void resolvesConstantStrictEqual() {
        assertCompilesTo(
                "a = 0 === null ",
                createInstruction(SET, "a", "false"),
                createInstruction(END)
        );
    }
}
