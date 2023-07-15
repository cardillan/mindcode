package info.teksol.mindcode.webapp;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.schemacode.SchemacodeCompiler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class WebAppSchematicsBuilderTest {

    @Test
    void refusesExternalFileAccess() {
        String definition = """
                schematic
                    dimensions = (1, 1)
                    @micro-processor at (0, 0) processor
                        mlog = file "some_file.mlog"
                    end
                end
                """;

        CompilerOutput<byte[]> output = SchemacodeCompiler.compile(definition, CompilerProfile.fullOptimizations(true), null);
        assertRegex("Loading code from external file not supported in web application.",
                output.messages());
    }

    private void assertRegex(String expectedRegex, List<CompilerMessage> messages) {
        List<String> list = messages.stream().filter(CompilerMessage::isError).map(CompilerMessage::message).toList();
        if (list.stream().anyMatch(s -> s.matches(expectedRegex))) {
            assertTrue(true);
        } else {
            fail("No message matched expected expression.\nExpected expression: %s, found messages: %s"
                    .formatted(expectedRegex, String.join("\n", list)));
        }
    }
}
