package io.github.cardillan.mindcode.compiler.generation.variables;

import io.github.cardillan.mindcode.compiler.ast.nodes.AstFunctionParameter;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstIdentifier;
import io.github.cardillan.mindcode.compiler.callgraph.MindcodeFunction;
import io.github.cardillan.mindcode.logic.arguments.LogicString;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface NameCreator extends ArrayNameCreator {
    void setupFunctionPrefix(MindcodeFunction function);

    String global(String name);
    String main(String variableName, int variableIndex);
    String local(MindcodeFunction function, String variableName, int variableIndex);
    String parameter(MindcodeFunction function, String parameterName);
    String retval(MindcodeFunction function);
    String retaddr(MindcodeFunction function);
    String stackFrame(MindcodeFunction function);
    String finished(MindcodeFunction function);

    String arrayAccess(String baseName, String suffix);

    String remote(String name);
    String remoteParameter(MindcodeFunction function, String parameterName);

    String temp(int index);
    String stackFrameSuffix(int frameIndex);
    String stackPointer();
    String stackMemory();
    String remoteSignature();
    String programId();

    default LogicString global(AstIdentifier identifier) {
        return LogicString.createRaw(identifier.sourcePosition(), global(identifier.getName()));
    }

    default LogicString remote(AstIdentifier identifier) {
        return LogicString.createRaw(identifier.sourcePosition(), remote(identifier.getName()));
    }

    default LogicString remoteParameter(MindcodeFunction function, AstFunctionParameter parameter) {
        return LogicString.createRaw(parameter.sourcePosition(), remoteParameter(function, parameter.getName()));
    }
}
