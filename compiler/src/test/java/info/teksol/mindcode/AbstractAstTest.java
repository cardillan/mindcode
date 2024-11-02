package info.teksol.mindcode;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.AstPrettyPrinter;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.grammar.AbstractParserTest;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.util.ExpectedMessages;

public class AbstractAstTest extends AbstractParserTest {

    public AstNode translateToAst(String code) {
        return AstNodeBuilder.generate(InputFile.createSourceFile(code),
                ExpectedMessages.throwOnMessage(), parse(code));
    }

    protected void assertGeneratesMessages(ExpectedMessages expectedMessages, String code) {
        AstNodeBuilder.generate(InputFile.createSourceFile(code), expectedMessages, parse(code));
        expectedMessages.validate();
    }

    protected String prettyPrint(AstNode node) {
        InstructionProcessor processor = InstructionProcessorFactory.getInstructionProcessor(ProcessorVersion.V7, ProcessorEdition.S);
        return new AstPrettyPrinter(processor).prettyPrint(node);
    }
}
