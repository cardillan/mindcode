package info.teksol.mc.mindcode.compiler.optimization.cases;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum SegmentType {
    /// A segment containing one distinct value
    /// When the distinct value is an `else` value, represents an empty segment
    SINGLE,

    // A merged segment containing one predominant value and a few possible exceptions
    MIXED,

    /// A segment containing multiple distinct values, produces a jump table
    JUMP_TABLE
}
