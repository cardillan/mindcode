package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

@NullMarked
public class IdentifierFunctionArgument implements FunctionArgument {
    protected final Supplier<ValueStore> valueSupplier;
    protected final AstIdentifier identifier;
    protected final boolean refModifier;
    protected @Nullable ValueStore value;

    public IdentifierFunctionArgument(Supplier<ValueStore> valueSupplier, AstIdentifier identifier, boolean refModifier) {
        this.valueSupplier = valueSupplier;
        this.identifier = identifier;
        this.refModifier = refModifier;
    }

    @Override
    public SourcePosition sourcePosition() {
        return identifier.sourcePosition();
    }

    public AstIdentifier getIdentifier() {
        return identifier;
    }

    public LogicKeyword getKeyword() {
        return LogicKeyword.create(identifier.sourcePosition(), identifier.getName());
    }

    /// Provides the value of the underlying argument
    @Override
    public ValueStore unwrap() {
        if (value == null) {
            value = valueSupplier.get();
        }
        return value.unwrap();
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public boolean hasInModifier() {
        return false;
    }

    @Override
    public boolean hasOutModifier() {
        return false;
    }

    @Override
    public boolean hasRefModifier() {
        return refModifier;
    }

    @Override
    public boolean isInput() {
        return true;
    }

    @Override
    public boolean isOutput() {
        return false;
    }

    @Override
    public boolean isComplex() {
        return unwrap().isComplex();
    }

    @Override
    public boolean isLvalue() {
        return unwrap().isLvalue();
    }

    @Override
    public LogicValue getValue(ContextfulInstructionCreator creator) {
        return unwrap().getValue(creator);
    }

    @Override
    public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
        unwrap().readValue(creator, target);
    }

    @Override
    public void setValue(ContextfulInstructionCreator creator, LogicValue newValue) {
        creator.setInternalError();
    }

    @Override
    public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        creator.setInternalError();
    }

    @Override
    public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
        creator.setInternalError();
        return LogicVariable.INVALID;
    }

    @Override
    public void storeValue(ContextfulInstructionCreator creator) {
        creator.setInternalError();
    }
}
