package info.teksol.util;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectionUtils {

    @SafeVarargs
    public static <T> Predicate<T> in(T... values) {
        return Set.of(values)::contains;
    }

    public static <T> Predicate<T> in(T value) {
        return t -> Objects.equals(t, value);
    }

    public static <T> Predicate<T> in(T v1, T v2) {
        return t -> {
            if (t == null) {
                return v1 == null || v2 == null;
            } else {
                return t.equals(v1) || t.equals(v2);
            }
        };
    }

    public static <T> Predicate<T> in(T v1, T v2, T v3) {
        return t -> {
            if (t == null) {
                return v1 == null || v2 == null || v3 == null;
            } else {
                return t.equals(v1) || t.equals(v2) || t.equals(v3);
            }
        };
    }

    @SafeVarargs
    public static <T, R> Predicate<T> resultIn(Function<T, R> mapping,  R... values) {
        return t -> in(values).test(mapping.apply(t));
    }

    public static <T, R> Predicate<T> resultIn(Function<T, R> mapping,  R value) {
        return t -> in(value).test(mapping.apply(t));
    }

    public static <T, R> Predicate<T> resultIn(Function<T, R> mapping,  R v1, R v2) {
        return t -> in(v1, v2).test(mapping.apply(t));
    }

    public static <T, R> Predicate<T> resultIn(Function<T, R> mapping,  R v1, R v2, R v3) {
        return t -> in(v1, v2, v3).test(mapping.apply(t));
    }

    @SafeVarargs
    public static <T> Predicate<T> notIn(T... values) {
        return Predicate.not(Set.of(values)::contains);
    }

    public static <E> int findFirstIndex(List<? extends E> list, int startIndex, Predicate<E> matcher) {
        if (startIndex < 0 || startIndex > list.size()) {
            throw new IndexOutOfBoundsException(startIndex);
        }
        for (int i = startIndex; i < list.size(); i++) {
            if (matcher.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static <E> int findLastIndex(List<? extends E> list, int startIndex, Predicate<E> matcher) {
        if (startIndex < 0 || startIndex > list.size()) {
            throw new IndexOutOfBoundsException(startIndex);
        }
        for (int i = startIndex; i >= 0; i--) {
            if (matcher.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static <E> int findFirstIndex(List<? extends E> list, Predicate<E> matcher) {
        return findFirstIndex(list, 0, matcher);
    }

    public static <E> int findLastIndex(List<? extends E> list, Predicate<E> matcher) {
        return findLastIndex(list, list.size() - 1, matcher);
    }

    public static <E> E removeFirstMatching(List<? extends E> list, Predicate<E> matcher) {
        int index = findFirstIndex(list, matcher);
        return index < 0 ? null : list.remove(index);
    }
}
