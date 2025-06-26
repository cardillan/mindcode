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
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class MlogBlocksBuilder extends AbstractBuilder implements
        AstMlogBlockVisitor<ValueStore>,
        AstMlogStatementVisitor<ValueStore> {
    public MlogBlocksBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    private record MlogVariable(String name, LogicArgument argument, boolean in, boolean out) {
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

    private void resolveMlogVariable(AstMlogVariable node) {
        LogicArgument argument = findMlogVariable(
                variables.resolveVariable(node.getIdentifier(), true, false));

        if (argument == null) {
            error(node, ERR.MLOG_BLOCK_VARIABLE_NOT_SIMPLE, node.getName());
        } else {
            if (node.isOutput() && !argument.isUserWritable()) {
                error(node, ERR.LVALUE_ASSIGNMENT_TO_CONST_NOT_ALLOWED, node.getName());
            }
            MlogVariable mlogVariable = new MlogVariable(node.getName(), argument, node.isInput(), node.isOutput());
            mlogVariables.put(node.getName(), mlogVariable);
        }
    }

    private void processLabel(AstMlogStatement astLabel) {
        assert astLabel.getLabel() != null;
        String label = astLabel.getLabel().getName();
        if (!label.endsWith(":")) {
            throw new MindcodeInternalError("Label without the ':' suffix: " + label);
        }
        String name = label.substring(0, label.length() - 1);
        if (mlogVariables.containsKey(name)) {
            error(astLabel, ERR.MLOG_BLOCK_CONFLICTING_LABEL, label);
        }
        if (!labels.add(name)) {
            error(astLabel, ERR.MLOG_BLOCK_DUPLICATE_LABEL, name);
        }
    }

    private @Nullable LogicArgument findMlogVariable(@Nullable ValueStore valueStore) {
        if (valueStore instanceof LogicArgument argument) {
            return switch (argument.getType()) {
                case NULL_LITERAL,
                     COLOR_LITERAL,
                     NAMED_COLOR_LITERAL,
                     BOOLEAN_LITERAL,
                     NUMERIC_LITERAL,
                     STRING_LITERAL,
                     BUILT_IN,
                     BLOCK,
                     PARAMETER,
                     GLOBAL_VARIABLE,
                     LOCAL_VARIABLE -> argument;
                default -> null;
            };
        } else {
            return null;
        }
    }

    @Override
    public ValueStore visitMlogStatement(AstMlogStatement node) {
        if (node.isLabel()) {
            // A label
            LogicInstruction instruction = assembler.createCustomInstruction(false, false, true,
                    mlogBlockPrefix + node.getLabelName(), List.of(), List.of());
            if (node.getComment() != null) {
                instruction.setComment(node.getCommentText());
            }
        } else if (node.getInstruction() != null) {
            LogicInstruction instruction = processMlogInstruction(node.getInstruction());
            if (node.getComment() != null) {
                instruction.setComment(node.getCommentText());
            }
        } else if (node.getComment() != null) {
            // Plain comment
            assembler.createComment(node.getCommentText());
        } else {
            throw new MindcodeInternalError("Mlog statement doesn't have an active content");
        }
        return LogicVoid.VOID;
    }

    private LogicInstruction processMlogInstruction(AstMlogInstruction instruction) {
        String opcode = instruction.getOpcode().getPlainToken();

        if (opcode.endsWith(":") && instruction.getTokens().isEmpty()) {
            error(instruction.getOpcode(), ERR.MLOG_BLOCK_INVALID_LABEL);
        }

        if (opcode.equals(Opcode.END.getOpcode())) return assembler.createEnd();

        if (opcode.equals(Opcode.JUMP.getOpcode())) {
            if (instruction.getTokens().isEmpty()) {
                error(instruction, ERR.MLOG_BLOCK_MISSING_LABEL);
            } else {
                if (instruction.getToken(0) instanceof AstMlogToken token) {
                    if (!labels.contains(token.getToken())) {
                        error(token, ERR.MLOG_BLOCK_UNKNOWN_LABEL, token.getToken());
                    }
                } else {
                    error(instruction.getToken(0), ERR.MLOG_BLOCK_NOT_A_LABEL);
                }
            }
        }

        List<LogicArgument> arguments = new ArrayList<>();
        List<InstructionParameterType> parameters = new ArrayList<>();

        for (AstExpression expression : instruction.getTokens()) {
            if (expression instanceof AstMlogToken astToken) {
                String token = astToken.getToken();

                // Note: mlogVariables can't contain keys starting with ':', so raw tokens are not matched
                MlogVariable mlogVariable = mlogVariables.get(token);

                if (labels.contains(token)) {
                    // A label
                    arguments.add(new LogicToken(mlogBlockPrefix + token));
                    parameters.add(InstructionParameterType.UNSPECIFIED);
                } else if (token.startsWith("$")) {
                    // An inlined variable
                    String name = token.substring(1);
                    ValueStore value = variables.findVariable(name, true);
                    LogicArgument argument = findMlogVariable(value);
                    if (argument == null) {
                        error(astToken, value == null ? ERR.MLOG_BLOCK_VARIABLE_NOT_FOUND
                                : ERR.MLOG_BLOCK_VARIABLE_NOT_SIMPLE, name);
                        arguments.add(new LogicToken(token));
                        parameters.add(InstructionParameterType.UNSPECIFIED);
                    } else {
                        arguments.add(argument);
                        parameters.add(InstructionParameterType.INPUT);
                    }
                } else if (mlogVariable != null) {
                    // A declared variable
                    arguments.add(mlogVariable.argument);
                    parameters.add(mlogVariable.type());
                } else {
                    arguments.add(new LogicToken(astToken.getPlainToken()));
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
