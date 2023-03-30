package info.teksol.mindcode.ast;


import java.util.Objects;

public class HeapAccess extends BaseAstNode {
    private final String cellName;
    private final AstNode address;
    private final boolean absolute;

    private HeapAccess(String cellName, AstNode address, boolean absolute) {
        super(address);
        this.cellName = cellName;
        this.address = address;
        this.absolute = absolute;
    }

    public HeapAccess(String cellName, AstNode address) {
        this(cellName, address, true);
    }

    public HeapAccess(String cellName, int index) {
        this(cellName, new NumericLiteral(index), false);
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
}
