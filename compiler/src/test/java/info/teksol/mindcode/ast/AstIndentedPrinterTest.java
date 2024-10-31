package info.teksol.mindcode.ast;

import info.teksol.mindcode.AbstractAstTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Order(99)
public class AstIndentedPrinterTest extends AbstractAstTest {

    private String printLinearized(String program) {
        return AstIndentedPrinter.printIndented(translateToAst(program), 1);
    }

    private String printNested(String program) {
        return AstIndentedPrinter.printIndented(translateToAst(program), 2);
    }

    @Test
    void printsLinearizedSeq() {
        assertEquals("""
                        Seq[
                            FunctionCall{
                                functionName='print',
                                arguments=[FunctionArgument{
                                    expression=VarRef{name='a'},
                                    inModifier=false,
                                    outModifier=false
                                }]
                            },
                            FunctionCall{
                                functionName='print',
                                arguments=[FunctionArgument{
                                    expression=VarRef{name='b'},
                                    inModifier=false,
                                    outModifier=false
                                }]
                            },
                            FunctionCall{
                                functionName='print',
                                arguments=[FunctionArgument{
                                    expression=VarRef{name='c'},
                                    inModifier=false,
                                    outModifier=false
                                }]
                            }
                        ]""",
                printLinearized("""
                        print(a);
                        print(b);
                        print(c);
                        """
                )
        );
    }

    @Test
    void printsNestedSeq() {
        assertEquals("""
                        Seq{
                            rest=Seq{
                                rest=Seq{rest=NoOp{}, last=FunctionCall{
                                    functionName='print',
                                    arguments=[FunctionArgument{
                                        expression=VarRef{name='a'},
                                        inModifier=false,
                                        outModifier=false
                                    }]
                                }},
                                last=FunctionCall{
                                    functionName='print',
                                    arguments=[FunctionArgument{
                                        expression=VarRef{name='b'},
                                        inModifier=false,
                                        outModifier=false
                                    }]
                                }
                            },
                            last=FunctionCall{
                                functionName='print',
                                arguments=[FunctionArgument{
                                    expression=VarRef{name='c'},
                                    inModifier=false,
                                    outModifier=false
                                }]
                            }
                        }""",
                printNested("""
                        print(a);
                        print(b);
                        print(c);
                        """
                )
        );
    }

    @Test
    void printsAssignmentsOpsAndCase() {
        assertEquals("""
                        Seq[
                            Assignment{
                                var=VarRef{name='x'},
                                value=NumericLiteral{literal='10'}
                            },
                            Assignment{
                                var=VarRef{name='y'},
                                value=BinaryOp{
                                    left=NumericLiteral{literal='20'},
                                    op='+',
                                    right=NumericLiteral{literal='30'}
                                }
                            },
                            Assignment{
                                var=VarRef{name='z'},
                                value=UnaryOp{
                                    op='~',
                                    expression=VarRef{name='y'}
                                }
                            },
                            Assignment{
                                var=VarRef{name='b'},
                                value=BooleanLiteral{value=true}
                            },
                            Seq{
                                rest=Assignment{
                                    var=VarRef{name='__ast0'},
                                    value=VarRef{name='x'}
                                },
                                last=CaseExpression{
                                    condition=VarRef{name='__ast0'},
                                    alternatives=[
                                        CaseAlternative{
                                            values=[NumericLiteral{literal='0'}],
                                            body=Seq{rest=NoOp{}, last=StringLiteral{text='zero'}}
                                        },
                                        CaseAlternative{
                                            values=[
                                                VarRef{name='y'},
                                                VarRef{name='z'}
                                            ],
                                            body=Seq{rest=NoOp{}, last=StringLiteral{text='y or z'}}
                                        }
                                    ],
                                    elseBranch=Seq{rest=NoOp{}, last=StringLiteral{text='other'}}
                                }
                            }
                        ]""",
                printLinearized("""
                        x = 10;
                        y = 20 + 30;
                        z = ~y;
                        b = true;
                        case x
                          when 0 then "zero";
                          when y, z then "y or z";
                          else "other";
                        end;
                        """
                )
        );
    }

