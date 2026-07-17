package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.MessageContext;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public interface VariablesContext extends MessageContext {
    InstructionProcessor instructionProcessor();
    NameCreator nameCreator();
    MindustryMetadata metadata();
    @Nullable Map<String, String> schematicLinks();
}
