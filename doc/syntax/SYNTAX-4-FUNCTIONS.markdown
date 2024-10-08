# Functions

Mindcode allows you to call existing functions or create new ones.
Functions are called by specifying a function name and placing parameters in parentheses.
The parenthesis must be used even if the function has no parameters.

```
foo(x);    // Calling function with a single argument
bar();     // Calling function with no arguments
```

# Mindustry Logic functions

Interactions with the Mindustry world take place through functions mapped to Mindustry Logic instructions.

Mindcode currently supports targeting Mindustry versions 6 and 7. Web application always targets version 7 processors,
while command-line version of the compiler allows to select version 6 for better backwards compatibility.
The differences are minuscule, though, most of the code generated for version 7 will run on version 6 as well.

A specific `7A` target was added to Mindcode, where the `getBlock` and `ulocate` functions return the building that 
was found at given coordinates or located using the criteria. This update makes the most occurring use case, where the 
located building is the only used output of the function, a natural way to use the function.

At this point, `7` is still the default target for both command line tool and web application. `7A` will become the 
default in the future, or in higher Mindcode versions.  

All supported functions and their respective Mindustry instruction counterparts can be found in the function reference.
Please note that the reference serves just to document all existing functions and the way they are compiled to Mindustry Logic,
but it does not aim to describe the behavior of the functions/instructions.

* [Function reference for Mindustry Logic 6](FUNCTIONS_V6.markdown)
* [Function reference for Mindustry Logic 7](FUNCTIONS_V7.markdown)
* [Function reference for Mindustry Logic 7A](FUNCTIONS_V7A.markdown)
* [Function reference for Mindustry Logic 8A](FUNCTIONS_V8A.markdown)

## Instruction parameters

There are several types of parameters a function can have:
* _Input parameters_: these parameters will accept any expression, including variables, as arguments.
* _Output parameters_: one of output parameters of the instruction is always mapped to the return value of the function,
  e.g. `block = getlink(linkNum)` translates to instruction `getlink block linkNum`.
  If the instruction provides more than one output parameters, the remaining ones become parameters of the function.
  Output parameters are typically optional, if you aren't interested in a particular value returned by the instruction,
  you can omit the argument (only arguments at the end of the argument list can be omitted.)
  When not omitted, the argument passed in should be a variable.
* _Enumerated parameters_: these parameters will only accept one of predefined constants as an argument.
  For example the `uradar` functions requires one of the following values for each of its first three arguments:
  `any`, `enemy`, `ally`, `player`, `attacker`, `flying`, `boss`, `ground`. The constants are passed as-is, without
  any escaping or double quotes. It is not possible to store the value in a variable and pass the variable instead.
  Passing a value different to one of the supported values results in compilation error.

## Instruction mapping rules

Generally, functions are mapped to instructions using these rules:
* If the instruction has just one form (such as `getlink`), the function name corresponds to the instruction name. 
  All instruction names are lowercase.
* If the instruction takes several forms depending on the exact operation the instruction performs (such as `draw` or `ucontrol`),
  the function name corresponds to the operation. In this case, the function name can be camelCase (such as `itemTake`).

The disparity between those two kinds of functions is a consequence of keeping Mindcode nomenclature as close to Mindustry Logic as possible.

There are a few issues with these rules:
* Both `ucontrol stop` and `stop` would map to the `stop()` function. The `stop` instruction is instead mapped to the `stopProcessor()` function.
* `ucontrol getBlock` is similar to the new `getblock` World Processor instruction. The resulting functions only differ in case.
* The `status` World Processor instruction distinguishes clearing and applying the status by an enumerated parameter 
  (`true` or `false`), which is not very readable. Mindcode instead creates separate functions,
  `applyStatus()` and `clearStatus()`.

## Methods

Some instructions perform an operation on an object (a linked block), which is passed as an argument to one
of instruction parameters. In these cases, the instruction can be mapped to a method called on the given block,
e.g. `block.shoot(x, y, doShoot)` translates to `control shoot block x y doShoot 0`.

