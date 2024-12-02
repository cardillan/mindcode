package info.teksol.mindcode.v3.compiler.antlr;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.v3.InputFiles;
import info.teksol.mindcode.v3.MindcodeCompiler;
import info.teksol.util.ExpectedMessages;

public abstract class AbstractParserTest {

    protected static final InputPosition EMPTY = InputPosition.EMPTY;

    protected ExpectedMessages expectedMessages() {
        return ExpectedMessages.create()
                .add("Main file: number of reported ambiguities: 0").ignored();
    }

    protected MindcodeParser.ProgramContext parse(ExpectedMessages expectedMessages, InputFiles inputFiles, boolean validate) {
        MindcodeCompiler compiler = new MindcodeCompiler(MindcodeCompiler.CompilationPhase.PARSER, expectedMessages);
        compiler.compile(inputFiles.getMainInputFile());

        if (validate) {
            expectedMessages.validate();
        }
        return compiler.getParseTree(inputFiles.getMainInputFile());
    }

    protected void assertGeneratesMessages(ExpectedMessages expectedMessages, String source) {
        parse(expectedMessages, InputFiles.fromSource(source), true);
    }

    protected void assertParses(String source) {
        assertGeneratesMessages(expectedMessages(), source);
    }
}