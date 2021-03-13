package info.teksol.mindcode;

import java.util.Objects;

public class HeapWrite implements AstNode {
    private final String cellName;
    private final String address;
    private final AstNode value;

    public HeapWrite(String cellName, String address, AstNode value) {
        this.cellName = cellName;
        this.address = address;
        this.value = value;
    }

    public String getCellName() {
        return cellName;
    }

    public String getAddress() {
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
