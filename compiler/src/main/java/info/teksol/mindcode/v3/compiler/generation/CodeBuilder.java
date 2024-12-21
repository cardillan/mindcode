package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.compiler.generator.LogicFunction;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.v3.ContextfulInstructionCreator;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;

import java.util.ArrayList;
import java.util.List;

public class CodeBuilder implements ContextfulInstructionCreator {
    private final CompilerProfile profile;
    private final InstructionProcessor processor;
    private final List<LogicInstruction> instructions = new ArrayList<>();
    private AstContext astContext;

    public CodeBuilder(CodeGeneratorContext context) {
        profile = context.compilerProfile();
        processor = context.instructionProcessor();
        astContext = context.rootAstContext();
    }

    public AstContext getAstContext() {
        return astContext;
    }

    public List<LogicInstruction> getInstructions() {
        return instructions;
    }

    public void enterAstNode(AstMindcodeNode node) {
        enterAstNode(node, node.getContextType());
    }

    public void enterAstNode(AstMindcodeNode node, AstContextType contextType) {
        if (node.getContextType() != AstContextType.NONE) {
            astContext = astContext.createChild(profile, node, contextType);
        }
    }

    public void enterFunctionAstNode(LogicFunction function, AstMindcodeNode node, double weight) {
        astContext = astContext.createFunctionDeclaration(profile, function, node, node.getContextType(), weight);
    }

    public void exitAstNode(AstMindcodeNode node) {
        if (node.getContextType() != AstContextType.NONE) {
            if (astContext.subcontextType() != node.getSubcontextType() || astContext.node() != node) {
                throw new MindcodeInternalError("Unexpected AST context " + astContext);
            }
            astContext = astContext.parent();
        }
    }

    public void setSubcontextType(AstSubcontextType subcontextType, double multiplier) {
        if (astContext.node() != null && astContext.subcontextType() != astContext.node().getSubcontextType()) {
            clearSubcontextType();
        }
        astContext = astContext.createSubcontext(subcontextType, multiplier);
    }

    public void setSubcontextType(LogicFunction function, AstSubcontextType subcontextType) {
        if (astContext.node() != null && astContext.subcontextType() != astContext.node().getSubcontextType()) {
            clearSubcontextType();
        }
        astContext = astContext.createSubcontext(function, subcontextType, 1.0);
    }

    public void clearSubcontextType() {
        astContext = astContext.parent();
    }

    @Override
    public LogicInstruction createInstruction(Opcode opcode, LogicArgument... arguments) {
        LogicInstruction instruction = processor.createInstruction(astContext, opcode, arguments);
        instructions.add(instruction);
        return instruction;
    }
}
