package info.teksol.util;

import java.util.List;
import java.util.function.Predicate;

public class CollectionUtils {

    public static <E> int findFirstIndex(List<? extends E> list, Predicate<E> criteria) {
        for (int i = 0; i < list.size(); i++) {
            if (criteria.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static <E> E removeFirstMatching(List<? extends E> list, Predicate<E> criteria) {
        int index = findFirstIndex(list, criteria);
        return index < 0 ? null : list.remove(index);
    }
}
