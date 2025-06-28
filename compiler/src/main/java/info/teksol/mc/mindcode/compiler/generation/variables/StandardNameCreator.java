package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@NullMarked
public class StandardNameCreator implements NameCreator {
    private final boolean shortFunctionPrefix;
    private final Map<String, AtomicInteger> functionPrefixCounter = new HashMap<>();
    private final Set<String> functionPrefixes = new HashSet<>();
    private int functionIndex = 0;

    private static final String SHORT_FUNCTION_PREFIX = "fn";

    private static final String RETURN_VALUE = "retval";
    private static final String RETURN_ADDRESS = "retaddr";
    private static final String FUNCTION_FINISHED = "finished";

    private static final String GLOBAL_PREFIX = ".";
    private static final String MAIN_PREFIX = ":";
    private static final String LOCAL_PREFIX = ":";
    private static final String FUNCTION_PREFIX = ":";
    private static final String DISTINCT_PREFIX = ".";
    private static final String COMPILER_PREFIX = "*";
    private static final String ARRAY_PREFIX = ".";
    private static final String PROCESSOR_ARRAY_PREFIX = ".";
    private static final String ELEMENT_PREFIX = "*";

    private final String globalPrefix;
    private final String mainPrefix;
    private final String localPrefix;
    private final String functionPrefix;
    private final String distinctPrefix;
    private final String compilerPrefix;
    private final String arrayPrefix;
    private final String processorArrayPrefix;
    private final String elementPrefix;

    public StandardNameCreator(boolean shortFunctionPrefix) {
        this.shortFunctionPrefix = shortFunctionPrefix;
        globalPrefix = GLOBAL_PREFIX;
        mainPrefix = MAIN_PREFIX;
        localPrefix = LOCAL_PREFIX;
        functionPrefix = FUNCTION_PREFIX;
        distinctPrefix = DISTINCT_PREFIX;
        compilerPrefix = COMPILER_PREFIX;
        arrayPrefix = ARRAY_PREFIX;
        processorArrayPrefix = PROCESSOR_ARRAY_PREFIX;
        elementPrefix = ELEMENT_PREFIX;
    }

    public StandardNameCreator(CompilerProfile compilerProfile) {
        this.shortFunctionPrefix = compilerProfile.isShortFunctionPrefix();
        globalPrefix = GLOBAL_PREFIX;
        mainPrefix = MAIN_PREFIX;
        localPrefix = LOCAL_PREFIX;
        functionPrefix = FUNCTION_PREFIX;
        distinctPrefix = DISTINCT_PREFIX;
        compilerPrefix = COMPILER_PREFIX;
        arrayPrefix = ARRAY_PREFIX;
        processorArrayPrefix = PROCESSOR_ARRAY_PREFIX;
        elementPrefix = ELEMENT_PREFIX;
    }

    public StandardNameCreator() {
        this.shortFunctionPrefix = false;
        globalPrefix = "[salmon]";
        mainPrefix = "[gold]";
        localPrefix = "[gold].";
        functionPrefix = "[green]";
        distinctPrefix = "[violet].";
        compilerPrefix = "[gray]*";
        arrayPrefix = "[salmon]";
        processorArrayPrefix = "[salmon].";
        elementPrefix = "[sky]*";
    }

    @Override
    public MindcodeFunction setupFunctionPrefix(MindcodeFunction function) {
        if (shortFunctionPrefix) {
            function.setPrefixAndIndex(SHORT_FUNCTION_PREFIX + functionIndex++, 0);
        } else {
            AtomicInteger counter = functionPrefixCounter.computeIfAbsent(function.getName(), k -> new AtomicInteger(0));
            function.setPrefixAndIndex(function.getName(), counter.getAndIncrement());
        }
        return function;
    }

    @Override
    public String global(String name) {
        return globalPrefix + name;
    }

    @Override
    public String remote(String name) {
        return GLOBAL_PREFIX + name;
    }

    @Override
    public String main(String variableName, int variableIndex) {
        return withPrefix(mainPrefix, variableName, variableIndex);
    }

    @Override
    public String local(MindcodeFunction function, String variableName, int variableIndex) {
        return withPrefix(functionPrefix, function.getPrefix(), function.getPrefixIndex())
               + withPrefix(localPrefix, variableName, variableIndex);
    }

    @Override
    public String parameter(MindcodeFunction function, String parameterName) {
        return withPrefix(functionPrefix, function.getPrefix(), function.getPrefixIndex())
               + withPrefix(localPrefix, parameterName, 0);
    }

    @Override
    public String remoteParameter(MindcodeFunction function, String parameterName) {
        return withPrefix(FUNCTION_PREFIX, function.getPrefix(), function.getPrefixIndex())
               + withPrefix(LOCAL_PREFIX, parameterName, 0);
    }

    @Override
    public String retval(MindcodeFunction function) {
        return function.isRemote()
                ?  withPrefix(FUNCTION_PREFIX, function.getPrefix(), function.getPrefixIndex()) + COMPILER_PREFIX + RETURN_VALUE
                :  withPrefix(functionPrefix, function.getPrefix(), function.getPrefixIndex()) + compilerPrefix + RETURN_VALUE;
    }

    @Override
    public String retaddr(MindcodeFunction function) {
        return function.isRemote()
                ?  withPrefix(FUNCTION_PREFIX, function.getPrefix(), function.getPrefixIndex()) + COMPILER_PREFIX + RETURN_ADDRESS
                :  withPrefix(functionPrefix, function.getPrefix(), function.getPrefixIndex()) + compilerPrefix + RETURN_ADDRESS;
    }

    @Override
    public String finished(MindcodeFunction function) {
        // Note: always remote
        return function.isRemote()
                ?  withPrefix(FUNCTION_PREFIX, function.getPrefix(), function.getPrefixIndex()) + COMPILER_PREFIX + FUNCTION_FINISHED
                :  withPrefix(functionPrefix, function.getPrefix(), function.getPrefixIndex()) + compilerPrefix + FUNCTION_FINISHED;
    }

    @Override
    public String arrayBase(String processorName, String arrayName) {
        return processorName.isEmpty()
                ? arrayPrefix + arrayName
                : globalPrefix + processorName + processorArrayPrefix + arrayName;
    }

    @Override
    public String arrayElement(String arrayName, int index) {
        return arrayPrefix + arrayName + elementPrefix + index;
    }

    @Override
    public String remoteArrayElement(String arrayName, int index) {
        return ARRAY_PREFIX + arrayName + ELEMENT_PREFIX + index;
    }

    @Override
    public String arrayAccess(String baseName, String suffix) {
        return baseName + compilerPrefix + suffix;
    }

    @Override
    public String temp(int index) {
        return compilerPrefix + "tmp" + index;
    }

    @Override
    public String stackPointer() {
        return compilerPrefix + "sp";
    }

    @Override
    public String remoteSignature() {
        return COMPILER_PREFIX + "signature";
    }

    private String withPrefix(String prefix, String name, int index) {
        return index == 0 ? prefix + name : prefix + name + distinctPrefix + index;
    }
}
