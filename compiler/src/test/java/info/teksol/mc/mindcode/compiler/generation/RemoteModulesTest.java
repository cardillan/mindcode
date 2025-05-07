package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.common.InputFiles;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
public class RemoteModulesTest extends AbstractCodeGeneratorTest {

    @Override
    protected InputFiles createInputFiles(String source) {
        InputFiles inputFiles = InputFiles.fromSource(source);

        inputFiles.addPackagedFile(
                "remote.mnd",
                """
                        module test;
                        
                        var cnt = 0;
                        remote var x = 0;
                        
                        remote def foo(in a, out count)
                            count = ++cnt;
                            return a + x;
                        end;
                        
                        remote void bar(in a, out b)
                            b = sin(a);
                        end;
                        """);

        inputFiles.addPackagedFile(
                "remote2.mnd",
                """
                        module test2;
                        
                        remote array[10];
                        
                        remote def baz(a, b, c)
                            return len(len(a * a + b * b), c * c);
                        end;
                        """);

        inputFiles.addPackagedFile(
                "conflict.mnd",
                """
                        module test2;
                        
                        remote def foo()
                            print("Hey!");
                        end;
                        """);

        return inputFiles;
    }

    @Nested
    class Basics {
        @Test
        void compilesSynchronousCall() {
            assertCompilesTo("""
                            require "remote.mnd" remote processor1;
                            z = foo(10, out a);
                            print(z, a);
                            """,
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(1), "processor1", q("*signature")),
                    createInstruction(JUMP, label(2), "notEqual", tmp(1), q("fe196785ecf7cd23:v1")),
                    createInstruction(WRITE, "10", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "false", "processor1", q(":foo*finished")),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(LABEL, label(3)),
                    createInstruction(WAIT, "1e-15"),
                    createInstruction(READ, tmp(4), "processor1", q(":foo*finished")),
                    createInstruction(JUMP, label(3), "equal", tmp(4), "false"),
                    createInstruction(READ, tmp(3), "processor1", q(":foo:count")),
                    createInstruction(SET, ":a", tmp(3)),
                    createInstruction(READ, tmp(5), "processor1", q(":foo*retval")),
                    createInstruction(SET, ":z", tmp(5)),
                    createInstruction(PRINT, ":z"),
                    createInstruction(PRINT, ":a")
            );
        }

        @Test
        void compilesAsynchronousCall() {
            assertCompilesTo("""
                            require "remote.mnd" remote processor1;
                            async(foo(10));
                            print(finished(foo));
                            z = await(foo);
                            print(z, foo.count);
                            """,
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(1), "processor1", q("*signature")),
                    createInstruction(JUMP, label(2), "notEqual", tmp(1), q("fe196785ecf7cd23:v1")),
                    createInstruction(WRITE, "10", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "false", "processor1", q(":foo*finished")),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(READ, tmp(5), "processor1", q(":foo*finished")),
                    createInstruction(PRINT, tmp(5)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(WAIT, "1e-15"),
                    createInstruction(READ, tmp(6), "processor1", q(":foo*finished")),
                    createInstruction(JUMP, label(3), "equal", tmp(6), "false"),
                    createInstruction(READ, tmp(7), "processor1", q(":foo*retval")),
                    createInstruction(SET, ":z", tmp(7)),
                    createInstruction(PRINT, ":z"),
                    createInstruction(READ, tmp(8), "processor1", q(":foo:count")),
                    createInstruction(PRINT, tmp(8))
            );
        }

