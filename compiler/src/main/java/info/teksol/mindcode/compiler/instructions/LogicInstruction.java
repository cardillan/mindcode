package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVariable;

public interface LogicInstruction extends MlogInstruction {

    LogicInstruction copy();

    AstContext getAstContext();

    LogicInstruction withContext(AstContext astContext);

    boolean belongsTo(AstContext astContext);

    AstContext findContextOfType(AstContextType contextType);

    AstContext findTopContextOfType(AstContextType contextType);

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
