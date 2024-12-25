package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.compiler.generator.LogicFunctionV2;
import info.teksol.mindcode.compiler.instructions.CustomInstruction;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.InstructionParameterType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.v3.AstContext;
import info.teksol.mindcode.v3.ContextfulInstructionCreator;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class CodeAssembler extends AbstractMessageEmitter implements ContextfulInstructionCreator, Consumer<LogicInstruction> {
    private final CompilerProfile profile;
    private final InstructionProcessor processor;
    private final List<LogicInstruction> instructions = new ArrayList<>();
    private AstContext astContext;

    /// Indicates whether the assembler is active. An inactive assembler ignores generated instructions.
    private boolean active = true;

    // Note: we'll need to save/restore loop stack when entering inline functions
    // In LIG, the implicit stack was used to manage the state
    private LoopStack loopStack;

    public CodeAssembler(CodeGeneratorContext context) {
        super(context.messageConsumer());
        profile = context.compilerProfile();
        processor = context.instructionProcessor();
        astContext = context.rootAstContext();

        loopStack = new LoopStack(messageConsumer);
    }

    @Override
    public void accept(LogicInstruction logicInstruction) {
        if (active) {
            instructions.add(logicInstruction);
        }
    }

    /// Indicates whether the assembler is active. An inactive assembler ignores generated instructions.
    public boolean isActive() {
        return active;
    }

    /// Activates/deactivates assembler status. An inactive assembler ignores generated instructions. This is
    /// useful to compile unreachable code (e.g. unused function) to validate syntax, but avoid generating code.
    public void setActive(boolean active) {
        this.active = active;
    }

    public AstContext getAstContext() {
        return astContext;
    }

    public List<LogicInstruction> getInstructions() {
        return instructions;
    }

    public LoopStack getLoopStack() {
        return loopStack;
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

    public void setSubcontextType(LogicFunctionV2 function, AstSubcontextType subcontextType) {
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
        if (active) {
            instructions.add(instruction);
        }
        return instruction;
    }

    public void createCompilerEnd() {
        if (active) {
            setSubcontextType(AstSubcontextType.END, 1.0);
            createEnd();
            clearSubcontextType();
        }
    }

    public void createCustomInstruction(boolean safe, String opcode, List<LogicArgument> args, List<InstructionParameterType> params) {
        if (active) {
            CustomInstruction customInstruction = new CustomInstruction(astContext, safe, opcode, args, params);
            instructions.add(customInstruction);
        }
    }
}