        @Test
        void compilesMultipleModules() {
            assertCompilesTo("""
                            require "remote.mnd" remote processor1;
                            require "remote2.mnd" remote processor2;
                            y = baz(10, 20, 30);
                            z = foo(10, out a);
                            print(y, z, a);
                            """,
                    createInstruction(LABEL, label(3)),
                    createInstruction(READ, tmp(11), "processor1", q("*signature")),
                    createInstruction(JUMP, label(3), "notEqual", tmp(11), q("fe196785ecf7cd23:v1")),
                    createInstruction(LABEL, label(4)),
                    createInstruction(READ, tmp(14), "processor2", q("*signature")),
                    createInstruction(JUMP, label(4), "notEqual", tmp(14), q("32deff56ad045d71:v1")),
                    createInstruction(WRITE, "10", "processor2", q(":baz:a")),
                    createInstruction(WRITE, "20", "processor2", q(":baz:b")),
                    createInstruction(WRITE, "30", "processor2", q(":baz:c")),
                    createInstruction(WRITE, "false", "processor2", q(":baz*finished")),
                    createInstruction(WRITE, "1", "processor2", q("@counter")),
                    createInstruction(LABEL, label(5)),
                    createInstruction(WAIT, "1e-15"),
                    createInstruction(READ, tmp(18), "processor2", q(":baz*finished")),
                    createInstruction(JUMP, label(5), "equal", tmp(18), "false"),
                    createInstruction(READ, tmp(19), "processor2", q(":baz*retval")),
                    createInstruction(SET, ":y", tmp(19)),
                    createInstruction(WRITE, "10", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "false", "processor1", q(":foo*finished")),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(LABEL, label(6)),
                    createInstruction(WAIT, "1e-15"),
                    createInstruction(READ, tmp(20), "processor1", q(":foo*finished")),
                    createInstruction(JUMP, label(6), "equal", tmp(20), "false"),
                    createInstruction(READ, tmp(13), "processor1", q(":foo:count")),
                    createInstruction(SET, ":a", tmp(13)),
                    createInstruction(READ, tmp(21), "processor1", q(":foo*retval")),
                    createInstruction(SET, ":z", tmp(21)),
                    createInstruction(PRINT, ":y"),
                    createInstruction(PRINT, ":z"),
                    createInstruction(PRINT, ":a")
            );
        }

