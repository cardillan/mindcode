package info.teksol.mindcode.v3.compiler.antlr;

import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MindcodeParserFacadeTest extends AbstractParserTest {

    @Nested
    class BasicStructure {
        @Test
        void refusesMissingSemicolon() {
            assertParses(
                    ExpectedMessages.create()
                            .add(3, 1, "Parse error: missing Semicolon at 'id'")
                            .add(5, 1, "Parse error: missing Semicolon at '<EOF>'"),
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
            assertParses(
                    ExpectedMessages.create()
                            .add("Parse error: token recognition error at: '$'"),
                    """
                    $"Formattable with ${
                        $"text ${ nested } text"
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
            assertParses(
                    ExpectedMessages.create()
                            .add(1, 40, "Parse error: token recognition error at: '\\n'"),
                    """
                            /// This is a comment with multiline ${
                                interpolation}
                            """);
        }

        @Test
        void refusesNestedEnhancedComments() {
            assertParses(
                    ExpectedMessages.create()
                            .add(1, 15, "Parse error: token recognition error at: '/// '"),
                    """
                            /// Nested ${ /// interpolation }
                            """);
        }

        @Test
        void refusesEnhancedCommentWithDoubleQuotes() {
            assertParses(
                    ExpectedMessages.create().add("Parse error: token recognition error at: '\"'").repeat(2),
                    """
                            /// "Quotes"
                            """);
        }
    }

}