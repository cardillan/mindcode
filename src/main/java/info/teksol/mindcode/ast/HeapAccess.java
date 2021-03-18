package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

import java.util.Objects;

public class HeapAccess implements AstNode {
    private final String cellName;
    private final AstNode address;

    public HeapAccess(String cellName, AstNode address) {
        if (RESERVED_KEYWORDS.contains(cellName)) {
            throw new ParsingException(cellName + " is a reserved keyword, please use a different word");
        }

        this.cellName = cellName;
        this.address = address;
    }

    public String getCellName() {
        return cellName;
    }

    public AstNode getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeapAccess HeapAccess = (HeapAccess) o;
        return Objects.equals(cellName, HeapAccess.cellName) &&
                Objects.equals(address, HeapAccess.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellName, address);
    }

    @Override
    public String toString() {
        return "HeapAccess{" +
                "cellName='" + cellName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
