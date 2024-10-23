package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.MlogInstruction;
import info.teksol.mindcode.logic.*;

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
        return generateCall(new ArrayList<>(opcodeVariant.namedParameters()), true);
    }

    @Override
    public String generateSecondarySampleCall() {
        return generateSecondaryCall(new ArrayList<>(opcodeVariant.namedParameters()), true);
    }

    protected abstract String generateCall(List<NamedParameter> arguments, boolean markOptional);

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
            return secondary == null ? generateCall(arguments, false) : secondary;
        }
        return null;
    }
}
