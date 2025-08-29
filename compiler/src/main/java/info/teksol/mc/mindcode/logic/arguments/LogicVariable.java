package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionParameter;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionParameter;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

import static info.teksol.mc.common.SourcePosition.EMPTY;
import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.*;

@NullMarked
public class LogicVariable extends AbstractArgument implements LogicValue, LogicAddress, FunctionParameter {
    // Looks like a variable but translates to 0 in mlog.
    private static final LogicVariable UNUSED_VARIABLE = new LogicVariable(EMPTY,
            PRESERVED, ValueMutability.IMMUTABLE, "0", "0");

    // This variable never makes it into a compiled file - it is only used to recover from compiler errors
    // When errors happen, the code is not given away. The name can be hard-coded
    public static final LogicVariable INVALID = LogicVariable.preserved("*invalid");

    private static final String RETURN_VALUE = "*retval";
    private static final String RETURN_ADDRESS = "*retaddr";
    private static final String FUNCTION_FINISHED = "*finished";
    private static final String REMOTE_WAIT_ADDRESS = "*waitaddr";

    protected final SourcePosition sourcePosition;
    protected final String functionPrefix;
    protected final String name;
    protected final String fullName;
    protected final String mlog;
    protected final boolean isVolatile;
    protected final boolean noinit;
    protected final boolean input;
    protected final boolean output;
    protected final boolean reference;
    protected final boolean optional;
    protected final boolean preserved;

    // Copy constructor
    private LogicVariable(SourcePosition sourcePosition, ArgumentType argumentType, ValueMutability mutability,
            String functionPrefix, String name, String fullName, String mlog, boolean isVolatile,
            boolean noinit, boolean input, boolean output, boolean reference) {
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
        this.reference = reference;
        this.optional = false;
        this.preserved = false;
    }

    // For block/parameter
    protected LogicVariable(SourcePosition sourcePosition, ArgumentType argumentType, ValueMutability mutability,
            String name, String mlog) {
        super(argumentType, mutability);
        this.sourcePosition = sourcePosition;
        this.functionPrefix = "";
        this.name = Objects.requireNonNull(name);
        this.mlog = Objects.requireNonNull(mlog);
        this.fullName = name;
        this.isVolatile = false;
        this.noinit = false;
        this.input = false;
        this.output = false;
        this.reference = false;
        this.optional = false;
        this.preserved = false;
    }

    // Global/main
    private LogicVariable(SourcePosition sourcePosition, ArgumentType argumentType, String name, String mlog,
            boolean isVolatile, boolean noinit, boolean optional) {
        super(argumentType, isVolatile ? ValueMutability.VOLATILE : ValueMutability.MUTABLE);
        this.sourcePosition = sourcePosition;
        this.functionPrefix = "";
        this.name = Objects.requireNonNull(name);
        this.mlog = mlog;
        this.fullName = name;
        this.isVolatile = isVolatile;
        this.noinit = noinit;
        this.input = false;
        this.output = false;
        this.reference = false;
        this.optional = optional;
        this.preserved = false;
    }

    // Local/parameter
    private LogicVariable(SourcePosition sourcePosition, ArgumentType argumentType, String functionName,
            String functionPrefix, String name, String mlog, boolean noinit, boolean input, boolean output,
            boolean reference, boolean preserved) {
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
        this.noinit = noinit;
        this.input = input;
        this.output = output;
        this.reference = reference;
        this.optional = false;
        this.preserved = preserved;
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
        return getType() == PARAMETER || getType() == GLOBAL_VARIABLE || getType() == GLOBAL_PRESERVED;
    }

    @Override
    public boolean isMainVariable() {
        return getType() == LOCAL_VARIABLE && functionPrefix.isEmpty();
    }

    @Override
    public boolean isTemporaryVariable() {
        return getType() == TMP_VARIABLE;
    }

    @Override
    public boolean isLocalVariable() {
        return getType() == LOCAL_VARIABLE && functionPrefix.isEmpty();
    }

