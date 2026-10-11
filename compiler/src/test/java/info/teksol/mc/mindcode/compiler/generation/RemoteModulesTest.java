package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.TextOffset;
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
                        
                        remote(processor1) require "remote.mnd";
                        
                        def localFoo(in a, out count)
                            print("Before foo");
                            var result = processor1.foo(a, out count);
                            print("After foo");
                            result;
                        end;
                        """,
                TextOffset.ZERO_OFFSET);

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
                        """,
                TextOffset.ZERO_OFFSET);

        inputFiles.addPackagedFile(
                "remote2.mnd",
                """
                        module test2;
                        
                        export array[10];
                        
                        export def baz(a, b, c)
                            return len(len(a * a + b * b), c * c);
                        end;
                        """,
                TextOffset.ZERO_OFFSET);

        inputFiles.addPackagedFile(
                "conflict.mnd",
                """
                        module test2;
                        
                        export def foo()
                            print("Hey!");
                        end;
                        """,
                TextOffset.ZERO_OFFSET);

        inputFiles.addPackagedFile(
                "relaxed1.mnd",
                """
                        module test;
                        print("foo");
                        """,
                TextOffset.ZERO_OFFSET);

        inputFiles.addPackagedFile(
                "relaxed2.mnd",
                """
                        #set syntax = relaxed;
                        module test;
                        print("foo");
                        """,
                TextOffset.ZERO_OFFSET);

        inputFiles.addPackagedFile(
                "target.mnd",
                """
                        #set target = 8.0w;
                        module test;
                        void foo() print("foo"); end;
                        """,
                TextOffset.ZERO_OFFSET);

        return inputFiles;
    }

    @Nested
    class Basics {
        @Test
        void compilesSynchronousCall() {
            assertCompilesTo("""
                            remote(processor1) require "remote.mnd";
                            z = processor1.foo(10, out a);
                            print(z, a);
                            """,
                    createInstruction(LABEL, label(4)),
                    createInstruction(READ, tmp(5), "processor1", q("*signature")),
                    createInstruction(JUMP, label(4), "notEqual", tmp(5), q("361567e89c3ad027:v2")),
                    createInstruction(WRITE, "10", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "@this", "processor1", q("*caller")),
                    createInstruction(SET, "*remotewait", "1e12"),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(WAIT, "*remotewait"),
                    createInstruction(READ, tmp(8), "processor1", q(":foo:count")),
                    createInstruction(SET, ":a", tmp(8)),
                    createInstruction(READ, tmp(10), "processor1", q(":foo*retval")),
                    createInstruction(SET, ":z", tmp(10)),
                    createInstruction(PRINT, ":z"),
                    createInstruction(PRINT, ":a")
            );
        }

        @Test
        void compilesAsynchronousCall() {
            assertCompilesTo("""
                            remote(processor1) require "remote.mnd";
                            async(processor1.foo(10));
                            print(finished(processor1.foo));
                            z = await(processor1.foo);
                            print(z, processor1.foo.count);
                            """,
                    createInstruction(LABEL, label(4)),
                    createInstruction(READ, tmp(5), "processor1", q("*signature")),
                    createInstruction(JUMP, label(4), "notEqual", tmp(5), q("361567e89c3ad027:v2")),
                    createInstruction(WRITE, "10", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "false", "processor1", q(":foo*finished")),
                    createInstruction(WRITE, "true", "processor1", q("*caller")),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(READ, tmp(8), "processor1", q(":foo*finished")),
                    createInstruction(PRINT, tmp(8)),
                    createInstruction(LABEL, label(5)),
                    createInstruction(WAIT, "0"),
                    createInstruction(READ, tmp(9), "processor1", q(":foo*finished")),
                    createInstruction(JUMP, label(5), "equal", tmp(9), "false"),
                    createInstruction(READ, tmp(11), "processor1", q(":foo*retval")),
                    createInstruction(SET, ":z", tmp(11)),
                    createInstruction(PRINT, ":z"),
                    createInstruction(READ, tmp(12), "processor1", q(":foo:count")),
                    createInstruction(PRINT, tmp(12))
            );
        }

        @Test
        void compilesFinished() {
            assertCompilesTo("""
                            remote(processor1) require "remote.mnd";
                            async(processor1.foo(10));
                            do while !finished(processor1.foo, out z);
                            print(z, processor1.foo.count);
                            """,
                    createInstruction(LABEL, label(4)),
                    createInstruction(READ, tmp(5), "processor1", q("*signature")),
                    createInstruction(JUMP, label(4), "notEqual", tmp(5), q("361567e89c3ad027:v2")),
                    createInstruction(WRITE, "10", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "false", "processor1", q(":foo*finished")),
                    createInstruction(WRITE, "true", "processor1", q("*caller")),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(LABEL, label(5)),
                    createInstruction(LABEL, label(6)),
                    createInstruction(READ, tmp(8), "processor1", q(":foo*finished")),
                    createInstruction(READ, tmp(9), "processor1", q(":foo*retval")),
                    createInstruction(SET, ":z", tmp(9)),
                    createInstruction(OP, "equal", tmp(10), tmp(8), "false"),
                    createInstruction(JUMP, label(5), "notEqual", tmp(10), "false"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(PRINT, ":z"),
                    createInstruction(READ, tmp(11), "processor1", q(":foo:count")),
                    createInstruction(PRINT, tmp(11))
            );
        }

        @Test
        void compilesMultipleModules() {
            assertCompilesTo("""
                            remote(processor1) require "remote.mnd";
                            remote(processor2) require "remote2.mnd";
                            y = processor2.baz(10, 20, 30);
                            z = processor1.foo(10, out a);
                            print(y, z, a);
                            """,
                    createInstruction(LABEL, label(6)),
                    createInstruction(READ, tmp(5), "processor1", q("*signature")),
                    createInstruction(JUMP, label(6), "notEqual", tmp(5), q("361567e89c3ad027:v2")),
                    createInstruction(LABEL, label(7)),
                    createInstruction(READ, tmp(19), "processor2", q("*signature")),
                    createInstruction(JUMP, label(7), "notEqual", tmp(19), q("ccc0ff41cc7e907b:v2")),
                    createInstruction(WRITE, "10", "processor2", q(":baz:a")),
                    createInstruction(WRITE, "20", "processor2", q(":baz:b")),
                    createInstruction(WRITE, "30", "processor2", q(":baz:c")),
                    createInstruction(WRITE, "@this", "processor2", q("*caller")),
                    createInstruction(SET, "*remotewait", "1e12"),
                    createInstruction(WRITE, "1", "processor2", q("@counter")),
                    createInstruction(WAIT, "*remotewait"),
                    createInstruction(READ, tmp(24), "processor2", q(":baz*retval")),
                    createInstruction(SET, ":y", tmp(24)),
                    createInstruction(WRITE, "10", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "@this", "processor1", q("*caller")),
                    createInstruction(SET, "*remotewait", "1e12"),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(WAIT, "*remotewait"),
                    createInstruction(READ, tmp(27), "processor1", q(":foo:count")),
                    createInstruction(SET, ":a", tmp(27)),
                    createInstruction(READ, tmp(29), "processor1", q(":foo*retval")),
                    createInstruction(SET, ":z", tmp(29)),
                    createInstruction(PRINT, ":y"),
                    createInstruction(PRINT, ":z"),
                    createInstruction(PRINT, ":a")
            );
        }

        @Test
        void compilesVerifySignature() {
            assertCompilesTo("""
                            remote(PROC) require "remote.mnd";
                            PROC = getlink(0);
                            if verifySignature(PROC) then
                                async(PROC.foo(10));
                            end;
                            """,
                    createInstruction(GETLINK, tmp(5), "0"),
                    createInstruction(SET, ".PROC", tmp(5)),
                    createInstruction(LABEL, label(6)),
                    createInstruction(READ, tmp(6), ".PROC", q("*signature")),
                    createInstruction(JUMP, label(6), "equal", tmp(6), "null"),
                    createInstruction(OP, "equal", tmp(7), tmp(6), q("361567e89c3ad027:v2")),
                    createInstruction(JUMP, label(4), "equal", tmp(7), "false"),
                    createInstruction(WRITE, "10", ".PROC", q(":foo:a")),
                    createInstruction(WRITE, "false", ".PROC", q(":foo*finished")),
                    createInstruction(WRITE, "true", ".PROC", q("*caller")),
                    createInstruction(WRITE, "2", ".PROC", q("@counter")),
                    createInstruction(SET, tmp(8), "null"),
                    createInstruction(JUMP, label(5), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(8), "null"),
                    createInstruction(LABEL, label(5))
            );
        }

        @Test
        void compilesMultipleInstantiations() {
            assertCompilesTo("""
                            remote(processor1, processor2) require "remote.mnd";
                            y = processor1.foo(10, out a);
                            z = processor2.foo(11, out b);
                            print(y, z, a, b);
                            """,
                    createInstruction(LABEL, label(4)),
                    createInstruction(READ, tmp(5), "processor1", q("*signature")),
                    createInstruction(JUMP, label(4), "notEqual", tmp(5), q("361567e89c3ad027:v2")),
                    createInstruction(LABEL, label(5)),
                    createInstruction(READ, tmp(11), "processor2", q("*signature")),
                    createInstruction(JUMP, label(5), "notEqual", tmp(11), q("361567e89c3ad027:v2")),
                    createInstruction(WRITE, "10", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "@this", "processor1", q("*caller")),
                    createInstruction(SET, "*remotewait", "1e12"),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(WAIT, "*remotewait"),
                    createInstruction(READ, tmp(14), "processor1", q(":foo:count")),
                    createInstruction(SET, ":a", tmp(14)),
                    createInstruction(READ, tmp(15), "processor1", q(":foo*retval")),
                    createInstruction(SET, ":y", tmp(15)),
                    createInstruction(WRITE, "11", "processor2", q(":foo:a")),
                    createInstruction(WRITE, "@this", "processor2", q("*caller")),
                    createInstruction(SET, "*remotewait", "1e12"),
                    createInstruction(WRITE, "2", "processor2", q("@counter")),
                    createInstruction(WAIT, "*remotewait"),
                    createInstruction(READ, tmp(18), "processor2", q(":foo:count")),
                    createInstruction(SET, ":b", tmp(18)),
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
                    createInstruction(LABEL, label(4)),
                    createInstruction(READ, tmp(5), "processor1", q("*signature")),
                    createInstruction(JUMP, label(4), "notEqual", tmp(5), q("361567e89c3ad027:v2")),
                    createInstruction(SET, ":localFoo:a", "10"),
                    createInstruction(PRINT, q("Before foo")),
                    createInstruction(WRITE, ":localFoo:a", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "@this", "processor1", q("*caller")),
                    createInstruction(SET, "*remotewait", "1e12"),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(WAIT, "*remotewait"),
                    createInstruction(READ, tmp(9), "processor1", q(":foo:count")),
                    createInstruction(SET, ":localFoo:count", tmp(9)),
                    createInstruction(READ, tmp(11), "processor1", q(":foo*retval")),
                    createInstruction(SET, ":localFoo:result", tmp(11)),
                    createInstruction(PRINT, q("After foo")),
                    createInstruction(SET, tmp(6), ":localFoo:result"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(SET, ":a", ":localFoo:count"),
                    createInstruction(WRITE, tmp(6), "processor1", q(".x")),
                    createInstruction(READ, tmp(12), "processor1", q(".x")),
                    createInstruction(PRINT, tmp(12)),
                    createInstruction(PRINT, ":a")
            );
        }

        @Test
        void compilesRemoteArrayAccess() {
            assertCompilesTo("""
                            remote(processor1) require "remote2.mnd";
                            for out i in processor1.array[0..2] do
                                i = rand(100);
                            end;
                            processor1.array[floor(rand(10))]++;
                            print(processor1.array[2..4]);
                            """,
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(13), "processor1", q("*signature")),
                    createInstruction(JUMP, label(2), "notEqual", tmp(13), q("ccc0ff41cc7e907b:v2")),
                    createInstruction(READ, tmp(15), "processor1", q(".array*0")),
                    createInstruction(SET, ":i", tmp(15)),
                    createInstruction(SETADDR, tmp(14), label(6)),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(MULTILABEL, label(6)),
                    createInstruction(WRITE, ":i", "processor1", q(".array*0")),
                    createInstruction(READ, tmp(16), "processor1", q(".array*1")),
                    createInstruction(SET, ":i", tmp(16)),
                    createInstruction(SETADDR, tmp(14), label(7)),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(MULTILABEL, label(7)),
                    createInstruction(WRITE, ":i", "processor1", q(".array*1")),
                    createInstruction(READ, tmp(17), "processor1", q(".array*2")),
                    createInstruction(SET, ":i", tmp(17)),
                    createInstruction(SETADDR, tmp(14), label(8)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(OP, "rand", tmp(18), "100"),
                    createInstruction(SET, ":i", tmp(18)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(MULTIJUMP, tmp(14), "0", "0"),
                    createInstruction(MULTILABEL, label(8)),
                    createInstruction(WRITE, ":i", "processor1", q(".array*2")),
                    createInstruction(LABEL, label(5)),
                    createInstruction(OP, "rand", tmp(19), "10"),
                    createInstruction(OP, "floor", tmp(20), tmp(19)),
                    createInstruction(SET, tmp(21), tmp(20)),
                    createInstruction(READARR, tmp(22), ".processor1.array[]", tmp(21)),
                    createInstruction(SET, tmp(23), tmp(22)),
                    createInstruction(OP, "add", tmp(22), tmp(22), "1"),
                    createInstruction(WRITEARR, tmp(22), ".processor1.array[]", tmp(21)),
                    createInstruction(READ, tmp(24), "processor1", q(".array*2")),
                    createInstruction(PRINT, tmp(24)),
                    createInstruction(READ, tmp(25), "processor1", q(".array*3")),
                    createInstruction(PRINT, tmp(25)),
                    createInstruction(READ, tmp(26), "processor1", q(".array*4")),
                    createInstruction(PRINT, tmp(26))
            );
        }

        @Test
        void compilesMultipleRemoteArrayAccess() {
            assertCompilesTo("""
                            remote(processor1, processor2) require "remote2.mnd";
                            for out i in processor1.array[0..2] do
                                i = rand(100);
                            end;
                            processor2.array[floor(rand(10))]++;
                            print(processor2.array[2..4]);
                            """,
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(13), "processor1", q("*signature")),
                    createInstruction(JUMP, label(2), "notEqual", tmp(13), q("ccc0ff41cc7e907b:v2")),
                    createInstruction(LABEL, label(3)),
                    createInstruction(READ, tmp(27), "processor2", q("*signature")),
                    createInstruction(JUMP, label(3), "notEqual", tmp(27), q("ccc0ff41cc7e907b:v2")),
                    createInstruction(READ, tmp(29), "processor1", q(".array*0")),
                    createInstruction(SET, ":i", tmp(29)),
                    createInstruction(SETADDR, tmp(28), label(7)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(7)),
                    createInstruction(WRITE, ":i", "processor1", q(".array*0")),
                    createInstruction(READ, tmp(30), "processor1", q(".array*1")),
                    createInstruction(SET, ":i", tmp(30)),
                    createInstruction(SETADDR, tmp(28), label(8)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(8)),
                    createInstruction(WRITE, ":i", "processor1", q(".array*1")),
                    createInstruction(READ, tmp(31), "processor1", q(".array*2")),
                    createInstruction(SET, ":i", tmp(31)),
                    createInstruction(SETADDR, tmp(28), label(9)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "rand", tmp(32), "100"),
                    createInstruction(SET, ":i", tmp(32)),
                    createInstruction(LABEL, label(5)),
                    createInstruction(MULTIJUMP, tmp(28), "0", "0"),
                    createInstruction(MULTILABEL, label(9)),
                    createInstruction(WRITE, ":i", "processor1", q(".array*2")),
                    createInstruction(LABEL, label(6)),
                    createInstruction(OP, "rand", tmp(33), "10"),
                    createInstruction(OP, "floor", tmp(34), tmp(33)),
                    createInstruction(SET, tmp(35), tmp(34)),
                    createInstruction(READARR, tmp(36), ".array[]", tmp(35)),
                    createInstruction(SET, tmp(37), tmp(36)),
                    createInstruction(OP, "add", tmp(36), tmp(36), "1"),
                    createInstruction(WRITEARR, tmp(36), ".array[]", tmp(35)),
                    createInstruction(READ, tmp(38), "processor2", q(".array*2")),
                    createInstruction(PRINT, tmp(38)),
                    createInstruction(READ, tmp(39), "processor2", q(".array*3")),
                    createInstruction(PRINT, tmp(39)),
                    createInstruction(READ, tmp(40), "processor2", q(".array*4")),
                    createInstruction(PRINT, tmp(40))
            );
        }

        @Test
        void compilesVariableAccessViaSynonym() {
            assertCompilesTo("""
                            linked p = processor1;
                            remote(p) require "remote.mnd";
                            p.foo(10, out a);
                            p.x = 10;
                            """,
                    createInstruction(LABEL, label(4)),
                    createInstruction(READ, tmp(5), "processor1", q("*signature")),
                    createInstruction(JUMP, label(4), "notEqual", tmp(5), q("361567e89c3ad027:v2")),
                    createInstruction(WRITE, "10", "processor1", q(":foo:a")),
                    createInstruction(WRITE, "@this", "processor1", q("*caller")),
                    createInstruction(SET, "*remotewait", "1e12"),
                    createInstruction(WRITE, "2", "processor1", q("@counter")),
                    createInstruction(WAIT, "*remotewait"),
                    createInstruction(READ, tmp(8), "processor1", q(":foo:count")),
                    createInstruction(SET, ":a", tmp(8)),
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
                    createInstruction(SETADDR, ":foo*retaddr", label(4)),
                    createInstruction(CALL, label(1), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(PRINT, ":foo*retval"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, "*signature", q("f3671b0880e88818:v2")),
                    createInstruction(LABEL, label(5)),
                    createInstruction(WRITE, "1e-15", "*caller", q("*remotewait")),
                    createInstruction(SET, "*caller", "false"),
                    createInstruction(WAIT, "1e12"),
                    createInstruction(JUMP, label(5), "always"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SETADDR, ":foo*retaddr", label(5)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":foo*retval", "null"),
                    createInstruction(LABEL, label(6)),
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
                            remote(processor1) require "remote.mnd";
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
                    "Export function 'foo(a)' conflicts with export function 'foo()': names of export functions must be unique.",
                    """
                            module library;
                            export def foo() end;
                            export def foo(a) end;
                            """
            );
        }

        @Test
        void refusesMultipleRemoteFunctions() {
            assertGeneratesMessage(
                    "Export function 'foo(in a, out count)' conflicts with export function 'foo()': names of export functions must be unique.",
                    """
                            remote(processor1) require "remote.mnd";
                            remote(processor2) require "conflict.mnd";
                            foo();
                            """
            );
        }

        @Test
        void refusesUnknownParameter() {
            assertGeneratesMessage(
                    "Function 'foo': unknown output parameter 'bar'.",
                    """
                            remote(processor1) require "remote.mnd";
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
                            remote(processor1) require "remote.mnd";
                            async(processor1.foo(10, out a));
                            """
            );
        }

        @Test
        void refusesUnknownProcessor() {
            assertGeneratesMessage(
                    "Unknown method 'foo'.",
                    """
                            remote(processor1) require "remote.mnd";
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
