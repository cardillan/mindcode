package info.teksol.mc.mindcode.logic.arguments;

import org.jspecify.annotations.NullMarked;

/// Represents a generic argument created outside the usual compiler context (e.g., in a test code, or when generating
/// sample instructions). Compiler/optimizer is unable to handle generic arguments.
@NullMarked
public class GenericArgument extends AbstractArgument {
    private final String mlog;

    public GenericArgument(String mlog) {
        super(ArgumentType.UNSPECIFIED, ValueMutability.MUTABLE);
        this.mlog = mlog;
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    @Override
    public String toString() {
        return "BaseArgument{" +
                "mlog='" + mlog + '\'' +
                '}';
    }
}
