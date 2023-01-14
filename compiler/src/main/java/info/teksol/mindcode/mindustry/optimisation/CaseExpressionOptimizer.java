package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.Iterator;
import java.util.List;

// Removes unncessary case expression variable (__ast*) and replaces it with the original variable containing
// the value of the case expresion. The set instruction is removed, while the other instructions (one jump 
// instruction per "when" branch) are updated to replace the ast variable with the one used in the set statement.
// The optimization is performed only when the following conditions are met:
// 1. The set instruction assigns to an __ast variable.
// 2. The set instruction is the first of all those using the __ast variable (the check is based on absolute
//    instruction sequence in the program, not on the actual program flow).
// 3. Each subsequent instruction using the __ast variable conforms to the code generated by the compiler
//    (ie. has the form of jump target notEqual __astX testValue)
class CaseExpressionOptimizer extends GlobalOptimizer {
    public CaseExpressionOptimizer(LogicInstructionPipeline next) {
        super(next);
    }

    @Override
    protected void optimizeProgram() {
        for (Iterator<LogicInstruction> it = program.iterator(); it.hasNext(); ) {
            LogicInstruction instruction = it.next();
            if (!instruction.isSet()) continue;
            
            String arg0 = instruction.getArgs().get(0);
            // Not an __ast variable
            if (!arg0.startsWith(AstNodeBuilder.AST_PREFIX)) continue;

            String arg1 = instruction.getArgs().get(1);
            List<LogicInstruction> list = findInstructions(ix -> ix.getArgs().contains(arg0));
            // The set instruciton is not the first one
            if (list.get(0) != instruction) continue;
            
            // Some of the other instructions aren't part of the case expression
            if (!list.stream().skip(1).allMatch(ix -> isStandardCaseWhenInstruction(ix, arg0))) continue;
            
            // Replace __ast with actual value in all case branches
            list.stream().skip(1).forEach(ix -> replaceInstruction(ix, replaceAllArgs(ix, arg0, arg1)));
            it.remove();
        }
    }
    
    private boolean isStandardCaseWhenInstruction(LogicInstruction ix, String ast) {
        return ix.isJump() && ix.getArgs().get(1).equals("notEqual") && ix.getArgs().get(2).equals(ast);
    }
}
