package info.teksol.mindcode.cmdline;

import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.profile.*;
import info.teksol.mc.profile.options.Target;
import info.teksol.mindcode.cmdline.Main.Action;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
public abstract class AbstractCommandLineTest {
    protected final Action action;

    protected AbstractCommandLineTest(Action action) {
        this.action = action;
    }

    protected Namespace parseCommandLine(String commandLine) throws ArgumentParserException {
        ArgumentParser parser = Main.createArgumentParser(Arguments.fileType(), 79);
        String args = action.getShortcut() + " " + commandLine;
        return parser.parseArgs(args.split("\\s+"));
    }

    protected CompilerProfile parseToProfile(String commandLine) throws ArgumentParserException {
        Namespace arguments = parseCommandLine(commandLine);
        return action.createCompilerProfile(arguments);
    }

    protected File resolveOutputFile(Namespace arguments, String extension) {
        return ActionHandler.resolveOutputFile(arguments.get("input"),
                arguments.get("output-directory"), arguments.get("output"), extension);
    }

    protected File resolveLogFile(Namespace arguments, String extension) {
        return ActionHandler.resolveOutputFile(arguments.get("input"),
                arguments.get("output-directory"), arguments.get("log"), extension);
    }

    @Nested
    class ActionHandlerCompilerProfileTest {

        @Nested
        class TargetArgumentTest {
            @Test
            public void shortArgument6() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-t 6");
                assertEquals(new Target("6"), profile.getTarget());
            }

