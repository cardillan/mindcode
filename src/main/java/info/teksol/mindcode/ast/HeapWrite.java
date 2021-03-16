package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

import java.util.Objects;

public class HeapWrite implements AstNode {
    private final String cellName;
    private final AstNode address;
    private final AstNode value;

    public HeapWrite(String cellName, AstNode address, AstNode value) {
        if (RESERVED_KEYWORDS.contains(cellName)) {
            throw new ParsingException(cellName + " is a reserved keyword, please use a different word");
        }

        this.cellName = cellName;
        this.address = address;
        this.value = value;
    }

    public String getCellName() {
        return cellName;
    }

    public AstNode getAddress() {
        return address;
    }

    public AstNode getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeapWrite heapWrite = (HeapWrite) o;
        return Objects.equals(cellName, heapWrite.cellName) &&
                Objects.equals(address, heapWrite.address) &&
                Objects.equals(value, heapWrite.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellName, address, value);
    }

    @Override
    public String toString() {
        return "HeapWrite{" +
                "cellName='" + cellName + '\'' +
                ", address='" + address + '\'' +
                ", value=" + value +
                '}';
    }
}
