package info.teksol.mc.mindcode.compiler.generation.variables;

/// Defines possible scopes of variable registration.
public enum VariableScope {
    /// The registration is valid within the entire function.
    FUNCTION,

    /// The registration is valid within the parent node.
    PARENT_NODE,

    /// The registration is valid within the current node only.
    NODE,
}
