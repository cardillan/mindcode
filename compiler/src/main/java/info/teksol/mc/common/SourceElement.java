package info.teksol.mc.common;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SourceElement {

    /// Provides the input position of the source element corresponding to this object.
    /// When the object doesn't have a corresponding source element, it should return
    /// `SourcePosition.EMPTY`.
    ///
    /// @return position of the source element corresponding to this object in the source code
    SourcePosition sourcePosition();

}
