package info.teksol.schemacode.schematics;

import info.teksol.schemacode.schematics.ParameterReplacer.ReplacementException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessorConfigurationBuilderTest {

    @Nested
    class WithComments {
        public static final String mlog = """
                # This is a comment
                set foo 10      # This is another comment
                set bar "10"    # And yet another
                print foo
                op add foo foo bar
                """;

        @Test
        void replacesNonStringParameters() throws ReplacementException {
            Map<String, String> replacements = Map.of("foo", "20");
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();

            String expected = """
                    # This is a comment
                    set foo 20      # This is another comment
                    set bar "10"    # And yet another
                    print foo
                    op add foo foo bar
                    """;

            assertEquals(expected, replacer.getResult());
        }

        @Test
        void replacesStringParameters() throws ReplacementException {
            Map<String, String> replacements = Map.of("bar", "20");
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();

            String expected = """
                    # This is a comment
                    set foo 10      # This is another comment
                    set bar 20    # And yet another
                    print foo
                    op add foo foo bar
                    """;

            assertEquals(expected, replacer.getResult());
        }


        @Test
        void processesWithoutReplacing() throws ReplacementException {
            Map<String, String> replacements = Map.of("fowl", "artemis");
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();

            String expected = """
                    # This is a comment
                    set foo 10      # This is another comment
                    set bar "10"    # And yet another
                    print foo
                    op add foo foo bar
                    """;

            assertEquals(expected, replacer.getResult());
            assertEquals(Set.of("fowl"), replacer.getAbsentParameters());
        }
    }

    @Nested
    class WithoutComments {
        public static final String mlog = """
                # This is a comment
                set foo 10
                set bar "10"
                print foo
                op add foo foo bar
                """;

        @Test
        void replacesNonStringParameters() throws ReplacementException {
            Map<String, String> replacements = Map.of("foo", "20");
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();

            String expected = """
                    # This is a comment
                    set foo 20
                    set bar "10"
                    print foo
                    op add foo foo bar
                    """;

            assertEquals(expected, replacer.getResult());
            assertEquals(Set.of(), replacer.getAbsentParameters());
        }

        @Test
        void replacesStringParameters() throws ReplacementException {
            Map<String, String> replacements = Map.of("bar", "20");
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();

            String expected = """
                    # This is a comment
                    set foo 10
                    set bar 20
                    print foo
                    op add foo foo bar
                    """;

            assertEquals(expected, replacer.getResult());
            assertEquals(Set.of(), replacer.getAbsentParameters());
        }


        @Test
        void processesWithoutReplacing() throws ReplacementException {
            Map<String, String> replacements = Map.of("fowl", "artemis");
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();

            String expected = """
                    # This is a comment
                    set foo 10
                    set bar "10"
                    print foo
                    op add foo foo bar
                    """;

            assertEquals(expected, replacer.getResult());
            assertEquals(Set.of("fowl"), replacer.getAbsentParameters());
        }
    }


    @Nested
    class WithSemicolons {
        public static final String mlog = """
                # This is a comment
                set foo 10;set bar "10";print foo
                op add foo foo bar
                """;

        @Test
        void replacesNonStringParameters() throws ReplacementException {
            Map<String, String> replacements = Map.of("foo", "20");
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();

            String expected = """
                    # This is a comment
                    set foo 20;set bar "10";print foo
                    op add foo foo bar
                    """;

            assertEquals(expected, replacer.getResult());
            assertEquals(Set.of(), replacer.getAbsentParameters());
        }

        @Test
        void replacesStringParameters() throws ReplacementException {
            Map<String, String> replacements = Map.of("bar", "20");
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();

            String expected = """
                    # This is a comment
                    set foo 10;set bar 20;print foo
                    op add foo foo bar
                    """;

            assertEquals(expected, replacer.getResult());
            assertEquals(Set.of(), replacer.getAbsentParameters());
        }


        @Test
        void processesWithoutReplacing() throws ReplacementException {
            Map<String, String> replacements = Map.of("fowl", "artemis");
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();

            String expected = """
                    # This is a comment
                    set foo 10;set bar "10";print foo
                    op add foo foo bar
                    """;

            assertEquals(expected, replacer.getResult());
            assertEquals(Set.of("fowl"), replacer.getAbsentParameters());
        }
    }

    @Nested
    class ReplacesWithString {
        public static final String mlog = """
                # This is a comment
                set foo 10      # This is another comment
                set bar "10"    # And yet another
                print foo
                op add foo foo bar
                """;

        @Test
        void replacesNonStringParameters() throws ReplacementException {
            Map<String, String> replacements = Map.of("foo", "\"20\"");
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();

            String expected = """
                    # This is a comment
                    set foo "20"      # This is another comment
                    set bar "10"    # And yet another
                    print foo
                    op add foo foo bar
                    """;

            assertEquals(expected, replacer.getResult());
            assertEquals(Set.of(), replacer.getAbsentParameters());
        }

        @Test
        void replacesStringParameters() throws ReplacementException {
            Map<String, String> replacements = Map.of("bar", "\"20\"");
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();

            String expected = """
                    # This is a comment
                    set foo 10      # This is another comment
                    set bar "20"    # And yet another
                    print foo
                    op add foo foo bar
                    """;

            assertEquals(expected, replacer.getResult());
            assertEquals(Set.of(), replacer.getAbsentParameters());
        }


        @Test
        void replacesAllParameters() throws ReplacementException {
            Map<String, String> replacements = Map.of("foo", "\"20\"", "bar", "\"20\"");
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();

            String expected = """
                    # This is a comment
                    set foo "20"      # This is another comment
                    set bar "20"    # And yet another
                    print foo
                    op add foo foo bar
                    """;

            assertEquals(expected, replacer.getResult());
            assertEquals(Set.of(), replacer.getAbsentParameters());
        }
    }
}
