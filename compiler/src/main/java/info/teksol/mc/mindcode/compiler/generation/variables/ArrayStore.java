package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicNull;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public abstract class ArrayStore implements ValueStore {
    protected final SourcePosition sourcePosition;
    protected final String name;
    protected final List<ValueStore> elements;

    public ArrayStore(AstIdentifier arrayIdentifier, List<ValueStore> elements) {
        this.sourcePosition = arrayIdentifier.sourcePosition();
        this.name = arrayIdentifier.getName();
        this.elements = elements;
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return elements.size();
    }

    public List<ValueStore> getElements() {
        return elements;
    }

    @Override
    public final boolean isComplex() {
        // This class can never be used in a context where isComplex matters.
        throw new MindcodeInternalError("Unsupported for FormattableContent.");
    }

    @Override
    public final boolean isLvalue() {
        return false;
    }

    @Override
    public final LogicValue getValue(CodeAssembler assembler) {
        assembler.error(sourcePosition, ERR.ARRAY_FORBIDDEN);
        return LogicNull.NULL;
    }

    @Override
    public final void setValue(CodeAssembler assembler, LogicValue value) {
        throw new MindcodeInternalError("Unsupported for FormattableContent.");
    }

    @Override
    public final void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        throw new MindcodeInternalError("Unsupported for FormattableContent.");
    }
}
