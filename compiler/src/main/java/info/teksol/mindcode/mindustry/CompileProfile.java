package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.optimisation.Optimisation;
import java.util.EnumSet;
import java.util.Set;

public class CompileProfile {
    private Set<Optimisation> optimisations = EnumSet.allOf(Optimisation.class);
    private boolean printParseTree = false;
    private int debugLevel = 0;

    private CompileProfile(Set<Optimisation> optimisations) {
        this.optimisations = optimisations;
    }

    public CompileProfile(Optimisation... optimisations) {
        this.optimisations = Set.of(optimisations);
    }

    public static CompileProfile fullOptimizations() {
        return new CompileProfile(EnumSet.allOf(Optimisation.class));
    }

    public static CompileProfile noOptimizations() {
        return new CompileProfile(EnumSet.noneOf(Optimisation.class));
    }

    public Set<Optimisation> getOptimisations() {
        return optimisations;
    }

    public void setOptimisations(Set<Optimisation> optimisations) {
        this.optimisations = optimisations;
    }

    public boolean isPrintParseTree() {
        return printParseTree;
    }

    public void setPrintParseTree(boolean printParseTree) {
        this.printParseTree = printParseTree;
    }

    public int getDebugLevel() {
        return debugLevel;
    }

    public void setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
    }
}
