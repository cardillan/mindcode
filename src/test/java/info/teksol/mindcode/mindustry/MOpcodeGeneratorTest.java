package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MOpcodeGeneratorTest extends AbstractAstTest {
    @Test
    void convertsComplexAssignment() {
        assertEquals(
                List.of(
                        new MOpcode("set", List.of("tmp0", "2")),
                        new MOpcode("op", List.of("sub", "tmp1", "bar", "tmp0")),
                        new MOpcode("set", List.of("tmp2", "3")),
                        new MOpcode("op", List.of("mul", "tmp3", "tmp1", "tmp2")),
                        new MOpcode("set", List.of("foo", "tmp3")),
                        new MOpcode("end")
                ),
                MOpcodeGenerator.generateFrom((Seq) translateToAst("foo = (bar - 2) * 3"))
        );
    }

    @Test
    void convertsWhileLoopAndPrintFunctionCall() {
        assertEquals(
                List.of(
                        new MOpcode("set", List.of("tmp0", "0")),
                        new MOpcode("set", List.of("n", "tmp0")),
                        new MOpcode("label", List.of("label0")),
                        new MOpcode("set", List.of("tmp1", "5")),
                        new MOpcode("op", List.of("lessThan", "tmp2", "n", "tmp1")),
                        new MOpcode("jump", List.of("label1", "notEqual", "tmp2", "true")),
                        new MOpcode("set", List.of("tmp3", "1")),
                        new MOpcode("op", List.of("add", "tmp4", "n", "tmp3")),
                        new MOpcode("set", List.of("n", "tmp4")),
                        new MOpcode("jump", List.of("label0", "always")),
                        new MOpcode("label", List.of("label1")),
                        new MOpcode("set", List.of("tmp5", "\"n: \"")),
                        new MOpcode("print", List.of("tmp5")),
                        new MOpcode("print", List.of("n")),
                        new MOpcode("end")
                ),
                MOpcodeGenerator.generateFrom((Seq) translateToAst("n = 0\nwhile n < 5 {\nn += 1\n}\nprint(\"n: \", n)"))
        );
    }

    @Test
    void convertsNullAndUnaryOp() {
        assertEquals(
                List.of(
                        new MOpcode("op", List.of("not", "tmp0", "n")),
                        new MOpcode("set", List.of("n", "tmp0")),
                        new MOpcode("set", List.of("x", "null")),
                        new MOpcode("end")
                ),
                MOpcodeGenerator.generateFrom((Seq) translateToAst("n = not n; x = null"))
        );
    }

    @Test
    void convertsSensorReadings() {
        assertEquals(
                List.of(
                        new MOpcode("sensor", "tmp0", "foundation1", "@copper"),
                        new MOpcode("sensor", "tmp1", "foundation1", "@itemCapacity"),
                        new MOpcode("op", "lessThan", "tmp2", "tmp0", "tmp1"),
                        new MOpcode("end")
                ),
                MOpcodeGenerator.generateFrom((Seq) translateToAst("foundation1.copper < foundation1.itemCapacity"))
        );
    }

    @Test
    void convertsBooleanOperations() {
        assertEquals(
                List.of(
                        new MOpcode("label", List.of("label0")),
                        new MOpcode("op", List.of("notEqual", "tmp0", "true", "false")),
                        new MOpcode("jump", List.of("label1", "notEqual", "tmp0", "true")),
                        new MOpcode("set", List.of("tmp1", "\"infinite loop!\"")),
                        new MOpcode("print", List.of("tmp1")),
                        new MOpcode("jump", List.of("label0", "always")),
                        new MOpcode("label", List.of("label1")),
                        new MOpcode("printflush", List.of("message1")),
                        new MOpcode("end")
                ),
                MOpcodeGenerator.generateFrom((Seq) translateToAst("while true != false {\nprint(\"infinite loop!\")\n}\nprintflush(message1)\n"))
        );
    }

    @Test
    void convertsControlStatements() {
        assertEquals(
                List.of(
                        new MOpcode("sensor", "tmp0", "foundation1", "@copper"),
                        new MOpcode("sensor", "tmp1", "tank1", "@water"),
                        new MOpcode("op", "strictEqual", "tmp2", "tmp0", "tmp1"),
                        new MOpcode("control", "enabled", "conveyor1", "tmp2"),
                        new MOpcode("end")
                ),
                MOpcodeGenerator.generateFrom((Seq) translateToAst("conveyor1.enabled = foundation1.copper === tank1.water"))
        );
    }

    @Test
    void convertsHeapAccesses() {
        assertEquals(
                List.of(
                        new MOpcode("read", "tmp0", "cell2", "4"),
                        new MOpcode("sensor", "tmp1", "conveyor1", "@enabled"),
                        new MOpcode("op", "add", "tmp2", "tmp0", "tmp1"),
                        new MOpcode("write", "tmp2", "cell1", "3"),
                        new MOpcode("end")
                ),
                MOpcodeGenerator.generateFrom((Seq) translateToAst("cell1[3] = cell2[4] + conveyor1.enabled"))
        );
    }

    @Test
    void convertsIfExpression() {
        assertEquals(
                List.of(
                        new MOpcode("read", "tmp0", "HEAP", "4"),
                        new MOpcode("set", "tmp1", "0"),
                        new MOpcode("op", "equal", "tmp2", "tmp0", "tmp1"),
                        new MOpcode("jump", "label0", "notEqual", "tmp2", "true"),
                        new MOpcode("set", "tmp5", "false"),
                        new MOpcode("jump", "label1", "always"),
                        new MOpcode("label", "label0"),
                        new MOpcode("write", "true", "HEAP", "4"),
                        new MOpcode("set", "tmp3", "1"),
                        new MOpcode("op", "add", "tmp4", "n", "tmp3"),
                        new MOpcode("set", "n", "tmp4"),
                        new MOpcode("set", "tmp5", "tmp4"),
                        new MOpcode("label", "label1"),
                        new MOpcode("set", "value", "tmp5"),
                        new MOpcode("end")
                ),
                MOpcodeGenerator.generateFrom((Seq) translateToAst("value = if HEAP[4] == 0 { false\n} else { HEAP[4] = true\nn += 1\n}"))
        );
    }

    @Test
    void convertsFunctionsReturningValues() {
        assertEquals(
                List.of(
                        new MOpcode("set", "tmp0", "9"),
                        new MOpcode("set", "tmp1", "9"),
                        new MOpcode("op", "pow", "tmp2", "tmp0", "tmp1"),
                        new MOpcode("op", "rand", "tmp3", "tmp2"),
                        new MOpcode("write", "tmp3", "cell1", "0"),
                        new MOpcode("end")
                ),
                MOpcodeGenerator.generateFrom((Seq) translateToAst("cell1[0] = rand(9**9)"))
        );
    }

    @Test
    void convertsUbindAndControl() {
        assertEquals(
                List.of(
                        new MOpcode("label", "label0"),
                        new MOpcode("op", "strictEqual", "tmp0", "@unit", "null"),
                        new MOpcode("jump", "label1", "notEqual", "tmp0", "true"),
                        new MOpcode("ubind", "@poly"),
                        new MOpcode("jump", "label0", "always"),
                        new MOpcode("label", "label1"),
                        new MOpcode("end")
                ),
                MOpcodeGenerator.generateFrom(
                        (Seq) translateToAst(
                                "while @unit === null {\n  @unit = ubind(@poly)\n}\n")
                )
        );

    }

    @Test
    void convertsReallifeTest1() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new MOpcode("set", "tmp0", "0"),
                                new MOpcode("set", "n", "tmp0"),
                                new MOpcode("label", "label2"),
                                new MOpcode("getlink", "tmp1", "n"),
                                new MOpcode("set", "reactor", "tmp1"),
                                new MOpcode("op", "notEqual", "tmp2", "tmp1", "null"),
                                new MOpcode("jump", "label3", "notEqual", "tmp2", "true"),
                                new MOpcode("sensor", "tmp3", "reactor", "@liquidCapacity"),
                                new MOpcode("set", "tmp4", "0"),
                                new MOpcode("op", "greaterThan", "tmp5", "tmp3", "tmp4"),
                                new MOpcode("jump", "label0", "notEqual", "tmp5", "true"),
                                new MOpcode("sensor", "tmp6", "reactor", "@cryofluid"),
                                new MOpcode("sensor", "tmp7", "reactor", "@liquidCapacity"),
                                new MOpcode("op", "div", "tmp8", "tmp6", "tmp7"),
                                new MOpcode("set", "pct_avail", "tmp8"),
                                new MOpcode("set", "tmp9", "0.25"),
                                new MOpcode("op", "greaterThanEq", "tmp10", "pct_avail", "tmp9"),
                                new MOpcode("control", "enabled", "reactor", "tmp10"),
                                new MOpcode("set", "tmp11", "tmp10"),
                                new MOpcode("jump", "label1", "always"),
                                new MOpcode("label", "label0"),
                                new MOpcode("set", "tmp11", "null"),
                                new MOpcode("label", "label1"),
                                new MOpcode("set", "tmp12", "1"),
                                new MOpcode("op", "add", "tmp13", "n", "tmp12"),
                                new MOpcode("set", "n", "tmp13"),
                                new MOpcode("jump", "label2", "always"),
                                new MOpcode("label", "label3"),
                                new MOpcode("end")
                        )
                ),
                prettyPrint(MOpcodeGenerator.generateFrom(
                        (Seq) translateToAst(
                                "" +
                                        "n = 0\n" +
                                        "\n" +
                                        "while (reactor = getlink(n)) != null {\n" +
                                        "  if reactor.liquidCapacity > 0 {\n" +
                                        "    pct_avail = reactor.cryofluid / reactor.liquidCapacity\n" +
                                        "    reactor.enabled = pct_avail >= 0.25\n" +
                                        "  }\n" +
                                        "\n" +
                                        "  n += 1\n" +
                                        "}\n" +
                                        "")
                        )
                )
        );
    }

    private final String prettyPrint(List<MOpcode> list) {
        return list.stream().map(Object::toString).reduce("", (s, s2) -> s + "\n" + s2);
    }
}
