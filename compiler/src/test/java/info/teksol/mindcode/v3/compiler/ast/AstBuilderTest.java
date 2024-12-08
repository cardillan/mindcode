package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.logic.Operation;
import info.teksol.mindcode.v3.DataType;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

class AstBuilderTest extends AbstractAstBuilderTest {
    private static final AstIdentifier identifier = id("identifier");
    private static final AstIdentifier object = id("object");
    private static final AstIdentifier operand = id("operand");
    private static final AstIdentifier left = id("left");
    private static final AstIdentifier right = id("right");
    private static final AstIdentifier a = id("a");
    private static final AstIdentifier b = id("b");
    private static final AstIdentifier c = id("c");
    private static final AstIdentifier d = id("d");
    private static final AstIdentifier e = id("e");

    private static final AstFunctionArgument NONE = new AstFunctionArgument(EMPTY);

    private static AstIdentifier id(String name) {
        return new AstIdentifier(EMPTY, name);
    }

    private static AstQualifiedIdentifier id(String... names) {
        return new AstQualifiedIdentifier(EMPTY, Stream.of(names).map(AstBuilderTest::id).toList());
    }

    private static AstLiteralDecimal number(int number) {
        return new AstLiteralDecimal(EMPTY, String.valueOf(number));
    }

    private static AstLiteralFloat number(double number) {
        return new AstLiteralFloat(EMPTY, String.valueOf(number));
    }

    private static AstFunctionArgument arg(AstMindcodeNode expression) {
        return new AstFunctionArgument(EMPTY, expression, false, false);
    }

    private static AstFunctionArgument arg(String name) {
        return new AstFunctionArgument(EMPTY, id(name), false, false);
    }

    private static AstFunctionArgument argIn(String name) {
        return new AstFunctionArgument(EMPTY, id(name), true, false);
    }

    private static AstFunctionArgument argOut(String name) {
        return new AstFunctionArgument(EMPTY, id(name), false, true);
    }

    private static AstFunctionArgument argInOut(String name) {
        return new AstFunctionArgument(EMPTY, id(name), true, true);
    }

    private static List<AstFunctionArgument> args(AstFunctionArgument... args) {
        return List.of(args);
    }

    private static AstFunctionCall call(AstIdentifier name, AstFunctionArgument... args) {
        return new AstFunctionCall(EMPTY, null, name, args(args));
    }

    private static AstFunctionCall call(AstMindcodeNode object, AstIdentifier name, AstFunctionArgument... args) {
        return new AstFunctionCall(EMPTY, object, name, args(args));
    }

