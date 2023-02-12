package info.teksol.mindcode.mindustry.instructions;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.mindustry.generator.GenerationException;
import info.teksol.mindcode.mindustry.logic.ArgumentType;
import info.teksol.mindcode.mindustry.logic.NamedArgument;
import info.teksol.mindcode.mindustry.logic.Opcode;
import info.teksol.mindcode.mindustry.logic.OpcodeVariant;
import info.teksol.mindcode.mindustry.logic.ProcessorEdition;
import info.teksol.mindcode.mindustry.logic.ProcessorVersion;
import info.teksol.mindcode.mindustry.logic.TypedArgument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static info.teksol.mindcode.mindustry.logic.ArgumentType.UNUSED;

public class BaseInstructionProcessor implements InstructionProcessor {
    private final ProcessorVersion processorVersion;
    private final ProcessorEdition processorEdition;
    private final List<OpcodeVariant> opcodeVariants;
    private final Map<Opcode, List<OpcodeVariant>> variantsByOpcode;
    private final Map<Opcode, Map<String, OpcodeVariant>> variantsByKeyword;
    private final Map<Opcode, Integer> opcodeKeywordPosition;
    private final Map<ArgumentType, Set<String>> validArgumentValues;
    private int tmpIndex = 0;
    private int labelIndex = 0;

    BaseInstructionProcessor(ProcessorVersion processorVersion, ProcessorEdition processorEdition,
            List<OpcodeVariant> opcodeVariants) {
        this.processorVersion = processorVersion;
        this.processorEdition = processorEdition;
        this.opcodeVariants = opcodeVariants;
        variantsByOpcode = opcodeVariants.stream().collect(Collectors.groupingBy(OpcodeVariant::getOpcode));
        opcodeKeywordPosition = variantsByOpcode.keySet().stream()
                .collect(Collectors.toMap(k -> k, k -> getOpcodeVariantSelectorPosition(k, variantsByOpcode.get(k))));

        variantsByKeyword = opcodeVariants.stream().collect(Collectors.groupingBy(OpcodeVariant::getOpcode,
                Collectors.toMap(this::getOpcodeVariantKeyword, v -> v)));

        validArgumentValues = createAllowedArgumentValuesMap();
    }


    @Override
    public String nextLabel() {
        return getLabelPrefix() + labelIndex++;
    }

    @Override
    public String nextTemp() {
        return getTempPrefix() + tmpIndex++;
    }

    @Override
    public LogicInstruction createInstruction(Opcode opcode, List<String> arguments) {
        return validate(new LogicInstruction(opcode, arguments));
    }

    @Override
    public LogicInstruction createInstruction(Opcode opcode, String... arguments) {
        return createInstruction(opcode, List.of(arguments));
    }

    @Override
    public LogicInstruction replaceArg(LogicInstruction instruction, int argIndex, String arg) {
        if (instruction.getArg(argIndex).equals(arg)) {
            return instruction;
        }
        else {
            List<String> newArgs = new ArrayList<>(instruction.getArgs());
            newArgs.set(argIndex, arg);
            return createInstruction(instruction.getOpcode(), newArgs);
        }
    }

    @Override
    public LogicInstruction replaceAllArgs(LogicInstruction instruction, String oldArg, String newArg) {
        List<String> args = new ArrayList<>(instruction.getArgs());
        args.replaceAll(arg -> arg.equals(oldArg) ? newArg : arg);
        return createInstruction(instruction.getOpcode(), args);
    }

    @Override
    public List<ArgumentType> getArgumentTypes(LogicInstruction instruction) {
        return getArgumentTypes(instruction.getOpcode(), instruction.getArgs());
    }

    @Override
    public int getTotalInputs(LogicInstruction instruction) {
        return (int) getArgumentTypes(instruction).stream().filter(ArgumentType::isInput).count();
    }

    @Override
    public int getTotalOutputs(LogicInstruction instruction) {
        return (int) getArgumentTypes(instruction).stream().filter(ArgumentType::isOutput).count();
    }

    @Override
    public int getPrintArgumentCount(LogicInstruction instruction) {
        // Maximum over all existing opcode variants, plus additional opcode-specific unused arguments
        return variantsByOpcode.get(instruction.getOpcode()).stream()
                .mapToInt(v -> v.getArguments().size()).max().orElse(0)
                + instruction.getOpcode().getAdditionalPrintArguments();
    }

