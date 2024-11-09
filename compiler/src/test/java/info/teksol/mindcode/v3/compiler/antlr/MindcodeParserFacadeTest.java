package info.teksol.mindcode.v3.compiler.antlr;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MindcodeParserFacadeTest extends AbstractParserTest {

    @Nested
    class BasicStructure {
        @Test
        void refusesMissingSemicolon() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add(3, 1, "Parse error: missing SEMICOLON at 'id'")
                            .add(5, 1, "Parse error: missing SEMICOLON at '<EOF>'"),
                    """
                            id
                            // This is a comment
                            id
                            """);
        }
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
        void parsesBasicElements() {
            assertParses("""
                    identifier;
                    @built-in-identifier;
                    """);
        }
    }


    @Nested
    class Literals {
        @Test
        void parsesStringLiterals() {
            assertParses("""
                    "";
                    "A string literal";
                    """);
        }

        @Test
        void parsesFloatLiterals() {
            assertParses("""
                    1.0;
                    0.0;
                    .5;
                    1e5;
                    1e+5;
                    1e-5;
                    1E10;
                    1E+10;
                    1E-10;
                    2.5E10;
                    2.5E+10;
                    2.5E-10;
                    .5E10;
                    .5E+10;
                    .5E-10;
                    """);
        }

        @Test
        void parsesNumericLiterals() {
            assertParses("""
                    0b0011;
                    0x0123456789ABCDEF;
                    0xfedcba9876543210;
                    0;
                    01;
                    123;
                    """);
        }

        @Test
        void parsesOtherLiterals() {
            assertParses("""
                    null;
                    true;
                    false;
                    """);
        }
    }

    @Nested
    class Comments {
        @Test
        void parsesComments() {
            assertParses("""
                    /*
                       Multiline comment
                    */
                    // Comment
                    /// Enhanced comment
                    //// Commented comment
                    identifier;
                    """);
        }

        @Test
        void refusesNestedComments() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add("Parse error: token recognition error at: '*'")
                            .add("Parse error: token recognition error at: '/\\n'")
                    ,
                    """
                    /*  /* Nested comment */ */
                    identifier;
                    """);
        }
    }

    @Nested
    class Formattable {
        @Test
        void parsesFormattableWithValue() {
            assertParses("""
                    $"foo$bar";"baz";
                    """);
        }

        @Test
        void parsesFormattableWithEscape() {
            assertParses("""
                    $"foo\\$bar";
                    """);
        }

        @Test
        void parsesMultilineFormattable() {
            assertParses("""
                    $"Formattable with ${
                        multiline
                    } interpolation";
                    """);
        }

        @Test
        void parsesBasicFormattables() {
            assertParses("""
                    $"Formattable";
                    $"Formattable with $value";
                    $"Formattable with ${interpolation}";
                    $"Formattable with ${ interpolation }";
                    $"Formattable with $ a placeholder";
                    $"Formattable with ${
                        multiline
                    } interpolation";
                    """);
        }

        @Test
        void refusesNestedFormattables() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add("Parse error: token recognition error at: '$'"),
                    """
                    $"Formattable with ${
                        $"a nested"
                    } interpolation";
                    """);
        }
    }

    @Nested
    class EnhancedComments {
        @Test
        void parsesEnhancedCommentWithInterpolation() {
            assertParses("""
                    /// This is a comment with ${ interpolation }
                    """);
        }

        @Test
        void refusesEnhancedCommentWithMultilineInterpolation() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add(1, 40, "Parse error: token recognition error at: '\\n'"),
                    """
                            /// This is a comment with multiline ${
                                interpolation}
                            """);
        }

        @Test
        void refusesNestedEnhancedComments() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add(1, 15, "Parse error: token recognition error at: '/// '"),
                    """
                            /// Nested ${ /// interpolation }
                            """);
        }

        @Test
        void refusesEnhancedCommentWithDoubleQuotes() {
            assertGeneratesMessages(
                    expectedMessages().add("Parse error: token recognition error at: '\"'").repeat(2),
                    """
                            /// "Quotes"
                            """);
        }

        @Test
        void refusesEnhancedCommentWithDoubleQuotesAfterPlaceholder() {
            assertGeneratesMessages(
                    expectedMessages().add("Parse error: token recognition error at: '\"'").repeat(2),
                    """
                            /// $foo" foo$" foo
                            """);
        }
    }

}