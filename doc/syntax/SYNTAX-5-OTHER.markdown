# Compiler directives

Mindcode allows you to alter some compiler options in the source code using special `#set` commands. The basic syntax is: 

```
#set option = value;
```

Some of these options can be alternatively specified as parameters of the command line compiler.

Supported compiler options are described below.

## Option `auto-printflush`

Activates/deactivates automatic flushing of print output. Possible values are:

* `false`: Mindcode doesn't flush print output automatically.
* `true` (the default value): When the program contains at least one `print` or `printchar` instruction, and no `printflush` or `draw print` instructions, Mindcode adds a `printflush message1` instruction at the end of the main program body, and generates a warning.

This feature is meant for small, test scripts, where a call to `printflush()` is easily missed. This situation would otherwise require new compilation and code injection into the mlog processor when detected.           

## Option `boundary-checks`

This option activates/deactivates runtime checks and specifies which mechanism to use when runtime checks are active. Runtime checks are a debug feature and should be only active when developing/debugging your scripts, as they make the code larger and slower. 

Currently, runtime checks are generated for these operations:

* Accessing an element of an internal or external array by index: the check makes sure the index lies within bounds. Note that no runtime checks are generated when accessing memory block directly without a declared array, for example `cell1[index]` or `memory[index]`, where `memory` is a variable and not an external array do not have runtime checks generated. 

Possible values for the `boundary-checks` directive are:

