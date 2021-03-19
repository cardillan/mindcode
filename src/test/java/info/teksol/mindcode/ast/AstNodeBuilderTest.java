package info.teksol.mindcode.ast;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ParsingException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AstNodeBuilderTest extends AbstractAstTest {
    @Test
    void parsesTheEmptyProgram() {
        assertEquals(
                new Seq(new NoOp()),
                translateToAst("")
        );
    }

    @Test
    void parsesLiteralInt() {
        assertEquals(
                new Seq(
                        new NumericLiteral("156")
                ),
                translateToAst("156")

        );
    }

    @Test
    void parsesLiteralFloat() {
        assertEquals(
                new Seq(
                        new NumericLiteral("156.156")
                ),
                translateToAst("156.156")

        );
    }

    @Test
    void parsesLiteralString() {
        assertEquals(
                new Seq(
                        new StringLiteral("156")
                ),
                translateToAst("\"156\"")

        );
    }

    @Test
    void parsesVarRef() {
        assertEquals(
                new Seq(
                        new VarRef("a")
                ),
                translateToAst("a")

        );
    }

    @Test
    void parsesSimpleAssignment() {
        assertEquals(
                new Seq(
                        new Assignment(
                                new VarRef("a"),
                                new NumericLiteral("1")
                        )
                ),
                translateToAst("a = 1")

        );
    }

    @Test
    void parsesSimpleBinOpPlusMinus() {
        assertEquals(
                new Seq(
                        new BinaryOp(
                                new BinaryOp(
                                        new NumericLiteral("1"),
                                        "+",
                                        new NumericLiteral("2")
                                ),
                                "-",
                                new NumericLiteral("3")
                        )
                ),
                translateToAst("1 + 2 - 3")

        );
    }

    @Test
    void parsesSimpleBinOpExp() {
        assertEquals(
                new Seq(
                        new BinaryOp(
                                new NumericLiteral("3.1415"),
                                "**",
                                new NumericLiteral("2")
                        )
                ),
                translateToAst("3.1415 ** 2")

        );
    }

    @Test
    void parsesSimpleBinOpMulDiv() {
        assertEquals(
                new Seq(
                        new BinaryOp(
                                new BinaryOp(
                                        new NumericLiteral("1"),
                                        "*",
                                        new NumericLiteral("2")
                                ),
                                "/",
                                new NumericLiteral("3")
                        )
                ),
                translateToAst("1 * 2 / 3")

        );
    }

    @Test
    void parsesUnaryOperation() {
        assertEquals(
                new Seq(
                        new UnaryOp("not", new VarRef("ready"))
                ),
                translateToAst("not ready")
        );
    }

    @Test
    void respectsArithmeticOrderOfOperations() {
        assertEquals(
                new Seq(
                        new BinaryOp(
                                new NumericLiteral("1"),
                                "+",
                                new BinaryOp(
                                        new NumericLiteral("2"),
                                        "*",
                                        new NumericLiteral("3")
                                )
                        )
                ),
                translateToAst("1 + 2 * 3")
        );
    }

    @Test
    void parsesParenthesis() {
        assertEquals(
                new Seq(
                        new BinaryOp(
                                new BinaryOp(
                                        new NumericLiteral("1"),
                                        "+",
                                        new NumericLiteral("2")
                                ),
                                "*",
                                new NumericLiteral("3")
                        )
                ),
                translateToAst("(1 + 2) * 3")
        );
    }

    @Test
    void parsesComplexConditionalExpression() {
        assertEquals(
                new Seq(
                        new UnaryOp("not",
                                new BinaryOp(
                                        new BinaryOp(
                                                new VarRef("a"),
                                                "<",
                                                new VarRef("b")
                                        ),
                                        "and",
                                        new BinaryOp(
                                                new VarRef("c"),
                                                ">",
                                                new BinaryOp(
                                                        new BinaryOp(
                                                                new NumericLiteral("4"),
                                                                "*",
                                                                new VarRef("r")
                                                        ),
                                                        ">",
                                                        new NumericLiteral("5")
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("not (a < b and c > (4 * r > 5))")
        );
    }

    @Test
    void parsesFunctionCalls() {
        assertEquals(
                new Seq(
                        new Seq(
                                new Seq(
                                        new Seq(
                                                new FunctionCall(
                                                        "print",
                                                        List.of(
                                                                new StringLiteral("\"a\": "),
                                                                new VarRef("a")
                                                        )
                                                )
                                        ),
                                        new FunctionCall("random", List.of())
                                ),
                                new FunctionCall("print", List.of(new VarRef("r")))
                        ),
                        new FunctionCall(
                                "print",
                                List.of(
                                        new StringLiteral("\\nb: "),
                                        new BinaryOp(
                                                new VarRef("b"),
                                                "/",
                                                new NumericLiteral("3.1415")
                                        )
                                )
                        )

                ),
                translateToAst("print(\"\\\"a\\\": \", a)\nrandom()\nprint(r)\nprint(\"\\nb: \", b / 3.1415)")


        );
    }

    @Test
    void parsesUnaryMinus() {
        assertEquals(
                new Seq(
                        new Seq(
                                new Seq(
                                        new Assignment(
                                                new VarRef("dx"),
                                                new BinaryOp(
                                                        new VarRef("dx"),
                                                        "*",
                                                        new NumericLiteral("-1")
                                                )
                                        )
                                ),
                                new Assignment(new VarRef("dy"), new NumericLiteral("-1"))
                        ),
                        new Assignment(
                                new VarRef("dz"),
                                new BinaryOp(
                                        new NumericLiteral("2"),
                                        "-",
                                        new NumericLiteral("1")
                                )
                        )
                ),
                translateToAst("dx *= -1\ndy = -1\ndz = 2 - 1")
        );
    }

    @Test
    void parsesHeapAccesses() {
        assertEquals(
                new Seq(
                        new Assignment(
                                new HeapAccess(
                                        "cell2",
                                        new NumericLiteral("1")
                                ),
                                new HeapAccess("cell3", new NumericLiteral("0"))
                        )
                ),
                translateToAst("cell2[1] = cell3[0]")
        );
    }

    @Test
    void parsesGlobalReferences() {
        assertEquals(
                new Seq(
                        new Seq(
                                new Seq(new NoOp(), new NoOp()),
                                new Assignment(
                                        new HeapAccess("cell2", new NumericLiteral("4")),
                                        new NumericLiteral("1")
                                )
                        ),
                        new Assignment(
                                new HeapAccess(
                                        "cell2",
                                        new NumericLiteral("5")
                                ),
                                new BinaryOp(
                                        new HeapAccess(
                                                "cell2",
                                                new NumericLiteral("4")
                                        ),
                                        "+",
                                        new NumericLiteral("42")
                                )
                        )
                ),
                translateToAst("allocate heap in cell2[4..5]\n$dx = 1\n$dy = $dx + 42")
        );
    }


    @Test
    void parsesExponentiationAssignment() {
        assertEquals(
                new Seq(
                        new Assignment(
                                new HeapAccess(
                                        "cell1",
                                        new NumericLiteral("0")
                                ),
                                new FunctionCall(
                                        "rand",
                                        List.of(
                                                new BinaryOp(
                                                        new NumericLiteral("9"),
                                                        "**",
                                                        new NumericLiteral("9")
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("cell1[0] = rand(9**9)")
        );
    }

    @Test
    void parsesHeapReferencesWithRvalues() {
        assertEquals(
                new Seq(
                        new Assignment(
                                new HeapAccess(
                                        "cell1",
                                        new VarRef("dx")
                                ),
                                new NumericLiteral("1")
                        )
                ),
                translateToAst("cell1[dx] = 1")
        );
    }

    @Test
    void parsesComments() {
        assertEquals(
                new Seq(
                        new Seq(
                                new Seq(
                                        // We only remove the leading `//`, because we don't know what kind of formatting
                                        // the end-user applied, so we have to keep the spaces in place
                                        new Comment(" Remember that we initialized ourselves")
                                ),
                                new Comment(" This is required otherwise we'll repeat ourselves")
                        ),
                        new Assignment(new VarRef("wasInitialized"), new NumericLiteral("1"))
                ),
                translateToAst(
                        "// Remember that we initialized ourselves\n// This is required otherwise we'll repeat ourselves\nwasInitialized = 1\n"
                )
        );
    }

    @Test
    void parsesMathFunctions() {
        assertEquals(
                new Seq(
                        new Assignment(
                                new VarRef("x"),
                                new FunctionCall(
                                        "ceil",
                                        new FunctionCall(
                                                "floor",
                                                new FunctionCall(
                                                        "sin",
                                                        new FunctionCall(
                                                                "log",
                                                                new FunctionCall(
                                                                        "cos",
                                                                        new FunctionCall(
                                                                                "abs",
                                                                                new FunctionCall(
                                                                                        "tan",
                                                                                        new FunctionCall("rand",
                                                                                                new NumericLiteral("1")
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("x = ceil(floor(sin(log(cos(abs(tan(rand(1))))))))")
        );
    }

    @Test
    void parsesRefsWithDashInThem() {
        assertEquals(
                new Seq(
                        new FunctionCall(
                                "build",
                                List.of(
                                        new VarRef("x"),
                                        new VarRef("y"),
                                        new Ref("titanium-conveyor"),
                                        new NumericLiteral("0"),
                                        new NumericLiteral("0")
                                )
                        )
                ),
                translateToAst("build(x, y, @titanium-conveyor, 0, 0)")
        );
    }

    @Test
    void parsesFlagAssignment() {
        assertEquals(
                new Seq(
                        new NoOp(),
                        new FunctionCall(
                                "flag",
                                List.of(new VarRef("FLAG"))
                        )
                ),
                translateToAst("flag(FLAG)")
        );
    }

    @Test
    void parsesAddressCalculationReferences() {
        assertEquals(
                new Seq(
                        new Assignment(
                                new HeapAccess(
                                        "cell1",
                                        new VarRef("ptr")
                                ),
                                new BinaryOp(
                                        new HeapAccess(
                                                "cell1",
                                                new BinaryOp(
                                                        new VarRef("ptr"),
                                                        "-",
                                                        new NumericLiteral("1")
                                                )
                                        ),
                                        "+",
                                        new HeapAccess(
                                                "cell1",
                                                new BinaryOp(
                                                        new VarRef("ptr"),
                                                        "-",
                                                        new NumericLiteral("2")
                                                )
                                        )

                                )
                        )
                ),
                translateToAst("cell1[ptr] = cell1[ptr - 1] + cell1[ptr - 2]")
        );
    }

    @Test
    void parsesHeapAllocationWithExclusiveRange() {
        assertEquals(
                new Seq(
                        new NoOp()
                ),
                translateToAst("allocate heap in cell2[ 0 ... 64 ]")
        );
    }

    @Test
    void parsesHeapAllocationWithInclusiveRange() {
        assertEquals(
                new Seq(
                        new NoOp()
                ),
                translateToAst("allocate heap in cell2[0 .. 30]")
        );
    }

    @Test
    void rejectsHeapUsageWhenUnallocated() {
        assertThrows(UnallocatedHeapException.class, () -> translateToAst("$dx = 1"));
    }

    @Test
    void throwsAnOutOfHeapSpaceExceptionWhenUsingMoreHeapSpaceThanAllocated() {
        assertThrows(OutOfHeapSpaceException.class, () -> translateToAst("allocate heap in cell1[0 .. 1]\n$dx = $dy = $dz"));
    }

    @Test
    void parsesPropertyAccesses() {
        assertEquals(
                new Seq(
                        new Seq(
                                new BinaryOp(
                                        new PropertyAccess(new VarRef("foundation1"), "copper"),
                                        "<",
                                        new PropertyAccess(new VarRef("foundation1"), "itemCapacity")
                                )
                        ),
                        new BinaryOp(
                                new PropertyAccess(new VarRef("reactor1"), "cryofluid"),
                                "<",
                                new NumericLiteral("10")
                        )
                ),
                translateToAst("foundation1.copper < foundation1.itemCapacity\nreactor1.cryofluid < 10")
        );
    }

    @Test
    void parsesWriteToPropertyAccess() {
        assertEquals(
                new Seq(
                        new Assignment(
                                new PropertyAccess(
                                        new VarRef("conveyor1"),
                                        "enabled"
                                ),
                                new BinaryOp(
                                        new PropertyAccess(new VarRef("CORE"), "copper"),
                                        "<",
                                        new PropertyAccess(new VarRef("CORE"), "itemCapacity")
                                )
                        )
                ),
                translateToAst("conveyor1.enabled = CORE.copper < CORE.itemCapacity")
        );
    }

    @Test
    void parsesUsefulWhileLoop() {
        assertEquals(
                new Seq(
                        new Seq(
                                new Assignment(new VarRef("n"), new NumericLiteral("5"))
                        ),
                        new WhileStatement(
                                new BinaryOp(new VarRef("n"), ">", new NumericLiteral("0")),
                                new Seq(
                                        new Assignment(
                                                new VarRef("n"),
                                                new BinaryOp(
                                                        new VarRef("n"),
                                                        "-",
                                                        new NumericLiteral("1")
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("n = 5\nwhile n > 0\nn -= 1\nend\n")
        );
    }


    @Test
    void parsesInclusiveIteratorStyleLoop() {
        assertEquals(
                new Seq(
                        new Assignment(new VarRef("n"), new NumericLiteral("1")),
                        new WhileStatement(
                                new BinaryOp(
                                        new VarRef("n"),
                                        "<=",
                                        new NumericLiteral("17")
                                ),
                                new Seq(
                                        new Seq(
                                                new FunctionCall("print", new VarRef("n"))
                                        ),
                                        new Assignment(
                                                new VarRef("n"),
                                                new BinaryOp(
                                                        new VarRef("n"),
                                                        "+",
                                                        new NumericLiteral("1")
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("for n in 1 .. 17\nprint(n)\nend\n")
        );
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertEquals(
                new Seq(
                        new Assignment(new VarRef("n"), new NumericLiteral("1")),
                        new WhileStatement(
                                new BinaryOp(
                                        new VarRef("n"),
                                        "<",
                                        new NumericLiteral("17")
                                ),
                                new Seq(
                                        new Seq(
                                                new FunctionCall("print", new VarRef("n"))
                                        ),
                                        new Assignment(
                                                new VarRef("n"),
                                                new BinaryOp(
                                                        new VarRef("n"),
                                                        "+",
                                                        new NumericLiteral("1")
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("for n in 1 ... 17\nprint(n)\nend\n")
        );
    }

    @Test
    void parsesCStyleLoop() {
        assertEquals(
                new Seq(
                        new Seq(
                                new Assignment(new VarRef("i"), new NumericLiteral("0")),
                                new Assignment(new VarRef("j"), new NumericLiteral("-5"))
                        ),
                        new WhileStatement(
                                new BinaryOp(
                                        new VarRef("i"),
                                        "<",
                                        new NumericLiteral("5")
                                ),
                                new Seq(
                                        new Seq(
                                                new FunctionCall("print", new VarRef("n"))
                                        ),
                                        new Seq(
                                                new Assignment(
                                                        new VarRef("j"),
                                                        new BinaryOp(
                                                                new VarRef("j"),
                                                                "-",
                                                                new NumericLiteral("1")
                                                        )
                                                ),
                                                new Assignment(
                                                        new VarRef("i"),
                                                        new BinaryOp(
                                                                new VarRef("i"),
                                                                "+",
                                                                new NumericLiteral("1")
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("for i = 0, j = -5; i < 5; j -= 1, i += 1\nprint(n)\nend\n")
        );
    }

    @Test
    void parsesRefs() {
        assertEquals(
                new Seq(
                        new NoOp(),
                        new WhileStatement(
                                new BinaryOp(
                                        new Ref("unit"),
                                        "===",
                                        new NullLiteral()
                                ),
                                new Seq(
                                        new FunctionCall(
                                                "ubind",
                                                List.of(new VarRef("poly"))
                                        )
                                )
                        )
                ),
                translateToAst("while @unit === null\nubind(poly)\nend")
        );
    }


    @Test
    void parsesIfExpression() {
        assertEquals(
                new Seq(
                        new IfExpression(
                                new BinaryOp(
                                        new VarRef("n"),
                                        ">",
                                        new NumericLiteral("4")
                                ),
                                new Seq(
                                        new Assignment(
                                                new HeapAccess(
                                                        "cell1",
                                                        new NumericLiteral("2")
                                                ),
                                                new BinaryOp(
                                                        new HeapAccess("cell1", new NumericLiteral("2")),
                                                        "+",
                                                        new NumericLiteral("1")
                                                )
                                        )
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("if n > 4 cell1[2] += 1 end")
        );
    }

    @Test
    void parsesIfElseExpression() {
        assertEquals(
                new Seq(
                        new Assignment(
                                new VarRef("value"),
                                new IfExpression(
                                        new BinaryOp(
                                                new HeapAccess("cell1", new NumericLiteral("4")),
                                                "==",
                                                new NumericLiteral("0")
                                        ),
                                        new Seq(new BooleanLiteral(false)),
                                        new Seq(
                                                new Seq(
                                                        new Assignment(
                                                                new HeapAccess(
                                                                        "cell1",
                                                                        new NumericLiteral("4")
                                                                ),
                                                                new BooleanLiteral(true)
                                                        )
                                                ),
                                                new Assignment(
                                                        new VarRef("n"),
                                                        new BinaryOp(
                                                                new VarRef("n"),
                                                                "+",
                                                                new NumericLiteral("1")
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("value = if cell1[4] == 0\nfalse\nelse\ncell1[4] = true\nn += 1\nend\n")
        );
    }

    @Test
    void parsesIfElseIf() {
        assertEquals(
                new Seq(
                        new IfExpression(
                                new BinaryOp(
                                        new VarRef("state"),
                                        "==",
                                        new NumericLiteral("1")),
                                new Seq(new FunctionCall(
                                        "print",
                                        List.of(new VarRef("m")))),
                                new Seq(new IfExpression(
                                        new BinaryOp(
                                                new VarRef("state"),
                                                "==",
                                                new NumericLiteral("2")),
                                        new Seq(new FunctionCall(
                                                "print",
                                                List.of(new VarRef("n")))),
                                        new NoOp())))),
                translateToAst("if state == 1\nprint(m)\nelse\nif state == 2\nprint(n)\nend\nend\n")
        );
    }

    @Test
    void parsesCaseWhen() {
        assertEquals(
                new Seq(
                        new Assignment(new VarRef("ast0"), new VarRef("n")),
                        new CaseExpression(
                                new VarRef("ast0"),
                                List.of(
                                        new CaseAlternative(new NumericLiteral("1"), new Seq(new StringLiteral("1"))),
                                        new CaseAlternative(new NumericLiteral("2"), new Seq(new StringLiteral("two")))
                                ),
                                new Seq(new StringLiteral("otherwise"))
                        )
                ),
                translateToAst("case n\nwhen 1\n\"1\"\nwhen 2\n\"two\"\nelse\n\"otherwise\"end\n")
        );
    }

    @Test
    void rejects_STACK_ReservedKeywordsInVarRef() {
        assertThrows(ParsingException.class, () -> translateToAst("cell1[STACK] = 0"));
    }

    @Test
    void rejects_HEAP_ReservedKeywordsInVarRef() {
        assertThrows(ParsingException.class, () -> translateToAst("cell1[HEAP] = 0"));
    }

    @Test
    void rejects_STACK_ReservedKeywordsInHeapWrite() {
        assertThrows(ParsingException.class, () -> translateToAst("STACK[1] = 0"));
    }

    @Test
    void rejects_HEAP_ReservedKeywordsInHeapWrite() {
        assertThrows(ParsingException.class, () -> translateToAst("HEAP[1] = 0"));
    }

    @Test
    void rejects_STACK_ReservedKeywordsInHeapAccess() {
        assertThrows(ParsingException.class, () -> translateToAst("STACK[0]"));
    }

    @Test
    void rejects_HEAP_ReservedKeywordsInHeapAccess() {
        assertThrows(ParsingException.class, () -> translateToAst("HEAP[0]"));
    }

    @Test
    void rejects_STACK_ReservedKeywordsInVarAssignment() {
        assertThrows(ParsingException.class, () -> translateToAst("STACK = 0"));
    }

    @Test
    void rejects_HEAP_ReservedKeywordsInVarAssignment() {
        assertThrows(ParsingException.class, () -> translateToAst("HEAP = 0"));
    }

    @Test
    void acceptsSemicolonAsStatementSeparator() {
        assertEquals(
                new Seq(
                        new Seq(
                                new Assignment(new VarRef("a"), new NumericLiteral("0"))
                        ),
                        new Assignment(new VarRef("b"), new NumericLiteral("1"))
                ),
                translateToAst("a=0;b=1")
        );
    }

    @Test
    void supportsCallingTheEndFunction() {
        assertEquals(
                new Seq(
                        new IfExpression(
                                new BinaryOp(
                                        new VarRef("some_cond"),
                                        "==",
                                        new BooleanLiteral(false)
                                ),
                                new Seq(
                                        new FunctionCall("end")
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("if some_cond == false\nend()\nend")
        );
    }

    @Test
    void supportsModuloOperator() {
        assertEquals(
                new Seq(
                        new Assignment(
                                new VarRef("running"),
                                new BinaryOp(
                                        new BinaryOp(
                                                new Ref("tick"),
                                                "%",
                                                new NumericLiteral("2")
                                        ),
                                        "==",
                                        new NumericLiteral("0")
                                )
                        )
                ),
                translateToAst("running = @tick % 2 == 0")
        );
    }
}
