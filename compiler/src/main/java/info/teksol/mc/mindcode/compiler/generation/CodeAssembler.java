package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import info.teksol.mc.mindcode.logic.instructions.CustomInstruction;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@NullMarked
public class CodeAssembler extends AbstractMessageEmitter implements ContextfulInstructionCreator, Consumer<LogicInstruction> {
    private final CompilerProfile profile;
    private final InstructionProcessor processor;
    private final List<LogicInstruction> instructions = new ArrayList<>();
    private AstContext astContext;
    private boolean internalError = false;

    /// Indicates whether the assembler is active. An inactive assembler ignores generated instructions.
    private boolean active = true;

    public CodeAssembler(CodeAssemblerContext context) {
        super(context.messageConsumer());
        profile = context.compilerProfile();
        processor = context.instructionProcessor();
        astContext = context.rootAstContext();
    }

    /// This is the sole method that adds an instruction to the list.
    private LogicInstruction addInstruction(LogicInstruction instruction) {
        if (active) {
            instructions.add(instruction);
        }
        return instruction;
    }

    /// Sets internal error indication.
    ///
    /// Internal errors may arise from compiling invalid source code. In such case it is possible that
    /// unsupported methods may get called (such as writing a value into input argument of a function).
    ///
    /// When other errors are encountered and reported when compiling source code, it is assumed that
    /// all internal errors arose due to the errors in the source code. However, when internal errors
    /// are indicated without a syntax error being reported, it means we have an internal error.
    /// Produced code is probably invalid and must not be published.
    public void setInternalError() {
        internalError = true;
    }

    public boolean isInternalError() {
        return internalError;
    }

    @Override
    public void accept(LogicInstruction instruction) {
        addInstruction(instruction);
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

    public void enterAstNode(AstMindcodeNode node) {
        if (active) {
            enterAstNode(node, node.getContextType());
        }
    }

    public void enterAstNode(AstMindcodeNode node, AstContextType contextType) {
        if (active) {
            if (node.getContextType() != AstContextType.NONE) {
                astContext = astContext.createChild(profile, node, contextType);
            }
        }
    }

    public void enterFunctionAstNode(MindcodeFunction function, AstMindcodeNode node, double weight) {
        if (active) {
            astContext = astContext.createFunctionDeclaration(profile, function, node, node.getContextType(), weight);
        }
    }

    public void exitAstNode(AstMindcodeNode node) {
        if (active) {
            if (node.getContextType() != AstContextType.NONE) {
                if (astContext.subcontextType() != node.getSubcontextType() || astContext.node() != node) {
                    throw new MindcodeInternalError("Unexpected AST context " + astContext);
                }
                astContext = Objects.requireNonNull(astContext.parent());
            }
        }
    }

    public void setSubcontextType(AstSubcontextType subcontextType, double multiplier) {
        if (active) {
            if (astContext.node() != null && astContext.subcontextType() != astContext.node().getSubcontextType()) {
                clearSubcontextType();
            }
            astContext = astContext.createSubcontext(subcontextType, multiplier);
        }
    }

    public void setSubcontextType(MindcodeFunction function, AstSubcontextType subcontextType) {
        if (active) {
            if (astContext.node() != null && astContext.subcontextType() != astContext.node().getSubcontextType()) {
                clearSubcontextType();
            }
            astContext = astContext.createSubcontext(function, subcontextType, 1.0);
        }
    }

    public void clearSubcontextType() {
        if (active) {
            astContext = astContext.parent();
        }
    }

    @Override
    public LogicInstruction createInstruction(Opcode opcode, List<LogicArgument> arguments) {
        return addInstruction(processor.createInstruction(astContext, opcode, arguments));
    }

    public void createCompilerEnd() {
        if (active) {
            setSubcontextType(AstSubcontextType.END, 1.0);
            createEnd();
            clearSubcontextType();
        }
    }

    public void createCustomInstruction(boolean safe, String opcode, List<LogicArgument> args, List<InstructionParameterType> params) {

        addInstruction(new CustomInstruction(astContext, safe, opcode, args, params));
    }
}
