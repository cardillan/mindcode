# Extending Mindcode

In addition to supporting all mlog features available in the Mindustry version selected by the [language target](SYNTAX-5-OTHER.markdown#option-target), Mindcode also provides a way to encode mlog instructions which are not part of the Mindcode standard. This may come in handy in these situations:

1. You want to use nonstandard instructions, keywords, blocks, or built-in variables provided by a Mindustry mod or an alternate client.
2. A new version of Mindustry (either an official release or a bleeding-edge version) creates new instructions not known to Mindcode.
3. An instruction was not implemented correctly in Mindcode and a fix is not yet available.

Mindcode offers these options to extend its basic syntax:

* [declaring new mlog keywords (function keywords)](#declaring-new-mlog-keywords),
* [declaring new builtin variables](#declaring-new-built-in-variables),
* [declaring new linked block names](#declaring-new-linked-block-names),
* [defining new mlog instructions](#defining-new-mlog-instructions),
* [embedding blocks of mlog code](#mlog-blocks). 

## Declaring new mlog keywords

[Mlog keywords](SYNTAX.markdown#mlog-keywords) are used as arguments in some mlog instructions. The instructions only accept a limited set of keywords, and those keywords cannot be stored in variables; they need to be used explicitly with the instruction. Using a keyword not defined for the given instruction argument by mlog standard is not supported and leads to compilation errors.

While mods probably cannot specify a new keyword, a custom Mindustry client could. In this case, it is possible to declare the new keyword. A category into which the new keyword belongs must be specified. The category tells Mindcode which instruction/parameter accepts the new keyword. These keyword categories are supported:

| Category          | Meaning                                                           |
|-------------------|-------------------------------------------------------------------|
| blockGroup        | group of blocks to look for in the `ulocate building` instruction |
| lookupType        | the content type in the `lookup` instruction                      |
| markerType        | type of marker to create in the `makemarker` instruction          |
| radarSort         | sort order for the `radar` and `uradar` instructions              |
| radarTarget       | targeting flags for the `radar` and `uradar` instructions         |
| settableTileLayer | layer to update in the `setblock` world-processor instruction     |
| statusEffect      | status to set in the `status` instruction                         |
| tileLayer         | layer to inspect in the `getblock` world-processor instruction    |

Keywords are declared using `#declare` statement:

```
#declare <category> keyword [, keyword ... ];
```

Example: if the `ulocate building` instruction was extended to accept additional block groups, the new keywords could be declared like this:

```Mindcode
#set target = 8;
#declare blockGroup :conveyor, :processor;

var x, y;
var building = ulocate(:building, :conveyor, false, out x, out y);
print($"Found $building at $x, $y.");
```
  
```mlog
ulocate building conveyor false @copper .x .y 0 .building
print "Found {0} at {0}, {0}."
format .building
format .x
format .y
```

## Declaring new built-in variables

While Mindcode doesn't limit where and how built-in variables are used, it emits a warning when it encounters an unknown built-in variable to help identify typos and mistakes. If a built-in variable unknown to Mindcode is actually valid (e.g., provided by a mod), it is possible to declare such a built-in variable as valid, which suppresses the warning. To declare a built-in variable, use the `#declare` command with a `builtin` category. No warning is then emitted when using the built-in:

```Mindcode
// Say we have a mod that creates a new `@dark-matter` item:
#declare builtin @dark-matter;

// A sorter can now sort it
sorter1.config = @dark-matter;
```

```mlog
control config sorter1 @dark-matter 0 0 0
```

## Declaring new linked block names

Mindcode handles identifiers that may represent a linked block specifically. When a mod adds a new type of block, it would need to be explicitly declared at all times, and even then warnings about unknown block names are generated. It is again possible to declare new block names, which instructs Mindcode to handle them identically to known block names. To declare a new block name, use the `#declare` command with a `linkedBlock` category:

```Mindcode
// Say we have a mod that creates a new `organic-fridge` block.
// Such a block would be linked under a `fridge1` or a similar name
#declare linkedBlock fridge;

// We can now use `fridge1` as a linked block:
fridge1.enabled = false;
```

```mlog
control enabled fridge1 false 0 0 0
```

## Defining new mlog instructions

Mindcode provides a mechanism of encoding entire instructions not known to Mindcode. Custom instructions may interact with Mindustry World or provide information about Mindustry World.

> [!NOTE]
> If an instruction alters the program flow (i.e., the existing `jump` and `end` instructions, or any other way of altering the program flow), it cannot be safely encoded using this mechanism. A more complex logic may be realized in mlog using [mlog blocks](#mlog-blocks), including jumps or possible new instructions (but always only within a single mlog block).

Custom instructions are created using one of these functions:

* `mlog()`: creates a standard instruction. Mindcode assumes the instruction has some effect on the Mindustry world and will not remove the instruction during optimizations.  
* `mlogSafe()`: creates an instruction which doesn't affect the Mindustry world (for example, `set`, `packcolor` or `sensor` are such instructions). Mindcode may remove the instruction if the value or values it produces are not used by the rest of the program.   
* `mlogText()`: creates an instruction which manipulates the text buffer. Mindcode never removes the instruction, and furthermore, handles it correctly during Print Merging optimizations.

Each of these functions takes the following arguments:

* The first argument to the function needs to be a string literal. This literal is the instruction code.
* All other arguments must be either keywords, literals, or user variables.
  * Keyword: the keyword is used as an instruction argument (the Mindcode specific `:` prefix is stripped before writing it to mlog). No `in` or `out` modifiers may be used. 
  * String literal: the text represented by the string literal is used as an instruction argument.
    * Without any modifier, a string literal can be used to encode keywords containing characters illegal in Mindcode keywords.
    * When the `in` modifier is used, the string literal will be used as an argument including the enclosing double quotes.
  * Numeric literal: all other literals must not be marked with either modifier. The typical use for numeric literals is to provide fill-in values (typically zeroes) for unused instruction parameters.
  * User variable: the variable is used as an instruction argument. The argument must use the `in` and/or `out` modifier to inform Mindcode how the corresponding instruction argument behaves:
    * `in`: the argument represents an input value—the instruction reads and uses the value of the variable.
    * `out`: the argument represents an output value—the instruction produces a value and stores it in the variable.
    * `in out`: the argument represents an input/output value—the instruction reads the input value, and/or updates the variable with a new value. With a possible exception to the `sync` instruction, no mlog instruction currently takes an input/output argument.

> [!TIP]
> Keywords can be encoded either using the Mindcode keyword syntax (e.g., `:keyword`), or as a string literal (`"keyword"`). Only if the keyword contains characters unsupported by Mindcode syntax, it can only be encoded as a string literal (e.g., `"unsupported:keyword"`). This is the only way to encode such a keyword to mlog, apart from mlog blocks.  

> [!TIP]
> Although not strictly required, it is recommended to create an inline function with proper input/output parameters for each custom-generated instruction. This way, the requirement that the `mlog()` functions always use user variables as arguments can be easily met, while allowing to use expressions for input parameters in the call to the enclosing function—see the examples below. It is also possible to use keywords as function arguments to inline functions, making this approach suitable even for instructions taking keywords as arguments.
 
For better understanding, the creation of custom instructions will be demonstrated on existing instructions. 

### The `format` instruction

The `format` instruction was introduced in Mindustry Logic 8. When compiling for Mindustry Logic 7, the instruction isn't available. We can create it using this code:

```Mindcode
#set target = 7;
inline void format(value)
    mlogText("format", in value);
end;

param a = 10;
println("The value is: {0}");
format(a * 20);
printflush(message1);
```

Compiling this code produces the following output:

```mlog
set a 10
print "The value is: {0}\n"
op mul :format:value a 20
format :format:value
printflush message1
```

Considerations:

* By using the `mlogText()` function, we're making sure the Print Merging optimization handles the instruction correctly.
* The processor emulator doesn't recognize custom instructions and won't handle them. The output produced by running the above code using the processor emulator would therefore be incorrect.

### The `draw print` instruction

Mindustry 8 Logic adds new variants of the `draw` instruction, `print` being one of them. Under the Mindustry Logic 8 language target, this instruction is mapped to the `drawPrint()` function. We can create it explicitly through this:

```Mindcode
#set target = 7;
inline void drawPrint(x, y, alignment)
    mlogText("draw", "print", in x, in y, alignment, 0, 0, 0);
end;

drawPrint(0, 10, :center);
drawPrint(0, 20, :bottomLeft);
```

Result:

```mlog
draw print 0 10 center 0 0 0
draw print 0 20 bottomLeft 0 0 0
```

Considerations:

* The `draw print` instruction manipulates the text buffer, so the `mlogText()` function is used.
* The function expects a keyword as an argument. It is possible to create custom instructions expecting several keywords this way. However, Mindcode is unable to enforce that the keywords are passed as arguments at the right places.  

### The `ucontrol getBlock` instruction

The `ucontrol getBlock` instruction is an example of instruction which has output parameters. Also, we know it is an instruction that doesn't modify the Mindustry World and therefore is safe. Had it not been known by Mindcode, it could be defined like this:

```Mindcode
// Using 'getBlock2' as a name to avoid clashing with the existing function name
inline def getBlock2(x, y, out type, out floor)
    mlogSafe("ucontrol", :getBlock, in x, in y, out type, out building, out floor);
    return building;
end;

x = floor(rand(100));
y = floor(rand(200));
// These two instruction generate the same mlog code:
building = getBlock(x + 1, y + 1, out type, out floor);
print(building, type, floor);

building = getBlock2(x - 1, y - 1, out type, out floor);
print(building, type, floor);
printflush(message1);
```

compiles to

```mlog
op rand *tmp0 100 0
op floor :x *tmp0 0
op rand *tmp2 200 0
op floor :y *tmp2 0
op add *tmp4 :x 1
op add *tmp5 :y 1
ucontrol getBlock *tmp4 *tmp5 :type :building :floor
print :building
print :type
print :floor
op sub *tmp7 :x 1
op sub *tmp8 :y 1
ucontrol getBlock *tmp7 *tmp8 :getBlock2:type :getBlock2:building :getBlock2:floor
print :getBlock2:building
print :getBlock2:type
print :getBlock2:floor
printflush message1
```

## Mlog blocks

Mindcode supports embedding larger pieces of mlog code, potentially containing complex logic, through _mlog blocks_. An mlog block may contain any mlog instructions, including instructions unknown to Mindcode, and is compiled mostly as-is into the final code. Mindcode variables can be accessed from an mlog block, allowing it to interact with the rest of the code. Mlog code inside an mlog block has syntax mostly similar to pure mlog, although Mindcode provides some features and imposes some restrictions not present in pure mlog.              

### Mlog block declaration

An mlog block represents executable code and may be placed wherever normal Mindcode code can be placed. The block may access Mindcode variables which are declared in the mlog block header:  

```
mlog (in input, out output, in out inputOutput) {
    ... mlog code ...
}
```

The variable declaration is similar to the declaration of function parameters, but all variables must refer to an existing variable (a non-existing variable is implicitly created in the relaxed syntax mode). The variables may be a global, main, local, or linked variable or a constant. Complex variables (such as external variables or array elements) aren't allowed.

Variables declared with the `in` keyword may be read by the mlog block instructions (the `in` keyword may be omitted). Variables declared with the `out` keyword are expected to be written to by the mlog block, and variables declared `in out` may be both read and written.      

When no input or output variables are declared for the mlog block, the parentheses may be left empty or omitted altogether.

### Mlog block syntax

The mlog code is placed within the curly braces. The code consists of labels and instructions. Individual instructions and labels must be separated by newlines or semicolons. 

A label consists of a single token, which must start with a letter and may contain alphanumerical characters and ends with a colon. A label name must be different from the names of declared input/output variables.

An instruction consists of one or more tokens, separated by one or more tabs or spaces. There are several types of tokens:

1. An instruction opcode. This is always the first token of the instruction, and it is not specially processed unless it is a raw token (see below).

2. A literal. Literals are handled according to Mindcode rules: when the literal isn't compatible with pure mlog (e.g., `1.5e3`), it is rewritten so that it is mlog compatible (in this case, to `1500`). When the literals do not conform to Mindcode rules (e.g., a value is too large, or the literal isn't recognized by the target Mindustry version), the usual warnings or errors are emitted. The following literals are supported:
   * [null](SYNTAX.markdown#null-literal) and [boolean literals](SYNTAX.markdown#boolean-literals),
   * [decimal literals](SYNTAX.markdown#decimal-literals), 
   * [binary and hexadecimal literals](SYNTAX.markdown#binary-and-hexadecimal-literals), 
   * [color literals](SYNTAX.markdown#color-literals), 
   * [named color literals](SYNTAX.markdown#named-color-literals), 
   * [character literals](SYNTAX.markdown#character-literals),
   * [string literals](SYNTAX.markdown#string-literals).

3. A [built-in variable](SYNTAX.markdown#built-in-variables-and-constants). Built-in variables are written to mlog as is, but warnings are generated if the variable is not recognized by Mindcode. Warnings are not generated for unknown built-in variables declared using the [#declare keyword](#declaring-new-built-in-variables).

4. A Mindcode variable. A Mindcode variable token can be entered in two ways: either by specifying the name of the variable declared in the mlog block header, or by prepending the variable name with a `$` (e.g., `$index`). In the second case, the variable must be an input variable; it is not possible to specify an input/output or output variable this way. A variable accessed via the `$` prefix needs to be created or declared outside the mlog block, even in relaxed syntax mode. If the variable is not found or doesn't represent a local processor variable (e.g., external variable), an error occurs. Variables are written to the compiled code using their mlog name, which is typically different from Mindcode name. Constants are encoded using their value.

5. A label reference: a token matching an existing label defined in the current mlog block. Mindcode adds a unique, mlog-block-specific prefix to label references as well as labels.

6. A _raw token_. Any token starting with a `:` (just like [mlog keywords in Mindcode](SYNTAX.markdown#mlog-keywords)) is written into the mlog exactly as it is after stripping the leading colon. This allows encoding those tokens into mlog that would otherwise be interpreted by Mindcode, such as `:1.5e3` or `:$index`, or which couldn't be encoded, such as tokens ending with a `}`. If the desired token itself starts with a colon, it can be also encoded this way by prepending an additional colon (e.g., `::token`). All tokens valid in pure mlog may be entered as a raw token.

7. An ordinary token. Any sequence of characters not matching any of the above token types is encoded into mlog as is. Even tokens that would produce an error in Mindcode source code (such as `1ee10`) are accepted as ordinary tokens. The only exception is that ordinary tokens may not contain single or double quotes (`'` or `"`), and must not end with a right brace (`}`). However, even those tokens may be encoded into mlog as raw tokens, e.g. `:{}`. 

Tokens that would be invalid in pure mlog, such as unclosed string literals (`"text`), or string literals not followed by a space (`"abc"def`) cause compilation errors in Mindcode too.

### Jumps and labels

Jumps and labels can be used in an mlog block. Labels must be unique within a block in which they're defined. Mindcode adds a unique, block-specific prefix to all labels, which distinguishes labels in one mlog block from labels in another block. This also means that labels must be resolved within the current mlog block: jumping from one block to another is not allowed.

Mindcode analyzes all `jump` instructions in an mlog block and inspects their labels; any label not defined in the current mlog block causes an error.

> [!NOTE]
> Mindcode tries to prevent creating jumps or other means of control transfer from one mlog block to another. When this protection is circumvented and jumps between different code blocks are realized, the resulting behavior of the program [is undefined](#jumping-between-mlog-blocks).

When a label appears as a token in _any_ instruction, it is replaced with the prefixed version. This ensures proper handling of possible future instructions referencing labels (say, `call label`). Naturally, any new, unknown instruction using labels won't be recognized by Mindcode and therefore can't be inspected to verify their labels are local.

Using an `end` instruction within an mlog block is also supported.

### Variables

Any variables used in the mlog block are used as-is. It is possible to use the same variable name in two mlog blocks, in which case the variable is shared between the blocks. Avoiding possible variable collisions between different mlog blocks is a responsibility of the user; this might be particularly challenging if the mlog blocks are defined in different source files.

> [!TIP]
> While it is not possible to use any expressions, even constant ones, in an mlog block, it is possible to create constants for these expressions outside the mlog block and use these constants as variable tokens. This might be especially useful when creating long text values, which may be split across several lines in Mindcode, but not in the mlog block.

It is the responsibility of the user to use the variables in an mlog block in a way compatible with the declaration. Mindcode currently doesn't even attempt to verify that known instructions use the variables in accordance with the declaration and can't do so in case of unknown instructions in principle. The following actions therefore lead to undefined behavior:
* Reading a value from a variable not declared as input.
* Writing to a variable not declared as output (this includes changing values of [program parameters](SYNTAX-1-VARIABLES.markdown#program-parameters), which cannot be declared as output variables at all).
* Accessing Mindcode's variables using their mlog names, bypassing the standard mechanism for accessing variables entirely. 

### Comments

Mlog block accepts two types of line comments:
* The `//` characters serve as a silent comment (unless they're part of a token, as in `print a//b`). The compiler ignores these comments completely.
* The `#` character creates an mlog comment. The mlog comments are propagated to the compiled code. When the `#` comment is on the same line as an instruction, it is output on the same line as the instruction in the compiled code as well.

Block comments (`/* A comment */`) are not supported in an mlog block.

### Optimization

The code in an mlog block is not optimized at all. Optimizations such as unreachable code elimination, print merging, and others are not applied to an mlog block. Only the following processing is applied to mlog blocks:

* A completely unreachable mlog block is removed by [Unreachable Code elimination](SYNTAX-6-OPTIMIZATIONS.markdown#unreachable-code-elimination). However, Mindcode can't detect when an mlog block itself makes the code following it unreachable (e.g., by using an infinite loop or the `end` instruction), and doesn't ever remove the code following an mlog block.
* If the mlog block contains an `end` instruction, Mindcode assumes the instruction may or may not be executed and updates the surrounding code accordingly.
* The mlog block is always treated as if it manipulates the text buffer: no print merging across the block will occur. 
* It is possible to activate a limited [Data Flow optimization](SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization) of mlog blocks by setting `mlog-block-optimization` to `true`. Currently, the optimization is only able to propagate constant values to the mlog block when possible. Correctly declaring output variables is vital for this optimization to produce valid code. 

### Example

#### Basic mlog code 

```Mindcode
const message = "Time measurement\n" +
        "Mlog block demonstration";
guarded linked switch = switch1;

require math;

mlog(in switch, out time) {
    # here starts the mlog block
    print $message                      # accessing an undeclared variable
    printflush message1                 # accessing a linked block using its implicit name
    control enabled switch false        # accessing a linked block using its Mindcode name
    set start @second                   # setting an mlog variable
    // Here we loop till the button is pressed. (This comment is ignored.)
    loop:                               # a label
        sensor enabled switch @enabled
    jump loop notEqual enabled false
    op sub time @second start           # setting the output variable
    # here ends the mlog block
}

print(round(time));
printflush(message1);
```

Compiled code:

```mlog
jump 0 equal switch1 null
# here starts the mlog block
print "Time measurement\nMlog block demonstration" # accessing an undeclared variable
printflush message1                     # accessing a linked block using its implicit name
control enabled switch1 false           # accessing a linked block using its Mindcode name
set start @second                       # setting an mlog variable
m0_loop:                                # a label
sensor enabled switch1 @enabled
jump m0_loop notEqual enabled false
op sub :time @second start              # setting the output variable
# here ends the mlog block
op add *tmp1 :time 0.5
op floor *tmp0 *tmp1 0
print *tmp0
printflush message1
```

#### Jumping between mlog blocks

To better understand how problems may arise from flow control structures introduced into the code in ways not understood by Mindcode, let's consider this:

```Mindcode
var i = 0;

mlog { set address @counter }
print(i++);
printflush(message1);
mlog {
    jump finish greaterThan $i 10
    set @counter address
    finish:
}

print("Final value: ", i);
printflush(message1);
```

which compiles to

```mlog
set address @counter
set .i 1
print 0
printflush message1
jump m1_finish greaterThan .i 10
set @counter address
m1_finish:
print "Final value: 1"
printflush message1
```

By accessing the `@counter`, a loop was created in a way which Mindcode doesn't understand. Due to this, the expressions manipulating `i` were removed and replaced by constant values that would be correct had the loop not been there.

The variable `i` is still set, because it is read inside the mlog block, and we did let Mindcode know.

---

[« Previous: Code optimization](SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Function reference for Mindustry Logic 6 »](FUNCTIONS-60.markdown)
