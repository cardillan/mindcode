package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.ContextFactory;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.InternalArray;
import info.teksol.mc.mindcode.compiler.postprocess.JumpTable;
import info.teksol.mc.mindcode.logic.arguments.LogicArray;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.ArrayConstruction;
import info.teksol.mc.mindcode.logic.instructions.ArrayOrganization;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
class ArrayConstructorTest extends AbstractCodeGeneratorTest {
    private final List<LogicArray> arrays = IntStream.rangeClosed(2, 10).boxed()
            .mapMulti((Integer size, Consumer<LogicArray> consumer) -> {
                consumer.accept(createLocalArray(size));
//                consumer.accept(createRemoteArray(size));
//                consumer.accept(createSharedArray(size));
            }).toList();

    private LogicArray createLocalArray(int size) {
        InternalArray array = InternalArray.create(ip, nameCreator, new AstIdentifier(EMPTY, "a"),
                size, false, false, null, false);
        return LogicArray.create(array);
    }

    private LogicArray createRemoteArray(int size) {
        InternalArray array = InternalArray.create(ip, nameCreator, new AstIdentifier(EMPTY, "a"),
                size, false, true, a, false);
        return LogicArray.create(array);
    }

    private LogicArray createSharedArray(int size) {
        InternalArray array = InternalArray.create(ip, nameCreator, new AstIdentifier(EMPTY, "a"),
                size, false, true, a, true);
        return LogicArray.create(array);
    }

    private String describeInstruction(ArrayAccessInstruction ix, String test) {
        return String.format("%s: %s, %s, %s, %s, size %d", test,
                ix.getArray().getArrayStore().getArrayType() == ArrayStore.ArrayType.REMOTE_SHARED
                        ? "shared"
                        : ix.getArray().isDeclaredRemote() ? "remote" : "local",
                ix.getArrayOrganization(), ix.getArrayConstruction(),
                ix.isArrayFolded() ? "folded" : "unfolded", ix.getArray().getSize());
    }

    private void testInstruction(ArrayAccessInstruction ix) {
        Map<String, Integer> sharedStructures = new HashMap<>();
        int computedSize = ix.getSharedSize(sharedStructures);
        int computedSharedSize = sharedStructures.getOrDefault(ix.getJumpTableId(), 0);
        double computedSteps = ix.getExecutionSteps();

        Map<String, JumpTable> jumpTables = new HashMap<>();
        List<LogicInstruction> instructions = new ArrayList<>();
        ix.getArrayConstructor().generateJumpTable(jumpTables);
        ix.getArrayConstructor().expandInstruction(instructions::add, jumpTables);

        JumpTable jumpTable = jumpTables.get(ix.getJumpTableId());
        List<LogicInstruction> jumpTableInstructions = jumpTable == null ? List.of() : jumpTable.instructions();
        int generatedSize = instructions.stream().mapToInt(LogicInstruction::getRealSize).sum();
        int generatedSharedSize = jumpTableInstructions.stream()
                .filter(i -> i.getOpcode() != Opcode.END)
                .mapToInt(LogicInstruction::getRealSize).sum();

        assertEquals(generatedSize, computedSize,
                () -> describeInstruction(ix, "incorrect instruction size"));

        assertEquals(generatedSharedSize, computedSharedSize,
                () -> describeInstruction(ix, "incorrect shared size"));

        long multiLabels = instructions.stream().filter(i -> i.getOpcode() == Opcode.MULTILABEL).count()
                + jumpTableInstructions.stream().filter(i -> i.getOpcode() == Opcode.MULTILABEL).count();

        boolean inlined = ix.getArrayOrganization() == ArrayOrganization.INLINED;
        boolean singleBranch = ix.getArray().getSize() <= (ix.isArrayFolded() ? 2 : 1);
        long generatedSteps = multiLabels > 0
                ? generatedSize + generatedSharedSize - 2 * (multiLabels - 1) + (inlined && !singleBranch ? 1 : 0) // Compensate for the missing last jump in an inlined table
                : generatedSize + generatedSharedSize;

        assertEquals(generatedSteps, Math.ceil(computedSteps), () -> describeInstruction(ix, "incorrect step count"));
    }

    protected void testConstructors(Consumer<ArrayAccessInstruction> decorator) {
        ContextFactory.setArrayConstructorContext(this);
        for (LogicArray array : arrays) {
            ArrayAccessInstruction ix = ip.createReadArr(mockAstContext, tmp0, array, tmp1);
            decorator.accept(ix);
            testInstruction(ix);
        }
        ContextFactory.clearArrayConstructorContext();
    }
    
    private Consumer<ArrayAccessInstruction> decorator(ArrayOrganization organization, ArrayConstruction construction, boolean folded) {
        return ix -> ix.setArrayOrganization(organization, construction).setArrayFolded(folded);
    } 

    @Test
    void checkInternalCompactUnfolded() {
        testConstructors(decorator(ArrayOrganization.INTERNAL, ArrayConstruction.COMPACT, false));
    }

    @Test
    void checkInternalCompactFolded() {
        testConstructors(decorator(ArrayOrganization.INTERNAL, ArrayConstruction.COMPACT, true));
    }

    @Test
    void checkInternalCompactTarget() {
        testConstructors(ix -> ix.setArrayOrganization(ArrayOrganization.INTERNAL, ArrayConstruction.COMPACT).setCompactAccessTarget());
    }

    @Test
    void checkInternalRegularUnfolded() {
        testConstructors(decorator(ArrayOrganization.INTERNAL, ArrayConstruction.REGULAR, false));
    }

    @Test
    void checkInternalRegularFolded() {
        testConstructors(decorator(ArrayOrganization.INTERNAL, ArrayConstruction.REGULAR, true));
    }

    @Test
    void checkInlinedCompactUnfolded() {
        testConstructors(decorator(ArrayOrganization.INLINED, ArrayConstruction.COMPACT, false));
    }

    @Test
    void checkInlinedCompactFolded() {
        testConstructors(decorator(ArrayOrganization.INLINED, ArrayConstruction.COMPACT, true));
    }

    @Test
    void checkInlinedCompactTarget() {
        testConstructors(ix -> ix.setArrayOrganization(ArrayOrganization.INLINED, ArrayConstruction.COMPACT).setCompactAccessTarget());
    }

    @Test
    void checkInlinedRegularUnfolded() {
        testConstructors(decorator(ArrayOrganization.INLINED, ArrayConstruction.REGULAR, false));
    }

    @Test
    void checkInlinedRegularFolded() {
        testConstructors(decorator(ArrayOrganization.INLINED, ArrayConstruction.REGULAR, true));
    }

    @Test
    void checkLookup() {
        testConstructors(ix -> ix.setArrayOrganization(ArrayOrganization.LOOKUP, ArrayConstruction.COMPACT).setArrayLookupType(LogicKeyword.BLOCK));
    }
}