In some cases, the instruction can be invoked as a function or as a method (`printflush(message1)` or `message1.printflush()`).
All existing mappings are shown in the function reference above.

## Alternative `control` syntax

There is a special case for `control` instruction setting a single value on a linked block, for whose Mindcode accepts
the following syntax:
* `block.enabled = boolean`, which is equivalent to `block.enabled(boolean)`
* `block.config = value`, which is equivalent to `block.config(value)`

The `block` in the examples can be a variable or a linked block object.

Currently, the property access syntax cannot be used with the new [`setprop`](FUNCTIONS_V7.markdown#instruction-setprop)
instruction. Looking for ways to support this syntax in the future.

## Alternative `sensor` syntax

The `sensor` method accepts an expression, not just constant property name, as an argument:

```
amount = vault1.sensor(use_titanium ? @titanium : @copper);
```

If the property you're trying to obtain is hard-coded, you can again use an alternate syntax:
`amount = storage.thorium` instead of the longer equivalent `amount = storage.sensor(@thorium)`.
You can use this for any property, not just item types, such as `@unit.dead` instead of `@unit.sensor(@dead)`.

Again, the `vault1` or `storage` in the examples can be a variable or a linked block object.

> [!IMPORTANT]
> In the case of property access, the `@` character at the beginning of the property name is omitted - 
> `storage.thorium` is the right syntax, `storage.@thorium` is wrong.

## The `sync()` function

A `sync` instruction (available in Mindustry Logic since version 7.0 build 146) is mapped to a `sync()` function. 
The function has one parameter - a variable to be synchronized across the network (namely, from the server to all 
clients). A [global variable](SYNTAX-1-VARIABLES.markdown#global-variables) must be passed as an argument to this 
function, otherwise a compilation error occurs. The reason is that global variables are more restrained in 
optimizations and therefore their dataflow is less altered. If local variables were used, they might not contain the 
expected value, as some (or all) assignments to them can be eliminated by the Data Flow Optimization.

This constraint makes sense semantically as well: a scope of a global variable is the entire program. When a 
variable is synced, its scope becomes even broader and is shared between multiple processors; using a local variable 
in this place doesn't therefore make sense.

> [!IMPORTANT]
> By using a variable in a sync function, the variable effectively becomes volatile - its value can change 
> externally, and it needs to be read from that variable on every access. Mindustry at the moment doesn't have a 
> mechanism for correctly handling volatile variables, although such mechanism is planned.
 
Currently, a value of a  global variable might not be re-read on every access, for example:

```
sync(A);
before = A;
wait(1000);
after = A;
print(after - before);
```

This snippet of code is meant to compute a difference in the value of `A` caused by external synchronization. 
However, due to the Data Flow Optimization, the resulting code is:

```
op rand A 10 0
sync A
wait 1000
op sub __tmp1 A A
print __tmp1
end
```

Storing the initial value of `A` into the `before` variable is eliminated, as the optimizer doesn't know the value 
of `A` might have changed in the meantime.

At this moment, a possible workaround is to encapsulate reading of `A` into a function declared `noinline`, to 
prevent its inlining:

```
noinline def readA()
    A;
end;

A = rand(10);
sync(A);
before = readA();
wait(1000);
after = readA();
print(after - before);
```

The resulting code now is 

```
op rand A 10 0
sync A
set __fn0retaddr 4
jump 11 always 0 0
set first __fn0retval
wait 1000
set __fn0retaddr 8
jump 11 always 0 0
op sub __tmp3 __fn0retval first
print __tmp3
end
set __fn0retval A
set @counter __fn0retaddr
```

This is a viable workaround if you run into the described problem, until a better solution is implemented. 

# Built-in functions

Mindcode defines a few built-in functions that enhance plain Mindustry Logic.

## Text output

There are several functions that can be used to send output to the text buffer, from where it can be transferred into a message block using `printflush()` or, from Mindustry Logic 8 onwards, used to draw text using the `drawPrint()` function.

### Plain text output

Printing text, variables and expressions just as they are is possible using the `print` and `println` functions. These functions take any number of arguments and generate a `print` instruction for each of them. The `println` function outputs one additional `print` instruction containing just the `\n` (newline) character, creating a new line in the text to be printed.

The value returned by functions performing plain text output corresponds to the value of the last argument in the argument list. If there were no arguments in the list (e.g. `println()`), the returned value is `null`.  

### Compile-time formatting

If the first argument passed to the `print()` or `println()` function is a formattable string literal, the functions perform compile-time formatting. All `$` characters inside the formattable string literal are replaced with variables and expressions like this:

* If the `$` character is followed by a variable name, the variable is printed (external variables, e.g. `$X`, aren't supported).
* If the `$` character is not followed by a variable name, next argument from the argument list is printed.
* To separate the variable name from the rest of the text, enclose the name in curly braces: `${name}`. This is not necessary if the next character cannot be part of a variable name. `${}` can be used to place an argument from the argument list immediately followed by some text.
* To print the `$` character, use `\$`.

| `print()` call                                                        | is the same as                                                         |
|-----------------------------------------------------------------------|------------------------------------------------------------------------|
| `print($"$@unit at position $x, $y")`                                 | `print(@unit, " at position ", x, ", ", y)`                            |
| `print($"Time: $ sec, increment: $", floor(@second), current - last)` | `print("Time: ", floor(@second), " sec, increment: ", current - last)` |
| `print($"Coordinates: ${real}+${imag}i")`                             | `print("Coordinates: ", real, "+", imag, "i")`                         |
| `print($"Price: \$$price")`                                           | `print("Price: $", price)`                                             |
| `print($"Speed: ${}m/s", distance / time)`                            | `print("Speed: ", distance / time, "m/s")`                             |

The `println()` function works the same, but outputs an additional newline character to the text buffer.  

The function was inspired by string interpolation in Ruby, but there are important differences. Firstly, the first argument to the printing function must be a formattable string literal or a constant string expression, as the formatting takes place at compile time (Mindustry Logic doesn't provide means to do it at runtime). Secondly, only variables are allowed in curly braces, not expressions:

```
x = 3;
y = 4;
println("Position: $, $", x, y);      // No formatting - the first parameter isn't a formattable string literal
                                      // Will output "Position: $, $34\n"
println($"Position: $, $", x, y);     // Will be formatted and will ultimately output "Position: 3, 4\n"

println($"Distance: ${len(x, y)}");   // Not allowed - expressions within the string aren't supported
                                      // Causes compilation error
println($"Distance: $", len(x, y));   // Allowed - the expression for $ is taken from the list
                                      // Will output "Distance: 5\n"
                                      
const FORMAT = $"Position: $, $"; 
println(FORMAT, x, y);                // Allowed - the formattable string can be assigned to a constant                                      
```

Compile-time formatting is supported by the `print()` and `println()` functions. The value returned by these functions performing compile-time formatting is always `null`.

### Run-time formatting

Since version 8, Mindustry Logic supports run-time text formatting. This is done by putting placeholders `{0}` to `{9}` into the text buffer, which can be later replaced with an actual value using the `format` instruction. As the placeholders can be put into the text buffer using `print` instruction, it is possible to store and pass around the formatting string in a variable and thus separate the formatting template from actual values being output.

Run-time formatting can be accomplished by the `printf()` function. This function takes several arguments (typically at least two). The first argument is the format string, which may be a text constant, or a string variable, and is passed into a `print` instruction. All the remaining arguments are passed into `format` instructions. Therefore, `printf(fmt, a, b, c)` gets translated to

```
print fmt
format a
format b
format c
```  

The `format` instruction searches the text buffer, looking for a placeholder with the lowest number. The first occurrence of such placeholder is then replaced by the value supplied to the `format`. This means that each format only replaces one placeholder: `printf("{0}{0}{1}", "A", "B")` followed by `printflush` therefore outputs `AB{1}` and not `AAB`. On the other hand, `printf("A{0}B", "1{0}2", "X")` outputs `A1X2B` - the placeholder inserted into the text buffer by the `format` instruction is used by the subsequent `format`. That opens up a possibility for building outputs incrementally.

Apart from the `printf()`, Mindcode supports a new `format()` function, which just outputs the `format` instruction for each of its arguments. The `printf(fmt, value1, value2, ...)` function call is therefore just a shorthand for `print(fmt); format(value1, value2, ...);`.

> [!TIP]
> Since the `format` instruction allows to decouple the formatting template from the values being applied to the template, Mindcode is unable to apply the print merging optimizations to the `format` instruction, even when their arguments get resolved to constant values during optimizations. Use compile-time formatting instead of run-time formatting whenever possible for more efficient code.      

> [!TIP]
> Print merging optimizations can utilize the `format` instruction for more effective optimizations. To make sure the optimizations do not interfere with the `format` instructions placed into the code by the user, the optimizer only uses the `{0}` placeholder for its own formatting. This leaves the remaining nine placeholders, `{1}` to `{9}`, for use in the user code. If you do use the `{0}` placeholder in your own code, the more efficient optimization using the `format` instruction will be disabled.

> [!WARNING]
> The `printf()` function has two forms depending on the language target:
> - For ML7A and lower, the `printf()` function performs compile-time formatting, and can take both formattable string literal and a standard string literal as the format argument.
> - For ML8A and higher, the `printf()` function performs the run-time formatting described in this chapter.
> 
> When migrating from Mindustry Logic 7 to Mindustry Logic 8, replace all occurrences of `printf("` with `print($"` in your codebase.

## Remarks

The `remark()` function has the same syntax as the `print()` function and supports both the plain text output and compile-time formatting modes just lke the `print()` function does. It also produces `print` instructions, but the way these instructions are generated into the code can be controlled using the [`remarks` option](SYNTAX-5-OTHER.markdown#option-remarks):

* `none`: remarks are suppressed in the compiled code - they do not appear there at all.
* `passive`: remarks are included in the compiled code, but a jump is generated in front each block of continuous remarks, so that the print statement themselves aren't executed. This is the default value.
* `active`: remarks are included in the compiled code and are executed, producing actual output to the text buffer.

Example:

```
remark("Configurable options:");
MIN = 10;
MAX = 100;

remark("Don't modify anything below this line.");
for i in MIN .. MAX
    print("Here is some actual code");
end
```

produces

```
jump 2 always 0 0
print "Configurable options:"
set MIN 10
set MAX 100
jump 6 always 0 0
print "Don't modify anything below this line."
set i MIN
jump 0 greaterThan MIN MAX
print "Here is some actual code"
op add i i 1
jump 8 lessThanEq i MAX
end
```

Remarks may also allow for better orientation in compiled code, especially as expressions inside remarks will get fully evaluated when possible:

```
for i in 1 .. 3
    remark("Iteration $i:");
    remark("Setting cell1[$i] to $", i * i);
    cell1[i] = i * i;
end;
```

compiles into

```
jump 3 always 0 0
print "Iteration 1:"
print "Setting cell1[1] to 1"
write 1 cell1 1
jump 7 always 0 0
print "Iteration 2:"
print "Setting cell1[2] to 4"
write 4 cell1 2
jump 11 always 0 0
print "Iteration 3:"
print "Setting cell1[3] to 9"
write 9 cell1 3
end
```

As you can see, remarks produced by two different `remark()` function calls are not merged together.

If a region of code is unreachable and is optimized away, the remarks are also stripped:

```
const DEBUG = false;

if DEBUG then
    remark("Compiled for DEBUG");
else
    remark("Compiled for RELEASE");
end;

print("Hello");
```

produces

```
jump 2 always 0 0
print "Compiled for RELEASE"
print "Hello"
end
```

### Enhanced comments

An alternative way to create a remark is the enhanced comment:

```
/// This is an enhanced comment
```

which produces the same result as `remark("This is an enhanced comment")`. The text of the remark is taken from the comment, with any leading and trailing whitespace trimmed. The enhanced comment supports compile-time formatting just as the `remark` function does (`///Iteration: $i` is the same as `remark($"Iteration: $i");`), but there is no way to pass in additional parameters (`remark("Iteration: $", i + 1);` cannot be expressed using an enhanced comment).       

# System library

The system function discussed so far are supported directly by the compiler. Additional system functions, defined in plain Mindcode, are included in a [system library](SYSTEM-LIBRARY.markdown).

# User-defined functions

You may declare your own functions using the `def` keyword:

```
def update(message, status)
    println("Status: ", status);
    printf("Game time: $ sec", floor(@time) / 1000);
    printflush(message);
end;
```

This function will print the status and flush it to given message block.
Calling your own functions is done in the same way as any other function:

```
update(message1, "The core is on fire!!!");
```

You can pass any variable or value assignable to a variable as an argument to the function. A function may call other functions.

## Function parameters and local variables

Function parameters and variables used in functions are local to the function.
See also [local variables](SYNTAX-1-VARIABLES.markdown#local-variables)
and [global variables](SYNTAX-1-VARIABLES.markdown#global-variables).

## Return values

Function ends (returns to the caller) when the end of function definition is reached, or when a `return` statement is executed.
In the first case, the return value of a function is given by the last expression evaluated by the function;
in the second case, the return value is equal to the expression following the `return` keyword, or `null` if the return
statement doesn't specify a return value:

```
def foo(n)
    case n
        when 1 then return;
        when 2 then return "Two";
    end;
    n;
end;

printf("$:$:$", foo(1), foo(2), foo(3));
printflush(message1);
```

The output of this program is `null:Two:3`.

## Inline functions

Normal function are compiled once and called from other places in the program.
Inline functions are compiled into every place they're called from,
so there can be several copies of them in the compiled code.
This leads to shorter code per call and faster execution.
To create an inline function, use `inline` keyword:

```
inline def printtext(name, value, min, max)
    if value < min then
        println(name, " too low");
    elsif value > max then
        println(name, " too high");
    end;
end;

printtext("Health", health, minHealth, maxHealth);
printtext("Speed", speed, minSpeed, maxSpeed);
```

Large inline functions called multiple times can generate lots of instructions and make the compiled code too long.
If this happens, remove the `inline` keyword from some function definitions to generate less code.

The compiler will automatically make a function inline when it is called just once in the entire program. This
is safe, as in this case the program will always be both smaller and faster. if a function is called more than once, 
it can still be inlined by the [Function Inlining optimization](SYNTAX-6-OPTIMIZATIONS.markdown#function-inlining).

To prevent inlining of a particular function, either directly by the compiler or later on by the optimizer, use the 
`noinline` keyword:

```
noinline def foo()
    print("This function is not inlined.");
end;

foo();
```

## Recursive functions

Functions calling themselves, or two (or more) functions calling each other are _recursive_.
Recursive functions require a stack to keep track of the calls in progress
and for storing local variables from different invocations of the function.

```
allocate stack in bank1;

def fib(n)
    return n < 2 ? max(n, 0) : fib(n - 1) + fib(n - 2);
end

fib(0); // 0
fib(1); // 0
fib(2); // 1
fib(3); // 1
fib(4); // 2
fib(5); // 3
fib(6); // 5
fib(7); // 8
fib(8); // 13
// and so on...
```

Declaring a recursive function inline leads to compilation error.

Mindcode detects the presence of recursive functions and only requires the stack when at least one is found in the program.
A significant limitation is imposed on recursive functions: parameters and local variables may end up being stored on the stack.
As the stack itself is stored in a memory bank or memory cell, any non-numeric value of these variables will get lost.
For more information, see discussion of [stack](SYNTAX-1-VARIABLES.markdown#stack) in the **Variables** section.

---

[« Previous: Control flow statements](SYNTAX-3-STATEMENTS.markdown) &nbsp; | &nbsp; [Next: Compiler directives »](SYNTAX-5-OTHER.markdown)
