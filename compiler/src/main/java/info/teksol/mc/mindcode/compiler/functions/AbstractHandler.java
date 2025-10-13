package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.IdentifierFunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.MlogInstruction;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.NamedParameter;
import info.teksol.mc.mindcode.logic.opcodes.OpcodeVariant;
import info.teksol.mc.util.CollectionUtils;
import info.teksol.mc.util.IntRange;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NullMarked
public abstract class AbstractHandler extends AbstractMessageEmitter implements FunctionHandler, PropertyHandler, SampleGenerator {
    protected final BaseFunctionMapper functionMapper;
    protected final OpcodeVariant opcodeVariant;
    protected final @Nullable CodeAssembler assembler;
    protected final String name;
    protected final IntRange argumentCount;
    protected final boolean hasResult;

    public AbstractHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant,
            int minArgs, int numArgs, boolean hasResult) {
        super(functionMapper.messageConsumer());
        this.functionMapper = functionMapper;
        this.name = name;
        this.opcodeVariant = opcodeVariant;
        this.assembler = functionMapper.assembler;
        this.argumentCount = new IntRange(minArgs, numArgs);
        this.hasResult = hasResult;
    }

    public AbstractHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant,
            int numArgs, boolean hasResult) {
        this(functionMapper, name, opcodeVariant, numArgs, numArgs, hasResult);
    }

    public CodeAssembler assembler() {
        return Objects.requireNonNull(assembler);
    }

    protected boolean validateArguments(AstFunctionCall call, List<FunctionArgument> arguments) {
        if (!argumentCount.contains(arguments.size())) {
            error(call, ERR.FUNCTION_CALL_WRONG_NUMBER_OF_ARGS,
                    name, argumentCount.getRangeString(), arguments.size());
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OpcodeVariant getOpcodeVariant() {
        return opcodeVariant;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "opcodeVariant=" + opcodeVariant + ", name=" + name + ", argumentCount=" + argumentCount + '}';
    }

    @Override
    public LogicInstruction generateSampleInstruction() {
        List<LogicArgument> arguments = getOpcodeVariant().namedParameters().stream()
                .map(a -> a.type() == InstructionParameterType.UNUSED_OUTPUT ? "0" : a.name())
                .map(GenericArgument::new)
                .collect(Collectors.toList());
        return functionMapper.createSampleInstruction(getOpcode(), arguments);
    }

    public String generateSampleCall() {
        return generateCall(new ArrayList<>(opcodeVariant.namedParameters()), false);
    }

    @Override
    public @Nullable String generateSecondarySampleCall() {
        return generateSecondaryCall(new ArrayList<>(opcodeVariant.namedParameters()), true);
    }

    protected String generateCall(List<NamedParameter> arguments, boolean useKeywordPrefixes) {
        StringBuilder str = new StringBuilder();
        NamedParameter result = CollectionUtils.removeFirstMatching(arguments, a -> a.type() == InstructionParameterType.RESULT);
        if (result != null) {
            str.append(result.name()).append(" = ");
        }

        List<String> strArguments = arguments.stream()
                .filter(a -> !a.type().isUnused() && !a.type().isFunctionName())
                .map(a -> useKeywordPrefixes && a.type().isKeyword() || a.type().isSelector() ?
                        ":" + a.name() : a.type().isOutput() ? "out " + a.name() : a.name())
                .collect(Collectors.toList());

        str.append(getName()).append("(").append(String.join(", ", strArguments)).append(")");
        return str.toString();
    }

    protected @Nullable String generateSecondaryCall(List<NamedParameter> arguments, boolean markOptional) {
        return null;
    }

    public @Nullable String decompile(MlogInstruction instruction) {
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
            return secondary == null ? generateCall(arguments, true) : secondary;
        }
        return null;
    }

    protected boolean isVolatileVariable(FunctionArgument argument) {
        return argument.unwrap() instanceof LogicVariable variable && variable.isVolatile();
    }

    private LogicKeyword toKeyword(FunctionArgument argument, boolean requireValidKeyword) {
        if (argument.unwrap() instanceof LogicKeyword keyword) {
            return keyword;
        } else if (argument instanceof IdentifierFunctionArgument ifa) {
            if (requireValidKeyword) {
                warn(argument.sourcePosition(), WARN.MISSING_MLOG_KEYWORD_PREFIX, ifa.getIdentifier().getName());
            }
            return ifa.getKeyword();
        } else if (argument.unwrap() instanceof LogicBoolean bool) {
            return LogicKeyword.create(argument.unwrap().sourcePosition(), bool.toMlog());
        } else {
            return LogicKeyword.INVALID;
        }
    }


    protected LogicKeyword validateKeyword(NamedParameter parameter, FunctionArgument argument, boolean requireValidKeyword) {
        if (argument.hasInModifier() || argument.hasOutModifier()) {
            error(argument, ERR.ARGUMENT_KEYWORD_IN_OUT_NOT_ALLOWED);
            return LogicKeyword.INVALID;
        }

        Collection<String> parameterValues = functionMapper.processor.getParameterValues(parameter.type());

        LogicKeyword keyword = toKeyword(argument, requireValidKeyword);
        if (!keyword.getKeyword().isEmpty()) {
            if (!parameterValues.contains(keyword.getKeyword())) {
                error(argument, ERR.ARGUMENT_KEYWORD_INVALID_VALUE, keyword.getKeywordLiteral(),
                        ":" + String.join("', ':", parameterValues));
                // Fill in one of the valid keywords to avoid further errors
                return requireValidKeyword ? LogicKeyword.create(SourcePosition.EMPTY,
                        parameterValues.iterator().next()) : LogicKeyword.INVALID;
            }

            return keyword;
        } else {
            error(argument, ERR.ARGUMENT_KEYWORD_UNSPECIFIED_VALUE,
                    ":" + String.join("', ':", parameterValues));
            // Fill in one of the valid keywords to avoid further errors
            return requireValidKeyword ? LogicKeyword.create(SourcePosition.EMPTY,
                    parameterValues.iterator().next()) : LogicKeyword.INVALID;
        }
    }

    protected FunctionArgument validateInput(NamedParameter parameter, FunctionArgument argument) {
        if (!argument.hasValue()) {
            error(argument, ERR.ARGUMENT_NOT_OPTIONAL, parameter.name());
        }
        if (argument.hasOutModifier()) {
            error(argument, ERR.ARGUMENT_OUT_MODIFIER_DISALLOWED, parameter.name());
        }
        return argument;
    }

    protected FunctionArgument validateOutput(NamedParameter parameter, FunctionArgument argument) {
        if (argument.hasInModifier()) {
            error(argument, ERR.ARGUMENT_IN_MODIFIER_NOT_ALLOWED, parameter.name());
        }
        if (argument.hasValue()) {
            if (!argument.hasOutModifier()) {
                error(argument, ERR.ARGUMENT_OUT_MODIFIER_REQUESTED, parameter.name());
            }
            if (!argument.isLvalue()) {
                error(argument, ERR.ARGUMENT_NOT_LVALUE, parameter.name());
            }
        }
        return argument;
    }

    @Override
    public ValueStore handleFunction(AstFunctionCall call, List<FunctionArgument> arguments) {
        return handleCall(call, null, arguments);
    }

    @Override
    public LogicValue handleProperty(AstFunctionCall call, ValueStore target, List<FunctionArgument> arguments) {
        return handleCall(call, target, arguments);
    }

    protected LogicValue handleCall(AstFunctionCall call, @Nullable ValueStore target, List<FunctionArgument> arguments) {
        if (!validateArguments(call, arguments)) {
            return LogicNull.NULL;
        }

        LogicValue result = hasResult ? functionMapper.processor.nextTemp() : LogicVoid.VOID;
        CodeAssembler assembler = assembler();

        // List of instruction arguments; must accommodate LogicKeywords
        List<LogicArgument> ixArgs = new ArrayList<>();
        List<FunctionArgument> outputs = new ArrayList<>();
        int argIndex = 0;

        for (NamedParameter p : opcodeVariant.namedParameters()) {
            // Function call was validated, meaning all mandatory parameters have corresponding arguments

            if (p.type().isGlobal() && argIndex < arguments.size() && !isVolatileVariable(arguments.get(argIndex))) {
                error(arguments.get(argIndex).sourcePosition(), ERR.ARGUMENT_VOLATILE_VARIABLE_REQUIRED, name);
                ixArgs.add(LogicVariable.INVALID);
            } else if (p.type() == InstructionParameterType.RESULT) {
                ixArgs.add(result);
            } else if (p.type() == InstructionParameterType.BLOCK && target != null) {
                // Target is not null on property (method) calls only.
                ixArgs.add(target.getValue(assembler()));
            } else if (p.type().isSelector() && !p.type().isFunctionName() || p.type().isKeyword()) {
                // Selector that IS NOT a function name is taken from the argument list
                ixArgs.add(validateKeyword(p, arguments.get(argIndex++), true));
            } else if (p.type().isSelector()) {
                // Selector that IS a function name isn't in an argument list and must be filled in
                ixArgs.add(p.keyword());
            } else if (p.type().isUnused()) {
                // Unused inputs must be filled with defaults
                // Generate new temporary variable for unused outputs (will be replaced by the temp optimizer)
                ixArgs.add(p.type().isOutput() ? functionMapper.processor.nextTemp() : p.keyword());
            } else if (p.type().isInput()) {
                // Input argument - take it as it is
                ixArgs.add(validateInput(p, arguments.get(argIndex++)).getValue(assembler));
            } else if (p.type().isOutput()) {
                if (argIndex >= arguments.size() || !arguments.get(argIndex).hasValue()) {
                    // Optional arguments are always output; generate temporary variable for them
                    ixArgs.add(functionMapper.processor.nextTemp());
                    argIndex++;
                } else {
                    FunctionArgument argument = validateOutput(p, arguments.get(argIndex++));
                    ixArgs.add(argument.getWriteVariable(assembler));
                    outputs.add(argument);
                }
            } else {
                // Nether input nor output???
                ixArgs.add(validateKeyword(p, arguments.get(argIndex++), true));
                throw new MindcodeInternalError("Internal error.");
            }
        }

        assembler.createInstruction(getOpcode(), ixArgs);
        outputs.forEach(argument -> argument.storeValue(assembler));
        return result;
    }
}
