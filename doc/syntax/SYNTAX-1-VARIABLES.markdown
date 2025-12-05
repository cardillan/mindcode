# Variables

Variables in Mindcode are built upon functionality provided by Mindustry Logic.

# Mindustry Logic

## Processor variables

Mindustry Logic processor variables are basic variables that can be used to store and retrieve values.  

These variables can contain a number, an object, or a `null` value. An object can be a Mindustry object, such as a block or a unit, a Mindustry built-in value, such as `@coal`, or a string such as `"Text"`, or any other supported non-numeric value.

### Numeric conversion

When a variable is used in a numeric operation, it is converted to a number depending on its value:

* a number is used as-is, or might be converted to an integer when needed.
* `null` is converted to `0`,
* an object is converted to `1`.

### Integer/double conversion

All numeric values, both integer and floating-point, are stored as `double`, a 64-bit floating point value. Some Mindustry Logic operations operate on integer values. To perform these operations, the numeric value is converted from the floating point representation (`double`) to a 64-bit integer (a Java `long`), then the operation is performed and the result is converted to a `double` again. 

> [!IMPORTANT]
> Conversion from `double` to `long` (an _integer conversion_) leads to loss of fractional part of the floating-point value. Values are always truncated towards zero, e.g., 0.9999 gets converted to 0, and -7.8 gets converted to -7.
> 
> Values larger than 2<sup>63</sup> are converted to 2<sup>63</sup> during integer conversion.  
>
> While `long` values are able to keep 64 bits, `double` values only hold 53 significant bits of precision. Processor variables are therefore limited to 53 significant bits after integer conversion. When an integer operation produces a number between 2<sup>53</sup> and 2<sup>63</sup>-1, the result is rounded to the closest double representation for storage in a processor variable. This rounding during a _double conversion_ on a number exceeding the safe integer range (i.e., outside the -2<sup>53</sup> to 2<sup>53</sup> range) may destroy information stored in individual bits of the number's integer representation.
>
> Every intermediate result during expression evaluation is stored in a processor variable, so the integer/double conversion may happen at every step of computation in mlog.

Producing integer numbers this large would be somewhat unusual in a typical Mindustry Logic program, except using bitwise operations to manipulate individual bits. In these cases, make sure to use at most 53 bits of each variable. Using 54 bits is not safe, because performing bitwise complement (operation inverting all bits) on values larger than 2<sup>53</sup>-1 may lead to loss of precision as well.
 
## Linked blocks

Linked blocks provide the most basic means for a processor to interact with blocks (i.e., buildings) in the Mindustry World. When a block is linked to a processor, a special, read-only variable which represents the linked block is created in the processor. The variable name is created using a base block name, i.e., the one-word representation of the block type name (e.g., `battery` for `@battery-large` or `cell` for `@memory-cell`) and a unique number starting from one.

> [!IMPORTANT]
> Logic processors silently ignore assignments to variables representing linked blocks. If a processor variable with the same name as a newly linked block exists in a processor when the block is linked, it is removed from the processor and replaced by a processor variable representing the linked block. 

Since linked blocks are present as special processor variables, Mindcode makes sure to handle these variables correctly, depending on a list of known base block names. Any variable name consisting of a base block name and a positive integer may potentially represent linked blocks.

