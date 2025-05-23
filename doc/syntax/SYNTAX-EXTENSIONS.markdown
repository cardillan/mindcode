# Extending Mindcode

In addition to supporting all mlog features available in the Mindustry version selected by the [language target](SYNTAX-5-OTHER.markdown#option-target), Mindcode also provides a way to encode mlog instructions which are not part of the Mindcode standard. This may come in handy in these situations:

1. You want to use nonstandard instructions, keywords, blocks, or built-in variables provided by a Mindustry mod or an alternate client.
2. A new version of Mindustry (either an official release or a bleeding-edge version) creates new instructions not known to Mindcode.
3. An instruction was not implemented correctly in Mindcode and a fix is not yet available.

Mindcode provides these options to extend its basic syntax:

* declaring new mlog keywords (function keywords),
* declaring new builtin variables,
* declaring new linked block names,
* defining new mlog instructions.

## Declaring new mlog keywords

[Mlog keywords](SYNTAX.markdown#mlog-keywords) are used as arguments in some mlog instructions. The instructions only accept a limited set of keywords, and those keywords cannot be stored in variables; they need to be used explicitly with the instruction. Using a keyword not defined for the given instruction argument by mlog standard is not supported and leads to compilation errors.

While mods probably cannot specify a new keyword, a custom Mindustry client could. In this case, it is possible to declare the new keyword. A category into which the new keyword belongs must be specified. The category tells Mindcode which instruction/parameter accepts the new keyword. These keyword categories are supported:

| Category          | Meaning                                                           |
|-------------------|-------------------------------------------------------------------|
| alignment         | text alignment in the `draw print` instruction                    |
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

Example: if the `draw print` instruction was extended to accept additional alignments, the new keywords could be declared like this:

```Mindcode
#set target = 8;
#declare alignment :baselineBottom, :baselineTop;

print("Hello, world!");
drawPrint(30, 40, :baselineBottom);
```
  
```mlog
print "Hello, world!"
draw print 30 40 baselineBottom 0 0 0
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
> If an instruction alters the program flow (for example, if a new function call instruction was added to Mindustry Logic), it cannot be safely encoded using this mechanism.

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
  * Numeric literal: all other literals must not be marked with either modifier. The primary use for numeric literals is to provide fill-in values (typically zeroes) for unused instruction parameters.
  * User variable: the variable is used as an instruction argument. The argument must use the `in` and/or `out` modifier to inform Mindcode how the corresponding instruction argument behaves:
    * `in`: the argument represents an input value—the instruction reads and uses the value of the variable.
    * `out`: the argument represents an output value—the instruction produces a value and stores it in the variable.
    * `in out`: the argument represents an input/output value—the instruction both reads and uses the input value, and then updates the variable with a new value. With a possible exception to the `sync` instruction, no mlog instruction currently takes an input/output argument.

> [!TIP]
> Keywords can be encoded either using the Mindcode keyword syntax (e.g., `:keyword`), or as a string literal (`"keyword"`). Only if the keyword contains characters unsupported by Mindcode syntax, it can only be encoded as a string literal (e.g., `"unsupported:keyword"`). This is the only way to encode such a keyword to mlog.  

> [!TIP]
> Although not strictly required, it is recommended to create an inline function with proper input/output parameters for each custom-generated instruction. This way, the requirement that the `mlog()` functions always use user variables as arguments can be easily met, while allowing to use expressions for input parameters in the call to the enclosing function—see the examples below. Furthermore, it is possible to use keywords as function arguments to inline functions.
 
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

---

[« Previous: Code optimization](SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Function reference for Mindustry Logic 6 »](FUNCTIONS-60.markdown)
