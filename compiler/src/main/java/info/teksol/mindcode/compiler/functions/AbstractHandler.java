package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.MlogInstruction;
import info.teksol.mindcode.logic.*;
import info.teksol.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractHandler extends AbstractMessageEmitter implements SampleGenerator {
    protected final BaseFunctionMapper functionMapper;
    protected final OpcodeVariant opcodeVariant;

    public AbstractHandler(BaseFunctionMapper functionMapper, OpcodeVariant opcodeVariant) {
        super(functionMapper.getMessageConsumer());
        this.functionMapper = functionMapper;
        this.opcodeVariant = opcodeVariant;
    }

    @Override
    public LogicInstruction generateSampleInstruction() {
        List<LogicArgument> arguments = getOpcodeVariant().namedParameters().stream()
                .map(a -> a.type() == InstructionParameterType.UNUSED_OUTPUT ? "0" : a.name())
                .map(BaseArgument::new)
                .collect(Collectors.toList());
        return functionMapper.createSampleInstruction(getOpcode(), arguments);
    }

    public String generateSampleCall() {
        return generateCall(new ArrayList<>(opcodeVariant.namedParameters()));
    }

    @Override
    public String generateSecondarySampleCall() {
        return generateSecondaryCall(new ArrayList<>(opcodeVariant.namedParameters()), true);
    }

    protected String generateCall(List<NamedParameter> arguments) {
        StringBuilder str = new StringBuilder();
        NamedParameter result = CollectionUtils.removeFirstMatching(arguments, a -> a.type() == InstructionParameterType.RESULT);
        if (result != null) {
            str.append(result.name()).append(" = ");
        }

        List<String> strArguments = arguments.stream()
                .filter(a -> !a.type().isUnused() && !a.type().isFunctionName())
                .map(a -> a.type().isOutput() ? "out " + a.name() : a.name())
                .collect(Collectors.toList());

        str.append(getName()).append("(").append(String.join(", ", strArguments)).append(")");
        return str.toString();
    }

    protected String generateSecondaryCall(List<NamedParameter> arguments, boolean markOptional) {
        return null;
    }

    public String decompile(MlogInstruction instruction) {
        if (instruction.getOpcode() == opcodeVariant.opcode() && opcodeVariant.namedParameters().size() <= instruction.getArgs().size()) {
            List<NamedParameter> arguments = new ArrayList<>();
            for (int i = 0; i < opcodeVariant.namedParameters().size(); i++) {
                NamedParameter parameter = opcodeVariant.namedParameters().get(i);
                String mlog = instruction.getArgs().get(i).toMlog();
                if (parameter.type().isSelector() && !parameter.name().equals(mlog)) {
                    return null;
                }
                arguments.add(new NamedParameter(parameter.type(), mlog));
            }

            // Prefer secondary version  when available
            String secondary = generateSecondaryCall(arguments, false);
            return secondary == null ? generateCall(arguments) : secondary;
        }
        return null;
    }

    protected boolean validateStandardArgumentModifiers(List<LogicFunctionArgument> declaredArguments) {
        boolean valid = true;

        for (LogicFunctionArgument argument : declaredArguments) {
            if (!argument.hasValue()) {
                error(argument.pos(), "Parameter corresponding to this argument isn't optional, a value must be provided.");
                valid = false;
            }
            if (argument.outModifier()) {
                error(argument.pos(), "Parameter corresponding to this argument isn't output, 'out' modifier cannot be used.");
                valid = false;
            }
        }

        return valid;
    }

    protected LogicValue requireNoModifiers(NamedParameter parameter, LogicFunctionArgument argument) {
        if (!argument.hasValue()) {
            error(argument.pos(), "Parameter '%s' isn't optional, a value must be provided.", parameter.name());
        }
        if (argument.inModifier() || argument.outModifier()) {
            error(argument.pos(), "Parameter '%s' is an mlog keyword, no 'in' or 'out' modifiers allowed.", parameter.name());
        }
        return argument.value();
    }

    protected LogicValue requireNoOutModifier(NamedParameter parameter, LogicFunctionArgument argument) {
        if (!argument.hasValue()) {
            error(argument.pos(), "Parameter '%s' isn't optional, a value must be provided.", parameter.name());
        }
        if (argument.outModifier()) {
            error(argument.pos(), "Parameter '%s' isn't output, 'out' modifier not allowed.", parameter.name());
        }
        return argument.value();
    }

    protected LogicValue requireOutModifier(NamedParameter parameter, LogicFunctionArgument argument) {
        if (argument.inModifier()) {
            error(argument.pos(), "Parameter '%s' isn't input, 'in' modifier not allowed.", parameter.name());
        }
        if (argument.hasValue()) {
            if (!argument.outModifier()) {
                warn(argument.pos(), "Parameter '%s' is output and 'out' modifier was not used, assuming 'out'.", parameter.name());
            }
            if (!argument.value().isUserVariable()) {
                error(argument.pos(), "Argument assigned to output parameter '%s' is not writable.", parameter.name());
            }
        }
        return argument.value();
    }
}
