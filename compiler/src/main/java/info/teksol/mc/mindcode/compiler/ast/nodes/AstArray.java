package info.teksol.mc.mindcode.compiler.ast.nodes;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface AstArray {

    @Nullable AstIdentifier getProcessor();

    AstIdentifier getArray();
}
