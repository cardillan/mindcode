package info.teksol.mc.mindcode.compiler.ast;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.mindcode.compiler.CallType;
import info.teksol.mc.mindcode.compiler.DataType;
import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstOperatorIncDec.Type;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static info.teksol.mc.mindcode.logic.arguments.Operation.ADD;
import static info.teksol.mc.mindcode.logic.arguments.Operation.SUB;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
class AstBuilderTest extends AbstractAstBuilderTest {

    @Nested
    class Arrays {
        @Test
        void buildsArrayAccess() {
            assertBuildsTo("""
                            array[index];
                            """,
                    List.of(
                            new AstArrayAccess(EMPTY,
                                    new AstIdentifier(EMPTY, "array"),
                                    new AstIdentifier(EMPTY, "index")
                            )
                    )
            );
        }

        @Test
        void buildsNestedArrayAccess() {
            assertBuildsTo("""
                            outer[inner[index]];
                            """,
                    List.of(
                            new AstArrayAccess(EMPTY,
                                    new AstIdentifier(EMPTY, "outer"),
                                    new AstArrayAccess(EMPTY,
                                            new AstIdentifier(EMPTY, "inner"),
                                            new AstIdentifier(EMPTY, "index"))
                            )
                    )
            );
        }

        @Test
        void buildsSubarray() {
            assertBuildsTo("""
                            array[10 ... 20];
                            """,
                    List.of(
                            new AstSubarray(EMPTY,
                                    new AstIdentifier(EMPTY, "array"),
                                    new AstRange(EMPTY, number(10), number(20), true)
                            )
                    )
            );
        }
    }

    @Nested
    class Assignments {
        @Test
        void buildsSimpleAssignments() {
            assertBuildsTo("""
                            a = b;
                            a = b = c;
                            a[b] = c;
                            a[b] = a[c] = d;
                            @a = @b;
                            """,
                    List.of(
                            new AstAssignment(EMPTY, null, a, b),
                            new AstAssignment(EMPTY, null, a, new AstAssignment(EMPTY, null, b, c)),
                            new AstAssignment(EMPTY, null, new AstArrayAccess(EMPTY, a, b), c),
                            new AstAssignment(EMPTY, null,
                                    new AstArrayAccess(EMPTY, a, b),
                                    new AstAssignment(EMPTY, null,
                                            new AstArrayAccess(EMPTY, a, c),
                                            d)
                            ),
                            new AstAssignment(EMPTY, null,
                                    new AstBuiltInIdentifier(EMPTY, "@a"),
                                    new AstBuiltInIdentifier(EMPTY, "@b"))
                    )
            );
        }

        @Test
        void buildsCompoundAssignments() {
            assertBuildsTo("""
                            a **= b;
                            a *= b /= c \\= d %= e;
                            a += b -= c <<= d >>= e;
                            a &= b |= c ^= d;
                            a &&= b ||= c;
                            """,
                    List.of(
                            new AstAssignment(EMPTY, Operation.POW, a, b),
                            new AstAssignment(EMPTY, Operation.MUL, a,
                                    new AstAssignment(EMPTY, Operation.DIV, b,
                                            new AstAssignment(EMPTY, Operation.IDIV, c,
                                                    new AstAssignment(EMPTY, Operation.MOD, d,
                                                            e)))),
                            new AstAssignment(EMPTY, ADD, a,
                                    new AstAssignment(EMPTY, Operation.SUB, b,
                                            new AstAssignment(EMPTY, Operation.SHL, c,
                                                    new AstAssignment(EMPTY, Operation.SHR, d,
                                                            e)))),
                            new AstAssignment(EMPTY, Operation.BITWISE_AND, a,
                                    new AstAssignment(EMPTY, Operation.BITWISE_OR, b,
                                            new AstAssignment(EMPTY, Operation.BITWISE_XOR, c,
                                                    d))),
                            new AstAssignment(EMPTY, Operation.BOOLEAN_AND, a,
                                    new AstAssignment(EMPTY, Operation.BOOLEAN_OR, b,
                                            c))
                    )
            );
        }

        @Test
        void buildsCompoundAssignmentsWithArrays() {
            assertBuildsTo("""
                            a[b] += c[d];
                            """,
                    List.of(
                            new AstAssignment(EMPTY, ADD,
                                    new AstArrayAccess(EMPTY, a, b),
                                    new AstArrayAccess(EMPTY, c, d)
                            )
                    )
            );
        }

        @Test
        void buildsCompoundAssignmentsWithExpressions() {
            assertBuildsTo("""
                            a += b ? c : d;
                            """,
                    List.of(
                            new AstAssignment(EMPTY, ADD,
                                    a,
                                    new AstOperatorTernary(EMPTY, b, c, d)
                            )
                    )
            );
        }
    }

    @Nested
    class BasicStructure {
        @Test
        void buildsEmptyProgram() {
            assertBuildsTo(";;;",
                    List.of()
            );
        }

        @Test
        void buildsCodeBlocks() {
            assertBuildsTo("""
                            begin
                                identifier;
                            end;
                            """,
                    List.of(
                            new AstCodeBlock(EMPTY, List.of(identifier))
                    )
            );
        }

        @Test
        void buildsEmptyCodeBlocks() {
            assertBuildsTo("""
                            begin
                            end;
                            """,
                    List.of(
                            new AstCodeBlock(EMPTY, List.of())
                    )
            );
        }

        @Test
        void buildsComments1() {
            assertBuildsTo("""
                            /*  A commented out // single-line comment */
                            identifier;
                            """,
                    List.of(identifier)
            );
        }

        @Test
        void buildsComments2() {
            assertBuildsTo("""
                            //  This is not an opening comment /*
                            identifier;
                            """,
                    List.of(identifier)
            );
        }

        @Test
        void buildsComments3() {
            assertBuildsTo("""
                            /*
                            //  This is a closing */
                            identifier;
                            """,
                    List.of(identifier)
            );
        }

        @Test
        void buildsUnusedDocComments() {
            assertBuildsTo("""
                            /** This is a doc comment */
                            identifier;
                            /** This is another doc comment */
                            """,
                    List.of(identifier)
            );
        }

        @Test
        void buildsConstantsWithDocComments() {
            AstModule module = build(expectedMessages(),
                    InputFiles.fromSource("""
                            /** This is a doc comment */
                            const x = 10;
                            """
                    )
            );
            assertEquals("/** This is a doc comment */",
                    Objects.requireNonNull(module.getStatements().getFirst().getDocComment()).getComment());
        }

        @Test
        void buildsParametersWithDocComments() {
            AstModule module = build(expectedMessages(),
                    InputFiles.fromSource("""
                            /** This is a doc comment */
                            param x = 10;
                            """
                    )
            );
            assertEquals("/** This is a doc comment */",
                    Objects.requireNonNull(module.getStatements().getFirst().getDocComment()).getComment());
        }

