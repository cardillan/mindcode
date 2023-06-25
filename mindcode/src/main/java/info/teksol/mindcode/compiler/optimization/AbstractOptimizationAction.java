package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.AstContext;

public abstract class AbstractOptimizationAction implements OptimizationAction {

    protected final AstContext astContext;
    protected final int cost;
    protected final double benefit;
    protected final int codeMultiplication;

    public AbstractOptimizationAction(AstContext astContext, int cost, double benefit, int codeMultiplication) {
        this.astContext = astContext;
        this.cost = cost;
        this.benefit = benefit;
        this.codeMultiplication = codeMultiplication;
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
    public String functionPrefix() {
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

    @Override
    public int codeMultiplication() {
        return codeMultiplication;
    }
}