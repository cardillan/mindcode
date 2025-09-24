package info.teksol.mc.mindcode.compiler.preprocess;

import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstDirectiveSet;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstDirectiveValue;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstModule;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.*;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static info.teksol.mc.common.SourcePosition.EMPTY;
import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class DirectivePreprocessorTest {

    private AstModule directive(String option, String... values) {
        return new AstModule(EMPTY,
                null,
                List.of(
                        new AstDirectiveSet(EMPTY, false,
                                new AstDirectiveValue(EMPTY, option),
                                Stream.of(values).map(this::directiveValue).toList())
                ),
                Collections.emptySortedSet(),
                true
        );
    }

    private AstDirectiveValue directiveValue(String value) {
        return new AstDirectiveValue(EMPTY, value);
    }

    private void processDirective(MessageConsumer messageConsumer, CompilerProfile profile, String option, String... values) {
        DirectivePreprocessor.processGlobalDirectives(new PreprocessorContextImpl(messageConsumer), profile,
                directive(option, values));
    }


    private void processDirective(CompilerProfile profile, String option, String... values) {
        processDirective(ExpectedMessages.throwOnMessage(), profile, option, values);
    }

    @Test
    void processesDirectiveAutoPrintflush() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setAutoPrintflush(false);
        processDirective(profile, "auto-printflush", "true");
        assertTrue(profile.isAutoPrintflush());
    }

    @Test
    void processesDirectiveBoundaryChecks() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setBoundaryChecks(RuntimeChecks.NONE);
        processDirective(profile, "boundary-checks", "assert");
        assertEquals(RuntimeChecks.ASSERT, profile.getBoundaryChecks());
    }

    @Test
    void processesDirectiveBuiltinEvaluation() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setBuiltinEvaluation(BuiltinEvaluation.COMPATIBLE);
        processDirective(profile, "builtin-evaluation", "none");
        assertEquals(BuiltinEvaluation.NONE, profile.getBuiltinEvaluation());
    }

    @Test
    void processesDirectiveCaseConfiguration() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setCaseConfiguration(0);
        processDirective(profile, "case-configuration", "147");
        assertEquals(147, profile.getCaseConfiguration());
    }

    @Test
    void processesDirectiveCaseGenerationLimit() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setCaseOptimizationStrength(1);
        processDirective(profile, "case-optimization-strength", "4");
        assertEquals(4, profile.getCaseOptimizationStrength());
    }

    @Test
    void processesDirectiveDebugOutput() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setDebugOutput(false);
        processDirective(profile, "debug-output", "true");
        assertTrue(profile.isDebugOutput());
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
    void processesDirectiveInstructionLimit() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setInstructionLimit(1);
        processDirective(profile, "instruction-limit", "900");
        assertEquals(900, profile.getInstructionLimit());
    }

    @Test
    void processesDirectiveMlogIndent() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setMlogIndent(0);
        processDirective(profile, "mlog-indent", "4");
        assertEquals(4, profile.getMlogIndent());
    }

    @Test
    void processesDirectiveNullCounterIsNoop() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setNullCounterIsNoop(false);
        assertFalse(profile.isNullCounterIsNoop());
        processDirective(profile, "null-counter-is-noop", "true");
        assertTrue(profile.isNullCounterIsNoop());
    }

    @Test
    void processesDirectiveOptimization() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setAllOptimizationLevels(OptimizationLevel.NONE);
        processDirective(profile, "optimization", "basic");
        assertTrue(Optimization.LIST.stream().map(profile::getOptimizationLevel).allMatch(l -> l == OptimizationLevel.BASIC));
    }

    @Test
    void processesDirectiveOutputProfiling() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setOutputProfiling(false);
        processDirective(profile, "output-profiling", "true");
        assertTrue(profile.isOutputProfiling());
    }

    @Test
    void processesDirectivePasses() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setOptimizationPasses(1);
        processDirective(profile, "passes", "10");
        assertEquals(10, profile.getOptimizationPasses());
    }

    @Test
    void processesDirectivePrintUnresolved() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setFinalCodeOutput(FinalCodeOutput.NONE);
        processDirective(profile, "print-unresolved", "source");
        assertEquals(FinalCodeOutput.SOURCE, profile.getFinalCodeOutput());
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
    void processesDirectiveSymbolicLabels() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setSymbolicLabels(false);
        processDirective(profile, "symbolic-labels", "true");
        assertTrue(profile.isSymbolicLabels());
    }

    @Test
    void processesDirectiveSyntax() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setSyntacticMode(SyntacticMode.RELAXED);
        processDirective(profile, "syntax", "strict");
        assertEquals(SyntacticMode.STRICT, profile.getSyntacticMode());
    }

    @Test
    void processesDirectiveTarget() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        processDirective(profile, "target", "6");
        assertEquals(ProcessorVersion.V6, profile.getProcessorVersion());
    }

    @Test
    void processesDirectiveTargetMinor() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        processDirective(profile, "target", "7.1");
        assertEquals(ProcessorVersion.V7A, profile.getProcessorVersion());
    }

    @Test
    void processesDirectiveTargetGuard() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setTargetGuard(false);
        processDirective(profile, "target-guard", "true");
        assertTrue(profile.isTargetGuard());
    }

    @Test
    void processesDirectiveUnsafeCaseOptimization() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setUnsafeCaseOptimization(false);
        processDirective(profile, "unsafe-case-optimization", "true");
        assertTrue(profile.isUnsafeCaseOptimization());
    }

    @Test
    void processesDirectiveMlogBlockOptimization() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setMlogBlockOptimization(false);
        processDirective(profile, "mlog-block-optimization", "true");
        assertTrue(profile.isMlogBlockOptimization());
    }

    @Test
    void processesDirectiveTextJumpTables() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setTextJumpTables(false);
        processDirective(profile, "text-tables", "false");
        assertFalse(profile.isTextJumpTables());
    }

    @Test
    void refusesInvalidOption() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        ExpectedMessages.create()
                .addRegex("Unknown compiler directive 'fluffyBunny'\\..*")
                .validate(consumer -> processDirective(consumer, profile, "fluffyBunny", "basic"));
    }

    @Test
    void refusesInvalidValue() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        ExpectedMessages.create()
                .addRegex("Invalid value 'fluffyBunny' of compiler directive 'target'.*")
                .validate(consumer -> processDirective(consumer, profile, "target", "fluffyBunny"));
    }
}
