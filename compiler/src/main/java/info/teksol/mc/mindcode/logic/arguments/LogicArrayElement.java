package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.GLOBAL_VARIABLE;

@NullMarked
public class LogicArrayElement extends LogicVariable {
    private @Nullable String elementName;

    protected LogicArrayElement(SourcePosition sourcePosition, ArgumentType argumentType, String name, String mlog,
            boolean isVolatile) {
        super(sourcePosition, argumentType, name, mlog, isVolatile, true, false);
    }

    public static LogicArrayElement arrayElement(AstIdentifier identifier, int index, String mlog, boolean isVolatile) {
        return new LogicArrayElement(identifier.sourcePosition(), GLOBAL_VARIABLE,
                identifier.getName() + "[" + index + "]", mlog, isVolatile);
    }

    @Override
    public String toMlog() {
        return Objects.requireNonNullElse(elementName, mlog);
    }

    @Override
    public void setArrayElementName(String elementName) {
        this.elementName = elementName;
    }
}
