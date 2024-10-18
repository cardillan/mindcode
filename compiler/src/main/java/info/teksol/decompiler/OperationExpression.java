package info.teksol.decompiler;

import info.teksol.mindcode.logic.Operation;

import java.util.List;
import java.util.Objects;

public class OperationExpression implements MlogExpression {
    private final Operation operation;
    private MlogExpression operand1;
    private MlogExpression operand2;

    public OperationExpression(Operation operation, MlogVariable operand1, MlogVariable operand2) {
        this.operation = Objects.requireNonNull(operation);
        this.operand1 = Objects.requireNonNull(operand1);
        this.operand2 = operation.getOperands() == 1 ? null : Objects.requireNonNull(operand2);
    }

    @Override
    public void gatherInputVariables(List<MlogVariable> variables) {
        if (operand1 instanceof MlogVariable var1) {
            variables.add(var1);
        }
        if (operand2 instanceof MlogVariable var2) {
            variables.add(var2);
        }
    }

    @Override
    public int size() {
        return (operand1 instanceof MlogVariable ? 1 : 0) + (operand2 instanceof MlogVariable ? 1 : 0);
    }

    @Override
    public void replaceVariable(MlogVariable variable, MlogExpression expression) {
        if (variable.equals(operand1)) {
            operand1 = expression;
        }
        if (variable.equals(operand2)) {
            operand2 = expression;
        }
    }

    @Override
    public String toMlog() {
        if (operation.isFunction()) {
            if (operation.getOperands() == 1) {
                return operation.getMindcode() + "(" + operand1.toMlog() + ")";
            } else {
                return operation.getMindcode() + "(" + operand1.toMlog() + ", " + operand2.toMlog() + ")";
            }
        } else {
            if (operation.getOperands() == 1) {
                return "(" + operation.getMindcode() + operand1.toMlog() + ")";
            } else {
                return "(" + operand1.toMlog() + " " + operation.getMindcode() + " " + operand2.toMlog() + ")";
            }
        }
    }
}
