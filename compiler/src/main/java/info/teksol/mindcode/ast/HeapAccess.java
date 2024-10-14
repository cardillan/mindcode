package info.teksol.mindcode.ast;


import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;

import java.util.Objects;

public class HeapAccess extends BaseAstNode {
    private final String cellName;
    private final AstNode address;
    private final boolean absolute;

    private HeapAccess(InputPosition inputPosition, String cellName, AstNode address, boolean absolute) {
        super(inputPosition, address);
        this.cellName = cellName;
        this.address = address;
        this.absolute = absolute;
    }

    public HeapAccess(InputPosition inputPosition, String cellName, AstNode address) {
        this(inputPosition, cellName, address, true);
    }

    public HeapAccess(InputPosition inputPosition, String cellName, int index) {
        this(inputPosition, cellName, new NumericLiteral(inputPosition, index), false);
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