        @Test
        void buildsFunctionsWithDocComments() {
            AstModule module = build(expectedMessages(),
                    InputFiles.fromSource("""
                            /** This is a doc comment */
                            def foo(x) end;
                            """
                    )
            );
            assertEquals("/** This is a doc comment */",
                    Objects.requireNonNull(module.getStatements().getFirst().getDocComment()).getComment());
        }

        @Test
        void refusesMisplacedExpressions() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 7, "Parse error: .*"),
                    """
                            while const a = 10 do
                                print(x);
                            end;
                            """);
        }
    }

    @Nested
    class BinaryOperations {
        @Test
        void buildsAdditionAndBitShifts() {
            assertBuildsTo("""
                            left+right;
                            left-right;
                            left<<right;
                            left>>right;
                            """,
                    List.of(
                            new AstOperatorBinary(EMPTY, ADD, left, right),
                            new AstOperatorBinary(EMPTY, Operation.SUB, left, right),
                            new AstOperatorBinary(EMPTY, Operation.SHL, left, right),
                            new AstOperatorBinary(EMPTY, Operation.SHR, left, right)
                    )
            );
        }

        @Test
        void buildsBitwiseOperations() {
            assertBuildsTo("""
                            left&right;
                            left|right;
                            left^right;
                            """,
                    List.of(
                            new AstOperatorBinary(EMPTY, Operation.BITWISE_AND, left, right),
                            new AstOperatorBinary(EMPTY, Operation.BITWISE_OR, left, right),
                            new AstOperatorBinary(EMPTY, Operation.BITWISE_XOR, left, right)
                    )
            );
        }

        @Test
        void buildsBooleanOperations() {
            assertBuildsTo("""
                            left&&right;
                            left and right;
                            left||right;
                            left or right;
                            """,
                    List.of(
                            new AstOperatorBinary(EMPTY, Operation.BOOLEAN_AND, left, right),
                            new AstOperatorBinary(EMPTY, Operation.LOGICAL_AND, left, right),
                            new AstOperatorBinary(EMPTY, Operation.BOOLEAN_OR, left, right),
                            new AstOperatorBinary(EMPTY, Operation.LOGICAL_OR, left, right)
                    )
            );
        }

        @Test
        void buildsEqualities() {
            assertBuildsTo("""
                            left==right;
                            left!=right;
                            left===right;
                            left!==right;
                            """,
                    List.of(
                            new AstOperatorBinary(EMPTY, Operation.EQUAL, left, right),
                            new AstOperatorBinary(EMPTY, Operation.NOT_EQUAL, left, right),
                            new AstOperatorBinary(EMPTY, Operation.STRICT_EQUAL, left, right),
                            new AstOperatorBinary(EMPTY, Operation.STRICT_NOT_EQUAL, left, right)
                    )
            );
        }

        @Test
        void buildsExponentiation() {
            assertBuildsTo("""
                            left**right;
                            """,
                    List.of(
                            new AstOperatorBinary(EMPTY, Operation.POW, left, right)
                    )
            );
        }

        @Test
        void buildsInequalities() {
            assertBuildsTo("""
                            left<right;
                            left<=right;
                            left>=right;
                            left>right;
                            """,
                    List.of(
                            new AstOperatorBinary(EMPTY, Operation.LESS_THAN, left, right),
                            new AstOperatorBinary(EMPTY, Operation.LESS_THAN_EQ, left, right),
                            new AstOperatorBinary(EMPTY, Operation.GREATER_THAN_EQ, left, right),
                            new AstOperatorBinary(EMPTY, Operation.GREATER_THAN, left, right)
                    )
            );
        }

        @Test
        void buildsMultiplication() {
            assertBuildsTo("""
                            left*right;
                            left/right;
                            left\\right;
                            left%right;
                            """,
                    List.of(
                            new AstOperatorBinary(EMPTY, Operation.MUL, left, right),
                            new AstOperatorBinary(EMPTY, Operation.DIV, left, right),
                            new AstOperatorBinary(EMPTY, Operation.IDIV, left, right),
                            new AstOperatorBinary(EMPTY, Operation.MOD, left, right)
                    )
            );
        }

        @Test
        void buildsCompoundExpressions() {
            assertBuildsTo("""
                            a + b * c ** d >> e;
                            """,
                    List.of(
                            new AstOperatorBinary(EMPTY, Operation.SHR,
                                    new AstOperatorBinary(EMPTY, ADD,
                                            a,
                                            new AstOperatorBinary(EMPTY, Operation.MUL,
                                                    b,
                                                    new AstOperatorBinary(EMPTY, Operation.POW, c, d)
                                            )
                                    ),
                                    e)
                    )
            );
        }

        @Test
        void buildsParenthesizedExpressions() {
            assertBuildsTo("""
                            (a + b) * (c - d);
                            """,
                    List.of(
                            new AstOperatorBinary(EMPTY, Operation.MUL,
                                    new AstParentheses(EMPTY,
                                            new AstOperatorBinary(EMPTY, ADD, a, b)
                                    ),
                                    new AstParentheses(EMPTY,
                                            new AstOperatorBinary(EMPTY, Operation.SUB, c, d)
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsMassivelyParenthesizedExpressions() {
            assertBuildsTo("""
                            ((((a + b))));
                            """,
                    List.of(
                            new AstParentheses(EMPTY,
                                    new AstParentheses(EMPTY,
                                            new AstParentheses(EMPTY,
                                                    new AstParentheses(EMPTY,
                                                            new AstOperatorBinary(EMPTY, ADD, a, b)
                                                    )
                                            )
                                    )
                            )

                    )
            );
        }

        @Test
        void buildsExpressionsInProperOrder() {
            assertBuildsTo("""
                            a + b * c;
                            """,
                    List.of(
                            new AstOperatorBinary(EMPTY, Operation.ADD,
                                    a,
                                    new AstOperatorBinary(EMPTY, Operation.MUL, b, c)
                            )
                    )
            );
        }
    }

    @Nested
    class CaseExpressions {
        @Test
        void buildsCaseExpressionWithoutElse() {
            assertBuildsTo("""
                            case identifier
                                when 1 then a;
                                when 2 then b;
                            end;
                            """,
                    List.of(
                            new AstCaseExpression(EMPTY,
                                    identifier,
                                    List.of(
                                            new AstCaseAlternative(EMPTY, List.of(number((1))), List.of(a)),
                                            new AstCaseAlternative(EMPTY, List.of(number((2))), List.of(b))
                                    ),
                                    List.of()
                            )
                    )
            );
        }

        @Test
        void buildsCaseExpressionWithElse() {
            assertBuildsTo("""
                            case identifier
                                when 1 then a;
                                when 2 then b;
                                else c;
                            end;
                            """,
                    List.of(
                            new AstCaseExpression(EMPTY,
                                    identifier,
                                    List.of(
                                            new AstCaseAlternative(EMPTY, List.of(number((1))), List.of(a)),
                                            new AstCaseAlternative(EMPTY, List.of(number((2))), List.of(b))
                                    ),
                                    List.of(c)
                            )
                    )
            );
        }

        @Test
        void buildsCaseExpressionWithoutAlternatives() {
            assertBuildsTo("""
                            case identifier
                                else c;
                            end;
                            """,
                    List.of(
                            new AstCaseExpression(EMPTY,
                                    identifier,
                                    List.of(),
                                    List.of(c)
                            )
                    )
            );
        }

        @Test
        void buildsEmptyCaseExpression() {
            assertBuildsTo("""
                            case identifier
                            end;
                            """,
                    List.of(
                            new AstCaseExpression(EMPTY,
                                    identifier,
                                    List.of(),
                                    List.of()
                            )
                    )
            );
        }

        @Test
        void buildsCaseExpressionWithSeveralValues() {
            assertBuildsTo("""
                            case identifier
                                when 1, 2, 10 .. 20     then a;
                                when 30 ... 50, 4**5, c then b;
                                else c;
                            end;
                            """,
                    List.of(
                            new AstCaseExpression(EMPTY,
                                    identifier,
                                    List.of(
                                            new AstCaseAlternative(EMPTY,
                                                    List.of(
                                                            number(1),
                                                            number(2),
                                                            new AstRange(EMPTY, number(10), number(20), false)
                                                    ),
                                                    List.of(a)),
                                            new AstCaseAlternative(EMPTY,
                                                    List.of(
                                                            new AstRange(EMPTY, number(30), number(50), true),
                                                            new AstOperatorBinary(EMPTY, Operation.POW, number(4), number(5)),
                                                            c
                                                    ),
                                                    List.of(b))
                                    ),
                                    List.of(c)
                            )
                    )
            );
        }
    }

    @Nested
    class Declarations {
        @Test
        void buildsAllocationsSeparate() {
            assertBuildsTo("""
                            allocate heap in cell1;
                            allocate stack in cell2[0 .. 63];
                            """,
                    List.of(
                            new AstAllocations(EMPTY,
                                    List.of(
                                            new AstAllocation(EMPTY,
                                                    AstAllocation.AllocationType.HEAP,
                                                    id("cell1"),
                                                    null)
                                    )
                            ),
                            new AstAllocations(EMPTY,
                                    List.of(
                                            new AstAllocation(EMPTY,
                                                    AstAllocation.AllocationType.STACK,
                                                    id("cell2"),
                                                    new AstRange(EMPTY, number(0), number(63), false))
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsAllocationsCombined() {
            assertBuildsTo("""
                            allocate heap in HEAPPTR[0 ... 64], stack in bank1;
                            """,
                    List.of(
                            new AstAllocations(EMPTY,
                                    List.of(
                                            new AstAllocation(EMPTY,
                                                    AstAllocation.AllocationType.HEAP,
                                                    id("HEAPPTR"),
                                                    new AstRange(EMPTY, number(0), number(64), true)),
                                            new AstAllocation(EMPTY,
                                                    AstAllocation.AllocationType.STACK,
                                                    id("bank1"),
                                                    null)
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsAllocationsWithConstants() {
            assertBuildsTo("""
                            const MIN = 10;
                            const MAX = 20;
                            allocate heap in cell1[MIN .. MAX];
                            """,
                    List.of(
                            new AstConstant(EMPTY, null, id("MIN"), new AstLiteralDecimal(EMPTY, "10")),
                            new AstConstant(EMPTY, null, id("MAX"), new AstLiteralDecimal(EMPTY, "20")),
                            new AstAllocations(EMPTY,
                                    List.of(
                                            new AstAllocation(EMPTY,
                                                    AstAllocation.AllocationType.HEAP,
                                                    id("cell1"),
                                                    new AstRange(EMPTY, id("MIN"), id("MAX"), false))
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsConstants() {
            assertBuildsTo("""
                            /** Comment1 */
                            /** Comment2 */
                            // Anything else
                            const /** Comment3 */ MIN = 10;
                            """,
                    List.of(
                            new AstConstant(EMPTY,
                                    new AstDocComment(EMPTY, "/** Comment2 */"),
                                    id("MIN"),
                                    new AstLiteralDecimal(EMPTY, "10")
                            )
                    )
            );
        }

        @Test
        void buildsModule() {
            AstModuleDeclaration declaration = new AstModuleDeclaration(EMPTY, id("test"));
            assertBuildsTo("""
                            module test;
                            var a = 1;
                            """,
                    new AstModule(EMPTY,
                            declaration,
                            List.of(
                                    declaration,
                                    new AstVariablesDeclaration(EMPTY,
                                            List.of(),
                                            List.of(
                                                    new AstVariableSpecification(EMPTY, a, l1)
                                            )
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsParameters() {
            assertBuildsTo("""
                            /** Comment1 */
                            /** Comment2 */
                            // Anything else
                            param /** Comment3 */ MIN = 10;
                            """,
                    List.of(
                            new AstParameter(EMPTY,
                                    new AstDocComment(EMPTY, "/** Comment2 */"),
                                    id("MIN"),
                                    new AstLiteralDecimal(EMPTY, "10")
                            )
                    )
            );
        }

        @Test
        void buildsRemoteVariables() {
            AstModuleDeclaration declaration = new AstModuleDeclaration(EMPTY, id("test"));
            assertBuildsTo("""
                            module test;
                            remote var a = 1;
                            """,
                    new AstModule(EMPTY,
                            declaration,
                            List.of(
                                    declaration,
                                    new AstVariablesDeclaration(EMPTY,
                                            List.of(
                                                    new AstVariableModifier(EMPTY, Modifier.REMOTE, null)
                                            ),
                                            List.of(
                                                    new AstVariableSpecification(EMPTY, a, l1)
                                            )
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsVariables() {
            assertBuildsTo("""
                            var a, b = 1;
                            """,
                    List.of(
                            new AstVariablesDeclaration(EMPTY,
                                    List.of(),
                                    List.of(
                                            new AstVariableSpecification(EMPTY, a, null),
                                            new AstVariableSpecification(EMPTY, b, l1)
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsLinkedVariables() {
            assertBuildsTo("""
                            linked cell1, cell2;
                            """,
                    List.of(
                            new AstVariablesDeclaration(EMPTY,
                                    List.of(new AstVariableModifier(EMPTY, Modifier.LINKED, null)),
                                    List.of(
                                            new AstVariableSpecification(EMPTY, id("cell1"), null),
                                            new AstVariableSpecification(EMPTY, id("cell2"), null)
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsExternalVariables() {
            assertBuildsTo("""
                            external $a = 0;
                            external cached b = 1;
                            """,
                    List.of(
                            new AstVariablesDeclaration(EMPTY,
                                    List.of(new AstVariableModifier(EMPTY, Modifier.EXTERNAL, null)),
                                    List.of(
                                            new AstVariableSpecification(EMPTY, ext("a"), l0)
                                    )
                            ),
                            new AstVariablesDeclaration(EMPTY,
                                    List.of(
                                            new AstVariableModifier(EMPTY, Modifier.EXTERNAL, null),
                                            new AstVariableModifier(EMPTY, Modifier.CACHED, null)
                                    ),
                                    List.of(
                                            new AstVariableSpecification(EMPTY, id("b"), l1)
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsExternalVariablesWithMemory() {
            AstIdentifier memory = id("cell1");
            assertBuildsTo("""
                            external cell1 $a = 0;
                            external cell1[1] $b = 0;
                            external cell1[0 ... 1] $c = 0;
                            """,
                    List.of(
                            new AstVariablesDeclaration(EMPTY,
                                    List.of(new AstVariableModifier(EMPTY, Modifier.EXTERNAL,
                                            new AstExternalParameters(EMPTY, memory, null, null))),
                                    List.of(new AstVariableSpecification(EMPTY, ext("a"), l0))
                            ),
                            new AstVariablesDeclaration(EMPTY,
                                    List.of(new AstVariableModifier(EMPTY, Modifier.EXTERNAL,
                                            new AstExternalParameters(EMPTY, memory, null, number(1)))),
                                    List.of(new AstVariableSpecification(EMPTY, ext("b"), l0))
                            ),
                            new AstVariablesDeclaration(EMPTY,
                                    List.of(new AstVariableModifier(EMPTY, Modifier.EXTERNAL,
                                            new AstExternalParameters(EMPTY, memory,
                                                    new AstRange(EMPTY, l0, l1, true), null))),
                                    List.of(new AstVariableSpecification(EMPTY, ext("c"), l0))
                            )
                    )
            );
        }
    }

    @Nested
    class Directives {
        @Test
        void buildsValuelessSetDirective() {
            assertBuildsTo("""
                            #set option-name;
                            """,
                    List.of(
                            new AstDirectiveSet(EMPTY,
                                    new AstDirectiveValue(EMPTY, "option-name"),
                                    List.of()
                            )
                    )
            );
        }

        @Test
        void buildsSingleValueSetDirective() {
            assertBuildsTo("""
                            #set option-name = some-value;
                            """,
                    List.of(
                            new AstDirectiveSet(EMPTY,
                                    new AstDirectiveValue(EMPTY, "option-name"),
                                    List.of(
                                            new AstDirectiveValue(EMPTY, "some-value")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsMultipleValuesSetDirective() {
            assertBuildsTo("""
                            #set option-name = 7,7A,8;
                            """,
                    List.of(
                            new AstDirectiveSet(EMPTY,
                                    new AstDirectiveValue(EMPTY, "option-name"),
                                    List.of(
                                            new AstDirectiveValue(EMPTY, "7"),
                                            new AstDirectiveValue(EMPTY, "7A"),
                                            new AstDirectiveValue(EMPTY, "8")
                                    )
                            )
                    )
            );
        }
    }

    @Nested
    class Elements {
        @Test
        void buildsBasicElements() {
            assertBuildsTo(
                    """
                            // A comment
                            identifier;
                            @built-in-identifier;
                            a.b;
                            a.b.c;
                            """,
                    List.of(
                            identifier,
                            new AstBuiltInIdentifier(EMPTY, "@built-in-identifier"),
                            new AstMemberAccess(EMPTY, a, b),
                            new AstMemberAccess(EMPTY, new AstMemberAccess(EMPTY, a, b), c)
                    )
            );
        }

        @Test
        void buildsMemberAccess() {
            assertBuildsTo(
                    """
                            identifier.@type;
                            identifier.@firstItem.@id;
                            a.b.c.@id;
                            """,
                    List.of(
                            new AstPropertyAccess(EMPTY,
                                    identifier,
                                    new AstBuiltInIdentifier(EMPTY, "@type")),
                            new AstPropertyAccess(EMPTY,
                                    new AstPropertyAccess(EMPTY,
                                            identifier,
                                            new AstBuiltInIdentifier(EMPTY, "@firstItem")),
                                    new AstBuiltInIdentifier(EMPTY, "@id")),
                            new AstPropertyAccess(EMPTY,
                                    new AstMemberAccess(EMPTY, new AstMemberAccess(EMPTY, a, b), c),
                                    new AstBuiltInIdentifier(EMPTY, "@id"))
                    )
            );
        }
    }

    @Nested
    class EnhancedComments {
        @Test
        void buildsBasicEnhancedComment() {
            assertBuildsTo("""
                            ///foo///foo
                            """,
                    List.of(
                            new AstEnhancedComment(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo///foo")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsEnhancedCommentWithVariablePlaceholder() {
            assertBuildsTo("""
                            ///$foo/* comment */
                            """,
                    List.of(
                            new AstEnhancedComment(EMPTY,
                                    List.of(
                                            new AstIdentifier(EMPTY, "foo"),
                                            new AstLiteralString(EMPTY, "/"),
                                            new AstLiteralString(EMPTY, "* comment */")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsEnhancedCommentWithEmptyPlaceholder() {
            assertBuildsTo("""
                            ///foo$/* comment */
                            """,
                    List.of(
                            new AstEnhancedComment(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo"),
                                            new AstFormattablePlaceholder(EMPTY),
                                            new AstLiteralString(EMPTY, "/"),
                                            new AstLiteralString(EMPTY, "* comment */")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsEnhancedCommentWithEmptyPlaceholder2() {
            assertBuildsTo("""
                            ///foo${  }bar
                            """,
                    List.of(
                            new AstEnhancedComment(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo"),
                                            new AstFormattablePlaceholder(EMPTY),
                                            new AstLiteralString(EMPTY, "bar")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsEnhancedCommentWithInterpolation() {
            assertBuildsTo("""
                            ///foo ${ expression } bar
                            """,
                    List.of(
                            new AstEnhancedComment(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo "),
                                            new AstIdentifier(EMPTY, "expression"),
                                            new AstLiteralString(EMPTY, " bar")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsEnhancedCommentWithUnescapableCharacter() {
            assertBuildsTo("""
                            ///foo\\bar
                            """,
                    List.of(
                            new AstEnhancedComment(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo"),
                                            new AstLiteralString(EMPTY, "\\b"),
                                            new AstLiteralString(EMPTY, "ar")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsEnhancedCommentWithEscapedInterpolation() {
            assertBuildsTo("""
                            ///foo \\${ expression } bar
                            """,
                    List.of(
                            new AstEnhancedComment(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo "),
                                            new AstLiteralEscape(EMPTY, "$"),
                                            new AstLiteralString(EMPTY, "{ expression } bar")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsEnhancedCommentWithEscapedEscape() {
            assertBuildsTo("""
                            ///foo \\\\${ expression } bar
                            """,
                    List.of(
                            new AstEnhancedComment(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo "),
                                            new AstLiteralEscape(EMPTY, "\\"),
                                            new AstIdentifier(EMPTY, "expression"),
                                            new AstLiteralString(EMPTY, " bar")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsEnhancedCommentWithInterpolationAndComment() {
            assertBuildsTo("""
                            ///foo ${ expression /* a comment */ } bar
                            """,
                    List.of(
                            new AstEnhancedComment(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo "),
                                            new AstIdentifier(EMPTY, "expression"),
                                            new AstLiteralString(EMPTY, " bar")
                                    )
                            )
                    )
            );
        }
    }

    @Nested
    class ForEachLoops {
        @Test
        void buildsSimpleForEachLoop() {
            assertBuildsTo("""
                            Label:
                            for a in 10, 20, 30 do
                                print(a);
                            end;
                            """,
                    List.of(
                            new AstForEachLoopStatement(EMPTY,
                                    id("Label"),
                                    List.of(
                                            new AstIteratorsValuesGroup(EMPTY,
                                                    false,
                                                    List.of(new AstIterator(EMPTY, a, false)),
                                                    new AstExpressionList(EMPTY,
                                                            List.of(number(10), number(20), number(30)))
                                            )
                                    ),
                                    List.of(call(id("print"), arg(a)))
                            )
                    )
            );
        }

        @Test
        void buildsMultipleIteratorsForEachLoop() {
            assertBuildsTo("""
                            Label:
                            for var a, b in 10, 20, 30, 40 do
                                print(a + b);
                            end;
                            """,
                    List.of(
                            new AstForEachLoopStatement(EMPTY,
                                    id("Label"),
                                    List.of(
                                            new AstIteratorsValuesGroup(EMPTY,
                                                    true,
                                                    List.of(
                                                            new AstIterator(EMPTY, a, false),
                                                            new AstIterator(EMPTY, b, false)
                                                    ),
                                                    new AstExpressionList(EMPTY,
                                                            List.of(number(10), number(20), number(30), number(40))
                                                    )
                                            )
                                    ),
                                    List.of(
                                            call(id("print"),
                                                    arg(new AstOperatorBinary(EMPTY, ADD, a, b))
                                            )
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsOutputIteratorsForEachLoop() {
            assertBuildsTo("""
                            Label:
                            for var out i, out j in a, b, c, d do
                                j = i;
                            end;
                            """,
                    List.of(
                            new AstForEachLoopStatement(EMPTY,
                                    id("Label"),
                                    List.of(
                                            new AstIteratorsValuesGroup(EMPTY,
                                                    true,
                                                    List.of(
                                                            new AstIterator(EMPTY, id("i"), true),
                                                            new AstIterator(EMPTY, id("j"), true)
                                                    ),
                                                    new AstExpressionList(EMPTY, List.of(a, b, c, d))
                                            )
                                    ),
                                    List.of(new AstAssignment(EMPTY, null, id("j"), id("i")))
                            )
                    )
            );
        }

        @Test
        void buildsForEachLoopWithUnitTypes() {
            assertBuildsTo("""
                            for a in @mono, @poly, @mega do
                                print(a);
                            end;
                            """,
                    List.of(
                            new AstForEachLoopStatement(EMPTY,
                                    null,
                                    List.of(
                                            new AstIteratorsValuesGroup(EMPTY,
                                                    false,
                                                    List.of(new AstIterator(EMPTY, a, false)),
                                                    new AstExpressionList(EMPTY,
                                                            List.of(builtIn("@mono"), builtIn("@poly"), builtIn("@mega")))
                                            )
                                    ),
                                    List.of(call(id("print"), arg(a)))
                            )
                    )
            );
        }

        @Test
        void buildsParallelForEachLoop() {
            assertBuildsTo("""
                            for var a in 1, 2; var out b in c, d do
                                b = a;
                            end;
                            """,
                    List.of(
                            new AstForEachLoopStatement(EMPTY,
                                    null,
                                    List.of(
                                            new AstIteratorsValuesGroup(EMPTY,
                                                    true,
                                                    List.of(new AstIterator(EMPTY, a, false)),
                                                    new AstExpressionList(EMPTY, List.of(number(1), number(2)))
                                            ),
                                            new AstIteratorsValuesGroup(EMPTY,
                                                    true,
                                                    List.of(new AstIterator(EMPTY, b, true)),
                                                    new AstExpressionList(EMPTY, List.of(c, d))
                                            )
                                    ),
                                    List.of(new AstAssignment(EMPTY, null, b, a))
                            )
                    )
            );
        }
    }

    @Nested
    class Formattables {
        @Test
        void buildsBasicFormattable() {
            assertBuildsTo("""
                            $"foo";
                            """,
                    List.of(
                            new AstFormattableLiteral(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo")
                                    )
                            )
                    )
            );
        }

        void buildsFormattableWithVariablePlaceholder() {
            assertBuildsTo("""
                            $"$foo";
                            """,
                    List.of(
                            new AstFormattableLiteral(EMPTY,
                                    List.of(
                                            new AstIdentifier(EMPTY, "foo")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsFormattableWithEmptyPlaceholder() {
            assertBuildsTo("""
                            $"foo$";
                            """,
                    List.of(
                            new AstFormattableLiteral(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo"),
                                            new AstFormattablePlaceholder(EMPTY)
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsFormattableWithEmptyPlaceholder2() {
            assertBuildsTo("""
                            $"foo${ }bar";
                            """,
                    List.of(
                            new AstFormattableLiteral(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo"),
                                            new AstFormattablePlaceholder(EMPTY),
                                            new AstLiteralString(EMPTY, "bar")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsFormattableWithInterpolation() {
            assertBuildsTo("""
                            $"foo ${ expression } bar";
                            """,
                    List.of(
                            new AstFormattableLiteral(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo "),
                                            new AstIdentifier(EMPTY, "expression"),
                                            new AstLiteralString(EMPTY, " bar")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsFormattableWithUnescapableCharacter() {
            assertBuildsTo("""
                            $"foo\\bar";
                            """,
                    List.of(
                            new AstFormattableLiteral(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo"),
                                            new AstLiteralString(EMPTY, "\\b"),
                                            new AstLiteralString(EMPTY, "ar")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsFormattableWithEscapedInterpolation() {
            assertBuildsTo("""
                            $"foo \\${ expression } bar";
                            """,
                    List.of(
                            new AstFormattableLiteral(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo "),
                                            new AstLiteralEscape(EMPTY, "$"),
                                            new AstLiteralString(EMPTY, "{ expression } bar")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsFormattableWithEscapedEscape() {
            assertBuildsTo("""
                            $"foo \\\\${ expression } bar";
                            """,
                    List.of(
                            new AstFormattableLiteral(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo "),
                                            new AstLiteralEscape(EMPTY, "\\"),
                                            new AstIdentifier(EMPTY, "expression"),
                                            new AstLiteralString(EMPTY, " bar")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsFormattableWithMultilineInterpolation() {
            assertBuildsTo("""
                            $"foo ${
                                // A comment
                                expression
                                /* Another comment */
                            } bar";
                            """,
                    List.of(
                            new AstFormattableLiteral(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo "),
                                            new AstIdentifier(EMPTY, "expression"),
                                            new AstLiteralString(EMPTY, " bar")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsFormattableWithInterpolatedExpression() {
            assertBuildsTo("""
                            $"foo${left+right}bar";
                            """,
                    List.of(
                            new AstFormattableLiteral(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo"),
                                            new AstOperatorBinary(EMPTY, ADD, left, right),
                                            new AstLiteralString(EMPTY, "bar")
                                    )
                            )
                    )
            );
        }
    }

    @Nested
    class FunctionCalls {
        @Test
        void buildsFunctionCalls() {
            assertBuildsTo("""
                            end();
                            a();
                            b(a);
                            c(in a);
                            d(out a);
                            e(in out a);
                            f(out in b);
                            g(a, b);
                            h(in a, in b);
                            i(in a, out b);
                            j(out a, in b);
                            k(, b);
                            l(, in b);
                            """,
                    List.of(
                            call(id("end")),
                            call(id("a")),
                            call(id("b"), arg("a")),
                            call(id("c"), argIn("a")),
                            call(id("d"), argOut("a")),
                            call(id("e"), argInOut("a")),
                            call(id("f"), argInOut("b")),
                            call(id("g"), arg("a"), arg("b")),
                            call(id("h"), argIn("a"), argIn("b")),
                            call(id("i"), argIn("a"), argOut("b")),
                            call(id("j"), argOut("a"), argIn("b")),
                            call(id("k"), NONE, arg("b")),
                            call(id("l"), NONE, argIn("b"))
                    )
            );
        }

        @Test
        void buildsMethodCalls() {
            assertBuildsTo("""
                            object.a();
                            object.b(a);
                            object.c(in a);
                            object.d(out a);
                            object.e(in out a);
                            object.f(out in a);
                            object.g(a, b);
                            object.h(in a, in b);
                            object.i(in a, out b);
                            object.j(out a, in b);
                            object.k(, b);
                            object.l(, in b);
                            """,
                    List.of(
                            call(object, id("a")),
                            call(object, id("b"), arg("a")),
                            call(object, id("c"), argIn("a")),
                            call(object, id("d"), argOut("a")),
                            call(object, id("e"), argInOut("a")),
                            call(object, id("f"), argInOut("a")),
                            call(object, id("g"), arg("a"), arg("b")),
                            call(object, id("h"), argIn("a"), argIn("b")),
                            call(object, id("i"), argIn("a"), argOut("b")),
                            call(object, id("j"), argOut("a"), argIn("b")),
                            call(object, id("k"), NONE, arg("b")),
                            call(object, id("l"), NONE, argIn("b"))
                    )
            );
        }

        @Test
        void buildsComplexCalls() {
            assertBuildsTo("""
                            a(b(), c());
                            a().b();
                            a.b.c();
                            a().b().@id;
                            d = a(b, c) + e.b(c, d);
                            """,
                    List.of(
                            call(a, arg(call(b)), arg(call(c))),
                            call(call(a), b),
                            call(new AstMemberAccess(EMPTY, a, b), c),
                            new AstPropertyAccess(EMPTY,
                                    call(call(a), b),
                                    new AstBuiltInIdentifier(EMPTY, "@id")),
                            new AstAssignment(EMPTY, null,
                                    d,
                                    new AstOperatorBinary(EMPTY, ADD,
                                            call(a, arg(b), arg(c)),
                                            call(e, b, arg(c), arg(d))
                                    )
                            )
                    )
            );
        }
    }

    @Nested
    class FunctionDeclarations {
        @Test
        void buildsFunctionDeclarations() {
            assertBuildsTo("""
                            /** Comment1 */
                            def a() end;
                            /** Comment2 */
                            /** Comment3 */
                            inline def b(a...) a; end;
                            noinline void c(in a, out b, in out c, out in d) a + b; end;
                            remote def d() end;
                            """,
                    List.of(
                            new AstFunctionDeclaration(EMPTY,
                                    new AstDocComment(EMPTY, "/** Comment1 */"),
                                    a,
                                    DataType.VAR,
                                    List.of(),
                                    List.of(),
                                    CallType.NONE),
                            new AstFunctionDeclaration(EMPTY,
                                    new AstDocComment(EMPTY, "/** Comment3 */"),
                                    b,
                                    DataType.VAR,
                                    List.of(new AstFunctionParameter(EMPTY, a, false, false, true)),
                                    List.of(a),
                                    CallType.INLINE),
                            new AstFunctionDeclaration(EMPTY,
                                    null,
                                    c,
                                    DataType.VOID,
                                    List.of(
                                            new AstFunctionParameter(EMPTY, a, true, false, false),
                                            new AstFunctionParameter(EMPTY, b, false, true, false),
                                            new AstFunctionParameter(EMPTY, c, true, true, false),
                                            new AstFunctionParameter(EMPTY, d, true, true, false)
                                    ),
                                    List.of(new AstOperatorBinary(EMPTY, ADD, a, b)),
                                    CallType.NOINLINE),
                            new AstFunctionDeclaration(EMPTY,
                                    null,
                                    d,
                                    DataType.VAR,
                                    List.of(),
                                    List.of(),
                                    CallType.REMOTE)
                    )
            );
        }
    }

    @Nested
    class IfExpressions {
        @Test
        void buildsIfExpressionWithoutElse() {
            assertBuildsTo("""
                            if a then b; end;
                            if a then b; else end;
                            """,
                    List.of(
                            new AstIfExpression(EMPTY,
                                    new AstIfBranch(EMPTY, a, List.of(b)),
                                    List.of(),
                                    List.of()
                            ),
                            new AstIfExpression(EMPTY,
                                    new AstIfBranch(EMPTY, a, List.of(b)),
                                    List.of(),
                                    List.of()
                            )
                    )
            );
        }

        @Test
        void buildsIfExpressionWithElse() {
            assertBuildsTo("""
                            if a then b; c; else d; e; end;
                            """,
                    List.of(
                            new AstIfExpression(EMPTY,
                                    new AstIfBranch(EMPTY, a, List.of(b, c)),
                                    List.of(),
                                    List.of(d, e)
                            )
                    )
            );
        }

        @Test
        void buildsIfExpressionWithElsif() {
            assertBuildsTo("""
                            if a then b; c; elsif d then e; f; end;
                            """,
                    List.of(
                            new AstIfExpression(EMPTY,
                                    new AstIfBranch(EMPTY, a, List.of(b, c)),
                                    List.of(new AstIfBranch(EMPTY, d, List.of(e, id("f")))),
                                    List.of()
                            )
                    )
            );
        }

        @Test
        void buildsIfExpressionWithElsifs() {
            assertBuildsTo("""
                            if a then
                                b; c;
                            elsif d then
                                e; f;
                            elsif g then
                            end;
                            """,
                    List.of(
                            new AstIfExpression(EMPTY,
                                    new AstIfBranch(EMPTY, a, List.of(b, c)),
                                    List.of(
                                            new AstIfBranch(EMPTY, d, List.of(e, id("f"))),
                                            new AstIfBranch(EMPTY, id("g"), List.of())
                                    ),
                                    List.of()
                            )
                    )
            );
        }

        @Test
        void buildsIfExpressionWithElsifsAndElse() {
            assertBuildsTo("""
                            if a then
                                b; c;
                            elsif d then
                                e; f;
                            elsif g then
                                h;
                            else
                                i;
                            end;
                            """,
                    List.of(
                            new AstIfExpression(EMPTY,
                                    new AstIfBranch(EMPTY, a, List.of(b, c)),
                                    List.of(
                                            new AstIfBranch(EMPTY, d, List.of(e, id("f"))),
                                            new AstIfBranch(EMPTY, id("g"), List.of(id("h")))
                                    ),
                                    List.of(id("i"))
                            )
                    )
            );
        }
    }

    @Nested
    class IteratedForLoops {
        @Test
        void buildsSimpleIteratedForLoop() {
            assertBuildsTo("""
                            Label:
                            for a = 0; a < 10; a++ do
                                print(a);
                            end;
                            """,
                    List.of(
                            new AstIteratedForLoopStatement(EMPTY,
                                    id("Label"),
                                    List.of(new AstAssignment(EMPTY, null, a, number(0))),
                                    new AstOperatorBinary(EMPTY, Operation.LESS_THAN, a, number(10)),
                                    List.of(new AstOperatorIncDec(EMPTY, Type.POSTFIX, ADD, a)),
                                    List.of(call(id("print"), arg(a)))
                            )
                    )
            );
        }

        @Test
        void buildsEmptyIteratedForLoop() {
            assertBuildsTo("""
                            for ;; do
                            end;
                            """,
                    List.of(
                            new AstIteratedForLoopStatement(EMPTY,
                                    null,
                                    List.of(),
                                    null,
                                    List.of(),
                                    List.of()
                            )
                    )
            );
        }

        @Test
        void buildsComplexIteratedForLoop() {
            assertBuildsTo("""
                            for a = 0, b = 100; a < b; a++, --b do
                                print(a + b);
                            end;
                            """,
                    List.of(
                            new AstIteratedForLoopStatement(EMPTY,
                                    null,
                                    List.of(
                                            new AstAssignment(EMPTY, null, a, number(0)),
                                            new AstAssignment(EMPTY, null, b, number(100))
                                    ),
                                    new AstOperatorBinary(EMPTY, Operation.LESS_THAN, a, b),
                                    List.of(
                                            new AstOperatorIncDec(EMPTY, Type.POSTFIX, ADD, a),
                                            new AstOperatorIncDec(EMPTY, Type.PREFIX, SUB, b)
                                    ),
                                    List.of(
                                            call(id("print"), arg(new AstOperatorBinary(EMPTY, ADD, a, b)))
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsComplexIteratedForLoopWithDeclaration() {
            assertBuildsTo("""
                            for var a = 0, b = 100; a < b; a++, --b do
                                print(a + b);
                            end;
                            """,
                    List.of(
                            new AstIteratedForLoopStatement(EMPTY,
                                    null,
                                    List.of(
                                            new AstVariablesDeclaration(EMPTY,
                                                    List.of(),
                                                    List.of(
                                                            new AstVariableSpecification(EMPTY, a, number(0)),
                                                            new AstVariableSpecification(EMPTY, b, number(100))
                                                    )
                                            )
                                    ),
                                    new AstOperatorBinary(EMPTY, Operation.LESS_THAN, a, b),
                                    List.of(
                                            new AstOperatorIncDec(EMPTY, Type.POSTFIX, ADD, a),
                                            new AstOperatorIncDec(EMPTY, Type.PREFIX, SUB, b)
                                    ),
                                    List.of(
                                            call(id("print"), arg(new AstOperatorBinary(EMPTY, ADD, a, b)))
                                    )
                            )
                    )
            );
        }
    }

    @Nested
    class Literals {
        @Test
        void buildsLiterals() {
            assertBuildsTo("""
                            156;
                            156.156;
                            "156";
                            "";
                            0xFF;
                            0b10101010;
                            true;
                            false;
                            null;
                            0;
                            %00ff00ff;
                            %ff00ff;
                            """,
                    List.of(
                            number(156),
                            new AstLiteralFloat(EMPTY, "156.156"),
                            new AstLiteralString(EMPTY, "156"),
                            new AstLiteralString(EMPTY, ""),
                            new AstLiteralHexadecimal(EMPTY, "0xFF"),
                            new AstLiteralBinary(EMPTY, "0b10101010"),
                            new AstLiteralBoolean(EMPTY, "true"),
                            new AstLiteralBoolean(EMPTY, "false"),
                            new AstLiteralNull(EMPTY, "null"),
                            number(0),
                            new AstLiteralColor(EMPTY, "%00ff00ff"),
                            new AstLiteralColor(EMPTY, "%ff00ff")
                    )
            );
        }

        @Test
        void buildsNegativeLiterals() {
            assertBuildsTo("""
                            -156;
                            -156.156;
                            -0xFF;
                            -0b10101010;
                            -0;
                            """,
                    List.of(
                            new AstOperatorUnary(EMPTY, Operation.SUB, number(156)),
                            new AstOperatorUnary(EMPTY, Operation.SUB, new AstLiteralFloat(EMPTY, "156.156")),
                            new AstOperatorUnary(EMPTY, Operation.SUB, new AstLiteralHexadecimal(EMPTY, "0xFF")),
                            new AstOperatorUnary(EMPTY, Operation.SUB, new AstLiteralBinary(EMPTY, "0b10101010")),
                            new AstOperatorUnary(EMPTY, Operation.SUB, number(0))
                    )
            );
        }

        @Test
        void refusesInvalidColorLiterals() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add("Invalid format of color literal (supported formats are %rrggbb or %rrggbbaa).").repeat(3),
                    """
                            %12345;
                            %1234567;
                            %123456789;
                            """);
        }
    }

    @Nested
    class RangedForLoop {
        @Test
        void buildsRangedForLoop() {
            assertBuildsTo("""
                            for var a in 0 ... 64 do
                                print(a);
                            end;
                            """,
                    List.of(
                            new AstRangedForLoopStatement(EMPTY,
                                    null,
                                    true,
                                    a,
                                    new AstRange(EMPTY, number(0), number(64), true),
                                    List.of(call(id("print"), arg(a))))
                    )
            );
        }

        @Test
        void buildsRangedForLoopWithLabel() {
            assertBuildsTo("""
                            Label:
                            for a in b + 10 .. c - 20 do
                                a;
                            end;
                            """,
                    List.of(
                            new AstRangedForLoopStatement(EMPTY,
                                    id("Label"),
                                    false,
                                    a,
                                    new AstRange(EMPTY,
                                            new AstOperatorBinary(EMPTY, ADD, b, number(10)),
                                            new AstOperatorBinary(EMPTY, Operation.SUB, c, number(20)),
                                            false),
                                    List.of(a)
                            )
                    )
            );
        }

        @Test
        void buildsRangedForLoopWithoutBody() {
            assertBuildsTo("""
                            Label:
                            for var a in b ... c do
                            end;
                            """,
                    List.of(
                            new AstRangedForLoopStatement(EMPTY,
                                    id("Label"),
                                    true,
                                    a,
                                    new AstRange(EMPTY, b, c, true),
                                    List.of()
                            )
                    )
            );
        }
    }

    @Nested
    class Require {
        @Test
        void buildsLibraryRequire() {
            assertBuildsTo("require math;",
                    List.of(
                            new AstRequireLibrary(EMPTY, new AstIdentifier(EMPTY, "math"), null)
                    )
            );
        }

        @Test
        void buildsFileRequire() {
            // If this file exists, the test will fail
            final String fileName = "s6zoH0%IbSsQH4!MOmpu%eDO-H!#Z81dr2xSYGds6xhTzx^V#ie7UNikF$xtYUAi";

            assertBuildsTo(
                    expectedMessages().add(1, 9, "Error reading file '" + fileName + "'."),
                    "require \"" + fileName + "\";",
                    List.of(
                            new AstRequireFile(EMPTY, new AstLiteralString(EMPTY, fileName), null)
                    )
            );
        }

        @Test
        void buildsFileRequireRemote() {
            // If this file exists, the test will fail
            final String fileName = "s6zoH0%IbSsQH4!MOmpu%eDO-H!#Z81dr2xSYGds6xhTzx^V#ie7UNikF$xtYUAi";

            assertBuildsTo(
                    expectedMessages().add(1, 9, "Error reading file '" + fileName + "'."),
                    "require \"" + fileName + "\" remote processor1;",
                    List.of(
                            new AstRequireFile(EMPTY, new AstLiteralString(EMPTY, fileName),
                                    id("processor1"))
                    )
            );
        }
    }

    @Nested
    class TernaryOperations {
        @Test
        void buildsTernaryOperator() {
            assertBuildsTo("""
                            identifier ? left : right;
                            """,
                    List.of(
                            new AstOperatorTernary(EMPTY, identifier, left, right)
                    )
            );
        }

        @Test
        void buildsNestedTernaryOperator() {
            assertBuildsTo("""
                            a?b?c:d:e?f:g;
                            """,
                    List.of(
                            new AstOperatorTernary(EMPTY,
                                    new AstIdentifier(EMPTY, "a"),
                                    new AstOperatorTernary(EMPTY,
                                            new AstIdentifier(EMPTY, "b"),
                                            new AstIdentifier(EMPTY, "c"),
                                            new AstIdentifier(EMPTY, "d")
                                    ),
                                    new AstOperatorTernary(EMPTY,
                                            new AstIdentifier(EMPTY, "e"),
                                            new AstIdentifier(EMPTY, "f"),
                                            new AstIdentifier(EMPTY, "g")
                                    )
                            )
                    )
            );
        }
    }

    @Nested
    class UnaryOperations {
        @Test
        void buildsBitwiseNot() {
            assertBuildsTo("""
                            ~operand;
                            """,
                    List.of(
                            new AstOperatorUnary(EMPTY, Operation.BITWISE_NOT, operand)
                    )
            );
        }

        @Test
        void buildsBooleanNot() {
            assertBuildsTo("""
                            // Testing both variants at once
                            not!operand;
                            """,
                    List.of(
                            new AstOperatorUnary(EMPTY,
                                    Operation.LOGICAL_NOT,
                                    new AstOperatorUnary(EMPTY, Operation.BOOLEAN_NOT, operand))
                    )
            );
        }

        @Test
        void buildsUnaryMinus() {
            assertBuildsTo("""
                            -operand;
                            """,
                    List.of(
                            new AstOperatorUnary(EMPTY, Operation.SUB, operand)
                    )
            );
        }

        @Test
        void buildsUnaryPlus() {
            assertBuildsTo("""
                            +operand;
                            """,
                    List.of(
                            new AstOperatorUnary(EMPTY, ADD, operand)
                    )
            );
        }

        @Test
        void buildsIncrementDecrement() {
            assertBuildsTo("""
                            ++operand;
                            --operand;
                            operand++;
                            operand--;
                            """,
                    List.of(
                            new AstOperatorIncDec(EMPTY, Type.PREFIX, ADD, operand),
                            new AstOperatorIncDec(EMPTY, Type.PREFIX, SUB, operand),
                            new AstOperatorIncDec(EMPTY, Type.POSTFIX, ADD, operand),
                            new AstOperatorIncDec(EMPTY, Type.POSTFIX, SUB, operand)
                    )
            );
        }
    }

    @Nested
    class Statements {
        @Test
        void buildsBreakStatement() {
            assertBuildsTo("""
                            break;
                            break label;
                            """,
                    List.of(
                            new AstBreakStatement(EMPTY, null),
                            new AstBreakStatement(EMPTY, id("label"))
                    )
            );
        }

        @Test
        void buildsContinueStatement() {
            assertBuildsTo("""
                            continue;
                            continue label;
                            """,
                    List.of(
                            new AstContinueStatement(EMPTY, null),
                            new AstContinueStatement(EMPTY, id("label"))
                    )
            );
        }

        @Test
        void buildsReturnStatement() {
            assertBuildsTo("""
                            return;
                            return a;
                            return a();
                            """,
                    List.of(
                            new AstReturnStatement(EMPTY, null),
                            new AstReturnStatement(EMPTY, a),
                            new AstReturnStatement(EMPTY, call(a))
                    )
            );
        }
    }

    @Nested
    class WhileLoops {
        @Test
        void buildsWhileLoop() {
            assertBuildsTo("""
                            Label:
                            while true do
                                print(a);
                            end;
                            """,
                    List.of(
                            new AstWhileLoopStatement(EMPTY,
                                    id("Label"),
                                    new AstLiteralBoolean(EMPTY, "true"),
                                    List.of(call(id("print"), arg(a))),
                                    true
                            )
                    )
            );
        }

        @Test
        void buildsEmptyWhileLoop() {
            assertBuildsTo("""
                            while true do end;
                            """,
                    List.of(
                            new AstWhileLoopStatement(EMPTY,
                                    null,
                                    new AstLiteralBoolean(EMPTY, "true"),
                                    List.of(),
                                    true
                            )
                    )
            );
        }

        @Test
        void buildsDoWhileLoop() {
            assertBuildsTo("""
                            do
                                print(a);
                            while true;
                            """,
                    List.of(
                            new AstWhileLoopStatement(EMPTY,
                                    null,
                                    new AstLiteralBoolean(EMPTY, "true"),
                                    List.of(call(id("print"), arg(a))),
                                    false
                            )
                    )
            );
        }

        //@Test
        void reportsDoWhileLoopDeprecation() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add(3, 1, "The 'loop' keyword is deprecated. Use just 'while' instead."),
                    """
                            do
                                print(a);
                            loop while true;
                            """
            );
        }

        @Test
        void buildsEmptyDoWhileLoop() {
            assertBuildsTo("""
                            Label:
                            do while true;
                            """,
                    List.of(
                            new AstWhileLoopStatement(EMPTY,
                                    id("Label"),
                                    new AstLiteralBoolean(EMPTY, "true"),
                                    List.of(),
                                    false
                            )
                    )
            );
        }


        @Test
        void refusesAssigningLoopsToVariables() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex("Parse error: .*").atLeast(1),
                    """
                            a = do
                                print(a);
                            while true;
                            """
            );
        }

    }
}
