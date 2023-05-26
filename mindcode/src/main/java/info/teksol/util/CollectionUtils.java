package info.teksol.util;

import java.util.List;
import java.util.function.Predicate;

public class CollectionUtils {

    public static <E> int findFirstIndex(List<? extends E> list, int startIndex, Predicate<E> matcher) {
        for (int i = startIndex; i < list.size(); i++) {
            if (matcher.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static <E> int findFirstIndex(List<? extends E> list, Predicate<E> matcher) {
        return findFirstIndex(list, 0, matcher);
    }

    public static <E> E removeFirstMatching(List<? extends E> list, Predicate<E> matcher) {
        int index = findFirstIndex(list, matcher);
        return index < 0 ? null : list.remove(index);
    }

    public static <E> E fromEnd(List<? extends E> list, int index) {
        return (index < 0 || index >= list.size()) ? null : list.get(list.size() - index - 1);
    }
}
