package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LogicInstructionGeneratorTest extends AbstractAstTest {
    @Test
    void convertsComplexAssignment() {
        assertEquals(
                List.of(
                        new LogicInstruction("set", "tmp0", "2"),
                        new LogicInstruction("op", "sub", "tmp1", "bar", "tmp0"),
                        new LogicInstruction("set", "tmp2", "3"),
                        new LogicInstruction("op", "mul", "tmp3", "tmp1", "tmp2"),
                        new LogicInstruction("set", "foo", "tmp3"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom((Seq) translateToAst("foo = (bar - 2) * 3"))
        );
    }

    @Test
    void convertsWhileLoopAndPrintFunctionCall() {
        assertEquals(
                List.of(
                        new LogicInstruction("set", "tmp0", "0"),
                        new LogicInstruction("set", "n", "tmp0"),
                        new LogicInstruction("label", "label0"),
                        new LogicInstruction("set", "tmp1", "5"),
                        new LogicInstruction("op", "lessThan", "tmp2", "n", "tmp1"),
                        new LogicInstruction("jump", "label1", "notEqual", "tmp2", "true"),
                        new LogicInstruction("set", "tmp3", "1"),
                        new LogicInstruction("op", "add", "tmp4", "n", "tmp3"),
                        new LogicInstruction("set", "n", "tmp4"),
                        new LogicInstruction("jump", "label0", "always"),
                        new LogicInstruction("label", "label1"),
                        new LogicInstruction("set", "tmp5", "\"n: \""),
                        new LogicInstruction("print", "tmp5"),
                        new LogicInstruction("print", "n"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom((Seq) translateToAst("n = 0\nwhile n < 5\nn += 1\nend\nprint(\"n: \", n)"))
        );
    }

    @Test
    void convertsNullAndUnaryOp() {
        assertEquals(
                List.of(
                        new LogicInstruction("op", "not", "tmp0", "n"),
                        new LogicInstruction("set", "n", "tmp0"),
                        new LogicInstruction("set", "x", "null"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom((Seq) translateToAst("n = not n\nx = null"))
        );
    }

    @Test
    void convertsSensorReadings() {
        assertEquals(
                List.of(
                        new LogicInstruction("sensor", "tmp0", "foundation1", "@copper"),
                        new LogicInstruction("sensor", "tmp1", "foundation1", "@itemCapacity"),
                        new LogicInstruction("op", "lessThan", "tmp2", "tmp0", "tmp1"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom((Seq) translateToAst("foundation1.copper < foundation1.itemCapacity"))
        );
    }

    @Test
    void convertsBooleanOperations() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("op", "notEqual", "tmp0", "true", "false"),
                                new LogicInstruction("jump", "label1", "notEqual", "tmp0", "true"),
                                new LogicInstruction("set", "tmp1", "\"infinite loop!\""),
                                new LogicInstruction("print", "tmp1"),
                                new LogicInstruction("jump", "label0", "always"),
                                new LogicInstruction("label", "label1"),
                                new LogicInstruction("printflush", "message1"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("while true != false\nprint(\"infinite loop!\")\nend\nprintflush(message1)\n")
                        )
                )
        );
    }

    @Test
    void convertsControlStatements() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("sensor", "tmp1", "foundation1", "@copper"),
                                new LogicInstruction("sensor", "tmp2", "tank1", "@water"),
                                new LogicInstruction("op", "strictEqual", "tmp3", "tmp1", "tmp2"),
                                new LogicInstruction("control", "enabled", "conveyor1", "tmp3"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("conveyor1.enabled = foundation1.copper === tank1.water")
                        )
                )
        );
    }

    @Test
    void convertsHeapAccesses() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp2", "4"),
                                new LogicInstruction("read", "tmp3", "cell2", "tmp2"),
                                new LogicInstruction("sensor", "tmp4", "conveyor1", "@enabled"),
                                new LogicInstruction("op", "add", "tmp5", "tmp3", "tmp4"),
                                new LogicInstruction("set", "tmp6", "3"),
                                new LogicInstruction("write", "tmp5", "cell1", "tmp6"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("cell1[3] = cell2[4] + conveyor1.enabled")
                        )
                )
        );
    }

    @Test
    void convertsIfExpression() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "4"),
                                new LogicInstruction("read", "tmp1", "cell1", "tmp0"),
                                new LogicInstruction("set", "tmp2", "0"),
                                new LogicInstruction("op", "equal", "tmp3", "tmp1", "tmp2"),
                                new LogicInstruction("jump", "label0", "notEqual", "tmp3", "true"),
                                new LogicInstruction("set", "tmp9", "false"),
                                new LogicInstruction("jump", "label1", "always"),
                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("set", "tmp6", "4"),
                                new LogicInstruction("write", "true", "cell1", "tmp6"),
                                new LogicInstruction("set", "tmp7", "1"),
                                new LogicInstruction("op", "add", "tmp8", "n", "tmp7"),
                                new LogicInstruction("set", "n", "tmp8"),
                                new LogicInstruction("set", "tmp9", "tmp8"),
                                new LogicInstruction("label", "label1"),
                                new LogicInstruction("set", "value", "tmp9"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst(
                                        "value = if cell1[4] == 0\nfalse\nelse\ncell1[4] = true\nn += 1\nend\n"
                                )
                        )
                )
        );
    }

    @Test
    void convertsFunctionsReturningValues() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp2", "9"),
                                new LogicInstruction("set", "tmp3", "9"),
                                new LogicInstruction("op", "pow", "tmp4", "tmp2", "tmp3"),
                                new LogicInstruction("op", "rand", "tmp5", "tmp4"),
                                new LogicInstruction("set", "tmp6", "0"),
                                new LogicInstruction("write", "tmp5", "cell1", "tmp6"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(LogicInstructionGenerator.generateFrom((Seq) translateToAst("cell1[0] = rand(9**9)")))
        );
    }

    @Test
    void convertsUbindAndControl() {
        assertEquals(
                List.of(
                        new LogicInstruction("label", "label0"),
                        new LogicInstruction("op", "strictEqual", "tmp0", "@unit", "null"),
                        new LogicInstruction("jump", "label1", "notEqual", "tmp0", "true"),
                        new LogicInstruction("ubind", "@poly"),
                        new LogicInstruction("jump", "label0", "always"),
                        new LogicInstruction("label", "label1"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst(
                                "while @unit === null\nubind(@poly)\nend\n")
                )
        );

    }

    @Test
    void convertsReallifeTest1() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "0"),
                                new LogicInstruction("set", "n", "tmp0"),
                                new LogicInstruction("label", "label2"),
                                new LogicInstruction("getlink", "tmp1", "n"),
                                new LogicInstruction("set", "reactor", "tmp1"),
                                new LogicInstruction("op", "notEqual", "tmp2", "tmp1", "null"),
                                new LogicInstruction("jump", "label3", "notEqual", "tmp2", "true"),
                                new LogicInstruction("sensor", "tmp3", "reactor", "@liquidCapacity"),
                                new LogicInstruction("set", "tmp4", "0"),
                                new LogicInstruction("op", "greaterThan", "tmp5", "tmp3", "tmp4"),
                                new LogicInstruction("jump", "label0", "notEqual", "tmp5", "true"),
                                new LogicInstruction("sensor", "tmp6", "reactor", "@cryofluid"),
                                new LogicInstruction("sensor", "tmp7", "reactor", "@liquidCapacity"),
                                new LogicInstruction("op", "div", "tmp8", "tmp6", "tmp7"),
                                new LogicInstruction("set", "pct_avail", "tmp8"),
                                new LogicInstruction("set", "tmp10", "0.25"),
                                new LogicInstruction("op", "greaterThanEq", "tmp11", "pct_avail", "tmp10"),
                                new LogicInstruction("control", "enabled", "reactor", "tmp11"),
                                new LogicInstruction("set", "tmp12", "tmp11"),
                                new LogicInstruction("jump", "label1", "always"),
                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("set", "tmp12", "null"),
                                new LogicInstruction("label", "label1"),
                                new LogicInstruction("set", "tmp13", "1"),
                                new LogicInstruction("op", "add", "tmp14", "n", "tmp13"),
                                new LogicInstruction("set", "n", "tmp14"),
                                new LogicInstruction("jump", "label2", "always"),
                                new LogicInstruction("label", "label3"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst(
                                "" +
                                        "n = 0\n" +
                                        "\n" +
                                        "while (reactor = getlink(n)) != null\n" +
                                        "  if reactor.liquidCapacity > 0\n" +
                                        "    pct_avail = reactor.cryofluid / reactor.liquidCapacity\n" +
                                        "    reactor.enabled = pct_avail >= 0.25\n" +
                                        "  end\n" +
                                        "\n" +
                                        "  n += 1\n" +
                                        "end\n" +
                                        "")
                        )
                )
        );
    }

    @Test
    void convertsUnaryMinus() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "-1"),
                                new LogicInstruction("op", "mul", "tmp1", "dx", "tmp0"),
                                new LogicInstruction("set", "dx", "tmp1"),
                                new LogicInstruction("set", "tmp2", "-1"),
                                new LogicInstruction("set", "dy", "tmp2"),
                                new LogicInstruction("set", "tmp3", "2"),
                                new LogicInstruction("set", "tmp4", "1"),
                                new LogicInstruction("op", "sub", "tmp5", "tmp3", "tmp4"),
                                new LogicInstruction("set", "dz", "tmp5"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst(
                                        "dx *= -1\ndy = -1\ndz = 2 - 1"
                                )
                        )
                )
        );
    }

    @Test
    void removesCommentsFromLogicInstructions() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "1"),
                                new LogicInstruction("set", "a", "tmp0"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst(
                                        "// Remember that we initialized ourselves\n\na = 1"
                                )
                        )
                )
        );
    }

    @Test
    void generatesRefsWithDashInThem() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "1"),
                                new LogicInstruction("set", "tmp1", "0"),
                                new LogicInstruction("ucontrol", "build", "x", "y", "@titanium-conveyor", "tmp0", "tmp1"),
                                new LogicInstruction("ucontrol", "getBlock", "x", "y", "b_type", "b_building"),
                                new LogicInstruction("op", "equal", "tmp2", "b_type", "@titanium-conveyor"),
                                new LogicInstruction("jump", "label0", "notEqual", "tmp2", "true"),
                                new LogicInstruction("set", "tmp3", "1"),
                                new LogicInstruction("op", "add", "tmp4", "n", "tmp3"),
                                new LogicInstruction("set", "n", "tmp4"),
                                new LogicInstruction("set", "tmp5", "tmp4"),
                                new LogicInstruction("jump", "label1", "always"),
                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("set", "tmp5", "null"),
                                new LogicInstruction("label", "label1"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst(
                                        "build(x, y, @titanium-conveyor, 1, 0)\ngetBlock(x, y, b_type, b_building)\nif b_type == @titanium-conveyor\nn += 1\nend\n"
                                )
                        )
                )
        );
    }

    @Test
    void generatesComplexMathExpression() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "1"),
                                new LogicInstruction("op", "rand", "tmp1", "tmp0"),
                                new LogicInstruction("op", "tan", "tmp2", "tmp1"),
                                new LogicInstruction("op", "abs", "tmp3", "tmp2"),
                                new LogicInstruction("op", "cos", "tmp4", "tmp3"),
                                new LogicInstruction("op", "log", "tmp5", "tmp4"),
                                new LogicInstruction("op", "sin", "tmp6", "tmp5"),
                                new LogicInstruction("op", "floor", "tmp7", "tmp6"),
                                new LogicInstruction("op", "ceil", "tmp8", "tmp7"),
                                new LogicInstruction("set", "x", "tmp8"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("x = ceil(floor(sin(log(cos(abs(tan(rand(1))))))))")
                        )
                )
        );
    }


    @Test
    void parsesInclusiveIteratorStyleLoop() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "1"),
                                new LogicInstruction("set", "n", "tmp0"),
                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("set", "tmp1", "17"),
                                new LogicInstruction("op", "lessThanEq", "tmp2", "n", "tmp1"),
                                new LogicInstruction("jump", "label1", "notEqual", "tmp2", "true"),
                                new LogicInstruction("print", "n"),
                                new LogicInstruction("set", "tmp3", "1"),
                                new LogicInstruction("op", "add", "tmp4", "n", "tmp3"),
                                new LogicInstruction("set", "n", "tmp4"),
                                new LogicInstruction("jump", "label0", "always"),
                                new LogicInstruction("label", "label1"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("for n in 1 .. 17\nprint(n)\nend")
                        )
                )
        );
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertEquals(
                prettyPrint(
                        List.of(
                                // init
                                new LogicInstruction("set", "tmp0", "1"),
                                new LogicInstruction("set", "n", "tmp0"),

                                // cond
                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("set", "tmp1", "17"),
                                new LogicInstruction("op", "lessThan", "tmp2", "n", "tmp1"),
                                new LogicInstruction("jump", "label1", "notEqual", "tmp2", "true"),

                                // loop body
                                new LogicInstruction("print", "n"),

                                // increment
                                new LogicInstruction("set", "tmp3", "1"),
                                new LogicInstruction("op", "add", "tmp4", "n", "tmp3"),
                                new LogicInstruction("set", "n", "tmp4"),

                                // loop
                                new LogicInstruction("jump", "label0", "always"),

                                // trailer
                                new LogicInstruction("label", "label1"),

                                // rest of program
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("for n in 1 ... 17\nprint(n)\nend\n")
                        )
                )
        );
    }

    @Test
    void generatesCStyleComplexForLoop() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "0"),
                                new LogicInstruction("set", "i", "tmp0"),
                                new LogicInstruction("set", "tmp1", "-5"),
                                new LogicInstruction("set", "j", "tmp1"),

                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("set", "tmp2", "5"),
                                new LogicInstruction("op", "lessThan", "tmp3", "i", "tmp2"),
                                new LogicInstruction("jump", "label1", "notEqual", "tmp3", "true"),
                                new LogicInstruction("print", "n"),

                                new LogicInstruction("set", "tmp4", "1"),
                                new LogicInstruction("op", "sub", "tmp5", "j", "tmp4"),
                                new LogicInstruction("set", "j", "tmp5"),

                                new LogicInstruction("set", "tmp6", "1"),
                                new LogicInstruction("op", "add", "tmp7", "i", "tmp6"),
                                new LogicInstruction("set", "i", "tmp7"),

                                new LogicInstruction("jump", "label0", "always"),
                                new LogicInstruction("label", "label1"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("for i = 0, j = -5; i < 5; j -= 1, i += 1\nprint(n)\nend\n")
                        )
                )
        );
    }

    @Test
    void supportsAssigningAssignmentResults() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "42"),
                                new LogicInstruction("set", "b", "tmp0"),
                                new LogicInstruction("set", "a", "tmp0"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("a = b = 42")
                        )
                )
        );
    }

    @Test
    void generatesCaseWhenElse() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "ast0", "n"),
                                new LogicInstruction("set", "tmp0", "ast0"),
                                new LogicInstruction("set", "tmp2", "1"),
                                new LogicInstruction("jump", "label1", "notEqual", "tmp0", "tmp2"),
                                new LogicInstruction("set", "tmp3", "\"1\""),
                                new LogicInstruction("set", "tmp1", "tmp3"),
                                new LogicInstruction("jump", "label0", "always"),
                                new LogicInstruction("label", "label1"),
                                new LogicInstruction("set", "tmp4", "2"),
                                new LogicInstruction("jump", "label2", "notEqual", "tmp0", "tmp4"),
                                new LogicInstruction("set", "tmp5", "\"two\""),
                                new LogicInstruction("set", "tmp1", "tmp5"),
                                new LogicInstruction("jump", "label0", "always"),
                                new LogicInstruction("label", "label2"),
                                new LogicInstruction("set", "tmp6", "\"otherwise\""),
                                new LogicInstruction("set", "tmp1", "tmp6"),
                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("case n\nwhen 1\n\"1\"\nwhen 2\n\"two\"\nelse\n\"otherwise\"end\n")
                        )
                )
        );
    }

    @Test
    void generatesCaseWhen() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "0"),
                                new LogicInstruction("read", "tmp1", "cell1", "tmp0"),
                                new LogicInstruction("set", "ast0", "tmp1"),
                                new LogicInstruction("set", "tmp2", "ast0"),
                                new LogicInstruction("jump", "label1", "notEqual", "tmp2", "ST_EMPTY"),
                                new LogicInstruction("set", "tmp6", "0"),
                                new LogicInstruction("write", "ST_INITIALIZED", "cell1", "tmp6"),
                                new LogicInstruction("set", "tmp3", "ST_INITIALIZED"),
                                new LogicInstruction("jump", "label0", "always"),
                                new LogicInstruction("label", "label1"),
                                new LogicInstruction("jump", "label2", "notEqual", "tmp2", "ST_INITIALIZED"),
                                new LogicInstruction("set", "tmp9", "0"),
                                new LogicInstruction("write", "ST_DONE", "cell1", "tmp9"),
                                new LogicInstruction("set", "tmp3", "ST_DONE"),
                                new LogicInstruction("jump", "label0", "always"),
                                new LogicInstruction("label", "label2"),
                                new LogicInstruction("set", "tmp3", "null"),
                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("allocate heap in cell1[0..10]\ncase $state\nwhen ST_EMPTY\n$state = ST_INITIALIZED\nwhen ST_INITIALIZED\n$state = ST_DONE\nend\n")
                        )
                )
        );
    }

    @Test
    void generatesDrawings() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("draw", "clear", "r", "g", "b"),
                                new LogicInstruction("draw", "color", "r", "g", "b", "alpha"),
                                new LogicInstruction("draw", "stroke", "width"),
                                new LogicInstruction("draw", "line", "x1", "y1", "x2", "y2"),
                                new LogicInstruction("draw", "rect", "x", "y", "w", "h"),
                                new LogicInstruction("draw", "lineRect", "x", "y", "w", "h"),
                                new LogicInstruction("draw", "poly", "x", "y", "sides", "radius", "rotation"),
                                new LogicInstruction("draw", "linePoly", "x", "y", "sides", "radius", "rotation"),
                                new LogicInstruction("draw", "triangle", "x1", "y1", "x2", "y2", "x3", "y3"),
                                new LogicInstruction("draw", "image", "x", "y", "@copper", "size", "rotation"),
                                new LogicInstruction("drawflush", "display1"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("clear(r, g, b)\ncolor(r, g, b, alpha)\nstroke(width)\nline(x1, y1, x2, y2)\nrect(x, y, w, h)\nlineRect(x, y, w, h)\npoly(x, y, sides, radius, rotation)\nlinePoly(x, y, sides, radius, rotation)\ntriangle(x1, y1, x2, y2, x3, y3)\nimage(x, y, @copper, size, rotation)\ndrawflush(display1)\n")
                        )
                )
        );
    }

    @Test
    void generatesURadar() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("uradar", "enemy", "ground", "any", "health", "MIN_TO_MAX", "BY_DISTANCE", "tmp0"),
                                new LogicInstruction("set", "target", "tmp0"),
                                new LogicInstruction("op", "notEqual", "tmp1", "target", "null"),
                                new LogicInstruction("jump", "label2", "notEqual", "tmp1", "true"),
                                new LogicInstruction("sensor", "tmp2", "target", "@x"),
                                new LogicInstruction("sensor", "tmp3", "target", "@y"),
                                new LogicInstruction("set", "tmp4", "10"),
                                new LogicInstruction("ucontrol", "approach", "tmp2", "tmp3", "tmp4"),
                                new LogicInstruction("sensor", "tmp5", "target", "@x"),
                                new LogicInstruction("sensor", "tmp6", "target", "@y"),
                                new LogicInstruction("set", "tmp7", "10"),
                                new LogicInstruction("ucontrol", "within", "tmp5", "tmp6", "tmp7", "tmp8"),
                                new LogicInstruction("jump", "label0", "notEqual", "tmp8", "true"),
                                new LogicInstruction("sensor", "tmp9", "target", "@x"),
                                new LogicInstruction("sensor", "tmp10", "target", "@y"),
                                new LogicInstruction("ucontrol", "target", "tmp9", "tmp10", "SHOOT"),
                                new LogicInstruction("set", "tmp11", "null"),
                                new LogicInstruction("jump", "label1", "always"),
                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("set", "tmp11", "null"),
                                new LogicInstruction("label", "label1"),
                                new LogicInstruction("set", "tmp12", "tmp11"),
                                new LogicInstruction("jump", "label3", "always"),
                                new LogicInstruction("label", "label2"),
                                new LogicInstruction("set", "tmp12", "null"),
                                new LogicInstruction("label", "label3"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("" +
                                        "target = uradar(enemy, ground, any, health, MIN_TO_MAX, BY_DISTANCE)\n" +
                                        "if target != null\n" +
                                        "  approach(target.x, target.y, 10)\n" +
                                        "  if within(target.x, target.y, 10)\n" +
                                        "    target(target.x, target.y, SHOOT)\n" +
                                        "  end\n" +
                                        "end\n"
                                )
                        )
                )
        );
    }

    @Test
    void generatesULocate() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("ulocate", "ore", "core", "true", "@surge-alloy", "outx", "outy", "tmp0", "tmp1"),
                                new LogicInstruction("ulocate", "building", "core", "ENEMY", "@copper", "outx", "outy", "tmp2", "outbuilding"),
                                new LogicInstruction("ulocate", "spawn", "core", "true", "@copper", "outx", "outy", "tmp3", "outbuilding"),
                                new LogicInstruction("ulocate", "damaged", "core", "true", "@copper", "outx", "outy", "tmp4", "outbuilding"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("" +
                                        "ulocate(ore, @surge-alloy, outx, outy)\n" +
                                        "ulocate(building, core, ENEMY, outx, outy, outbuilding)\n" +
                                        "ulocate(spawn, outx, outy, outbuilding)\n" +
                                        "ulocate(damaged, outx, outy, outbuilding)\n"
                                )
                        )
                )
        );
    }

    @Test
    void generatesEndFromFunctionCall() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("op", "equal", "tmp0", "some_cond", "false"),
                                new LogicInstruction("jump", "label0", "notEqual", "tmp0", "true"),
                                new LogicInstruction("end"),
                                new LogicInstruction("set", "tmp1", "null"),
                                new LogicInstruction("jump", "label1", "always"),
                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("set", "tmp1", "null"),
                                new LogicInstruction("label", "label1"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("" +
                                        "if some_cond == false\n  end()\nend"
                                )
                        )
                )
        );
    }

    @Test
    void generatesModuloOperator() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "2"),
                                new LogicInstruction("op", "mod", "tmp1", "@tick", "tmp0"),
                                new LogicInstruction("set", "tmp2", "0"),
                                new LogicInstruction("op", "equal", "tmp3", "tmp1", "tmp2"),
                                new LogicInstruction("set", "running", "tmp3"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("" +
                                        "running = @tick % 2 == 0"
                                )
                        )
                )
        );
    }

    @Test
    void refusesToDeclareFunctionsWhenNoStackAround() {
        assertThrows(MissingStackException.class, () ->
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("" +
                                "def foo\n0\nend\n\n\nfoo()\n"
                        )
                )
        );
    }

    @Test
    void generatesCodeForFunctionCallingAndReturn() {
        assertEquals(
                prettyPrint(
                        List.of(
                                // setup stack
                                new LogicInstruction("set", "tmp2", "63"),
                                new LogicInstruction("set", "tmp3", "63"),
                                new LogicInstruction("write", "tmp2", "cell1", "tmp3"),

                                // push return address on stack
                                new LogicInstruction("set", "tmp5", "63"),
                                new LogicInstruction("read", "tmp6", "cell1", "tmp5"), // read stack pointer
                                new LogicInstruction("set", "tmp4", "tmp6"),
                                new LogicInstruction("set", "tmp7", "1"),
                                new LogicInstruction("op", "sub", "tmp8", "tmp4", "tmp7"), // calculate new stack pointer
                                new LogicInstruction("set", "tmp4", "tmp8"),
                                new LogicInstruction("write", "label1", "cell1", "tmp4"), // write value on stack, at stack pointer
                                new LogicInstruction("set", "tmp12", "63"),
                                new LogicInstruction("write", "tmp4", "cell1", "tmp12"), // update stack pointer itself

                                // jump to function
                                new LogicInstruction("set", "@counter", "label0"),

                                // return label
                                new LogicInstruction("label", "label1"),

                                // pop return value from stack
                                new LogicInstruction("set", "tmp15", "63"),
                                new LogicInstruction("read", "tmp16", "cell1", "tmp15"), // read stack pointer
                                new LogicInstruction("set", "tmp14", "tmp16"),
                                new LogicInstruction("read", "tmp17", "cell1", "tmp14"), // read value on stack, at stack pointer
                                new LogicInstruction("set", "tmp13", "tmp17"),
                                new LogicInstruction("set", "tmp18", "1"),
                                new LogicInstruction("op", "add", "tmp19", "tmp14", "tmp18"), // calculate new stack pointer
                                new LogicInstruction("set", "tmp14", "tmp19"),
                                new LogicInstruction("set", "tmp22", "63"),
                                new LogicInstruction("write", "tmp14", "cell1", "tmp22"), // update stack pointer itself

                                // continue rest of main script
                                new LogicInstruction("set", "x", "tmp13"),
                                new LogicInstruction("print", "x"),
                                new LogicInstruction("printflush", "message1"),

                                // end of main script
                                new LogicInstruction("end"),

                                // start of function foo
                                new LogicInstruction("label", "label0"),

                                // no parameters to pop
                                new LogicInstruction("set", "tmp23", "0"),

                                // pop return address
                                new LogicInstruction("set", "tmp26", "63"),
                                new LogicInstruction("read", "tmp27", "cell1", "tmp26"),
                                new LogicInstruction("set", "tmp25", "tmp27"),
                                new LogicInstruction("read", "tmp28", "cell1", "tmp25"),
                                new LogicInstruction("set", "tmp24", "tmp28"),
                                new LogicInstruction("set", "tmp29", "1"),
                                new LogicInstruction("op", "add", "tmp30, tmp25", "tmp29"),
                                new LogicInstruction("set", "tmp25", "tmp30"),
                                new LogicInstruction("set", "tmp33", "63"),
                                new LogicInstruction("write", "tmp25", "cell1", "tmp33"),

                                // push return value
                                new LogicInstruction("set", "tmp35", "63"),
                                new LogicInstruction("read", "tmp36", "cell1", "tmp35"),
                                new LogicInstruction("set", "tmp34", "tmp36"),
                                new LogicInstruction("set", "tmp37", "1"),
                                new LogicInstruction("op", "sub", "tmp38, tmp34", "tmp37"),
                                new LogicInstruction("set", "tmp34", "tmp38"),
                                new LogicInstruction("write", "tmp23", "cell1", "tmp34"),
                                new LogicInstruction("set", "tmp42", "63"),
                                new LogicInstruction("write", "tmp34", "cell1", "tmp42"),

                                // jump to return address
                                new LogicInstruction("set", "@counter", "tmp24"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("" +
                                        "allocate stack in cell1\ndef foo\n0\nend\n\n\nx = foo()\nprint(x)\nprintflush(message1)\n"
                                )
                        )
                )
        );
    }

    @Test
    void passesParametersToFunction() {
        assertEquals(
                prettyPrint(
                        List.of(
                                // setup stack
                                new LogicInstruction("set", "tmp2", "63"),
                                new LogicInstruction("set", "tmp3", "63"),
                                new LogicInstruction("write", "tmp2", "cell1", "tmp3"),

                                // push return address on stack
                                new LogicInstruction("set", "tmp4", "4"),
                                new LogicInstruction("set", "boo", "tmp4"),
                                new LogicInstruction("set", "tmp5", "2"),

                                // push return address on stack
                                new LogicInstruction("set", "tmp7", "63"),
                                new LogicInstruction("read", "tmp8", "cell1", "tmp7"),
                                new LogicInstruction("set", "tmp6", "tmp8"),
                                new LogicInstruction("set", "tmp9", "1"),
                                new LogicInstruction("op", "sub", "tmp10", "tmp6", "tmp9"),
                                new LogicInstruction("set", "tmp6", "tmp10"),
                                new LogicInstruction("write", "label1", "cell1", "tmp6"),
                                new LogicInstruction("set", "tmp14", "63"),
                                new LogicInstruction("write", "tmp6", "cell1", "tmp14"),

                                // push n on stack
                                new LogicInstruction("set", "tmp16", "63"),
                                new LogicInstruction("read", "tmp17", "cell1", "tmp16"),
                                new LogicInstruction("set", "tmp15", "tmp17"),
                                new LogicInstruction("set", "tmp18", "1"),
                                new LogicInstruction("op", "sub", "tmp19", "tmp15", "tmp18"),
                                new LogicInstruction("set", "tmp15", "tmp19"),
                                new LogicInstruction("write", "tmp5", "cell1", "tmp15"),
                                new LogicInstruction("set", "tmp23", "63"),
                                new LogicInstruction("write", "tmp15", "cell1", "tmp23"),

                                // push r on stack
                                new LogicInstruction("set", "tmp25", "63"),
                                new LogicInstruction("read", "tmp26", "cell1", "tmp25"),
                                new LogicInstruction("set", "tmp24", "tmp26"),
                                new LogicInstruction("set", "tmp27", "1"),
                                new LogicInstruction("op", "sub", "tmp28", "tmp24", "tmp27"),
                                new LogicInstruction("set", "tmp24", "tmp28"),
                                new LogicInstruction("write", "boo", "cell1", "tmp24"),
                                new LogicInstruction("set", "tmp32", "63"),
                                new LogicInstruction("write", "tmp24", "cell1", "tmp32"),

                                // jump to function
                                new LogicInstruction("set", "@counter", "label0"),

                                // function return address
                                new LogicInstruction("label", "label1"),

                                // pop return value from stack
                                new LogicInstruction("set", "tmp35", "63"),
                                new LogicInstruction("read", "tmp36", "cell1", "tmp35"),
                                new LogicInstruction("set", "tmp34", "tmp36"),
                                new LogicInstruction("read", "tmp37", "cell1", "tmp34"),
                                new LogicInstruction("set", "tmp33", "tmp37"),
                                new LogicInstruction("set", "tmp38", "1"),
                                new LogicInstruction("op", "add", "tmp39", "tmp34", "tmp38"),
                                new LogicInstruction("set", "tmp34", "tmp39"),
                                new LogicInstruction("set", "tmp42", "63"),
                                new LogicInstruction("write", "tmp34", "cell1", "tmp42"),

                                // continue main body
                                new LogicInstruction("set", "x", "tmp33"),
                                new LogicInstruction("print", "x"),
                                new LogicInstruction("printflush", "message1"),
                                new LogicInstruction("end"),

                                // function foo
                                new LogicInstruction("label", "label0"),

                                // pop r
                                new LogicInstruction("set", "tmp45", "63"),
                                new LogicInstruction("read", "tmp46", "cell1", "tmp45"),
                                new LogicInstruction("set", "tmp44", "tmp46"),
                                new LogicInstruction("read", "tmp47", "cell1", "tmp44"),
                                new LogicInstruction("set", "tmp43", "tmp47"),
                                new LogicInstruction("set", "tmp48", "1"),
                                new LogicInstruction("op", "add", "tmp49", "tmp44", "tmp48"),
                                new LogicInstruction("set", "tmp44", "tmp49"),
                                new LogicInstruction("set", "tmp52", "63"),
                                new LogicInstruction("write", "tmp44", "cell1", "tmp52"),
                                new LogicInstruction("set", "r", "tmp43"),

                                // pop n
                                new LogicInstruction("set", "tmp55", "63"),
                                new LogicInstruction("read", "tmp56", "cell1", "tmp55"),
                                new LogicInstruction("set", "tmp54", "tmp56"),
                                new LogicInstruction("read", "tmp57", "cell1", "tmp54"),
                                new LogicInstruction("set", "tmp53", "tmp57"),
                                new LogicInstruction("set", "tmp58", "1"),
                                new LogicInstruction("op", "add", "tmp59", "tmp54", "tmp58"),
                                new LogicInstruction("set", "tmp54", "tmp59"),
                                new LogicInstruction("set", "tmp62", "63"),
                                new LogicInstruction("write", "tmp54", "cell1", "tmp62"),
                                new LogicInstruction("set", "n", "tmp53"),

                                // execute function body
                                new LogicInstruction("set", "tmp63", "2"),
                                new LogicInstruction("op", "pow", "tmp64", "n", "r"),
                                new LogicInstruction("op", "mul", "tmp65", "tmp63", "tmp64"),

                                // pop return address from stack
                                new LogicInstruction("set", "tmp68", "63"),
                                new LogicInstruction("read", "tmp69", "cell1", "tmp68"),
                                new LogicInstruction("set", "tmp67", "tmp69"),
                                new LogicInstruction("read", "tmp70", "cell1", "tmp67"),
                                new LogicInstruction("set", "tmp66", "tmp70"),
                                new LogicInstruction("set", "tmp71", "1"),
                                new LogicInstruction("op", "add", "tmp72", "tmp67", "tmp71"),
                                new LogicInstruction("set", "tmp67", "tmp72"),
                                new LogicInstruction("set", "tmp75", "63"),
                                new LogicInstruction("write", "tmp67", "cell1", "tmp75"),

                                // push return value on stack
                                new LogicInstruction("set", "tmp77", "63"),
                                new LogicInstruction("read", "tmp78", "cell1", "tmp77"),
                                new LogicInstruction("set", "tmp76", "tmp78"),
                                new LogicInstruction("set", "tmp79", "1"),
                                new LogicInstruction("op", "sub", "tmp80", "tmp76", "tmp79"),
                                new LogicInstruction("set", "tmp76", "tmp80"),
                                new LogicInstruction("write", "tmp65", "cell1", "tmp76"),
                                new LogicInstruction("set", "tmp84", "63"),
                                new LogicInstruction("write", "tmp76", "cell1", "tmp84"),

                                // jump back to caller
                                new LogicInstruction("set", "@counter", "tmp66"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("" +
                                        "allocate stack in cell1\ndef foo(n, r)\n2 * (n ** r)\nend\n\n\nboo = 4\nx = foo(2, boo)\nprint(x)\nprintflush(message1)\n"
                                )
                        )
                )
        );
    }

    @Test
    void functionsCanCallOtherFunctions() {
        assertEquals(
                prettyPrint(
                        List.of(
                                // setup stack
                                new LogicInstruction("set", "tmp2", "63"),
                                new LogicInstruction("set", "tmp3", "63"),
                                new LogicInstruction("write", "tmp2", "cell1", "tmp3"),

                                // prepare parameters
                                new LogicInstruction("set", "tmp4", "8"),
                                new LogicInstruction("set", "boo", "tmp4"),
                                new LogicInstruction("set", "tmp5", "7"),

                                // push return address
                                new LogicInstruction("set", "tmp7", "63"),
                                new LogicInstruction("read", "tmp8", "cell1", "tmp7"),
                                new LogicInstruction("set", "tmp6", "tmp8"),
                                new LogicInstruction("set", "tmp9", "1"),
                                new LogicInstruction("op", "sub", "tmp10", "tmp6", "tmp9"),
                                new LogicInstruction("set", "tmp6", "tmp10"),
                                new LogicInstruction("write", "label2", "cell1", "tmp6"),
                                new LogicInstruction("set", "tmp14", "63"),
                                new LogicInstruction("write", "tmp6", "cell1", "tmp14"),

                                // push n
                                new LogicInstruction("set", "tmp16", "63"),
                                new LogicInstruction("read", "tmp17", "cell1", "tmp16"),
                                new LogicInstruction("set", "tmp15", "tmp17"),
                                new LogicInstruction("set", "tmp18", "1"),
                                new LogicInstruction("op", "sub", "tmp19", "tmp15", "tmp18"),
                                new LogicInstruction("set", "tmp15", "tmp19"),
                                new LogicInstruction("write", "tmp5", "cell1", "tmp15"),
                                new LogicInstruction("set", "tmp23", "63"),
                                new LogicInstruction("write", "tmp15", "cell1", "tmp23"),

                                // push r
                                new LogicInstruction("set", "tmp25", "63"),
                                new LogicInstruction("read", "tmp26", "cell1", "tmp25"),
                                new LogicInstruction("set", "tmp24", "tmp26"),
                                new LogicInstruction("set", "tmp27", "1"),
                                new LogicInstruction("op", "sub", "tmp28", "tmp24", "tmp27"),
                                new LogicInstruction("set", "tmp24", "tmp28"),
                                new LogicInstruction("write", "boo", "cell1", "tmp24"),
                                new LogicInstruction("set", "tmp32", "63"),
                                new LogicInstruction("write", "tmp24", "cell1", "tmp32"),

                                // jump to function
                                new LogicInstruction("set", "@counter", "label0"),

                                // return address
                                new LogicInstruction("label", "label2"),

                                // pop return value from stack
                                new LogicInstruction("set", "tmp35", "63"),
                                new LogicInstruction("read", "tmp36", "cell1", "tmp35"),
                                new LogicInstruction("set", "tmp34", "tmp36"),
                                new LogicInstruction("read", "tmp37", "cell1", "tmp34"),
                                new LogicInstruction("set", "tmp33", "tmp37"),
                                new LogicInstruction("set", "tmp38", "1"),
                                new LogicInstruction("op", "add", "tmp39", "tmp34", "tmp38"),
                                new LogicInstruction("set", "tmp34", "tmp39"),
                                new LogicInstruction("set", "tmp42", "63"),
                                new LogicInstruction("write", "tmp34", "cell1", "tmp42"),

                                // rest of main body
                                new LogicInstruction("set", "x", "tmp33"),
                                new LogicInstruction("print", "x"),
                                new LogicInstruction("printflush", "message1"),
                                new LogicInstruction("end"),

                                // def bar
                                new LogicInstruction("label", "label1"),

                                // pop bar.x
                                new LogicInstruction("set", "tmp45", "63"),
                                new LogicInstruction("read", "tmp46", "cell1", "tmp45"),
                                new LogicInstruction("set", "tmp44", "tmp46"),
                                new LogicInstruction("read", "tmp47", "cell1", "tmp44"),
                                new LogicInstruction("set", "tmp43", "tmp47"),
                                new LogicInstruction("set", "tmp48", "1"),
                                new LogicInstruction("op", "add", "tmp49", "tmp44", "tmp48"),
                                new LogicInstruction("set", "tmp44", "tmp49"),
                                new LogicInstruction("set", "tmp52", "63"),
                                new LogicInstruction("write", "tmp44", "cell1", "tmp52"),
                                new LogicInstruction("set", "x", "tmp43"),

                                // function body
                                new LogicInstruction("set", "tmp53", "2"),
                                new LogicInstruction("op", "mul", "tmp54", "tmp53", "x"),

                                // pop return address
                                new LogicInstruction("set", "tmp57", "63"),
                                new LogicInstruction("read", "tmp58", "cell1", "tmp57"),
                                new LogicInstruction("set", "tmp56", "tmp58"),
                                new LogicInstruction("read", "tmp59", "cell1", "tmp56"),
                                new LogicInstruction("set", "tmp55", "tmp59"),
                                new LogicInstruction("set", "tmp60", "1"),
                                new LogicInstruction("op", "add", "tmp61", "tmp56", "tmp60"),
                                new LogicInstruction("set", "tmp56", "tmp61"),
                                new LogicInstruction("set", "tmp64", "63"),
                                new LogicInstruction("write", "tmp56", "cell1", "tmp64"),

                                // push return value
                                new LogicInstruction("set", "tmp66", "63"),
                                new LogicInstruction("read", "tmp67", "cell1", "tmp66"),
                                new LogicInstruction("set", "tmp65", "tmp67"),
                                new LogicInstruction("set", "tmp68", "1"),
                                new LogicInstruction("op", "sub", "tmp69", "tmp65", "tmp68"),
                                new LogicInstruction("set", "tmp65", "tmp69"),
                                new LogicInstruction("write", "tmp54", "cell1", "tmp65"),
                                new LogicInstruction("set", "tmp73", "63"),
                                new LogicInstruction("write", "tmp65", "cell1", "tmp73"),

                                // return
                                new LogicInstruction("set", "@counter", "tmp55"),
                                new LogicInstruction("end"),

                                // def foo
                                new LogicInstruction("label", "label0"),

                                // pop foo.r
                                new LogicInstruction("set", "tmp76", "63"),
                                new LogicInstruction("read", "tmp77", "cell1", "tmp76"),
                                new LogicInstruction("set", "tmp75", "tmp77"),
                                new LogicInstruction("read", "tmp78", "cell1", "tmp75"),
                                new LogicInstruction("set", "tmp74", "tmp78"),
                                new LogicInstruction("set", "tmp79", "1"),
                                new LogicInstruction("op", "add", "tmp80", "tmp75", "tmp79"),
                                new LogicInstruction("set", "tmp75", "tmp80"),
                                new LogicInstruction("set", "tmp83", "63"),
                                new LogicInstruction("write", "tmp75", "cell1", "tmp83"),
                                new LogicInstruction("set", "r", "tmp74"),

                                // pop foo.n
                                new LogicInstruction("set", "tmp86", "63"),
                                new LogicInstruction("read", "tmp87", "cell1", "tmp86"),
                                new LogicInstruction("set", "tmp85", "tmp87"),
                                new LogicInstruction("read", "tmp88", "cell1", "tmp85"),
                                new LogicInstruction("set", "tmp84", "tmp88"),
                                new LogicInstruction("set", "tmp89", "1"),
                                new LogicInstruction("op", "add", "tmp90", "tmp85", "tmp89"),
                                new LogicInstruction("set", "tmp85", "tmp90"),
                                new LogicInstruction("set", "tmp93", "63"),
                                new LogicInstruction("write", "tmp85", "cell1", "tmp93"),
                                new LogicInstruction("set", "n", "tmp84"),

                                // function body
                                new LogicInstruction("set", "tmp94", "2"),

                                // push return address
                                new LogicInstruction("set", "tmp96", "63"),
                                new LogicInstruction("read", "tmp97", "cell1", "tmp96"),
                                new LogicInstruction("set", "tmp95", "tmp97"),
                                new LogicInstruction("set", "tmp98", "1"),
                                new LogicInstruction("op", "sub", "tmp99", "tmp95", "tmp98"),
                                new LogicInstruction("set", "tmp95", "tmp99"),
                                new LogicInstruction("write", "label3", "cell1", "tmp95"),
                                new LogicInstruction("set", "tmp103", "63"),
                                new LogicInstruction("write", "tmp95", "cell1", "tmp103"),

                                // push bar.x
                                new LogicInstruction("set", "tmp105", "63"),
                                new LogicInstruction("read", "tmp106", "cell1", "tmp105"),
                                new LogicInstruction("set", "tmp104", "tmp106"),
                                new LogicInstruction("set", "tmp107", "1"),
                                new LogicInstruction("op", "sub", "tmp108", "tmp104", "tmp107"),
                                new LogicInstruction("set", "tmp104", "tmp108"),
                                new LogicInstruction("write", "r", "cell1", "tmp104"),
                                new LogicInstruction("set", "tmp112", "63"),
                                new LogicInstruction("write", "tmp104", "cell1", "tmp112"),

                                // jump to subroutine
                                new LogicInstruction("set", "@counter", "label1"),

                                // return address
                                new LogicInstruction("label", "label3"),

                                // pop return value from stack
                                new LogicInstruction("set", "tmp115", "63"),
                                new LogicInstruction("read", "tmp116", "cell1", "tmp115"),
                                new LogicInstruction("set", "tmp114", "tmp116"),
                                new LogicInstruction("read", "tmp117", "cell1", "tmp114"),
                                new LogicInstruction("set", "tmp113", "tmp117"),
                                new LogicInstruction("set", "tmp118", "1"),
                                new LogicInstruction("op", "add", "tmp119", "tmp114", "tmp118"),
                                new LogicInstruction("set", "tmp114", "tmp119"),
                                new LogicInstruction("set", "tmp122", "63"),
                                new LogicInstruction("write", "tmp114", "cell1", "tmp122"),

                                // function body
                                new LogicInstruction("op", "pow", "tmp123", "n", "tmp113"),
                                new LogicInstruction("op", "mul", "tmp124", "tmp94", "tmp123"),

                                // pop return address
                                new LogicInstruction("set", "tmp127", "63"),
                                new LogicInstruction("read", "tmp128", "cell1", "tmp127"),
                                new LogicInstruction("set", "tmp126", "tmp128"),
                                new LogicInstruction("read", "tmp129", "cell1", "tmp126"),
                                new LogicInstruction("set", "tmp125", "tmp129"),
                                new LogicInstruction("set", "tmp130", "1"),
                                new LogicInstruction("op", "add", "tmp131", "tmp126", "tmp130"),
                                new LogicInstruction("set", "tmp126", "tmp131"),
                                new LogicInstruction("set", "tmp134", "63"),
                                new LogicInstruction("write", "tmp126", "cell1", "tmp134"),

                                // push return value
                                new LogicInstruction("set", "tmp136", "63"),
                                new LogicInstruction("read", "tmp137", "cell1", "tmp136"),
                                new LogicInstruction("set", "tmp135", "tmp137"),
                                new LogicInstruction("set", "tmp138", "1"),
                                new LogicInstruction("op", "sub", "tmp139", "tmp135", "tmp138"),
                                new LogicInstruction("set", "tmp135", "tmp139"),
                                new LogicInstruction("write", "tmp124", "cell1", "tmp135"),
                                new LogicInstruction("set", "tmp143", "63"),
                                new LogicInstruction("write", "tmp135", "cell1", "tmp143"),

                                // return
                                new LogicInstruction("set", "@counter", "tmp125"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("" +
                                        "allocate stack in cell1\ndef foo(n, r)\n2 * (n ** bar(r))\nend\n\ndef bar(x)\n2 * x\nend\n\n\nboo = 8\nx = foo(7, boo)\nprint(x)\nprintflush(message1)\n"
                                )
                        )
                )
        );
    }

    @Test
    void generatesMultiParameterControlInstruction() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("sensor", "tmp0", "leader", "@shootX"),
                                new LogicInstruction("sensor", "tmp1", "leader", "@shootY"),
                                new LogicInstruction("sensor", "tmp2", "leader", "@shooting"),
                                new LogicInstruction("control", "shoot", "turret", "tmp0", "tmp1", "tmp2"),
                                new LogicInstruction("set", "tmp3", "14"),
                                new LogicInstruction("set", "tmp4", "15"),
                                new LogicInstruction("set", "tmp5", "16"),
                                new LogicInstruction("control", "color", "turret", "tmp3", "tmp4", "tmp5"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("" +
                                        "turret.shoot(leader.shootX, leader.shootY, leader.shooting)\nturret.color(14, 15, 16)\n"
                                )
                        )

                )
        );
    }

    @Test
    void canIndirectlyReferenceHeap() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "HEAPPTR", "cell1"),
                                new LogicInstruction("set", "tmp2", "0"),
                                new LogicInstruction("set", "tmp3", "0"),
                                new LogicInstruction("write", "tmp2", "HEAPPTR", "tmp3"), // write $dx
                                new LogicInstruction("set", "tmp6", "1"),
                                new LogicInstruction("read", "tmp7", "HEAPPTR", "tmp6"), // read $dy
                                new LogicInstruction("set", "tmp8", "0"),
                                new LogicInstruction("read", "tmp9", "HEAPPTR", "tmp8"), // read $dx
                                new LogicInstruction("op", "add", "tmp10", "tmp7", "tmp9"), // tmp10 = $dx + $dy
                                new LogicInstruction("set", "tmp11", "1"),
                                new LogicInstruction("write", "tmp10", "HEAPPTR", "tmp11"), // set $dy
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("" +
                                        "set HEAPPTR = cell1\nallocate heap in HEAPPTR[0...4]\n$dx = 0\n\n$dy += $dx"
                                )
                        )

                )
        );
    }

    @Test
    void canIndirectlyReferenceStack() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "STACKPTR", "cell1"),
                                new LogicInstruction("set", "HEAPPTR", "cell2"),

                                new LogicInstruction("set", "tmp2", "63"), // init stack
                                new LogicInstruction("set", "tmp3", "63"),
                                new LogicInstruction("write", "tmp2", "STACKPTR", "tmp3"),

                                new LogicInstruction("set", "tmp7", "63"), // push return address on stack
                                new LogicInstruction("read", "tmp8", "STACKPTR", "tmp7"),
                                new LogicInstruction("set", "tmp6", "tmp8"),
                                new LogicInstruction("set", "tmp9", "1"),
                                new LogicInstruction("op", "sub", "tmp10", "tmp6", "tmp9"),
                                new LogicInstruction("set", "tmp6", "tmp10"),
                                new LogicInstruction("write", "label1", "STACKPTR", "tmp6"),
                                new LogicInstruction("set", "tmp14", "63"),
                                new LogicInstruction("write", "tmp6", "STACKPTR", "tmp14"),

                                new LogicInstruction("set", "@counter", "label0"), // invoke function

                                new LogicInstruction("label", "label1"),

                                new LogicInstruction("set", "tmp17", "63"), // pop return value from stack
                                new LogicInstruction("read", "tmp18", "STACKPTR", "tmp17"),
                                new LogicInstruction("set", "tmp16", "tmp18"),
                                new LogicInstruction("read", "tmp19", "STACKPTR", "tmp16"),
                                new LogicInstruction("set", "tmp15", "tmp19"),
                                new LogicInstruction("set", "tmp20", "1"),
                                new LogicInstruction("op", "add", "tmp21", "tmp16", "tmp20"),
                                new LogicInstruction("set", "tmp16", "tmp21"),
                                new LogicInstruction("set", "tmp24", "63"),
                                new LogicInstruction("write", "tmp16", "STACKPTR", "tmp24"),

                                new LogicInstruction("set", "tmp25", "0"), // write $dx
                                new LogicInstruction("write", "tmp15", "HEAPPTR", "tmp25"),

                                new LogicInstruction("end"), // end of main function body

                                new LogicInstruction("label", "label0"), // start of delay function

                                new LogicInstruction("set", "tmp26", "0"), // return value

                                new LogicInstruction("set", "tmp29", "63"), // pop return address from stack
                                new LogicInstruction("read", "tmp30", "STACKPTR", "tmp29"),
                                new LogicInstruction("set", "tmp28", "tmp30"),
                                new LogicInstruction("read", "tmp31", "STACKPTR", "tmp28"),
                                new LogicInstruction("set", "tmp27", "tmp31"),
                                new LogicInstruction("set", "tmp32", "1"),
                                new LogicInstruction("op", "add", "tmp33, tmp28", "tmp32"),
                                new LogicInstruction("set", "tmp28", "tmp33"),
                                new LogicInstruction("set", "tmp36", "63"),
                                new LogicInstruction("write", "tmp28", "STACKPTR", "tmp36"),

                                new LogicInstruction("set", "tmp38", "63"), // push return value on stack
                                new LogicInstruction("read", "tmp39", "STACKPTR", "tmp38"),
                                new LogicInstruction("set", "tmp37", "tmp39"),
                                new LogicInstruction("set", "tmp40", "1"),
                                new LogicInstruction("op", "sub", "tmp41", "tmp37", "tmp40"),
                                new LogicInstruction("set", "tmp37", "tmp41"),
                                new LogicInstruction("write", "tmp26", "STACKPTR", "tmp37"),
                                new LogicInstruction("set", "tmp45", "63"),
                                new LogicInstruction("write", "tmp37", "STACKPTR", "tmp45"),

                                new LogicInstruction("set", "@counter", "tmp27"), // jump back to caller
                                new LogicInstruction("end")

                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("" +
                                        "set STACKPTR = cell1\n" +
                                        "set HEAPPTR = cell2\n" +
                                        "allocate heap in HEAPPTR[0...16], stack in STACKPTR\n" +
                                        "def delay\n" +
                                        "0\n" +
                                        "end\n" +
                                        "\n" +
                                        "$dx = delay()\n"
                                )
                        )

                )
        );
    }

    @Test
    void supportsTheSqrtFunction() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "2"),
                                new LogicInstruction("op", "sqrt", "tmp1", "tmp0"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("" +
                                        "sqrt(2)"
                                )
                        )

                )
        );
    }

    @Test
    void supportsLogiclessCaseWhen() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp0", "2"),
                                new LogicInstruction("op", "rand", "tmp1", "tmp0"),
                                new LogicInstruction("op", "floor", "tmp2", "tmp1"),
                                new LogicInstruction("set", "ast0", "tmp2"),
                                new LogicInstruction("set", "tmp3", "ast0"),
                                new LogicInstruction("set", "tmp5", "0"),
                                new LogicInstruction("jump", "label1", "notEqual", "tmp3", "tmp5"),
                                new LogicInstruction("set", "tmp6", "1000"),
                                new LogicInstruction("set", "tmp4", "tmp6"),
                                new LogicInstruction("jump", "label0", "always"),
                                new LogicInstruction("label", "label1"),
                                new LogicInstruction("set", "tmp7", "1"),
                                new LogicInstruction("jump", "label2", "notEqual", "tmp3", "tmp7"),
                                new LogicInstruction("set", "tmp4", "null"),
                                new LogicInstruction("jump", "label0", "always"),
                                new LogicInstruction("label", "label2"),
                                new LogicInstruction("set", "tmp4", "null"),
                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("" +
                                        "case floor(rand(2))\nwhen 0\n  1000\nwhen 1\n  // no op\nend"
                                )
                        )

                )
        );
    }
}
