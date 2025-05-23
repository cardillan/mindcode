package info.teksol.mc.mindcode.compiler.optimization.cases;

public enum SegmentType {
    /// A segment containing one distinct value
    SINGLE,

    // A merged segment containing one predominant value and a few possible exceptions
    MIXED,

    /// A segment containing multiple distinct values, produces a jump table
    JUMP_TABLE
}
