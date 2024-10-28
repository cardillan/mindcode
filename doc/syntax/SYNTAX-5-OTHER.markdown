# Advanced features

This document describes the more advanced features of Mindcode.

# Compiler directives

Mindcode allows you to alter some compiler options in the source code using special `#set` commands.
The basic syntax is: 

```
#set option = value;
```

Some of these options can be alternatively specified as parameters of the command line compiler.

Supported compiler options are described below.

## Option `target`

Use the `target` option to specify the Mindcode version:

```
#set target = ML6;
```

Possible values for this option are:
* `ML6`: compile for Mindcode Logic version 6
* `ML7S`: compile for Mindcode Logic version 7 standard processors
* `ML7W`: compile for Mindcode Logic version 7 world processor
* `ML7AS`: compile for Mindcode Logic version 7 (revision A) standard processors
* `ML7AW`: compile for Mindcode Logic version 7 (revision A) world processor
* `ML8AS`: compile for Mindcode Logic version 8 (revision A) standard processors
* `ML8AW`: compile for Mindcode Logic version 8 (revision A) world processor

## Option `remarks`

This option controls the way remarks, generated through the [remark() function](SYNTAX-4-FUNCTIONS.markdown#remarks), 
are propagated to the compiled code. Remarks are written into the compiled code as `print` instructions. Possible values 
of the `remarks` option are:

* `none`: remarks are suppressed in the compiled code - they do not appear there at all.
* `passive`: remarks are included in the compiled code, but a jump is generated in front each block of continuous 
  remarks, so that the print statement themselves aren't executed. This is the default value.
* `active`: remarks are included in the compiled code and are executed, producing actual output to the text buffer.

Passive remarks can be used for putting instructions or comments in the compiled code, or to mark a specific portion 
of the code. Remarks in a loop may help identifying individual iterations when the loop is unrolled, for example.

Active remarks can be used to easily add debugging output to a program that can be deactivated using a compiler 
option (potentially through a command line switch without modifying the source code).

## Option `goal`

Use the `goal` option to specify whether Mindcode should prefer to generate smaller code, or faster code. 
Possible values are:

* `size`: Mindcode tries to generate smaller code.
* `speed`: Mindcode can generate additional instructions, if it makes the resulting code faster while adhering to 
  the 1000 instructions limit. When several possible optimizations of this kind are available, the ones having the 
  best effect (highest speedup per additional instruction generated) are selected until the instruction limit is 
  reached. 
* `auto`: the default value. At this moment the setting is identical to `speed`.

## Option `sort-variables`

The **Vars** screen of the Mindustry processor shows all variables and their values, but the variables are displayed in the order in which they were created. This typically results in a very chaotic order of variables, where variables defined by the user are mixed with temporary variables, making it quite difficult to find a specific variable in sufficiently large programs.

This option can be used to make variables be displayed in a Mindustry processor in a well-defined order. Mindcode compiler ensures that by prepending a special block at the beginning of the program which creates user-defined variables in a specific order without altering their value. (The `packcolor` instruction is used, which can read - and therefore create - up to four variables per instruction. The result is not stored anywhere so that the variable-ordering code block doesn't change values of any variables, and therefore the behavior of the program remains unaltered, except for possible difference in timing.)

The value assigned to the sort-variables directive is a list of variable categories:

* `linked`: variables representing [linked blocks](SYNTAX-1-VARIABLES.markdown#linked-blocks), 
* `params`: variables representing [program parameters](SYNTAX-1-VARIABLES.markdown#program-parameters), 
* `globals`: [global variables](SYNTAX-1-VARIABLES.markdown#global-variables),
* `main`: [main variables](SYNTAX-1-VARIABLES.markdown#main-variables),
* `locals`: [local variables](SYNTAX-1-VARIABLES.markdown#local-variables),
* `all`: user variables that aren't matched by any other specified category,
* `none`: no variables at all. Can be used as a `#set sort-variables=none;`, ensuring that no variable ordering will be performed.  

It is possible to use the directive without specifying any value at all (`#set sort-variables;`). In this case, the categories will be processed in the order above.

When processing the directive, the categories are processed in the given order, with all variables in a category sorted alphabetically. This defines the resulting order of variables.

Note on the `linked` category: when a block is linked into the processor, a variable of that name is removed from the variable list. By putting the `linked` variables first, it is very easy to see which linked blocks used by the program are not linked under their proper names.

The number of variables being sorted is limited by the [instruction limit](#option-instruction-limit). Should the resulting program size exceeds the instruction limit, some or all variables will remain unordered.  

## Option `memory-model`

This option has been added to support future enhancements of Mindcode. Setting the option doesn't have any effect at 
this moment. 

## Option `instruction-limit`

This option allows to change the instruction limit used by [speed
optimization](SYNTAX-6-OPTIMIZATIONS.markdown#optimization-for-speed). The speed optimization strives not to exceed 
this instruction limit. In some cases, the optimization cost estimates are too conservative - some optimizations 
applied together may lead to code reductions that are not known to individual optimizers considering each 
optimization in isolation. In these cases, increasing the instruction limit might allow more optimizations to be 
performed. When the resulting code exceeds 1000 instructions, it is not usable in Mindustry processors and the 
option should be decreased or set back to 1000. (A new feature, which would perform this trial-and-error 
optimization automatically, is planned.)

The limit only affects the optimization for speed. The option has no effect on code generated by the compiler or 
optimizers which do not work in the speed optimization mode, and doesn't help reduce the code size generated outside 
the optimization for speed mechanism. 

It is also possible to decrease the instruction limit, if you wish so. The valid range for this compiler option is 1 
to 100,000 for the command-line tool, and 1 to 1,500 for the web application.

> [!IMPORTANT]
> Setting the limit to a very high value can have severe impact on the performance of the compiler. High 
> values of the instruction limit might cause the code compilation to take minutes or even hours to complete.

## Option `optimization`

Use the `optimization` option to set the optimization level of the compiler:

```
#set optimization = basic;
```

Possible values for this option are:

* `none`
* `basic`
* `advanced`
* `experimental`

The `none` setting deactivates all optimizations. The `basic` setting performs most of the available optimizations.
The `advanced` optimizations performs all the available optimizations, even those that might take more time, or 
which make changes that are potentially risky or make understanding of the resulting mlog code more difficult.

The `experimental` level is used for new optimizations that are still being evaluated, or for optimizations that might alter the meaning of existing code. Optimizations performed on the experimental level will typically be migrated to advanced or basic levels eventually.

The default optimization level for the web application compiler is `basic`, for the command line compiler it is 
`advanced`.

## Option `passes`

Use the `passes` option to set the maximum number of optimization passes to be done:

```
#set passes = 10;
```

The default value is 3 for the web application and 25 for the command line tool. The number of optimization passes
can be limited to a value between 1 and 1000 (inclusive).

A more complex code can usually benefit from more optimization passes. On the other hand, each optimization pass can
take some time to complete. Limiting the total number can prevent optimization from taking too much time or
consuming too many resources (this is a consideration for the web application).

## Individual optimization options

It is possible to set the level of individual optimization tasks. Every optimization is assigned a name,
and this name can be used in the compiler directive like this:

```
#set dead-code-elimination = advanced;
```

Not all optimizations support the `advanced` level. For those the level `advanced` is the same as `basic`.
The complete list of available optimizations, including the option name for setting the level of given optimization
and availability of the advanced optimization level is:

| Optimization                                                                                       | Option name                  | Advanced |
|----------------------------------------------------------------------------------------------------|------------------------------|:--------:|
| [Temporary Variables Elimination](SYNTAX-6-OPTIMIZATIONS.markdown#temporary-variables-elimination) | temp-variables-elimination   |    N     |
| [Case Expression Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#case-expression-optimization)       | case-expression-optimization |    N     |
| [Dead Code Elimination](SYNTAX-6-OPTIMIZATIONS.markdown#dead-code-elimination)                     | dead-code-elimination        |    Y     |
| [Jump Normalization](SYNTAX-6-OPTIMIZATIONS.markdown#jump-normalization)                           | jump-normalization           |    N     |
| [Jump Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#jump-optimization)                             | jump-optimization            |    N     |
| [Single Step Elimination](SYNTAX-6-OPTIMIZATIONS.markdown#single-step-elimination)                 | single-step-elimination      |    Y     |
| [Expression Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#expression-optimization)                 | expression-optimization      |    Y     |
| [If Expression Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#if-expression-optimization)           | if-expression-optimization   |    N     |
| [Data Flow Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization)                   | data-flow-optimization       |    Y     |
| [Loop Hoisting](SYNTAX-6-OPTIMIZATIONS.markdown#loop-hoisting)                                     | loop-hoisting                |    Y     |
| [Loop Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#loop-optimization)                             | loop-optimization            |    N     |
| [Loop Unrolling](SYNTAX-6-OPTIMIZATIONS.markdown#loop-unrolling)                                   | loop-unrolling               |    Y     |
| [Function Inlining](SYNTAX-6-OPTIMIZATIONS.markdown#function-inlining)                             | function-inlining            |    Y     |
| [Case Switching](SYNTAX-6-OPTIMIZATIONS.markdown#case-switching)                                   | case-switching               |    N     |
| [Return Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#return-optimization)                         | case-switching               |    N     |
| [Jump Straightening](SYNTAX-6-OPTIMIZATIONS.markdown#jump-straightening)                           | return-optimization          |    N     |
| [Jump Threading](SYNTAX-6-OPTIMIZATIONS.markdown#jump-threading)                                   | jump-threading               |    Y     |
| [Unreachable Code Elimination](SYNTAX-6-OPTIMIZATIONS.markdown#unreachable-code-elimination)       | unreachable-code-elimination |    Y     |
| [Stack Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#stack-optimization)                           | stack-optimization           |    N     |
| [Print Merging](SYNTAX-6-OPTIMIZATIONS.markdown#print-merging)                                     | print-merging                |    Y     |

This table doesn't track which optimizations provide some functionality on the `experimental` level. This information is available in the individual optimization documentation. 

You normally shouldn't need to deactivate any optimization, but if there was a bug in some of the optimizers,
deactivating it might allow you to use Mindcode until a fix is available.

In particular, some optimizers expect to work on code that was already processed by different optimizations,
so turning off some optimizations might render other optimizations ineffective. **This is not a bug.**  

# Creating custom mlog instructions

Mindcode provides a mechanism of encoding a custom instruction not known to Mindcode. Using custom instructions is useful in only a few distinct cases:

1. A new version of Mindustry (either an official release, or a bleeding edge version) creates new instructions not known to Mindcode.
2. An instruction was not implemented correctly in Mindcode and a fix is not available.
3. Mindustry was expanded to allow new instructions created by mods.

Custom instructions may interact with Mindustry World or provide information about Mindustry World. If an instruction alters the program flow (for example, if a new function call instruction was added to Mindustry Logic), it cannot be safely encoded using this mechanism. In addition, some custom instructions might break existing optimizations through their side effects. 

Custom instructions are created using the `mlog()` function:

* The first argument to the `mlog` function needs to be a string literal. This literal is the instruction code.
* All other arguments must be either literals, or user variables.
  * String literal: the text represented by the string literal is used as an instruction argument. If the `in` modifier is used, the string literal will be used as an argument including the enclosing double quotes.
  * Numeric literal: all other literals must not be marked with either modifier. The primary use for numeric literals is to provide fill-in values (typically zeroes) for unused instruction parameters.
  * User variable: the variable is used as an instruction argument. The argument must use the `in` and `out` modifier to inform Mindcode how the corresponding instruction argument behaves:
    * `in`: the argument represents an input value - the instruction reads and uses the value of the variable.
    * `out`: the argument represents an output value - the instruction produces a value and stores it in the variable.
    * `in out`: the argument represents an input/output value - the instruction both reads and uses the input value, and then updates the variable with a new value. With a possible exception to the `sync` instruction, no mlog instruction currently takes an input/output argument.

Mindcode assumes that a custom instruction interacts with the Mindustry World and cannot be safely removed from the program. This is not true for instructions which only return information about the Mindustry World, but do not modify or interact with it in any way. If you want to encode such an instruction, you can use the `mlogSafe()` function instead of `mlog()`.

> [!TIP]
> Although not strictly required, it is recommended to create an inline function with proper input/output parameters for each custom generated instruction. This way, the requirement that the `mlog()` function always uses user variables as arguments can be easily met, while allowing to use expressions for input parameters in the call to the enclosing function. See the examples below.  
     
For better understanding, the creating of custom instructions will be demonstrated on existing instructions. 

## The `format` instruction

The `format` instruction was introduced in Mindustry Logic 8. When compiling for target ML7A, the instruction isn't available. We can create it using this code:

```
#set target = ML7A;
inline void format(value)
    mlog("format", in value);
end;

param a = 10;
println("The value is: {0}");
format(a * 20);
```

Compiling this code produces the following output:

```
set a 10
print "The value is: {0}\n"
op mul __tmp0 a 20
format __tmp0
```

Considerations:

* The Print Merging optimization under ML8A target knows and properly handles the `format` instruction. When replaced by a custom instruction, the Print Merger won't be aware of it and might produce incorrect code. It would be necessary to turn off Print Merging optimization, if the `format` instruction was introduced in this way.
* THe processor emulator doesn't recognize custom instructions and won't handle them. The output produced by running the above code using the processor emulator would therefore be incorrect.

## The `draw print` instruction

Mindustry 8 Logic adds new variants of the `draw` instruction, `print` being one of them. Under the ML8A target, this instruction is mapped to the `drawPrint()` function. Unfortunately, this instruction takes an additional keyword argument - alignment, which needs special treatment when defining a custom instruction. Each possible value of alignment needs to be handled separately.

```
#set target = ML7A;
inline void drawPrintCenter(x, y)
    mlog("draw", "print", in x, in y, "center", 0, 0, 0);
end;

inline void drawPrintBottomLeft(x, y)
    mlog("draw", "print", in x, in y, "bottomLeft", 0, 0, 0);
end;

drawPrintCenter(0, 10);
drawPrintBottomLeft(0, 20);
```

Result:

```
draw print 0 10 center 0 0 0
draw print 0 20 bottomLeft 0 0 0
```

Considerations:

* The `draw print` instruction manipulates the text buffer and therefore interferes with the Print Merging optimization again. This optimization would need to be switched off.
* A separate function for each possible alignment is required. Unused functions aren't compiled, so there isn't any penalty with defining a function for each existing alignment, but it is tedious and cumbersome. Defining all possible variants for instructions that have several keyword arguments might become unfeasible.   

## The `ucontrol getblock` instruction

The `ucontrol getBlock` instruction is an example of instruction which has output parameters. Also, we know it is an instruction which doesn't modify the Mindustry World and therefore is safe. If not known by Mindcode, it could be defined like this:

```
// the ML7A target uses a bit different syntax for getBlock from the ML7 target.
#set target = ML7A;

// Using 'getBlock2' as a namoe to avoid clashing with the existing function name
inline def getBlock2(x, y, out type, out floor)
    mlog("ucontrol", "getBlock", in x, in y, out type, out building, out floor);
    return building;
end;

x = floor(rand(100));
y = floor(rand(200));
// These two instruction generate the same mlog code:
building = getBlock(x, y, out type, out floor);
print(building, type, floor);

building = getBlock2(x, y, out type, out floor);
print(building, type, floor);
```

compiles to

```
op rand __tmp0 100 0
op floor x __tmp0 0
op rand __tmp2 200 0
op floor y __tmp2 0
ucontrol getBlock x y type building floor
print building
print type
print floor
ucontrol getBlock x y __fn0_type __fn0_building __fn0_floor
print __fn0_building
print __fn0_type
print __fn0_floor
```

## The `jump` instruction

The `jump` instruction is a control flow instruction, an as such producing it through the `mlog()` function is strongly discouraged. Anyhow, Mindcode will allow the following code to be compiled:

```
mlog("jump", 50, "always");
```

producing 

```
jump 50 always
```

Considerations:

* There aren't direct ways to obtain the targets for the `jump` instruction. Forcing the instruction to target the intended code might be very difficult (albeit not outright impossible when modifying the instruction to target labels instead).
* Introducing jumps unrecognized by Mindcode would render most of the compiled code unsafe. Mindcode uses the knowledge of the program control flow to generate the code and make various optimizations. Introducing unrecognized control flow instructions would mean the knowledge is incorrect and the generated code possibly flawed.  

---

[« Previous: Functions](SYNTAX-4-FUNCTIONS.markdown) &nbsp; | &nbsp; [Next: Code optimization »](SYNTAX-6-OPTIMIZATIONS.markdown)