    @Override
    public List<String> getInputValues(LogicInstruction instruction) {
        return getTypedArguments(instruction)
                .filter(a -> a.getArgumentType().isInput())
                .map(TypedArgument::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getOutputValues(LogicInstruction instruction) {
        return getTypedArguments(instruction)
                .filter(a -> a.getArgumentType().isOutput())
                .map(TypedArgument::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Stream<TypedArgument> getTypedArguments(LogicInstruction instruction) {
        List<ArgumentType> types = getArgumentTypes(instruction);
        return IntStream.range(0, types.size())
                .mapToObj(i -> new TypedArgument(types.get(i), instruction.getArgs().get(i)));
    }


    @Override
    public boolean isValid(ArgumentType type, String value) {
        if (type.isInput() || type.isOutput() || type == UNUSED) {
            return true;
        } else {
            Set<String> values = validArgumentValues.get(type);
            return values == null || values.contains(value);
        }
    }

    protected OpcodeVariant getOpcodeVariant(Opcode opcode, List<String> arguments) {
        List<OpcodeVariant> variants = variantsByOpcode.get(opcode);
        if (variants == null) {
            return null;
        } else if (variants.size() == 1) {
            return variants.get(0);
        } else {
            // Selector keyword position in the list
            int position = opcodeKeywordPosition.get(opcode);

            // We know that variantsByKeyword contains opcode, as variantsByOpcode.get(opcode) isn't null
            return position >= arguments.size() ? null : variantsByKeyword.get(opcode).get(arguments.get(position));
        }
    }

    /**
     * Returns list of argument types based on instruction opcode and instruction variant. The variant of the
     * instruction is determined by inspecting its arguments.
     *
     * @param opcode
     * @param arguments arguments to the instruction
     * @return list of types of given arguments
     */
    protected List<ArgumentType> getArgumentTypes(Opcode opcode, List<String> arguments) {
        OpcodeVariant opcodeVariant = getOpcodeVariant(opcode, arguments);
        return opcodeVariant.getArguments().stream().map(NamedArgument::getType).collect(Collectors.toList());
    }

    protected LogicInstruction validate(LogicInstruction instruction) {
        OpcodeVariant opcodeVariant = getOpcodeVariant(instruction.getOpcode(), instruction.getArgs());
        if (opcodeVariant == null) {
            throw new GenerationException("Invalid or version incompatible instruction " + instruction);
        }

        if (instruction.getArgs().size() != opcodeVariant.getArguments().size()) {
            throw new GenerationException("Wrong number of arguments of instruction " + instruction.getOpcode()
                    + " (expected " + opcodeVariant.getArguments().size() + "). " + instruction);
        }

        for (int i = 0; i < instruction.getArgs().size(); i++) {
            String argument = instruction.getArgs().get(i);
            ArgumentType type = opcodeVariant.getArguments().get(i).getType();
            if (!isValid(type, argument)) {
                throw new GenerationException("Argument " + argument + " not compatible with argument type " + type + ". " + instruction);
            }
        }

        return instruction;
    }

    private int getOpcodeVariantSelectorPosition(Opcode opcode, List<OpcodeVariant> opcodeVariants) {
        List<Integer> indexes = opcodeVariants.stream()
                .map(v -> findFirstIndex(v.getArguments(), a -> a.getType().isSelector()))
                .distinct()
                .collect(Collectors.toList());

        if (indexes.size() != 1)  {
            throw new MindcodeException("Cannot determine variant selector position for opcode " + opcode);
        }

        return indexes.get(0);
    }

    private String getOpcodeVariantKeyword(OpcodeVariant opcodeVariant) {
        int position = opcodeKeywordPosition.get(opcodeVariant.getOpcode());
        if (position < 0) {
            return "";      // Single-variant opcode; no keyword
        } else {
            return opcodeVariant.getArguments().get(position).getName();
        }
    }

    private <E> int findFirstIndex(List<E> list, Predicate<E> criteria) {
        for (int i = 0; i < list.size(); i++) {
            if (criteria.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private Map<ArgumentType, Set<String>> createAllowedArgumentValuesMap() {
        Map<ArgumentType, Set<String>> map = new HashMap<>();
        for (ArgumentType type : ArgumentType.values()) {
            Set<String> allowedValues = createAllowedValues(type);
            if (allowedValues != null) {
                map.put(type, allowedValues);
            }
        }

        return Map.copyOf(map);
    }

    private Set<String> createAllowedValues(ArgumentType type) {
        if (type.isSelector()) {
            return opcodeVariants.stream()
                    .flatMap(v -> v.getArguments().stream())
                    .filter(v -> v.getType() == type)
                    .map(NamedArgument::getName)
                    .collect(Collectors.toUnmodifiableSet());
        } else {
            // Select only compatible keywords and put them into a set
            return type.getAllowedValues().stream()
                    .filter(v -> v.versions.contains(processorVersion))
                    .flatMap(v -> v.values.stream())
                    .collect(Collectors.toUnmodifiableSet());
        }
    }
}
