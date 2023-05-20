package info.teksol.mindcode;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.AstPrettyPrinter;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.grammar.AbstractParserTest;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;

import java.util.List;

public class AbstractAstTest extends AbstractParserTest {

    public AstNode translateToAst(String program) {
        return AstNodeBuilder.generate(parse(program));
    }

    protected static String q(String str) {
        return '"' + str + '"';
    }

    protected final String prettyPrint(List<LogicInstruction> list) {
        //return list.stream().map(Object::toString).reduce("", (s, s2) -> s + "\n" + s2).strip();
        return formatAsCode(list);
    }

    private String formatAsCode(List<LogicInstruction> program) {
        StringBuilder str = new StringBuilder();
        for (LogicInstruction ix : program) {
            str.append(ix.getOpcode().getOpcode());
            ix.getArgs().forEach(a -> str.append(" ").append(escape(a.toMlog())));
            str.append("\n");
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    private String escape(String value) {
        return q(value.replace("\\", "\\\\").replace("\"", "\\\""));
    }

    protected String prettyPrint(AstNode node) {
        InstructionProcessor processor = InstructionProcessorFactory.getInstructionProcessor(ProcessorVersion.V7, ProcessorEdition.S);
        return new AstPrettyPrinter(processor).prettyPrint(node);
    }
}
