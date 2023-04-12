package info.teksol.mindcode.ast;

import info.teksol.mindcode.AbstractAstTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AstIndentedPrinterTest extends AbstractAstTest {
    
    private String printLinearized(String program) {
        return AstIndentedPrinter.printIndented(translateToAst(program), 1);
    }

    private String printNested(String program) {
        return AstIndentedPrinter.printIndented(translateToAst(program), 2);
    }

    @Test
    void printsLinearizedSeq() {
        assertEquals("" +
                "Seq[\n" +
                "    FunctionCall{\n" +
                "        functionName='print',\n" +
                "        params=[VarRef{name='a'}]\n" +
                "    },\n" +
                "    FunctionCall{\n" +
                "        functionName='print',\n" +
                "        params=[VarRef{name='b'}]\n" +
                "    },\n" +
                "    FunctionCall{\n" +
                "        functionName='print',\n" +
                "        params=[VarRef{name='c'}]\n" +
                "    }\n" +
                "]",
                printLinearized("" +
                        "print(a)\n" +
                        "print(b)\n" +
                        "print(c)"
                )
        );
    }

    @Test
    void printsNestedSeq() {
        assertEquals("" +
                "Seq{\n" +
                "    rest=Seq{\n" +
                "        rest=Seq{rest=NoOp{}, last=FunctionCall{\n" +
                "            functionName='print',\n" +
                "            params=[VarRef{name='a'}]\n" +
                "        }},\n" +
                "        last=FunctionCall{\n" +
                "            functionName='print',\n" +
                "            params=[VarRef{name='b'}]\n" +
                "        }\n" +
                "    },\n" +
                "    last=FunctionCall{\n" +
                "        functionName='print',\n" +
                "        params=[VarRef{name='c'}]\n" +
                "    }\n" +
                "}",
                printNested("" +
                        "print(a)\n" +
                        "print(b)\n" +
                        "print(c)"
                )
        );
    }

    @Test
    void printsAssignmentsOpsAndCase() {
        assertEquals("" +
                "Seq[\n" +
                "    Assignment{\n" +
                "        var=VarRef{name='x'},\n" +
                "        value=NumericLiteral{literal='10'}\n" +
                "    },\n" +
                "    Assignment{\n" +
                "        var=VarRef{name='y'},\n" +
                "        value=BinaryOp{\n" +
                "            left=NumericLiteral{literal='20'},\n" +
                "            op='+',\n" +
                "            right=NumericLiteral{literal='30'}\n" +
                "        }\n" +
                "    },\n" +
                "    Assignment{\n" +
                "        var=VarRef{name='z'},\n" +
                "        value=UnaryOp{\n" +
                "            op='~',\n" +
                "            expression=VarRef{name='y'}\n" +
                "        }\n" +
                "    },\n" +
                "    Assignment{\n" +
                "        var=VarRef{name='b'},\n" +
                "        value=BooleanLiteral{value=true}\n" +
                "    },\n" +
                "    Seq{\n" +
                "        rest=Assignment{\n" +
                "            var=VarRef{name='__ast0'},\n" +
                "            value=VarRef{name='x'}\n" +
                "        },\n" +
                "        last=CaseExpression{\n" +
                "            condition=VarRef{name='__ast0'},\n" +
                "            alternatives=[\n" +
                "                CaseAlternative{\n" +
                "                    values=[NumericLiteral{literal='0'}],\n" +
                "                    body=Seq{rest=NoOp{}, last=StringLiteral{text='zero'}}\n" +
                "                },\n" +
                "                CaseAlternative{\n" +
                "                    values=[\n" +
                "                        VarRef{name='y'},\n" +
                "                        VarRef{name='z'}\n" +
                "                    ],\n" +
                "                    body=Seq{rest=NoOp{}, last=StringLiteral{text='y or z'}}\n" +
                "                }\n" +
                "            ],\n" +
                "            elseBranch=Seq{rest=NoOp{}, last=StringLiteral{text='other'}}\n" +
                "        }\n" +
                "    }\n" +
                "]",
                printLinearized("" +
                        "x = 10\n" +
                        "y = 20 + 30\n" +
                        "z = ~y\n" +
                        "b = true\n" +
                        "case x\n" +
                        "  when 0 \"zero\"\n" +
                        "  when y, z \"y or z\"\n" +
                        "  else \"other\"\n" +
                        "end"
                )
        );
    }

    @Test
    void printsWhileIfBreak() {
        assertEquals("" +
                "Seq[\n" +
                "    WhileExpression{\n" +
                "        label=null,\n" +
                "        condition=BooleanLiteral{value=true},\n" +
                "        body=Seq[\n" +
                "            FunctionCall{\n" +
                "                functionName='print',\n" +
                "                params=[StringLiteral{text='In loop'}]\n" +
                "            },\n" +
                "            IfExpression{\n" +
                "                condition=BinaryOp{\n" +
                "                    left=PropertyAccess{\n" +
                "                        target=Ref{name='unit'},\n" +
                "                        property=Ref{name='dead'}\n" +
                "                    },\n" +
                "                    op='===',\n" +
                "                    right=NumericLiteral{literal='0'}\n" +
                "                },\n" +
                "                trueBranch=Seq{rest=NoOp{}, last=BreakStatement{}},\n" +
                "                falseBranch=NoOp{}\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    FunctionCall{\n" +
                "        functionName='print',\n" +
                "        params=[StringLiteral{text='Out of loop'}]\n" +
                "    }\n" +
                "]",
                printLinearized("" +
                        "while true\n" +
                        "  print(\"In loop\")\n" +
                        "  if @unit.dead === 0\n" +
                        "    break\n" +
                        "  end\n" +
                        "end\n" +
                        "print(\"Out of loop\")"
                )
        );
    }

    @Test
    void printsStackAllocationFunctionDefCall() {
        assertEquals("" +
                "Seq[\n" +
                "    StackAllocation{\n" +
                "        name='bank1',\n" +
                "        range=ExclusiveRange{firstValue=NumericLiteral{literal='0'}, lastValue=NumericLiteral{literal='512'}}\n" +
                "    },\n" +
                "    FunctionDeclaration{\n" +
                "        name='fib',\n" +
                "        params=[VarRef{name='n'}],\n" +
                "        body=Seq{rest=NoOp{}, last=IfExpression{\n" +
                "            condition=BinaryOp{\n" +
                "                left=VarRef{name='n'},\n" +
                "                op='<=',\n" +
                "                right=NumericLiteral{literal='0'}\n" +
                "            },\n" +
                "            trueBranch=Seq{rest=NoOp{}, last=NumericLiteral{literal='0'}},\n" +
                "            falseBranch=Seq{\n" +
                "                rest=Assignment{\n" +
                "                    var=VarRef{name='__ast0'},\n" +
                "                    value=VarRef{name='n'}\n" +
                "                },\n" +
                "                last=CaseExpression{\n" +
                "                    condition=VarRef{name='__ast0'},\n" +
                "                    alternatives=[CaseAlternative{\n" +
                "                        values=[NumericLiteral{literal='1'}],\n" +
                "                        body=Seq{rest=NoOp{}, last=NumericLiteral{literal='1'}}\n" +
                "                    }],\n" +
                "                    elseBranch=Seq{rest=NoOp{}, last=BinaryOp{\n" +
                "                        left=FunctionCall{\n" +
                "                            functionName='fib',\n" +
                "                            params=[BinaryOp{\n" +
                "                                left=VarRef{name='n'},\n" +
                "                                op='-',\n" +
                "                                right=NumericLiteral{literal='1'}\n" +
                "                            }]\n" +
                "                        },\n" +
                "                        op='+',\n" +
                "                        right=FunctionCall{\n" +
                "                            functionName='fib',\n" +
                "                            params=[BinaryOp{\n" +
                "                                left=VarRef{name='n'},\n" +
                "                                op='-',\n" +
                "                                right=NumericLiteral{literal='2'}\n" +
                "                            }]\n" +
                "                        }\n" +
                "                    }}\n" +
                "                }\n" +
                "            }\n" +
                "        }}\n" +
                "    },\n" +
                "    FunctionCall{\n" +
                "        functionName='print',\n" +
                "        params=[FunctionCall{\n" +
                "            functionName='fib',\n" +
                "            params=[NumericLiteral{literal='5'}]\n" +
                "        }]\n" +
                "    }\n" +
                "]",
                printLinearized("" +
                        "allocate stack in bank1[0...512]\n" +
                        "\n" +
                        "def fib(n)\n" +
                        "  if n <= 0\n" +
                        "    0\n" +
                        "  else\n" +
                        "    case n\n" +
                        "    when 1\n" +
                        "      1\n" +
                        "    else\n" +
                        "      fib(n - 1) + fib(n - 2)\n" +
                        "    end\n" +
                        "  end\n" +
                        "end\n" +
                        "\n" +
                        "print(fib(5))"
                )
        );
    }
}
