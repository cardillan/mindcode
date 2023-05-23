package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.compiler.instructions.AstContext;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.List;

public record GeneratorOutput(
        List<LogicInstruction> instructions,
        AstContext rootAstContext
) {

}
