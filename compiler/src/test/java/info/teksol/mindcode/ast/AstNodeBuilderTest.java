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
                        new BinaryOp(new VarRef("ready"), "==", new BooleanLiteral(false))
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
                        new BinaryOp(
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
                                ),
                                "==",
                                new BooleanLiteral(false)
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
                                        new PropertyAccess(new VarRef("foundation1"), new Ref("copper")),
                                        "<",
                                        new PropertyAccess(new VarRef("foundation1"), new Ref("itemCapacity"))
                                )
                        ),
                        new BinaryOp(
                                new PropertyAccess(new VarRef("reactor1"), new Ref("cryofluid")),
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
                                new PropertyAccess(new VarRef("conveyor1"), new Ref("enabled")),
                                new BinaryOp(
                                        new PropertyAccess(new VarRef("CORE"), new Ref("copper")),
                                        "<",
                                        new PropertyAccess(new VarRef("CORE"), new Ref("itemCapacity"))
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
                        new WhileExpression(
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
                                ),
                                new NoOp()
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
                        new WhileExpression(
                                new BinaryOp(
                                        new VarRef("n"),
                                        "<=",
                                        new NumericLiteral("17")
                                ),
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
                ),
                translateToAst("for n in 1 .. 17\nprint(n)\nend\n")
        );
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertEquals(
                new Seq(
                        new Assignment(new VarRef("n"), new NumericLiteral("1")),
                        new WhileExpression(
                                new BinaryOp(
                                        new VarRef("n"),
                                        "<",
                                        new NumericLiteral("17")
                                ),
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
                        new WhileExpression(
                                new BinaryOp(
                                        new VarRef("i"),
                                        "<",
                                        new NumericLiteral("5")
                                ),
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
                ),
                translateToAst("for i = 0, j = -5; i < 5; j -= 1, i += 1\nprint(n)\nend\n")
        );
    }

    @Test
    void parsesRefs() {
        assertEquals(
                new Seq(
                        new NoOp(),
                        new WhileExpression(
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
                                ),
                                new NoOp()
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
                        new Assignment(new VarRef("__ast0"), new VarRef("n")),
                        new CaseExpression(
                                new VarRef("__ast0"),
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

    @Test
    void supportsDeclaringAStack() {
        assertEquals(
                new Seq(new StackAllocation("cell1", 0, 63)),
                translateToAst("allocate stack in cell1[0...64]")
        );
    }

    @Test
    void rejectsDualStackAllocation() {
        assertThrows(StackAlreadyAllocatedException.class, () ->
                translateToAst("allocate stack in cell1[0...64], stack in cell2[0...512]")
        );
    }

    @Test
    void supportFunctionDeclarations() {
        assertEquals(
                prettyPrint(
                        new Seq(
                                new FunctionDeclaration(
                                        "delay",
                                        List.of(),
                                        new Seq(
                                                new Seq(
                                                        new Seq(
                                                                new Assignment(
                                                                        new VarRef("n"), new NumericLiteral("0")
                                                                )
                                                        ),
                                                        new Assignment(
                                                                new VarRef("deadline"),
                                                                new BinaryOp(
                                                                        new Ref("tick"),
                                                                        "+",
                                                                        new NumericLiteral("60")
                                                                )
                                                        )
                                                ),
                                                new WhileExpression(
                                                        new BinaryOp(
                                                                new Ref("tick"),
                                                                "<",
                                                                new VarRef("deadline")
                                                        ),
                                                        new Seq(
                                                                new Assignment(
                                                                        new VarRef("n"),
                                                                        new BinaryOp(new VarRef("n"),
                                                                                "+",
                                                                                new NumericLiteral("1")
                                                                        )
                                                                )
                                                        ),
                                                        new NoOp()
                                                )
                                        )
                                )
                        )
                ),
                prettyPrint(
                        translateToAst("" +
                                "def delay\n" +
                                "  n = 0\n" +
                                "  deadline = @tick + 60\n" +
                                "  while @tick < deadline\n" +
                                "    n += 1\n" +
                                "  end\n" +
                                "end\n" +
                                "")
                )
        );
    }

    @Test
    void supportsCallingDeclaredFunctions() {
        assertEquals(
                prettyPrint(
                        new Seq(
                                new Seq(
                                        new FunctionDeclaration(
                                                "foo",
                                                List.of(),
                                                new Seq(
                                                        new Assignment(new VarRef("n"),
                                                                new BinaryOp(new VarRef("n"), "+", new NumericLiteral("1"))
                                                        )
                                                )
                                        )
                                ),
                                new FunctionCall("foo")
                        )
                ),
                prettyPrint(
                        translateToAst("" +
                                "def foo\n" +
                                "n=n+1\n" +
                                "end\n" +
                                "foo()\n"
                        )
                )
        );
    }

    @Test
    void supportsDeclaringFunctionsThatAcceptParameters() {
        assertEquals(
                prettyPrint(
                        new Seq(
                                new Seq(
                                        new FunctionDeclaration(
                                                "foo",
                                                List.of(new VarRef("s")),
                                                new Seq(
                                                        new BinaryOp(new VarRef("s"), "+", new NumericLiteral("1"))
                                                )
                                        )
                                ),
                                new FunctionCall("foo", new NumericLiteral("1"))
                        )
                ),
                prettyPrint(
                        translateToAst("" +
                                "def foo(s)\n" +
                                "  s + 1\n" +
                                "end\n" +
                                "foo(1)\n"
                        )
                )
        );
    }

    @Test
    void supportsDeclaringFunctionsWithInitializers() {
        assertEquals(
                prettyPrint(
                        new Seq(
                                new Seq(
                                        new FunctionDeclaration(
                                                "foo",
                                                List.of(new VarRef("s"), new VarRef("r")),
                                                new Seq(
                                                        new BinaryOp(
                                                                new BinaryOp(new VarRef("s"), "+", new NumericLiteral("1")),
                                                                "+",
                                                                new VarRef("r")
                                                        )
                                                )
                                        )
                                ),
                                new FunctionCall("foo", new NumericLiteral("1"), new NumericLiteral("6"))
                        )
                ),
                prettyPrint(
                        translateToAst("" +
                                "def foo(s, r)\n" +
                                "  s + 1 + r\n" +
                                "end\n" +
                                "foo(1, 6)\n"
                        )
                )
        );
    }

    @Test
    void supportsControllingBuildingsThroughPropAccessFunctionCalls() {
        assertEquals(
                prettyPrint(
                        new Seq(
                                new Control(
                                        new VarRef("turret"),
                                        "shoot",
                                        List.of(
                                                new PropertyAccess(new VarRef("leader"), new Ref("shootX")),
                                                new PropertyAccess(new VarRef("leader"), new Ref("shootY")),
                                                new PropertyAccess(new VarRef("leader"), new Ref("shooting"))
                                        )
                                )
                        )
                ),
                prettyPrint(
                        translateToAst("turret.shoot(leader.shootX, leader.shootY, leader.shooting)\n")
                )
        );
    }

    @Test
    void supportsBitwiseAndOrXorShiftLeftAndShiftRight() {
        assertEquals(
                prettyPrint(
                        new Seq(
                                new BinaryOp(
                                        new BinaryOp(
                                                new BinaryOp(
                                                        new NumericLiteral("9842"),
                                                        "&",
                                                        new NumericLiteral("1")
                                                ),
                                                "^",
                                                new BinaryOp(
                                                        new NumericLiteral("1"),
                                                        "<<",
                                                        new NumericLiteral("4")
                                                )
                                        ),
                                        "|",
                                        new BinaryOp(
                                                new VarRef("y"),
                                                ">>",
                                                new NumericLiteral("1")
                                        )
                                )
                        )
                ),
                prettyPrint(
                        translateToAst("(9842 & 1) ^ (1 << 4) | y >> 1\n")
                )
        );
    }

    @Test
    void correctlyParsesElsif() {
        assertEquals(
                new Seq(
                        new IfExpression(
                                new BooleanLiteral(false),
                                new Seq(new NumericLiteral(1)),
                                new IfExpression(
                                        new BooleanLiteral(true),
                                        new Seq(new NumericLiteral(2)),
                                        new Seq(new NumericLiteral(3))
                                )
                        )
                ),
                translateToAst(
                        "if false 1 elsif true 2 else 3 end"
                )
        );
    }

    @Test
    void correctlyParsesIndirectPropertyReference() {
        assertEquals(
                new Seq(
                        new Seq(
                                new Assignment(new VarRef("resource"), new Ref("silicon"))
                        ),
                        new IfExpression(
                                new BinaryOp(
                                        new PropertyAccess(new VarRef("vault1"), new VarRef("resource")),
                                        "<",
                                        new PropertyAccess(new VarRef("vault1"), new Ref("itemCapacity"))
                                ),
                                new Seq(
                                        new FunctionCall("harvest", new VarRef("vault1"), new VarRef("resource"))
                                ),
                                new NoOp()
                        )
                ),
                translateToAst(
                        "" +
                                "resource = @silicon\n" +
                                "if vault1.sensor(resource) < vault1.itemCapacity\n" +
                                "  harvest(vault1, resource)\n" +
                                "end\n"
                )
        );
    }

    @Test
    void correctlyReadsTernaryOperator() {
        assertEquals(
                new Seq(
                        new FunctionCall(
                                "print",
                                new StringLiteral("\\nsm.enabled: "),
                                new IfExpression(
                                        new PropertyAccess(
                                                new VarRef("smelter1"),
                                                new Ref("enabled")
                                        ),
                                        new StringLiteral("true"),
                                        new StringLiteral("false")
                                )
                        )
                ),
                translateToAst("print(\"\\nsm.enabled: \", smelter1.enabled ? \"true\" : \"false\")")
        );
    }

    @Test
    void correctlyParsesBreakContinue() {
        assertEquals(
                new Seq(
                        new Seq(
                                new WhileExpression(
                                        new VarRef("a"),
                                        new Seq(
                                                new IfExpression(
                                                        new VarRef("b"),
                                                        new Seq(new ContinueStatement()),
                                                        new IfExpression(
                                                                new VarRef("c"),
                                                                new Seq(new BreakStatement()),
                                                                new NoOp()
                                                        )
                                                )
                                        ),
                                        new NoOp()
                                )
                        ),
                        new FunctionCall("print", new StringLiteral("End"))
                ),

                translateToAst("" +
                        "while a\n" +
                        "  if b\n" +
                        "    continue\n" +
                        "  elsif c\n" +
                        "    break\n" +
                        "  end\n" +
                        "end\n" +
                        "print(\"End\")"
                )
        );
    }
}
