package info.teksol.mc.util;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@NullMarked
public class CollectionUtils {
    private static final Object NULL = new Object();

    @SafeVarargs
    public static <T extends @Nullable Object> Predicate<T> in(T... values) {
        for (T value : values) {
            if (value == null) {
                Object[] elements = new Object[values.length];
                for (int i = 0; i < elements.length; i++) {
                    elements[i] = Objects.requireNonNullElse(values[i], NULL);
                }
                return Set.of(elements)::contains;
            }
        }

        return Set.of(values)::contains;
    }

    public static <T extends @Nullable Object> Predicate<T> in(T value) {
        return t -> Objects.equals(t, value);
    }

    public static <T extends @Nullable Object> Predicate<T> in(T v1, T v2) {
        return t -> {
            if (t == null) {
                return v1 == null || v2 == null;
            } else {
                return t.equals(v1) || t.equals(v2);
            }
        };
    }

    public static <T extends @Nullable Object> Predicate<T> in(T v1, T v2, T v3) {
        return t -> {
            if (t == null) {
                return v1 == null || v2 == null || v3 == null;
            } else {
                return t.equals(v1) || t.equals(v2) || t.equals(v3);
            }
        };
    }

    @SafeVarargs
    public static <T extends @Nullable Object, R extends @Nullable Object> Predicate<T> resultIn(Function<T, R> mapping,  R... values) {
        return t -> in(values).test(mapping.apply(t));
    }

    public static <T extends @Nullable Object, R extends @Nullable Object> Predicate<T> resultIn(Function<T, R> mapping,  R value) {
        return t -> in(value).test(mapping.apply(t));
    }

    public static <T extends @Nullable Object, R extends @Nullable Object> Predicate<T> resultIn(Function<T, R> mapping,  R v1, R v2) {
        return t -> in(v1, v2).test(mapping.apply(t));
    }

    public static <T extends @Nullable Object, R extends @Nullable Object> Predicate<T> resultIn(Function<T, R> mapping,  R v1, R v2, R v3) {
        return t -> in(v1, v2, v3).test(mapping.apply(t));
    }

    @SafeVarargs
    public static <T extends @Nullable Object> Predicate<T> notIn(T... values) {
        return in(values).negate();
    }

    public static <E extends @Nullable Object> int indexOf(List<? extends E> list, int startIndex, Predicate<E> matcher) {
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

    public static <E extends @Nullable Object> int lastIndexOf(List<? extends E> list, int startIndex, Predicate<E> matcher) {
        if (startIndex < 0 || startIndex >= list.size()) {
            throw new IndexOutOfBoundsException(startIndex);
        }
        for (int i = startIndex; i >= 0; i--) {
            if (matcher.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static <E extends @Nullable Object> int indexOf(List<? extends E> list, Predicate<E> matcher) {
        return indexOf(list, 0, matcher);
    }

    public static <E extends @Nullable Object> int lastIndexOf(List<? extends E> list, Predicate<E> matcher) {
        return list.isEmpty() ? -1 : lastIndexOf(list, list.size() - 1, matcher);
    }

    public static <E extends @Nullable Object> @Nullable E removeFirstMatching(List<? extends E> list, Predicate<E> matcher) {
        int index = indexOf(list, matcher);
        return index < 0 ? null : list.remove(index);
    }

    public static <E> @NonNull List<E> createList(E firstItem, List<E> otherItems) {
        if (otherItems.isEmpty()) {
            return List.of(firstItem);
        } else {
            List<E> list = new ArrayList<>(otherItems);
            list.addFirst(firstItem);
            return List.copyOf(list);
        }
    }

    public static <E> @NonNull List<E> createList(List<E> otherItems, E lastItem) {
        if (otherItems.isEmpty()) {
            return List.of(lastItem);
        } else {
            List<E> list = new ArrayList<>(otherItems);
            list.addLast(lastItem);
            return List.copyOf(list);
        }
    }
}
