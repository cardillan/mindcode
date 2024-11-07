package info.teksol.mindcode.v3.compiler.antlr;

import info.teksol.mindcode.v3.InputFiles;
import info.teksol.util.ExpectedMessages;

class AbstractParserTest {

    protected void assertParses(ExpectedMessages expectedMessages, String source) {
        InputFiles inputFiles = InputFiles.fromSource(source);
        MindcodeParserFacade.parse(expectedMessages, inputFiles.getMainInputFile());
        expectedMessages.validate();
    }

    protected void assertParses(String source) {
        assertParses(ExpectedMessages.none(), source);
    }
}