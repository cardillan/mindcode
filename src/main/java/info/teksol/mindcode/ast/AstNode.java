package info.teksol.mindcode.ast;

import java.util.Set;

public interface AstNode {
    Set<String> RESERVED_KEYWORDS = Set.of("STACK", "HEAP");
}
