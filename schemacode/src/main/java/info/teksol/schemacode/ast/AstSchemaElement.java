package info.teksol.schemacode.ast;

import org.jspecify.annotations.NullMarked;

/// Represents a placeable schematic element, which can be:
/// - Block
/// - Inlined region
/// - Reference to a region
@NullMarked
public interface AstSchemaElement extends AstSchemaItem {
}
