package info.teksol.mc.mindcode.compiler;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum DataType {
    // No return type
    VOID    ("void"),

    // General mlog variable
    VAR     ("def"),
    ;

    public final String keyword;

    DataType(String keyword) {
        this.keyword = keyword;
    }
}
