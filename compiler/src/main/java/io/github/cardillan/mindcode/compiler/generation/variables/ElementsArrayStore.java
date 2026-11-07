package io.github.cardillan.mindcode.compiler.generation.variables;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mindcode.logic.arguments.LogicNumber;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/// Array implementation with a pre-created list of elements.
@NullMarked
public abstract class ElementsArrayStore extends AbstractArrayStore {
    /// Actual list of elements
    protected final List<ValueStore> elements;
    private final int distinctValues;
    private final int maxCardinality;

    public ElementsArrayStore(SourcePosition sourcePosition, String name, ArrayType arrayType, int startOffset, int size,
            @Nullable ArrayStore masterArray, List<ValueStore> elements, boolean subarray) {
        super(sourcePosition, name, arrayType, startOffset, size, LogicNumber.create(size), masterArray, subarray);
        if (masterArray != null && !(masterArray instanceof ElementsArrayStore)) {
            throw new IllegalArgumentException("Master array must be an ElementsArrayStore");
        }
        this.elements = elements;

        List<String> mlog = elements.stream().map(e -> e.unwrap() instanceof LogicArgument a ? a.toMlog() : "").toList();
        if (mlog.stream().anyMatch(String::isEmpty)) {
            distinctValues = elements.size();
            maxCardinality = 1;
        } else {
            Map<String, Long> frequencies = mlog.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            distinctValues = frequencies.size();
            maxCardinality = frequencies.values().stream().mapToInt(Long::intValue).max().orElse(0);
        }
    }

    @Override
    public ElementsArrayStore getMasterArray() {
        return (ElementsArrayStore) super.getMasterArray();
    }

    public List<ValueStore> getElements() {
        return elements;
    }

    public int fullSize() {
        return getElements().size();
    }

    public int distinctValues() {
        return distinctValues;
    }

    public int getMaxCardinality() {
        return maxCardinality;
    }
}
