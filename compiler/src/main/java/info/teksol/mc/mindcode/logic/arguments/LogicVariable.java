package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionParameter;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;

import java.util.Objects;
import java.util.function.Consumer;

public class LogicVariable extends AbstractArgument implements LogicValue, LogicAddress {
    // Looks like a variable, but translates to 0 in mlog.
    private static final LogicVariable UNUSED_VARIABLE = new LogicVariable(ArgumentType.TMP_VARIABLE, "0");

    public static final LogicVariable STACK_POINTER = LogicVariable.special("__sp");
    public static final LogicVariable INVALID = LogicVariable.special("invalid");
    private static final String RETURN_VALUE = "retval";
    private static final String RETURN_ADDRESS = "retaddr";

    protected final String functionPrefix;
    protected final String name;
    protected final String fullName;
    protected final boolean volatileVar;
    protected final boolean input;
    protected final boolean output;

    private LogicVariable(ArgumentType argumentType, String functionPrefix, String name, String fullName,
            boolean volatileVar, boolean input, boolean output) {
        super(argumentType);
        this.functionPrefix = functionPrefix;
        this.name = name;
        this.fullName = fullName;
        this.volatileVar = volatileVar;
        this.input = input;
        this.output = output;
    }

    protected LogicVariable(ArgumentType argumentType, String name, boolean volatileVar, boolean input, boolean output) {
        super(argumentType);
        this.functionPrefix = null;
        this.name = Objects.requireNonNull(name);
        this.fullName = name;
        this.volatileVar = volatileVar;
        this.input = input;
        this.output = output;
    }

    private LogicVariable(ArgumentType argumentType, String name) {
        this(argumentType, name, false, false, false);
    }

    private LogicVariable(ArgumentType argumentType, String functionName, String functionPrefix, String name,
            boolean input, boolean output) {
        super(argumentType);
        this.functionPrefix = Objects.requireNonNull(functionPrefix);
        if (functionPrefix.isEmpty()) {
            throw new IllegalArgumentException("functionPrefix must not be empty");
        }
        if (Objects.requireNonNull(functionName).isEmpty()) {
            throw new IllegalStateException("Empty function name.");
        }
        this.name = Objects.requireNonNull(name);
        this.fullName = functionName + "." + name;
        this.volatileVar = false;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean canEvaluate() {
        return true;
    }

    @Override
    public boolean isConstant() {
        return getType() == ArgumentType.BLOCK;
    }

    @Override
    public String format(InstructionProcessor instructionProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUserVariable() {
        return getType() == ArgumentType.PARAMETER || getType() == ArgumentType.GLOBAL_VARIABLE || getType() == ArgumentType.LOCAL_VARIABLE;
    }

    @Override
    public boolean isUserWritable() {
        return getType() == ArgumentType.GLOBAL_VARIABLE || getType() == ArgumentType.LOCAL_VARIABLE;
    }

    @Override
    public boolean isGlobalVariable() {
        return getType() == ArgumentType.PARAMETER || getType() == ArgumentType.GLOBAL_VARIABLE;
    }

    @Override
    public boolean isMainVariable() {
        return getType() == ArgumentType.LOCAL_VARIABLE && functionPrefix == null;
    }

    @Override
    public boolean isTemporaryVariable() {
        return getType() == ArgumentType.TMP_VARIABLE;
    }

    @Override
    public boolean isLocalVariable() {
        return getType() == ArgumentType.LOCAL_VARIABLE && functionPrefix != null;
    }

    @Override
    public boolean isCompilerVariable() {
        return getType() == ArgumentType.COMPILER;
    }

    public String getName() {
        return name;
    }

    public String getFunctionPrefix() {
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
        return functionPrefix != null && argumentType != ArgumentType.FUNCTION_RETVAL ? functionPrefix + "_" + name : name;
    }

    @Override
    public String toString() {
        return "LogicVariable{" +
                "argumentType=" + argumentType +
                ", fullName='" + fullName + '\'' +
                ", functionPrefix='" + functionPrefix + '\'' +
                ", volatileVar=" + volatileVar +
                ", input=" + input +
                ", output=" + output +
                '}';
    }

    public static LogicVariable block(String name) {
        return new LogicVariable(ArgumentType.BLOCK, name);
    }

    public static LogicVariable global(String name, boolean volatileVar) {
        return new LogicVariable(ArgumentType.GLOBAL_VARIABLE, name, volatileVar, false, false);
    }

    @SuppressWarnings("ConfusingMainMethod")
    public static LogicVariable main(String name) {
        return new LogicVariable(ArgumentType.LOCAL_VARIABLE, name);
    }

    public static LogicVariable local(String functionName, String functionPrefix, String name) {
        return new LogicVariable(ArgumentType.LOCAL_VARIABLE, functionName, functionPrefix, name, false, false);
    }

    public static LogicVariable local(String functionName, String functionPrefix, String name, boolean input, boolean output) {
        return new LogicVariable(ArgumentType.LOCAL_VARIABLE, functionName, functionPrefix, name, input, output);
    }

    public static LogicVariable local(String functionName, String functionPrefix, AstFunctionParameter parameter) {
        // A variable without an out modifier is input by default
        return new LogicVariable(ArgumentType.LOCAL_VARIABLE, functionName, functionPrefix, parameter.getName(),
                parameter.isInput(), parameter.isOutput());
    }

    public static LogicVariable temporary(String name) {
        return new LogicVariable(ArgumentType.TMP_VARIABLE, name);
    }

    public static LogicVariable ast(String name) {
        return new LogicVariable(ArgumentType.AST_VARIABLE, name);
    }

    public static LogicVariable fnRetVal(MindcodeFunction function) {
        return new LogicVariable(ArgumentType.FUNCTION_RETVAL, function.getName(),
                function.getPrefix(), function.getPrefix() + RETURN_VALUE, false, true);
    }

    public static LogicVariable fnRetVal(String functionName, String functionPrefix) {
        return new LogicVariable(ArgumentType.FUNCTION_RETVAL, functionName, functionPrefix,
                functionPrefix + RETURN_VALUE, false, true);
    }

    public static LogicVariable fnRetAddr(String functionPrefix) {
        return new LogicVariable(ArgumentType.FUNCTION_RETADDR, functionPrefix + RETURN_ADDRESS);
    }

    public static LogicVariable special(String name) {
        return new LogicVariable(ArgumentType.COMPILER, name);
    }

    /**
     * Return the variable passed as an argument to unused instruction parameters.
     * @return variable for unused instruction parameters
     */
    public static LogicVariable unusedVariable() {
        return UNUSED_VARIABLE;
    }

    @Override
    public boolean isVolatile() {
        return volatileVar;
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
        return new LogicVariable(argumentType, functionPrefix, name, fullName, volatileVar, input, output);
    }
}