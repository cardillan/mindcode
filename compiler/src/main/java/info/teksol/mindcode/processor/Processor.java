package info.teksol.mindcode.processor;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.logic.Opcode;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static info.teksol.mindcode.processor.ProcessorFlag.*;

/**
 * Mindustry Processor emulator.
 */
public class Processor {
    private final Set<ProcessorFlag> flags;
    private final Map<String, Variable> variables = new TreeMap<>();
    private final List<MindustryObject> blocks = new ArrayList<>();
    private final List<String> textBuffer = new ArrayList<>();
    private final Variable counter = new IntVariable("@counter", 0);
    private int steps = 0;

    public Processor() {
        flags = EnumSet.noneOf(ProcessorFlag.class);
        
        variables.put("@counter", counter);
        variables.put("null", new DoubleVariable(true, "null", null));
        variables.put("true", new IntVariable(true, "true", 1));
        variables.put("false", new IntVariable(true, "false", 0));
        variables.put("0", new IntVariable(true, "0", 0));
        variables.put("1", new IntVariable(true, "1", 1));
    }

    public void addBlock(MindustryObject block) {
        blocks.removeIf(b -> b.getName().equals(block.getName()));
        blocks.add(block);
        variables.put(block.getName(), new DoubleVariable(true, block.getName(), block));
    }

    public List<String> getTextBuffer() {
        return textBuffer;
    }

    public int getSteps() {
        return steps;
    }

    public boolean getFlag(ProcessorFlag flag) {
        return flags.contains(flag);
    }

    public void setFlag(ProcessorFlag flag, boolean value) {
        if (value) {
            flags.add(flag);
        } else {
            flags.remove(flag);
        }
    }

    public void run(List<LogicInstruction> program, int stepLimit) {
        if (!getFlag(STOP_PROCESSOR_OPTIONAL) && !program.stream().anyMatch(ix -> ix.getOpcode() == Opcode.STOP)) {
            throw new ExecutionException(STOP_PROCESSOR_OPTIONAL, "A stop instruction not present in given program.");            
        }

        steps = 0;
        textBuffer.clear();
        counter.setIntValue(0);
        variables.put("@links", new IntVariable(true, "@links", blocks.size()));

        while (steps < stepLimit) {
            try {
                int index = counter.getIntValue();
                if (index == program.size()) {
                    index = 0;
                    if (getFlag(ProcessorFlag.STOP_ON_PROGRAM_END)) {
                        break;
                    }
                }
                if (index < 0 || index > program.size()) {
                    counter.setIntValue(0);
                    throw new ExecutionException(ERR_INVALID_COUNTER, "Value of @counter (" + index + ") outside valid range (0 to " + program.size() + ")");
                }

                LogicInstruction instruction = program.get(index);

                if (false) {
                    if (instruction.getOpcode() == Opcode.PRINT) {
                        Variable var = getExistingVariable(instruction.getArg(0));
                        System.out.printf("Step: %d, counter: %d, instruction: %s *** Print %s%n", steps, index, instruction, var.toString());

                    } else {
                        System.out.printf("Step: %d, counter: %d, instruction: %s%n", steps, index, instruction);
                    }
                }

                steps++;
                counter.setIntValue(index + 1);
                if (!execute(instruction)) {
                    break;
                }
            } catch (ExecutionException ex) {
                if (!getFlag(ex.getFlag())) {
                    throw ex;
                }
            }
        }

        if (steps >= stepLimit) {
            throw new ExecutionException(ERR_EXECUTION_LIMIT_EXCEEDED, "Execution step limit of " + stepLimit + " exceeded");
        }
    }
    
    private static String arg(LogicInstruction ix, int index) {
        return index >= ix.getArgs().size() ? "0" : ix.getArg(index);
    }

    private boolean execute(LogicInstruction ix) {
        switch(ix.getOpcode()) {
            case READ:      return executeRead(arg(ix, 0), arg(ix, 1), arg(ix, 2));
            case WRITE:     return executeWrite(arg(ix, 0), arg(ix, 1), arg(ix, 2));
            case PRINT:     return executePrint(arg(ix, 0));
            case SET:       return executeSet(arg(ix, 0), arg(ix, 1));
            case OP:        return executeOp(arg(ix, 0), arg(ix, 1), arg(ix, 2), arg(ix, 3));
            case JUMP:      return executeJump(arg(ix, 0), arg(ix, 1), arg(ix, 2), arg(ix, 3));
            case END:       counter.setIntValue(0); return getFlag(ProcessorFlag.STOP_ON_END_INSTRUCTION);
            case STOP:      return false;
        }

        throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Unsupported opcode " + ix.getOpcode());
    }

