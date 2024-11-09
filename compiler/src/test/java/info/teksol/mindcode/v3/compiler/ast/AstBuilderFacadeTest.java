package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

class AstBuilderFacadeTest extends AbstractAstBuilderTest {

    @Nested
    class BasicStructure {
        @Test
        void correctlyProcessesComments1() {
            assertBuilds("""
                            /*  A commented out // single-line comment */
                            identifier;
                            """,
                    List.of(
                            new AstIdentifier(EMPTY, "identifier")
                    )
            );
        }

        @Test
        void correctlyProcessesComments2() {
            assertBuilds("""
                            //  This is not an opening comment /*
                            identifier;
                            """,
                    List.of(
                            new AstIdentifier(EMPTY, "identifier")
                    )
            );
        }

        @Test
        void correctlyProcessesComments3() {
            assertBuilds("""
                            /*
                            //  This is a closing */
                            identifier;
                            """,
                    List.of(
                            new AstIdentifier(EMPTY, "identifier")
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
                            """,
                    List.of(
                            new AstIdentifier(EMPTY, "identifier"),
                            new AstBuiltInIdentifier(EMPTY, "@built-in-identifier")
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
}