package info.teksol.mindcode;

import java.util.Objects;

public record Tuple2<T1, T2>(T1 _1, T2 _2) {

    public T1 getT1() {
        return _1;
    }

    public T2 getT2() {
        return _2;
    }
}
