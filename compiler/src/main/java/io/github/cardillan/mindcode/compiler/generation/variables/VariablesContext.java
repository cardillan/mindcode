package io.github.cardillan.mindcode.compiler.generation.variables;

import io.github.cardillan.mindcode.compiler.MessageContext;
import io.github.cardillan.mindcode.logic.instructions.InstructionProcessor;
import io.github.cardillan.mindcode.logic.mimex.MindustryMetadata;
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
