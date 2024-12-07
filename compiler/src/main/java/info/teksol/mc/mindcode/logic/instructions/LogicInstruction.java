package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;

public interface LogicInstruction extends MlogInstruction {

    LogicInstruction copy();

    AstContext getAstContext();

    LogicInstruction withContext(AstContext astContext);

    boolean belongsTo(AstContext astContext);

    AstContext findContextOfType(AstContextType contextType);

    AstContext findTopContextOfType(AstContextType contextType);

    default boolean affectsControlFlow() {
        return false;
    }

    default LogicVariable getResult() {
        return null;
    }

    default LogicLabel getMarker() {
        return null;
    }

    default boolean endsCodePath() {
        return false;
    }
}
