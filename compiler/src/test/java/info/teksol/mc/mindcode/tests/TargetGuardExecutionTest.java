package info.teksol.mc.mindcode.tests;

import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.BuiltinEvaluation;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.EmulatorOptions;
import info.teksol.mc.profile.options.Target;
import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@NullMarked
public class TargetGuardExecutionTest extends AbstractProcessorTest {

    // Some Mindcode code tested in subclasses of this class can be quite complex and hard to maintain
    // as a string constant. Such code can be saved as a file in subdirectories of this directory.
    public static final String SCRIPTS_DIRECTORY = SCRIPTS_BASE_DIRECTORY + "/compatibility";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        CompilerProfile profile = super.createCompilerProfile();
        profile.setTargetGuard(true).getOption(EmulatorOptions.RUN_STEPS).setValue(10);
        return profile;
    }

    private ProcessorVersion minimalCompatibleVersion(ProcessorVersion version) {
        //return version == V8C ? V8B : version;
        return version;
    }

    void executeCompatibilityTest(ProcessorVersion compilerVersion, ProcessorVersion emulatorVersion, boolean compatible) {
        Target compilerTarget = new Target(compilerVersion, ProcessorType.MICRO_PROCESSOR);
        Target emulatorTarget = new Target(emulatorVersion, ProcessorType.MICRO_PROCESSOR);
        String code = """
                #set target = %s;
                #set emulator-target = %s;
                #set builtin-evaluation = %s;
                print("matched");
                """.formatted(compilerTarget.targetName(), emulatorTarget.targetName(),
                EnumUtils.toKebabCase(compatible ? BuiltinEvaluation.COMPATIBLE : BuiltinEvaluation.FULL));

        boolean matches = compilerVersion == emulatorVersion || (compatible && emulatorVersion.atLeast(compilerVersion));

        process(matches ? expectedMessages() : expectedMessages().addRegex(".*Execution step limit of 10 exceeded\\."),
                createInputFiles(code),
                emulator -> setupLogicBlock(emulator, Map.of()),
                compiler -> {
                    assertFalse(compiler.hasCompilerErrors(), "Errors while compiling program.");
                    List<Executable> executables = new ArrayList<>();
                    if (matches) {
                        assertEquals("matched", compiler.getEmulator().getExecutorResults(0).getFormattedOutput());
                    }
                    return null;
                });
    }

    //@Test
    void testSpecificCombination() {
        executeCompatibilityTest(V8A, V8B, false);
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode testCompatibility() {
        List<ProcessorVersion> versions = Stream.of(ProcessorVersion.values())
                .filter(v -> v != V7 && v != MAX)
                .toList();

        return DynamicContainer.dynamicContainer(
                "Compatibility library tests",
                Stream.of(true, false).map(compatible ->
                        DynamicContainer.dynamicContainer(
                                compatible ? "Compatible built-in evaluation" : "Full built-in evaluation",
                                versions.stream().map(compiler ->
                                        DynamicContainer.dynamicContainer(
                                                "Compiler target: " + compiler,
                                                versions.stream()
                                                        // .filter(emulator -> emulator.atLeast(compiler))
                                                        .map(emulator ->
                                                                DynamicTest.dynamicTest(
                                                                        "Emulator target: " + emulator,
                                                                        () -> executeCompatibilityTest(compiler, emulator, compatible)))
                                        )
                                )
                        )
                )
        );
    }
}
