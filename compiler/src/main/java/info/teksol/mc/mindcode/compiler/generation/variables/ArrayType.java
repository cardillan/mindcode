package info.teksol.mc.mindcode.compiler.generation.variables;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum ArrayType {
    // NOTE: the order of elements is important: external arrays first

    /// Residing in a memory cell or memory bank
    EXTERNAL,

    /// Spanning several memory cells or banks
    LARGE,

    /// The array of local linked blocks
    LINKS,

    /// Residing in the current processor.
    /// Even arrays declared `remote` are internal within the current module
    INTERNAL,

    /// Constant array elements, no variables involved
    CONSTANT,

    /// Residing in a remote processor
    REMOTE,

    /// Residing in remote processors, multiplexed
    REMOTE_SHARED,
}
