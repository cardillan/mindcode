package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
abstract class AbstractOptimizationAction implements OptimizationAction {
    protected final AstContext astContext;
    protected final int cost;
    protected final double benefit;

    public AbstractOptimizationAction(AstContext astContext, int cost, double benefit) {
        this.astContext = astContext;
        this.cost = Math.max(cost, 0);
        this.benefit = benefit;
    }

    @Override
    public AstContext astContext() {
        return astContext;
    }

    @Override
    public boolean inlining() {
        return false;
    }

    @Override
    public @Nullable String functionPrefix() {
        return null;
    }

    @Override
    public int cost() {
        return cost;
    }

    @Override
    public double benefit() {
        return benefit;
    }

    public abstract String toString();
}
