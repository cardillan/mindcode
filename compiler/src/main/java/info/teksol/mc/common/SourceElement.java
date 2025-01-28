package info.teksol.mc.common;

import org.jspecify.annotations.NullMarked;

/// An instance which can be tied to a particular position in the source file. Typically, AST tree
/// nodes are instances of source element, but some other classes can also implement this interface
/// to provide the source position.
@NullMarked
public interface SourceElement {

    /// Provides the input position of the source element corresponding to this object.
    /// When the object doesn't have a corresponding source element, it should return
    /// `SourcePosition.EMPTY`.
    ///
    /// @return position of the source element corresponding to this object in the source code
    SourcePosition sourcePosition();

}
