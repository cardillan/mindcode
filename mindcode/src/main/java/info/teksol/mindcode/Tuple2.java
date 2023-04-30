package info.teksol.mindcode;

public record Tuple2<T1, T2>(T1 _1, T2 _2) {

    public T1 getT1() {
        return _1;
    }

    public T2 getT2() {
        return _2;
    }

    public static <T1, T2> Tuple2<T1, T2> of(T1 _1, T2 _2) {
        return new Tuple2<>(_1, _2);
    }

    public static <T> Tuple2<T, T> ofSame(T _1, T _2) {
        return new Tuple2<>(_1, _2);
    }
}
