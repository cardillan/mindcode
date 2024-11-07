package info.teksol.mindcode.ast;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.util.ExpectedMessages;
import info.teksol.util.UnexpectedMessageException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AstNodeBuilderTest extends AbstractAstTest {

    private static FunctionArgument fa(AstNode expression) {
        return new FunctionArgument(null, expression, false, false);
    }

    private static List<FunctionArgument> fa(AstNode... expressions) {
        return Stream.of(expressions).map(AstNodeBuilderTest::fa).toList();
    }

    @Test
    void parsesTheEmptyProgram() {
        assertEquals(
                new Seq(null, new NoOp()),
                translateToAst("")
        );
    }

    @Test
    void parsesLiteralInt() {
        assertEquals(
                new Seq(null,
                        new NumericLiteral(null, "156")
                ),
                translateToAst("156;")

        );
    }

    @Test
    void parsesLiteralFloat() {
        assertEquals(
                new Seq(null,
                        new NumericLiteral(null, "156.156")
                ),
                translateToAst("156.156;")

        );
    }

    @Test
    void parsesLiteralString() {
        assertEquals(
                new Seq(null,
                        new StringLiteral(null, "156")
                ),
                translateToAst("\"156\";")

        );
    }

    @Test
    void parsesVarRef() {
        assertEquals(
                new Seq(null,
                        new VarRef(null, "a")
                ),
                translateToAst("a;")

        );
    }

    @Test
    void parsesSimpleAssignment() {
        assertEquals(
                new Seq(null,
                        new Assignment(null,
                                new VarRef(null, "a"),
                                new NumericLiteral(null, "1")
                        )
                ),
                translateToAst("a = 1;")

        );
    }

    @Test
    void parsesSimpleBinOpPlusMinus() {
        assertEquals(
                new Seq(null,
                        new BinaryOp(null,
                                new BinaryOp(null,
                                        new NumericLiteral(null, "1"),
                                        "+",
                                        new NumericLiteral(null, "2")
                                ),
                                "-",
                                new NumericLiteral(null, "3")
                        )
                ),
                translateToAst("1 + 2 - 3;")

        );
    }

    @Test
    void parsesSimpleBinOpExp() {
        assertEquals(
                new Seq(null,
                        new BinaryOp(null,
                                new NumericLiteral(null, "3.1415"),
                                "**",
                                new NumericLiteral(null, "2")
                        )
                ),
                translateToAst("3.1415 ** 2;")

        );
    }

    @Test
    void parsesSimpleBinOpMulDiv() {
        assertEquals(
                new Seq(null,
                        new BinaryOp(null,
                                new BinaryOp(null,
                                        new NumericLiteral(null, "1"),
                                        "*",
                                        new NumericLiteral(null, "2")
                                ),
                                "/",
                                new NumericLiteral(null, "3")
                        )
                ),
                translateToAst("1 * 2 / 3;")

        );
    }

    @Test
    void parsesUnaryOperation() {
        assertEquals(
                new Seq(null,
                        new BinaryOp(null, new VarRef(null, "ready"), "==", new BooleanLiteral(null, false))
                ),
                translateToAst("not ready;")
        );
    }

    @Test
    void respectsArithmeticOrderOfOperations() {
        assertEquals(
                new Seq(null,
                        new BinaryOp(null,
                                new NumericLiteral(null, "1"),
                                "+",
                                new BinaryOp(null,
                                        new NumericLiteral(null, "2"),
                                        "*",
                                        new NumericLiteral(null, "3")
                                )
                        )
                ),
                translateToAst("1 + 2 * 3;")
        );
    }

    @Test
    void parsesParenthesis() {
        assertEquals(
                new Seq(null,
                        new BinaryOp(null,
                                new BinaryOp(null,
                                        new NumericLiteral(null, "1"),
                                        "+",
                                        new NumericLiteral(null, "2")
                                ),
                                "*",
                                new NumericLiteral(null, "3")
                        )
                ),
                translateToAst("(1 + 2) * 3;")
        );
    }

    @Test
    void parsesComplexConditionalExpression() {
        assertEquals(
                new Seq(null,
                        new BinaryOp(null,
                                new BoolBinaryOp(null,
                                        new BinaryOp(null,
                                                new VarRef(null, "a"),
                                                "<",
                                                new VarRef(null, "b")
                                        ),
                                        "and",
                                        new BinaryOp(null,
                                                new VarRef(null, "c"),
                                                ">",
                                                new BinaryOp(null,
                                                        new BinaryOp(null,
                                                                new NumericLiteral(null, "4"),
                                                                "*",
                                                                new VarRef(null, "r")
                                                        ),
                                                        ">",
                                                        new NumericLiteral(null, "5")
                                                )
                                        )
                                ),
                                "==",
                                new BooleanLiteral(null, false)
                        )
                ),
                translateToAst("not (a < b and c > (4 * r > 5));")
        );
    }

    @Test
    void parsesFunctionCalls() {
        assertEquals(
                new Seq(null,
                        new Seq(null,
                                new Seq(null,
                                        new Seq(null,
                                                new FunctionCall(null,
                                                        "print",
                                                        fa(new StringLiteral(null, "'a': ")),
                                                        fa(new VarRef(null, "a"))
                                                )
                                        ),
                                        new FunctionCall(null, "random")
                                ),
                                new FunctionCall(null, "print", fa(new VarRef(null, "r")))
                        ),
                        new FunctionCall(null,
                                "print",
                                fa(new StringLiteral(null, "\\nb: ")),
                                fa(new BinaryOp(null,
                                                new VarRef(null, "b"),
                                                "/",
                                                new NumericLiteral(null, "3.1415")
                                        )
                                )
                        )

                ),
                translateToAst("""
                        print("'a': ", a);
                        random();
                        print(r);
                        print("\\nb: ", b / 3.1415);""")


        );
    }

    @Test
    void parsesUnaryMinus() {
        assertEquals(
                new Seq(null,
                        new Seq(null,
                                new Seq(null,
                                        new Assignment(null,
                                                new VarRef(null, "dx"),
                                                new BinaryOp(null,
                                                        new VarRef(null, "dx"),
                                                        "*",
                                                        new NumericLiteral(null, "-1")
                                                )
                                        )
                                ),
                                new Assignment(null, new VarRef(null, "dy"), new NumericLiteral(null, "-1"))
                        ),
                        new Assignment(null,
                                new VarRef(null, "dz"),
                                new BinaryOp(null,
                                        new NumericLiteral(null, "2"),
                                        "-",
                                        new NumericLiteral(null, "1")
                                )
                        )
                ),
                translateToAst("dx *= -1;\ndy = -1;\ndz = 2 - 1;")
        );
    }

    @Test
    void parsesHeapAccesses() {
        assertEquals(
                new Seq(null,
                        new Assignment(null,
                                new HeapAccess(null, "cell2", new NumericLiteral(null, "1")),
                                new HeapAccess(null, "cell3", new NumericLiteral(null, "0"))
                        )
                ),
                translateToAst("cell2[1] = cell3[0];")
        );
    }

    @Test
    void parsesGlobalReferences() {
        assertEquals(
                new Seq(null,
                        new Seq(null,
                                new Seq(null, new HeapAllocation(null, "cell2", 4, 5), new NoOp()),
                                new Assignment(null,
                                        new HeapAccess(null, "cell2", 0),
                                        new NumericLiteral(null, "1")
                                )
                        ),
                        new Assignment(null,
                                new HeapAccess(null, "cell2", 1),
                                new BinaryOp(null,
                                        new HeapAccess(null, "cell2", 0),
                                        "+",
                                        new NumericLiteral(null, "42")
                                )
                        )
                ),
                translateToAst("allocate heap in cell2[4..5];\n$dx = 1;\n$dy = $dx + 42;")
        );
    }


    @Test
    void parsesExponentiationAssignment() {
        assertEquals(
                new Seq(null,
                        new Assignment(null,
                                new HeapAccess(null, "cell1", new NumericLiteral(null, "0")),
                                new FunctionCall(null,
                                        "rand",
                                        fa(new BinaryOp(null,
                                                        new NumericLiteral(null, "9"),
                                                        "**",
                                                        new NumericLiteral(null, "9")
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("cell1[0] = rand(9**9);")
        );
    }

    @Test
    void parsesHeapReferencesWithRvalues() {
        assertEquals(
                new Seq(null,
                        new Assignment(null,
                                new HeapAccess(null, "cell1", new VarRef(null, "dx")),
                                new NumericLiteral(null, "1")
                        )
                ),
                translateToAst("cell1[dx] = 1;")
        );
    }

    @Test
    void parsesComments() {
        assertEquals(
                new Seq(null,
                        new Assignment(null, new VarRef(null, "wasInitialized"), new NumericLiteral(null, "1"))
                ),
                translateToAst(
                        """
                                // Remember that we initialized ourselves
                                // This is required otherwise we'll repeat ourselves
                                wasInitialized = 1;
                                """
                )
        );
    }

    @Test
    void parsesMathFunctions() {
        assertEquals(
                new Seq(null,
                        new Assignment(null,
                                new VarRef(null, "x"),
                                new FunctionCall(null,
                                        "ceil",
                                        fa(new FunctionCall(null,
                                                "floor",
                                                fa(new FunctionCall(null,
                                                        "sin",
                                                        fa(new FunctionCall(null,
                                                                "log",
                                                                fa(new FunctionCall(null,
                                                                        "cos",
                                                                        fa(new FunctionCall(null,
                                                                                "abs",
                                                                                fa(new FunctionCall(null,
                                                                                        "tan",
                                                                                        fa(new FunctionCall(null, "rand",
                                                                                                fa(new NumericLiteral(null, "1"))
                                                                                        ))
                                                                                ))
                                                                        ))
                                                                ))
                                                        ))
                                                ))
                                        ))
                                )
                        )
                ),
                translateToAst("x = ceil(floor(sin(log(cos(abs(tan(rand(1))))))));")
        );
    }

    @Test
    void parsesRefsWithDashInThem() {
        assertEquals(
                new Seq(null,
                        new FunctionCall(null,
                                "build",
                                fa(
                                        new VarRef(null, "x"),
                                        new VarRef(null, "y"),
                                        new Ref(null, "@titanium-conveyor", true),
                                        new NumericLiteral(null, "0"),
                                        new NumericLiteral(null, "0")
                                )
                        )
                ),
                translateToAst("build(x, y, @titanium-conveyor, 0, 0);")
        );
    }

    @Test
    void parsesFlagAssignment() {
        assertEquals(
                new Seq(null,
                        new NoOp(),
                        new FunctionCall(null,
                                "flag",
                                fa(new VarRef(null, "FLAG"))
                        )
                ),
                translateToAst("flag(FLAG);")
        );
    }

    @Test
    void parsesAddressCalculationReferences() {
        assertEquals(
                new Seq(null,
                        new Assignment(null,
                                new HeapAccess(null, "cell1", new VarRef(null, "ptr")),
                                new BinaryOp(null,
                                        new HeapAccess(null,
                                                "cell1",
                                                new BinaryOp(null,
                                                        new VarRef(null, "ptr"),
                                                        "-",
                                                        new NumericLiteral(null, "1")
                                                )
                                        ),
                                        "+",
                                        new HeapAccess(null,
                                                "cell1",
                                                new BinaryOp(null,
                                                        new VarRef(null, "ptr"),
                                                        "-",
                                                        new NumericLiteral(null, "2")
                                                )
                                        )

                                )
                        )
                ),
                translateToAst("cell1[ptr] = cell1[ptr - 1] + cell1[ptr - 2];")
        );
    }

    @Test
    void parsesHeapAllocationWithExclusiveRange() {
        assertEquals(
                new Seq(null,
                        new HeapAllocation(null, "cell2", new ExclusiveRange(null, new NumericLiteral(null, 0), new NumericLiteral(null, 64))),
                        new NoOp()
                ),
                translateToAst("allocate heap in cell2[ 0 ... 64 ];")
        );
    }

    @Test
    void parsesHeapAllocationWithInclusiveRange() {
        assertEquals(
                new Seq(null,
                        new HeapAllocation(null, "cell2", 0, 30),
                        new NoOp()
                ),
                translateToAst("allocate heap in cell2[0 .. 30];")
        );
    }

    @Test
    void rejectsHeapUsageWhenUnallocated() {
        assertThrows(UnexpectedMessageException.class, () -> translateToAst("$dx = 1;"));
    }

    @Test
    void parsesPropertyAccesses() {
        assertEquals(
                new Seq(null,
                        new Seq(null,
                                new BinaryOp(null,
                                        new PropertyAccess(null, new VarRef(null, "foundation1"), new Ref(null, "@copper", true)),
                                        "<",
                                        new PropertyAccess(null, new VarRef(null, "foundation1"), new Ref(null, "@itemCapacity", true))
                                )
                        ),
                        new BinaryOp(null,
                                new PropertyAccess(null, new VarRef(null, "reactor1"), new Ref(null, "@cryofluid", true)),
                                "<",
                                new NumericLiteral(null, "10")
                        )
                ),
                translateToAst("""
                        foundation1.@copper < foundation1.@itemCapacity;
                        reactor1.@cryofluid < 10;
                        """)
        );
    }

    @Test
    void parsesWriteToPropertyAccess() {
        assertEquals(
                new Seq(null,
                        new Assignment(null,
                                new PropertyAccess(null, new VarRef(null, "conveyor1"), new Ref(null, "@enabled", true)),
                                new BinaryOp(null,
                                        new PropertyAccess(null, new VarRef(null, "CORE"), new Ref(null, "@copper", true)),
                                        "<",
                                        new PropertyAccess(null, new VarRef(null, "CORE"), new Ref(null, "@itemCapacity", true))
                                )
                        )
                ),
                translateToAst("conveyor1.enabled = CORE.@copper < CORE.@itemCapacity;")
        );
    }

    @Test
    void parsesUsefulWhileLoop() {
        assertEquals(
                new Seq(null,
                        new Seq(null,
                                new Assignment(null, new VarRef(null, "n"), new NumericLiteral(null, "5"))
                        ),
                        new WhileExpression(null, null,
                                new NoOp(),
                                new BinaryOp(null, new VarRef(null, "n"), ">", new NumericLiteral(null, "0")),
                                new Seq(null,
                                        new Assignment(null,
                                                new VarRef(null, "n"),
                                                new BinaryOp(null,
                                                        new VarRef(null, "n"),
                                                        "-",
                                                        new NumericLiteral(null, "1")
                                                )
                                        )
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("""
                        n = 5;
                        while n > 0 do
                            n -= 1;
                        end;
                        """)
        );
    }


    @Test
    void parsesInclusiveIteratorStyleLoop() {
        assertEquals(
                new Seq(null,
                        new RangedForExpression(null,
                                null,
                                new VarRef(null, "n"),
                                new InclusiveRange(null, new NumericLiteral(null, 1), new NumericLiteral(null, 17)),
                                new Seq(null, new FunctionCall(null, "print", fa(new VarRef(null, "n"))))
                        )
                ),
                translateToAst("""
                        for n in 1 .. 17 do
                            print(n);
                        end;
                        """)
        );
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertEquals(
                new Seq(null,
                        new RangedForExpression(null,
                                null,
                                new VarRef(null, "n"),
                                new ExclusiveRange(null, new NumericLiteral(null, 1), new NumericLiteral(null, 17)),
                                new Seq(null, new FunctionCall(null, "print", fa(new VarRef(null, "n"))))
                        )
                ),
                translateToAst("""
                        for n in 1 ... 17 do
                            print(n);
                        end;
                        """)
        );
    }

    @Test
    void parsesForEachLoop() {
        assertEquals(
                new Seq(null,
                        new ForEachExpression(null,
                                null,
                                List.of(
                                        new Iterator(null, true, false, new VarRef(null, "a"))
                                ),
                                List.of(
                                        new Ref(null, "@mono", true),
                                        new Ref(null, "@poly", true),
                                        new Ref(null, "@mega", true)
                                ),
                                new Seq(null,
                                        new FunctionCall(null,
                                                "print",
                                                fa(new VarRef(null, "a"))
                                        )
                                )
                        )
                ),
                translateToAst("""
                        for a in @mono, @poly, @mega do
                            print(a);
                        end;
                        """)
        );
    }

    @Test
    void parsesCStyleLoop() {
        assertEquals(new Seq(null,
                        new WhileExpression(null, null,
                                new Seq(null,
                                        new Assignment(null, new VarRef(null, "i"), new NumericLiteral(null, "0")),
                                        new Assignment(null, new VarRef(null, "j"), new NumericLiteral(null, "-5"))
                                ),
                                new BinaryOp(null,
                                        new VarRef(null, "i"),
                                        "<",
                                        new NumericLiteral(null, "5")
                                ),
                                new Seq(null,
                                        new FunctionCall(null, "print", fa(new VarRef(null, "n")))
                                ),
                                new Seq(null,
                                        new Assignment(null,
                                                new VarRef(null, "j"),
                                                new BinaryOp(null,
                                                        new VarRef(null, "j"),
                                                        "-",
                                                        new NumericLiteral(null, "1")
                                                )
                                        ),
                                        new Assignment(null,
                                                new VarRef(null, "i"),
                                                new BinaryOp(null,
                                                        new VarRef(null, "i"),
                                                        "+",
                                                        new NumericLiteral(null, "1")
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("""
                        for i = 0, j = -5; i < 5; j -= 1, i += 1 do
                            print(n);
                        end;
                        """)
        );
    }

    @Test
    void parsesRefs() {
        assertEquals(
                new Seq(null,
                        new NoOp(),
                        new WhileExpression(null, null,
                                new NoOp(),
                                new BinaryOp(null,
                                        new Ref(null, "@unit", true),
                                        "===",
                                        new NullLiteral(null)
                                ),
                                new Seq(null,
                                        new FunctionCall(null,
                                                "ubind",
                                                fa(new VarRef(null, "poly"))
                                        )
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("""
                        while @unit === null do
                            ubind(poly);
                        end;
                        """)
        );
    }


    @Test
    void parsesIfExpression() {
        assertEquals(
                new Seq(null,
                        new IfExpression(null,
                                new BinaryOp(null,
                                        new VarRef(null, "n"),
                                        ">",
                                        new NumericLiteral(null, "4")
                                ),
                                new Seq(null,
                                        new Assignment(null,
                                                new HeapAccess(null, "cell1", new NumericLiteral(null, "2")),
                                                new BinaryOp(null,
                                                        new HeapAccess(null, "cell1", new NumericLiteral(null, "2")),
                                                        "+",
                                                        new NumericLiteral(null, "1")
                                                )
                                        )
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("if n > 4 then cell1[2] += 1; end;")
        );
    }

    @Test
    void parsesIfElseExpression() {
        assertEquals(
                new Seq(null,
                        new Assignment(null,
                                new VarRef(null, "value"),
                                new IfExpression(null,
                                        new BinaryOp(null,
                                                new HeapAccess(null, "cell1", new NumericLiteral(null, "4")),
                                                "==",
                                                new NumericLiteral(null, "0")
                                        ),
                                        new Seq(null, new BooleanLiteral(null, false)),
                                        new Seq(null,
                                                new Seq(null,
                                                        new Assignment(null,
                                                                new HeapAccess(null, "cell1", new NumericLiteral(null, "4")),
                                                                new BooleanLiteral(null, true)
                                                        )
                                                ),
                                                new Assignment(null,
                                                        new VarRef(null, "n"),
                                                        new BinaryOp(null,
                                                                new VarRef(null, "n"),
                                                                "+",
                                                                new NumericLiteral(null, "1")
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("""
                        value = if cell1[4] == 0 then
                            false;
                        else
                            cell1[4] = true;
                            n += 1;
                        end;
                        """)
        );
    }

    @Test
    void parsesIfElseIf() {
        assertEquals(
                new Seq(null,
                        new IfExpression(null,
                                new BinaryOp(null,
                                        new VarRef(null, "state"),
                                        "==",
                                        new NumericLiteral(null, "1")),
                                new Seq(null, new FunctionCall(null,
                                        "print",
                                        fa(new VarRef(null, "m")))),
                                new Seq(null, new IfExpression(null,
                                        new BinaryOp(null,
                                                new VarRef(null, "state"),
                                                "==",
                                                new NumericLiteral(null, "2")),
                                        new Seq(null, new FunctionCall(null,
                                                "print",
                                                fa(new VarRef(null, "n")))),
                                        new NoOp())))),
                translateToAst("""
                        if state == 1 then
                            print(m);
                        else
                            if state == 2 then
                                print(n);
                            end;
                        end;
                        """)
        );
    }

    @Test
    void parsesCaseWhen() {
        assertEquals(
                new Seq(null,
                        new Assignment(null, new VarRef(null, "__ast0"), new VarRef(null, "n")),
                        new CaseExpression(null,
                                new VarRef(null, "__ast0"),
                                List.of(
                                        new CaseAlternative(null, new NumericLiteral(null, "1"), new Seq(null, new StringLiteral(null, "1"))),
                                        new CaseAlternative(null, new NumericLiteral(null, "2"), new Seq(null, new StringLiteral(null, "two")))
                                ),
                                new Seq(null, new StringLiteral(null, "otherwise"))
                        )
                ),
                translateToAst("""
                        case n
                            when 1 then
                                "1";
                            when 2 then
                                "two";
                            else
                                "otherwise";
                        end;
                        """)
        );
    }

    @Test
    void parsesCaseWhenMultipleWithRange() {
        assertEquals(
                new Seq(null,
                        new Assignment(null, new VarRef(null, "__ast0"), new VarRef(null, "n")),
                        new CaseExpression(null,
                                new VarRef(null, "__ast0"),
                                List.of(
                                        new CaseAlternative(null,
                                                List.of(
                                                        new InclusiveRange(null, new NumericLiteral(null, "0"), new NumericLiteral(null, "4")),
                                                        new InclusiveRange(null, new NumericLiteral(null, "6"), new NumericLiteral(null, "8")),
                                                        new NumericLiteral(null, "10"),
                                                        new NumericLiteral(null, "12")
                                                ),
                                                new Seq(null, new StringLiteral(null, "A number I like"))
                                        )
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("case n when 0 .. 4, 6 .. 8, 10, 12 then \"A number I like\"; end;")
        );
    }

    @Test
    void acceptsSemicolonAsStatementSeparator() {
        assertEquals(
                new Seq(null,
                        new Seq(null,
                                new Assignment(null, new VarRef(null, "a"), new NumericLiteral(null, "0"))
                        ),
                        new Assignment(null, new VarRef(null, "b"), new NumericLiteral(null, "1"))
                ),
                translateToAst("a=0;b=1;")
        );
    }

    @Test
    void supportsCallingTheEndFunction() {
        assertEquals(
                new Seq(null,
                        new IfExpression(null,
                                new BinaryOp(null,
                                        new VarRef(null, "some_cond"),
                                        "==",
                                        new BooleanLiteral(null, false)
                                ),
                                new Seq(null,
                                        new FunctionCall(null, "end")
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("""
                        if some_cond == false then
                            end();
                        end;
                        """)
        );
    }

    @Test
    void supportsModuloOperator() {
        assertEquals(
                new Seq(null,
                        new Assignment(null,
                                new VarRef(null, "running"),
                                new BinaryOp(null,
                                        new BinaryOp(null,
                                                new Ref(null, "@tick", true),
                                                "%",
                                                new NumericLiteral(null, "2")
                                        ),
                                        "==",
                                        new NumericLiteral(null, "0")
                                )
                        )
                ),
                translateToAst("running = @tick % 2 == 0;")
        );
    }

    @Test
    void supportsDeclaringAStack() {
        assertEquals(
                new Seq(null, new StackAllocation(null, "cell1", 0, 63)),
                translateToAst("allocate stack in cell1[0..63];")
        );
    }

    @Test
    void rejectsDualStackAllocation() {
        assertThrows(UnexpectedMessageException.class, () ->
                translateToAst("allocate stack in cell1[0...64], stack in cell2[0...512];")
        );
    }

    @Test
    void supportFunctionDeclarations() {
        assertEquals(
                prettyPrint(
                        new Seq(null,
                                new FunctionDeclaration(null, false, false, false,
                                        "delay",
                                        List.of(),
                                        new Seq(null,
                                                new Seq(null,
                                                        new Seq(null,
                                                                new Assignment(null,
                                                                        new VarRef(null, "n"), new NumericLiteral(null, "0")
                                                                )
                                                        ),
                                                        new Assignment(null,
                                                                new VarRef(null, "deadline"),
                                                                new BinaryOp(null,
                                                                        new Ref(null, "@tick", true),
                                                                        "+",
                                                                        new NumericLiteral(null, "60")
                                                                )
                                                        )
                                                ),
                                                new WhileExpression(null, null,
                                                        new NoOp(),
                                                        new BinaryOp(null,
                                                                new Ref(null, "@tick", true),
                                                                "<",
                                                                new VarRef(null, "deadline")
                                                        ),
                                                        new Seq(null,
                                                                new Assignment(null,
                                                                        new VarRef(null, "n"),
                                                                        new BinaryOp(null, new VarRef(null, "n"),
                                                                                "+",
                                                                                new NumericLiteral(null, "1")
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
                        translateToAst("""
                                def delay()
                                  n = 0;
                                  deadline = @tick + 60;
                                  while @tick < deadline do
                                    n += 1;
                                  end;
                                end;
                                """)
                )
        );
    }

    @Test
    void supportsCallingDeclaredFunctions() {
        assertEquals(
                prettyPrint(
                        new Seq(null,
                                new Seq(null,
                                        new FunctionDeclaration(null, false, false, false,
                                                "foo",
                                                List.of(),
                                                new Seq(null,
                                                        new Assignment(null, new VarRef(null, "n"),
                                                                new BinaryOp(null, new VarRef(null, "n"), "+", new NumericLiteral(null, "1"))
                                                        )
                                                )
                                        )
                                ),
                                new FunctionCall(null, "foo")
                        )
                ),
                prettyPrint(
                        translateToAst("""
                                def foo()
                                    n=n+1;
                                end;
                                foo();
                                """
                        )
                )
        );
    }

    @Test
    void supportsDeclaringFunctionsThatAcceptParameters() {
        assertEquals(
                prettyPrint(
                        new Seq(null,
                                new Seq(null,
                                        new FunctionDeclaration(null, false, false, false,
                                                "foo",
                                                List.of(new FunctionParameter(null, "s", false, false, false)),
                                                new Seq(null,
                                                        new BinaryOp(null, new VarRef(null, "s"), "+", new NumericLiteral(null, "1"))
                                                )
                                        )
                                ),
                                new FunctionCall(null, "foo", fa(new NumericLiteral(null, "1")))
                        )
                ),
                prettyPrint(
                        translateToAst("""
                                def foo(s)
                                  s + 1;
                                end;
                                foo(1);
                                """
                        )
                )
        );
    }

    @Test
    void supportsDeclaringFunctionsWithInitializers() {
        assertEquals(
                prettyPrint(
                        new Seq(null,
                                new Seq(null,
                                        new FunctionDeclaration(null, false, false, false,
                                                "foo",
                                                List.of(
                                                        new FunctionParameter(null, "s", false, false, false),
                                                        new FunctionParameter(null, "r", false, false, false)
                                                ),
                                                new Seq(null,
                                                        new BinaryOp(null,
                                                                new BinaryOp(null, new VarRef(null, "s"), "+", new NumericLiteral(null, "1")),
                                                                "+",
                                                                new VarRef(null, "r")
                                                        )
                                                )
                                        )
                                ),
                                new FunctionCall(null, "foo",
                                        fa(new NumericLiteral(null, "1")),
                                        fa(new NumericLiteral(null, "6"))
                                )
                        )
                ),
                prettyPrint(
                        translateToAst("""
                                def foo(s, r)
                                  s + 1 + r;
                                end;
                                foo(1, 6);
                                """
                        )
                )
        );
    }

    @Test
    void supportsControllingBuildingsThroughPropAccessFunctionCalls() {
        assertEquals(
                prettyPrint(
                        new Seq(null,
                                new Control(null,
                                        new VarRef(null, "turret"),
                                        "shoot",
                                        fa(
                                                new PropertyAccess(null, new VarRef(null, "leader"), new Ref(null, "@shootX", true)),
                                                new PropertyAccess(null, new VarRef(null, "leader"), new Ref(null, "@shootY", true)),
                                                new PropertyAccess(null, new VarRef(null, "leader"), new Ref(null, "@shooting", true))
                                        )
                                )
                        )
                ),
                prettyPrint(
                        translateToAst("turret.shoot(leader.@shootX, leader.@shootY, leader.@shooting);\n")
                )
        );
    }

    @Test
    void supportsBitwiseAndOrXorShiftLeftAndShiftRight() {
        assertEquals(
                prettyPrint(
                        new Seq(null,
                                new BinaryOp(null,
                                        new BinaryOp(null,
                                                new BinaryOp(null,
                                                        new NumericLiteral(null, "9842"),
                                                        "&",
                                                        new NumericLiteral(null, "1")
                                                ),
                                                "^",
                                                new BinaryOp(null,
                                                        new NumericLiteral(null, "1"),
                                                        "<<",
                                                        new NumericLiteral(null, "4")
                                                )
                                        ),
                                        "|",
                                        new BinaryOp(null,
                                                new VarRef(null, "y"),
                                                ">>",
                                                new NumericLiteral(null, "1")
                                        )
                                )
                        )
                ),
                prettyPrint(
                        translateToAst("(9842 & 1) ^ (1 << 4) | y >> 1;\n")
                )
        );
    }

    @Test
    void correctlyParsesElsif() {
        assertEquals(
                new Seq(null,
                        new IfExpression(null,
                                new BooleanLiteral(null, false),
                                new Seq(null, new NumericLiteral(null, 1)),
                                new IfExpression(null,
                                        new BooleanLiteral(null, true),
                                        new Seq(null, new NumericLiteral(null, 2)),
                                        new Seq(null, new NumericLiteral(null, 3))
                                )
                        )
                ),
                translateToAst(
                        "if false then 1; elsif true then 2; else 3; end;"
                )
        );
    }

    @Test
    void correctlyParsesIndirectPropertyReference() {
        assertEquals(
                new Seq(null,
                        new Seq(null,
                                new Assignment(null, new VarRef(null, "resource"), new Ref(null, "@silicon", true))
                        ),
                        new IfExpression(null,
                                new BinaryOp(null,
                                        new PropertyAccess(null, new VarRef(null, "vault1"), new VarRef(null, "resource")),
                                        "<",
                                        new PropertyAccess(null, new VarRef(null, "vault1"), new Ref(null, "@itemCapacity", true))
                                ),
                                new Seq(null,
                                        new FunctionCall(null, "harvest", fa(new VarRef(null, "vault1"), new VarRef(null, "resource")))
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("""
                        resource = @silicon;
                        if vault1.sensor(resource) < vault1.@itemCapacity then
                            harvest(vault1, resource);
                        end;
                        """
                )
        );
    }

    @Test
    void correctlyReadsTernaryOperator() {
        assertEquals(
                new Seq(null,
                        new FunctionCall(null,
                                "print",
                                fa(
                                        new StringLiteral(null, "sm.enabled: "),
                                        new IfExpression(null,
                                                new PropertyAccess(null,
                                                        new VarRef(null, "smelter1"),
                                                        new Ref(null, "@enabled", true)
                                                ),
                                                new StringLiteral(null, "true"),
                                                new StringLiteral(null, "false")
                                        )
                                )
                        )
                ),
                translateToAst("""
                        print("sm.enabled: ", smelter1.@enabled ? "true" : "false");
                        """)
        );
    }

    @Test
    void correctlyParsesStrictNotEqual() {
        assertEquals(
                new Seq(null,
                        new Assignment(null,
                                new VarRef(null, "a"),
                                new BinaryOp(null,
                                        new BinaryOp(null,
                                                new Ref(null, "@unit", true),
                                                "===",
                                                new NullLiteral(null)
                                        ),
                                        "==",
                                        new BooleanLiteral(null, false)
                                )
                        )
                ),
                translateToAst("a = @unit !== null;")
        );
    }

    @Test
    void correctlyParsesBreakContinue() {
        assertEquals(
                new Seq(null,
                        new Seq(null,
                                new WhileExpression(null, null,
                                        new NoOp(),
                                        new VarRef(null, "a"),
                                        new Seq(null,
                                                new IfExpression(null,
                                                        new VarRef(null, "b"),
                                                        new Seq(null, new ContinueStatement(null, null)),
                                                        new IfExpression(null,
                                                                new VarRef(null, "c"),
                                                                new Seq(null, new BreakStatement(null, null)),
                                                                new NoOp()
                                                        )
                                                )
                                        ),
                                        new NoOp()
                                )
                        ),
                        new FunctionCall(null, "print", fa(new StringLiteral(null, "End")))
                ),

                translateToAst("""
                        while a do
                          if b then
                            continue;
                          elsif c then
                            break;
                          end;
                        end;
                        print("End");
                        """
                )
        );
    }

    @Test
    void parsesListDirective() {
        assertEquals(
                new Seq(null,
                        new Directive(null,
                                new DirectiveText(null, "sort-variables"),
                                List.of(
                                        new DirectiveText(null, "PARAMS"),
                                        new DirectiveText(null, "GLOBALS")
                                )
                        )
                ),
                translateToAst("#set sort-variables = PARAMS, GLOBALS;")

        );
    }

    @Test
    void parsesEmptyListDirective() {
        assertEquals(
                new Seq(null,
                        new Directive(null,
                                new DirectiveText(null, "sort-variables"),
                                List.of()
                        )
                ),
                translateToAst("#set sort-variables;")

        );
    }

    @Test
    void generatesRelaxedSyntaxWarning() {
        assertGeneratesMessages(
                ExpectedMessages.create().add(1, 1, "The relaxed syntax is deprecated."),
                """
                        #relaxed;
                        print("Relaxed");
                        """
        );
    }

    @Test
    void generatesForEachLoopParenthesesWarning() {
        assertGeneratesMessages(
                ExpectedMessages.create().add(1, 11, "Usage of parentheses around value list in list iteration loops is deprecated."),
                """
                        for i in (1, 2, 3) do
                            print(i);
                        end;
                        """
        );
    }

    @Test
    void generatesEscapedQuotesWarning() {
        assertGeneratesMessages(
                ExpectedMessages.create().add(1, 7, "Usage of double quotes in string literals is deprecated."),
                """
                        print("\\"Hey!\\"");
                        """
        );
    }
}