        @Test
        void compilesRemoteArrayAccess() {
            assertCompilesTo("""
                            require "remote2.mnd" remote processor1;
                            for out i in array[0..2] do
                                i = rand(100);
                            end;
                            array[floor(rand(10))]++;
                            print(array[2..4]);
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(READ, tmp(10), "processor1", q("*signature")),
                    createInstruction(JUMP, label(1), "notEqual", tmp(10), q("32deff56ad045d71:v1")),
                    createInstruction(READ, tmp(0), "processor1", q(".array*0")),
                    createInstruction(SET, ":i", tmp(0)),
                    createInstruction(SETADDR, tmp(11), label(5)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(5)),
                    createInstruction(WRITE, ":i", "processor1", q(".array*0")),
                    createInstruction(READ, tmp(1), "processor1", q(".array*1")),
                    createInstruction(SET, ":i", tmp(1)),
                    createInstruction(SETADDR, tmp(11), label(6)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(6)),
                    createInstruction(WRITE, ":i", "processor1", q(".array*1")),
                    createInstruction(READ, tmp(2), "processor1", q(".array*2")),
                    createInstruction(SET, ":i", tmp(2)),
                    createInstruction(SETADDR, tmp(11), label(7)),
                    createInstruction(LABEL, label(2)),
                    createInstruction(OP, "rand", tmp(12), "100"),
                    createInstruction(SET, ":i", tmp(12)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(MULTIJUMP, tmp(11), "0", "0"),
                    createInstruction(MULTILABEL, label(7)),
                    createInstruction(WRITE, ":i", "processor1", q(".array*2")),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "rand", tmp(13), "10"),
                    createInstruction(OP, "floor", tmp(14), tmp(13)),
                    createInstruction(SET, tmp(15), tmp(14)),
                    createInstruction(READARR, tmp(16), ".array[]", tmp(15)),
                    createInstruction(SET, tmp(17), tmp(16)),
                    createInstruction(OP, "add", tmp(16), tmp(16), "1"),
                    createInstruction(WRITEARR, tmp(16), ".array[]", tmp(15)),
                    createInstruction(READ, tmp(2), "processor1", q(".array*2")),
                    createInstruction(PRINT, tmp(2)),
                    createInstruction(READ, tmp(3), "processor1", q(".array*3")),
                    createInstruction(PRINT, tmp(3)),
                    createInstruction(READ, tmp(18), "processor1", q(".array*4")),
                    createInstruction(PRINT, tmp(18))
            );
        }

    }

    @Nested
    class Errors {

        @Test
        void refusesAsyncOnLogicFunction() {
            assertGeneratesMessage(
                    "Function 'printflush' cannot be called asynchronously.",
                    "async(printflush(message1));"
            );
        }

        @Test
        void refusesAsyncOnLibraryFunction() {
            assertGeneratesMessage(
                    "Function 'print' cannot be called asynchronously.",
                    "async(print(10));"
            );
        }

        @Test
        void refusesAsyncOnAsyncFunction() {
            assertGeneratesMessage(
                    "Function 'async' cannot be called asynchronously.",
                    """
                            require "remote.mnd" remote processor1;
                            async(async(foo(10)));"""
            );
        }

        @Test
        void refusesAsyncOnExpression() {
            assertGeneratesMessage(
                    "The 'async' function requires a call of a remote function as an argument.",
                    "async(10);"
            );
        }

        @Test
        void refusesConflictingRequires() {
            assertGeneratesMessage(
                    "Multiple instantiations of file 'File remote.mnd'.",
                    """
                            require "remote.mnd" remote processor1;
                            require "remote.mnd" remote processor2;
                            """
            );
        }

        @Test
        void refusesConflictingRemoteFunctions() {
            assertGeneratesMessage(
                    "Remote function 'foo(a)' conflicts with remote function 'foo()': names of remote functions must be unique.",
                    """
                            remote def foo() end;
                            remote def foo(a) end;
                            """
            );
        }

        @Test
        void refusesLocalCallToRemoteFunction() {
            assertGeneratesMessage(
                    "Cannot call remote function 'foo' locally.",
                    """
                            remote def foo() end;
                            print(foo());
                            """
            );
        }

        @Test
        void refusesMultipleRemoteFunctions() {
            assertGeneratesMessage(
                    "Remote function 'foo(in a, out count)' conflicts with remote function 'foo()': names of remote functions must be unique.",
                    """
                            require "remote.mnd" remote processor1;
                            require "conflict.mnd" remote processor1;
                            foo();
                            """
            );
        }

        @Test
        void refusesUnknownParameter() {
            assertGeneratesMessage(
                    "Function 'foo': unknown output parameter 'bar'.",
                    """
                            require "remote.mnd" remote processor1;
                            async(foo(10));
                            await(foo);
                            print(foo.bar);
                            """
            );
        }

        @Test
        void refusesWrongAwaitParameter() {
            assertGeneratesMessage(
                    "The 'await' function requires a remote function name as an argument.",
                    """
                            await(print);
                            """
            );
        }

        @Test
        void refusesWrongFinishedParameter() {
            assertGeneratesMessage(
                    "The 'finished' function requires a remote function name as an argument.",
                    """
                            finished(print);
                            """
            );
        }

        @Test
        void refusesRemoteVariablesInLocalScope() {
            assertGeneratesMessage(
                    "Local variable cannot be declared 'remote'.",
                    """
                            begin
                                remote x = 10;
                            end;
                            """
            );
        }

        @Test
        void refusesGlobalVariablesCollidingWithRemoteFunctions() {
            assertGeneratesMessage(
                    "Multiple declarations of 'foo'.",
                    """
                            require "remote.mnd" remote processor1;
                            foo(10);
                            var foo;
                            """
            );
        }

        @Test
        void refusesOutputParametersInAsyncCall() {
            assertGeneratesMessage(
                    "Function 'foo': asynchronous calls to remote function cannot take output arguments.",
                    """
                            require "remote.mnd" remote processor1;
                            async(foo(10, out a));
                            """
            );
        }
    }
}
