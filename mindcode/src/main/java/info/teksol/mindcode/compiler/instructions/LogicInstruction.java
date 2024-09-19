package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.logic.*;

import java.util.List;
import java.util.stream.Stream;

public interface LogicInstruction {

    LogicInstruction copy();

    Opcode getOpcode();

    List<LogicArgument> getArgs();

    LogicArgument getArg(int index);

    List<InstructionParameterType> getParams();

    InstructionParameterType getParam(int index);

    List<ParameterAssignment> getAssignments();

    Stream<ParameterAssignment> assignmentsStream();

    /** @return number of input parameters */
    int getInputs();

    /** @return number of output parameters */
    int getOutputs();

    /** @return stream of arguments assigned to input parameters */
    Stream<LogicArgument> inputArgumentsStream();

    /** @return stream of arguments assigned to output parameters */
    Stream<LogicArgument> outputArgumentsStream();

    /** @return stream of arguments assigned to input or output parameters */
    Stream<LogicArgument> inputOutputArgumentsStream();

    AstContext getAstContext();

    LogicInstruction withContext(AstContext astContext);

    boolean belongsTo(AstContext astContext);

    AstContext findContextOfType(AstContextType contextType);

    AstContext findTopContextOfType(AstContextType contextType);

    /**
     * Returns the true size of the instruction. Real instructions have a size of 1, virtual instruction may get
     * resolved to more (or less) real instructions.
     *
     * @return real size of the instruction
     */
    default int getRealSize() {
        return getOpcode().getSize();
    }

    default LogicVariable getResult() {
        return null;
    }

    default LogicLabel getMarker() {
        return null;
    }
}
