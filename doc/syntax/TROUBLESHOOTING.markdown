# Troubleshooting Mindcode

## Dealing with syntax errors

In earlier versions of Mindcode, syntax errors were reported very poorly. As of version 2.3.0, the error reporting got much better. In most cases, the position of the error in source code is correctly identified and displayed. In the web application, clicking on the position given with the error message places the cursor at the occurrence of the error in the source panel. Of course, if the source code gets edited (especially when lines are added or deleted), the positions will no longer be valid, and you need to compile the code again. When using an IDE to edit and compile the code offline, it should be possible to [set up the IDE](TOOLS-IDE-INTEGRATION.markdown) to provide navigation from the error messages to the occurrence of the error in the source code as well.  

When encountering more complicated situations, one of these tips might help.

### Not all reported errors are necessarily accurate

When an error is encountered in the source code, the parser tries to recover and continue with the analysis to be able to report more errors at once during a single compilation. Therefore, when several errors are reported, some of them may be a byproduct of unsuccessful recovery from previous errors.
- In most cases, the first reported error is accurate. Your best course of action often is to fix the first error and from the rest those that are obvious, and compile the source code again.
- Mindcode is particularly prone to report missing semicolons at strange places when another error affecting the overall program structure (such as a missing keyword) is present.

### Always look at the preceding code

Quite often the error is caused by some problem in the statement immediately preceding the position on which it is reported. It's always a good idea to inspect the previous statement. Remember the true source of error might be separated from the reported error by a lengthy comment.
- Sometimes the cause for the error lies within the same expression, just earlier: `print[rand(i));`. The error is caused by typing a left square bracket instead of opening parenthesis, but is reported at the closing parenthesis.   
- Special handling has been implemented for missing semicolons to be reported at the end of the previous statements, which is typically the correct place. If your code doesn't contain any other error but missing semicolons, they should all be reported accurately.

### Unexpected input

When Mindcode encounters an input which disallows further analysis, it may report `unexpected 'token'`, for example `unexpected 'out'`. This error typically has one of two causes:
- A keyword is omitted or mistyped: in `while a < 10 print(a); end;` the `do` keyword is missing. Mindcode will announce the `print` is unexpected at the place where the `do` keyword should be present.
- A keyword is used as a name for a variable or function. This may suddenly appear in an existing code when a new keyword is introduced into the language, such as `out` or `param` in the recent versions of Mindcode. In this case Mindcode will typically announce the keyword is unexpected.

### Missing `end` keyword

A particularly challenging error is a missing `end` keyword. As it is not possible to uniquely match `end` keywords with corresponding opening statements (such as `while`, `for` or `if` statements), Mindcode will typically report the missing `end` keyword at the end of the file. In most cases, the true cause of the error is buried somewhere deep down in a structure of several nested statements.
- The best way to prevent these errors from happening is to meticulously indent code inside control structures. This should make the error more noticeable while editing the code.
- Mindcode currently doesn't have a good one-line conditional statement. When putting the entire if statement on one line, the `end` keyword still needs to be there (as in `if condition then statement; end;`), but is easily left out. Pay particular attention to not forget the `end` keyword in these cases.
- Having a large program with many levels of nested statements makes spotting the missing `end` particularly challenging, In this situation, comment out some of the inner structures. Once you establish the outer structure(s) are compiling well, you may start returning the inner ones one by one until the source of the error becomes clear.
- Try to extract as much code into functions as possible, using additional output parameters when needed. Mindcode is pretty good at optimizing functions, and in most cases the functions will be inlined and optimized just as well as if the code was nested.

### Ask for help

There's always a possibility that Mindcode contains some bug that is causing the error you're encountering. When in trouble, don't hesitate to ask for help by [creating an issue](https://github.com/cardillan/mindcode/issues/new).

You can ask for help here even if you don't suspect there's an error in Mindcode. All questions are welcome.  

## Debugging the compiled code

Mindcode provides several tools for making debugging the compiled code a bit easier.

### Debug-specific code

