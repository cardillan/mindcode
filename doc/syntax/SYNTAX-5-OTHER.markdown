# Compiler Options

Mindcode allows you to alter some compiler options in the source code using special `#set` and `#setlocal` directives. The basic syntax is: 

```
#set option = value;
```
or
```
#setlocal option = value;
```

Some of the compiler options can be also specified as parameters of the command line compiler. A compiler option specified in the source file using the `#set` directive takes precedence over the option specified as a parameter of the command line compiler. The `#set` directives can be placed anywhere in the source file. We suggest placing them at the very top of each file, before every other directive or statement.

When the same option is set multiple times (either on the command line or in the source file), the last occurrence of the option is used.

## Option scope

Each compiler option resides in a certain _scope_, which determines where the option values can be specified and how they are applied. 

### Global scope

Options in the global scope can be set on the command line or in the main source file using the `#set` directive. These options govern the entire compilation. The `#set` directives of global options inside a module imported through the `require` directive are ignored and are only processed when the module is compiled independently.

### Module scope

Options in the module scope can be set on the command line or using the `#set` directive. When used inside a module imported through the `require` directive, it sets the option for the entire module. Modules imported via the `require` directive from another module inherit options from the global scope, not from the module that required them, to avoid conflicts in case a nested module gets imported from two different modules using different options.

### Local scope

Options in the local scope can be set on the command line or using the `#set` or `#setlocal` directives. Options set using the `#set` directive are inherited by modules in the same way as options in the  `module` scope.   

The `#setlocal` directives only apply to the next statement. Multiple `#setlocal` directives can precede a single statement, in which case all are applied. To apply the directive to more statements in succession, it is possible to use a code block (`begin ... end`). The `#setlocal` directive can precede a function declaration, in which case it sets the option for the entire function body.

Trying to set a non-local compiler option using the `#setlocal` directive causes an error.

## Inline functions

When a function being called is declared `inline`, options set at the module level are replaced with options effective at the call site. Options set locally for the function declaration (by a `#setlocal` directive preceding the function declaration) are preserved.

Functions not explicitly declared `inline` are always compiled using the options effective in the module containing the function declaration, or by the `#setlocal` directive preceding the function declaration. This is the case even when the function gets inlined later on either because it is called just once or by the Function Inlining optimization.

## Semantic stability

Some compiler options may change the semantic of the affected code. For example, the `unsafe-case-optimization` is such an option: when set to `true`, unhandled case values may cause undefined behavior, when set to `false`, all unhandled case values are handled by the `else` branch. Such options are _semantically unstable_.

Semantically unstable compiler options are handled specifically in these contexts:

* The values of these options aren't inherited from the global scope for modules. Unless explicitly set within a module, the semantic-altering options have their default values.
* When a function is declared `inline`, the semantically unstable options keep the values effective at the function declaration.

# Option reference

## Environment options

Options to specify the target environment for the code being compiled. This includes the Mindustry version,
as well as prescribing which specific processor features may or may not be used.

