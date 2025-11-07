package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicNull;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.mimex.ContentType;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.profile.BuiltinEvaluation;
import info.teksol.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@NullMarked
public class ValueAnalyzer {
    private final GlobalCompilerProfile profile;
    private final MindustryMetadata metadata;
    private final boolean allowMindustryContent;
    private boolean first = true;
    private @Nullable ContentType contentType;        // ContentType.UNKNOWN represents an integer
    private @Nullable Integer lastValue;

    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;
    private final Set<Integer> values = new HashSet<>();

    public ValueAnalyzer(GlobalCompilerProfile profile, MindustryMetadata metadata, boolean allowMindustryContent) {
        this.profile = profile;
        this.metadata = metadata;
        this.allowMindustryContent = allowMindustryContent;
    }

    public @Nullable ContentType getContentType() {
        return contentType;
    }

    public boolean inspect(LogicValue value) {
        if (value == LogicNull.NULL) {
            lastValue = null;
            return true;
        }

        ContentType category = categorize(value);

        if (first) {
            contentType = category;
            first = false;
        }

        return getContentType() == category && getContentType() != null;
    }

    public boolean inspectRange(int rangeLowValue, int rangeHighValue) {
        if (!first && getContentType() != ContentType.UNKNOWN) {
            contentType = null;
            return false;
        }

        contentType = ContentType.UNKNOWN;
        first = false;
        registerValue(rangeLowValue);
        registerValue(rangeHighValue);
        return true;
    }

    public @Nullable ContentType categorize(LogicValue value) {
        if (value.isNumericConstant() && value.isInteger()) {
            registerValue(value.getIntValue());
            return ContentType.UNKNOWN;
        } else if (allowMindustryContent && value instanceof LogicBuiltIn builtIn && builtIn.getObject() != null && canConvert(builtIn)) {
            registerValue(builtIn.getObject().logicId());
            return builtIn.getObject().contentType();
        }

        return null;
    }

    private void registerValue(int value) {
        lastValue = value;
        min = Math.min(min, value);
        max = Math.max(max, value);
        values.add(value);
    }

    private boolean canConvert(LogicBuiltIn builtIn) {
        MindustryContent object = builtIn.getObject();
        return object != null
                && profile.getBuiltinEvaluation() != BuiltinEvaluation.NONE
                && object.contentType().hasLookup
                && object.logicId() >= 0
                && (profile.getBuiltinEvaluation() == BuiltinEvaluation.FULL || metadata.isStableBuiltin(builtIn.getName()));
    }

    public @Nullable Integer getLastValue() {
        return lastValue;
    }

    public boolean isMindustryContent() {
        return getContentType() != ContentType.UNKNOWN;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getRange() {
        return max - min + 1;
    }

    public Set<Integer> getValues() {
        return values;
    }
}
