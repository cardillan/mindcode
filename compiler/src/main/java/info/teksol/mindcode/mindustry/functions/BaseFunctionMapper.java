package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.generator.GenerationException;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.logic.Opcode;
import java.util.List;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

public class BaseFunctionMapper implements FunctionMapper {
    private final InstructionProcessor instructionProcessor;

    public BaseFunctionMapper(InstructionProcessor InstructionProcessor) {
        this.instructionProcessor = InstructionProcessor;
    }

    protected final LogicInstruction createInstruction(Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    @Override
    public String handleFunction(LogicInstructionPipeline pipeline, String functionName, List<String> params) {
        switch (functionName) {
            case "print":
                return handlePrint(pipeline, params);

            case "printflush":
                return handlePrintflush(pipeline, params);

            case "wait":
                return handleWait(pipeline, params);

            case "ubind":
                return handleUbind(pipeline, params);

            case "move":
                return handleMove(pipeline, params);

            case "rand":
                return handleRand(pipeline, params);

            case "getlink":
                return handleGetlink(pipeline, params);

            case "radar":
                return handleRadar(pipeline, params);

            case "mine":
                return handleMine(pipeline, params);

            case "itemDrop":
                return handleItemDrop(pipeline, params);

            case "itemTake":
                return handleItemTake(pipeline, params);

            case "flag":
                return handleFlag(pipeline, params);

            case "approach":
                return handleApproach(pipeline, params);

            case "idle":
                return handleIdle(pipeline);

            case "pathfind":
                return handlePathfind(pipeline);

            case "stop":
                return handleStop(pipeline);

            case "boost":
                return handleBoost(pipeline, params);

            case "target":
                return handleTarget(pipeline, params);

            case "targetp":
                return handleTargetp(pipeline, params);

            case "payDrop":
                return handlePayDrop(pipeline);

            case "payTake":
                return handlePayTake(pipeline, params);

            case "build":
                return handleBuild(pipeline, params);

            case "getBlock":
                return handleGetBlock(pipeline, params);

            case "within":
                return handleWithin(pipeline, params);

            case "tan":
            case "sin":
            case "cos":
            case "log":
            case "abs":
            case "floor":
            case "ceil":
                return handleMath(pipeline, functionName, params);

            case "clear":
                return handleClear(pipeline, params);

            case "color":
                return handleColor(pipeline, params);

            case "stroke":
                return handleStroke(pipeline, params);

            case "line":
                return handleLine(pipeline, params);
            case "rect":
                return handleRect(pipeline, params);

            case "lineRect":
                return handleLineRect(pipeline, params);

            case "poly":
                return handlePoly(pipeline, params);

            case "linePoly":
                return handleLinePoly(pipeline, params);

            case "triangle":
                return handleTriangle(pipeline, params);

            case "image":
                return handleImage(pipeline, params);

            case "drawflush":
                return handleDrawflush(pipeline, params);

            case "uradar":
                return handleURadar(pipeline, params);

            case "ulocate":
                return handleULocate(pipeline, params);

            case "end":
                return handleEnd(pipeline);

            case "sqrt":
                return handleSqrt(pipeline, params);

            case "min":
                return handleMin(pipeline, params);

            case "max":
                return handleMax(pipeline, params);

            case "len":
                return handleLen(pipeline, params);

            case "angle":
                return handleAngle(pipeline, params);

            case "log10":
                return handleLog10(pipeline, params);

            case "noise":
                return handleNoise(pipeline, params);

            default:
                return null;
        }
    }

    private String handleNoise(LogicInstructionPipeline pipeline, List<String> params) {
        String tmp = instructionProcessor.nextTemp();
        pipeline.emit(createInstruction(OP, "noise", tmp, params.get(0), params.get(1)));
        return tmp;
    }

    private String handleLog10(LogicInstructionPipeline pipeline, List<String> params) {
        String tmp = instructionProcessor.nextTemp();
        pipeline.emit(createInstruction(OP, "log10", tmp, params.get(0)));
        return tmp;
    }

    private String handleAngle(LogicInstructionPipeline pipeline, List<String> params) {
        String tmp = instructionProcessor.nextTemp();
        pipeline.emit(createInstruction(OP, "angle", tmp, params.get(0), params.get(1)));
        return tmp;
    }

    private String handleLen(LogicInstructionPipeline pipeline, List<String> params) {
        String tmp = instructionProcessor.nextTemp();
        pipeline.emit(createInstruction(OP, "len", tmp, params.get(0), params.get(1)));
        return tmp;
    }

    private String handleMax(LogicInstructionPipeline pipeline, List<String> params) {
        String tmp = instructionProcessor.nextTemp();
        pipeline.emit(createInstruction(OP, "max", tmp, params.get(0), params.get(1)));
        return tmp;
    }

    private String handleMin(LogicInstructionPipeline pipeline, List<String> params) {
        String tmp = instructionProcessor.nextTemp();
        pipeline.emit(createInstruction(OP, "min", tmp, params.get(0), params.get(1)));
        return tmp;
    }

    private String handleSqrt(LogicInstructionPipeline pipeline, List<String> params) {
        String tmp = instructionProcessor.nextTemp();
        pipeline.emit(createInstruction(OP, "sqrt", tmp, params.get(0)));
        return tmp;
    }

    private String handleEnd(LogicInstructionPipeline pipeline) {
        pipeline.emit(createInstruction(END));
        return "null";
    }

    private String handleULocate(LogicInstructionPipeline pipeline, List<String> params) {
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
        
        String tmp = instructionProcessor.nextTemp();
        switch (params.get(0)) {
            case "ore":
                if (params.size() < 4) {
                    throw new InsufficientArgumentsException("ulocate(ore) requires 4 arguments, received " + params.size());
                }

                pipeline.emit(createInstruction(ULOCATE, "ore", "core", "true", params.get(1), params.get(2), params.get(3), tmp, instructionProcessor.nextTemp()));
                break;
            case "building":
                if (params.size() < 6) {
                    throw new InsufficientArgumentsException("ulocate(building) requires 6 arguments, received " + params.size());
                }

                pipeline.emit(createInstruction(ULOCATE, "building", params.get(1), params.get(2), "@copper", params.get(3), params.get(4), tmp, params.get(5)));
                break;

            case "spawn":
                if (params.size() < 4) {
                    throw new InsufficientArgumentsException("ulocate(spawn) requires 4 arguments, received " + params.size());
                }

                pipeline.emit(createInstruction(ULOCATE, "spawn", "core", "true", "@copper", params.get(1), params.get(2), tmp, params.get(3)));
                break;

            case "damaged":
                if (params.size() < 4) {
                    throw new InsufficientArgumentsException("ulocate(damaged) requires 4 arguments, received " + params.size());
                }

                pipeline.emit(createInstruction(ULOCATE, "damaged", "core", "true", "@copper", params.get(1), params.get(2), tmp, params.get(3)));
                break;

            default:
                throw new GenerationException("Unhandled type of ulocate in " + params);
        }

        return tmp;
    }

    private String handleURadar(LogicInstructionPipeline pipeline, List<String> params) {
        // uradar enemy attacker ground armor 0 order result
        String tmp = instructionProcessor.nextTemp();
        pipeline.emit(createInstruction(URADAR, params.get(0), params.get(1), params.get(2), params.get(3), "0", params.get(4), tmp));
        return tmp;
    }

    private String handleDrawflush(LogicInstructionPipeline pipeline, List<String> params) {
        pipeline.emit(createInstruction(DRAWFLUSH, params.get(0)));
        return params.get(0);
    }

    private String handleImage(LogicInstructionPipeline pipeline, List<String> params) {
        pipeline.emit(createInstruction(DRAW, "image", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return "null";
    }

    private String handleTriangle(LogicInstructionPipeline pipeline, List<String> params) {
        pipeline.emit(createInstruction(DRAW, "triangle", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4), params.get(5)));
        return "null";
    }

    private String handleLinePoly(LogicInstructionPipeline pipeline, List<String> params) {
        pipeline.emit(createInstruction(DRAW, "linePoly", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return "null";
    }

    private String handlePoly(LogicInstructionPipeline pipeline, List<String> params) {
        pipeline.emit(createInstruction(DRAW, "poly", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return "null";
    }

    private String handleLineRect(LogicInstructionPipeline pipeline, List<String> params) {
        pipeline.emit(createInstruction(DRAW, "lineRect", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleRect(LogicInstructionPipeline pipeline, List<String> params) {
        pipeline.emit(createInstruction(DRAW, "rect", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleLine(LogicInstructionPipeline pipeline, List<String> params) {
        pipeline.emit(createInstruction(DRAW, "line", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleStroke(LogicInstructionPipeline pipeline, List<String> params) {
        pipeline.emit(createInstruction(DRAW, "stroke", params.get(0)));
        return "null";
    }

    private String handleColor(LogicInstructionPipeline pipeline, List<String> params) {
        pipeline.emit(createInstruction(DRAW, "color", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleClear(LogicInstructionPipeline pipeline, List<String> params) {
        pipeline.emit(createInstruction(DRAW, "clear", params.get(0), params.get(1), params.get(2)));
        return "null";
    }

    private String handleMath(LogicInstructionPipeline pipeline, String functionName, List<String> params) {
        String tmp = instructionProcessor.nextTemp();
        pipeline.emit(createInstruction(OP, functionName, tmp, params.get(0)));
        return tmp;
    }

    private String handleWithin(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol within x y radius result 0
        String tmp = instructionProcessor.nextTemp();
        pipeline.emit(createInstruction(UCONTROL, "within", params.get(0), params.get(1), params.get(2), tmp));
        return tmp;
    }

    private String handleGetBlock(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol getBlock x y resultType resultBuilding 0
        // TODO: either handle multiple return values, or provide a better abstraction over getBlock
        pipeline.emit(createInstruction(UCONTROL, "getBlock", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleBuild(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol build x y block rotation config
        pipeline.emit(createInstruction(UCONTROL, "build", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return "null";
    }

    private String handlePayTake(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol payTake takeUnits 0 0 0 0
        pipeline.emit(createInstruction(UCONTROL, "payTake", params.get(0)));
        return "null";
    }

    private String handlePayDrop(LogicInstructionPipeline pipeline) {
        // ucontrol payDrop 0 0 0 0 0
        pipeline.emit(createInstruction(UCONTROL, "payDrop"));
        return "null";
    }

    private String handleItemTake(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol itemTake from item amount 0 0
        pipeline.emit(createInstruction(UCONTROL, "itemTake", params.get(0), params.get(1), params.get(2)));
        return "null";
    }

    private String handleTargetp(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol targetp unit shoot 0 0 0
        pipeline.emit(createInstruction(UCONTROL, "targetp", params.get(0), params.get(1)));
        return "null";
    }

    private String handleTarget(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol target x y shoot 0 0
        pipeline.emit(createInstruction(UCONTROL, "target", params.get(0), params.get(1), params.get(2)));
        return "null";
    }

    private String handleBoost(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol boost enable 0 0 0 0
        pipeline.emit(createInstruction(UCONTROL, "boost", params.get(0)));
        return params.get(0);
    }

    private String handlePathfind(LogicInstructionPipeline pipeline) {
        // ucontrol pathfind 0 0 0 0 0
        pipeline.emit(createInstruction(UCONTROL, "pathfind"));
        return "null";
    }

    private String handleIdle(LogicInstructionPipeline pipeline) {
        // ucontrol idle 0 0 0 0 0
        pipeline.emit(createInstruction(UCONTROL, "idle"));
        return "null";
    }

    private String handleStop(LogicInstructionPipeline pipeline) {
        // ucontrol stop 0 0 0 0 0
        pipeline.emit(createInstruction(UCONTROL, "stop"));
        return "null";
    }

    private String handleApproach(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol approach x y radius 0 0
        pipeline.emit(createInstruction(UCONTROL, "approach", params.get(0), params.get(1), params.get(2)));
        return "null";
    }

    private String handleFlag(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol flag value 0 0 0 0
        pipeline.emit(createInstruction(UCONTROL, "flag", params.get(0)));
        return params.get(0);
    }

    private String handleItemDrop(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol itemDrop to amount 0 0 0
        pipeline.emit(createInstruction(UCONTROL, "itemDrop", params.get(0), params.get(1)));
        return "null";
    }

    private String handleMine(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol mine x y 0 0 0
        pipeline.emit(createInstruction(UCONTROL, "mine", params.get(0), params.get(1)));
        return "null";
    }

    private String handleGetlink(LogicInstructionPipeline pipeline, List<String> params) {
        // getlink result 0
        String tmp = instructionProcessor.nextTemp();
        pipeline.emit(createInstruction(GETLINK, tmp, params.get(0)));
        return tmp;
    }

    private boolean isRadarSearchProperty(String prop) {
        return List.of("attacker", "enemy", "ally", "player", "flying", "ground", "boss", "any").contains(prop);
    }
    private boolean isRadarSortbyOption(String sortby) {
        return List.of("distance", "health", "shield", "armor", "maxHealth").contains(sortby);
    }

    private String handleRadar(LogicInstructionPipeline pipeline, List<String> params) {
        // radar prop1 prop2 prop3 sortby target order out
        String tmp = instructionProcessor.nextTemp();
        final String prop1 = params.get(0);
        final String prop2 = params.get(1);
        final String prop3 = params.get(2);
        final String sortby = params.get(3);
        // Radar search properties should be hardcoded and can't be indirectly referenced. (Last test: v7.0.1.)
        if (!isRadarSearchProperty(prop1)) {
            throw new GenerationException("Invalid radar search property [" + prop1 + "]");
        }
        if (!isRadarSearchProperty(prop2)) {
            throw new GenerationException("Invalid radar search property [" + prop2 + "]");
        }
        if (!isRadarSearchProperty(prop3)) {
            throw new GenerationException("Invalid radar search property [" + prop3 + "]");
        }
        if (!isRadarSortbyOption(sortby)) {
            throw new GenerationException("Invalid radar sort option [" + sortby + "]");
        }
        pipeline.emit(createInstruction(RADAR, params.get(0), params.get(1), params.get(2), params.get(3), params.get(4), params.get(5), tmp));
        return tmp;
    }

    private String handleRand(LogicInstructionPipeline pipeline, List<String> params) {
        // op rand result 200 0
        String tmp = instructionProcessor.nextTemp();
        pipeline.emit(createInstruction(OP, "rand", tmp, params.get(0)));
        return tmp;
    }

    private String handleMove(LogicInstructionPipeline pipeline, List<String> params) {
        // ucontrol move 14 15 0 0 0
        pipeline.emit(createInstruction(UCONTROL, "move", params.get(0), params.get(1)));
        return "null";
    }

    private String handleUbind(LogicInstructionPipeline pipeline, List<String> params) {
        // ubind @poly
        pipeline.emit(createInstruction(UBIND, params.get(0)));
        return "null";
    }

    private String handlePrintflush(LogicInstructionPipeline pipeline, List<String> params) {
        params.forEach((param) -> pipeline.emit(createInstruction(PRINTFLUSH, param)));
        return "null";
    }

    private String handlePrint(LogicInstructionPipeline pipeline, List<String> params) {
        params.forEach((param) -> pipeline.emit(createInstruction(PRINT, param)));
        return params.get(params.size() - 1);
    }

    private String handleWait(LogicInstructionPipeline pipeline, List<String> params) {
        pipeline.emit(createInstruction(WAIT, params.get(0)));
        return "null";
    }
}