As Mindcode doesn't provide a debugger, you can only inspect the state of your running program by including specific code to output important information onto message blocks or by watching variables in the actual processor in the Vars screen. In the first case, you'll probably need to add message blocks to your design to output the information onto. In the second case, you'll probably need to pause or stop the program execution to be able to inspect the values of your variables when the processor is not running.

This requires adding a code only used for the actual debugging process. Mindcode provides the following tools for including the debug-specific code only when needed:

* A debug code block: `debug <some code> end;`. Debug block is not compiled, unless `debug` is set to `true`.
* A debug function: `debug void foo() <some code> end;`. Calls to debug functions are not compiled, unless `debug` is set to `true`.

By setting the [`debug` compiler option](SYNTAX-5-OTHER.markdown#option-debug) to `true`, the code gets compiled with debug support. The compiler ensures the following: 
 
* All user-defined variables are preserved and not removed by optimizations. It is possible to inspect all variables on the Vars screen.
* Statements in debug blocks are included in the compiled code.
* Calls to debug functions are compiled.

Compiling code with debug support enabled may result in larger code (both due to additional code in debug blocks and debug functions, and due to some optimizations not being performed to preserve user-defined variables). If the new code size exceeds the mlog code size limit (1000 instructions), you can use the [MlogAssertions mod](https://github.com/cardillan/MlogAssertions) to increase the instruction limit (up to 2000 instructions) to be able to still run the compiled code.

When entering the processor screen while the program is running doesn't stop program execution. To get a meaningful insight into the state of the program, you may need to stop it. Stopping the program is possible in several ways:

* By pausing the game. In this case, it might be challenging to stop the program at a well-defined time.
* By including a `stop` instruction in the program (provided by the `stopProcessor()` or `error()` functions, or by compiler-generated [runtime checks](SYNTAX-5-OTHER.markdown#option-error-reporting)). However, once stopped, the program execution can't be resumed; the whole processor needs to be restarted.
* By implementing a pause functionality. The program might wait for some user-interaction, for example, until the user presses a switch. This allows stopping the program at a well-defined point while maintaining the ability to resume its execution. The easiest way for this is to declare the function performing the pause as `debug` - this way the program will only pause when compiled with debug support enabled. 

Example:

```Mindcode
#set debug = true;          // Activating debug mode

require units;

debug void pause(in switch)
    // Pauses the execution of the program until the switch passed in is deactivated - clicked by the user
    // Allows the user to inspect the state of the program - the variables at the place where it was called.  
    switch.enabled = true;
    do while not switch1.enabled;
end;

begin
    unit = findFreeUnit(@flare, 10);
    
    debug
        // Debug code - will only be compiled in debug
        if unit == null then
            print("No free unit found!");
            printflush(message1);
            pause(switch1);
        end;
    end;
    
    do
        move(@thisx, @thisy);
    while !within(@thisx, @thisy, 1);
    
    print("Unit moved to processor");
    printflush(message1);
    pause(switch1);
end;
```

### The `error()` function

The built-in `error()` function serves for logging diagnostic information when the program encounters an unexpected situation. The information is stored in special variables or displayed in-game, and the processor is stopped.

> [!NOTE]
> When the `error-function` option is set to `false`, or the `error-reporting` option is set to `none`, calls to the `error()` function are ignored and not compiled into the final code.

When the first parameter to the error function is a formattable string literal, the string and other function arguments are [formatted at compile-time](SYNTAX-4-FUNCTIONS.markdown#compile-time-formatting). All constant values are embedded into the string literal, and remaining values are accumulated and referenced from the string literal as `[1]`, `[2]`, and so on.

When the first parameter to the error function is not a formattable string literal, all parameters to the function are simply taken as is.

How the function is compiled depends on the `error-reporting` compiler option:

* when set to `minimal`, `simple` or `described`, the function parameters are stored in variables `*ERROR_n`, where `n` is an index starting at 0. When the formattable string literal was used, the formatted string is stored in `*ERROR_0` and the rest in `*ERROR_1`, `*ERROR_2` etc. After that, a `stop` instruction is emitted.
* when set to `assert`, an `error` instruction is produced, taking up to 10 of the accumulated parameters as arguments. When the formattable string literal was used, the formatted string is used as the first argument. This instruction is then handled by the **Mlog Assertions** mod, which displays the message and stops the processor.

Example:

```Mindcode
#set error-reporting = simple;
#set symbolic-labels = true;

const min = 0;
param max = 8;
param i = 10;

if i < min or i > max then
    error($"Index $i of array $ is out of bounds ($, $)!", "foo", min, max);
end;

print(i);
```

gets compiled to

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    set max 8
    set i 10
    jump label_4 lessThan i 0
    jump label_8 lessThanEq i max
label_4:
        set *ERROR_0 "Index [[1] of array foo is out of bounds (0, [[2])!"
        set *ERROR_1 i
        set *ERROR_2 max
        stop
    label_8:
    print i
```

### Running the compiled code in an emulator

Both the web app and the command line tool allow the compiled code [to be run on an emulated processor](TOOLS-CMDLINE.markdown#running-mlog-code-or-schematics). While the emulator supports almost no interaction with the Mindustry World, it can still be used to debug code that doesn't access the outside world.

An expansion of this feature is planned, where a static mindustry World would be simulated to some extent for the running code to interact with.

The main advantage is that the entire output of the program can be displayed and inspected. [Remarks in active mode](SYNTAX-5-OTHER.markdown#option-remarks) can be used to activate additional output just for debugging. Also, the output is available right after compiling the code, without having to go to Mindustry to insert the code into a processor and inspect the output in a properly linked message block. The total size of the produced text can be up to 10,000 characters, as opposed to the 400-character limit of the message block.

For even more detailed insight into the execution of the program, the state of variables at certain points - or even at every executed instruction - can be inspected using the techniques described [here](TOOLS-PROCESSOR-EMULATOR.markdown#inspecting-program-state).

### Inspecting the variables in a real Mindustry processor

The other option to debug a Mindcode program is to examine its behavior in actual Mindustry World. Mindustry provides a way to inspect the program's variables in the **Vars** screen of the Mindustry processor shows all variables and their values, but the variables are displayed in the order in which they were created. This typically results in a very chaotic order of variables, where variables defined by the user are mixed with temporary variables, making it quite difficult to find a specific variable in large enough programs.

It is possible to use the [`sort-variables`](SYNTAX-5-OTHER.markdown#option-sort-variables) compiler directive, or the `--sort-variables` command-line option, to make variables be displayed in a Mindustry processor in a well-defined order. Mindcode compiler ensures that by prepending a special block at the beginning of the program which creates user-defined variables in a specific order without altering their value. (The `packcolor` instruction is used, which can read – and therefore create – up to four variables per instruction, without any side effects. The result is not stored anywhere so that the variable-ordering code block doesn't change values of any variables, and therefore the behavior of the program remains unaltered, except for an unavoidable difference in the total runtime of the program.)

An example of a program created with sorting variables:

```Mindcode
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

```mlog
jump 2 always 0 0
draw triangle MAX MIN .A :i :foo:n 0
set MIN 10
set MAX 50
set *tmp1 "not first"
jump 7 notEqual .A null
set *tmp1 "first"
set :i MIN
jump 16 greaterThan MIN MAX
print :i
op add .A .A 1
op mul :foo:n 2 :i
print :foo:n
op add .A .A 1
op add :i :i 1
jump 9 lessThanEq :i MAX
print "\n"
print *tmp1
print ": "
print .A
print "\n"
```

The number of variables being sorted is limited by the [instruction limit](SYNTAX-5-OTHER.markdown#option-instruction-limit). Should the resulting program size exceed the instruction limit, some or all variables will remain unordered.

---

[&#xAB; Previous: Testing tool](TOOLS-TESTING-TOOL.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Mindustry Tips and Tricks &#xBB;](MINDUSTRY-TIPS-N-TRICKS.markdown)
