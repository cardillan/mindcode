package info.teksol.mindcode.v3.compiler.antlr;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.v3.InputFiles;
import info.teksol.util.ExpectedMessages;

public abstract class AbstractParserTest {

    protected final InputPosition EMPTY = InputPosition.EMPTY;

    protected ExpectedMessages expectedMessages() {
        return ExpectedMessages.create()
                .add("Number of reported ambiguities: 0").ignored();
    }

    protected MindcodeParser.ProgramContext parse(ExpectedMessages expectedMessages, InputFiles inputFiles, boolean validate) {
        MindcodeParser.ProgramContext parsed = MindcodeParserFacade.parse(expectedMessages, inputFiles.getMainInputFile());
        if (validate) {
            expectedMessages.validate();
        }
        return parsed;
    }

    protected void assertGeneratesMessages(ExpectedMessages expectedMessages, String source) {
        parse(expectedMessages, InputFiles.fromSource(source), true);
    }

    protected void assertParses(String source) {
        assertGeneratesMessages(expectedMessages(), source);
    }
}