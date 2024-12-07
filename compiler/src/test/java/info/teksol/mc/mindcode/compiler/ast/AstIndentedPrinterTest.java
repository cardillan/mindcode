package info.teksol.mc.mindcode.compiler.ast;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.generated.ast.AstIndentedPrinter;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@NullMarked
public class AstIndentedPrinterTest extends AbstractAstBuilderTest {

    protected void assertPrints(ExpectedMessages expectedMessages, String source, String expected) {
        AstMindcodeNode program = build(expectedMessages, InputFiles.fromSource(source));
        String actual = new AstIndentedPrinter().print(program);
        Assertions.assertEquals(expected, actual);
    }

    protected void assertPrints(String source, String expected) {
        assertPrints(expectedMessages(), source, expected);
    }

    @Test
    void printsFunctionCalls() {
        assertPrints("""
                        print(a);
                        print(b);
                        print(c);
                        """,
                """
                        AstModule {
                            statements = [
                                AstFunctionCall {
                                    object = null,
                                    identifier = AstIdentifier{name = "print", external = false},
                                    arguments = AstFunctionArgument {
                                        expression = AstIdentifier{name = "a", external = false},
                                        inModifier = false,
                                        outModifier = false
                                    }
                                },
                                AstFunctionCall {
                                    object = null,
                                    identifier = AstIdentifier{name = "print", external = false},
                                    arguments = AstFunctionArgument {
                                        expression = AstIdentifier{name = "b", external = false},
                                        inModifier = false,
                                        outModifier = false
                                    }
                                },
                                AstFunctionCall {
                                    object = null,
                                    identifier = AstIdentifier{name = "print", external = false},
                                    arguments = AstFunctionArgument {
                                        expression = AstIdentifier{name = "c", external = false},
                                        inModifier = false,
                                        outModifier = false
                                    }
                                }
                            ]
                        }"""
        );
    }

    @Test
    void printsStackAllocationFunctionDefCall() {
        assertPrints("""
                        allocate stack in bank1[0...512];
                        
                        def fib(n)
                            if n <= 0 then
                                0;
                            else
                                case n
                                    when 1 then 1;
                                    else fib(n - 1) + fib(n - 2);
                                end;
                            end;
                        end;
                        
                        print(fib(5));
                        """,
                """
                        AstModule {
                            statements = [
                                AstAllocations{allocations = AstAllocation{type = STACK, memory = AstIdentifier{name = "bank1", external = false}, range = AstRange {
                                    firstValue = AstLiteralDecimal{literal = "0", suppressWarning = false},
                                    lastValue = AstLiteralDecimal{literal = "512", suppressWarning = false},
                                    exclusive = true
                                }}},
                                AstFunctionDeclaration {
                                    identifier = AstIdentifier{name = "fib", external = false},
                                    dataType = VAR,
                                    parameters = AstFunctionParameter {
                                        identifier = AstIdentifier{name = "n", external = false},
                                        inModifier = false,
                                        outModifier = false,
                                        varargs = false
                                    },
                                    body = AstIfExpression {
                                        ifBranches = AstIfBranch {
                                            condition = AstOperatorBinary {
                                                operation = LESS_THAN_EQ,
                                                left = AstIdentifier{name = "n", external = false},
                                                right = AstLiteralDecimal{literal = "0", suppressWarning = false}
                                            },
                                            body = AstLiteralDecimal{literal = "0", suppressWarning = false}
                                        },
                                        elseBranch = AstCaseExpression {
                                            expression = AstIdentifier{name = "n", external = false},
                                            alternatives = AstCaseAlternative {
                                                values = AstLiteralDecimal{literal = "1", suppressWarning = false},
                                                body = AstLiteralDecimal{literal = "1", suppressWarning = false}
                                            },
                                            elseBranch = AstOperatorBinary {
                                                operation = ADD,
                                                left = AstFunctionCall {
                                                    object = null,
                                                    identifier = AstIdentifier{name = "fib", external = false},
                                                    arguments = AstFunctionArgument {
                                                        expression = AstOperatorBinary {
                                                            operation = SUB,
                                                            left = AstIdentifier{name = "n", external = false},
                                                            right = AstLiteralDecimal{literal = "1", suppressWarning = false}
                                                        },
                                                        inModifier = false,
                                                        outModifier = false
                                                    }
                                                },
                                                right = AstFunctionCall {
                                                    object = null,
                                                    identifier = AstIdentifier{name = "fib", external = false},
                                                    arguments = AstFunctionArgument {
                                                        expression = AstOperatorBinary {
                                                            operation = SUB,
                                                            left = AstIdentifier{name = "n", external = false},
                                                            right = AstLiteralDecimal{literal = "2", suppressWarning = false}
                                                        },
                                                        inModifier = false,
                                                        outModifier = false
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    inline = false,
                                    noinline = false
                                },
                                AstFunctionCall {
                                    object = null,
                                    identifier = AstIdentifier{name = "print", external = false},
                                    arguments = AstFunctionArgument {
                                        expression = AstFunctionCall {
                                            object = null,
                                            identifier = AstIdentifier{name = "fib", external = false},
                                            arguments = AstFunctionArgument {
                                                expression = AstLiteralDecimal{literal = "5", suppressWarning = false},
                                                inModifier = false,
                                                outModifier = false
                                            }
                                        },
                                        inModifier = false,
                                        outModifier = false
                                    }
                                }
                            ]
                        }"""
        );
    }

    @Test
    void printsWhileIfBreak() {
        assertPrints("""
                        while true do
                            print("In loop");
                            if @unit.@dead === 0 then
                                a[i] = 10;
                                break;
                            end;
                        end;
                        print("Out of loop");
                        """,
                """
                        AstModule {
                            statements = [
                                AstWhileLoopStatement {
                                    label = null,
                                    condition = AstLiteralBoolean{literal = "true", suppressWarning = false},
                                    body = [
                                        AstFunctionCall {
                                            object = null,
                                            identifier = AstIdentifier{name = "print", external = false},
                                            arguments = AstFunctionArgument {
                                                expression = AstLiteralString{literal = "In loop", suppressWarning = false},
                                                inModifier = false,
                                                outModifier = false
                                            }
                                        },
                                        AstIfExpression {
                                            ifBranches = AstIfBranch {
                                                condition = AstOperatorBinary {
                                                    operation = STRICT_EQUAL,
                                                    left = AstPropertyAccess{object = AstBuiltInIdentifier{name = "@unit"}, property = AstBuiltInIdentifier{name = "@dead"}},
                                                    right = AstLiteralDecimal{literal = "0", suppressWarning = false}
                                                },
                                                body = [
                                                    AstAssignment {
                                                        operation = null,
                                                        target = AstArrayAccess {
                                                            array = AstIdentifier{name = "a", external = false},
                                                            index = AstIdentifier{name = "i", external = false}
                                                        },
                                                        value = AstLiteralDecimal{literal = "10", suppressWarning = false}
                                                    },
                                                    AstBreakStatement{label = null}
                                                ]
                                            },
                                            elseBranch = null
                                        }
                                    ],
                                    entryCondition = true
                                },
                                AstFunctionCall {
                                    object = null,
                                    identifier = AstIdentifier{name = "print", external = false},
                                    arguments = AstFunctionArgument {
                                        expression = AstLiteralString{literal = "Out of loop", suppressWarning = false},
                                        inModifier = false,
                                        outModifier = false
                                    }
                                }
                            ]
                        }"""
        );
    }

}
