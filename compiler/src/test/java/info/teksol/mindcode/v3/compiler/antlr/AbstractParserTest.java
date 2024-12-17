package info.teksol.mindcode.v3.compiler.antlr;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.mindcode.v3.CompilationPhase;
import info.teksol.mindcode.v3.InputFiles;
import info.teksol.mindcode.v3.MindcodeCompiler;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser.ModuleContext;
import info.teksol.util.ExpectedMessages;

import java.util.function.Function;

public abstract class AbstractParserTest {

    protected static final InputPosition EMPTY = InputPosition.EMPTY;

    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.PARSER;
    }

    protected ProcessorVersion getProcessorVersion() {
        return ProcessorVersion.MAX;
    }

    protected ProcessorEdition getProcessorEdition() {
        return ProcessorEdition.WORLD_PROCESSOR;
    }

    protected CompilerProfile createCompilerProfile() {
        return CompilerProfile.noOptimizations(false)
                .setProcessorVersion(getProcessorVersion())
                .setProcessorEdition(getProcessorEdition())
                .setDebugLevel(3);
    }

    protected ExpectedMessages expectedMessages() {
        return ExpectedMessages.create()
                .add("Main file: number of reported ambiguities: 0").ignored();
    }

    protected <T> T process(ExpectedMessages expectedMessages, InputFiles inputFiles, boolean validate,
            Function<MindcodeCompiler, T> resultExtractor) {
        expectedMessages.setAccumulateErrors(true);
        MindcodeCompiler compiler = new MindcodeCompiler(getTargetPhase(), expectedMessages,
                createCompilerProfile(), inputFiles);

        compiler.compile();
        if (validate) {
            expectedMessages.validate();
        }
        expectedMessages.setAccumulateErrors(false);
        return resultExtractor.apply(compiler);
    }

    protected ModuleContext parse(ExpectedMessages expectedMessages, InputFiles inputFiles, boolean validate) {
        return process(expectedMessages, inputFiles, validate,
                c -> c.getParseTree(inputFiles.getMainInputFile()));
    }

    protected void assertGeneratesMessages(ExpectedMessages expectedMessages, String source) {
        parse(expectedMessages, InputFiles.fromSource(source), true);
    }

    protected void assertParses(String source) {
        assertGeneratesMessages(expectedMessages(), source);
    }
}