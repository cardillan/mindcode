package info.teksol.mc.mindcode.logic.arguments;

public class BaseArgument extends AbstractArgument {

    private final String mlog;

    public BaseArgument(String mlog) {
        super(ArgumentType.UNSPECIFIED);
        this.mlog = mlog;
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    @Override
    public String toString() {
        return "BaseArgument{" +
                "mlog='" + mlog + '\'' +
                '}';
    }
}
