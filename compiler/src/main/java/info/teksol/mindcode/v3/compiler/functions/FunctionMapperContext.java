package info.teksol.mindcode.v3.compiler.functions;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.v3.CompilerContext;
import info.teksol.mindcode.v3.compiler.generation.CodeAssembler;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface FunctionMapperContext extends CompilerContext {
    InstructionProcessor instructionProcessor();
    CodeAssembler assembler();
}
