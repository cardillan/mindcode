package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.ArrayOrganization;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;

@NullMarked
class ArrayOptimizer extends BaseOptimizer {
    public ArrayOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.ARRAY_OPTIMIZATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        try (OptimizationContext.LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                LogicInstruction instruction = iterator.next();
                if (instruction instanceof ArrayAccessInstruction ix && ix.getArrayOrganization() == ArrayOrganization.INTERNAL_REGULAR) {
                    ArrayOrganization organization = switch (ix.getArray().getArrayStore().getSize()) {
                        case 1 -> ArrayOrganization.INTERNAL_SIZE1;
                        case 2 -> ArrayOrganization.INTERNAL_SIZE2;
                        case 3 -> ArrayOrganization.INTERNAL_SIZE3;
                        default -> ix.getArrayOrganization();
                    };

                    if (ix.getArrayOrganization() != organization) {
                        ArrayAccessInstruction copy = instructionProcessor.copy(ix);
                        copy.setArrayOrganization(organization);
                        iterator.set(copy);
                    }
                }
            }
        }

        return false;
    }
}
