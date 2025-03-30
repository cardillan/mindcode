package info.teksol.mc.mindcode.compiler.preprocess;

import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstDirectiveSet;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstDirectiveValue;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstModule;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstProgram;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mc.profile.Remarks;
import info.teksol.mc.profile.SortCategory;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static info.teksol.mc.common.SourcePosition.EMPTY;
import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class DirectivePreprocessorTest {

    private AstProgram directive(String option, String... values) {
        return new AstProgram(EMPTY,
                List.of(
                        new AstModule(EMPTY,
                                null,
                                List.of(
                                        new AstDirectiveSet(EMPTY,
                                                new AstDirectiveValue(EMPTY, option),
                                                Stream.of(values).map(this::directiveValue).toList())
                                ),
                                null
                        )
                )
        );
    }

    private AstDirectiveValue directiveValue(String value) {
        return new AstDirectiveValue(EMPTY, value);
    }

    private void processDirective(MessageConsumer messageConsumer, CompilerProfile profile, String option, String... values) {
        DirectivePreprocessor.processDirectives(new PreprocessorContextImpl(messageConsumer, profile),
                directive(option, values));
    }


    private void processDirective(CompilerProfile profile, String option, String... values) {
        processDirective(ExpectedMessages.throwOnMessage(), profile, option, values);
    }

    @Test
    void processesDirectiveTarget() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        processDirective(profile, "target", "6");
        assertEquals(ProcessorVersion.V6, profile.getProcessorVersion());
    }

    @Test
    void processesDirectiveOptimization() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        processDirective(profile, "optimization", "basic");
        assertTrue(profile.getOptimizationLevels().values().stream().allMatch(l -> l == OptimizationLevel.BASIC));
    }

    @Test
    void processesDirectiveBooleanEval() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setShortCircuitEval(false);
        processDirective(profile, "boolean-eval", "short");
        assertTrue(profile.isShortCircuitEval());
    }

    @Test
    void processesDirectiveInstructionLimit() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setInstructionLimit(1);
        processDirective(profile, "instruction-limit", "900");
        assertEquals(900, profile.getInstructionLimit());
    }

    @Test
    void processesDirectivePasses() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setOptimizationPasses(1);
        processDirective(profile, "passes", "10");
        assertEquals(10, profile.getOptimizationPasses());
    }

    @Test
    void processesDirectiveFunctionPrefix() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setShortFunctionPrefix(true);
        processDirective(profile, "function-prefix", "long");
        assertFalse(profile.isShortFunctionPrefix());
    }

    @Test
    void processesDirectiveGoal() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setGoal(GenerationGoal.SIZE);
        processDirective(profile, "goal", "speed");
        assertEquals(GenerationGoal.SPEED, profile.getGoal());
    }

    @Test
    void processesDirectiveRemarks() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setRemarks(Remarks.NONE);
        processDirective(profile, "remarks", "active");
        assertEquals(Remarks.ACTIVE, profile.getRemarks());
    }

    @Test
    void processesDirectiveSortVariables() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        processDirective(profile, "sort-variables", "params", "globals");
        assertEquals(List.of(SortCategory.PARAMS, SortCategory.GLOBALS), profile.getSortVariables());
    }

    @Test
    void processesDirectiveSortVariablesEmpty() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        processDirective(profile, "sort-variables");
        assertEquals(SortCategory.getAllCategories(), profile.getSortVariables());
    }

    @Test
    void refusesInvalidOption() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        ExpectedMessages.create()
                .addRegex("Unknown compiler directive 'fluffyBunny'\\. Did you mean '.*'\\?")
                .validate(consumer -> processDirective(consumer, profile, "fluffyBunny", "basic"));
    }

    @Test
    void refusesInvalidValue() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        ExpectedMessages.create()
                .add("Invalid value 'fluffyBunny' of compiler directive 'target'.")
                .validate(consumer -> processDirective(consumer, profile, "target", "fluffyBunny"));
    }
}