* These are block variable names: `point3`, `arc7`, `tank999999` (indexes this high are improbable to ever appear, but not impossible in principle).
* These aren't: `switch` (no numeric index at all), `cell05` (leading zero), `Router15` (upper-case letter) or
  `wave_1` (the index doesn't immediately follow the block name).

<details><summary>Show a full list of known base block names.</summary>

* `acropolis`
* `afflict`
* `arc`
* `assembler`
* `bank`
* `bastion`
* `battery`
* `bore`
* `breach`
* `bridge`
* `canvas`
* `cell`
* `centrifuge`
* `chamber`
* `citadel`
* `compressor`
* `concentrator`
* `condenser`
* `conduit`
* `constructor`
* `container`
* `conveyor`
* `crucible`
* `crusher`
* `cultivator`
* `cyclone`
* `deconstructor`
* `diffuse`
* `diode`
* `disassembler`
* `disperse`
* `display`
* `distributor`
* `dome`
* `door`
* `drill`
* `driver`
* `duct`
* `duo`
* `electrolyzer`
* `extractor`
* `fabricator`
* `factory`
* `foreshadow`
* `foundation`
* `furnace`
* `fuse`
* `gate`
* `generator`
* `gigantic`
* `hail`
* `heater`
* `huge`
* `illuminator`
* `incinerator`
* `junction`
* `kiln`
* `lancer`
* `link`
* `loader`
* `lustre`
* `malign`
* `meltdown`
* `melter`
* `mender`
* `message`
* `mine`
* `mixer`
* `module`
* `node`
* `nucleus`
* `pad`
* `panel`
* `parallax`
* `point`
* `press`
* `processor`
* `projector`
* `pulverizer`
* `pump`
* `radar`
* `reactor`
* `reconstructor`
* `redirector`
* `refabricator`
* `ripple`
* `router`
* `salvo`
* `scathe`
* `scatter`
* `scorch`
* `segment`
* `separator`
* `shard`
* `smelter`
* `smite`
* `sorter`
* `source`
* `spectre`
* `sublimate`
* `swarmer`
* `switch`
* `synthesizer`
* `tank`
* `thruster`
* `titan`
* `tower`
* `tsunami`
* `turret`
* `unloader`
* `vault`
* `void`
* `wall`
* `wave`
* `weaver`

</details>

> [!IMPORTANT]
> When building a schematic, it is possible that the processor will be built before some other parts of the schematics. In this case, its code will be executed before all linked blocks expected by the schematics are finished and linked. Code that depends on the linked blocks defined by the schematics to be available may therefore execute incorrectly, possibly resulting in a state which can only be fixed by restarting the processor or even rebuilding the schematics.
> 
 > To help avoid this situation, Mindcode provides means to generate code ("guard code") that will pause the execution of the program until required linked blocks become available.  

## External memory

Mindustry provides special blocks that are capable of holding numeric values independently of the processor:

* `@memory-cell`
* `@memory-bank`
* `@world-cell`

All these blocks consist of individual elements, which are identified by an index.

Values written to the elements of these blocks are available to all processors that have a reference to the block (with possible exception to team rules) and can be used to communicate between processors. Once written, the values remain in the memory block until the block is destroyed.

Only numeric values are supported by memory block elements. When the program attempts to write a non-numeric value to an element, the value actually written is 0 (for `null`) or 1 (for all other non-null objects).

# Mindcode variables

Depending on the [syntax mode](SYNTAX.markdown#syntax-modes), variables are created either through explicit variable declarations or through using them in the source code.

> [!NOTE]
> Variables created using a declaration are called 'explicit' or 'declared' variables. Variables used in the code without prior declaration are called 'implicit' variables.

Detailed information on individual kinds of variables and means of their declaration are described later in this chapter.

> [!IMPORTANT]
> While compiling the source code, Mindcode may perform various optimizations which replace variables (both the implicit and explicit ones) with different user or computer-generated variables or with constant values, or remove unused variables altogether, while upholding the semantics of the compiled program. This may make it quite difficult to see how the compiled code relates to the source code.

Example:

```Mindcode
var a = 10;
var b = 20;
print(a);
printflush(message1);
```

compiles to:

```mlog
print 10
printflush message1
```

Variable `a` in the above example was removed, because it is possible to use the value assigned to it (`10`) directly later in the program. Variable `b` was removed, as it is not used in any following computation at all. 

[Program parameters](#program-parameters) and some specifically declared variables may be preserved in the program even if they could be replaced by constant values or are not used at all.

## Variable scope

Variables are limited to a certain scope and are considered nonexistent outside their scope. The following scopes are recognized:

* Global: encompasses the entire program. Global variables are generally accessible from anywhere in the program.
* Main: encompasses the main program body. The main program body generally consists of all code outside user-defined functions.  
* Local: encompasses a single user-defined function. Function parameters are also local to their function. 
* Code block: encompasses a single body of statement, e.g., body of a while loop, a branch of the else statement, or an explicitly marked code block. If a code block contains nested code blocks, the scope of the outer block includes all inner blocks. 

## Implicit variables

Implicit variables are only supported in the [`relaxed` syntax](SYNTAX.markdown#syntax-modes) , and are created when first encountered in the code. The kind and scope of the variable is determined by the name of the variable:

* **Linked variables**: if the variable name corresponds to a known linked block, it is automatically regarded as a linked variable referring to that block, and its scope is global (e.g., `cell1` or `switch2`). To use a linked block not recognized by Mindcode (e.g., a block provided by a mod extension), an explicit declaration is required. 
* **External variables**: if the variable name starts with the `$` prefix, the variable is external, residing in a common external memory pool (a _heap_). Scope of external variables is global (e.g., `$Total`). See [Heap](#heap) for information on heap declaration.
* **Global variables**: if the variable name doesn't contain lowercase characters, it is a regular variable in the global scope (e.g., `COUNT`).
* **Main/local variables**: if the variable name contains at least one lowercase character, it is a regular variable in either the main (if used outside a function) or local (if used within a function) scope (e.g., `unitType`).

An example code using implicit variables:

```Mindcode
def foo(x)
    MAIN = x + 10;
    local = 10;
end;
MAIN = 5;
local = 5;
x = 0;
foo(10);
print(MAIN, ", ", local);
printflush(message1);
```

This code displays `20, 5` on the `message1` block in the Mindustry world, since both `x` and `local` in the `foo` function are local variables and therefore distinct from `x` and `local` in the main code block.

In `relaxed` syntax, using global variables as function parameters (e.g., `def foo(Z) ... end`) is not allowed. In `strict` syntax, such parameters are allowed.

> [!NOTE]
> In previous versions of Mindcode, global variables also served as program parameters [described below](#program-parameters). This usage of global variables is no longer supported.

## Explicit variables

Explicit variables are created using explicit declaration. The kind of the variable is determined by the declaration used, and the scope by the declaration placement: global, when the declaration occurs outside all code blocks, or limited to the code block containing the declaration.

There are no name restrictions on explicitly declared variables, except the `$` prefix, which can only be used on explicitly declared external variables. Specifically, it is possible to use a name of a linked block for explicitly declared variables (e.g., `wave1`) regardless of their type, i.e., even for variables that do not represent linked blocks.

When a main or local variable has the same name as a global variable, the global variable is said to be _shadowed_ and cannot be accessed in the corresponding code block.

## Heap

Heap provides default storage for external variables and needs to be declared before an external variable without explicit storage specification is declared or implicitly created. Heap is declared using this syntax:

```
allocate heap in <memory>[<range>];
```

for example:

```Mindcode
allocate heap in cell4[50 ... 64];
```

> [!NOTE]
> The range specification is optional. When no range is specified and the memory block is resolved to a memory bank (i.e., its name starts with 'bank'), range `0 ... 512` is assumed. Otherwise, range `0 ... 64` is assumed.

This statement allocates a heap, stored in `cell4`, and uses memory locations 50, 51, 52, ..., 62, and 63 for external variables (note the exclusive range). If you create more external variables than you have allocated space for, a compilation error will occur. In that case, allocate more space for the heap in the cell, or possibly switch to a memory bank to get even more space.

A linked block or variable, a constant, a parameter or a global variable can be used as a memory in the heap declaration. This way (by using a global variable) it is even possible to choose a memory block for heap dynamically. It is up to the user to ensure that:

1. The linked block or variable was initialized before external variables are accessed for read or write.
2. The value of a global variable used to hold the memory doesn't change once initialized.

The first requirement may be satisfied by using a guarded linked block as the initial value of the variable. In more complex cases, assigning a value to the variable via function call might be necessary.

# Arrays

Arrays consist of elements which can be accessed via an integer index. The index is specified in square brackets: `a[5]` accesses an element of array `a` at the position `5`. The first element has an index of 0, therefore `a[1]` refers to the **second** element, and `a[9]` refers to the last element of a ten-element array. 

## Implicit arrays

All [memory blocks](#external-memory) can be accessed as an array in Mindcode. These arrays do not need to be declared and are used by referencing the memory block directly: `cell1[0] = 10;` writes the value of `10` to the first element of the `cell1` memory cell. It is possible to access an implicit array even when the reference to the memory block is stored in another variable: `memory = bank1; print(memory[8]);`. The array size depends on the memory block used, and Mindcode doesn't check bounds when accessing implicit arrays in any way.

It is also possible to store statically or dynamically linked blocks in regular variables or to pass them to non-recursive functions:

```Mindcode
linked bank1, bank2;

def copy(source, target, size)
    for var i in 0 ... size do
        target[i] = source[i];
    end;
end;

begin
    copy(bank1, bank2, 512);
end;
```

As implicit arrays are stored in memory blocks, they can only hold numeric values.

## External arrays

External arrays are explicitly declared and are allocated on the [heap](#heap) similarly to other external variables. The array size is specified when declaring the array, but Mindcode again doesn't check bounds when accessing external arrays in any way. Accessing an element outside the bounds of an external array may cause other elements or variables stored on the heap to be accessed instead. 

As external arrays are stored in memory blocks, they can only hold numeric values.

## Internal arrays

Mindustry Logic doesn't provide a specialized mechanism for creating arrays out of internal variables. However, it is possible to create a reasonably efficient implementation of random access arrays using _`@counter` arrays_, named as such since accessing an element of such arrays involves manipulating the `@counter` variable. Individual elements of such arrays are stored in processor variables (one variable per array element), and therefore can hold non-numerical values as well, such as unit or block references, items, liquids, strings and so on.

Accessing individual elements of internal arrays is slower than accessing elements of external arrays and consumes additional instruction space for `@counter` tables. However, when Mindcode is able to resolve the index during compilation to a numeric value, the variable corresponding to the element is accessed directly, providing performance which can be even better than that of external arrays. When Mindcode is able to resolve all index-based array accesses (e.g., when unrolling all loops in the program), the `@counter` tables might be eliminated entirely, keeping only the individual element variables in the resulting code.

Several different implementations of internal arrays are available in target 8 or higher, some of them may perform better than normal `@counter` tables available in Mindustry 7. The [Array Optimization](optimizations/ARRAY-OPTIMIZATION.markdown) chooses the most efficient implementation for each array or individual array access. 

## Remote arrays

Remote arrays are internal arrays stored in another processor. They are implemented similarly to internal arrays, but the mechanism to access individual elements reads the values from the remote processor. In many situations, remote arrays can be as fast as internal arrays, or they may need an additional instruction to read or write the value from/to the remote processor. 

## Subarrays

For some operations, such as array assignments, list-iteration loops, and function calls, it is possible to select a portion of the array for the operation. The syntax for creating subarrays is: `array[range]`, where `range` is a constant range expression. It is possible to create subarrays from implicit, external, internal, and remote arrays: 

```Mindcode
allocate heap in bank1;
var a[10];
external $a[10];

cell1[0 ... 5];         // Elements 0 to 4 from cell1 memory cell 
$a[5 .. 8];             // Elements 5 to 8 of an external array
a[1 ... 10];            // All but the first element of an internal array  
```

# Variable declarations

**Variables** are declared using the `var` keyword. The syntax for declaring a variable is:

```
[modifier [modifier ... ]] var <variable1> [= <initial value>] [, <variable2> [= <initial value>] ... ];
```

The initial value is assigned directly to the variable at the moment of the declaration. Any expression can be used as the initial value, including function calls.

**Arrays** are declared using the `var` keyword in the same way, but the `[]` brackets are used to specify the array size. The syntax for declaring an array is:

```
[modifier [modifier ... ]] var <variable1>[size] [= (<initial values>)] [, <variable2> [= (<initial values>)] ... ];
```

`size` must be a constant expression evaluating to a positive integer, which specifies the array size, i.e., the number of elements in the array. When initial values for the array are specified, their number must be equal to the size of the array, or the size specification may be omitted (`var a[] = (1, 2, 3);`).

Modifiers specify the kind of the variable to be created. When a modifier is used, the `var` keyword is optional. Modifiers can be specified in any order.

Variables and arrays may be declared in the same declaration.

Examples of variable declarations:

```
var a, b[10], c = rand(10), d[3] = ("x", "y", "z"); 
```

Variable modifiers are used to provide additional information of the variable. There are two kinds of variable modifiers:

* **Storage modifiers**: determine how the variable is stored or handled by Mindcode.
* **Other modifiers**: provide additional specification or parametrization of the variable.

Some modifiers accept additional parameters, which are specified in parentheses after the modifier name. For example:

```
external(cell1) var foo[4];
```

## Storage modifiers

The following storage modifiers are available:

* `const`: the variable represents a [constant value](#constants).
* `linked`: the variable is a [linked variable](#linked-variables).
* `external`: the variable is an [external variable](#external-variables).
* `export`: the variable is an [exported variable](#exported-variables): stored in the current processor, but made available to other processors. 
* `remote`: the variable is a [remote variable](#remote-variables) stored in another processor.

Storage modifiers are only allowed in the global scope. At most one of the storage modifiers can be specified in a declaration. When no storage modifier is specified, the declaration creates [regular variables](#regular-variables) stored in the current processor.

## Other modifiers

Other modifiers are compatible only with specific storage modifiers. This table specifies which modifiers are compatible with which storage modifiers and scopes:

| Modifier   | Compatible with                        | Scope           |
|------------|----------------------------------------|-----------------|
| `cached`   | `external`, `remote`                   | global          |
| `guarded`  | `linked` or none                       | global          |
| `mlog`     | `export`, `remote` or none             | global          |
| `noinit`   | `export`, `external`, `remote` or none | global or local |
| `volatile` | `export` or none                       | global          |

### `cached` modifier

Can be used with the `external` and `remote` modifiers. This modifier cannot be used when declaring arrays.

The value of a cached variable is read from the external memory or processor just once at the variable creation and is kept in a dedicated processor variable (a _cache variable_). When reading a cached variable, Variable reads are served using the cache variable. Writes update the cache variable and also write the new value to the memory/processor.

If an initial value was specified in the declaration, the external/remote value is not read at all, and the initial value is written to the memory or processor. 

Cached variables are useful in situations where you want to store the latest value of the variable in an external memory to be read from when the processor is reset.

> [!IMPORTANT]
> Using the `cached` modifier while using a variable to represent the external memory or remote processor may lead to unexpected results. Only the external memory or remote processor currently referenced by the variable is updated when the variable is written to.

### `guarded` modifier

Can be used with the `linked` modifier or without a storage modifier, in which case the `linked` modifier is implied. Mindcode generates a guard code (one instruction per declared variable) which pauses the program execution until a block is linked to the processor under the expected name:

```Mindcode
/// A guard code: loops until message1 is not null
guarded output = message1;
print("Here we are");
printflush(output);
stopProcessor();
```

compiles to:

```mlog
# A guard code: loops until message1 is not null
jump 0 equal message1 null
print "Here we are"
printflush message1
stop
```

### `mlog` modifier

Can be used with the `export` or `remote` modifier, or without a storage modifier.

By using the `mlog` modifier, it is possible to specify the mlog name of the variable or array elements, as stored in the current or remote processor. The name(s) must be specified as a list of constant string expressions enclosed in parentheses, or as a single specification of a lookup type (in case of arrays). 

Specifying an mlog name is useful when accessing the variable indirectly, by the current or other processors.

> [!NOTE]
> In most cases, indirect access also requires the variable to be declared `volatile`.

When using the `mlog` modifier, only one variable or array can be specified per declaration (as multiple variables would share the same mlog name otherwise). Examples of declarations using the `mlog` modifier:

```
mlog("foo") var foo;
mlog("a", "b", "c") var bar[3];
mlog(:liquid) var baz[10];
mlog(@coal.@name) var coal;
```

* When declaring a variable, a single constant string expression must be specified.
* When declaring an array, there are two possibilities:
  * A name as a constant string expression is specified for each array element. The number of names must be equal to the array size.
  * A single lookup type is specified, creating a [lookup array](optimizations/ARRAY-OPTIMIZATION.markdown#lookup-arrays). Lookup arrays require target 8 or higher. The following lookup types are available:  `:block`, `:unit`, `:item`, `:liquid` or `:team`.
* Names specified by mlog must not correspond to linked block names.
* When a name specified by the `mlog` modifier collides with another user-defined variable or array element in the current processor, a warning is produced. 

#### Lookup variables

When compiling code for Mindustry 8, the `mlog` modifier allows you to create variables to be used as _lookup variables_: variables whose content is accessed indirectly, typically by using names of Mindustry content objects. These lookups may be faster even than optimized `case` statements, especially when the Mindustry content used for lookup doesn't have logic IDs assigned.

Example:

```Mindcode
#set target = 8;

volatile mlog(@ore-copper.@name)  ore_copper = @copper;
volatile mlog(@ore-lead.@name)    ore_lead = @lead;
volatile mlog(@ore-coal.@name)    ore_coal = @coal;

// Find floor type at given coordinates
getBlock(x, y, , out floor);

// Convert the floor type to item that can be mined there
ore = @this.read(floor.@name);
print(ore);
```

The variables are declared `volatile`, as this ensures they will be available for indirect access. 

### `noinit` modifier

Can be used with the `export`, `external` or `remote` modifier, or without a storage modifier. This modifier cannot be used for declaring arrays or when the declared variable is assigned an initial value.

The `noinit` modifier can be used for two purposes:

* To suppress the "uninitialized variable" warning for the declared variable. Uninitialized global variables retain the last value assigned to them in the last iteration of the program.
* When used with the `cached` modifier, the initial value of the variable is not read from the external memory or remote processor at all; the variable only allows writing new values to the memory/processor.

Cached `noinit` variables are useful for a sending side of a unidirectional communication between processors in situations where the variable is also read locally, and is read more often then it is written to. The variable is read without any performance penalty, but all the writes are automatically propagated to the external memory.

### `volatile` modifier

Can be used with the `export` modifier, or without a storage modifier. Variables declared `export` are automatically volatile, so this modifier has no effect on `export` variables.

The compiler assumes that volatile variables or array can be changed externally (for example, by other processors, or via the `sync` instruction) or indirectly (via the `read` or `write` using `@this` as the processor) and handles them accordingly. A volatile variable is guaranteed to exist in the processor (and can be accessed remotely or indirectly), even if it is not directly accessed by an mlog instruction.

# Types of variables

## Regular variables

Regular variables and arrays directly correspond to Mindustry Logic variables. They are the most basic all-purpose variables. Regular variables are declared without any storage modifier.

When declaring regular variables, these additional modifiers can be used:

* [`mlog`](#mlog-modifier)
* [`noinit`](#noinit-modifier)
* [`volatile`](#volatile-modifier) 

Example

```Mindcode
noinit volatile var a;      // Uninitalized volatile global variable
var b;                      // "Normal" global variable

begin
    b = 2;                  // Accesses global variables
    
    if a > 0 then
        var c = 3;          // Creates and initializes a main variable
        var a = c + 1, d;   // More main variables.
    
        d = a + b + c;
        println(d);
    end;
    
    // Global variable. Variable 'a' declared in code block above is no longer in scope.
    println(a);
end;
```

## Constants

Constants are unmodifiable variables that can hold values evaluated at compile time. Mindcode doesn't store constant values in processor variables but places the value of the constant directly into the compiled code when the constant is used. Storing a value in a constant allows easily changing the value at a single place in the source code. Unlike program parameters, changes to constants aren't possible in the compiled code.

Constants are declared using the [`const` storage modifier](#storage-modifiers). No other modifiers are supported.

A constant declaration must specify an initial value for the variable/array. The following types of values can be assigned to constants:

* literals of any kind except formattable string literals and mlog keywords,
* other constants and constant expressions,
* linked variables,
* linked blocks (only in relaxed syntax mode),
* built-in variables, except built-in variables known not to be constant.

The following values can be assigned to constant variables, but not to arrays:

* formattable string literals (mlog-incompatible),
* mlog keywords (mlog-incompatible).

These constants can be passed to inline functions but cannot be stored in variables or passed to out-of-line or recursive functions.

Example:

```Mindcode
const DEBUG = true;
const HIGH_SPEED = 50;
const LOW_SPEED = HIGH_SPEED / 2;
const RATIO = sqrt(2);
const message = DEBUG ? "Debug" : "Release";
const FORMAT = $"Position: $x, $y";

// Array of switches - linked blocks
linked switch1, switch2, switch3;
const links[] = (switch1, switch2, switch3); 
```

Compile-time evaluation uses the same rules as Mindustry Logic, i.e., `const ERROR = 1 / 0` is a valid constant declaration which creates a constant `ERROR` with a value of `null`.

If a numeric value is assigned to a constant, and it isn't possible to [encode the value into an mlog literal](SYNTAX.markdown#specifics-of-numeric-literals-in-mindustry-logic), a compilation error occurs.

Among other uses, constants can be used to optionally exclude sections of code while compiling:

```Mindcode
const DEBUG = false;

// ...

if DEBUG then
    println("State: ", state);
    println("Item count: ", @unit.@totalItems);
end;
```

the entire `if DEBUG then ... end` statement will be skipped and not included in the compiled code.

### Constant expressions

Expressions that have constant arguments and are deterministic (i.e., they give the same result for the same input) are constant expressions. This also applies to deterministic logic functions, such as `sin()` or `sqrt()`, and some built-in functions, such as `length()` and `encode()`.

Furthermore, `@id` and `@name` properties can be used in constant expressions, assuming `builtin-evaluation` is set to `full` or `compatible`. When `builtin-evaluation` is set to `compatible`, only stable objects that have [stable logic IDs](SYNTAX-5-OTHER.markdown#option-builtin-evaluation) are allowed in constant expressions.

User-defined functions can also be used in constant expressions. Currently, only functions meeting these criteria can be used as constant expressions:

* the function isn't declared `noinline` or `export`,
* the function isn't recursive,
* the function doesn't have a vararg parameter,
* the function returns a value and doesn't have other output parameters,
* the function consists of a single, constant expression (a `return` statement may or may not be used),
* any global constants used in the function must be declared not only before the function itself, but also before the place where the function is used as a constant expression.

An example of a constant expression using a user-defined function:

```Mindcode
#set optimization = none;
const OFFSET_X = 100;
const OFFSET_Y = 200;

def coords(x, y)
    return 1000 * (x + OFFSET_X) + (y + OFFSET_Y);
end;

const COORD1 = coords(10, 20);
const COORD2 = coords(55, 71);

println(COORD1);
println(COORD2);
```

compiles to:

```mlog
print 110220
print "\n"
print 155271
print "\n"
end
```

### Constant arrays

By specifying the `const` modifier it is possible to create a constant array. A constant array must be initialized, and its elements cannot be modified after initialization. Processor variables are not allocated for constant array elements; the constant values are used instead.

```Mindcode
var array[3] = (rand(4), rand(4), rand(4));
const colors[] = ("[red]", "[green]", "[blue]", "[crimson]");
print(colors[floor(array[0])]);
print(array);
```

compiles into

```mlog
op rand *tmp0 4 0
op rand *tmp1 4 0
op rand *tmp2 4 0
op floor *tmp4 *tmp0 0
op mul *tmp7 *tmp4 2
op add @counter *tmp7 6
set *tmp6 "[red]"
jump 13 always 0 0
set *tmp6 "[green]"
jump 13 always 0 0
set *tmp6 "[blue]"
jump 13 always 0 0
set *tmp6 "[crimson]"
print *tmp6
print *tmp0
print *tmp1
print *tmp2
```

### Constants representing built-in icons

Mindustry has a set of built-in icons that are represented by specific Unicode characters and are properly rendered in all the user interface elements. While it is possible to enter a corresponding Unicode character into a String literal directly, it would be quite cumbersome. To provide access to these icons, Mindcode comes with a predefined set of constants that correspond to individual icons. The constants map the symbolic icon names onto a String literal containing their corresponding Unicode character.

Printing an icon is as easy as this:

```Mindcode
println(ITEM_LEAD, " ", vault1.@lead);       // As a list of values
println($"$ITEM_COAL ${vault1.@coal}");      // As formattable literal
printflush(message1);
```
Built-in icon constants are defined in the global scope. User-defined constants, parameters, and variables in the global scope must not use identifiers used by these constants.

The list of all existing icons is quite huge:

<details><summary>Show a full list of Mindustry block icons.</summary>

* `BLOCK_ADDITIVE_RECONSTRUCTOR`
* `BLOCK_AFFLICT`
* `BLOCK_AIR_FACTORY`
* `BLOCK_ALPHA_MECH_PAD`
* `BLOCK_ARC`
* `BLOCK_ARKYCITE_FLOOR`
* `BLOCK_ARKYIC_BOULDER`
* `BLOCK_ARKYIC_STONE`
* `BLOCK_ARKYIC_VENT`
* `BLOCK_ARKYIC_WALL`
* `BLOCK_ARMORED_CONVEYOR`
* `BLOCK_ARMORED_DUCT`
* `BLOCK_ATMOSPHERIC_CONCENTRATOR`
* `BLOCK_BALLISTIC_SILO`
* `BLOCK_BARRIER_PROJECTOR`
* `BLOCK_BASALT`
* `BLOCK_BASALT_BOULDER`
* `BLOCK_BASIC_ASSEMBLER_MODULE`
* `BLOCK_BASIC_RECONSTRUCTOR`
* `BLOCK_BATTERY`
* `BLOCK_BATTERY_LARGE`
* `BLOCK_BEAM_DRILL`
* `BLOCK_BEAM_LINK`
* `BLOCK_BEAM_NODE`
* `BLOCK_BEAM_TOWER`
* `BLOCK_BERYLLIC_BOULDER`
* `BLOCK_BERYLLIC_STONE`
* `BLOCK_BERYLLIC_STONE_WALL`
* `BLOCK_BERYLLIUM_WALL`
* `BLOCK_BERYLLIUM_WALL_LARGE`
* `BLOCK_BLAST_DOOR`
* `BLOCK_BLAST_DRILL`
* `BLOCK_BLAST_MIXER`
* `BLOCK_BLOCK_FORGE`
* `BLOCK_BLOCK_LAUNCHER`
* `BLOCK_BLOCK_LOADER`
* `BLOCK_BLOCK_UNLOADER`
* `BLOCK_BLUEMAT`
* `BLOCK_BOULDER`
* `BLOCK_BREACH`
* `BLOCK_BRIDGE_CONDUIT`
* `BLOCK_BRIDGE_CONVEYOR`
* `BLOCK_BUILD_TOWER`
* `BLOCK_CANVAS`
* `BLOCK_CARBIDE_CRUCIBLE`
* `BLOCK_CARBIDE_WALL`
* `BLOCK_CARBIDE_WALL_LARGE`
* `BLOCK_CARBON_BOULDER`
* `BLOCK_CARBON_STONE`
* `BLOCK_CARBON_VENT`
* `BLOCK_CARBON_WALL`
* `BLOCK_CELL_SYNTHESIS_CHAMBER`
* `BLOCK_CHAR`
* `BLOCK_CHEMICAL_COMBUSTION_CHAMBER`
* `BLOCK_CLIFF`
* `BLOCK_CLIFF_CRUSHER`
* `BLOCK_CLIFFS`
* `BLOCK_COAL_CENTRIFUGE`
* `BLOCK_COMBUSTION_GENERATOR`
* `BLOCK_COMMAND_CENTER`
* `BLOCK_CONDUIT`
* `BLOCK_CONSTRUCTOR`
* `BLOCK_CONTAINER`
* `BLOCK_CONVEYOR`
* `BLOCK_COPPER_WALL`
* `BLOCK_COPPER_WALL_LARGE`
* `BLOCK_CORE_ACROPOLIS`
* `BLOCK_CORE_BASTION`
* `BLOCK_CORE_CITADEL`
* `BLOCK_CORE_FOUNDATION`
* `BLOCK_CORE_NUCLEUS`
* `BLOCK_CORE_SHARD`
* `BLOCK_CORE_SILO`
* `BLOCK_CORE_ZONE`
* `BLOCK_CRATER_STONE`
* `BLOCK_CRATERS`
* `BLOCK_CRAWLER_FACTORY`
* `BLOCK_CRYOFLUID_MIXER`
* `BLOCK_CRYSTAL_BLOCKS`
* `BLOCK_CRYSTAL_CLUSTER`
* `BLOCK_CRYSTAL_FLOOR`
* `BLOCK_CRYSTAL_ORBS`
* `BLOCK_CRYSTALLINE_BOULDER`
* `BLOCK_CRYSTALLINE_STONE`
* `BLOCK_CRYSTALLINE_STONE_WALL`
* `BLOCK_CRYSTALLINE_VENT`
* `BLOCK_CULTIVATOR`
* `BLOCK_CYANOGEN_SYNTHESIZER`
* `BLOCK_CYCLONE`
* `BLOCK_DACITE`
* `BLOCK_DACITE_BOULDER`
* `BLOCK_DACITE_WALL`
* `BLOCK_DAGGER_FACTORY`
* `BLOCK_DARK_METAL`
* `BLOCK_DARK_PANEL_1`
* `BLOCK_DARK_PANEL_2`
* `BLOCK_DARK_PANEL_3`
* `BLOCK_DARK_PANEL_4`
* `BLOCK_DARK_PANEL_5`
* `BLOCK_DARK_PANEL_6`
* `BLOCK_DARKSAND`
* `BLOCK_DARKSAND_TAINTED_WATER`
* `BLOCK_DARKSAND_WATER`
* `BLOCK_DART_MECH_PAD`
* `BLOCK_DART_SHIP_PAD`
* `BLOCK_DATA_PROCESSOR`
* `BLOCK_DECONSTRUCTOR`
* `BLOCK_DEEP_TAINTED_WATER`
* `BLOCK_DEEP_WATER`
* `BLOCK_DEEPWATER`
* `BLOCK_DELTA_MECH_PAD`
* `BLOCK_DENSE_RED_STONE`
* `BLOCK_DIFFERENTIAL_GENERATOR`
* `BLOCK_DIFFUSE`
* `BLOCK_DIODE`
* `BLOCK_DIRT`
* `BLOCK_DIRT_WALL`
* `BLOCK_DISASSEMBLER`
* `BLOCK_DISPERSE`
* `BLOCK_DISTRIBUTOR`
* `BLOCK_DOOR`
* `BLOCK_DOOR_LARGE`
* `BLOCK_DRAUG_FACTORY`
* `BLOCK_DUCT`
* `BLOCK_DUCT_BRIDGE`
* `BLOCK_DUCT_ROUTER`
* `BLOCK_DUCT_UNLOADER`
* `BLOCK_DUNE_WALL`
* `BLOCK_DUO`
* `BLOCK_ELECTRIC_HEATER`
* `BLOCK_ELECTROLYZER`
* `BLOCK_EMPTY`
* `BLOCK_ERUPTION_DRILL`
* `BLOCK_EXPONENTIAL_RECONSTRUCTOR`
* `BLOCK_FABRICATOR`
* `BLOCK_FERRIC_BOULDER`
* `BLOCK_FERRIC_CRATERS`
* `BLOCK_FERRIC_STONE`
* `BLOCK_FERRIC_STONE_WALL`
* `BLOCK_FLUX_REACTOR`
* `BLOCK_FORCE_PROJECTOR`
* `BLOCK_FORESHADOW`
* `BLOCK_FORTRESS_FACTORY`
* `BLOCK_FRACTURE`
* `BLOCK_FUSE`
* `BLOCK_GHOUL_FACTORY`
* `BLOCK_GLAIVE_SHIP_PAD`
* `BLOCK_GRAPHITE_PRESS`
* `BLOCK_GRAPHITIC_WALL`
* `BLOCK_GRASS`
* `BLOCK_GROUND_FACTORY`
* `BLOCK_HAIL`
* `BLOCK_HEAT_REACTOR`
* `BLOCK_HEAT_REDIRECTOR`
* `BLOCK_HEAT_ROUTER`
* `BLOCK_HEAT_SOURCE`
* `BLOCK_HOTROCK`
* `BLOCK_HYPER_PROCESSOR`
* `BLOCK_ICE`
* `BLOCK_ICE_SNOW`
* `BLOCK_ICE_WALL`
* `BLOCK_IGNAROCK`
* `BLOCK_ILLUMINATOR`
* `BLOCK_IMPACT_DRILL`
* `BLOCK_IMPACT_REACTOR`
* `BLOCK_IMPULSE_PUMP`
* `BLOCK_INCINERATOR`
* `BLOCK_INTERPLANETARY_ACCELERATOR`
* `BLOCK_INVERTED_SORTER`
* `BLOCK_ITEM_SOURCE`
* `BLOCK_ITEM_VOID`
* `BLOCK_JAVELIN_SHIP_PAD`
* `BLOCK_JUNCTION`
* `BLOCK_KILN`
* `BLOCK_LANCER`
* `BLOCK_LARGE_CONSTRUCTOR`
* `BLOCK_LARGE_LOGIC_DISPLAY`
* `BLOCK_LARGE_OVERDRIVE_PROJECTOR`
* `BLOCK_LARGE_PAYLOAD_MASS_DRIVER`
* `BLOCK_LARGE_PLASMA_BORE`
* `BLOCK_LARGE_SHIELD_PROJECTOR`
* `BLOCK_LASER_DRILL`
* `BLOCK_LAUNCH_PAD`
* `BLOCK_LAUNCH_PAD_LARGE`
* `BLOCK_LEGACY_COMMAND_CENTER`
* `BLOCK_LEGACY_MECH_PAD`
* `BLOCK_LEGACY_UNIT_FACTORY`
* `BLOCK_LEGACY_UNIT_FACTORY_AIR`
* `BLOCK_LEGACY_UNIT_FACTORY_GROUND`
* `BLOCK_LIQUID_CONTAINER`
* `BLOCK_LIQUID_JUNCTION`
* `BLOCK_LIQUID_ROUTER`
* `BLOCK_LIQUID_SOURCE`
* `BLOCK_LIQUID_TANK`
* `BLOCK_LIQUID_VOID`
* `BLOCK_LOGIC_DISPLAY`
* `BLOCK_LOGIC_PROCESSOR`
* `BLOCK_LUSTRE`
* `BLOCK_MAGMAROCK`
* `BLOCK_MALIGN`
* `BLOCK_MASS_CONVEYOR`
* `BLOCK_MASS_DRIVER`
* `BLOCK_MECH_ASSEMBLER`
* `BLOCK_MECH_FABRICATOR`
* `BLOCK_MECH_RECONSTRUCTOR`
* `BLOCK_MECH_REFABRICATOR`
* `BLOCK_MECHANICAL_DRILL`
* `BLOCK_MECHANICAL_PUMP`
* `BLOCK_MELTDOWN`
* `BLOCK_MELTER`
* `BLOCK_MEMORY_BANK`
* `BLOCK_MEMORY_CELL`
* `BLOCK_MEND_PROJECTOR`
* `BLOCK_MENDER`
* `BLOCK_MESSAGE`
* `BLOCK_METAL_FLOOR`
* `BLOCK_METAL_FLOOR_2`
* `BLOCK_METAL_FLOOR_3`
* `BLOCK_METAL_FLOOR_4`
* `BLOCK_METAL_FLOOR_5`
* `BLOCK_METAL_FLOOR_DAMAGED`
* `BLOCK_MICRO_PROCESSOR`
* `BLOCK_MOLTEN_SLAG`
* `BLOCK_MOSS`
* `BLOCK_MUD`
* `BLOCK_MULTI_PRESS`
* `BLOCK_MULTIPLICATIVE_RECONSTRUCTOR`
* `BLOCK_NAVAL_FACTORY`
* `BLOCK_NEOPLASIA_REACTOR`
* `BLOCK_NUCLEAR_WARHEAD`
* `BLOCK_OIL_EXTRACTOR`
* `BLOCK_OMEGA_MECH_PAD`
* `BLOCK_ORE_BERYLLIUM`
* `BLOCK_ORE_COAL`
* `BLOCK_ORE_COPPER`
* `BLOCK_ORE_CRYSTAL_THORIUM`
* `BLOCK_ORE_LEAD`
* `BLOCK_ORE_SCRAP`
* `BLOCK_ORE_THORIUM`
* `BLOCK_ORE_TITANIUM`
* `BLOCK_ORE_TUNGSTEN`
* `BLOCK_ORE_WALL_BERYLLIUM`
* `BLOCK_ORE_WALL_THORIUM`
* `BLOCK_ORE_WALL_TUNGSTEN`
* `BLOCK_OVERDRIVE_DOME`
* `BLOCK_OVERDRIVE_PROJECTOR`
* `BLOCK_OVERFLOW_DUCT`
* `BLOCK_OVERFLOW_GATE`
* `BLOCK_OXIDATION_CHAMBER`
* `BLOCK_OXIDIZER`
* `BLOCK_PARALLAX`
* `BLOCK_PAYLOAD_CONVEYOR`
* `BLOCK_PAYLOAD_INCINERATOR`
* `BLOCK_PAYLOAD_LOADER`
* `BLOCK_PAYLOAD_MASS_DRIVER`
* `BLOCK_PAYLOAD_ROUTER`
* `BLOCK_PAYLOAD_SOURCE`
* `BLOCK_PAYLOAD_UNLOADER`
* `BLOCK_PAYLOAD_VOID`
* `BLOCK_PEBBLES`
* `BLOCK_PHANTOM_FACTORY`
* `BLOCK_PHASE_CONDUIT`
* `BLOCK_PHASE_CONVEYOR`
* `BLOCK_PHASE_HEATER`
* `BLOCK_PHASE_SYNTHESIZER`
* `BLOCK_PHASE_WALL`
* `BLOCK_PHASE_WALL_LARGE`
* `BLOCK_PHASE_WEAVER`
* `BLOCK_PINE`
* `BLOCK_PLASMA_BORE`
* `BLOCK_PLASTANIUM_COMPRESSOR`
* `BLOCK_PLASTANIUM_CONVEYOR`
* `BLOCK_PLASTANIUM_WALL`
* `BLOCK_PLASTANIUM_WALL_LARGE`
* `BLOCK_PLATED_CONDUIT`
* `BLOCK_PNEUMATIC_DRILL`
* `BLOCK_POOLED_CRYOFLUID`
* `BLOCK_POWER_NODE`
* `BLOCK_POWER_NODE_LARGE`
* `BLOCK_POWER_SOURCE`
* `BLOCK_POWER_VOID`
* `BLOCK_PRESSURE_TURBINE`
* `BLOCK_PRIME_REFABRICATOR`
* `BLOCK_PULSE_CONDUIT`
* `BLOCK_PULVERIZER`
* `BLOCK_PUR_BUSH`
* `BLOCK_PYRATITE_MIXER`
* `BLOCK_PYROLYSIS_GENERATOR`
* `BLOCK_RADAR`
* `BLOCK_RAVAGE`
* `BLOCK_RED_DIAMOND_WALL`
* `BLOCK_RED_ICE`
* `BLOCK_RED_ICE_BOULDER`
* `BLOCK_RED_ICE_WALL`
* `BLOCK_RED_STONE`
* `BLOCK_RED_STONE_BOULDER`
* `BLOCK_RED_STONE_VENT`
* `BLOCK_RED_STONE_WALL`
* `BLOCK_REDMAT`
* `BLOCK_REDWEED`
* `BLOCK_REFABRICATOR`
* `BLOCK_REGEN_PROJECTOR`
* `BLOCK_REGOLITH`
* `BLOCK_REGOLITH_WALL`
* `BLOCK_REINFORCED_BRIDGE_CONDUIT`
* `BLOCK_REINFORCED_CONDUIT`
* `BLOCK_REINFORCED_CONTAINER`
* `BLOCK_REINFORCED_LIQUID_CONTAINER`
* `BLOCK_REINFORCED_LIQUID_JUNCTION`
* `BLOCK_REINFORCED_LIQUID_ROUTER`
* `BLOCK_REINFORCED_LIQUID_TANK`
* `BLOCK_REINFORCED_MESSAGE`
* `BLOCK_REINFORCED_PAYLOAD_CONVEYOR`
* `BLOCK_REINFORCED_PAYLOAD_ROUTER`
* `BLOCK_REINFORCED_PUMP`
* `BLOCK_REINFORCED_SURGE_WALL`
* `BLOCK_REINFORCED_SURGE_WALL_LARGE`
* `BLOCK_REINFORCED_VAULT`
* `BLOCK_REPAIR_POINT`
* `BLOCK_REPAIR_TURRET`
* `BLOCK_RESUPPLY_POINT`
* `BLOCK_REVENANT_FACTORY`
* `BLOCK_RHYOLITE`
* `BLOCK_RHYOLITE_BOULDER`
* `BLOCK_RHYOLITE_CRATER`
* `BLOCK_RHYOLITE_VENT`
* `BLOCK_RHYOLITE_WALL`
* `BLOCK_RIPPLE`
* `BLOCK_ROCK`
* `BLOCK_ROTARY_PUMP`
* `BLOCK_ROUGH_RHYOLITE`
* `BLOCK_ROUTER`
* `BLOCK_RTG_GENERATOR`
* `BLOCK_SALT`
* `BLOCK_SALT_WALL`
* `BLOCK_SALVO`
* `BLOCK_SAND`
* `BLOCK_SAND_BOULDER`
* `BLOCK_SAND_FLOOR`
* `BLOCK_SAND_WALL`
* `BLOCK_SAND_WATER`
* `BLOCK_SCATHE`
* `BLOCK_SCATTER`
* `BLOCK_SCORCH`
* `BLOCK_SCRAP_WALL`
* `BLOCK_SCRAP_WALL_GIGANTIC`
* `BLOCK_SCRAP_WALL_HUGE`
* `BLOCK_SCRAP_WALL_LARGE`
* `BLOCK_SEGMENT`
* `BLOCK_SEPARATOR`
* `BLOCK_SHALE`
* `BLOCK_SHALE_BOULDER`
* `BLOCK_SHALE_WALL`
* `BLOCK_SHALLOW_WATER`
* `BLOCK_SHIELD_PROJECTOR`
* `BLOCK_SHIELDED_WALL`
* `BLOCK_SHIP_ASSEMBLER`
* `BLOCK_SHIP_FABRICATOR`
* `BLOCK_SHIP_RECONSTRUCTOR`
* `BLOCK_SHIP_REFABRICATOR`
* `BLOCK_SHOCK_MINE`
* `BLOCK_SHOCKWAVE_TOWER`
* `BLOCK_SHRUBS`
* `BLOCK_SILICON_ARC_FURNACE`
* `BLOCK_SILICON_CRUCIBLE`
* `BLOCK_SILICON_SMELTER`
* `BLOCK_SLAG_CENTRIFUGE`
* `BLOCK_SLAG_HEATER`
* `BLOCK_SLAG_INCINERATOR`
* `BLOCK_SMALL_DECONSTRUCTOR`
* `BLOCK_SMITE`
* `BLOCK_SNOW`
* `BLOCK_SNOW_BOULDER`
* `BLOCK_SNOW_PINE`
* `BLOCK_SNOW_WALL`
* `BLOCK_SNOWROCK`
* `BLOCK_SOLAR_PANEL`
* `BLOCK_SOLAR_PANEL_LARGE`
* `BLOCK_SORTER`
* `BLOCK_SPACE`
* `BLOCK_SPAWN`
* `BLOCK_SPECTRE`
* `BLOCK_SPIRIT_FACTORY`
* `BLOCK_SPORE_CLUSTER`
* `BLOCK_SPORE_MOSS`
* `BLOCK_SPORE_PINE`
* `BLOCK_SPORE_PRESS`
* `BLOCK_SPORE_WALL`
* `BLOCK_STEAM_GENERATOR`
* `BLOCK_STEAM_VENT`
* `BLOCK_STONE`
* `BLOCK_STONE_WALL`
* `BLOCK_SUBLIMATE`
* `BLOCK_SURGE_CONVEYOR`
* `BLOCK_SURGE_CRUCIBLE`
* `BLOCK_SURGE_DUCT`
* `BLOCK_SURGE_ROUTER`
* `BLOCK_SURGE_SMELTER`
* `BLOCK_SURGE_TOWER`
* `BLOCK_SURGE_WALL`
* `BLOCK_SURGE_WALL_LARGE`
* `BLOCK_SWARMER`
* `BLOCK_SWITCH`
* `BLOCK_TAINTED_WATER`
* `BLOCK_TANK_ASSEMBLER`
* `BLOCK_TANK_FABRICATOR`
* `BLOCK_TANK_RECONSTRUCTOR`
* `BLOCK_TANK_REFABRICATOR`
* `BLOCK_TAR`
* `BLOCK_TAU_MECH_PAD`
* `BLOCK_TENDRILS`
* `BLOCK_TETRATIVE_RECONSTRUCTOR`
* `BLOCK_THERMAL_GENERATOR`
* `BLOCK_THORIUM_REACTOR`
* `BLOCK_THORIUM_WALL`
* `BLOCK_THORIUM_WALL_LARGE`
* `BLOCK_THRUSTER`
* `BLOCK_TITAN`
* `BLOCK_TITAN_FACTORY`
* `BLOCK_TITANIUM_CONVEYOR`
* `BLOCK_TITANIUM_WALL`
* `BLOCK_TITANIUM_WALL_LARGE`
* `BLOCK_TRIDENT_SHIP_PAD`
* `BLOCK_TSUNAMI`
* `BLOCK_TUNGSTEN_WALL`
* `BLOCK_TUNGSTEN_WALL_LARGE`
* `BLOCK_TURBINE_CONDENSER`
* `BLOCK_UNDERFLOW_DUCT`
* `BLOCK_UNDERFLOW_GATE`
* `BLOCK_UNIT_CARGO_LOADER`
* `BLOCK_UNIT_CARGO_UNLOAD_POINT`
* `BLOCK_UNIT_REPAIR_TOWER`
* `BLOCK_UNLOADER`
* `BLOCK_VAULT`
* `BLOCK_VENT_CONDENSER`
* `BLOCK_VIBRANT_CRYSTAL_CLUSTER`
* `BLOCK_WALL_ORE_BERYLLIUM`
* `BLOCK_WALL_ORE_TUNGSTEN`
* `BLOCK_WARHEAD_ASSEMBLER`
* `BLOCK_WATER_EXTRACTOR`
* `BLOCK_WAVE`
* `BLOCK_WHITE_TREE`
* `BLOCK_WHITE_TREE_DEAD`
* `BLOCK_WORLD_CELL`
* `BLOCK_WORLD_MESSAGE`
* `BLOCK_WORLD_PROCESSOR`
* `BLOCK_WRAITH_FACTORY`
* `BLOCK_YELLOW_STONE`
* `BLOCK_YELLOW_STONE_BOULDER`
* `BLOCK_YELLOW_STONE_PLATES`
* `BLOCK_YELLOW_STONE_VENT`
* `BLOCK_YELLOW_STONE_WALL`
* `BLOCK_YELLOWCORAL`

</details>

<details><summary>Show a full list of Mindustry item/liquid icons.</summary>

* `ITEM_BERYLLIUM`
* `ITEM_BLAST_COMPOUND`
* `ITEM_CARBIDE`
* `ITEM_COAL`
* `ITEM_COPPER`
* `ITEM_DORMANT_CYST`
* `ITEM_FISSILE_MATTER`
* `ITEM_GRAPHITE`
* `ITEM_LEAD`
* `ITEM_METAGLASS`
* `ITEM_OXIDE`
* `ITEM_PHASE_FABRIC`
* `ITEM_PLASTANIUM`
* `ITEM_PYRATITE`
* `ITEM_SAND`
* `ITEM_SCRAP`
* `ITEM_SILICON`
* `ITEM_SPORE_POD`
* `ITEM_SURGE_ALLOY`
* `ITEM_THORIUM`
* `ITEM_TITANIUM`
* `ITEM_TUNGSTEN`
* `LIQUID_ARKYCITE`
* `LIQUID_CRYOFLUID`
* `LIQUID_CYANOGEN`
* `LIQUID_GALLIUM`
* `LIQUID_HYDROGEN`
* `LIQUID_NEOPLASM`
* `LIQUID_NITROGEN`
* `LIQUID_OIL`
* `LIQUID_OXYGEN`
* `LIQUID_OZONE`
* `LIQUID_SLAG`
* `LIQUID_WATER`

</details>

<details><summary>Show a full list of Mindustry status and team icons.</summary>

* `STATUS_BLASTED`
* `STATUS_BOSS`
* `STATUS_BURNING`
* `STATUS_CORRODED`
* `STATUS_DISARMED`
* `STATUS_ELECTRIFIED`
* `STATUS_FREEZING`
* `STATUS_INVINCIBLE`
* `STATUS_MELTING`
* `STATUS_MUDDY`
* `STATUS_NONE`
* `STATUS_OVERCLOCK`
* `STATUS_OVERDRIVE`
* `STATUS_SAPPED`
* `STATUS_SHIELDED`
* `STATUS_SHOCKED`
* `STATUS_SLOW`
* `STATUS_SPORE_SLOWED`
* `STATUS_TARRED`
* `STATUS_UNMOVING`
* `STATUS_WET`
* `TEAM_CRUX`
* `TEAM_DERELICT`
* `TEAM_MALIS`
* `TEAM_SHARDED`

</details>

<details><summary>Show a full list of Mindustry unit icons.</summary>

* `UNIT_AEGIRES`
* `UNIT_ALPHA`
* `UNIT_ANTHICUS`
* `UNIT_ANTHICUS_MISSILE`
* `UNIT_ANTUMBRA`
* `UNIT_ARKYID`
* `UNIT_ASSEMBLY_DRONE`
* `UNIT_ATRAX`
* `UNIT_AVERT`
* `UNIT_BETA`
* `UNIT_BLOCK`
* `UNIT_BRYDE`
* `UNIT_CATACLYST`
* `UNIT_CLEROI`
* `UNIT_COLLARIS`
* `UNIT_CONQUER`
* `UNIT_CORVUS`
* `UNIT_CRAWLER`
* `UNIT_CYERCE`
* `UNIT_DAGGER`
* `UNIT_DISRUPT`
* `UNIT_DISRUPT_MISSILE`
* `UNIT_ECLIPSE`
* `UNIT_EFFECT_DRONE`
* `UNIT_ELUDE`
* `UNIT_EMANATE`
* `UNIT_EVOKE`
* `UNIT_FLARE`
* `UNIT_FORTRESS`
* `UNIT_GAMMA`
* `UNIT_HORIZON`
* `UNIT_INCITE`
* `UNIT_LATUM`
* `UNIT_LOCUS`
* `UNIT_MACE`
* `UNIT_MANIFOLD`
* `UNIT_MEGA`
* `UNIT_MERUI`
* `UNIT_MINKE`
* `UNIT_MONO`
* `UNIT_NAVANAX`
* `UNIT_NOVA`
* `UNIT_OBVIATE`
* `UNIT_OCT`
* `UNIT_OMURA`
* `UNIT_OSC`
* `UNIT_OXYNOE`
* `UNIT_POLY`
* `UNIT_PRECEPT`
* `UNIT_PULSAR`
* `UNIT_QUAD`
* `UNIT_QUASAR`
* `UNIT_QUELL`
* `UNIT_QUELL_MISSILE`
* `UNIT_REIGN`
* `UNIT_RENALE`
* `UNIT_RETUSA`
* `UNIT_RISSE`
* `UNIT_RISSO`
* `UNIT_SCATHE_MISSILE`
* `UNIT_SCEPTER`
* `UNIT_SEI`
* `UNIT_SPIROCT`
* `UNIT_STELL`
* `UNIT_TECTA`
* `UNIT_TOXOPID`
* `UNIT_TURRET_UNIT_BUILD_TOWER`
* `UNIT_VANQUISH`
* `UNIT_VELA`
* `UNIT_VESTIGE`
* `UNIT_ZENITH`

</details>

<details><summary>Show a full list of Mindustry unclassified icons.</summary>

* `ALPHAAAA`
* `CRATER`

</details>

### Compiler-defined constants

Mindcode provides the following built-in constants which reflect the compiler options used for the compilation: 

* `__MINDUSTRY_VERSION__`: String. The Mindustry version specified by the `target` option (e.g. `v146`).
* `__TARGET_MAJOR__`: Integer. Major target version number (i.e., `6`, `7` or `8`).
* `__TARGET_MINOR__`: Integer. Minor target version number (e.g., `0` or `1`).
* `__PROCESSOR_EDITION__`: String. `S` for standard and `W` for world edition.

Through these constants, it is possible to support several different targets in the same code, for example:

```Mindcode
void printExactHex(n)
    for var i in 0 ... 16 descending do
        var digit = (n >> (4 * i)) & 0xF;
        if __TARGET_MAJOR__ < 8 then
            case digit
                when 0 .. 9 then print(digit);
                when 10 then print("A");
                when 11 then print("B");
                when 12 then print("C");
                when 13 then print("D");
                when 14 then print("E");
                when 15 then print("F");
            end;
        else
            printchar('0' + digit + (digit >= 10) * ('A' - 0xA - '0'));
        end;
    end;
end;
```

## Linked variables

> [!NOTE]
> Blocks linked to Mindustry processors are called _linked blocks_. Mindcode variables representing linked blocks are called _linked variables_. In relaxed syntax mode, these terms can be used interchangeably, as using a linked block name in the source code creates a linked variable with the same name. In strict syntax, the distinction between linked blocks and linked variables is important: linked variables are created for specific linked blocks through an explicit declaration. 

Linked variables represent blocks directly linked to the processor. A guard code may be created for linked variables. Linked variables are declared using the [`linked` or `guarded` storage modifiers](#storage-modifiers).

When an initial value is not assigned to a linked variable, the variable identifier is the name of the linked block: `linked cell1;` declares a `cell1` variable representing the `cell1` block linked to the processor. When an initial value is assigned to the variable, the assigned value must be a name of the linked block, while the variable identifier will be used to represent the variable in the program: `linked up = switch1, down = switch2;`. This is useful to assign meaningful names to linked blocks.

A warning is generated if the name of the linked block used in the linked variable declaration is not recognized, however, the linked variable is nevertheless created. This way, it is possible to use linked blocks unrecognized by Mindcode, for example, blocks provided by a mod. (The warning can be suppressed using the [`#declare` directive](SYNTAX-EXTENSIONS.markdown#declaring-new-linked-block-names)). 

When declaring linked variables, these additional modifiers can be used:

* [`guarded`](#guarded-modifier)

Example:

```Mindcode
guarded on = switch1, memory = cell1;   // These blocks are required
linked message1;                        // message1 is optional

while on.@enabled do
    memory[0]++;
    print($"Current value: ${memory[0]}");
    printflush(message1);
end;
```

> [!IMPORTANT]
> Linked variables (both implicit and explicit ones) reflect the changes made to linked blocks during the execution of the program. For example, if `switch1` is linked to the processor, but then is destroyed, the value of `switch1` turns to `null`. If the switch is then rebuilt by the user and linked to the processor under the same name, the linked variable will automatically reconnect to the new instance of the switch when it becomes available.
> 
> It is important to consider that in some cases the new block linked to the processor under the same name as a previously linked and subsequently destroyed block may be of a different type (e.g., replacing sorter `sorter1` with inverted sorter will link the inverted sorter also under the name `sorter1`).  
> 
> When a linked block is stored in a regular variable or program parameter, the variable will always refer to the same instance of the block that was assigned to it. When such a block gets destroyed, it still appears to be present (doesn't become `null`), and can only be recognized as missing by querying the `@dead` property.

## External variables

External variables and arrays represent cells in a memory block. They are declared using the [`external` storage modifier](#storage-modifiers).

The `external` modifier can optionally specify storage for the declared variables or arrays. The storage specification consist of the name of the memory block (e.g., `cell1`, `bank2`, or a variable), and optionally an index or range in square brackets. When no index or range is specified, index `0` is assumed. When a storage clause is specified, all variables declared after the `external` keyword are allocated in given memory block, starting at the given index/range. If more space than provided by a given range is required for variables, an error is reported.

Examples:

```Mindcode
allocate heap in cell1[32 ... 64];
external var foo;
external(cell1) bar;
external(cell1[10 ... 20]) var a, b, c[8];   
```

When no storage clause is specified, the declared variables are placed in the heap, which needs to be allocated first. The cells are assigned to variables in the order in which the variables appear in the source code. Explicit declaration of all external variables is the easiest way to specify fixed allocation order of external variables on the heap.

Declared external variables can optionally use the `$` prefix, which, if used, is part of the variable name: `$ext` is different from `ext`.

When an initial value is provided, it is assigned to the variable and written to the memory cell at the moment of the declaration.

When declaring external variables, these additional modifiers can be used:

* [`cached`](#cached-modifier)
* [`noinit`](#noinit-modifier)

> [!IMPORTANT]
> Since external variables are stored in [external memory](#external-memory), they only support numeric values. At this moment, Mindcode is incapable of detecting situations when unsupported values are being written to external memory.

> [!NOTE]
> External arrays stored in memory cells or memory banks starting at an index different from `0` incur small performance penalty when accessing the array elements, since the offset needs to be added to the index to access the correct memory cell.

Example of code using external variables:

```Mindcode
#set remarks = comments;
allocate heap in cell4[32 ... 64];

// Implicitly created variables
$dx = 1; 
$dy = 1;

// Explicity declared varaibles
cached external a, b = 10;        
noinit cached external(bank1) c;
external(bank1[256 ... 512]) d[256];
c = a + b;
print(c);
/// Here's the additional instruction for computing the array offset 
d[c] = 10; 
```

The above will compile to:

```mlog
write 1 cell4 32
write 1 cell4 33
read .a cell4 34
write 10 cell4 35
op add .c .a 10
write .c bank1 0
print .c
# Here's the additional instruction for computing the array offset 
op add *tmp260 .c 256
write 10 bank1 *tmp260
```

## Exported variables

Target 8 or higher is required to use exported variables. 

Exported variables are created in the current processor and are available through remote access in code which imports the module. Exported variables/arrays may or may not be initialized. When some exported variables or array elements are unused within the module (and the corresponding mlog variables would therefore not be created when parsing the code by the logic processor), the missing variables are explicitly created using `draw triangle` instruction(s), so that they may be accessed remotely. These instructions are placed at the end of the generated code and are never executed. See the [`require remote` statement](REMOTE-CALLS.markdown#main-processor-code).

Exported variables and arrays are declared using the [`export` storage modifier](#storage-modifiers). When declaring exported variables, these additional modifiers can be used:

* [`mlog`](#mlog-modifier)
* [`noinit`](#noinit-modifier)
* [`volatile`](#volatile-modifier)

All exported variables are implicitly `volatile`, so the `volatile` modifier is superfluous, but can be used for better clarity. The only difference between `export` and `volatile` is that `volatile` variables aren't imported to modules which require the current module. 

> [!NOTE]
> Element names of an exported array are part of the module contract, and therefore cannot be changed by the optimizer. To use a lookup implementation of an exported array, the lookup type must be specified explicitly using the `mlog` modifier.

Example:

```Mindcode
#set target = 8;

// Exported variables, a unitialized, b initialized
export a, b = 10;

// Exported arrays: initialized and uninitialized
export array2[5] = (1, 2, 3, 4, 5);
export mlog(:liquid) array1[10];
```

## Remote variables

Target 8 or higher is required to use remote variables.

External variables and arrays are stored in another processor. They are declared using the [`remote` storage modifier](#storage-modifiers). The `remote` modifier specifies the processor the variable is stored in, and it may be specified as a linked variable, linked block or a regular variable:

```Mindcode
#set target = 8;
linked processor1, processor2;
var proc;

remote(processor1) a;
remote(processor2) b;
remote(proc) c[10];

proc = a > b ? processor1 : processor2;
print(a, b, c[0]);
```

When declaring remote variables, these additional modifiers can be used:

* [`cached`](#cached-modifier)
* [`mlog`](#mlog-modifier)
* [`noinit`](#noinit-modifier)

> [!NOTE]
> Unlike [remotely accessed exported variables](REMOTE-CALLS.markdown#exported-variables), remote variables are specified by their name only in Mindcode, not as a property on the processor they are stored in.

Using the `mlog` modifier allows specifying the mlog name of the remote variable. This is useful in two cases:

* The code in the remote processor wasn't created by Mindcode. In this case, the naming convention used for variables is probably different from the one used by Mindcode, and you need to use the `mlog` modifier to specify the correct name. Note that unless the remote code supports writing to the variable remotely, it might stop working correctly when changing the value of the variable.
* The remote variable was itself created by Mindcode using the `mlog` modifier. The same mlog name must be specified in the remote variable declaration.    

> [!IMPORTANT]
> Mindcode variables to be accessed remotely from different processor must be declared as `volatile` variables. 

## Program parameters

Program parameters are processor variables that have a value assigned to them at declaration which, after the initial assignment, remains constant for the entire execution of the program. Assignments to program parameters, apart from the initialization in the declaration, aren't allowed.

The initial value assigned to the parameter is compiled to a single `set` instruction. The assigned value may be changed in the compiled code—there is always exactly one `set` instruction assigning a value to the program parameter—and such a change has the same effect on the program as if the code was actually compiled with the new parameter value. In other words, program parameters allow users of your program to change some basic parameters (such as unit types, linked blocks, or various numeric limits) without having to recompile the entire program; even without having access to the original source code and/or Mindcode compiler.

> [!IMPORTANT]
> Correct execution of the program is only guaranteed if the value assigned to the program parameter in the compiled code is constant. When modifying the compiled code to assign a non-constant value (for example `@links` or `@time`) to a program parameter, the behavior of the resulting code is generally undefined.

Names of the mlog variables representing the program parameters are always the same as the names used in the source code. For this reason, Mindcode disallows using potential [linked block names](#linked-blocks) as identifiers for parameters (i.e., you can't create a parameter named `switch1`). 

Program parameters cannot be created implicitly and always need to be declared using this syntax:

```
param <parameter> = <initial value>;
```

Program parameters must be declared in global scope and are therefore always global. The following values can be assigned to program parameters:

* literals of any kind except formattable string literals and mlog keywords,
* other constants and constant expressions,
* linked variables,
* linked blocks,
* built-in variables, except built-in variables known not to be constant.

If a numeric value is assigned to a parameter, and it isn't possible to [encode the value into an mlog literal](SYNTAX.markdown#specifics-of-numeric-literals-in-mindustry-logic), a compilation error occurs.

> [!TIP]
> Even in strict syntax mode, linked blocks can be assigned to a program parameter without prior declaration.

> [!NOTE]
> Using a guarded linked variable as a value for a program parameter is discouraged. The purpose of the program parameter is to allow making changes to the compiled code, however, a change just to the program parameter's value would not mean the guard code would protect the new block.

Example (the optimization is turned off to prevent removing unused parameters):

```Mindcode
#set optimization = none;
param unitType = @flare;
param target = vault1;
param maxUnits = 10;
param diff = sin(45); 
param userName = "Pete"; 
```

This code, when compiled, produces the following instructions:

```mlog
set unitType @flare
set target vault1
set maxUnits 10
set diff 0.7071067811865475
set userName "Pete"
end
```

 As we can see, values assigned through these instructions can be easily changed in the compiled code.

> [!TIP]
> It is a good idea to _sanitize_ the values of program parameters to make sure that changes to the parameters in the compiled code do not break the program.

For example, let's say that a parameter is created to specify the percentage of container capacity usage at which some action should happen. Constraining the parameter to a range of `0 .. 100` ensures parameter values outside this range do not break the code:

```Mindcode
param CUTOFF_PCT = 50;
var cutoffPct = max(min(CUTOFF_PCT, 100), 0);
print(cutoffPct);
```

produces the following code:

```mlog
set CUTOFF_PCT 50
op min *tmp0 CUTOFF_PCT 100
op max .cutoffPct *tmp0 0
print .cutoffPct
```

An additional benefit is that, upon closer inspection, the expected range of the parameter value becomes noticeable from the compiled code itself. 

## Function parameters

Function parameters are valid within the scope of the entire function and are always explicit, as they are declared as part of the function declaration. 

# Dynamic linking to blocks

Mindustry Logic provides functions which allow you to access linked blocks, or even blocks not linked to the processor, dynamically through the [`getlink()`](FUNCTIONS-80.markdown#instruction-get-link), [`ulocate()`](FUNCTIONS-80.markdown#instruction-unit-locate) and other functions.

These functions return a reference to a block. This reference can be stored in a regular variable, which can then be used to perform operations on the block in the same way as linked variables:

```Mindcode
var message = findMessage();

begin
    println("Hello, world!");
    printflush(message);        // Using dynamically obtained block
end;

def findMessage()
    while true do
        for var i in 0 ... @links do
            var block = getlink(i);
            if block.@type == @message then return block; end;
        end;
    end;
end;
```

# Mlog variable name generation

Implicit and explicit variable names used in source code are translated to mlog using these rules:

* linked block and parameters: no modification to the variable name
* global variables: `.variable` (a `.` prefix)
* main variables: `:variable` (a `:` prefix)
* local variables: `:fnX:variable` (a `:` prefix both to the function counter and a variable name) for short prefixes, or `:function.X:variable` (using actual function name and a numeric counter) for long prefixes.
* array elements: `.array*index` (index corresponds to the numeric index of the element within the array)
* temporary variables: `*tmpX` (`*tmp` + counter)
* function return address/value: `:fnX*retaddr`/`:fnX*retval`
* stack pointer: `*sp`
* remote function parameters: `:function:parameter` (using actual function and parameter name)
* remote function internal variables (e.g., address or finished flag): `:function*variable`

In short, global variables start with `.`, function names and local variables start with `:` and compiler-generated variables start with or contain `*`.

If the same main or local variable is declared multiple times in the same function (in different, non-overlapping code blocks), they actually represent different variables within a program. In this case, a unique numeric suffix is appended to variables created in the second and further declarations, separated by `.` (a dot):

```Mindcode
#set optimization = none;
begin var i = 1; end;
begin var i = 2; end;
```

compiles into

```mlog
set :i 1
set :i.1 2
end
```

---

[Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Expressions &#xBB;](SYNTAX-2-EXPRESSIONS.markdown)
