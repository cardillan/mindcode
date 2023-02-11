package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.optimisation.Optimisation;
import java.util.EnumSet;
import java.util.Set;

public class CompilerProfile {
    private Set<Optimisation> optimisations = EnumSet.allOf(Optimisation.class);
    private int parseTreeLevel = 0;
    private int debugLevel = 0;

    private CompilerProfile(Set<Optimisation> optimisations) {
        this.optimisations = optimisations;
    }

    public CompilerProfile(Optimisation... optimisations) {
        this.optimisations = Set.of(optimisations);
    }

    public static CompilerProfile fullOptimizations() {
        return new CompilerProfile(EnumSet.allOf(Optimisation.class));
    }

    public static CompilerProfile noOptimizations() {
        return new CompilerProfile(EnumSet.noneOf(Optimisation.class));
    }

    public Set<Optimisation> getOptimisations() {
        return optimisations;
    }

    public void setOptimisations(Set<Optimisation> optimisations) {
        this.optimisations = optimisations;
    }

    public int getParseTreeLevel() {
        return parseTreeLevel;
    }

    public void setParseTreeLevel(int parseTreeLevel) {
        this.parseTreeLevel = parseTreeLevel;
    }

    public int getDebugLevel() {
        return debugLevel;
    }

    public void setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
    }
}