| Option                                               | Scope  | Semantic stability |
|------------------------------------------------------|--------|--------------------|
| [builtin-evaluation](#option-builtin-evaluation)     | global | stable             |
| [instruction-limit](#option-instruction-limit)       | global | stable             |
| [null-counter-is-noop](#option-null-counter-is-noop) | global | stable             |
| [target](#option-target)                             | module | stable             |

### Option `builtin-evaluation`

**Option scope: [global](#global-scope)**

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

compiles to:

```mlog
print "fissile-matter\ndormant-cyst\ntungsten\ncarbide\noxide\n"
printflush message1
```

When `target` is set to `8`, the code instead compiles to:

```
print "tungsten\noxide\ncarbide\n"
printflush message1
```

(Notice the difference—running the code on a processor different from the compilation target produces incorrect results.)

> [!NOTE]
> You can use the [`target-guard` option](#option-target-guard) to ensure your code will only run on the expected version of Mindustry.

### Option `instruction-limit`

**Option scope: [global](#global-scope)**

Sets the instruction limit: the maximum number of instructions that can be placed into a Mindustry processor. The standard value is 1000, although some mods allow changing this value within the game.

Mindcode uses the instruction limit with the [`speed` optimization goal](#option-goal): optimizations for speed must not exceed the instruction limit. In some cases, the optimization cost estimates are too conservative—some optimizations applied together may lead to code reductions that are not known to individual optimizers considering each optimization in isolation. In these cases, increasing the instruction limit might allow more optimizations to be performed. When the resulting code exceeds 1000 instructions, it is not usable in regular Mindustry processors, and the option should be decreased or set back to 1000. (A new feature, which would perform this trial-and-error optimization automatically, is planned.)

The limit only affects the optimization for speed. The option has no effect on code generated by the compiler or optimizers, which do not work in the speed optimization mode and doesn't help reduce the code size generated apart from the optimization for speed mechanism.

It is also possible to decrease the instruction limit if you wish so. The valid range for this compiler option is 1 to 100,000 for the command-line tool, and 1 to 1,500 for the web application.

> [!IMPORTANT]
> Setting the limit to a very high value can have a severe impact on the performance of the compiler. High values of the instruction limit might cause the code compilation to take minutes or even hours to complete.

### Option `null-counter-is-noop`

**Option scope: [global](#global-scope)**

Informs Mindcode how operations assigning `null` to `@counter` are handled by the processor. Possible values are:

* `false` (the default value for target 8.0): assigning `null` to `@counter` is interpreted by the processor (possibly by jumping to address #0).
* `true` (the default value for all other targets): assigning `null` to `@counter` is ignored by the processor. Mindcode may generate code depending on this behavior.

In the past, Mindustry processor behavior has been inconsistent when assigning `null` to `@counter`. To allow Mindcode to produce the correct code in case it gets changed again, this option has been added.

### Option `target`

**Option scope: [module](#module-scope)**

Use the `target` option to specify the Mindcode/Mindustry Logic version and the processor type to be used by the compiler and processor emulator. Compiler will generate code compatible with the selected processor version and type, and both the compiler and processor emulator recognize Mindustry objects, built-in variables and other elements available in a given Mindustry Logic version.

The target versions consist of a major and minor version number. As of now, these versions exist:

| Version | Description                                                      |
|:-------:|:-----------------------------------------------------------------|
|   6.0   | Mindustry Logic for the latest release of Mindustry 6.           |
|   7.0   | Mindustry Logic for the latest release of Mindustry 7.           |
|   7.1   | As above, with slightly revised syntax of some functions.        |
|   8.0   | Mindustry Logic for the v8 Build 149 Beta Mindustry release.     |
|   8.1   | Mindustry Logic for the latest BE release of future Mindustry 8. |

The target can be set using either just a major or both major and minor version numbers. When specifying both numbers, the specified version is used. When specifying just the major version, the most recent minor version in the given major category is used. Example:

```
#set target = 7;        // Sets version 7.1
#set target = 7.0;      // Sets version 7.0
```

The processor type is specified by appending a one-letter suffix to the version number:

* `m` or `M` for micro-processor (the default when not specified),
* `l` or `L` for logic-processor,
* `h` or `H` for hyper-processor,
* `w` or `W` for world-processor.

```
#set target = 7.0w    // World-processor, version 7.0
#set target = 8L;     // Logic-processor, version 8
```

Note that when the Mindcode is compiled to configure a schematic-defined processor when building a schematic, the processor type is taken from the schematic definition and the target version is ignored. A warning is emitted   

#### Module targets

Option `target`, when set within a module, specifies the target supported by the module. If the global `target` setting isn't compatible with the target specified by the module, a compilation error occurs with a message 'Module target \<target\> is incompatible with global target \<target\>.'

Target compatibility matrix:

| Module target | Compatible global targets |
|---------------|---------------------------|
| 6.0           | 6.0, 7.0                  |
| 7.0           | 7.0                       |
| 7.1           | 7.1 or higher             |
| 8.0           | 8.0 or higher             |
| 8.1           | 8.1 or higher             |

Note: the incompatibility between 7.0 and 7.1 is caused by different instruction mappings.

Generally, a module target is compatible with the same or higher global target, unless a backwards-incompatible feature is introduced to Mindustry Logic or Mindcode.

## Mlog formatting options

Options determining how the mlog code is generated and formatted, including instructions generated to
carry over specific information into the compiled program (such as signature or processor ID).

| Option                                                   | Scope  | Semantic stability |
|----------------------------------------------------------|--------|--------------------|
| [author](#option-author)                                 | global | stable             |
| [encode-zero-characters](#option-encode-zero-characters) | global | stable             |
| [function-prefix](#option-function-prefix)               | global | stable             |
| [mlog-indent](#option-mlog-indent)                       | global | stable             |
| [no-argument-padding](#option-no-argument-padding)       | global | stable             |
| [processor-id](#option-processor-id)                     | global | stable             |
| [program-name](#option-program-name)                     | global | stable             |
| [program-version](#option-program-version)               | global | stable             |
| [symbolic-labels](#option-symbolic-labels)               | global | stable             |

### Option `author`

**Option scope: [global](#global-scope)**

Adds an author to the list of the program's authors. The author list is added to the end of the program alongside the signature if there's enough instruction space and the program doesn't loop naturally. Multiple authors can be listed in a single directive. The author's name is normally enclosed in double quotes. Double quotes are optional if the name consists only of alphanumerical characters and underscores (no spaces).

The `author` directive can be specified multiple times, and can be included in a module or library. When such a module is required by the main program, the authors are added to the list of the main program's authors.

Example:

```Mindcode
#set target = 8;
#set author = cardillan, "Mickey Mouse";
#set author = "Rameses Niblick the Third";

noinline void foo()
    print("foo");
end;
foo();
```

compiles to:

```mlog
set :foo*retaddr 2
jump 3 always 0 0
end
print "foo"
set @counter :foo*retaddr
print "Created by cardillan, Mickey Mouse and Rameses Niblick the Third"
print "Compiled by Mindcode - github.com/cardillan/mindcode"
```

### Option `encode-zero-characters`

**Option scope: [global](#global-scope)**

Allows encoding zero characters into mlog string literals. Possible values are:

* `false` (the default value): mlog string literals containing zero characters are never created.
* `true`: when needed, mlog string literals containing zero characters can be created.

Zero characters represent zero and `false` values in string literals and may allow more efficient encoding of some expressions. 

> [!WARNING]
> Texts containing zero characters cannot be transferred via clipboard at all and oftentimes pose a challenge to text editors.
> 
> The only way to insert code containing zero characters into Mindustry processors is via the MlogWatcher mod, or by generating and importing a schematic (a schematic can be imported via clipboard, even when the encoded code contains zero characters).
> 
> The only way to copy the original mlog code from a processor in this situation is by creating and exporting a schematic containing the processor and extracting the mlog code from the schematic.
> 
> Use this option with caution. We only recommend using it when creating a schematic that won't be subsequently edited.

In the web app, setting this option to `true` has no effect. When an mlog code containing zero characters is created in the command-line tool, and copying the resulting mlog code into a clipboard is requested, the code won't be copied to clipboard and an error message will be displayed instead. 

### Option `function-prefix`

**Option scope: [global](#global-scope)**

This option specifies the function prefix to use to generate mlog names of local variables. Possible values are:

* `long` (the default value): the prefix is composed of a function name and a number, starting at 0. Variable names may become long and often will be incompletely displayed in Mindustry interface, but the function name is part of variable name, making it easier to recognize each variable.
* `short`: the prefix is `:fn` followed by a number, starting at 0. Leads to short variable names, which are more easily readable when displayed in Mindustry interface, but it is not immediately noticeable which function each variable belongs to.

Note: the function prefix of remote functions is always `:` followed by the function name, with no additional number, regardless of this setting.

### Option `mlog-indent`

**Option scope: [global](#global-scope)**

Allows setting the length of an indenting prefix (the number of spaces) applied to the generated mlog code. Allowed values are `0` to `8`, where `0` disables the indenting entirely. The indenting level is derived from the nesting level of the corresponding source-code construct.

Default value is `0` when `symbolic-labels` is set to `false`, and `4` when `symbolic-labels` is set to `true`.

For an example of an indented mlog code, see the [`symbolic-labels` option](#option-symbolic-labels).

### Option `no-argument-padding`

**Option scope: [global](#global-scope)**

Disables padding unused arguments in generated instructions with zero values. Possible values are:

* `false` (the default value): instructions in the compiled code are padded with zero values in unused arguments. The format of the instructions generated this way matches the format produced by Mindustry when copying code from a processor to the clipboard.
* `true`: instructions in the compiled code won't contain unused arguments. Generally, this makes the code more readable.

### Option `processor-id`

**Option scope: [global](#global-scope)**

Specifies the processor ID, which is an arbitrary string value. Processor ID is encoded, together with the program name and program version, into the `*id` variable, and the encoded value is also available as `@@ID` within Mindcode.

```Mindcode
#set processor-id = Main;
#set program-name = "A sample program";
#set program-version = "v0.1.0";

println(@@ID);
```

compiles to:

```mlog
set *id "id: Main\nname: A sample program\nversion: v0.1.0"
print "id: Main\nname: A sample program\nversion: v0.1.0\n"
```

The processor ID is used by the [processor emulator](TOOLS-PROCESSOR-EMULATOR.markdown) to identify the processor used to run the compiled program. Since the `set *id ...` instruction is always the first (or almost first) instruction in the compiled program, it can also be easily inspected in the game just by looking at the code. Hopefully, mods will also be developed which will display processor ID in a more convenient way.  

### Option `program-name`

**Option scope: [global](#global-scope)**

Specifies the name of the compiled program, which is an arbitrary string value. The program name is encoded, together with the processor ID and program version, into the `*id` variable, and the encoded value is also available as `@@ID` within Mindcode.

```Mindcode
#set processor-id = Main;
#set program-name = "A sample program";
#set program-version = "v0.1.0";

println(@@ID);
```

compiles to:

```mlog
set *id "id: Main\nname: A sample program\nversion: v0.1.0"
print "id: Main\nname: A sample program\nversion: v0.1.0\n"
```

Since the `set *id ...` instruction is always the first (or almost first) instruction in the compiled program, it can also be easily inspected in the game just by looking at the code.

### Option `program-version`

**Option scope: [global](#global-scope)**

Specifies the version of the compiled program, which is an arbitrary string value. The program version is encoded, together with the processor ID and program name, into the `*id` variable, and the encoded value is also available as `@@ID` within Mindcode.

```Mindcode
#set processor-id = Main;
#set program-name = "A sample program";
#set program-version = "v0.1.0";

println(@@ID);
```

compiles to:

```mlog
set *id "id: Main\nname: A sample program\nversion: v0.1.0"
print "id: Main\nname: A sample program\nversion: v0.1.0\n"
```

Since the `set *id ...` instruction is always the first (or almost first) instruction in the compiled program, it can also be easily inspected in the game just by looking at the code.

### Option `symbolic-labels`

**Option scope: [global](#global-scope)**

Activates/deactivates generating symbolic labels for jump instruction targets. Possible values are:

* `false` (the default value): the compiled code contains absolute instruction addresses in `jump` instructions. Absolute addresses are also used to store program locations in variables or to compute offsets in jump tables.
* `true`: the compiled code contains symbolic labels in `jump` instructions. Program locations stored in variables are always derived from actual `@counter` values, including `@counter` manipulations in jump tables.

As a consequence, the compiled mlog code can be altered by adding and/or removing instructions when symbolic labels are produced. Instructions must not be added to or removed from sections of code where the `@counter` variable is manipulated. All Mindcode features are supported when generating code using symbolic labels, including function call and remote function calls, list iteration loops, internal arrays, and so on.

When symbolic labels are produced, the compiler also uses mlog comments to mark function entry points.

> [!NOTE]
> Activating symbolic labels may increase program size and decrease execution speed.

The following features are affected when activating symbolic labels:

* List iteration loops: if the `null-couner-is-noop` is set to false, the number of instructions used to create a list iteration loop code is increased by two.
* Stackless function calls: instruction setting up a function return address cannot be hoisted.
* Recursive function calls: the size and execution time of a recursive function call are increased by one.
* Regular internal array element access (inlined array access is not affected):
  * the size of a jump table and execution time of array access are increased by one,
  * instructions setting up a function return address cannot be hoisted.
* Optimized case expressions: the size and execution time of an [optimized case expression](optimizations/CASE-SWITCHING.markdown) may increase significantly.

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

compiles to:

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

## Compiler options

Options which affect the way the source code is compiled.

| Option                                                       | Scope  | Semantic stability |
|--------------------------------------------------------------|--------|--------------------|
| [auto-printflush](#option-auto-printflush)                   | global | stable             |
| [boundary-checks](#option-boundary-checks)                   | local  | stable             |
| [emulate-strict-not-equal](#option-emulate-strict-not-equal) | global | stable             |
| [error-function](#option-error-function)                     | local  | stable             |
| [error-reporting](#option-error-reporting)                   | local  | stable             |
| [ipt](#option-ipt)                                           | local  | stable             |
| [remarks](#option-remarks)                                   | local  | stable             |
| [setrate](#option-setrate)                                   | global | stable             |
| [syntax](#option-syntax)                                     | module | stable             |
| [target-guard](#option-target-guard)                         | global | stable             |
| [volatile-atomic](#option-volatile-atomic)                   | local  | stable             |

### Option `auto-printflush`

**Option scope: [global](#global-scope)**

Activates/deactivates automatic flushing of print output. Possible values are:

* `false`: Mindcode doesn't flush print output automatically.
* `true` (the default value): When the program contains at least one `print` or `printchar` instruction, and no `printflush` or `draw print` instructions, Mindcode adds a `printflush message1` instruction at the end of the main program body, and generates a warning.

This feature is meant for small, test scripts, where a call to `printflush()` is easily missed. This situation would otherwise require new compilation and code injection into the mlog processor when detected.

### Option `boundary-checks`

**Option scope: [local](#local-scope)**

This option activates/deactivates runtime checks when accessing an internal or external array by index.

* `false`: no boundary checks are performed.
* `true` (the default value): boundary checks are performed according to the error-reporting mechanism, unless `error-reporting` is set to `none`, in which case no runtime checks are performed.

> [!NOTE]
> No runtime checks are generated when accessing memory block directly without a declared array, for example `cell1[index]` or `memory[index]` (where `memory` is a variable and not an external array) do not have runtime checks generated.

### Option `emulate-strict-not-equal`

**Option scope: [global](#global-scope)**

Allows or disallows using the `select` instruction to emulate a `jump strictNotEqual` instruction.

* `false`: the `select` instruction won't be used to emulate an unsupported `jump` instruction.
* `true` (the default value): when possible, a `select` instruction can be used to emulate an unsupported `jump` instruction.

Example:

```Mindcode
#set target = 8;

while @unit.@dead === 1 do
    ubind(@mono);
end;

print("Found!");
```

compiles to:

```
sensor *tmp0 @unit @dead
select @counter strictEqual *tmp0 1 @counter 5
ubind @mono
sensor *tmp0 @unit @dead
jump 2 strictEqual *tmp0 1
print "Found!"
```

### Option `error-function`

**Option scope: [local](#local-scope)**

Specifies how calls to the [`error()` function](TROUBLESHOOTING.markdown#the-error-function) are handled:

* `false`: call to `error()` are ignored.
* `true` (the default value): calls to `error()` are reported according to the error-reporting mechanism, unless `error-reporting` is set to `none`, in which case the calls are also ignored. 

### Option `error-reporting`

**Option scope: [local](#local-scope)**

This option specifies the mechanism to be used by the compiler to report failing runtime checks. Runtime checks are a debug feature and should be only active when developing/debugging your scripts, as they make the code larger and slower.

Which runtime checks are performed is governed by other compiler options. Only when `error-reporting` is set to `none`, no runtime checks are performed, regardless of other option values.  

The following compiler options govern which runtime checks are performed:

* `boundary-checks`: when accessing an element of an internal or external array by index, a runtime-check is generated to make sure the index lies within bounds.
* `error-function`: when a call to the [`error()` function](TROUBLESHOOTING.markdown#the-error-function) is made, this option specifies how the error is reported.

Possible values for the `error-reporting` option are:

* `none` (the default value): no runtime checks are generated.
* `assert`: runtime checks are generated using instructions provided by the [MlogAssertions mod](https://github.com/cardillan/MlogAssertions). The mod is available for Mindustry 7 and the latest Mindustry 8 Beta. If the mod is not installed, no runtime checks are performed, but otherwise the code runs as expected. Each runtime check takes one instruction. When the runtime check fails, the mod displays an error message over the processor for easier detection.
* `minimal`: when the runtime check fails, the program execution stops on a `jump` instruction (this instruction permanently jumps to itself, which can be determined by inspecting the `@counter` variable in the **Vars** screen). Each runtime check takes two instructions.
* `simple`: when the runtime check fails, the program execution stops on a `stop` instruction (again, this can be determined by inspecting the `@counter` variable). Each runtime check takes three instructions.
* `described`: when the runtime check fails, the program execution stops on a `stop` instruction. However, a `print` instruction containing an error message is generated just before the `stop` instruction; after locating the faulting `stop` instruction, the error message can be read. Each runtime check takes four instructions.

### Option `ipt`

**Option scope: [local](#local-scope)**

This option specifies world processor's speed in instructions per tick. This value is used by the compiler building [atomic code blocks](REMOTE-CALLS.markdown#atomic-code-execution). Allowed values are in the range of `1` to `1000`.

The option has a local scope and can be applied to atomic code blocks to specify the current processor speed for the block:

```Mindcode
#set target = 8w;

setrate(500);
#setlocal ipt = 500;
atomic
    cell1[0]++;
end;

setrate(1000);
#setlocal ipt = 1000;
atomic
    cell1[1]++;
end;
```

compiles to:

```mlog
setrate 500
wait 0.000134                           # 0.008 ticks for atomic execution of 4 steps at 500 ipt
read *tmp1 cell1 0
op add *tmp0 *tmp1 1
write *tmp0 cell1 0                     # The last atomic block instruction
setrate 1000
wait 0.000067                           # 0.004 ticks for atomic execution of 4 steps at 1000 ipt
read *tmp4 cell1 1
op add *tmp3 *tmp4 1
write *tmp3 cell1 1                     # The last atomic block instruction
```

An alternative way to specify the processor speed is the [`setrate` compiler option](#option-setrate), however, this option also generates a `setrate` instruction to apply the specified speed to the processor. 

### Option `remarks`

**Option scope: [local](#local-scope)**

This option controls the way remarks, generated through the [remark() function](SYNTAX-4-FUNCTIONS.markdown#remarks), are propagated to the compiled code. Remarks are written into the compiled code as `print` instructions. Possible values of the `remarks` option are:

* `none`: remarks are suppressed in the compiled code—they do not appear there at all.
* `comments`: remarks are output as mlog comments into the compiled code. Expressions in remarks are output as separate comments with the mlog name of the variable holding the value of the expression.
* `passive`: remarks are included in the compiled code, but a jump is generated in front each block of continuous remarks, so that the print statements themselves aren't executed. This is the default value.
* `active`: remarks are included in the compiled code and are executed, producing actual output to the text buffer.

Converting remarks to comments may improve code readability. Remarks in a loop may help identify individual iterations when the loop is unrolled, for example. However, when the code is inserted into an mlog processor, the comments are not preserved.

Passive remarks can be used for putting instructions or comments in the compiled code in a way which is still visible in the game UI.

Active remarks can be used to easily add debugging output to a program that can be deactivated using a compiler option (potentially through a command line switch without modifying the source code).

### Option `setrate`

**Option scope: [global](#global-scope)**

This option specifies world processor's speed in instructions per tick, and generates the `setrate` instruction in the initialization code to apply the specified speed. The specified value is also used by the compiler building [atomic code blocks](REMOTE-CALLS.markdown#atomic-code-execution). Allowed values are in the range of `1` to `1000`.

Example:

```Mindcode
#set target = 8w;
#set setrate = 750;

atomic
    cell1[0]++;
end;
```

compiles to:

```mlog
setrate 750
wait 0.000089                           # 0.005 ticks for atomic execution of 4 steps at 750 ipt
read *tmp1 cell1 0
op add *tmp0 *tmp1 1
write *tmp0 cell1 0                     # The last atomic block instruction
```

An alternative way to specify the processor speed is the [`ipt` compiler option](#option-ipt), however, this option does not generate a `setrate` instruction to apply the specified speed to the processor.

### Option `syntax`

**Option scope: [module](#module-scope)**

Chooses the [syntax mode](SYNTAX.markdown#syntax-modes) to be used for compilation. Possible values are:

* `relaxed` (the default value): useful for shorter scripts, as it requires less boilerplate code.
* `strict`: useful for larger projects, as it enforces additional rules designed to make source code more maintainable.
* `mixed`: designed to help with a transition of relaxed syntax code to the strict standard. In this mode, code is compiled using the relaxed syntax rules, but all violations of the strict syntax rules are reported as warnings.

> [!NOTE]
> All modules are implicitly compiled using the `strict` syntax. If the `syntax` option is used within a module, the only value that can be set is `strict`.

### Option `target-guard`

**Option scope: [global](#global-scope)**

When set, adds a guard code to the beginning of the program which verifies the code is run by a Mindustry version compatible with both the `target` and `builtin-evaluation` options. If the processor isn't compatible with the compiler options used to generate the code, the execution remains stuck at the beginning of the program. This prevents the code from running on an incompatible processor, resulting in potentially faulty execution of the code.

The target guard code doesn't distinguish between world and standard processors.

The guard code is always a single `jump` instruction which jumps back to itself if an incompatible processor is detected. The following table shows the guard instructions corresponding to given target settings:

| Target | Built-in evaluation | Instruction                            |
|--------|:--------------------|----------------------------------------|
| 6      | compatible          | No test, code runs on all versions     |
| 6      | full                | `jump 0 greaterThan %FFFFFF 0`         |
| 7      | compatible          | `jump 0 strictEqual %FFFFFF null`      |
| 7      | full                | `jump 0 notEqual @blockCount 254`      |
| 8.0    | compatible          | `jump 0 strictEqual %[red] null`       |
| 8.0    | full                | `jump 0 strictEqual @bufferUsage null` |
| 8.1    | compatible          | `jump 0 strictEqual @bufferSize null`  |
| 8.1    | full                | `jump 0 strictEqual @bufferSize null`  |

The jump target (`0`) is replaced with proper instruction address when it's not the first in the compiled code.

### Option `volatile-atomic`

**Option scope: [local](#local-scope)**

This option governs the behavior of the [`atomic` code blocks](REMOTE-CALLS.markdown#atomic-code-execution). Possible values are:

* `false`: all instructions in an atomic block are protected and guaranteed to be executed atomically.
* `true` (the default value): only instructions interacting with the world, and instructions accessing volatile variables, are protected and guaranteed to be executed atomically.

Mindcode's contract regarding variables is that only volatile variables may be accessed by other processors, and therefore non-volatile variables, including compiler-generated variables, do not need to be protected. Such variables can't be influenced from the outside and therefore don't need to be protected.

## Optimization options

Options guiding the overall optimization of the compiled code or activating/deactivating specific
optimization actions.

| Option                                                           | Scope  | Semantic stability |
|------------------------------------------------------------------|--------|--------------------|
| [case-optimization-strength](#option-case-optimization-strength) | local  | stable             |
| [goal](#option-goal)                                             | local  | stable             |
| [mlog-block-optimization](#option-mlog-block-optimization)       | local  | stable             |
| [passes](#option-passes)                                         | global | stable             |
| [unsafe-case-optimization](#option-unsafe-case-optimization)     | local  | unstable           |
| [use-lookup-arrays](#option-use-lookup-arrays)                   | global | stable             |
| [use-short-arrays](#option-use-short-arrays)                     | global | stable             |
| [use-text-jump-tables](#option-use-text-jump-tables)             | local  | stable             |
| [use-text-translations](#option-use-text-translations)           | local  | stable             |
| [weight](#option-weight)                                         | local  | stable             |

### Option `case-optimization-strength`

**Option scope: [local](#local-scope)**

This option affects the number of segment arrangements considered when the Case Switching optimization performs [jump table compression](optimizations/CASE-SWITCHING.markdown#jump-table-compression). The higher the number, the more segment arrangements are considered, but the more time is needed for generating and evaluating them. The default value of this option is `2` for the web application, and `3` for the command-line tool. The maximal possible value is `4` for the web application, and `6` for the command-line tool. Values larger than `4` typically only bring additional benefits for case expressions with a very complex structure. Increasing the value by one may significantly increase both the number of segment arrangements and the optimization time.

Setting the optimization strength to `0` causes the optimizer to forgo considering any segment arrangements and only consider turning the entire case statement into a jump table.

Setting the optimization strength to `6` may lead to a very long compilation time (from tens of seconds to minutes or more), and only improves the efficiency of the most complex case expressions.

> [!NOTE]
> This option only improves [compressed jump tables](optimizations/CASE-SWITCHING.markdown#jump-table-compression). If there's enough instruction space for a full jump table, and the [optimization goal](#option-goal) is set to `speed`, increasing the value of this option cannot bring additional benefit, but still increases the compilation time.

### Option `goal`

**Option scope: [local](#local-scope)**

Use the `goal` option to specify whether Mindcode should prefer to generate smaller code, or faster code. Possible values are:

* `speed` (the default value): Mindcode can generate additional instructions if it makes the resulting code faster while adhering to the current [instruction limit](#option-instruction-limit). When several possible optimizations of this kind are available, the ones having the best effect (the highest speedup per additional instruction generated) are applied first, until the instruction limit is reached.
* `neutral`: When an optimization making the code either smaller or faster than the original code can be found, it is applied. Otherwise, the original code is used. The optimized code should never be larger or slower than the original code.
* `size`: Mindcode tries to generate the smallest code possible, even at the expense of execution speed.

See [Dynamic optimizations](SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations) for a detailed explanation of the optimization goals.

### Option `mlog-block-optimization`

**Option scope: [local](#local-scope)**

Activates/deactivates a limited [Data Flow optimization](optimizations/DATA-FLOW-OPTIMIZATION.markdown) inside [mlog blocks](SYNTAX-EXTENSIONS.markdown#mlog-blocks). Possible values are:

* `false`: no optimization is applied to the mlog blocks.
* `true` (the default value): the Data Flow Optimization may replace input/output mlog variables used in instructions with different variables/values.

### Option `passes`

**Option scope: [global](#global-scope)**

Use the `passes` option to set the maximum number of optimization passes to be done:

```
#set passes = 10;
```

The default value is `5` for the web application and `25` for the command line tool. The number of optimization passes can be limited to a value between `1` and `1000` (inclusive).

A more complex code can usually benefit from more optimization passes. On the other hand, each optimization pass can take some time to complete. Limiting the total number can prevent optimization from taking too much time.

### Option `unsafe-case-optimization`

**Option scope: [local](#local-scope)**

This option instructs the compiler to drop range checking when performing case expression optimization. For more information, [Range check elimination](optimizations/CASE-SWITCHING.markdown#range-check-elimination).

> [!NOTE]
> The `unsafe-case-optimization` is _semantically unstable_: when applied to an unsuitable case expression, the compiler may generate incorrect code. It is recommended to always use the option locally, by using `#setlocal` immediately before the case expression on which it is meant to be applied. 

### Option `use-lookup-arrays`

**Option scope: [global](#global-scope)**

This option activates or deactivates the lookup mechanism for arrays. [Lookup arrays](optimizations/ARRAY-OPTIMIZATION.markdown#lookup-arrays) use the Mindustry 8 Logic ability to [read variables from processors](MINDUSTRY-8.markdown#reading-and-writing-processor-variables) for array implementation different from the usual `@counter` array mechanism. Possible values are:

* `false`: lookup arrays won't be generated.
* `true` (the default value): the compiler generates lookup arrays if possible.

Note that [declaring an array explicitly to use the lookup mechanism](SYNTAX-1-VARIABLES.markdown#mlog-modifier) is possible even when this option is set to `false`.

### Option `use-short-arrays`

**Option scope: [global](#global-scope)**

This option activates or deactivates using [specialized implementation for short arrays](optimizations/ARRAY-OPTIMIZATION.markdown#short-arrays). Possible values are:

* `false`: short arrays won't be generated.
* `true` (the default value): the compiler generates short arrays when it's appropriate.

### Option `use-text-jump-tables`

**Option scope: [local](#local-scope)**

The Mindustry 8 Logic ability to [read values from strings](MINDUSTRY-8.markdown#reading-characters-from-strings) makes it possible to encode instruction addresses into a string. This option governs whether the compiler will use this possibility. Possible values are:

* `false`: text-based jump tables won't be generated.
* `true` (the default value): the compiler generates text-based jump tables where possible.

Text-based jump tables allow very efficient encoding of jump tables used by internal arrays and some `case` expressions, both in terms of program size and execution time. The functionality, however, often requires storing unprintable characters in the string. If this poses a problem, the option can be set to `false` to disable text-based jump tables.

### Option `use-text-translations`

**Option scope: [local](#local-scope)**

This option specifies whether the compiler may create strings (potentially containing unprintable characters) to implement [value translations](optimizations/CASE-SWITCHING.markdown#value-translation). Possible values are:  

* `false`: value translations won't be used.
* `true` (the default value): the compiler generates value translations where possible.

### Option `weight`

**Option scope: [local](#local-scope)**

This option adjusts the weight of the next statement's code by multiplying it with the specified value. Values greater than `1` increase the weight of the next statement's code, while values less than `1` decrease it. Code with a greater weight is preferred when applying [optimizations for speed](SYNTAX-6-OPTIMIZATIONS.markdown#optimization-benefit).

While it is possible to set the code weight globally using the `#set` directive, it doesn't make any sense to do so, as the same factor is applied to all generated code. The speed optimization considers code weight ratios, not absolute values, when selecting portions of code to optimize.

## Optimization levels

Options specifying the global and individual optimization levels. Individual optimizers use global level
when not explicitly set. Available optimization levels are {none, basic, advanced, experimental}.

| Option                                                                              | Scope | Semantic stability |
|-------------------------------------------------------------------------------------|-------|--------------------|
| [optimization](#option-optimization)                                                | local | stable             |
| [array-optimization](optimizations/ARRAY-OPTIMIZATION.markdown)                     | local | stable             |
| [boolean-optimization](optimizations/BOOLEAN-OPTIMIZATION.markdown)                 | local | stable             |
| [case-expression-optimization](optimizations/CASE-EXPRESSION-OPTIMIZATION.markdown) | local | stable             |
| [case-switching](optimizations/CASE-SWITCHING.markdown)                             | local | stable             |
| [condition-optimization](optimizations/CONDITION-OPTIMIZATION.markdown)             | local | stable             |
| [data-flow-optimization](optimizations/DATA-FLOW-OPTIMIZATION.markdown)             | local | stable             |
| [dead-code-elimination](optimizations/DEAD-CODE-ELIMINATION.markdown)               | local | stable             |
| [expression-optimization](optimizations/EXPRESSION-OPTIMIZATION.markdown)           | local | stable             |
| [function-inlining](optimizations/FUNCTION-INLINING.markdown)                       | local | stable             |
| [if-expression-optimization](optimizations/IF-EXPRESSION-OPTIMIZATION.markdown)     | local | stable             |
| [instruction-reordering](optimizations/INSTRUCTION-REORDERING.markdown)             | local | stable             |
| [jump-normalization](optimizations/JUMP-NORMALIZATION.markdown)                     | local | stable             |
| [jump-straightening](optimizations/JUMP-STRAIGHTENING.markdown)                     | local | stable             |
| [jump-threading](optimizations/JUMP-THREADING.markdown)                             | local | stable             |
| [loop-hoisting](optimizations/LOOP-HOISTING.markdown)                               | local | stable             |
| [loop-rotation](optimizations/LOOP-ROTATION.markdown)                               | local | stable             |
| [loop-unrolling](optimizations/LOOP-UNROLLING.markdown)                             | local | stable             |
| [print-merging](optimizations/PRINT-MERGING.markdown)                               | local | stable             |
| [return-optimization](optimizations/RETURN-OPTIMIZATION.markdown)                   | local | stable             |
| [single-step-elimination](optimizations/SINGLE-STEP-ELIMINATION.markdown)           | local | stable             |
| [stack-optimization](optimizations/STACK-OPTIMIZATION.markdown)                     | local | stable             |
| [temp-variables-elimination](optimizations/TEMP-VARIABLES-ELIMINATION.markdown)     | local | stable             |
| [unreachable-code-elimination](optimizations/UNREACHABLE-CODE-ELIMINATION.markdown) | local | stable             |

**Option scope: [local](#local-scope)** (for all options in this category)

### Option `optimization`

Use the `optimization` option to set the optimization level for all optimizers, or specific options to configure individual optimizers.

Possible values for these options are:

* `none`: deactivates all optimizations.
* `basic`: performs most optimizations, except those that depend on certain assumptions about the program or Mindustry Logic.
* `advanced`: performs some additional optimizations. Only a few of the optimizers perform additional optimizations on the `advanced` level. Please see the individual optimizers documentation for details.
* `experimental`: perform optimizations that are currently in the experimental phase.

The default optimization level is `experimental`.

You normally shouldn't need to deactivate any optimization, but if there was a bug in some optimizer, deactivating it might allow you to use Mindcode until a fix is available. However, some optimizers expect to work on code that was already processed by different optimizations, so turning off some optimizations might render other optimizations ineffective.

### Setting the optimization level locally

Optimization levels are handled specifically:

* Activating/deactivating an optimizer is only allowed at a global level. For example, when `optimization` is set to `none` at the global level, no optimizations will happen regardless of module or local scope compiler directives. Similarly, it is not possible to turn of an optimization entirely by setting it to `none` at the local level: using `#setlocal` directive with a value of `none` causes an error.
* At the local level, it is possible to switch between optimization levels (i.e. `basic`, `advanced` or `experimental`) if the given optimization is active.

Some optimizations performed on the `experimental` level have a global scope and can't be turned off locally. These optimizations are performed if and only if the global setting for the given optimizer is set to `experimental`.  

## Debugging options

Options to activate debugging features or additional output from the compiler.

| Option                                       | Scope  | Semantic stability |
|----------------------------------------------|--------|--------------------|
| [debug](#option-debug)                       | local  | unstable           |
| [debug-messages](#option-debug-messages)     | global | stable             |
| [parse-tree](#option-parse-tree)             | global | stable             |
| [print-code-size](#option-print-code-size)   | global | stable             |
| [print-unresolved](#option-print-unresolved) | global | stable             |
| [sort-variables](#option-sort-variables)     | global | stable             |

### Option `debug`

**Option scope: [local](#local-scope)**

This option controls whether the code is compiled with debug support. Possible values are:
* 
* `false` (the default value): no debug support.
* `true` or omitted: the code is compiled with debug support.

Code compiled with debug support enabled has these properties:

* All user-defined variables are preserved and not removed by optimizations. It is therefore possible to inspect all variables on the Vars screen.
* [Debug code blocks](TROUBLESHOOTING.markdown#debug-specific-code) are included in the compiled code.
* Calls to [debug functions](TROUBLESHOOTING.markdown#debug-specific-code) are compiled and included in the code.

The `debug` option is local. It is therefore possible to activate debugging for some parts of code only (e.g., for a specific function).

> [!NOTE]
> Currently, activating the debug support may preclude loops from being unrolled. 

### Option `debug-messages`

**Option scope: [global](#global-scope)**

This option sets the detail level of debug messages emitted by individual optimizers, 0 = off.

### Option `parse-tree`

**Option scope: [global](#global-scope)**

This option sets the detail level of parse tree output into the log file, 0 = off

### Option `print-code-size`

**Option scope: [global](#global-scope)**

This option activates or deactivates printing the final code size broken down by function. 

* `false`: don't output the code size.
* `true` (the default value): output the final code size.

The code size information looks approximately like this:

```text
Code size and number of instantiations by function:
  Size  Times  AvgSize  Function
   214     7x     30,6  inline def computeRowValues(in x, in y, in ore, in size)
   196    49x      4,0  inline def tileValue(in x, in y, in ore)
   147     1x    147,0  export def createTerrainMask(in unit, in x, in y)
   147    49x      3,0  inline def isSolid(in x, in y)
   125    25x      5,0  inline void updatePosition(in dx, in dy, in value, bestX, bestY, best)
    35     1x     35,0  export def findBestPosition(in unit, in x, in y, in ore, in size, out bestX, out bestY)
     6     1x      6,0  <no function>
     4     4x      1,0  inline void updateRowPositionsEven(in dy, in rowSum, bestX, bestY, best)
     3     3x      1,0  inline void updateRowPositionsOdd(in dy, in rowSum, bestX, bestY, best)
```

### Option `print-unresolved`

**Option scope: [global](#global-scope)**

This option governs how the unresolved code (i.e., code containing unresolved virtual instructions including labels) is written to the log file. Possible values are:

* `none` (the default value): unresolved code is not written. 
* `plain`: just the instructions are output.
* `flat-ast`: a flattened AST structure is included in the output.
* `deep-ast`: a non-flattened AST structure is included in the output.
* `source`: the source code is included in the output. The entire line of source code is included if it can be matched to the instruction. Some optimizations may prevent locating the proper source code line.
 
### Option `sort-variables`

**Option scope: [global](#global-scope)**

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

## Emulator options

Options to specify whether and how to run the compiled code on an emulated processor. The emulated
processor is much faster than Mindustry processors, but can't run instructions which obtain information
from the Mindustry World. Sole exceptions are memory cells ('cell1' to 'cell9') and memory banks
('bank1' to 'bank9'), which can be read and written.

| Option                                       | Scope  | Semantic stability |
|----------------------------------------------|--------|--------------------|
| [emulator-fps](#option-emulator-fps)         | global | stable             |
| [emulator-target](#option-emulator-target)   | global | stable             |
| [output-profiling](#option-output-profiling) | global | stable             |
| [run](#option-run)                           | global | stable             |
| [run-steps](#option-run-steps)               | global | stable             |

### Option `emulator-fps`

**Option scope: [global](#global-scope)**

Sets the FPS rate the emulator will use to schedule instructions. The standard FPS rate is 60 (the default value). Higher or lower FPS rates affect the number of instructions executed per tick.

### Option `emulator-target`

**Option scope: [global](#global-scope)**

Use the `emulator-target` option to specify the Mindustry Logic version and processor type to be used by the processor emulator. When the option is not specified, the target specified by the [`target` option](#option-target) is used for the emulator. This option is therefore only useful to emulate executing the compiled code on a Mindustry processor different from the one used by the compiler.

For possible values of this option, please see [Option `target`](#option-target).

### Option `output-profiling`

**Option scope: [global](#global-scope)**

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

### Option `run`

**Option scope: [global](#global-scope)**

Activates running the compiled code on an emulated processor. Possible values are:

* `false` (the default value): the code is not run.
* `true` (or omitted): the code is run.

For more information on running the compiled Mindcode, see [Processor emulator](TOOLS-PROCESSOR-EMULATOR.markdown).

### Option `run-steps`

**Option scope: [global](#global-scope)**

Sets the maximum number of steps (individual instructions) to be executed by the processor emulator. If the program doesn't stop by itself (e.g., by running to its natural end, or by using the stop instruction), the emulator will stop the execution when the specified number of steps is reached.

When multiple processors get emulated, the step limit is applied to the aggregate of the instructions executed by all processors. 

### Execution flags

There are additional options to control the execution of the compiled code, all of which reside in the `global` scope. For a list of these options, see [Execution flags](TOOLS-PROCESSOR-EMULATOR.markdown#execution-flags).

---

[&#xAB; Previous: Parallel processing](REMOTE-CALLS.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Code Optimization &#xBB;](SYNTAX-6-OPTIMIZATIONS.markdown)
