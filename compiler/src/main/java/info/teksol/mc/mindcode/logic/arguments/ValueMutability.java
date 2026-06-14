package info.teksol.mc.mindcode.logic.arguments;

import org.jspecify.annotations.NullMarked;

/// Defines the character of the value as seen by the compiler.
@NullMarked
public enum ValueMutability {

    /// Represents a value, which is a compile-time constant and can be compile-time evaluated. In most cases, these
    /// would be numeric/color/boolean/string/null literals. Defensive copies of these values need not be made.
    ///
    /// Some built-ins could be handled as compile-time constants as well, for example `@pi`, `@e` or `@ctrlProcessor`.
    CONSTANT,

    /// Represents a value which is immutable, i.e., it is known the value doesn't change during program
    /// execution, but the actual value isn't known and thus can't be compile-time evaluated. The guarantee of
    /// stability means defensive copies of these values need not be made.
    ///
    /// Examples of values which are immutable:
    /// - a program parameter,
    /// - most built-in variables. This includes almost all built-in variables, such as `@this`,
    ///   `@thisx`, `@coal` and so on. Even `LAccess` built-ins (such as `@dead` or `@controlled`) are runtime
    ///    constants - of course, the values returned by `op sensor` for them aren't,
    /// - logic keywords
    IMMUTABLE,

    /// Represents a value which is not run-time constant but only changes through an explicit action of the program.
    /// These are regular variables. In essence, everything that doesn't fall into any of the other categories ends
    /// up here.
    MUTABLE,

    /// Represents a value which can change independently of the program/processor. This includes:
    /// - some built-ins, such as `@time` or `@links`
    /// - variables explicitly declared as volatile
    /// - linked blocks (in some future version)
    ///
    /// Note: `@unit` is regarded as volatile, because its value is changed as a side effect of `ubind`, and this.
    /// Information is currently not available through instruction metadata. It would make sense to implement this
    /// knowledge into the compiler in some way.
    ///
    /// Values stored in memory blocks are also volatile by design, and Mindcode currently handles them as such,
    /// except cached external variables. A mechanism for better control of external storage handling is in the
    /// wish list.
    ///
    /// By their nature, linked blocks are volatile, as they may change due to blocks being unlinked or
    /// destroyed.
    VOLATILE,
}
