# Functions

Mindcode allows you to call existing functions or create new ones. Functions are called by specifying a function name and placing arguments in parentheses. The parenthesis must be used even if the function has no parameters. Functions may or may not return a value. Trying to assign a function not returning a value to a variable, or to pass the result of the function as an argument to another function call, causes an error.

There are several types of functions in Mindcode:

* [Logic functions](#logic-functions)
* [Built-in functions](#built-in-functions)
* [System library functions](#system-library-functions)
* [User-defined functions](#user-defined-functions), specifically:
  * [stackless (or out-of-line) functions](#stackless-functions),
  * [inline functions](#inline-functions),
  * [recursive functions](#recursive-functions),
  * [remote functions](REMOTE-CALLS.markdown#remote-functions-and-variables).

## Function parameters

There are several kinds of parameters a function can have:

1. _Input parameter_: serve to pass an argument to the function by the caller.
2. _Output parameter_: serve to return an output value from the function to the caller, in addition to a value possibly returned by the function itself. Arguments corresponding to output parameters are optional when calling a function. When output arguments are specified, they need to correspond to a global, main, or local variable, and need to be marked with an `out` modifier to express the intention to receive the output value from the function. Variables passed as arguments to output parameters need not be initialized, as they're initialized by the function call.  
3. _Input/output parameter_: serve both to pass a value into the function and to retrieve the value back. Input/output parameters aren't optional. It is possible to use an `in` modifier when passing an argument to input/output parameter, meaning the caller isn't interested in the output value of the argument, or an `out` modifier to also receive the output value from the function. When using the `in` modifier, any expression may be provided, but for the `out` modifier, a global, main, or local variable needs to be used. Variables passed as arguments to input/output parameters should be initialized. Using `in out` modifiers is identical to using just the `out` modifier, as it is not possible to opt out from passing the input value to the function.
4. _Reference parameter_: instead of an argument value, a reference to the argument is passed into the function. Reference parameters are declared using the `ref` modifier, and must not use either `in` or `out` modifier at the same time. Arguments passed to the reference parameters also need to be marked with the `ref` keyword. Only functions declared inline may have reference parameters, and only a variable may be passed. It is possible to pass an array (internal, external, or remote) of any length as a reference and access its elements by index from within the function.     
5. _Keyword parameter_: these parameters can be passed to Logic functions and inline functions. They require one of the predefined mlog keywords as an argument. For example, the `uradar` functions requires one of the following keywords for each of its first three arguments: `:any`, `:enemy`, `:ally`, `:player`, `:attacker`, `:flying`, `:boss`, `:ground`. The colon is stripped from keywords when converting them to mlog. It is not possible to store one of these values in a variable and pass the variable instead; an inline function can only use the keyword as an argument to another inline function or Logic function. Passing a value different to one of the supported values for a given function argument causes an error.
6. _Formattable string literal_: specific built-in functions and inline functions may also accept a [formattable string literal](SYNTAX.markdown#formattable-string-literals) as an argument. An inline function can only use the formattable string literal as an argument to another inline function or a text output built-in function.

Examples of function definitions and function calls:

```Mindcode
// a: input parameter
// b: output parameter
// c: input/output parameter
void foo(a, out b, in out c)
    b = a + c;
    c = c + 1;
    println(c);
end;

void bar()
    println("No parameters");
end;

var y = 10;
foo(20, out x, out y);    // Passing y as an input value for c, and obtaining return values from b and c
foo(20, , in y * 2);      // Not obtaining the value of b and c, but passing y * 2 as an input value for c   
bar();                    // Calling a function with no parameters
```

A difference between a reference argument and an output argument:

```Mindcode
inline void foo(in out x, ref y)
    x = y = 10;
    println("x: ", x);
    println("y: ", y);
    println("global1 in foo: ", global1);
    println("global2 in foo: ", global2);
end;

var global1 = 1, global2 = 2;
foo(in out global1, ref global2);     // The same as just foo(out global1, ref global2);
println("global1: ", global1);
println("global2: ", global2);
```

outputs 

```
x: 10
y: 10
global1 in foo: 1
global2 in foo: 10
global1: 10
global2: 10
```

The change to the `global1` variable only happens when the function call is finished, while the change to `global2` happens at the time the assignment to `y` - the reference parameter - is made.

# Logic functions

[Logic functions](FUNCTIONS.markdown) are the primary mechanism of executing specific mlog instruction. The available instruction set depends on the Mindustry version and processor type. It is possible to choose the target Mindustry version/processor type using the [`#set target` directive](SYNTAX-5-OTHER.markdown#option-target), or a [command-line argument](TOOLS-CMDLINE.markdown).

# Built-in functions

Mindcode defines several built-in functions that enhance plain Mindustry Logic. Built-in functions typically generate specialized code which is not possible to express using the standard Mindcode syntax.  

## Text output

There are several functions that can be used to send output to the text buffer, from where it can be transferred into a message block using `printflush()` or, from Mindustry Logic 8 onwards, used to draw text using the `drawPrint()` function.

### Plain text output

Printing text, variables, and expressions just as they are is possible using the `print()` and `println()` functions. These functions take any number of arguments and generate a `print` instruction for each of them. The `println()` function outputs one additional `print` instruction containing just the `"\n"` string, creating a new line in the text to be printed.

The value returned by functions performing plain text output corresponds to the value of the last argument in the argument list. If there were no arguments in the list (e.g., `println()`), the returned value is `null`.  

### Compile-time formatting

If the first argument passed to the `print()` or `println()` function is a [formattable string literal](SYNTAX.markdown#formattable-string-literals), the functions perform compile-time formatting. All `$` characters inside the formattable string literal are replaced with variables and expressions like this:

* If the `$` character is followed by a variable name, the variable is printed (external variables with the `$` prefix, e.g., `$X`, aren't supported).
* If the `$` character is not followed by a variable name, next argument from the argument list is printed.
* To separate the variable name from the rest of the text, enclose the name in curly braces: `${name}`. This is not necessary if the next character cannot be part of a variable name. `${}` can be used to place an argument from the argument list immediately followed by some text.
* It is possible to put expressions, including, for example, function calls, in curly braces: `${sum / count}`.
* The `\` character can be used to escape the `\` and `$` characters:
  * `print($"a\$b");` outputs `a$b`,
  * `print($"a\\$b");` outputs `a\` followed by the value of `b`.
* The `\n` sequence is translated to a newline as usual, while no other characters use the escape character:  
  * `print($"a\nb");` outputs `a\nb` (`\n` in the text creates a newline when output),
  * `print($"a\b\\c");` outputs `a\b\c`: the first `\` is kept as is, while the second one is used to escape the third one. 

| `print()` call                                                         | is the same as                                                         |
|------------------------------------------------------------------------|------------------------------------------------------------------------|
| `print($"$@unit at position $x, $y");`                                 | `print(@unit, " at position ", x, ", ", y)`                            |
| `print($"Time: $ sec, increment: $", floor(@second), current - last);` | `print("Time: ", floor(@second), " sec, increment: ", current - last)` |
| `print($"Coordinates: ${real}+${imag}i");`                             | `print("Coordinates: ", real, "+", imag, "i")`                         |
| `print($"Price: \$$price");`                                           | `print("Price: $", price)`                                             |
| `print($"Speed: ${distance / time}m/s");`                              | `print("Speed: ", distance / time, "m/s")`                             |

The `println()` function works the same, but outputs an additional newline character to the text buffer.  

The function was inspired by string interpolation in Ruby, but there is an important difference: the first argument to the printing function must be a formattable string literal or a constant string expression, as the formatting takes place at compile time (Mindustry Logic doesn't provide the means to do it at runtime).

```Mindcode
var x = 3;
var y = 4;
println("Position: $, $", x, y);      // No formatting - the first parameter isn't a formattable string literal
                                      // Will output "Position: $, $34\n"
println($"Position: $, $", x, y);     // Will be formatted and will ultimately output "Position: 3, 4\n"

println($"Distance: $", len(x, y));   // Will output "Distance: 5\n"
                                      // The expression for $ is taken from the list
println($"Distance: ${len(x, y)}");   // The same as above
                                      
const format = $"Position: $x, $y"; 
println(format);                      // The formattable string can be assigned to a constant                                      
```

Compile-time formatting is supported by the `print()` and `println()` functions. The value returned by these functions performing compile-time formatting is always `null`.

#### Using formattable string literal constants in functions

Variables and expressions embedded in the formattable string literals are evaluated in the context of the function in which they're used:

```Mindcode
const format = $"Position: $x, $y";
var x = 10;               // Global variables

void foo(x, y)
    println(format);      // Uses function parameters x, y
end;

void bar(y)        
    println(format);      // Uses global variable x and parameter y
end;

void baz(x)        
    println(format);      // Uses parameter x and undeclared local variable y
end;

begin
    foo(1, 2);            // Prints "Position: 1, 2"
    bar(20);              // Prints "Position: 10, 20"
    baz(30);              // Prints "Position: 30, null"
end;
```

When compiled in strict syntax, the above code produces the error "Variable 'y' is not defined." The error is located in the definition of the constant `format`, which makes identifying the cause of the error difficult. Use formattable string literal constants with caution. 

### Run-time formatting

Since version 8, Mindustry Logic supports run-time text formatting. This is done by putting placeholders `{0}` to `{9}` into the text buffer, which can be later replaced with an actual value using the `format` instruction. As the placeholders can be put into the text buffer using `print` instruction, it is possible to store and pass around the formatting string in a variable and thus separate the formatting template from actual values being output.

Run-time formatting can be done by the `printf()` function. This function takes several arguments (typically at least two). The first argument is the format string, which may be a text constant, or a string variable, and is passed into a `print` instruction. All the remaining arguments are passed into `format` instructions. Therefore, `printf(fmt, a, b, c)` gets translated to

```
print fmt
format a
format b
format c
```  

The `format` instruction searches the text buffer, looking for a placeholder with the lowest number. The first occurrence of such a placeholder is then replaced by the value supplied to the `format` instruction. This means that each format only replaces one placeholder: `printf("{0}{0}{1}", "A", "B")` followed by `printflush` therefore outputs `AB{1}` and not `AAB`. On the other hand, `printf("A{0}B", "1{0}2", "X")` outputs `A1X2B` - the placeholder inserted into the text buffer by the `format` instruction is used by the subsequent `format`. This opens up the possibility for building outputs incrementally.

Apart from the `printf()`, Mindcode supports a new `format()` function, which just outputs the `format` instruction for each of its arguments. The `printf(fmt, value1, value2)` function call is therefore just shorthand for `print(fmt); format(value1, value2);`.

> [!TIP]
> Since the `format` instruction allows decoupling the formatting template from the values being applied to the template, Mindcode is unable to apply the print merging optimizations to the `format` instruction with constant values. Use compile-time formatting instead of run-time formatting whenever possible for more efficient code.      

> [!TIP]
> Print merging optimizations can use the `format` instruction for more effective optimizations. To make sure the optimizations do not interfere with the `format` instructions placed into the code by the user, the optimizer only uses the `{0}` placeholder for its own formatting. This leaves the remaining nine placeholders, `{1}` to `{9}`, for use in the code itself. If you do use the `{0}` placeholder in your own code, the more efficient optimization using the `format` instruction will be disabled.

> [!NOTE]
> The `printf()` function is only supported in Mindustry Logic 8.
> 
> In older versions of Mindcode, `printf()` function performed compile-time formatting, and could take both formattable string literal and a standard string literal as the format argument. This version of the function is no longer available. Use `print()`/`println()` functions instead.

## Remarks

The `remark()` function has the same syntax as the `print()` function and supports both the plain text output and compile-time formatting modes just like the `print()` function does. It also produces `print` instructions, but the way these instructions are generated into the code can be controlled using the [`remarks` option](SYNTAX-5-OTHER.markdown#option-remarks):

* `none`: remarks are suppressed in the compiled code—they do not appear there at all.
* `comments`: remarks are output as mlog comments into the compiled code. Expressions in remarks are output as separate comments with the mlog name of the variable holding the value of the expression.
* `passive`: remarks are included in the compiled code, but a jump is generated in front each block of continuous remarks, so that the print statements themselves aren't executed. This is the default value.
* `active`: remarks are included in the compiled code and are executed, producing actual output to the text buffer.

Example:

```Mindcode
#set remarks = passive;
remark("Configurable options:");
param MIN = 10;
param MAX = 100;

remark("Don't modify anything below this line.");
for var i in MIN .. MAX do
    println("Here is some actual code");
    // ...
    printflush(message1);
end;
```

produces

```mlog
jump 2 always 0 0
print "Configurable options:"
set MIN 10
set MAX 100
jump 6 always 0 0
print "Don't modify anything below this line."
set :i MIN
jump 0 greaterThan MIN MAX
print "Here is some actual code\n"
printflush message1
op add :i :i 1
jump 8 lessThanEq :i MAX
```

Remarks may also allow for better orientation in compiled code, especially as expressions inside remarks will get fully evaluated when possible:

```Mindcode
#set remarks = comments;
for var i in 1 .. 3 do
    remark($"Iteration $i:");
    remark($"Setting cell1[$i] to ${i * i}");
    cell1[i] = i * i;
end;
```

compiles into

```mlog
# Iteration 1:
# Setting cell1[1] to 1
write 1 cell1 1
# Iteration 2:
# Setting cell1[2] to 4
write 4 cell1 2
# Iteration 3:
# Setting cell1[3] to 9
write 9 cell1 3
```

As you can see, remarks produced by two different `remark()` function calls are not merged.

If a region of code is unreachable and is optimized away, the remarks are also stripped:

```Mindcode
#set remarks = passive;
const DEBUG = false;

if DEBUG then
    remark("Compiled for DEBUG");
else
    remark("Compiled for RELEASE");
end;

print("Hello");
```

produces

```mlog
jump 2 always 0 0
print "Compiled for RELEASE"
print "Hello"
```

In strict syntax mode, remarks can only be used inside code blocks.

### Enhanced comments

An alternative way to create a remark is the enhanced comment:

```Mindcode
/// This is an enhanced comment
```

which produces the same result as `remark("This is an enhanced comment")`. The text of the remark is taken from the comment, with any leading and trailing whitespace trimmed. The enhanced comment supports compile-time formatting just as the `remark` function does (`///Iteration: ${i + 1}` is the same as `remark($"Iteration: ${i + 1}");`). There is no way to pass in additional parameters (`remark("Iteration: $", i + 1);` cannot be expressed using an enhanced comment and the expression must be embedded inside the format string).

In strict syntax mode, enhanced comments are allowed outside code blocks:

```Mindcode
#set syntax = strict;

/// Configurable options:
param MIN = 10;
param MAX = 100;

/// Don't modify anything below this line.
linked message1;

begin
    for var i in MIN .. MAX do
        println("Here is some actual code");
        // ...
        printflush(message1);
    end;
end;
```

If the enhanced comment is used at the end of a line containing a complete statement, the generated output is moved in front of the first such statement:

```Mindcode
#set remarks = passive;
param MIN = 10;       /// Minimal value
param MAX = 100;      /// Maximal value

print(MAX - MIN);
```

produces:

```mlog
jump 2 always 0 0
print "Minimal value"
set MIN 10
jump 5 always 0 0
print "Maximal value"
set MAX 100
op sub *tmp0 MAX MIN
print *tmp0
```

## The `ascii()` function

The `ascii()` function takes a string constant as an argument, and returns the ASCII value of its first character.

The function allows converting built-in string constants (most importantly, icons) to their ASCII values and using them with `printchar()`. Since the ASCII values are numbers, they can be stored in external variables:

```Mindcode
#set target = 8m;
external(cell1) a = ascii(ITEM_COAL);
printchar(a);
```

compiles to 

```mlog
write 63539 cell1 0
read *tmp1 cell1 0
printchar *tmp1
```

## The `char()` function

The `char()` function returns the ASCII value of a character at a given position in a string. The operation is supported by the `read` instruction in Mindustry Logic since version 8, therefore, both the string and the position may be stored in a variable. Because of this, the `char()` function is only supported in Mindustry Logic 8.

The values obtained from this function may be used with `printchar()`:

```Mindcode
#set target = 8m;
param data = "Dbftbs!djqifs";
linked message1;

begin
    var index = 0;
    while true do
        var ch = char(data, index);
        if ch == null then
            printflush(message1);
            index = 0;
        else
            printchar(ch - 1);
            index++;
        end;
    end;
end;
```

compiles to:

```mlog
set data "Dbftbs!djqifs"
set :index 0
read :ch data :index
jump 7 notEqual :ch null
printflush message1
set :index 0
jump 2 always 0 0
op sub *tmp3 :ch 1
printchar *tmp3
op add :index :index 1
jump 2 always 0 0
```

## The `strlen()` function

The `strlen()` function returns the length of a string passed in as an argument. The function requires target `8` or higher.

```Mindcode
#set target = 8m;
param data = "Dbftbs!djqifs";
println(strlen(data));
printflush(message1);
```

compiles to:

```mlog
set data "Dbftbs!djqifs"
sensor *tmp0 data @size
print *tmp0
print "\n"
printflush message1
```

## The `encode()` function

The `encode()` function allows encoding numeric values into a string, so that they can be later retrieved through the `char()` function. Values encoded to a string can be stored in variables or internal arrays and passed around to functions or remote functions. The string value is created at compile-time, meaning all function arguments must be compile-time constants.

The first argument to the function is an _offset_. The remaining arguments are the values to be encoded. The offset is added to each value before it is encoded. Using a nonzero offset makes it possible to encode values that cannot be represented in an mlog string. The offset needs to be subtracted from the values obtained by the `char()` function.

The following integer values can be encoded into a string:

* Integers in the range `1 .. 0xD7FF` (`1 .. 55295`), except 13 and 34 (note that `0` is not allowed either).
* Integers in the range `0xE000 .. 0xFFFF` (`57344 .. 65535`).

Furthermore, the encoded string must not contain the character sequence `\n` (`92` followed by `110`).

When the function produces a string that cannot be encoded, a compilation error occurs.

Example:

```Mindcode
#set target = 8m;

const offset = 35;
const data = encode(offset, 0, 1, 2, 5, 10, 20, 50);

for i in 0 ... strlen(data) do
    println(char(data, i) - offset);
end;

printflush(message1);
```

compiles into

```mlog
sensor *tmp1 "#$%(-7U" @size
set :i 0
jump 9 greaterThanEq 0 *tmp1
read *tmp2 "#$%(-7U" :i
op sub *tmp3 *tmp2 35
print *tmp3
print "\n"
op add :i :i 1
jump 3 lessThan :i *tmp1
printflush message1
```

> [!NOTE]
> Data encoded with the `encode()` function may not be easily readable in a text editor or the game interface. Some text editors may not be able to display the encoded data correctly or may even damage the data when copying them to the clipboard. If you suspect this is happening, use the Mlog Watcher mod or create schematics containing a processor with your code to avoid the clipboard. 

## The `error()` function

The `error()` function, which can be used to report errors detected at runtime, is described [here](TROUBLESHOOTING.markdown#the-error-function).

## The `length()` function

A `length()` function determines the length of an array passed in as an argument, or the number [vararg](#vararg-functions) elements. `0` is returned when the vararg contains no elements.

```Mindcode
inline void foo(args...)
    println(length(args));
end;

foo();                      // 0
foo(10);                    // 1 
foo(1, 2, 3, 4, 5);         // 5 
```

The function always takes just one argument. When the argument passed in is not an array or a vararg, the function returns `1`.

## Remote calls

The intrinsic functions `async()`, `finished()`, `await()` and `atomic()` are part of the Parallel Processing framework and are described [here](REMOTE-CALLS.markdown#asynchronous-remote-calls) and [here](REMOTE-CALLS.markdown#atomic-functions).   

# System library functions

The functions discussed so far are either mapped directly to Mindustry Logic or built-in to the compiler. Additional system functions, defined in plain Mindcode, are included in the [system libraries](SYSTEM-LIBRARY.markdown).

# User-defined functions

You may declare your own functions using the `def` keyword:

```Mindcode
def sum(x, y)
    x + y;
end;    
```

Functions not returning any value must be declared using the `void` keyword:

```Mindcode
void update(message, status)
    println($"Status: $status");
    println($"Game time: ${floor(@time) / 1000} sec");
    printflush(message);
end;
```

Functions that do return a value must be declared using the `def` keyword, and they need to provide a return value on all possible code paths. The last statement executed on all code paths must be either an expression providing the function value, or a `return` statement providing the function value:

```Mindcode
def foo(number)
    if number < 0 then
        println("negative");
        return -1;         // Directly provides the return value, terminating the code path 
    end;
    
    // The if expression always has a value
    if number = 0 then
        println("zero");
        0;    // Provides value of the true branch
    else
        println("positive");
        1;    // Provides value of the false branch
    end;
    
    // At this point the return value of the function is the value of the above if expression
end;
```

Loops are statements, not expressions, and provide a value of `null`:

```Mindcode
def foo(count)
    sum = 0;
    while count > 0 do
        sum += count--;        
    end;
end;

print(foo(10));       // Prints "null"
```

As has been described above, function parameters in a user function can be input, output or input/output. Use the `in` and `out` modifiers to properly mark the function parameters:

```Mindcode
def function(in x, out y, in out z)
    // ...
end; 
```

The `in` modifier to mark input parameters is optional—function parameters are input by default. 

## Function parameters and local variables

Function parameters and variables used in functions are local to the function. See also [variable scope](SYNTAX-1-VARIABLES.markdown#variable-scope).

## Return values

Function declared usinf the `def` keyword return a value. The return value is determined when the function ends (returns to the caller). This happens either when the end of function definition is reached, or when a `return` statement is executed. In the first case, the return value of a function is equal to the last expression evaluated by the function; in the second case, the return value is equal to the expression following the `return` keyword:

```Mindcode
def foo(n)
    case n
        when 1 then return "One";
        when 2 then return "Two";
    end;
    n;
end;

print($"$:$:$", foo(1), foo(2), foo(3));
printflush(message1);
```

The output of this program is `One:Two:3`.

Void functions are declared using the `void` keyword instead of `def` keyword. Void functions do not return any value. The `return` statement in a `void` function therefore must not specify a value:

```Mindcode
void foo(n)
    if n > 10 then
        return;
    else
        print(n);
    end;
end;
```

## Function modifiers

The following function modifiers are supported:

* `inline`: creates an inline function.
* `noinline`: prevents the function from being inlined, resulting either in a stackless or recursive function.
* `export`: creates a [function which can be called remotely](REMOTE-CALLS.markdown#exported-functions).
* `atomic`: creates a [function whose body is executed atomically](REMOTE-CALLS.markdown#atomic-functions).
* `debug:` creates a function that is only compiled in debug mode.

## Stackless functions

Stackless functions (also out-of-line functions) are compiled once and called from other places in the program. This is the basic type of user-defined functions. When possible, a stackless function may still be inlined by the [Function Inlinig optimizer](optimizations/FUNCTION-INLINING.markdown).

## Inline functions

Inline functions are compiled into every place they're called from, so there can be several copies of them in the compiled code. This leads to shorter code per call and faster execution. To create an inline function, use `inline` keyword:

```Mindcode
inline void printtext(name, value, min, max)
    if value < min then
        println(name, " too low");
    elsif value > max then
        println(name, " too high");
    end;
end;

printtext("Health", health, minHealth, maxHealth);
printtext("Speed", speed, minSpeed, maxSpeed);
```

Large inline functions called multiple times can generate lots of instructions and make the compiled code too long. If this happens, remove the `inline` keyword from some function definitions to generate less code.

The compiler will automatically make a function inline when it is called just once in the entire program. This is safe, as in this case the program will always be both smaller and faster. Even when a function is called more than once, it can still be inlined by the [Function Inlining optimization](optimizations/FUNCTION-INLINING.markdown).

To prevent inlining of a particular function, either directly by the compiler or later on by the optimizer, use the `noinline` keyword:

```Mindcode
noinline void foo()
    print("This function is not inlined.");
end;

foo();
```

### Vararg functions

Vararg functions are also called "variable arity" functions. These functions can take a variable number of arguments.

In Mindcode, only functions declared `inline` may use vararg parameter. The vararg parameter must always be the last one, and is declared by appending `...` after the parameter name. The vararg argument may be used in a list iteration loop within the function or may be passed to another function.

```Mindcode
inline void foo(values...)
    for i in values do
        println(i);
    end;
    println("The sum is: ", sum(values));
end;

inline def sum(values...)
    sum = 0;
    for i in values do
        sum += i;
    end;
    sum;
end;

foo(1, 2, 3);
```

### Varargs as function arguments

The vararg parameter may be passed to standard functions as well. For example

```Mindcode
inline void foo(args...)
    println(min(args));
    println(max(args));
    println($"Values: $, $, $, $", args); 
end;

foo(1, 2, 3, 4);

// foo(1, 2, 3, 4, 5);         // Causes error in the call to println($"Values...") function, as it expects exactly 4 arguments 
```

An `out` argument may also be passed via vararg to a function that accepts it:

```Mindcode
inline void foo(arg...)
    bar(arg);
end;

void bar(a, b, out c)
    c = a + b;
end;

foo(1, 2, out result);      // No error, places the sum into result
foo(1, 2);                  // Also no error, the third argument to bar is optional
// foo(1);                  // Error - bar needs at least two arguments
// foo(1, 2, 3);            // Error - bar needs an out argument at the third position
```

> [!NOTE]
> The errors produced when passing the vararg parameter to another function are reported at the call of the inner function, not at the call of the vararg function, and may therefore be a bit misleading. Use with caution.

The vararg parameter may be mixed with ordinary variables or expressions and used more than once, even in the same list iteration or function call:

```Mindcode
inline void foo(arg...)
    bar(10, arg, 20, arg); 
end;

inline void bar(a, b, c...)
    for i in a, b, c, c do
        println(i);
    end;
end;

foo(1, 2, 3);
```

## Recursive functions

Functions calling themselves, or two (or more) functions calling each other are _recursive_. Recursive functions require a stack to keep track of the calls in progress and for storing local variables from different invocations of the function.

```Mindcode
allocate stack in bank1;

def fib(n)
    return n < 2 ? max(n, 0) : fib(n - 1) + fib(n - 2);
end;

println(fib(0)); // 0
println(fib(1)); // 1
println(fib(2)); // 1
println(fib(3)); // 2
println(fib(4)); // 3
println(fib(5)); // 5
println(fib(6)); // 8
println(fib(7)); // 13
println(fib(8)); // 21
// and so on...
```

Declaring a recursive function inline leads to a compilation error.

Mindcode detects the presence of recursive functions and only requires the stack when at least one is found in the program. A significant limitation is imposed on recursive functions: parameters and local variables may end up being stored on the stack. As the stack itself is stored in a memory bank or memory cell, any non-numeric value of these variables will get lost.

### Stack

When using recursive functions, some of their local variables and parameters may have to be stored on a stack. As Mindustry Logic doesn't provide a built-in stack, external memory is used instead. This places the same limitations on local variables and parameters of recursive functions as on arrays and external variables (that is, only numeric values are supported).

Stack needs to be allocated similarly to heap:

```Mindcode
allocate stack in bank1[256...512];
```

> [!NOTE]
> When no range is given (e.g., `allocate stack in cell1`), full range of the given memory block is assumed.

When a function is not recursive, it won't store anything on a stack, even when it is called from or it itself calls a recursive function. If your code contains a recursive function, it won't compile unless the stack is allocated. Therefore, if your code compiles without the `allocate stack` statement, you don't need to worry about your functions not supporting non-numeric variables or parameters.

## Atomic functions

An atomic function guarantees the function body will be executed atomically. See [Atomic functions](REMOTE-CALLS.markdown#atomic-functions) for more details. 

## Debugging functions

It is possible to declare a function as `debug`. In this case, calls to the function will only be compiled when the [`debug` option](SYNTAX-5-OTHER.markdown#option-debug) is set to true. Example:

```Mindcode
debug inline void pause(in switch)
    // Pauses the execution of the program until the switch passed in is deactivated - clicked by the user
    // Allows the user to inspect the state of the program - the variables at the place where it was called.  
    switch.enabled = true;
    do while not switch1.enabled;
end;
```

A debug function must not return a value - must be declared `void` - and must not contain any parameter declared `out`. Other modifiers (`in`, `in out` and `ref`) are allowed.

The `debug` keyword must precede the `inline`, `noinline`, or `export` keywords (when used) and the `void` keyword. 

## Function overloading

Mindcode supports function overloading. Several functions can have the same name, provided they differ in the number of arguments they take. For example:

```Mindcode
void foo(x)
    println("This is foo(x)");
end;

void foo(x, y)
    println("This is foo(x, y)");
end;

foo(1);     // Calls foo(x)
foo(1, 1);  // Calls foo(x, y) 
```

To match a function call with function declaration, the number and types (input, output or input/output) of function call arguments must match the number and types of function parameters. Optional and vararg arguments are taken into account when evaluating the match.

When two or more function declarations could be matched by the same function call, the functions conflict with each other. Declaring conflicting functions results in an error. A function with output parameters takes a variable number of arguments and therefore may conflict with a function having a different number of parameters:

* `void foo(a, b)`: takes two arguments.
* `void foo(x, y, out z)`: conflict - may also take two arguments when `z` is omitted.

* `void bar(x)`: `x` is an input parameter
* `void bar(out y)`: `y` is an output parameter, therefore, the function is different from `bar(x)`.
* `void bar(in out z)`: `z` is an input/output parameter, therefore, the function clashes with both `bar(x)` and `bar(out y)`.

A vararg function doesn't conflict with a non-vararg function. When a function call matches both a vararg function and a non-vararg function, the non-vararg function will be called. It is therefore possible to declare functions handling a specific number of arguments, plus a vararg function handling the generic case. The non-vararg functions handling a specific number of arguments will be used when possible.

Mindcode will report all conflicts of function declarations as errors, even if there aren't any ambiguous function calls.

A user-defined function may have the same name as a Logic function. User-defined functions override Logic functions of the same name. When a function call matches the user-defined function, the user-defined function will be called instead of Logic function:

```Mindcode
inline def ulocate(ore, oreType)
    print("Calling user-defined function");
end;

found = ulocate(:ore, @copper);                   // Calls the user-defined function
found = ulocate(:ore, @copper, out x, out y);     // Calls the Logic function
```

If, however, the user-defined ulocate function was defined with output variables as well, both calls would call the user-defined function:

```Mindcode
inline def ulocate(ore, oreType, out x, out y)
    print("Calling user-defined function");
    x = y = 0;
end;

found = ulocate(:ore, @copper);                  // Calls the user-defined function
found = ulocate(:ore, @copper, out x, out y);    // Also calls the user-defined function
```

It is not possible to call a Logic function if a matching user-defined function exists.

---

[&#xAB; Previous: Control Flow Statements](SYNTAX-3-STATEMENTS.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Logic functions &#xBB;](FUNCTIONS.markdown)
