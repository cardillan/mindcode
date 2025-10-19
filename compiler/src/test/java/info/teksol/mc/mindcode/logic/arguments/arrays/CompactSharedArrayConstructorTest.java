package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.mindcode.compiler.generation.variables.InternalArray;
import info.teksol.mc.mindcode.logic.arguments.LogicArray;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.ArrayConstruction;
import info.teksol.mc.mindcode.logic.instructions.ArrayOrganization;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@NullMarked
class CompactSharedArrayConstructorTest {

    abstract static class Base extends AbstractCodeGeneratorTest {
        protected ArrayOrganization organization = ArrayOrganization.INTERNAL;
        protected ArrayConstruction construction = ArrayConstruction.REGULAR;
        protected boolean folded;

        protected void setup(ArrayOrganization organization, ArrayConstruction construction, boolean folded) {
            this.organization = organization;
            this.construction = construction;
            this.folded = folded;
            ArrayConstructorFactory.setContext(this);
        }

        private LogicArray createArray(int size) {
            InternalArray array = InternalArray.create(nameCreator, new AstIdentifier(EMPTY, "a"), size,
                    false, false, null, false);
            return LogicArray.create(array);
        }

        protected void checkCodeSizeSteps(int arraySize, int expectedSize, int expectedSharedSize, double expectedSteps) {
            LogicArray array = createArray(arraySize);
            ArrayAccessInstruction ix = ip.createReadArr(mockAstContext, tmp0, array, tmp1)
                    .setArrayOrganization(organization, construction).setArrayFolded(folded);

            Map<String, Integer> sharedStructures = new HashMap<>();
            int actualSize = ix.getRealSize(sharedStructures);
            Assertions.assertEquals(expectedSize, actualSize, "difference in code size");
            Assertions.assertEquals(expectedSharedSize, sharedStructures.getOrDefault(ix.getJumpTableId(), 0),
                    "difference in shared structures");

            double actualSteps = ix.getExecutionSteps();
            Assertions.assertEquals(expectedSteps, actualSteps, "difference in execution steps");
        }
    }

    @Nested
    public class TextBasedDispatch extends Base {
        @Test
        void checkInternalCompactUnfolded() {
            setup(ArrayOrganization.INTERNAL, ArrayConstruction.COMPACT, false);
            checkCodeSizeSteps(1, 3, 2, 5.0);
            checkCodeSizeSteps(2, 3, 4, 5.0);
            checkCodeSizeSteps(3, 3, 6, 5.0);
            checkCodeSizeSteps(5, 3, 10, 5.0);
            checkCodeSizeSteps(10, 3, 20, 5.0);
        }

        @Test
        void checkInternalCompactFolded() {
            setup(ArrayOrganization.INTERNAL, ArrayConstruction.COMPACT, true);
            checkCodeSizeSteps(1, 4, 2, 6.0);
            checkCodeSizeSteps(2, 4, 2, 6.0);
            checkCodeSizeSteps(3, 4, 4, 6.0);
            checkCodeSizeSteps(5, 4, 6, 6.0);
            checkCodeSizeSteps(10, 4, 10, 6.0);
        }

        @Test
        void checkInlinedCompactUnfolded() {
            setup(ArrayOrganization.INLINED, ArrayConstruction.COMPACT, false);
            checkCodeSizeSteps(1, 3, 0, 4.0 - 1.0 / 1.0);
            checkCodeSizeSteps(2, 5, 0, 4.0 - 1.0 / 2.0);
            checkCodeSizeSteps(3, 7, 0, 4.0 - 1.0 / 3.0);
            checkCodeSizeSteps(5, 11, 0, 4.0 - 1.0 / 5.0);
            checkCodeSizeSteps(10, 21, 0, 4.0 - 1.0 / 10.0);
        }

        @Test
        void checkInlinedCompactFolded() {
            setup(ArrayOrganization.INLINED, ArrayConstruction.COMPACT, true);
            checkCodeSizeSteps(1, 3, 0, 4.0 - 1.0 / 1.0);
            checkCodeSizeSteps(2, 3, 0, 4.0 - 2.0 / 2.0);
            checkCodeSizeSteps(3, 5, 0, 4.0 - 1.0 / 3.0);
            checkCodeSizeSteps(5, 7, 0, 4.0 - 1.0 / 5.0);
            checkCodeSizeSteps(10, 11, 0, 4.0 - 2.0 / 10.0);
        }

        @Test
        void checkInternalRegularUnfolded() {
            setup(ArrayOrganization.INTERNAL, ArrayConstruction.REGULAR, false);
            checkCodeSizeSteps(1, 3, 2, 4.8);
            checkCodeSizeSteps(2, 3, 4, 4.8);
            checkCodeSizeSteps(3, 3, 6, 4.8);
            checkCodeSizeSteps(5, 3, 10, 4.8);
            checkCodeSizeSteps(10, 3, 20, 4.8);
        }
        
        @Test
        void checkInternalRegularFolded() {
            setup(ArrayOrganization.INTERNAL, ArrayConstruction.REGULAR, true);
            checkCodeSizeSteps(1, 4, 2, 5.8);
            checkCodeSizeSteps(2, 4, 2, 5.8);
            checkCodeSizeSteps(3, 4, 4, 5.8);
            checkCodeSizeSteps(5, 4, 6, 5.8);
            checkCodeSizeSteps(10, 4, 10, 5.8);
        }

        @Test
        void checkInlinedRegularUnfolded() {
            setup(ArrayOrganization.INLINED, ArrayConstruction.REGULAR, false);
            checkCodeSizeSteps(1, 2, 0, 3.0 - 1.0 / 1.0);
            checkCodeSizeSteps(2, 4, 0, 3.0 - 1.0 / 2.0);
            checkCodeSizeSteps(3, 6, 0, 3.0 - 1.0 / 3.0);
            checkCodeSizeSteps(5, 10, 0, 3.0 - 1.0 / 5.0);
            checkCodeSizeSteps(10, 20, 0, 3.0 - 1.0 / 10.0);
        }
        
        @Test
        void checkInlinedRegularFolded() {
            setup(ArrayOrganization.INLINED, ArrayConstruction.REGULAR, true);
            checkCodeSizeSteps(1, 2, 0, 3.0 - 1.0 / 1.0);
            checkCodeSizeSteps(2, 2, 0, 3.0 - 2.0 / 2.0);
            checkCodeSizeSteps(3, 4, 0, 3.0 - 1.0 / 3.0);
            checkCodeSizeSteps(5, 6, 0, 3.0 - 1.0 / 5.0);
            checkCodeSizeSteps(10, 10, 0, 3.0 - 2.0 / 10.0);
        }
    }
}