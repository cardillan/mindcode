package info.teksol.mc.mindcode.logic.arguments;

/// Defines the character of the value as seen by the compiler.
public enum ValueMutability {

    /// Represents a value which is a compile-time constant and can be compile-time evaluated. In most cases, these
    /// would be numeric/color/boolean/string/null literals. Defensive copies of these values need not be made.
    ///
    /// Some built-ins could be handled as compile-time constants as well, for example `@pi`, `@e` or `@ctrlProcessor`
    /// (currently aren't).
    CONSTANT,

    /// Represents a value which is immutable, i.e. it is known the value doesn't change during program
    /// execution, but the actual value isn't known and thus can't be compile-time evaluated. The guarantee of
    /// stability means defensive copies of these values need not be made.
    ///
    /// Examples of values which are runtime constant:
    /// - a parameter,
    /// - built-in variables except the volatile ones. This includes almost all built-in variables, such as `@this`,
    ///   `@thisx`, `@coal` and so on. Even `LAccess` built-ins (such as `@dead` or `@controlled`) are runtime
    ///    constants - of course the values returned by `op sensor` for them aren't,
    /// - logic keywords,
    /// - linked blocks.
    ///
    /// By their nature, linked blocks aren't runtime constants, as they may change due to blocks being unlinked or
    /// destroyed. This would make them volatile. However, since they can't be meaningfully used in expressions
    /// (except the `sensor` instruction), making defensive copies of them is unnecessary. They are therefore
    /// considered immutable. When `sensor` is concerned, though, linked blocks are exempt from immutability and
    /// results of `sensor` invocations on linked blocks aren't reused even for deterministic properties.
    IMMUTABLE,

    /// Represents a value which is not run-time constant, but only changes through an explicit action of the program.
    /// These are regular variables. In essence everything which doesn't fall into any of the other categories ends
    /// up here.
    MUTABLE,

    /// Represents a value which can change independently of the program/processor. This includes:
    /// - some built-ins, such as `@time`, `@links`, `@unit` (!)
    /// - variables explicitly declared as volatile (for use with the `sync` instruction, although this mechanism
    ///   isn't currently wel understood)
    /// - linked blocks (in some future version)
    ///
    /// Note: `@unit` is regarded as volatile, because it's value is changed as a side effect of `ubind`, and this.
    /// information is currently not available through instruction metadata. It would make sense to implement this
    /// knowledge into the compiler in some way.
    ///
    /// Values stored in memory blocks are also volatile by design, and Mindcode currently handles them as such,
    /// except cached external variables. A mechanism for better control of external storage handling is in the
    /// wish list.
    VOLATILE,
}
