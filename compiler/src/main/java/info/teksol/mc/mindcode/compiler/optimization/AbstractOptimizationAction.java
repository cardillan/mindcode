package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class AbstractOptimizationAction implements OptimizationAction {
    protected final AstContext astContext;
    protected final int cost;
    protected final double benefit;

    public AbstractOptimizationAction(AstContext astContext, int cost, double benefit) {
        this.astContext = astContext;
        this.cost = cost;
        this.benefit = benefit;
    }

    @Override
    public AstContext astContext() {
        return astContext;
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
