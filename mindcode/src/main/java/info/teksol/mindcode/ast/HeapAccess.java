package info.teksol.mindcode.ast;


import info.teksol.mindcode.compiler.SourceFile;
import info.teksol.mindcode.compiler.generator.AstContextType;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class HeapAccess extends BaseAstNode {
    private final String cellName;
    private final AstNode address;
    private final boolean absolute;

    private HeapAccess(Token startToken, SourceFile sourceFile, String cellName, AstNode address, boolean absolute) {
        super(startToken, sourceFile, address);
        this.cellName = cellName;
        this.address = address;
        this.absolute = absolute;
    }

    public HeapAccess(Token startToken, SourceFile sourceFile, String cellName, AstNode address) {
        this(startToken, sourceFile, cellName, address, true);
    }

    public HeapAccess(Token startToken, SourceFile sourceFile, String cellName, int index) {
        this(startToken, sourceFile, cellName, new NumericLiteral(startToken, sourceFile, index), false);
    }

    public String getCellName() {
        return cellName;
    }

    public AstNode getAddress() {
        return address;
    }

    public boolean isAbsolute() {
        return absolute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeapAccess that = (HeapAccess) o;
        return absolute == that.absolute &&
                Objects.equals(cellName, that.cellName) &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellName, address, absolute);
    }

    @Override
    public String toString() {
        return "HeapAccess{" +
                "cellName='" + cellName + '\'' +
                ", address=" + address + 
                ", absolute=" + absolute +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.HEAP_ACCESS;
    }
}
