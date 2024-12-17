package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.List;

public record CodeGeneratorOutput(List<LogicInstruction> instructions, AstContext rootAstContext) {
}
