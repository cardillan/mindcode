package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.IntRange;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVariable;

import java.util.List;
import java.util.Set;

public interface LogicFunction {
    String format();

    Set<LogicFunction> getDirectCalls();

    Set<LogicFunction> getIndirectCalls();

    InputPosition getInputPosition();

    LogicLabel getLabel();

    String getName();

    LogicVariable getParameter(int index);

    IntRange getParameterCount();

    List<LogicVariable> getParameters();

    String getPrefix();

    int getStandardParameterCount();

    int getUseCount();

    boolean isInline();

    boolean isInputFunctionParameter(LogicVariable variable);

    boolean isMain();

    boolean isNoinline();

    boolean isNotOutputFunctionParameter(LogicVariable variable);

    boolean isOutputFunctionParameter(LogicVariable variable);

    boolean isRecursive();

    boolean isUsed();

    boolean isVoid();

    // Increase usage count of this function
    void markUsage(int count);

    void setInlined();

    void setLabel(LogicLabel label);

    void setPrefix(String prefix);
}