* `none` (the default value): no runtime checks are generated.
* `assert`: runtime checks are generated using instructions provided by the [MlogAssertions mod](https://github.com/cardillan/MlogAssertions). The mod is available for Mindustry 7. If the mod is not installed, no runtime checks are performed, but otherwise the code runs as expected. Each runtime check takes one instruction. When the runtime check fails, the mod displays an error message over the processor for easier detection.   
* `minimal`: when the runtime check fails, the program execution stops on a `jump` instruction (this instruction permanently jumps to itself, which can be determined by inspecting the `@counter` variable in the **Vars** screen). Each runtime check takes two instructions.
* `simple`: when the runtime check fails, the program execution stops on a `stop` instruction (again, this can be determined by inspecting the `@counter` variable). Each runtime check takes three instructions.
* `described`: when the runtime check fails, the program execution stops on a `stop` instruction. However, a `print` instruction containing an error message is generated just before the `stop` instruction; after locating the faulting `stop` instruction, the error message can be read. Each runtime check takes four instructions.

## Option `builtin-evaluation`

This option specifies how Mindcode handles built-in variables (stable and unstable) having numeric values. Possible values are:

* `none`: no built-in variables are compile-time evaluated, 
* `compatible` (the default value): expressions using stable built-in variables will be compile-time evaluated when possible, 
* `full`: all expressions using built-in variables will be compile-time evaluated when possible.

Numerical built-in variables and logic IDs are considered stable if their value is the same in all known versions of Mindustry Logic in which they appear, and if they weren't removed in a later known Mindustry version.

On the `compatible` setting only the stable the built-in and logic ID values are evaluated at compile time. It is expected these values won't change in a future release. Unstable builtin variables and logic IDs are left to be evaluated by the processor at runtime. For example, in the following code

```Mindustry
for var unitIndex in 0 ... @unitCount do
    var unitType = lookup(:unit, unitIndex);
    // Handle the unit type, e.g., deflag all units of this type
end;
```

the loop won't be unrolled, and the code will loop through all unit types available in the version in which it is actually run.

The `full` setting ensures even the unstable built-in variables will be compile-time evaluated, using the value corresponding to the Mindustry version specified by the `target` option. This not only allows performing more optimizations (such as unroll loops) but also allows using the built-in variables to specify array sizes. However, the code will only produce a correct result when run on the processor of the correct Mindustry version:

```Mindcode
#set target = 7;
#set builtin-evaluation = full;

// This wouldn't compile on the compatible setting
var items[@itemCount];

for var i in 17 ... @itemCount do
    items[i] = lookup(:item, i); 
end;

for var item in items[17 ... length(items)] do
    println(item);
end; 

printflush(message1);
```

compiles to

```mlog
print "fissile-matter\ndormant-cyst\ntungsten\ncarbide\noxide\n"
printflush message1
```

When `target` is set to `8`, the code instead compiles to

```
print "tungsten\noxide\ncarbide\n"
printflush message1
```

(Notice the difference—running the code on a processor different from the compilation target produces incorrect results.)

## Option `case-optimization-strength`

This option affects the number of segment arrangements considered when the Case Switching optimization performs [jump table compression](SYNTAX-6-OPTIMIZATIONS.markdown#jump-table-compression). The higher the number, the more segment arrangements are considered, but the more time is needed for generating and evaluating them. The default value of this option is `2` for the web application, and `3` for the command-line tool. The maximal possible value is `4` for the web application, and `6` for the command-line tool. Values larger than `4` typically only bring additional benefits for case expressions with a very complex structure. Increasing the value by one significantly increases both the number of segment arrangements and the optimization time. 

Setting the optimization strength to `0` causes the optimizer to forgo considering any segment arrangements and only consider turning the entire case statement into a jump table.

Setting the optimization strength to `6` may lead to very long compilation time (from tens of seconds to minutes or more), and only improves the efficiency of very complex case expressions.

> [!NOTE]
> This option only improves [compressed jump tables](SYNTAX-6-OPTIMIZATIONS.markdown#jump-table-compression). If there's enough instruction space for a full jump table, and the [optimization goal](#option-goal) is set to `speed`, increasing the value of this option cannot bring additional benefit, but still increases the compilation time.

## Option `function-prefix`

This option specifies the function prefix to use to generate mlog names of local variables. Possible values are:

* `long` (the default value): the prefix is composed of a function name and a number, starting at 0. Variable names may become long and often will be incompletely displayed in Mindustry interface, but the function name is part of variable name, making it easier to recognize each variable.   
* `short`: the prefix is `:fn` followed by a number, starting at 0. Leads to short variable names, which are more easily readable when displayed in Mindustry interface, but it is not immediately noticeable which function each variable belongs to.

Note: the function prefix of remote functions is always `:` followed by the function name, with no additional number, regardless of this setting. 

## Option `goal`

Use the `goal` option to specify whether Mindcode should prefer to generate smaller code, or faster code. Possible values are:

* `speed` (the default value): Mindcode can generate additional instructions if it makes the resulting code faster while adhering to the current [instruction limit](#option-instruction-limit). When several possible optimizations of this kind are available, the ones having the best effect (the highest speedup per additional instruction generated) are applied first, until the instruction limit is reached.
* `neutral`: When an optimization making the code either smaller or faster than the original code can be found, it is applied. Otherwise, the original code is used. The optimized code should never be larger or slower than the original code.
* `size`: Mindcode tries to generate the smallest code possible, even at the expense of execution speed.

See [Dynamic optimizations](SYNTAX-6-OPTIMIZATIONS.markdown#dynamic-optimizations) for a detailed explanation of the optimization goals. 

## Option `instruction-limit`

This option allows changing the instruction limit applied to the [`speed` optimization goal](#option-goal). The speed optimization strives not to exceed this instruction limit. In some cases, the optimization cost estimates are too conservative—some optimizations applied together may lead to code reductions that are not known to individual optimizers considering each optimization in isolation. In these cases, increasing the instruction limit might allow more optimizations to be performed. When the resulting code exceeds 1000 instructions, it is not usable in Mindustry processors, and the option should be decreased or set back to 1000. (A new feature, which would perform this trial-and-error optimization automatically, is planned.)

The limit only affects the optimization for speed. The option has no effect on code generated by the compiler or optimizers, which do not work in the speed optimization mode, and doesn't help reduce the code size generated apart from the optimization for speed mechanism.

It is also possible to decrease the instruction limit if you wish so. The valid range for this compiler option is 1 to 100,000 for the command-line tool, and 1 to 1,500 for the web application.

> [!IMPORTANT]
> Setting the limit to a very high value can have a severe impact on the performance of the compiler. High values of the instruction limit might cause the code compilation to take minutes or even hours to complete.

## Option `mlog-block-optimization`

Activates/deactivates a limited [Data Flow optimization](SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization) inside [mlog blocks](SYNTAX-EXTENSIONS.markdown#mlog-blocks). Possible values are:

* `false` (the default value): no optimization is applied to the mlog blocks.
* `true`: the Data Flow Optimization may replace input/output mlog variables used in instructions with different variables/values. 

## Option `mlog-indent`

Allows setting the length of an indenting prefix (the number of spaces) applied to the generated mlog code. Allowed values are `0` to `8`, where `0` disables the indenting entirely. The indenting level is derived from the nesting level of the corresponding source-code construct. 

Default value is `0` when `symbolic-labels` is set to `false`, and `4` when `symbolic-labels` is set to `true`.

For an example of an indented mlog code, see the [`symbolic-labels` option](#option-symbolic-labels).

## Option `optimization`

Use the `optimization` option to set the optimization level of the compiler:

```
#set optimization = basic;
```

Possible values for this option are:

* `none`: deactivates all optimizations.
* `basic`: performs most optimizations, except those that depend on certain assumptions about the program or Mindustry Logic.
* `advanced`: performs additional optimizations based upon some assumptions about Mindustry Logic (e.g., that numerical ID produced by a lookup instruction for Mindustry content elements is stable) or the source code (e.g., that it doesn't depend on expressions converting null values to non-null ones).
* `experimental`: perform optimizations that are currently in the experimental phase.

The default optimization level is `advanced`.

## Option `output-profiling`

Setting this option to `true` activates outputting the profiling information (the number of times each instruction was executed) to the log file. The profiling information is output only when the code has actually been executed.

Example of the produced output:

```
Code profiling result:

     1: set :n 0
     1: jump 0 greaterThanEq 0 @links
    36: getlink :reactor :n
    36: sensor *tmp1 :reactor @type
    36: jump 10 notEqual *tmp1 @thorium-reactor
     0: sensor *tmp6 :reactor @cryofluid
     0: sensor *tmp7 :reactor @liquidCapacity
     0: op mul *tmp8 0.25 *tmp7
     0: op greaterThanEq *tmp9 *tmp6 *tmp8
     0: control enabled :reactor *tmp9 0 0 0
    36: op add :n :n 1
    36: jump 2 lessThan :n @links
```

## Option `passes`

Use the `passes` option to set the maximum number of optimization passes to be done:

```
#set passes = 10;
```

The default value is 3 for the web application and 25 for the command line tool. The number of optimization passes can be limited to a value between 1 and 1000 (inclusive).

A more complex code can usually benefit from more optimization passes. On the other hand, each optimization pass can take some time to complete. Limiting the total number can prevent optimization from taking too much time or consuming too many resources (this is a consideration for the web application).

## Option `remarks`

This option controls the way remarks, generated through the [remark() function](SYNTAX-4-FUNCTIONS.markdown#remarks), are propagated to the compiled code. Remarks are written into the compiled code as `print` instructions. Possible values of the `remarks` option are:

* `none`: remarks are suppressed in the compiled code—they do not appear there at all.
* `comments`: remarks are output as mlog comments into the compiled code. Expressions in remarks are output as separate comments with the mlog name of the variable holding the value of the expression.
* `passive`: remarks are included in the compiled code, but a jump is generated in front each block of continuous remarks, so that the print statements themselves aren't executed. This is the default value.
* `active`: remarks are included in the compiled code and are executed, producing actual output to the text buffer.

Converting remarks to comments may improve code readability. Remarks in a loop may help identify individual iterations when the loop is unrolled, for example. However, when the code is inserted into an mlog processor, the comments are not preserved.

Passive remarks can be used for putting instructions or comments in the compiled code in a way which is still visible in the game UI.

Active remarks can be used to easily add debugging output to a program that can be deactivated using a compiler option (potentially through a command line switch without modifying the source code).

## Option `sort-variables`

The **Vars** screen of the Mindustry processor shows all variables and their values, but the variables are displayed in the order in which they were created. This typically results in a very chaotic order of variables, where variables defined by the user are mixed with temporary variables, making it quite difficult to find a specific variable in large enough programs.

This option can be used to make variables be displayed in a Mindustry processor in a well-defined order. Mindcode compiler ensures that by prepending a special non-executable block at the beginning of the program which creates user-defined variables in a specific order without altering their value. (The `draw triangle` instruction is used, which can create up to six variables per instruction. Since the entire segment of `draw triangle` instruction is skipped, the behavior of the program remains unaltered, except for possible difference in timing.)

The value assigned to the sort-variables directive is a list of variable categories:

* `linked`: variables representing [linked blocks](SYNTAX-1-VARIABLES.markdown#linked-blocks),
* `params`: variables representing [program parameters](SYNTAX-1-VARIABLES.markdown#program-parameters),
* `globals`: [global variables](SYNTAX-1-VARIABLES.markdown#variable-scope),
* `main`: [main variables](SYNTAX-1-VARIABLES.markdown#variable-scope),
* `locals`: [local variables](SYNTAX-1-VARIABLES.markdown#variable-scope),
* `all`: user variables, which aren't matched by any other specified category,
* `none`: no variables at all. Can be used as a `#set sort-variables=none;`, ensuring that no variable ordering will be performed.

It is possible to use the directive without specifying any value at all (`#set sort-variables;`). In this case, the categories will be processed in the order above.

When processing the directive, the categories are processed in the given order, with all variables in a category sorted alphabetically. This defines the resulting order of variables.

Note on the `linked` category: when a block is linked into the processor, a variable of that name is removed from the variable list. By putting the `linked` variables first, it is easier to see which linked blocks used by the program are not linked under their proper names.

The number of variables being sorted is limited by the [instruction limit](#option-instruction-limit). Should the resulting program size exceed the instruction limit, some or all variables will remain unordered.

## Option `symbolic-labels`

Activates/deactivates generating symbolic labels for jump instruction targets. Possible values are:

* `false` (the default value): the compiled code contains absolute instruction addresses in `jump` instructions. Absolute addresses are also used to store program locations in variables or to compute offsets in jump tables.
* `true`: the compiled code contains symbolic labels in `jump` instructions. Program locations stored in variables are always derived from actual `@counter` values, including `@counter` manipulations in jump tables.

As a consequence, the compiled mlog code can be altered by adding and/or removing instructions when symbolic labels are produced. Instructions must not be added to or removed from sections of code where the `@counter` variable is manipulated. All Mindcode features are supported when generating code using symbolic labels, including function call and remote function calls, list iteration loops, internal arrays, and so on.

When symbolic labels are produced, the compiler also uses mlog comments to mark function entry points.

> [!NOTE]
> Activating symbolic labels may increase program size and decrease execution speed.
 
The following features are affected when activating symbolic labels:

* List iteration loops: the number of instructions used to create a list iteration loop code is increased by two.
* Stackless function calls: instruction setting up a function return address cannot be hoisted.
* Recursive function calls: the size and execution time of a recursive function call are increased by one.
* Regular internal array element access (inlined array access is not affected):
  * the size of a jump table and execution time of array access are increased by one,
  * instructions setting up a function return address cannot be hoisted.
* Optimized case expressions: the size and execution time of an optimized case expression may increase.

Example of a program compiled with mlog comments and symbolic labels:

```Mindcode
#set syntax = strict;
#set remarks = comments;
#set symbolic-labels = true;

/// Number of units we want to control
param target = 2;

/// Type of unit
param type = @mono;

/// Flag to mark our units
var myFlag = rand(1e10);

begin
    noinit var firstUnit, active, change;

    while true do
        ubind(type);
        /// Remember first unit we found to be aware we're looping again
        if firstUnit.@dead then
            firstUnit = @unit;
            active = 0;
            change = 0;
        elsif @unit == firstUnit then
            /// We've completed a loop: visited all existing units once
            /// Compute how many units we want to acquire/release to meet the target
            /// When change is negative, we need to drop units
            change = target - active;

            /// Counts active units. Contains valid value when completing the loop.
            active = 0;
        end;
        var unitFlag = @unit.@flag;

        if unitFlag == 0 then
            /// This is a free unit
            if change > 0 then
                /// We're acquiring a new unit
                change--;
                flag(myFlag);
            else
                /// We don't need a new unit, skip it
                continue;
            end;
        else
            /// If not our unit, skip it
            if unitFlag != myFlag then
                continue;
            end;
        end;

        /// This is our unit.
        if change < 0 then
            /// The unit is superfluous: free it
            flag(0);
            unbind();
            change++;
            /// Skip processing for this unit
            continue;
        end;

        /// We found an active unit, count it
        active++;

        /// Handle your unit here
        var angle = 30 * active + @tick;
        move(@thisx + 15 * sin(angle), @thisy + 15 * cos(angle));
        /// End of unit handling
    end;
end;
```

compiles to

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    # Number of units we want to control
    set target 2
    # Type of unit
    set type @mono
    # Flag to mark our units
    op rand .myFlag 10000000000 0
label_3:
        ubind type
        # Remember first unit we found to be aware we're looping again
        sensor *tmp1 :firstUnit @dead
        jump label_10 equal *tmp1 false
            set :firstUnit @unit
            set :active 0
            set :change 0
            jump label_13 always 0 0
        label_10:
            jump label_13 notEqual @unit :firstUnit
                # We've completed a loop: visited all existing units once
                # Compute how many units we want to acquire/release to meet the target
                # When change is negative, we need to drop units
                op sub :change target :active
                # Counts active units. Contains valid value when completing the loop.
                set :active 0
        label_13:
        sensor :unitFlag @unit @flag
        jump label_20 notEqual :unitFlag 0
            # This is a free unit
            jump label_19 lessThanEq :change 0
                # We're acquiring a new unit
                op sub :change :change 1
                ucontrol flag .myFlag 0 0 0 0
                jump label_21 always 0 0
                # We don't need a new unit, skip it
            label_19:
                jump label_3 always 0 0
            # If not our unit, skip it
        label_20:
            jump label_3 notEqual :unitFlag .myFlag
        # This is our unit.
        label_21:
        jump label_26 greaterThanEq :change 0
            # The unit is superfluous: free it
            ucontrol flag 0 0 0 0 0
            ucontrol unbind 0 0 0 0 0
            op add :change :change 1
            # Skip processing for this unit
            jump label_3 always 0 0
        # We found an active unit, count it
        label_26:
        op add :active :active 1
        # Handle your unit here
        op mul *tmp19 30 :active
        op add :angle *tmp19 @tick
        op sin *tmp21 :angle 0
        op mul *tmp22 15 *tmp21
        op add *tmp23 @thisx *tmp22
        op cos *tmp24 :angle 0
        op mul *tmp25 15 *tmp24
        op add *tmp26 @thisy *tmp25
        ucontrol move *tmp23 *tmp26 0 0 0
        # End of unit handling
        jump label_3 always 0 0
```

## Option `syntax`

Chooses the [syntax mode](SYNTAX.markdown#syntax-modes) to be used for compilation. Possible values are:

* `relaxed` (the default value): useful for shorter scripts, as it requires less boilerplate code.
* `strict`: useful for larger projects, as it enforces additional rules designed to make source code more maintainable.
* `mixed`: designed to help with a transition of relaxed syntax code to the strict standard. In this mode, code is compiled using the relaxed syntax rules, but all violations of the strict syntax rules are reported as warnings.

## Option `target`

Use the `target` option to specify the Mindcode/Mindustry Logic version to be used by the compiler and processor emulator. Compiler will generate code compatible with the selected processor version and edition, and both compiler and processor emulator recognize Mindustry objects, built-in variables and other elements available in a given Mindustry Logic version.

The target versions consist of a major and minor version number. As of now, these versions exist:

| Version | Description                                                      |
|:-------:|:-----------------------------------------------------------------|
|   6.0   | Mindustry Logic for the latest release of Mindustry 6.           |
|   7.0   | Mindustry Logic for the latest release of Mindustry 7.           |
|   7.1   | As above, with slightly revised syntax of some functions.        |
|   8.0   | Mindustry Logic for the latest BE release of future Mindustry 8. |

Version 8.0 will be the version corresponding to the official Mindustry 8 release. Future enhancements of Mindustry 8 Logic or, possibly, of the Mindcode language will be incorporated as separate versions with updated minor version number.

The target can be set using either just a major or both major and minor version numbers. When specifying both numbers, the specified version is used. When specifying just the major version, the most recent minor version in the given major category is used. Example:

```
#set target = 7;        // Sets version 7.1
#set target = 7.0;      // Sets version 7.0
```

To use a world-processor variant of Mindcode language, it is necessary to add `W` as a suffix to the version number:

```
#set target = 8W;     // World-processor logic version 8
#set target = 7.0W    // World-processor logic version 7.0
```

The same names of version targets are used with the `-t` / `--target` command-line option.

## Option `target-guard`

When set, adds a guard code to the beginning of the program which verifies the code is run by a Mindustry version compatible with both the `target` and `builtin-evaluation` options. If the processor isn't compatible with the compiler options used to generate the code, the execution remains stuck at the beginning of the program. This prevents the code from running on an incompatible processor, resulting in potentially faulty execution of the code.

THe target guard code doesn't distinguish between world and standard processors. 

The guard code is always a single `jump` instruction which jumps back to itself if an incompatible processor is detected. The following table shows the guard instructions corresponding to given target settings:

| Target and optimization | Instruction                        |
|-------------------------|------------------------------------|
| 6, compatible           | No test, code runs on all versions |
| 6, specific             | `jump 0 greaterThan %FFFFFF 0`     |
| 7, compatible           | `jump 0 strictEqual %FFFFFF null`  |
| 7, specific             | `jump 0 notEqual @blockCount 254`  |
| 8, compatible           | `jump 0 strictEqual %[red] null`   |
| 8, specific             | `jump 0 strictEqual %[red] null`   |

The jump target (`0`) is replaced with proper instruction address when it's not the first in the compiled code.

## Option `text-jump-tables`

The Mindustry 8 Logic ability to [read values from strings](MINDUSTRY-8.markdown#reading-characters-from-strings) makes it possible to encode instruction addresses into a string. This option governs whether the compiler will use this possibility. Possible values are:

* `false`: text-based jump tables won't be generated.
* `true` (the default value): the compiler generates text-based jump tables where possible.

Text-based jump tables allow very efficient encoding of jump tables, both in terms of program size and execution time. The functionality, however, requires storing unprintable characters in the string. If this poses a problem, the option can be set to `false` to disable text-based jump tables.

## Option `unsafe-case-optimization`

This option instructs the compiler to drop range checking when performing case expression optimization. For more information, [Range check elimination](SYNTAX-6-OPTIMIZATIONS.markdown#range-check-elimination).

## Individual optimization options

It is possible to set the level of individual optimization tasks. Every optimization is assigned a name, and this name can be used in the compiler directive like this:

```
#set expression-optimization = basic;
```

Most optimizations don't support the `advanced` level. For those the level `advanced` is the same as `basic`. The complete list of available optimizations, including the option name for setting the level of the given optimization and availability of the advanced optimization level, is:

| Optimization                                                                                       | Option name                  | Advanced |
|----------------------------------------------------------------------------------------------------|------------------------------|:--------:|
| [Temporary Variables Elimination](SYNTAX-6-OPTIMIZATIONS.markdown#temporary-variables-elimination) | temp-variables-elimination   |    N     |
| [Case Expression Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#case-expression-optimization)       | case-expression-optimization |    N     |
| [Dead Code Elimination](SYNTAX-6-OPTIMIZATIONS.markdown#dead-code-elimination)                     | dead-code-elimination        |    N     |
| [Jump Normalization](SYNTAX-6-OPTIMIZATIONS.markdown#jump-normalization)                           | jump-normalization           |    N     |
| [Jump Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#jump-optimization)                             | jump-optimization            |    N     |
| [Single Step Elimination](SYNTAX-6-OPTIMIZATIONS.markdown#single-step-elimination)                 | single-step-elimination      |    N     |
| [Expression Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#expression-optimization)                 | expression-optimization      |    Y     |
| [If Expression Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#if-expression-optimization)           | if-expression-optimization   |    N     |
| [Data Flow Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization)                   | data-flow-optimization       |    N     |
| [Loop Hoisting](SYNTAX-6-OPTIMIZATIONS.markdown#loop-hoisting)                                     | loop-hoisting                |    N     |
| [Loop Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#loop-optimization)                             | loop-optimization            |    N     |
| [Loop Unrolling](SYNTAX-6-OPTIMIZATIONS.markdown#loop-unrolling)                                   | loop-unrolling               |    Y     |
| [Function Inlining](SYNTAX-6-OPTIMIZATIONS.markdown#function-inlining)                             | function-inlining            |    N     |
| [Case Switching](SYNTAX-6-OPTIMIZATIONS.markdown#case-switching)                                   | case-switching               |    Y     |
| [Array Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#array-optimization)                           | array-optimization           |    N     |
| [Return Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#return-optimization)                         | case-switching               |    Y     |
| [Jump Straightening](SYNTAX-6-OPTIMIZATIONS.markdown#jump-straightening)                           | return-optimization          |    N     |
| [Jump Threading](SYNTAX-6-OPTIMIZATIONS.markdown#jump-threading)                                   | jump-threading               |    N     |
| [Unreachable Code Elimination](SYNTAX-6-OPTIMIZATIONS.markdown#unreachable-code-elimination)       | unreachable-code-elimination |    N     |
| [Stack Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#stack-optimization)                           | stack-optimization           |    N     |
| [Print Merging](SYNTAX-6-OPTIMIZATIONS.markdown#print-merging)                                     | print-merging                |    Y     |

This table doesn't track which optimizations provide some functionality on the `experimental` level. This information is available in the individual optimization documentation. 

You normally shouldn't need to deactivate any optimization, but if there was a bug in some optimizer, deactivating it might allow you to use Mindcode until a fix is available.

In particular, some optimizers expect to work on code that was already processed by different optimizations, so turning off some optimizations might render other optimizations ineffective. 

---

[« Previous: Functions](SYNTAX-4-FUNCTIONS.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Code optimization »](SYNTAX-6-OPTIMIZATIONS.markdown)
