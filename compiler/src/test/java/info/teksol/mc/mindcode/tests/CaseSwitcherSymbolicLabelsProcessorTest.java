package info.teksol.mc.mindcode.tests;

import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;

import java.io.IOException;
import java.util.stream.IntStream;

@NullMarked
@Order(5)
public class CaseSwitcherSymbolicLabelsProcessorTest extends CaseSwitcherProcessorTestBase {

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setUseTextTranslations(false);
    }

    @Override
    protected IntStream codeSizes(String fileName) {
        int i = fileName.indexOf("-");
        return switch(fileName.substring(0, i)) {
            case "distinct" -> IntStream.of(0, 110, 200, 500);
            case "mixed" -> IntStream.of(0, 140, 230, 500);
            case "homogenous" -> IntStream.of(0, 60, 100, 500);
            default -> throw new IllegalArgumentException("Unknown test name: " + fileName);
        };
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, CaseSwitcherSymbolicLabelsProcessorTest.class.getSimpleName());
    }
}
