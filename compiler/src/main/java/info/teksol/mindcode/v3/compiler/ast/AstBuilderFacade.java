package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.v3.InputFile;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;

import java.util.function.Consumer;

public class AstBuilderFacade {

    public static AstMindcodeNode buildAst(Consumer<MindcodeMessage> messageConsumer, MindcodeParser.ProgramContext program,
            InputFile inputFile) {
        final AstBuilder builder = new AstBuilder(messageConsumer, inputFile);
        return builder.visit(program);
    }

}
