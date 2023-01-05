package info.teksol.mindcode.mindustry;

import java.util.*;

class DeadCodeEliminator implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;

    private final List<LogicInstruction> program = new ArrayList<>();
    private final Set<String> reads = new HashSet<>();
    private final Map<String, List<LogicInstruction>> writes = new HashMap<>();

    DeadCodeEliminator(LogicInstructionPipeline next) {
        this.next = next;
    }

    @Override
    public void emit(LogicInstruction instruction) {
        program.add(instruction);
    }

    @Override
    public void flush() {
        do {
            analyzeDataflow();
        } while (removeUselessWrites());

        for (LogicInstruction instruction : program) {
            next.emit(instruction);
        }
        program.clear();
    }

    private void analyzeDataflow() {
        reads.clear();
        writes.clear();
        reads.add("@counter"); // instruction pointer is *always* read -- and our implementation of call/return depends on this
        program.forEach(this::examineInstruction);
    }

    /**
     * @return true if we need to do another round of dataflow analysis.
     */
    private boolean removeUselessWrites() {
        final Set<String> uselessWrites = new HashSet<>(writes.keySet());
        uselessWrites.removeAll(reads);
        for (String key : uselessWrites) {
            final List<LogicInstruction> deadInstructions = writes.get(key);
            program.removeAll(deadInstructions);
        }

        return !uselessWrites.isEmpty();
    }

    private void addWrite(LogicInstruction instruction, int index) {
        final String varName = instruction.getArgs().get(index);
        final List<LogicInstruction> ls = writes.getOrDefault(varName, new ArrayList<>());
        ls.add(instruction);
        writes.put(varName, ls);
    }

    private void examineInstruction(LogicInstruction instruction) {
        switch (instruction.getOpcode()) {
            case "set":
                visitSet(instruction);
                break;

            case "write":
                visitWrite(instruction);
                break;

            case "read":
                visitRead(instruction);
                break;

            case "jump":
                visitJump(instruction);
                break;

            case "op":
                visitOp(instruction);
                break;

            case "ucontrol":
                visitUcontrol(instruction);
                break;

            case "wait":
                visitWait(instruction);
                break;

            case "sensor":
                visitSensor(instruction);
                break;

            case "radar":
                visitRadar(instruction);

            case "ubind":
                visitUbind(instruction);
                break;

            case "control":
                visitControl(instruction);
                break;

            case "getlink":
                visitGetlink(instruction);
                break;

            case "print":
                visitPrint(instruction);
                break;

            case "printflush":
                visitPrintflush(instruction);
                break;

            case "ulocate":
                visitUlocate(instruction);
                break;

            case "uradar":
                visitUradar(instruction);
                break;

            case "draw":
                visitDraw(instruction);
                break;

            case "drawflush":
                visitDrawflush(instruction);
                break;

            case "label":
            case "end":
                // These don't have useful args
                break;

            default:
                throw new GenerationException("Unvisited opcode [" + instruction.getOpcode() + "]");
        }
    }

    private void visitDrawflush(LogicInstruction instruction) {
        reads.add(instruction.getArgs().get(0));
    }

    private void visitDraw(LogicInstruction instruction) {
        reads.addAll(instruction.getArgs().subList(1, instruction.getArgs().size()));
    }

    private void visitUradar(LogicInstruction instruction) {
        // uradar enemy attacker ground armor 0 order result
        reads.addAll(instruction.getArgs().subList(0, 5));
        addWrite(instruction, 6);
    }

    private void visitUlocate(LogicInstruction instruction) {
        /*
            found = ulocate(ore, @surge-alloy, outx, outy)
                    ulocate ore core true @surge-alloy outx outy found building

            found = ulocate(building, core, ENEMY, outx, outy, outbuilding)
                    ulocate building core true @copper outx outy found building

            found = ulocate(spawn, outx, outy, outbuilding)
                    ulocate spawn core true @copper outx outy found building

            found = ulocate(damaged, outx, outy, outbuilding)
                    ulocate damaged core true @copper outx outy found building
         */
        switch (instruction.getArgs().get(0)) {
            case "ore":
                visitUlocateOre(instruction);
                break;

            case "building":
                visitUlocateBuilding(instruction);
                break;

            case "spawn":
                visitUlocateSpawn(instruction);
                break;

            case "damaged":
                visitUlocateDamaged(instruction);
                break;
        }
    }

    private void visitUlocateDamaged(LogicInstruction instruction) {
        /*
            found = ulocate(damaged, outx, outy, outbuilding)
                    ulocate damaged core true @copper outx outy found building
         */

        // Due to the way the dead code eliminator works, we must register all writes as reads as well, otherwise
        // the whole instruction will be eliminated:
        //
        //   ulocate damaged core true @copper outx outy found building
        //              0     1    2    3       4    5    6    7
        //
        // If we do naïve deadcode elimination, if 4, 5 and 6 aren't used by the written code, the whole instruction
        // will be eliminated.
        addWrite(instruction, 4);
        addWrite(instruction, 5);
        addWrite(instruction, 6);
        reads.add(instruction.getArgs().get(4));
        reads.add(instruction.getArgs().get(5));
        reads.add(instruction.getArgs().get(6));
    }

    private void visitUlocateSpawn(LogicInstruction instruction) {
        /*
            found = ulocate(spawn, outx, outy, outbuilding)
                    ulocate spawn core true @copper outx outy found building
         */
        addWrite(instruction, 4);
        addWrite(instruction, 5);
        addWrite(instruction, 6);
        reads.add(instruction.getArgs().get(4));
        reads.add(instruction.getArgs().get(5));
        reads.add(instruction.getArgs().get(6));
        reads.add(instruction.getArgs().get(7));
    }

    private void visitUlocateBuilding(LogicInstruction instruction) {
        /*
            found = ulocate(building, core, ENEMY, outx, outy, outbuilding)
                    ulocate building core true @copper outx outy found building
         */
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
        addWrite(instruction, 4);
        addWrite(instruction, 5);
        addWrite(instruction, 7);
        reads.add(instruction.getArgs().get(4));
        reads.add(instruction.getArgs().get(5));
        reads.add(instruction.getArgs().get(7));
    }

    private void visitUlocateOre(LogicInstruction instruction) {
        /*
            found = ulocate(ore, @surge-alloy, outx, outy)
                    ulocate ore core true @surge-alloy outx outy found building
         */
        reads.add(instruction.getArgs().get(3));
        addWrite(instruction, 4);
        addWrite(instruction, 5);
        reads.add(instruction.getArgs().get(4));
        reads.add(instruction.getArgs().get(5));
    }

    private void visitPrintflush(LogicInstruction instruction) {
        reads.add(instruction.getArgs().get(0));
    }

    private void visitPrint(LogicInstruction instruction) {
        reads.add(instruction.getArgs().get(0));
    }

    private void visitGetlink(LogicInstruction instruction) {
        addWrite(instruction, 0);
        reads.add(instruction.getArgs().get(1));
    }

    private void visitControl(LogicInstruction instruction) {
        reads.addAll(instruction.getArgs());
    }

    private void visitUbind(LogicInstruction instruction) {
        // ubind type
        reads.add(instruction.getArgs().get(0));
    }

    private void visitSensor(LogicInstruction instruction) {
        // sensor out target property
        addWrite(instruction, 0);
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
    }

    private void visitRadar(LogicInstruction instruction) {
        // radar prop1 prop2 prop3 sortby target order out
        reads.add(instruction.getArgs().get(0));
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
        reads.add(instruction.getArgs().get(3));
        reads.add(instruction.getArgs().get(4));
        reads.add(instruction.getArgs().get(5));
        addWrite(instruction, 6);
    }

    private void visitSet(LogicInstruction instruction) {
        addWrite(instruction, 0);
        reads.add(instruction.getArgs().get(1));
    }

    private void visitWrite(LogicInstruction instruction) {
        reads.add(instruction.getArgs().get(0));
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
    }

    private void visitRead(LogicInstruction instruction) {
        addWrite(instruction, 0);
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
    }

    private void visitJump(LogicInstruction instruction) {
        if (instruction.getArgs().size() > 2) reads.add(instruction.getArgs().get(2));
        if (instruction.getArgs().size() > 3) reads.add(instruction.getArgs().get(3));
    }

    private void visitOp(LogicInstruction instruction) {
        addWrite(instruction, 1);
        if (instruction.getArgs().size() > 2) reads.add(instruction.getArgs().get(2));
        if (instruction.getArgs().size() > 3) reads.add(instruction.getArgs().get(3));
    }

    private void visitWait(LogicInstruction instruction) {
        reads.add(instruction.getArgs().get(0));
    }

    private void visitUcontrol(LogicInstruction instruction) {
        switch (instruction.getArgs().get(0)) {
            case "mine":
                visitUcontrolMine(instruction);
                break;

            case "move":
                visitUcontrolMove(instruction);
                break;

            case "approach":
                visitUcontrolApproach(instruction);
                break;

            case "within":
                visitUcontrolWithin(instruction);
                break;

            case "getBlock":
                visitUcontrolGetBlock(instruction);
                break;

            case "build":
                visitUcontrolBuild(instruction);
                break;

            case "flag":
                visitUcontrolFlag(instruction);
                break;

            case "itemDrop":
                visitUcontrolItemDrop(instruction);
                break;


            case "target":
                visitTarget(instruction);
                break;

            case "targetp":
                visitTargetp(instruction);
                break;

            case "itemTake":
                visitUcontrolItemTake(instruction);
                break;

            case "payTake":
                visitUcontrolPayTake(instruction);
                break;

            case "boost":
                visitUcontrolBoost(instruction);
                break;

            case "payDrop":
            case "pathfind":
            case "idle":
            case "stop":
                // These ucontrol don't have any arguments, hence they have nothing to read or write
                break;

            default:
                throw new GenerationException("Unknown ucontrol opcode [" + instruction.getArgs().get(0) + "]");
        }
    }

    private void visitUcontrolBoost(LogicInstruction instruction) {
        reads.add(instruction.getArgs().get(1));
    }

    private void visitUcontrolPayTake(LogicInstruction instruction) {
        reads.add(instruction.getArgs().get(1));
    }

    private void visitTargetp(LogicInstruction instruction) {
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
    }

    private void visitTarget(LogicInstruction instruction) {
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
        reads.add(instruction.getArgs().get(3));
    }

    private void visitUcontrolMine(LogicInstruction instruction) {
        // ucontrol mine x y
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
    }

    private void visitUcontrolMove(LogicInstruction instruction) {
        // ucontrol move x y
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
    }

    private void visitUcontrolApproach(LogicInstruction instruction) {
        // ucontrol approach x y radius
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
        reads.add(instruction.getArgs().get(3));
    }

    private void visitUcontrolWithin(LogicInstruction instruction) {
        // ucontrol within x y radius
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
        reads.add(instruction.getArgs().get(3));
    }

    private void visitUcontrolGetBlock(LogicInstruction instruction) {
        // ucontrol getBlock x y resultType resultBuilding
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
        addWrite(instruction, 3);
        addWrite(instruction, 4);
    }

    private void visitUcontrolBuild(LogicInstruction instruction) {
        // ucontrol build x y block rotation config
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
        reads.add(instruction.getArgs().get(3));
        reads.add(instruction.getArgs().get(4));
    }

    private void visitUcontrolFlag(LogicInstruction instruction) {
        // ucontrol flag value
        reads.add(instruction.getArgs().get(1));
    }

    private void visitUcontrolItemDrop(LogicInstruction instruction) {
        // ucontrol itemDrop to amount
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
    }

    private void visitUcontrolItemTake(LogicInstruction instruction) {
        // ucontrol itemTake from item amount
        reads.add(instruction.getArgs().get(1));
        reads.add(instruction.getArgs().get(2));
        reads.add(instruction.getArgs().get(3));
    }

}
