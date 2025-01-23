package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionParameter;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

import static info.teksol.mc.common.SourcePosition.EMPTY;
import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.*;

@NullMarked
public class LogicVariable extends AbstractArgument implements LogicValue, LogicAddress {
    // Looks like a variable, but translates to 0 in mlog.
    private static final LogicVariable UNUSED_VARIABLE = new LogicVariable(EMPTY,
            TMP_VARIABLE, ValueMutability.IMMUTABLE, "0");

    public static final LogicVariable STACK_POINTER = LogicVariable.special("*sp");
    public static final LogicVariable INVALID = LogicVariable.special("*invalid");
    private static final String RETURN_VALUE = "*retval";
    private static final String RETURN_ADDRESS = "*retaddr";

    protected final SourcePosition sourcePosition;
    protected final @Nullable String functionPrefix;
    protected final String name;
    protected final String fullName;
    protected final String mlog;
    protected final boolean isVolatile;
    protected final boolean noinit;
    protected final boolean input;
    protected final boolean output;

    // Copy constructor
    private LogicVariable(SourcePosition sourcePosition, ArgumentType argumentType, ValueMutability mutability,
            @Nullable String functionPrefix, String name, String fullName, String mlog, boolean isVolatile,
            boolean noinit, boolean input, boolean output) {
        super(argumentType, mutability);
        this.sourcePosition = sourcePosition;
        this.functionPrefix = functionPrefix;
        this.name = name;
        this.fullName = fullName;
        this.mlog = mlog;
        this.isVolatile = isVolatile;
        this.noinit = noinit;
        this.input = input;
        this.output = output;
    }

    // For block/parameter
    protected LogicVariable(SourcePosition sourcePosition, ArgumentType argumentType, ValueMutability mutability, String name) {
        super(argumentType, mutability);
        this.sourcePosition = sourcePosition;
        this.functionPrefix = null;
        this.name = Objects.requireNonNull(name);
        this.mlog = name;
        this.fullName = name;
        this.isVolatile = false;
        this.noinit = false;
        this.input = false;
        this.output = false;
    }

    // Global/main
    protected LogicVariable(SourcePosition sourcePosition, ArgumentType argumentType, String name, String mlog,
            boolean isVolatile, boolean noinit) {
        super(argumentType, isVolatile ? ValueMutability.VOLATILE : ValueMutability.MUTABLE);
        this.sourcePosition = sourcePosition;
        this.functionPrefix = null;
        this.name = Objects.requireNonNull(name);
        this.mlog = mlog;
        this.fullName = name;
        this.isVolatile = isVolatile;
        this.noinit = noinit;
        this.input = false;
        this.output = false;
    }

