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
                "local.mnd",
                """
                        module local;
                        
                        require "remote.mnd" remote processor1;
                        
                        def localFoo(in a, out count)
                            print("Before foo");
                            var result = processor1.foo(a, out count);
                            print("After foo");
                            result;
                        end;
                        """);

        inputFiles.addPackagedFile(
                "remote.mnd",
                """
                        module test;
                        
                        var cnt = 0;
                        export var x = 0;
                        
                        export def foo(in a, out count)
                            count = ++cnt;
                            return a + x;
                        end;
                        
                        export void bar(in a, out b)
                            b = sin(a);
                        end;
                        """);

        inputFiles.addPackagedFile(
                "remote2.mnd",
                """
                        module test2;
                        
                        export array[10];
                        
                        export def baz(a, b, c)
                            return len(len(a * a + b * b), c * c);
                        end;
                        """);

        inputFiles.addPackagedFile(
                "conflict.mnd",
                """
                        module test2;
                        
                        export def foo()
                            print("Hey!");
                        end;
                        """);

        inputFiles.addPackagedFile(
                "relaxed1.mnd",
                """
                        module test;
                        print("foo");
                        """);

        inputFiles.addPackagedFile(
                "relaxed2.mnd",
                """
                        #set syntax = relaxed;
                        module test;
                        print("foo");
                        """);

        inputFiles.addPackagedFile(
                "target.mnd",
                """
                        #set target = 8.0w;
                        module test;
                        void foo() print("foo"); end;
                        """);

        return inputFiles;
    }

    @Nested
    class Basics {
        @Test
        void compilesSynchronousCall() {
            assertCompilesTo("""
                            require "remote.mnd" remote processor1;
                            z = processor1.foo(10, out a);
                            print(z, a);
                            """,
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(1), "processor1", q("*signature")),
                    createInstruction(JUMP, label(2), "notEqual", tmp(1), q("361567e89c3ad027:v1")),
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
                            async(processor1.foo(10));
                            print(finished(processor1.foo));
                            z = await(processor1.foo);
                            print(z, processor1.foo.count);
                            """,
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(1), "processor1", q("*signature")),
                    createInstruction(JUMP, label(2), "notEqual", tmp(1), q("361567e89c3ad027:v1")),
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
        void compilesFinished() {
            assertCompilesTo("""
                            require "remote.mnd" remote processor1;
                            async(processor1.foo(10));
                            do while !finished(processor1.foo, out z);
                            print(z, processor1.foo.count);
                            """,
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(8), "processor1", q("*signature")),
                    createInstruction(JUMP, label(2), "notEqual", tmp(8), q("361567e89c3ad027:v1")),
                    createInstruction(WRITE, "10", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "false", "processor1", q(":foo*finished")),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(LABEL, label(3)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(READ, tmp(9), "processor1", q(":foo*finished")),
                    createInstruction(READ, tmp(10), "processor1", q(":foo*retval")),
                    createInstruction(SET, ":z", tmp(10)),
                    createInstruction(OP, "equal", tmp(11), tmp(9), "false"),
                    createInstruction(JUMP, label(3), "notEqual", tmp(11), "false"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(PRINT, ":z"),
                    createInstruction(READ, tmp(12), "processor1", q(":foo:count")),
                    createInstruction(PRINT, tmp(12))
            );
        }

        @Test
        void compilesMultipleModules() {
            assertCompilesTo("""
                            require "remote.mnd" remote processor1;
                            require "remote2.mnd" remote processor2;
                            y = processor2.baz(10, 20, 30);
                            z = processor1.foo(10, out a);
                            print(y, z, a);
                            """,
                    createInstruction(LABEL, label(3)),
                    createInstruction(READ, tmp(11), "processor1", q("*signature")),
                    createInstruction(JUMP, label(3), "notEqual", tmp(11), q("361567e89c3ad027:v1")),
                    createInstruction(LABEL, label(4)),
                    createInstruction(READ, tmp(14), "processor2", q("*signature")),
                    createInstruction(JUMP, label(4), "notEqual", tmp(14), q("ccc0ff41cc7e907b:v1")),
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
        void compilesVerifySignature() {
            assertCompilesTo("""
                            require "remote.mnd" remote PROC;
                            PROC = getlink(0);
                            if verifySignature(PROC) then
                                async(PROC.foo(10));
                            end;
                            """,
                    createInstruction(GETLINK, tmp(6), "0"),
                    createInstruction(SET, ".proc", tmp(6)),
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(7), ".proc", q("*signature")),
                    createInstruction(JUMP, label(2), "equal", tmp(7), "null"),
                    createInstruction(OP, "equal", tmp(8), tmp(7), q("361567e89c3ad027:v1")),
                    createInstruction(JUMP, label(3), "equal", tmp(8), "false"),
                    createInstruction(WRITE, "10", ".proc", q(":foo:a")),
                    createInstruction(WRITE, "false", ".proc", q(":foo*finished")),
                    createInstruction(WRITE, "2", ".proc", q("@counter")),
                    createInstruction(SET, tmp(9), "null"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(9), "null"),
                    createInstruction(LABEL, label(4))
            );
        }

        @Test
        void compilesMultipleInstantiations() {
            assertCompilesTo("""
                            require "remote.mnd" remote processor1, processor2;
                            y = processor1.foo(10, out a);
                            z = processor2.foo(11, out b);
                            print(y, z, a, b);
                            """,
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(5), "processor1", q("*signature")),
                    createInstruction(JUMP, label(2), "notEqual", tmp(5), q("361567e89c3ad027:v1")),
                    createInstruction(LABEL, label(3)),
                    createInstruction(READ, tmp(11), "processor2", q("*signature")),
                    createInstruction(JUMP, label(3), "notEqual", tmp(11), q("361567e89c3ad027:v1")),
                    createInstruction(WRITE, "10", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "false", "processor1", q(":foo*finished")),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(LABEL, label(4)),
                    createInstruction(WAIT, "1e-15"),
                    createInstruction(READ, tmp(14), "processor1", q(":foo*finished")),
                    createInstruction(JUMP, label(4), "equal", tmp(14), "false"),
                    createInstruction(READ, tmp(13), "processor1", q(":foo:count")),
                    createInstruction(SET, ":a", tmp(13)),
                    createInstruction(READ, tmp(15), "processor1", q(":foo*retval")),
                    createInstruction(SET, ":y", tmp(15)),
                    createInstruction(WRITE, "11", "processor2", q(":foo:a")),
                    createInstruction(WRITE, "false", "processor2", q(":foo*finished")),
                    createInstruction(WRITE, "2", "processor2", q("@counter")),
                    createInstruction(LABEL, label(5)),
                    createInstruction(WAIT, "1e-15"),
                    createInstruction(READ, tmp(18), "processor2", q(":foo*finished")),
                    createInstruction(JUMP, label(5), "equal", tmp(18), "false"),
                    createInstruction(READ, tmp(17), "processor2", q(":foo:count")),
                    createInstruction(SET, ":b", tmp(17)),
                    createInstruction(READ, tmp(19), "processor2", q(":foo*retval")),
                    createInstruction(SET, ":z", tmp(19)),
                    createInstruction(PRINT, ":y"),
                    createInstruction(PRINT, ":z"),
                    createInstruction(PRINT, ":a"),
                    createInstruction(PRINT, ":b")
            );
        }

        @Test
        void compilesNestedRemoteModule() {
            assertCompilesTo("""
                            require "local.mnd";
                            processor1.x = localFoo(10, out a);
                            print(processor1.x, a);
                            """,
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(7), "processor1", q("*signature")),
                    createInstruction(JUMP, label(2), "notEqual", tmp(7), q("361567e89c3ad027:v1")),
                    createInstruction(SET, ":localFoo:a", "10"),
                    createInstruction(PRINT, q("Before foo")),
                    createInstruction(WRITE, ":localFoo:a", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "false", "processor1", q(":foo*finished")),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(LABEL, label(4)),
                    createInstruction(WAIT, "1e-15"),
                    createInstruction(READ, tmp(9), "processor1", q(":foo*finished")),
                    createInstruction(JUMP, label(4), "equal", tmp(9), "false"),
                    createInstruction(READ, tmp(2), "processor1", q(":foo:count")),
                    createInstruction(SET, ":localFoo:count", tmp(2)),
                    createInstruction(READ, tmp(10), "processor1", q(":foo*retval")),
                    createInstruction(SET, ":localFoo:result", tmp(10)),
                    createInstruction(PRINT, q("After foo")),
                    createInstruction(SET, tmp(8), ":localFoo:result"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, ":a", ":localFoo:count"),
                    createInstruction(WRITE, tmp(8), "processor1", q(".x")),
                    createInstruction(READ, tmp(0), "processor1", q(".x")),
                    createInstruction(PRINT, tmp(0)),
                    createInstruction(PRINT, ":a")
            );
        }

        @Test
        void compilesRemoteArrayAccess() {
            assertCompilesTo("""
                            require "remote2.mnd" remote processor1;
                            for out i in processor1.array[0..2] do
                                i = rand(100);
                            end;
                            processor1.array[floor(rand(10))]++;
                            print(processor1.array[2..4]);
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(READ, tmp(10), "processor1", q("*signature")),
                    createInstruction(JUMP, label(1), "notEqual", tmp(10), q("ccc0ff41cc7e907b:v1")),
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

        @Test
        void compilesMultipleRemoteArrayAccess() {
            assertCompilesTo("""
                            require "remote2.mnd" remote processor1, processor2;
                            for out i in processor1.array[0..2] do
                                i = rand(100);
                            end;
                            processor2.array[floor(rand(10))]++;
                            print(processor2.array[2..4]);
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(READ, tmp(13), "processor1", q("*signature")),
                    createInstruction(JUMP, label(1), "notEqual", tmp(13), q("ccc0ff41cc7e907b:v1")),
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(27), "processor2", q("*signature")),
                    createInstruction(JUMP, label(2), "notEqual", tmp(27), q("ccc0ff41cc7e907b:v1")),
                    createInstruction(READ, tmp(3), "processor1", q(".array*0")),
                    createInstruction(SET, ":i", tmp(3)),
                    createInstruction(SETADDR, tmp(28), label(6)),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(MULTILABEL, label(6)),
                    createInstruction(WRITE, ":i", "processor1", q(".array*0")),
                    createInstruction(READ, tmp(4), "processor1", q(".array*1")),
                    createInstruction(SET, ":i", tmp(4)),
                    createInstruction(SETADDR, tmp(28), label(7)),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(MULTILABEL, label(7)),
                    createInstruction(WRITE, ":i", "processor1", q(".array*1")),
                    createInstruction(READ, tmp(5), "processor1", q(".array*2")),
                    createInstruction(SET, ":i", tmp(5)),
                    createInstruction(SETADDR, tmp(28), label(8)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(OP, "rand", tmp(29), "100"),
                    createInstruction(SET, ":i", tmp(29)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(MULTIJUMP, tmp(28), "0", "0"),
                    createInstruction(MULTILABEL, label(8)),
                    createInstruction(WRITE, ":i", "processor1", q(".array*2")),
                    createInstruction(LABEL, label(5)),
                    createInstruction(OP, "rand", tmp(30), "10"),
                    createInstruction(OP, "floor", tmp(31), tmp(30)),
                    createInstruction(SET, tmp(32), tmp(31)),
                    createInstruction(READARR, tmp(33), ".processor2.array[]", tmp(32)),
                    createInstruction(SET, tmp(34), tmp(33)),
                    createInstruction(OP, "add", tmp(33), tmp(33), "1"),
                    createInstruction(WRITEARR, tmp(33), ".processor2.array[]", tmp(32)),
                    createInstruction(READ, tmp(19), "processor2", q(".array*2")),
                    createInstruction(PRINT, tmp(19)),
                    createInstruction(READ, tmp(20), "processor2", q(".array*3")),
                    createInstruction(PRINT, tmp(20)),
                    createInstruction(READ, tmp(35), "processor2", q(".array*4")),
                    createInstruction(PRINT, tmp(35))
            );
        }

        @Test
        void compilesVariableAccessViaSynonym() {
            assertCompilesTo("""
                            linked p = processor1;
                            require "remote.mnd" remote p;
                            p.foo(10, out a);
                            p.x = 10;
                            """,
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(5), "processor1", q("*signature")),
                    createInstruction(JUMP, label(2), "notEqual", tmp(5), q("361567e89c3ad027:v1")),
                    createInstruction(WRITE, "10", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "false", "processor1", q(":foo*finished")),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(LABEL, label(3)),
                    createInstruction(WAIT, "1e-15"),
                    createInstruction(READ, tmp(8), "processor1", q(":foo*finished")),
                    createInstruction(JUMP, label(3), "equal", tmp(8), "false"),
                    createInstruction(READ, tmp(7), "processor1", q(":foo:count")),
                    createInstruction(SET, ":a", tmp(7)),
                    createInstruction(WRITE, "10", "processor1", q(".x"))
            );
        }

        @Test
        void compilesLocalCallToRemoteFunction() {
            assertCompilesTo("""
                            module local;
                            export def foo() end;
                            begin print(foo()); end;
                            """,
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SETADDR, ":foo*retaddr", label(3)),
                    createInstruction(CALL, label(1), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(PRINT, ":foo*retval"),
                    createInstruction(SET, "*signature", q("f3671b0880e88818:v1")),
                    createInstruction(LABEL, label(4)),
                    createInstruction(WAIT, "1e12"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SETADDR, ":foo*retaddr", label(4)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":foo*retval", "null"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(SET, ":foo*finished", "true"),
                    createInstruction(RETURN, ":foo*retaddr")
            );
        }

        @Test
        void compilesCompatibleTargets() {
            assertCompilesTo("""
                            #set target = 8w;
                            require "target.mnd";
                            foo();
                            """,
                    createInstruction(PRINT, q("foo")),
                    createInstruction(LABEL, label(0))
            );
        }
    }

    @Nested
    class Errors {

        @Test
        void refusesAsyncOnLogicFunction() {
            assertGeneratesMessage(
                    "Function or method 'printflush' cannot be called asynchronously.",
                    "async(printflush(message1));"
            );
        }

        @Test
        void refusesAsyncOnLibraryFunction() {
            assertGeneratesMessage(
                    "Function or method 'print' cannot be called asynchronously.",
                    "async(print(10));"
            );
        }

        @Test
        void refusesAsyncOnAsyncFunction() {
            assertGeneratesMessage(
                    "Function or method 'async' cannot be called asynchronously.",
                    """
                            require "remote.mnd" remote processor1;
                            async(async(processor1.foo(10)));"""
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
        void refusesConflictingRemoteFunctions() {
            assertGeneratesMessage(
                    "Remote function 'foo(a)' conflicts with remote function 'foo()': names of remote functions must be unique.",
                    """
                            export def foo() end;
                            export def foo(a) end;
                            """
            );
        }

        @Test
        void refusesMultipleRemoteFunctions() {
            assertGeneratesMessage(
                    "Remote function 'foo(in a, out count)' conflicts with remote function 'foo()': names of remote functions must be unique.",
                    """
                            require "remote.mnd" remote processor1;
                            require "conflict.mnd" remote processor2;
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
                            async(processor1.foo(10));
                            await(processor1.foo);
                            print(processor1.foo.bar);
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
        void refusesWrongVerifySignatureParameter() {
            assertGeneratesMessage(
                    "Unrecognized remote processor.",
                    """
                            verifySignature(print);
                            """
            );
        }

        @Test
        void refusesVariablesInLocalScope() {
            assertGeneratesMessage(
                    "The 'remote' modifier cannot be used to declare local variables.",
                    """
                            begin
                                remote(processor1) x = 10;
                            end;
                            """
            );
        }

        @Test
        void refusesOutputParametersInAsyncCall() {
            assertGeneratesMessage(
                    "Function 'foo': asynchronous calls to remote function cannot take output arguments.",
                    """
                            require "remote.mnd" remote processor1;
                            async(processor1.foo(10, out a));
                            """
            );
        }

        @Test
        void refusesUnknownProcessor() {
            assertGeneratesMessage(
                    "Unknown method 'foo'.",
                    """
                            require "remote.mnd" remote processor1;
                            async(processor2.foo(10));
                            """
            );
        }

        @Test
        void requiresStrictModeForDirectModules() {
            assertGeneratesMessage(
                    "The 'strict' syntax mode is required for modules.",
                    """
                            #set syntax = relaxed;
                            module test;
                            export var foo = 0;
                            """
            );
        }

        @Test
        void requiresStrictModeForRequiredModules() {
            assertGeneratesMessage(
                    "Code outside a main code block or function.",
                    """
                            require "relaxed1.mnd";
                            begin
                                print(a);
                            end;
                            """
            );
        }

        @Test
        void defaultsToStrictModeForModules() {
            assertGeneratesMessage(
                    "Code outside a main code block or function.",
                    """
                            module test;
                            print("foo");
                            """
            );
        }

        @Test
        void defaultsToStrictModeForRequiredModules() {
            assertGeneratesMessage(
                    "The 'strict' syntax mode is required for modules.",
                    """
                            require "relaxed2.mnd";
                            begin
                                print(a);
                            end;
                            """
            );
        }

        @Test
        void reportsIncompatibleVersion() {
            assertGeneratesMessage(
                    "Module target '8.0w' is incompatible with global target '7.1w'.",
                    """
                            #set target = 7w;
                            require "target.mnd";
                            """
            );
        }

        @Test
        void reportsIncompatibleTarget() {
            assertGeneratesMessage(
                    "Module target '8.0w' is incompatible with global target '8.1m'.",
                    """
                            #set target = 8.1s;
                            require "target.mnd";
                            """
            );
        }
    }
}
