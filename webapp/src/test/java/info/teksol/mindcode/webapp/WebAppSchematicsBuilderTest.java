package info.teksol.mindcode.webapp;

import info.teksol.mc.common.CompilerOutput;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.profile.CompilerProfile;
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

        CompilerOutput<byte[]> output = SchemacodeCompiler.compile(
                InputFiles.fromSource(definition),
                CompilerProfile.fullOptimizations(true));
        assertRegex("Loading code from external file not supported in web application.",
                output.messages());
    }

    private void assertRegex(String expectedRegex, List<MindcodeMessage> messages) {
        List<String> list = messages.stream().filter(MindcodeMessage::isError).map(MindcodeMessage::message).toList();
        if (list.stream().anyMatch(s -> s.matches(expectedRegex))) {
            assertTrue(true);
        } else {
            fail("No message matched expected expression.\nExpected expression: %s, found messages: %s"
                    .formatted(expectedRegex, String.join("\n", list)));
        }
    }
}
