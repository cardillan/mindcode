package info.teksol.mindcode.logic;

public interface LogicValue extends LogicArgument {

    boolean isConstant();

    /**
     * Provides a text representation of the contained value as if printed by Mindustry Logic
     * Is not the same as an mlog representation!
     *
     * @return a text representation of the contained value
     */
    String format();
}