    @Test
    void printsWhileIfBreak() {
        assertEquals("""
                        Seq[
                            WhileExpression{
                                label=null,
                                initialization=NoOp{},
                                condition=BooleanLiteral{value=true},
                                body=Seq[
                                    FunctionCall{
                                        functionName='print',
                                        arguments=[FunctionArgument{
                                            expression=StringLiteral{text='In loop'},
                                            inModifier=false,
                                            outModifier=false
                                        }]
                                    },
                                    IfExpression{
                                        condition=BinaryOp{
                                            left=PropertyAccess{
                                                target=Ref{name='@unit'},
                                                property=Ref{name='@dead'}
                                            },
                                            op='===',
                                            right=NumericLiteral{literal='0'}
                                        },
                                        trueBranch=Seq{rest=NoOp{}, last=BreakStatement{}},
                                        falseBranch=NoOp{}
                                    }
                                ]
                            },
                            FunctionCall{
                                functionName='print',
                                arguments=[FunctionArgument{
                                    expression=StringLiteral{text='Out of loop'},
                                    inModifier=false,
                                    outModifier=false
                                }]
                            }
                        ]""",
                printLinearized("""
                        while true do
                          print("In loop");
                          if @unit.@dead === 0 then
                            break;
                          end;
                        end;
                        print("Out of loop");
                        """
                )
        );
    }

    @Test
    void printsStackAllocationFunctionDefCall() {
        assertEquals("""
                        Seq[
                            StackAllocation{
                                name='bank1',
                                range=ExclusiveRange{firstValue=NumericLiteral{literal='0'}, lastValue=NumericLiteral{literal='512'}}
                            },
                            FunctionDeclaration{
                                name='fib',
                                inline=false,
                                noinline=false,
                                procedure=false,
                                params=[FunctionParameter{name='n', inModifier=false, outModifier=false, varArgs=false}],
                                body=Seq{rest=NoOp{}, last=IfExpression{
                                    condition=BinaryOp{
                                        left=VarRef{name='n'},
                                        op='<=',
                                        right=NumericLiteral{literal='0'}
                                    },
                                    trueBranch=Seq{rest=NoOp{}, last=NumericLiteral{literal='0'}},
                                    falseBranch=Seq{
                                        rest=Assignment{
                                            var=VarRef{name='__ast0'},
                                            value=VarRef{name='n'}
                                        },
                                        last=CaseExpression{
                                            condition=VarRef{name='__ast0'},
                                            alternatives=[CaseAlternative{
                                                values=[NumericLiteral{literal='1'}],
                                                body=Seq{rest=NoOp{}, last=NumericLiteral{literal='1'}}
                                            }],
                                            elseBranch=Seq{rest=NoOp{}, last=BinaryOp{
                                                left=FunctionCall{
                                                    functionName='fib',
                                                    arguments=[FunctionArgument{
                                                        expression=BinaryOp{
                                                            left=VarRef{name='n'},
                                                            op='-',
                                                            right=NumericLiteral{literal='1'}
                                                        },
                                                        inModifier=false,
                                                        outModifier=false
                                                    }]
                                                },
                                                op='+',
                                                right=FunctionCall{
                                                    functionName='fib',
                                                    arguments=[FunctionArgument{
                                                        expression=BinaryOp{
                                                            left=VarRef{name='n'},
                                                            op='-',
                                                            right=NumericLiteral{literal='2'}
                                                        },
                                                        inModifier=false,
                                                        outModifier=false
                                                    }]
                                                }
                                            }}
                                        }
                                    }
                                }}
                            },
                            FunctionCall{
                                functionName='print',
                                arguments=[FunctionArgument{
                                    expression=FunctionCall{
                                        functionName='fib',
                                        arguments=[FunctionArgument{
                                            expression=NumericLiteral{literal='5'},
                                            inModifier=false,
                                            outModifier=false
                                        }]
                                    },
                                    inModifier=false,
                                    outModifier=false
                                }]
                            }
                        ]""",
                printLinearized("""
                        allocate stack in bank1[0...512];
                        
                        def fib(n)
                          if n <= 0 then
                            0;
                          else
                            case n
                            when 1 then
                              1;
                            else
                              fib(n - 1) + fib(n - 2);
                            end;
                          end;
                        end;
                        
                        print(fib(5));
                        """
                )
        );
    }
}