            @Test
            public void longArgument7_0W() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-t 7.0w");
                assertEquals(new Target("7.0w"), profile.getTarget());
            }
        }

        @Nested
        class BuiltinEvaluationArgumentTest {
            @Test
            public void longArgumentNone() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--builtin-evaluation none");
                assertEquals(BuiltinEvaluation.NONE, profile.getBuiltinEvaluation());
            }

            @Test
            public void longArgumentCompatible() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--builtin-evaluation compatible");
                assertEquals(BuiltinEvaluation.COMPATIBLE, profile.getBuiltinEvaluation());
            }

            @Test
            public void longArgumentFull() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--builtin-evaluation full");
                assertEquals(BuiltinEvaluation.FULL, profile.getBuiltinEvaluation());
            }
        }

        @Nested
        class TargetGuardArgumentTest {
            @Test
            public void longArgumentTrue() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--target-guard true");
                assertTrue(profile.isTargetGuard());
            }

            @Test
            public void longArgumentFalse() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--target-guard false");
                assertFalse(profile.isTargetGuard());
            }
        }

        @Nested
        class UnsafeCaseOptimizationArgumentTest {
            @Test
            public void longArgumentTrue() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--unsafe-case-optimization true");
                assertTrue(profile.isUnsafeCaseOptimization());
            }

            @Test
            public void longArgumentFalse() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--unsafe-case-optimization false");
                assertFalse(profile.isUnsafeCaseOptimization());
            }
        }

        @Nested
        class MlogBlockOptimizationArgumentTest {
            @Test
            public void longArgumentTrue() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--mlog-block-optimization true");
                assertTrue(profile.isMlogBlockOptimization());
            }

            @Test
            public void longArgumentFalse() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--mlog-block-optimization false");
                assertFalse(profile.isMlogBlockOptimization());
            }
        }

        @Nested
        class TextJumpTablesArgumentTest {
            @Test
            public void longArgumentTrue() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--text-jump-tables true");
                assertTrue(profile.isTextJumpTables());
            }

            @Test
            public void longArgumentFalse() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--text-jump-tables false");
                assertFalse(profile.isTextJumpTables());
            }
        }

        @Nested
        class NullCounterIsNoopArgumentTest {
            @Test
            public void longArgumentTrue() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--null-counter-is-noop true");
                assertTrue(profile.isNullCounterIsNoop());
            }

            @Test
            public void longArgumentFalse() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--null-counter-is-noop false");
                assertFalse(profile.isNullCounterIsNoop());
            }
        }

        @Nested
        class SyntaxArgumentTest {
            @Test
            public void shortArgumentRelaxed() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-y relaxed");
                assertEquals(SyntacticMode.RELAXED, profile.getSyntacticMode());
            }

            @Test
            public void shortArgumentMixed() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-y mixed");
                assertEquals(SyntacticMode.MIXED, profile.getSyntacticMode());
            }

            @Test
            public void shortArgumentStrict() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-y strict");
                assertEquals(SyntacticMode.STRICT, profile.getSyntacticMode());
            }

            @Test
            public void longArgumentRelaxed() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--syntax relaxed");
                assertEquals(SyntacticMode.RELAXED, profile.getSyntacticMode());
            }

            @Test
            public void longArgumentMixed() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--syntax mixed");
                assertEquals(SyntacticMode.MIXED, profile.getSyntacticMode());
            }

            @Test
            public void longArgumentStrict() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--syntax strict");
                assertEquals(SyntacticMode.STRICT, profile.getSyntacticMode());
            }
        }

        @Nested
        class InstructionLimitArgumentTest {
            @Test
            public void shortArgument() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-i 500");
                assertEquals(500, profile.getInstructionLimit());
            }

            @Test
            public void longArgument() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--instruction-limit 999");
                assertEquals(999, profile.getInstructionLimit());
            }
        }

        @Nested
        class GoalArgumentTest {
            @Test
            public void shortArgumentSize() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-g size");
                assertEquals(GenerationGoal.SIZE, profile.getGoal());
            }

            @Test
            public void shortArgumentSpeed() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-g speed");
                assertEquals(GenerationGoal.SPEED, profile.getGoal());
            }

            @Test
            public void shortArgumentAuto() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-g neutral");
                assertEquals(GenerationGoal.NEUTRAL, profile.getGoal());
            }

            @Test
            public void longArgumentSize() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--goal size");
                assertEquals(GenerationGoal.SIZE, profile.getGoal());
            }

            @Test
            public void longArgumentSpeed() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--goal speed");
                assertEquals(GenerationGoal.SPEED, profile.getGoal());
            }

            @Test
            public void longArgumentAuto() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--goal neutral");
                assertEquals(GenerationGoal.NEUTRAL, profile.getGoal());
            }
        }

        @Nested
        class PassesArgumentTest {
            @Test
            public void shortArgument() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-e 50");
                assertEquals(50, profile.getOptimizationPasses());
            }

            @Test
            public void longArgument() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--passes 100");
                assertEquals(100, profile.getOptimizationPasses());
            }
        }

        @Nested
        class RemarksArgumentTest {
            @Test
            public void shortArgumentNone() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-r none");
                assertEquals(Remarks.NONE, profile.getRemarks());
            }

            @Test
            public void shortArgumentComments() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-r comments");
                assertEquals(Remarks.COMMENTS, profile.getRemarks());
            }

            @Test
            public void shortArgumentActive() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-r active");
                assertEquals(Remarks.ACTIVE, profile.getRemarks());
            }

            @Test
            public void shortArgumentPassive() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-r passive");
                assertEquals(Remarks.PASSIVE, profile.getRemarks());
            }

            @Test
            public void longArgumentNone() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--remarks none");
                assertEquals(Remarks.NONE, profile.getRemarks());
            }

            @Test
            public void longArgumentComments() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--remarks comments");
                assertEquals(Remarks.COMMENTS, profile.getRemarks());
            }

            @Test
            public void longArgumentActive() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--remarks active");
                assertEquals(Remarks.ACTIVE, profile.getRemarks());
            }

            @Test
            public void longArgumentPassive() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--remarks passive");
                assertEquals(Remarks.PASSIVE, profile.getRemarks());
            }
        }

        @Nested
        class SymbolicLabelsArgumentTest {
            @Test
            public void longArgument() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--symbolic-labels");
                assertTrue(profile.isSymbolicLabels());
            }

            @Test
            public void longArgumentTrue() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--symbolic-labels true");
                assertTrue(profile.isSymbolicLabels());
            }

            @Test
            public void longArgumentFalse() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--symbolic-labels false");
                assertFalse(profile.isSymbolicLabels());
            }
        }

        @Nested
        class MlogIndentTest {
            @Test
            public void defaultValueForSymbolicLabels() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--symbolic-labels true");
                assertEquals(4, profile.getMlogIndent());
            }

            @Test
            public void defaultValueForDirectAddressing() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--symbolic-labels false");
                assertEquals(0, profile.getMlogIndent());
            }

            @Test
            public void longArgument() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--mlog-indent 3");
                assertEquals(3, profile.getMlogIndent());
            }
        }

        @Nested
        class BoundaryArgumentTest {
            @Test
            public void longArgumentNone() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--boundary-checks none");
                assertEquals(RuntimeChecks.NONE, profile.getBoundaryChecks());
            }

            @Test
            public void longArgumentAssert() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--boundary-checks assert");
                assertEquals(RuntimeChecks.ASSERT, profile.getBoundaryChecks());
            }

            @Test
            public void longArgumentMinimal() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--boundary-checks minimal");
                assertEquals(RuntimeChecks.MINIMAL, profile.getBoundaryChecks());
            }

            @Test
            public void longArgumentSimple() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--boundary-checks simple");
                assertEquals(RuntimeChecks.SIMPLE, profile.getBoundaryChecks());
            }

            @Test
            public void longArgumentDescribed() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--boundary-checks described");
                assertEquals(RuntimeChecks.DESCRIBED, profile.getBoundaryChecks());
            }
        }

        @Nested
        class FunctionPrefixArgumentTest {
            @Test
            public void longArgumentShort() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--function-prefix short");
                assertTrue(profile.isShortFunctionPrefix());
            }

            @Test
            public void longArgumentLong() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--function-prefix long");
                assertFalse(profile.isShortFunctionPrefix());
            }
        }

        @Nested
        class NoSignatureArgumentTest {
            @Test
            public void longArgumentPresent() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--no-signature");
                assertFalse(profile.isSignature());
            }

            @Test
            public void longArgumentAbsent() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("");
                assertTrue(profile.isSignature());
            }
        }

        @Nested
        class PrintflushArgumentTest {
            @Test
            public void longArgumentTrue() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--auto-printflush true");
                assertTrue(profile.isAutoPrintflush());
            }

            @Test
            public void longArgumentFalse() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--auto-printflush false");
                assertFalse(profile.isAutoPrintflush());
            }
        }

        @Nested
        class SortVariablesArgumentTest {
            @Test
            public void sortVariablesArgumentMissing() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("");
                assertEquals(List.of(SortCategory.NONE), profile.getSortVariables());
            }

            @Test
            public void sortVariablesArgumentEmpty() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--sort-variables");
                assertEquals(List.of(SortCategory.values()), profile.getSortVariables());
            }

            @Test
            public void sortVariablesArgumentSpecific() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--sort-variables params globals");
                assertEquals(List.of(SortCategory.PARAMS, SortCategory.GLOBALS), profile.getSortVariables());
            }
        }

        @Nested
        class OptimizationLevelsArgumentTest {
            @Test
            public void shortArgumentNone() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-O0");
                assertEquals(OptimizationLevel.NONE, profile.getOptimizationLevel(Optimization.JUMP_OPTIMIZATION));
            }

            @Test
            public void shortArgumentBasic() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-O1");
                assertEquals(OptimizationLevel.BASIC, profile.getOptimizationLevel(Optimization.JUMP_OPTIMIZATION));
            }

            @Test
            public void shortArgumentAdvanced() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-O2");
                assertEquals(OptimizationLevel.ADVANCED, profile.getOptimizationLevel(Optimization.JUMP_OPTIMIZATION));
            }

            @Test
            public void shortArgumentExperimental() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-03");
                assertEquals(OptimizationLevel.EXPERIMENTAL, profile.getOptimizationLevel(Optimization.JUMP_OPTIMIZATION));
            }

            @Test
            public void specificOptimizationArgumentsNone() throws ArgumentParserException {
                String cmdLine = Optimization.LIST.stream().map(o -> "--" + o.getOptionName() + " none")
                        .collect(Collectors.joining(" "));

                CompilerProfile profile = parseToProfile(cmdLine);
                List<OptimizationLevel> expected = Collections.nCopies(Optimization.LIST.size(), OptimizationLevel.NONE);
                assertTrue(Optimization.LIST.stream().map(profile::getOptimizationLevel).allMatch(OptimizationLevel.NONE::equals));
            }

            @Test
            public void specificOptimizationArgumentsBasic() throws ArgumentParserException {
                String cmdLine = Optimization.LIST.stream().map(o -> "--" + o.getOptionName() + " basic")
                        .collect(Collectors.joining(" "));

                CompilerProfile profile = parseToProfile(cmdLine);
                List<OptimizationLevel> expected = Collections.nCopies(Optimization.LIST.size(), OptimizationLevel.BASIC);
                assertTrue(Optimization.LIST.stream().map(profile::getOptimizationLevel).allMatch(OptimizationLevel.BASIC::equals));
            }
        }

        @Nested
        class FileReferencesArgumentTest {
            @Test
            public void longArgumentPath() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--file-references path");
                assertEquals(FileReferences.PATH, profile.getFileReferences());
            }

            @Test
            public void longArgumentUri() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--file-references uri");
                assertEquals(FileReferences.URI, profile.getFileReferences());
            }

            @Test
            public void longArgumentWindowsUri() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--file-references windows-uri");
                assertEquals(FileReferences.WINDOWS_URI, profile.getFileReferences());
            }
        }

        @Nested
        class ParseTreeArgumentTest {
            @Test
            public void shortArgument() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-p 1");
                assertEquals(1, profile.getParseTreeLevel());
            }

            @Test
            public void longArgument() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--parse-tree 1");
                assertEquals(1, profile.getParseTreeLevel());
            }
        }

        @Nested
        class DebugMessagesArgumentTest {
            @Test
            public void shortArgument() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-d 1");
                assertEquals(1, profile.getDebugMessages());
            }

            @Test
            public void longArgument() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--debug-messages 1");
                assertEquals(1, profile.getDebugMessages());
            }
        }

        @Nested
        class PrintUnresolvedArgumentTest {
            @Test
            public void shortArgumentNone() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-u none");
                assertEquals(FinalCodeOutput.NONE, profile.getFinalCodeOutput());
            }

            @Test
            public void shortArgumentPlain() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-u plain");
                assertEquals(FinalCodeOutput.PLAIN, profile.getFinalCodeOutput());
            }

            @Test
            public void shortArgumentSource() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-u source");
                assertEquals(FinalCodeOutput.SOURCE, profile.getFinalCodeOutput());
            }

            @Test
            public void shortArgumentFlatAst() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-u flat-ast");
                assertEquals(FinalCodeOutput.FLAT_AST, profile.getFinalCodeOutput());
            }

            @Test
            public void shortArgumentDeepAst() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-u deep-ast");
                assertEquals(FinalCodeOutput.DEEP_AST, profile.getFinalCodeOutput());
            }

            @Test
            public void longArgumentNone() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--print-unresolved none");
                assertEquals(FinalCodeOutput.NONE, profile.getFinalCodeOutput());
            }

            @Test
            public void longArgumentPlain() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--print-unresolved plain");
                assertEquals(FinalCodeOutput.PLAIN, profile.getFinalCodeOutput());
            }

            @Test
            public void longArgumentSource() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--print-unresolved source");
                assertEquals(FinalCodeOutput.SOURCE, profile.getFinalCodeOutput());
            }

            @Test
            public void longArgumentFlatAst() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--print-unresolved flat-ast");
                assertEquals(FinalCodeOutput.FLAT_AST, profile.getFinalCodeOutput());
            }

            @Test
            public void longArgumentDeepAst() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--print-unresolved deep-ast");
                assertEquals(FinalCodeOutput.DEEP_AST, profile.getFinalCodeOutput());
            }
        }

        @Nested
        class StacktraceArgumentTest {
            @Test
            public void shortArgumentAbsent() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("");
                assertFalse(profile.isPrintStackTrace());
            }

            @Test
            public void shortArgumentPresent() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-s");
                assertTrue(profile.isPrintStackTrace());
            }

            @Test
            public void longArgumentAbsent() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("");
                assertFalse(profile.isPrintStackTrace());
            }

            @Test
            public void longArgumentPresent() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--stacktrace");
                assertTrue(profile.isPrintStackTrace());
            }
        }
    }
}