    // Local/parameter
    private LogicVariable(SourcePosition sourcePosition, ArgumentType argumentType, String functionName,
            String functionPrefix, String name, String mlog, boolean input, boolean output) {
        super(argumentType, ValueMutability.MUTABLE);
        this.sourcePosition = sourcePosition;
        this.functionPrefix = Objects.requireNonNull(functionPrefix);
        if (functionPrefix.isEmpty()) {
            throw new IllegalArgumentException("functionPrefix must not be empty");
        }
        if (Objects.requireNonNull(functionName).isEmpty()) {
            throw new IllegalStateException("Empty function name.");
        }
        this.name = Objects.requireNonNull(name);
        this.fullName = functionName + "." + name;
        this.mlog = mlog;
        this.isVolatile = false;
        this.noinit = false;
        this.input = input;
        this.output = output;
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUserVariable() {
        return getType() == PARAMETER || getType() == GLOBAL_VARIABLE || getType() == LOCAL_VARIABLE;
    }

    @Override
    public boolean isUserWritable() {
        return getType() == GLOBAL_VARIABLE || getType() == LOCAL_VARIABLE;
    }

    @Override
    public boolean isGlobalVariable() {
        return getType() == PARAMETER || getType() == GLOBAL_VARIABLE;
    }

    @Override
    public boolean isMainVariable() {
        return getType() == LOCAL_VARIABLE && functionPrefix == null;
    }

    @Override
    public boolean isTemporaryVariable() {
        return getType() == TMP_VARIABLE;
    }

    @Override
    public boolean isLocalVariable() {
        return getType() == LOCAL_VARIABLE && functionPrefix != null;
    }

    @Override
    public boolean isCompilerVariable() {
        return getType() == COMPILER;
    }

    public String getName() {
        return name;
    }

    public @Nullable String getFunctionPrefix() {
        return functionPrefix;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean isInput() {
        return input;
    }

    @Override
    public boolean isOutput() {
        return output;
    }

    public boolean isOptional() {
        return output && !input;
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    @Override
    public String toString() {
        return "LogicVariable{" +
                "argumentType=" + argumentType +
                ", fullName='" + fullName + '\'' +
                ", functionPrefix='" + functionPrefix + '\'' +
                ", isVolatile=" + isVolatile +
                ", input=" + input +
                ", output=" + output +
                '}';
    }

    public static LogicVariable block(SourcePosition sourcePosition, String name) {
        return new LogicVariable(sourcePosition, BLOCK, ValueMutability.IMMUTABLE, name);
    }

    public static LogicVariable block(AstIdentifier identifier) {
        return new LogicVariable(identifier.sourcePosition(), BLOCK, ValueMutability.IMMUTABLE, identifier.getName());
    }

    public static LogicVariable global(AstIdentifier identifier) {
        return new LogicVariable(identifier.sourcePosition(), GLOBAL_VARIABLE,
                identifier.getName(), "." + identifier.getName(), false, false);
    }

    public static LogicVariable global(AstIdentifier identifier, boolean volatileVar, boolean noinit) {
        return new LogicVariable(identifier.sourcePosition(), GLOBAL_VARIABLE,
                identifier.getName(), "." + identifier.getName(), volatileVar, noinit);
    }

    @SuppressWarnings("ConfusingMainMethod")
    public static LogicVariable main(AstIdentifier identifier) {
        return new LogicVariable(identifier.sourcePosition(), LOCAL_VARIABLE,
                identifier.getName(), ":" + identifier.getName(), false, false);
    }

    @SuppressWarnings("ConfusingMainMethod")
    public static LogicVariable main(AstIdentifier identifier, String mlogSuffix) {
        return new LogicVariable(identifier.sourcePosition(), LOCAL_VARIABLE,
                identifier.getName(), ":" + identifier.getName() + mlogSuffix, false, false);
    }

    public static LogicVariable local(AstIdentifier identifier, String functionName, String functionPrefix, String mlogSuffix) {
        return new LogicVariable(identifier.sourcePosition(), LOCAL_VARIABLE, functionName,
                functionPrefix, identifier.getName(), functionPrefix + ":" + identifier.getName() + mlogSuffix,
                false, false);
    }

    public static LogicVariable parameter(AstFunctionParameter parameter, String functionName, String functionPrefix) {
        AstIdentifier identifier = parameter.getIdentifier();
        return new LogicVariable(identifier.sourcePosition(), LOCAL_VARIABLE, functionName,
                functionPrefix, identifier.getName(), functionPrefix + ":" + identifier.getName(),
                parameter.isInput(), parameter.isOutput());
    }

    public static LogicVariable temporary(String name) {
        return new LogicVariable(EMPTY, TMP_VARIABLE, ValueMutability.MUTABLE, name);
    }

    public static LogicVariable ast(String name) {
        return new LogicVariable(EMPTY, AST_VARIABLE, ValueMutability.MUTABLE, name);
    }

    public static LogicVariable fnRetVal(MindcodeFunction function) {
        return new LogicVariable(EMPTY, FUNCTION_RETVAL,
                function.getName(), function.getPrefix(), function.getPrefix() + RETURN_VALUE,
                function.getPrefix() + RETURN_VALUE, false, true);
    }

    public static LogicVariable fnRetVal(String functionName, String functionPrefix) {
        return new LogicVariable(EMPTY, FUNCTION_RETVAL,
                functionName, functionPrefix, functionPrefix + RETURN_VALUE,
                functionPrefix + RETURN_VALUE,false, true);
    }

    public static LogicVariable fnRetAddr(String functionPrefix) {
        return new LogicVariable(EMPTY, FUNCTION_RETADDR, ValueMutability.MUTABLE,
                functionPrefix + RETURN_ADDRESS);
    }

    public static LogicVariable special(String name) {
        return new LogicVariable(EMPTY, COMPILER, ValueMutability.MUTABLE, name);
    }

    /// Return the variable passed as an argument to unused instruction parameters.
    /// @return variable for unused instruction parameters
    public static LogicVariable unusedVariable() {
        return UNUSED_VARIABLE;
    }

    public boolean isNoinit() {
        return noinit;
    }

    @Override
    public boolean isVolatile() {
        return isVolatile;
    }

    // ValueStore methods

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public boolean isLvalue() {
        // Only user-declared variables are l-values
        // Invalids are l-values to suppress further errors
        return isUserWritable() || this == INVALID;
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue value) {
        assembler.createSet(this, value);
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        valueSetter.accept(this);
    }

    public LogicVariable withType(ArgumentType argumentType) {
        return new LogicVariable(sourcePosition, argumentType, mutability, functionPrefix, name, fullName, mlog, isVolatile, noinit, input, output);
    }
}
