package info.teksol.mindcode.logic;

import javax.swing.table.DefaultTableCellRenderer;

public interface LogicValue extends LogicArgument {

    default boolean isFormattable() {
        return false;
    }
}
