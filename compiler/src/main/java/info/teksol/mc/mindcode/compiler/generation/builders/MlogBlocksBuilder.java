package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstMlogBlockVisitor;
import info.teksol.mc.generated.ast.visitors.AstMlogStatementVisitor;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicToken;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class MlogBlocksBuilder extends AbstractBuilder implements
        AstMlogBlockVisitor<ValueStore>,
        AstMlogStatementVisitor<ValueStore>
{
    public MlogBlocksBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    private record MlogVariable(String name, ValueStore valueStore, boolean in, boolean out) {
        InstructionParameterType type() {
            return out ? in ? InstructionParameterType.INPUT_OUTPUT : InstructionParameterType.OUTPUT : InstructionParameterType.INPUT;
        }
    }

    // Maps declared variable names to their value stores.
    // Only one instance is needed, since mlog blocks can't be reentered.
    private final Map<String, MlogVariable> mlogVariables = new HashMap<>();
    private final Set<String> labels = new HashSet<>();
    private int mlogBlockIndex = 0;
    private String mlogBlockPrefix = "";

    private void clear() {
        mlogVariables.clear();
        labels.clear();
    }

    @Override
    public ValueStore visitMlogBlock(AstMlogBlock node) {
        try {
            mlogBlockPrefix = "m" + mlogBlockIndex++ + "_";
            node.getVariables().forEach(this::resolveMlogVariable);
            node.getStatements().stream().filter(AstMlogStatement::isLabel).forEach(this::processLabel);
            visitBody(node.getStatements());
            return LogicVoid.VOID;
        } finally {
            clear();
        }
    }

    private void resolveMlogVariable(AstMlogVariable variable) {
        ValueStore valueStore = variables.resolveVariable(variable.getIdentifier(), true, false);
        if (valueStore.isComplex()) {
            error(variable, ERR.MLOG_BLOCK_VARIABLE_NOT_SIMPLE, variable.getName());
        } else {
            MlogVariable mlogVariable = new MlogVariable(variable.getName(), valueStore, variable.isInput(), variable.isOutput());
            mlogVariables.put(variable.getName(), mlogVariable);
        }
    }

    private void processLabel(AstMlogStatement astMlogStatement) {
        assert astMlogStatement.getLabel() != null;
        String label = astMlogStatement.getLabel().getName();
        if (!label.endsWith(":")) {
            throw new MindcodeInternalError("Label without the ':' suffix: " + label);
        }
        String name = label.substring(0, label.length() - 1);
        if (!labels.add(name)) {
            error(astMlogStatement, ERR.MLOG_BLOCK_DUPLICATE_LABEL, name);
        }
    }

    @Override
    public ValueStore visitMlogStatement(AstMlogStatement node) {
        if (node.isLabel()) {
            // A label
            assembler.createCustomInstruction(false, false, true, mlogBlockPrefix + node.getLabelName(),
                    List.of(), List.of());
        } else if (node.getInstruction() != null) {
            LogicInstruction instruction = processMlogInstruction(node.getInstruction());
            if (node.getComment() != null) {
                instruction.setComment(node.getCommentText());
            }
        } else if (node.getComment() != null) {
            // Plain comment
            assembler.createComment(node.getCommentText());
        } else {
            throw new MindcodeInternalError("Mlog statement has not active content");
        }
        return LogicVoid.VOID;
    }

    private LogicInstruction processMlogInstruction(AstMlogInstruction instruction) {
        String opcode = instruction.getOpcode().getToken();

        if (opcode.equals(Opcode.END.getOpcode())) return assembler.createEnd();

        if (opcode.equals(Opcode.JUMP.getOpcode())) {
            if (instruction.getTokens().isEmpty()) {
                error(instruction, ERR.MLOG_BLOCK_MISSING_LABEL);
            } else {
                if (instruction.getToken(0) instanceof AstMlogToken token) {
                    if (!labels.contains(token.getToken())) {
                        error(instruction.getToken(0), ERR.MLOG_BLOCK_UNKNOWN_LABEL, token.getToken());
                    }
                } else {
                    error(instruction.getToken(0), ERR.MLOG_BLOCK_INVALID_LABEL);
                }
            }
        }

        List<LogicArgument> arguments = new ArrayList<>();
        List<InstructionParameterType> parameters = new ArrayList<>();

        for (AstExpression expression : instruction.getTokens()) {
            if (expression instanceof AstMlogToken token) {
                String mlog = token.getToken();
                MlogVariable mlogVariable = mlogVariables.get(mlog);

                if (labels.contains(mlog)) {
                    // A label
                    arguments.add(new LogicToken(mlogBlockPrefix + mlog));
                    parameters.add(InstructionParameterType.UNSPECIFIED);
                } else if (mlog.startsWith("$")) {
                    // An inlined variable
                    String name = mlog.substring(1);
                    ValueStore value = variables.findVariable(name, true);
                    if (value == null || value.isComplex()) {
                        error(token, value == null ? ERR.MLOG_BLOCK_VARIABLE_NOT_FOUND
                                : ERR.MLOG_BLOCK_VARIABLE_NOT_SIMPLE, name);
                        arguments.add(new LogicToken(mlog));
                        parameters.add(InstructionParameterType.UNSPECIFIED);
                    } else {
                        arguments.add(value.getValue(assembler));
                        parameters.add(InstructionParameterType.INPUT);
                    }
                } else if (mlogVariable != null) {
                    // A declared variable
                    arguments.add(mlogVariable.valueStore.getValue(assembler));
                    parameters.add(mlogVariable.type());
                } else {
                    // Note: mlogVariables can't contain keys starting with ':'
                    arguments.add(new LogicToken(mlog.startsWith(":") ? mlog.substring(1) : mlog));
                    parameters.add(InstructionParameterType.UNSPECIFIED);
                }
            } else {
                // A literal
                ValueStore value = evaluate(expression);
                if (value instanceof LogicArgument argument) {
                    arguments.add(argument);
                    parameters.add(InstructionParameterType.UNSPECIFIED);
                } else {
                    throw new MindcodeInternalError("Expected LogicArgument, got " + value);
                }
            }
        }

        return assembler.createCustomInstruction(false, true, false, opcode, arguments, parameters);
    }
}
