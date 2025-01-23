package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface LogicInstruction extends MlogInstruction {

    LogicInstruction copy();

    AstContext getAstContext();

    LogicInstruction withContext(AstContext astContext);

    boolean belongsTo(@Nullable AstContext astContext);

    @Nullable AstContext findContextOfType(AstContextType contextType);

    @Nullable AstContext findTopContextOfType(AstContextType contextType);

    default boolean affectsControlFlow() {
        return false;
    }

    default @Nullable LogicVariable getResult() {
        return null;
    }

    default @Nullable LogicLabel getMarker() {
        return null;
    }

    default boolean endsCodePath() {
        return false;
    }
}
