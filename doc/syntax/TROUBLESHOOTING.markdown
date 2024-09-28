# Troubleshooting Mindcode

## Dealing with syntax errors

Dealing with syntax errors in Mindcode isn't always easy. There are various reasons for this, some of them might be
amenable and some probably not. Many other compilers for Mindustry Logic probably provide better means to handle syntax
errors. Making the syntax errors easier to handle isn't an easy task for various reasons; to make up for this
deficiency, we'll try to describe some possible approaches to diagnosing and fixing syntax errors in Mindcode.

**To be expanded.**

## Debugging the compiled code

Mindcode provides two tools for making debugging the compiled code a bit easier.

### Running the compiled code in an emulator

Both the web app and the command line tool allow the compiled code [to be run on an emulated processor](TOOLS-CMDLINE.markdown#running-the-compiled-code). While the emulator supports almost no interaction with the Mindustry World (the sole exception at this time is access to memory cells and memory banks), it can still be used to debug code that doesn't access the outside world (e.g. by commenting out the offending statements in the source code).

An expansion of this feature is planned, where a static mindustry World would be simulated to some extent for the running code to interact with.

The main advantage is that the entire output of the program can be displayed and inspected. [Remarks in active mode](SYNTAX-5-OTHER.markdown#option-remarks) can be used to activate additional output just for the purpose of the debugging. Also, the output is available right after compiling the code, without having to go to Mindustry to insert the code into a processor and inspect the output in a properly linked message block. The total size of the produced text can be up to 10,000 characters, as opposed to the 400-character limit of the message block.

### Inspecting the variables in real Mindustry processor

The other option to debug a Mindcode program is to examine its behavior in actual Mindustry World. Mindustry provides a way to inspect the program's variables in the **Vars** screen of the Mindustry processor shows all variables and their values, but the variables are displayed in the order in which they were created. This typically results in a very chaotic order of variables, where variables defined by the user are mixed with temporary variables, making it quite difficult to find a specific variable in sufficiently large programs.

It is possible to use the [`sort-variables`](SYNTAX-5-OTHER.markdown#option-sort-variables) compiler directive, or the `--sort-variables` command-line option, to make variables be displayed in a Mindustry processor in a well-defined order. Mindcode compiler ensures that by prepending a special block at the beginning of the program which creates user-defined variables in a specific order without altering their value. (The `packcolor` instruction is used, which can read - and therefore create - up to four variables per instruction, without any side effects. The result is not stored anywhere so that the variable-ordering code block doesn't change values of any variables, and therefore the behavior of the program remains unaltered, except for an unavoidable difference in total runtime of the program.)

An example of a program created with sorting variables:

```
#set sort-variables;

param MIN = 10;
param MAX = 50;

// A is an uninitialized variable
B = A == null ? "first" : "not first";

for i in MIN .. MAX do
    foo(i);
    foo(2 * i);
end;

println($"\n$B: $A");

def foo(n)
  print(n);
  A += 1;
end;
```

is compiled to

```
packcolor null MAX MIN A B
packcolor null i __fn0_n null null
set MIN 10
set MAX 50
set B "not first"
jump 7 notEqual A null
set B "first"
set i MIN
jump 16 greaterThan MIN MAX
print i
op add A A 1
op mul __fn0_n 2 i
print __fn0_n
op add A A 1
op add i i 1
jump 9 lessThanEq i MAX
print "\n"
print B
print ": "
print A
print "\n"
end
```

The number of variables being sorted is limited by the [instruction limit](#option-instruction-limit). Should the resulting program size exceed the instruction limit, some or all variables will remain unordered.

---

[« Previous: Schematics Refresher](TOOLS-REFRESHER.markdown) &nbsp; | &nbsp;
[Next: Mindustry Tips and Tricks »](MINDUSTRY-TIPS-N-TRICKS.markdown)
