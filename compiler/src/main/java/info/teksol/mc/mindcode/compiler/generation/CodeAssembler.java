package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionDeclaration;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import info.teksol.mc.mindcode.logic.instructions.CustomInstruction;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.WAIT;

/// CodeAssembler provides means for creating code from the AST tree: methods for creating individual instructions
/// and methods for maintaining and tracking AST context. The AST context tracked by this instance is automatically
/// injected into all created instructions so that the caller doesn't have to handle that.
///
/// Labels and temporary variables may also be generated through CodeAssembler.
@NullMarked
public class CodeAssembler extends AbstractMessageEmitter implements ContextfulInstructionCreator, Consumer<LogicInstruction> {
    private final InstructionProcessor processor;
    private final Variables variables;
    private final List<LogicInstruction> instructions = new ArrayList<>();
    private AstContext astContext;
    private boolean internalError = false;

    /// Indicates whether the assembler is active. An inactive assembler ignores generated instructions.
    private boolean active = true;

    public CodeAssembler(CodeAssemblerContext context) {
        super(context.messageConsumer());
        processor = context.instructionProcessor();
        variables = context.variables();
        astContext = context.rootAstContext();
    }

    public NameCreator nameCreator() {
        return variables.nameCreator();
    }

    public InstructionProcessor getProcessor() {
        return processor;
    }

    public LogicLabel nextLabel() {
        return processor.nextLabel();
    }

    public LogicLabel nextMarker() {
        return processor.nextMarker();
    }

    /// Allocates a new temporary variable whose lifespan doesn't enter another node (child, sibling or parent).
    /// The variable will **NOT** be pushed on the stack for recursive calls.
    ///
    /// @return a temporary variable for use strictly within the current AST node
    public LogicVariable unprotectedTemp() {
        return processor.nextTemp();
    }

    /// Allocates a new temporary variable whose scope is limited to a node (i.e. not needed outside that node).
    /// The variable will be pushed on the stack if needed.
    ///
    /// @return a temporary variable for use within and below the current AST node
    public LogicVariable nextTemp() {
        LogicVariable variable = processor.nextTemp();
        variables.registerNodeVariable(variable);
        return variable;
    }

    /// Allocates a new temporary variable which transfers a value from child to parent node.
    /// The variable will be pushed on the stack if needed.
    ///
    /// @return a temporary variable for use within the parent of the current AST node
    public LogicVariable nextNodeResultTemp() {
        LogicVariable variable = processor.nextTemp();
        variables.registerParentNodeVariable(variable);
        return variable;
    }

    /// Provides an unchanging representation of the given ValueStore at the moment this method is called.
    /// The returned value is guaranteed not to change. If `valueStore` is a literal, returns the literal
    /// directly, as it cannot be changed.
    ///
    /// @param valueStore the value to use
    /// @return a LogicValue capturing the current value of the valueStore
    public LogicValue defensiveCopy(ValueStore valueStore, ArgumentType argumentType) {
        if (valueStore instanceof LogicValue value && value.isImmutable()) {
            return value;
        } else {
            LogicVariable tmp = processor.nextTemp().withType(argumentType);
            variables.registerNodeVariable(tmp);
            createSet(tmp, valueStore.getValue(this));
            return tmp;
        }
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
    /// When other errors are encountered and reported while compiling source code, it is assumed that
    /// all internal errors arose due to the errors in the source code. However, when internal errors
    /// are indicated without a syntax error being reported, it means a bug in the compiler.
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
    /// useful to compile unreachable code (e.g., unused function) to validate syntax but avoid generating code.
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
            if (contextType != AstContextType.NONE) {
                astContext = astContext.createChild(node, contextType);
            }
        }
    }

    public void enterFunctionAstNode(MindcodeFunction function, AstFunctionDeclaration node, double weight) {
        if (active) {
            astContext = astContext.createFunctionDeclaration(function, node, node.getContextType(), weight);
        }
    }

    public void enterFunctionBodyAstNode(MindcodeFunction function, AstMindcodeNode node, AstContextType contextType) {
        if (active) {
            astContext = astContext.createFunctionBody(function, node, contextType);
        }
    }

    public void exitAstNode(AstMindcodeNode node) {
        exitAstNode(node, node.getContextType());
    }

    public void exitAstNode(AstMindcodeNode node, AstContextType contextType) {
        if (active) {
            if (contextType != AstContextType.NONE) {
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
            assert astContext.parent() != null;
            astContext = astContext.parent();
        }
    }

    public void setContextType(AstMindcodeNode node, AstContextType contextType, AstSubcontextType subcontextType) {
        if (active) {
            astContext = astContext.createChild(node, contextType, subcontextType);
        }
    }

    public void clearContextType(AstMindcodeNode node) {
        if (active) {
            if (astContext.node() != node) {
                throw new MindcodeInternalError("Unexpected AST context " + astContext);
            }

            assert astContext.parent() != null;
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

    /// Suspends code execution. Used by remote processors to wait until a remote call is initiated.
    /// The current implementation uses a very long wait (over 30,000 years)
    public void suspendExecution() {
        if (processor.isSupported(WAIT)) {
            createWait(LogicNumber.create(processor, "1e12"));
        }
    }

    /// Yields the execution for the rest of the tick. Used when waiting for a remote processor to update variables
    /// or provide results.
    public void createYieldExecution() {
        if (processor.isSupported(WAIT)) {
            createWait( LogicNumber.create(processor, "1e-15"));
        }
    }

    public LogicLabel createNextLabel() {
        return createLabel(nextLabel()).getLabel();
    }

    public LogicInstruction createCustomInstruction(boolean safe, boolean text, boolean label, String opcode, List<LogicArgument> args, List<InstructionParameterType> params) {
        return addInstruction(new CustomInstruction(astContext, safe, text, label, opcode, args, params));
    }
}
