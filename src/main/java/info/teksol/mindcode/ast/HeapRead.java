package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

import java.util.Objects;

public class HeapRead implements AstNode {
    private final String cellName;
    private final AstNode address;

    public HeapRead(String cellName, AstNode address) {
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
        HeapRead heapRead = (HeapRead) o;
        return Objects.equals(cellName, heapRead.cellName) &&
                Objects.equals(address, heapRead.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellName, address);
    }

    @Override
    public String toString() {
        return "HeapRead{" +
                "cellName='" + cellName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
