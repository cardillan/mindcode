package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

class AstBuilderFacadeTest extends AbstractAstBuilderTest {

    @Nested
    class BasicStructure {
    }

    @Nested
    class Directives {
        @Test
        void parsesDirectives() {
            assertParses("""
                    #set option;
                    #set option = value;
                    #set option = value,value;
                    """);
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
                            "string literal";
                            """,
                    List.of(
                            new AstIdentifier(EMPTY, "identifier"),
                            new AstLiteralString(EMPTY, "string literal")
                    ));
        }
    }

    @Nested
    class Formattables {
        @Test
        void parsesBasicFormattable() {
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

        void parsesFormattableWithVariablePlaceholder() {
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
        void parsesFormattableWithEmptyPlaceholder() {
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
        void parsesFormattableWithEmptyPlaceholder2() {
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
        void parsesFormattableWithInterpolation() {
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
        void parsesFormattableWithUnescapableCharacter() {
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
        void parsesFormattableWithEscapedInterpolation() {
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
        void parsesFormattableWithEscapedEscape() {
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
        void parsesFormattableWithMultilineInterpolation() {
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
        void parsesBasicEnhancedComment() {
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
        void parsesEnhancedCommentWithVariablePlaceholder() {
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
        void parsesEnhancedCommentWithEmptyPlaceholder() {
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
        void parsesEnhancedCommentWithEmptyPlaceholder2() {
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
        void parsesEnhancedCommentWithInterpolation() {
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
        void parsesEnhancedCommentWithUnescapableCharacter() {
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
        void parsesEnhancedCommentWithEscapedInterpolation() {
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
        void parsesEnhancedCommentWithEscapedEscape() {
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
        void parsesEnhancedCommentWithInterpolationAndComment() {
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