    private boolean executeSet(String target, String value) {
        Variable targetVar = getOrCreateVariable(target);
        Variable valueVar = getExistingVariable(value);
        targetVar.assign(valueVar);
        return true;
    }

    private boolean executeOp(String operation, String target, String a, String b) {
        Variable targetVar = getOrCreateVariable(target);
        Variable aVar = getExistingVariable(a);
        Variable bVar = getExistingVariable(b);
        Operation op = ExpressionEvaluator.getOperation(operation);
        if (op == null) {
            throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Invalid op operation " + operation);
        }
        op.execute(targetVar, aVar, bVar);
        return true;
    }

    private boolean executeJump(String address, String condition, String a, String b) {
        Variable aVar = getExistingVariable(a);
        Variable bVar = getExistingVariable(b);
        Condition cond = CONDITIONS.get(condition);
        if (cond == null) {
            throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Invalid jump condition " + condition);
        }
        if (cond.evaluate(aVar, bVar)) {
            counter.setIntValue(Integer.parseInt(address));
        }
        return true;
    }

    private boolean executePrint(String value) {
        Variable var = getExistingVariable(value);
        textBuffer.add(var.toString());
        return true;
    }

    private boolean executeRead(String target, String block, String index) {
        Variable targetVar = getOrCreateVariable(target);
        Variable blockVar = getExistingVariable(block);
        Variable indexVar = getExistingVariable(index);
        targetVar.setDoubleValue(blockVar.getExistingObject().read(indexVar.getIntValue()));
        return true;
    }

    private boolean executeWrite(String source, String block, String index) {
        Variable sourceVar = getExistingVariable(source);
        Variable blockVar = getExistingVariable(block);
        Variable indexVar = getExistingVariable(index);
        blockVar.getExistingObject().write(indexVar.getIntValue(), sourceVar.getDoubleValue());
        return true;
    }

    private Variable getOrCreateVariable(String value) {
        return variables.computeIfAbsent(value, this::createVariable);
    }

    private Variable getExistingVariable(String value) {
        return variables.computeIfAbsent(value, this::createConstant);
    }

    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("^[_a-zA-Z][-a-zA-Z_0-9]*$");

    private DoubleVariable createVariable(String value) {
        if (VARIABLE_NAME_PATTERN.matcher(value).matches()) {
            return new DoubleVariable(value, null);
        } else {
            throw new ExecutionException(ERR_INVALID_IDENTIFIER, "Invalid identifier " + value);
        }
    }

    private Variable createConstant(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            String repl = value.substring(1, value.length() - 1).replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
            return new DoubleVariable(true, value, new MindustryObject(repl, repl));
        }
        try {
            return new DoubleVariable(true, value, value.startsWith("0x") ? Long.decode(value) : Double.parseDouble(value));
        } catch (NumberFormatException ex) {
            if (VARIABLE_NAME_PATTERN.matcher(value).matches()) {
                throw new ExecutionException(ERR_UNINITIALIZED_VAR, "Uninitialized variable " + value);
            } else {
                throw new ExecutionException(ERR_INVALID_IDENTIFIER, "Invalid number or identifier " + value);
            }
        }
    }

    private static final Map<String, Condition> CONDITIONS = createConditionsMap();

    private interface Condition {
        boolean evaluate(Variable a, Variable b);
    }

    private static Map<String, Condition> createConditionsMap() {
        Map<String, Condition> map = new HashMap<>();
        map.put("always",       (a, b) -> true);
        map.put("equal",        (a, b) -> ExpressionEvaluator.equals(a, b));
        map.put("notEqual",     (a, b) -> !ExpressionEvaluator.equals(a, b));
        map.put("land",         (a, b) -> a.getDoubleValue() != 0 && b.getDoubleValue() != 0);
        map.put("lessThan",     (a, b) -> a.getDoubleValue() < b.getDoubleValue());
        map.put("lessThanEq",   (a, b) -> a.getDoubleValue() <= b.getDoubleValue());
        map.put("greaterThan",  (a, b) -> a.getDoubleValue() > b.getDoubleValue());
        map.put("greaterThanEq",(a, b) -> a.getDoubleValue() >= b.getDoubleValue());
        map.put("strictEqual",  (a, b) -> a.isObject() == b.isObject() && ExpressionEvaluator.equals(a, b));
        return map;
    }
}
