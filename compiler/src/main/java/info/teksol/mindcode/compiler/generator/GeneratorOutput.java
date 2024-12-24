package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.v3.AstContext;

import java.util.List;

public record GeneratorOutput(
        CallGraph callGraph,
        List<LogicInstruction> instructions,
        AstContext rootAstContext
) {

}
