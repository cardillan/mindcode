package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.logic.Operation;
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

    private static AstFunctionArgumentList args(AstFunctionArgument... args) {
        return new AstFunctionArgumentList(EMPTY, List.of(args));
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
                            new AstExpressionList(EMPTY, List.of(identifier))
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
                            new AstExpressionList(EMPTY, List.of())
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
