package info.teksol.mc.emulator.blocks.graphics;

import org.jspecify.annotations.NullMarked;

/// Represents the transformation matrix. Is shared among displays.
///
/// In Mindustry Logic, the matrix is owned by the processor and applied to whichever display
/// the drawflush is being called. The same needs to be done here.
@NullMarked
public class TransformationMatrix {
}
