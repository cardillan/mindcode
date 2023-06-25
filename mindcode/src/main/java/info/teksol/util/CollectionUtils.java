package info.teksol.util;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectionUtils {

    @SafeVarargs
    public static <T> Predicate<T> in(T... values) {
        return Set.of(values)::contains;
    }

    public static <T, R> Predicate<T> in(Function<T, R> mapping,  R... values) {
        return t -> Set.of(values).contains(mapping.apply(t));
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
