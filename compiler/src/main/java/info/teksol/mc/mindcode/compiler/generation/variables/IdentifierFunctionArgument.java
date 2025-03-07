package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

@NullMarked
public class IdentifierFunctionArgument implements FunctionArgument {
    protected final Supplier<ValueStore> valueSupplier;
    protected final AstIdentifier identifier;
    protected @Nullable ValueStore value;

    public IdentifierFunctionArgument(Supplier<ValueStore> valueSupplier, AstIdentifier identifier) {
        this.valueSupplier = valueSupplier;
        this.identifier = identifier;
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
        return value;
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
    public LogicValue getValue(CodeAssembler assembler) {
        return unwrap().getValue(assembler);
    }

    @Override
    public void readValue(CodeAssembler assembler, LogicVariable target) {
        unwrap().readValue(assembler, target);
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue newValue) {
        assembler.setInternalError();
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        assembler.setInternalError();
    }

    @Override
    public LogicValue getWriteVariable(CodeAssembler assembler) {
        assembler.setInternalError();
        return LogicVariable.INVALID;
    }

    @Override
    public void storeValue(CodeAssembler assembler) {
        assembler.setInternalError();
    }
}
