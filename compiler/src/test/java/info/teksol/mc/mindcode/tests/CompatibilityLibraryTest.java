package info.teksol.mc.mindcode.tests;

import info.teksol.mc.emulator.ExecutionFlag;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.Target;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion.*;

@NullMarked
public class CompatibilityLibraryTest extends AbstractProcessorTest {

    // Some Mindcode code tested in subclasses of this class can be quite complex and hard to maintain
    // as a string constant. Such code can be saved as a file in subdirectories of this directory.
    public static final String SCRIPTS_DIRECTORY = SCRIPTS_BASE_DIRECTORY + "/compatibility";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        CompilerProfile profile = super.createCompilerProfile();
        profile.getOption(ExecutionFlag.ERR_EXECUTION_LIMIT_EXCEEDED).setValue(false);
        return profile;
    }

    String expectedOutput(ProcessorVersion compiler, ProcessorVersion emulator) {
        if (compiler == emulator) {
            return "[green]No compatibility issues encountered. Mindcode is fully compatible with this Mindustry version.";
        } else if (emulator.atLeast(compiler)) {
            return """
                    [salmon]Mindcode metadata of unstable built-ins are not compatible with this Mindustry version.[]
                    Please report the problem, and use [gold]#set builtin-evaluation = compatible;[]
                    to avoid incompatibility issues.""";
        } else if (compiler.atLeast(V8B)) {
            return """
                    [salmon]Cannot evaluate processor compatibility - the [gold]select[] instruction is missing.[]
                    Use the correct target for this Mindustry version when compiling your code.""";
        } else {
            return """
                    [salmon]Mindcode metadata of stable built-ins are not compatible with this Mindustry version.[]
                    Please report the problem, and use [gold]#set builtin-evaluation = none;[]
                    to avoid incompatibility issues.""";
        }
    }

    void executeCompatibilityTest(ProcessorVersion compiler, ProcessorVersion emulator) {
        Target compilerTarget = new Target(compiler, ProcessorType.MICRO_PROCESSOR);
        Target emulatorTarget = new Target(emulator, ProcessorType.MICRO_PROCESSOR);
        String code = """
                #set target = %s;
                #set emulator-target = %s;
                #set run-steps = 1000;
                #set err-unsupported-opcode = false;
                #set err-unsupported-block-operation = false;
                #set err-parse-error = false;
                //#set trace-execution = true;
                require compatibility;
                runCompatibilityTest();
                """.formatted(compilerTarget.targetName(), emulatorTarget.targetName());

        testAndEvaluateCode(
                testName(compiler, emulator),
                expectedMessages(),
                code,
                Map.of(),
                containsOutputEvaluator(expectedOutput(compiler, emulator)),
                null);
    }

    //@Test
    void testSpecificCombination() {
        executeCompatibilityTest(V8B, V8A);
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode testCompatibility() {
        List<ProcessorVersion> versions = Stream.of(ProcessorVersion.values())
                .filter(v -> v != V7A && v != MAX)
                .toList();

        return DynamicContainer.dynamicContainer("Compatibility library tests",
                versions.stream().flatMap(
                        compiler -> versions.stream()
                                //.filter(emulator -> emulator.atLeast(compiler))
                                .map(emulator -> DynamicTest.dynamicTest(testName(compiler, emulator),
                                        () -> executeCompatibilityTest(compiler, emulator)))));
    }

    private String testName(ProcessorVersion compiler, ProcessorVersion emulator) {
        Target compilerTarget = new Target(compiler, ProcessorType.MICRO_PROCESSOR);
        Target emulatorTarget = new Target(emulator, ProcessorType.MICRO_PROCESSOR);
        return "compiler target: " + compilerTarget.targetName() + ", emulator target: " + emulatorTarget.targetName();
    }
}