    @Override
    public boolean isPreserved() {
        return preserved || getType() == PRESERVED || getType() == MLOG_VARIABLE || getType() == GLOBAL_PRESERVED;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getFunctionPrefix() {
        return functionPrefix;
    }

    public String getFullName() {
        return fullName;
    }

    public LogicString getMlogString() {
        return LogicString.create(sourcePosition, mlog);
    }

    /// @return true if the parameter is effectively input
    @Override
    public boolean isInput() {
        return input;
    }

    @Override
    public boolean isOutput() {
        return output;
    }

    public boolean isOptional() {
        return optional || output && !input;
    }

    public boolean isReference() {
        return reference;
    }

    public boolean isNoinit() {
        return noinit;
    }

    @Override
    public boolean isVolatile() {
        return isVolatile;
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

    // CREATION

    public static LogicVariable block(SourcePosition sourcePosition, String name) {
        return new LogicVariable(sourcePosition, BLOCK, ValueMutability.IMMUTABLE, name, name);
    }

    public static LogicVariable block(AstIdentifier identifier) {
        return new LogicVariable(identifier.sourcePosition(), BLOCK, ValueMutability.IMMUTABLE, identifier.getName(), identifier.getName());
    }

    public static LogicVariable block(AstIdentifier identifier, AstIdentifier linkedTo) {
        return new LogicVariable(identifier.sourcePosition(), BLOCK, ValueMutability.IMMUTABLE, identifier.getName(), linkedTo.getName());
    }

    public static LogicVariable global(AstIdentifier identifier, String mlog) {
        return new LogicVariable(identifier.sourcePosition(), GLOBAL_VARIABLE,
                identifier.getName(), mlog, false, false, false);
    }

    public static LogicVariable global(AstIdentifier identifier, String mlog, boolean volatileVar, boolean noinit, boolean optional) {
        return new LogicVariable(identifier.sourcePosition(), GLOBAL_VARIABLE,
                identifier.getName(), mlog, volatileVar, noinit, optional);
    }

    @SuppressWarnings("ConfusingMainMethod")
    public static LogicVariable main(AstIdentifier identifier, String mlog) {
        return main(identifier, mlog, false);
    }

    @SuppressWarnings("ConfusingMainMethod")
    public static LogicVariable main(AstIdentifier identifier, String mlog, boolean noinit) {
        return new LogicVariable(identifier.sourcePosition(), LOCAL_VARIABLE,
                identifier.getName(), mlog, false, noinit, false);
    }

    public static LogicVariable local(AstIdentifier identifier, MindcodeFunction function, String mlog, boolean noinit) {
        return new LogicVariable(identifier.sourcePosition(), LOCAL_VARIABLE, function.getName(),
                function.getPrefix(), identifier.getName(), mlog, noinit, false, false, false, false);
    }

    public static LogicVariable parameter(AstFunctionParameter parameter, MindcodeFunction function, String mlog, boolean preserved) {
        AstIdentifier identifier = parameter.getIdentifier();
        return new LogicVariable(identifier.sourcePosition(), LOCAL_VARIABLE, function.getName(),
                function.getPrefix(), identifier.getName(), mlog,
                false, parameter.isInput(), parameter.isOutput(), parameter.isReference(), preserved);
    }

    public static LogicVariable temporary(String name) {
        return new LogicVariable(EMPTY, TMP_VARIABLE, ValueMutability.MUTABLE, name, name);
    }

    public static LogicVariable fnRetVal(MindcodeFunction function, String mlog) {
        return new LogicVariable(function.getSourcePosition(), FUNCTION_RETVAL,
                function.getName(), function.getPrefix(), function.getPrefix() + RETURN_VALUE,
                mlog, false, false, true, false, function.isRemote());
    }

    public static LogicVariable fnRetAddr(MindcodeFunction function, String mlog) {
        return new LogicVariable(function.getSourcePosition(), FUNCTION_RETADDR, ValueMutability.MUTABLE, mlog, mlog);
    }

    public static LogicVariable fnFinished(MindcodeFunction function, String mlog) {
        return new LogicVariable(EMPTY, GLOBAL_PRESERVED,
                function.getName(), function.getPrefix(), function.getPrefix() + FUNCTION_FINISHED,
                mlog,false, false, true, false, true);
    }

    public static LogicVariable remoteWaitAddr() {
        return new LogicVariable(EMPTY, ADDRESS, ValueMutability.IMMUTABLE, REMOTE_WAIT_ADDRESS, REMOTE_WAIT_ADDRESS);
    }

    public static LogicVariable preserved(String name) {
        return new LogicVariable(EMPTY, PRESERVED, ValueMutability.MUTABLE, name, name);
    }

    public static LogicVariable mlogVariable(String name) {
        return new LogicVariable(EMPTY, MLOG_VARIABLE, name, name, true, true, false);
    }

    public static LogicVariable arrayElement(AstIdentifier identifier, int index, String mlog, boolean isVolatile) {
        return new LogicVariable(identifier.sourcePosition(), GLOBAL_VARIABLE,
                identifier.getName() + "[" + index + "]", mlog, isVolatile, true, false);
    }

    public static LogicVariable arrayAccess(String arrayName, String suffix, String mlog) {
        return new LogicVariable(EMPTY, GLOBAL_VARIABLE,
                arrayName + suffix, mlog, false, true, true);
    }

    public static LogicVariable arrayReturn(String arrayName, String suffix, String mlog) {
        return new LogicVariable(EMPTY, FUNCTION_RETADDR,
                arrayName + suffix, mlog, false, true, false);
    }

    /// Return the variable passed as an argument to unused instruction parameters.
    /// @return variable for unused instruction parameters
    public static LogicVariable unusedVariable() {
        return UNUSED_VARIABLE;
    }

    // TEST SUPPORT

    public static LogicVariable ast(String name) {
        return new LogicVariable(EMPTY, AST_VARIABLE, ValueMutability.MUTABLE, name, name);
    }

    public static LogicVariable fnRetVal(String functionName, String functionPrefix) {
        return new LogicVariable(EMPTY, FUNCTION_RETVAL,
                functionName, functionPrefix, functionPrefix + RETURN_VALUE,
                functionPrefix + RETURN_VALUE, false, false, true, false, false);
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
    public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
        creator.createSet(this, value);
    }

    @Override
    public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        valueSetter.accept(this);
    }

    public LogicVariable withType(ArgumentType argumentType) {
        return new LogicVariable(sourcePosition, argumentType, mutability, functionPrefix, name, fullName, mlog,
                isVolatile, noinit, input, output, reference);
    }
}