    @Nested
    class Arrays {
        @Test
        void buildsArrayAccess() {
            assertBuilds("""
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
            assertBuilds("""
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
    }

    @Nested
    class Assignments {
        @Test
        void buildsSimpleAssignments() {
            assertBuilds("""
                            a = b;
                            a = b = c;
                            a[b] = c;
                            a[b] = a[c] = d;
                            @a = @b;
                            """,
                    List.of(
                            new AstAssignmentSimple(EMPTY, a, b),
                            new AstAssignmentSimple(EMPTY, a, new AstAssignmentSimple(EMPTY, b, c)),
                            new AstAssignmentSimple(EMPTY, new AstArrayAccess(EMPTY, a, b), c),
                            new AstAssignmentSimple(EMPTY,
                                    new AstArrayAccess(EMPTY, a, b),
                                    new AstAssignmentSimple(EMPTY,
                                            new AstArrayAccess(EMPTY, a, c),
                                            d)
                            ),
                            new AstAssignmentSimple(EMPTY,
                                    new AstBuiltInIdentifier(EMPTY, "@a"),
                                    new AstBuiltInIdentifier(EMPTY, "@b"))
                    )
            );
        }

        @Test
        void buildsCompoundAssignments() {
            assertBuilds("""
                            a **= b;
                            a *= b /= c \\= d %= e;
                            a += b -= c <<= d >>= e;
                            a &= b |= c ^= d;
                            a &&= b ||= c;
                            """,
                    List.of(
                            new AstAssignmentCompound(EMPTY, Operation.POW, a, b),
                            new AstAssignmentCompound(EMPTY, Operation.MUL, a,
                                    new AstAssignmentCompound(EMPTY, Operation.DIV, b,
                                            new AstAssignmentCompound(EMPTY, Operation.IDIV, c,
                                                    new AstAssignmentCompound(EMPTY, Operation.MOD, d,
                                                            e)))),
                            new AstAssignmentCompound(EMPTY, Operation.ADD, a,
                                    new AstAssignmentCompound(EMPTY, Operation.SUB, b,
                                            new AstAssignmentCompound(EMPTY, Operation.SHL, c,
                                                    new AstAssignmentCompound(EMPTY, Operation.SHR, d,
                                                            e)))),
                            new AstAssignmentCompound(EMPTY, Operation.BITWISE_AND, a,
                                    new AstAssignmentCompound(EMPTY, Operation.BITWISE_OR, b,
                                            new AstAssignmentCompound(EMPTY, Operation.BITWISE_XOR, c,
                                                    d))),
                            new AstAssignmentCompound(EMPTY, Operation.BOOLEAN_AND, a,
                                    new AstAssignmentCompound(EMPTY, Operation.BOOLEAN_OR, b,
                                            c))
                    )
            );
        }

        @Test
        void buildsCompoundAssignmentsWithArrays() {
            assertBuilds("""
                            a[b] += c[d];
                            """,
                    List.of(
                            new AstAssignmentCompound(EMPTY, Operation.ADD,
                                    new AstArrayAccess(EMPTY, a, b),
                                    new AstArrayAccess(EMPTY, c, d)
                            )
                    )
            );
        }

        @Test
        void buildsCompoundAssignmentsWithExpressions() {
            assertBuilds("""
                            a += b ? c : d;
                            """,
                    List.of(
                            new AstAssignmentCompound(EMPTY, Operation.ADD,
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
        void buildsCodeBlocks() {
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
                            /*  A commented out // single-line comment */
                            identifier;
                            """,
                    List.of(identifier)
            );
        }

        @Test
        void buildsComments2() {
            assertBuilds("""
                            //  This is not an opening comment /*
                            identifier;
                            """,
                    List.of(identifier)
            );
        }

        @Test
        void buildsComments3() {
            assertBuilds("""
                            /*
                            //  This is a closing */
                            identifier;
                            """,
                    List.of(identifier)
            );
        }

        @Test
        void buildsDocComments() {
            assertBuilds("""
                            /** This is a doc comment */
                            identifier;
                            /** This is another doc comment */
                            """,
                    List.of(identifier)
            );
        }
    }

    @Nested
    class BinaryOperations {

        @Test
        void buildsAdditionAndBitShifts() {
            assertBuilds("""
                            left+right;
                            left-right;
                            left<<right;
                            left>>right;
                            """,
                    List.of(
                            new AstOperatorBinary(EMPTY, Operation.ADD, left, right),
                            new AstOperatorBinary(EMPTY, Operation.SUB, left, right),
                            new AstOperatorBinary(EMPTY, Operation.SHL, left, right),
                            new AstOperatorBinary(EMPTY, Operation.SHR, left, right)
                    )
            );
        }

        @Test
        void buildsBitwiseOperations() {
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
                            left**right;
                            """,
                    List.of(
                            new AstOperatorBinary(EMPTY, Operation.POW, left, right)
                    )
            );
        }

        @Test
        void buildsInequalities() {
            assertBuilds("""
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
            assertBuilds("""
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
    }

    @Nested
    class CaseExpressions {
        @Test
        void buildsCaseExpressionWithoutElse() {
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
    }

    @Nested
    class Declarations {
        @Test
        void buildsAllocationsSeparate() {
            assertBuilds("""
                            allocate heap in cell1;
                            allocate stack in cell2[0 .. 63];
                            """,
                    List.of(
                            new AstAllocation(EMPTY,
                                    AstAllocation.AllocationType.HEAP,
                                    id("cell1"),
                                    null),
                            new AstAllocation(EMPTY,
                                    AstAllocation.AllocationType.STACK,
                                    id("cell2"),
                                    new AstRange(EMPTY, number(0), number(63), false))
                    )
            );
        }

        @Test
        void buildsAllocationsCombined() {
            assertBuilds("""
                            allocate heap in HEAPPTR[0 ... 64], stack in bank1;
                            """,
                    List.of(
                            new AstStatementList(EMPTY,
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
        void buildsConstants() {
            assertBuilds("""
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
        void buildsParameters() {
            assertBuilds("""
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
    }

    @Nested
    class Directives {
        @Test
        void buildsValuelessSetDirective() {
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds(
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
                            new AstQualifiedIdentifier(EMPTY, a, b),
                            new AstQualifiedIdentifier(EMPTY, a, b, c)
                    )
            );
        }

        @Test
        void buildsLiterals() {
            assertBuilds(
                    """
                            0b0011;                 // Binary
                            0xabcdef;               // Hexadecimal
                            13579;                  // Decimal
                            1.4e15;                 // Float
                            "";                     // Empty string
                            "text";                 // String
                            null;
                            true;
                            false;
                            """,
                    List.of(
                            new AstLiteralBinary(EMPTY, "0b0011"),
                            new AstLiteralHexadecimal(EMPTY, "0xabcdef"),
                            new AstLiteralDecimal(EMPTY, "13579"),
                            new AstLiteralFloat(EMPTY, "1.4e15"),
                            new AstLiteralString(EMPTY, ""),
                            new AstLiteralString(EMPTY, "text"),
                            new AstLiteralNull(EMPTY, "null"),
                            new AstLiteralBoolean(EMPTY, "true"),
                            new AstLiteralBoolean(EMPTY, "false")
                    )
            );
        }

        @Test
        void buildsMemberAccess() {
            assertBuilds(
                    """
                            identifier.@type;
                            identifier.@firstItem.@id;
                            a.b.c.@id;
                            """,
                    List.of(
                            new AstMemberAccess(EMPTY,
                                    identifier,
                                    new AstBuiltInIdentifier(EMPTY, "@type")),
                            new AstMemberAccess(EMPTY,
                                    new AstMemberAccess(EMPTY,
                                            identifier,
                                            new AstBuiltInIdentifier(EMPTY, "@firstItem")),
                                    new AstBuiltInIdentifier(EMPTY, "@id")),
                            new AstMemberAccess(EMPTY,
                                    new AstQualifiedIdentifier(EMPTY, a, b, c),
                                    new AstBuiltInIdentifier(EMPTY, "@id"))
                    )
            );
        }
    }

    @Nested
    class EnhancedComments {
        @Test
        void buildsBasicEnhancedComment() {
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
    class Formattables {
        @Test
        void buildsBasicFormattable() {
            assertBuilds("""
                            $"foo";
                            """,
                    List.of(
                            new AstFormattable(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo")
                                    )
                            )
                    )
            );
        }

        void buildsFormattableWithVariablePlaceholder() {
            assertBuilds("""
                            $"$foo";
                            """,
                    List.of(
                            new AstFormattable(EMPTY,
                                    List.of(
                                            new AstIdentifier(EMPTY, "foo")
                                    )
                            )
                    )
            );
        }

        @Test
        void buildsFormattableWithEmptyPlaceholder() {
            assertBuilds("""
                            $"foo$";
                            """,
                    List.of(
                            new AstFormattable(EMPTY,
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
            assertBuilds("""
                            $"foo${ }bar";
                            """,
                    List.of(
                            new AstFormattable(EMPTY,
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
            assertBuilds("""
                            $"foo ${ expression } bar";
                            """,
                    List.of(
                            new AstFormattable(EMPTY,
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
            assertBuilds("""
                            $"foo\\bar";
                            """,
                    List.of(
                            new AstFormattable(EMPTY,
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
            assertBuilds("""
                            $"foo \\${ expression } bar";
                            """,
                    List.of(
                            new AstFormattable(EMPTY,
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
            assertBuilds("""
                            $"foo \\\\${ expression } bar";
                            """,
                    List.of(
                            new AstFormattable(EMPTY,
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
            assertBuilds("""
                            $"foo ${
                                // A comment
                                expression
                                /* Another comment */
                            } bar";
                            """,
                    List.of(
                            new AstFormattable(EMPTY,
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
            assertBuilds("""
                            $"foo${left+right}bar";
                            """,
                    List.of(
                            new AstFormattable(EMPTY,
                                    List.of(
                                            new AstLiteralString(EMPTY, "foo"),
                                            new AstOperatorBinary(EMPTY, Operation.ADD, left, right),
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
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
                            a(b(), c());
                            a().b();
                            a.b.c();
                            a().b().@id;
                            d = a(b, c) + b(c, d);
                            """,
                    List.of(
                            call(a, arg(call(b)), arg(call(c))),
                            call(call(a), b),
                            call(id("a", "b"), c),
                            new AstMemberAccess(EMPTY,
                                    call(call(a), b),
                                    new AstBuiltInIdentifier(EMPTY, "@id")),
                            new AstAssignmentSimple(EMPTY,
                                    d,
                                    new AstOperatorBinary(EMPTY, Operation.ADD,
                                            call(a, arg(b), arg(c)),
                                            call(b, arg(c), arg(d))
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
            assertBuilds("""
                            /** Comment1 */
                            def a() end;
                            /** Comment2 */
                            /** Comment3 */
                            inline def b(a...) a; end;
                            noinline void c(in a, out b, in out c, out in d) a + b; end;
                            """,
                    List.of(
                            new AstFunctionDeclaration(EMPTY,
                                    new AstDocComment(EMPTY, "/** Comment1 */"),
                                    a,
                                    DataType.VAR,
                                    List.of(),
                                    List.of(),
                                    false,
                                    false),
                            new AstFunctionDeclaration(EMPTY,
                                    new AstDocComment(EMPTY, "/** Comment3 */"),
                                    b,
                                    DataType.VAR,
                                    List.of(new AstFunctionParameter(EMPTY, a, false, false, true)),
                                    List.of(a),
                                    true,
                                    false),
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
                                    List.of(new AstOperatorBinary(EMPTY, Operation.ADD, a, b)),
                                    false,
                                    true)
                    )
            );
        }
    }


    @Nested
    class IfExpressions {
        @Test
        void buildsIfExpressionWithoutElse() {
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
            assertBuilds("""
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
    class RangedForLoop {
        @Test
        void buildsRangedForLoop() {
            assertBuilds("""
                            for a in 0 ... 64 do
                                print(a);
                            end;
                            """,
                    List.of(
                            new AstRangedForLoopStatement(EMPTY,
                                    null,
                                    a,
                                    new AstRange(EMPTY, number(0), number(64), true),
                                    List.of(call(id("print"), arg(a))))
                    )
            );
        }

        @Test
        void buildsRangedForLoopWithLabel() {
            assertBuilds("""
                            Label:
                            for a in b + 10 .. c - 20 do
                                a;
                            end;
                            """,
                    List.of(
                            new AstRangedForLoopStatement(EMPTY,
                                    id("Label"),
                                    a,
                                    new AstRange(EMPTY,
                                            new AstOperatorBinary(EMPTY, Operation.ADD, b, number(10)),
                                            new AstOperatorBinary(EMPTY, Operation.SUB, c, number(20)),
                                            false),
                                    List.of(a)
                            )
                    )
            );
        }
    }

    @Nested
    class Require {
        @Test
        void buildsLibraryRequire() {
            assertBuilds("""
                            require blocks;
                            """,
                    List.of(
                            new AstRequireLibrary(EMPTY, new AstIdentifier(EMPTY, "blocks"))
                    )
            );
        }

        @Test
        void buildsFileRequire() {
            assertBuilds("""
                            require "file";
                            """,
                    List.of(
                            new AstRequireFile(EMPTY, new AstLiteralString(EMPTY, "file"))
                    )
            );
        }
    }

    @Nested
    class TernaryOperations {
        @Test
        void buildsTernaryOperator() {
            assertBuilds("""
                            identifier ? left : right;
                            """,
                    List.of(
                            new AstOperatorTernary(EMPTY, identifier, left, right)
                    )
            );
        }

        @Test
        void buildsNestedTernaryOperator() {
            assertBuilds("""
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
            assertBuilds("""
                            ~operand;
                            """,
                    List.of(
                            new AstOperatorUnary(EMPTY, Operation.BITWISE_NOT, operand)
                    )
            );
        }

        @Test
        void buildsBooleanNot() {
            assertBuilds("""
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
            assertBuilds("""
                            -operand;
                            """,
                    List.of(
                            new AstOperatorUnary(EMPTY, Operation.SUB, operand)
                    )
            );
        }

        @Test
        void buildsUnaryPlus() {
            assertBuilds("""
                            +operand;
                            """,
                    List.of(
                            new AstOperatorUnary(EMPTY, Operation.ADD, operand)
                    )
            );
        }

        @Test
        void buildsIncrementDecrement() {
            assertBuilds("""
                            ++operand;
                            --operand;
                            operand++;
                            operand--;
                            """,
                    List.of(
                            new AstOperatorIncDec(EMPTY, AstOperatorIncDec.Type.PREFIX, AstOperatorIncDec.Operation.INCREMENT, operand),
                            new AstOperatorIncDec(EMPTY, AstOperatorIncDec.Type.PREFIX, AstOperatorIncDec.Operation.DECREMENT, operand),
                            new AstOperatorIncDec(EMPTY, AstOperatorIncDec.Type.POSTFIX, AstOperatorIncDec.Operation.INCREMENT, operand),
                            new AstOperatorIncDec(EMPTY, AstOperatorIncDec.Type.POSTFIX, AstOperatorIncDec.Operation.DECREMENT, operand)
                    )
            );
        }
    }
}
