package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionParameter;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.logic.arguments.LogicString;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface NameCreator extends ArrayNameCreator {
    MindcodeFunction setupFunctionPrefix(MindcodeFunction function);

    String global(String name);
    String main(String variableName, int variableIndex);
    String local(MindcodeFunction function, String variableName, int variableIndex);
    String parameter(MindcodeFunction function, String parameterName);
    String retval(MindcodeFunction function);
    String retaddr(MindcodeFunction function);
    String finished(MindcodeFunction function);

    String arrayAccess(String baseName, String suffix);

    String remote(String name);
    String remoteParameter(MindcodeFunction function, String parameterName);

    String temp(int index);
    String stackPointer();
    String remoteSignature();

    default LogicString global(AstIdentifier identifier) {
        return LogicString.create(identifier.sourcePosition(), global(identifier.getName()));
    }

    default LogicString remote(AstIdentifier identifier) {
        return LogicString.create(identifier.sourcePosition(), remote(identifier.getName()));
    }

    default LogicString remoteParameter(MindcodeFunction function, AstFunctionParameter parameter) {
        return LogicString.create(parameter.sourcePosition(), remoteParameter(function, parameter.getName()));
    }
}
