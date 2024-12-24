package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.MessageConsumer;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class StackTracker extends AbstractMessageEmitter {
    private LogicVariable stackMemory = LogicVariable.INVALID;

    public StackTracker(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

    public boolean isValid() {
        return stackMemory != LogicVariable.INVALID;
    }

    public void setStackMemory(LogicVariable stackMemory) {
        this.stackMemory = Objects.requireNonNull(stackMemory);
    }

    public LogicVariable getStackMemory(AstMindcodeNode node) {
//        if (stackMemory == LogicVariable.INVALID) {
//            error(node, "Stack needs to be allocated for recursive functions.");
//        }
        return stackMemory;
    }
}
