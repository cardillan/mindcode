package info.teksol.mindcode.logic;

import java.util.Objects;

public class LogicVariable extends AbstractArgument implements LogicValue, LogicAddress {
    public static final LogicVariable STACK_POINTER = LogicVariable.special("__sp");
    private static final String RETURN_VALUE = "retval";
    private static final String RETURN_ADDRESS = "retaddr";

    protected final String functionPrefix;
    protected final String name;
    protected final String fullName;

    protected LogicVariable(ArgumentType argumentType, String name) {
        super(argumentType);
        this.functionPrefix = null;
        this.name = Objects.requireNonNull(name);
        this.fullName = name;
    }

    private LogicVariable(ArgumentType argumentType, String functionPrefix, String name) {
        super(argumentType);
        this.functionPrefix = Objects.requireNonNull(functionPrefix);
        this.name = Objects.requireNonNull(name);
        this.fullName = name;
    }

    private LogicVariable(ArgumentType argumentType, String functionName, String functionPrefix, String name) {
        super(argumentType);
        this.functionPrefix = Objects.requireNonNull(functionPrefix);
        if (functionPrefix.isEmpty()) {
            throw new IllegalArgumentException("functionPrefix must not be empty");
        }
        this.name = Objects.requireNonNull(name);
        this.fullName = Objects.requireNonNull(functionName) + "." + name;
        if (fullName.startsWith(".")) {
            throw new IllegalStateException("Empty function name.");
        }
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String format() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUserVariable() {
        return getType() == ArgumentType.PARAMETER || getType() == ArgumentType.GLOBAL_VARIABLE || getType() == ArgumentType.LOCAL_VARIABLE;
    }

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
    public boolean isFunctionVariable() {
        return getType() == ArgumentType.LOCAL_VARIABLE && functionPrefix != null;
    }

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
    public String toMlog() {
        return functionPrefix != null && argumentType != ArgumentType.FUNCTION_RETVAL ? functionPrefix + "_" + name : name;
    }

    @Override
    public String toString() {
        return "LogicVariable{" +
                "argumentType=" + argumentType +
                ", fullName='" + fullName + '\'' +
                ", functionPrefix='" + functionPrefix + '\'' +
                '}';
    }

    public static LogicVariable block(String name) {
        return new LogicVariable(ArgumentType.BLOCK, name);
    }

    public static LogicVariable global(String name) {
        return new LogicVariable(ArgumentType.GLOBAL_VARIABLE, name);
    }

    @SuppressWarnings("ConfusingMainMethod")
    public static LogicVariable main(String name) {
        return new LogicVariable(ArgumentType.LOCAL_VARIABLE, name);
    }

    public static LogicVariable local(String functionName, String functionPrefix, String name) {
        return new LogicVariable(ArgumentType.LOCAL_VARIABLE, functionName, functionPrefix, name);
    }

    public static LogicVariable temporary(String name) {
        return new LogicVariable(ArgumentType.TMP_VARIABLE, name);
    }

    public static LogicVariable ast(String name) {
        return new LogicVariable(ArgumentType.AST_VARIABLE, name);
    }

    public static LogicVariable fnRetVal(String functionPrefix) {
        return new LogicVariable(ArgumentType.FUNCTION_RETVAL, functionPrefix, functionPrefix + RETURN_VALUE);
    }

    public static LogicVariable fnRetAddr(String functionPrefix) {
        return new LogicVariable(ArgumentType.FUNCTION_RETADDR, functionPrefix + RETURN_ADDRESS);
    }

    public static LogicVariable special(String name) {
        return new LogicVariable(ArgumentType.COMPILER, name);
    }
}
