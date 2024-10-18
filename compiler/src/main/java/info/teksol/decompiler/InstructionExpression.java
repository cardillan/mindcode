package info.teksol.decompiler;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.instructions.MlogInstruction;
import info.teksol.mindcode.logic.InstructionParameterType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.TypedArgument;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class InstructionExpression implements MlogExpression, MlogInstruction {
    private final Opcode opcode;
    private final List<MlogExpression> arguments;
    private final List<InstructionParameterType> types;
    private final int inputs;
    private final int outputs;

    public InstructionExpression(Opcode opcode, List<MlogExpression> arguments, List<InstructionParameterType> types) {
        Objects.requireNonNull(opcode);
        Objects.requireNonNull(arguments);
        Objects.requireNonNull(types);
        if (types.size() != arguments.size()) {
            throw new IllegalArgumentException("Argument and type list sizes do not match");
        }
        this.opcode = opcode;
        this.arguments = arguments;
        this.types = types;
        inputs = (int) types.stream().filter(InstructionParameterType::isInput).count();
        outputs = (int) types.stream().filter(InstructionParameterType::isOutput).count();
    }

    public List<MlogExpression> getArguments() {
        return arguments;
    }

    public void setArgument(int index, MlogVariable argument) {
        if (arguments.get(index) instanceof MlogVariable) {
            arguments.set(index, argument);
        } else {
            throw new MindcodeInternalError("Cannot replace expression with a variable");
        }
    }

    @Override
    public void gatherInputVariables(List<MlogVariable> variables) {
        for (int i = 0; i < arguments.size(); i++) {
            if (types.get(i).isInput()) {
                arguments.get(i).gatherInputVariables(variables);
            }
        }
    }

    @Override
    public int size() {
        return inputs;
    }

    @Override
    public void replaceVariable(MlogVariable variable, MlogExpression expression) {
        for (int i = 0; i < arguments.size(); i++) {
            if (types.get(i).isInput()) {
                if (arguments.get(i).equals(variable)) {
                    arguments.set(i, expression);
                } else {
                    arguments.get(i).replaceVariable(variable, expression);
                }
            }
        }
    }

    @Override
    public String toMlog() {
        return "";
    }

    @Override
    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public int getInputs() {
        return inputs;
    }

    @Override
    public int getOutputs() {
        return outputs;
    }

    @Override
    public List<LogicArgument> getArgs() {
        return List.copyOf(arguments);
    }

    @Override
    public LogicArgument getArg(int index) {
        return arguments.get(index);
    }

    @Override
    public List<TypedArgument> getTypedArguments() {
        return IntStream.range(0, arguments.size())
                .mapToObj(index -> new TypedArgument(types.get(index), arguments.get(index)))
                .toList();
    }

    @Override
    public InstructionParameterType getArgumentType(int index) {
        return types.get(index);
    }

    @Override
    public List<InstructionParameterType> getArgumentTypes() {
        return List.copyOf(types);
    }
}
