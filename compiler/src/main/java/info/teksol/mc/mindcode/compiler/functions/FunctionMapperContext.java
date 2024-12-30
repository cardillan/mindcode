package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.compiler.CompilerContext;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface FunctionMapperContext extends CompilerContext {
    InstructionProcessor instructionProcessor();
    CodeAssembler assembler();
}
