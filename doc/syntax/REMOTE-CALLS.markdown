# Remote functions and variables

Mindcode supports accessing variables (including arrays) in linked processors and calling Mindcode functions in them. This functionality uses the Mindustry Logic 8 ability to read and write variables to/from other processors, including `@counter`, by name.

Calling functions stored in other processors is a feature which brings two main benefits to Mindcode:

1. Possibility to break a large program into several pieces stored in multiple processors, effectively surpassing the 1000 instruction limit per program.
2. Means to call remote functions asynchronously, resulting in seemingly parallel processing.

## Architecture

The processor which accesses another processor's variables or functions is called _main processor_. The processor which contains these variables and functions is called _remote processor_. A main processor can use multiple remote processors, but each remote processor can be used by at most one main processor. Furthermore, a remote processor can act as a main processor towards another remote processor. Processors can therefore be arranged into an oriented graph—a tree (in the sense of graph theory: the root is the main processor, which starts and controls the execution over the entire processor tree). We call the code stored in the topmost main processor a _program_, and the code stored in a remote processor a _module_.

The execution of the entire distributed program starts with the topmost main processor (there's always just one), while the other remote processors wait until a remote call is initiated from their respective main processor. Both the main processor and the remote processors wait until their respective remote processors are initialized.

To successfully run a distributed program in Mindustry World, all necessary processors with their code need to be built. The easiest way to do so is to create a schematic (using [Schemacode](SCHEMACODE.markdown)) containing all the processors, properly linked, and their code.

Code for each processor in a distributed system is compiled separately from a standalone source code. When using the command-line compiler, each source code would typically be stored in a separate file. It is also possible to include all processors and their code in a single Schematic Definition File, which can be compiled by both the web application and the command-line processor.

## Module signatures

During compilation, a CRC64 value, based on all remote function declarations, is computed for each module. Function name, call type, return type, parameter names, types, and modifiers are included in the calculation. Additionally, the version number of the remote call protocol used by the compiler is appended to the text representation of the CRC64 value.

A remote processor is successfully initialized only when the signature stored in the remote processor matches the signature expected by its main processor. This ensures that the remote code won't be called if it is not compatible with the main processor. If any remote method's signature is altered, or remote methods are added or removed from the module, the signature changes, and the code in the processor that binds to the remote processor needs to be recompiled too. Similarly, if a new Mindcode version which uses a different remote call protocol is used to recompile some processor's code, all processors related to the altered one through the remote call mechanism need to be recompiled too.

Remote variables are not included in module signatures. If a variable used by the main processor gets removed from the remote module, and the remote processor's code is updated with the new code, access to the remote variable from the main processor will be broken (writes to the removed variable will be ignored, while reads from the removed variable will always return `null`).    

## Remote processors code

Code belonging to a remote processor must be placed in a module. A module is created by using the `module` keyword. Modules must be named, although at this moment there is no use for the name:

```
module library;
```

When compiling a module, the compiler generates a dispatch table and an initialization code.

### Dispatch table

A dispatch table is a jump table that contains a jump to each remote function. All remote functions are sorted by name and assigned indexes starting at 1. An instruction at the given index of the dispatch table contains a jump to the start of the actual function (index 0 is reserved for a jump to the initialization code). Since any change affecting the order of functions would alter the module signature, matching module signature ensures the dispatch table contains the function entry points at expected indexes.

When making remote calls, the main processor sets the `@counter` in the remote processor to the remote function index. This allows the code in the remote processor to be recompiled without having to reinitialize (restart) the main processor. However, recompiling the remote processor may terminate pending remote calls and cause the main processor to never receive the results of these remote calls.

### Initialization code

If the module contains any code outside functions (i.e., a main code block, or global variable initializations), this code is executed as part of the initialization of the remote processor. At the very end of this custom initialization code, the `*signature` variable is set to the module signature.

Only when the initialization code completes and the signature is set, the processor is ready to accept remote calls. The main processor's initialization waits for all remote processor initializations to complete, so it is crucial for the initialization code to be fast. If the initialization code contains an endless loop, the main processor execution will be blocked forever.

### Remote variables

It is possible to declare a remote variable or array in a module using the `remote` keyword:

```
[noinit] [volatile] remote [var] <variable1> [= <initial value>];
[volatile] remote [var] <variable1>'['size']' [= (<initial values>)] [, <variable2> [= (<initial values>)] ... ];
```

Remote variables must be declared in global scope and are therefore always global. When an initial value is provided, it is assigned to the variable at the moment of the declaration. Any expression can be used as the initial value, including function calls.

When declaring remote variables, these additional modifiers can be used:

* `noinit`: this modifier suppresses the "uninitialized variable" warning for the declared variable. This modifier cannot be used if the variable is assigned an initial value.
* `volatile`: remote variables are volatile by default, because they may be read or written by other processors. It is possible to specify the `volatile` modifier explicitly for better clarity.

Modifiers can be specified in any order.

```Mindcode
#set target = 8;

// Remote variables, a unitialized, b initialized
remote a, b = 10;

// Remote arrays: initialized and uninitialized
remote array2[5] = (1, 2, 3, 4, 5);
remote array1[10];
```

Remote variables are available through remote access in code which imports the module. Remote variables/arrays may or may not be initialized. When some remote variables or array elements are unused within the module (and the corresponding mlog variables would therefore not be created when parsing the code by the logic processor), the missing variables are explicitly created using `draw triangle` instruction(s), so that they may be accessed remotely. These instructions are placed at the end of the generated code and are never executed.

When a remote module is [instantiated multiple times](#multiple-instantiations-of-a-remote-module), `@counter` arrays are created just once and are shared among all processors where the module is contained. This increases both the size and cost of array access by one instruction needed to set up the remote processor. When the array access is inlined or otherwise optimized, this additional cost disappears.  

### Remote functions

Function to be called remotely must be declared using the `remote` keyword:

```Mindcode
#set target = 8;

remote def foo(x, y, out z)
    z = x + y;
    return x * y;
end;
```

Function parameters may be input or output (as usual). A remote function is never inlined and can be declared neither `inline` nor `noinline`. Varags and `ref` parameters aren't supported. Remote functions cannot be overloaded, although it is possible to overload a remote function with non-remote ones. Remote functions must not be called from the module which declares them, meaning they also can't be recursive. It is possible to call a recursive function from within a remote function, though.

All remote functions are active entry points for the compiler and thus are always included in the compiled code.

### Background process

When waiting for a remote call, the remote processor is by default idle. It is possible to define a function to be executed while waiting for a remote call. This function is executed after the remote module initializes, and subsequently whenever a remote call finishes. If the background function ever terminates (or if it is not defined at all), a `wait` instruction is used to wait for the next remote call.

When a remote call occurs while the background function still executes, the background process is terminated and the remote function starts executing immediately. There's no cleanup process, it is up to the programmer to make sure the background function can be safely terminated at any time, and reentered when the next remote call ends.

The background process must be contained in a function named `backgroundProcess`. The function declaration needs to have these properties:

1. No `remote`, `inline` or `noinline` keywords used in function declaration.
2. Return type of the function is `void`.
3. The function has no parameters.
4. The function is not recursive.

If a `backgroundProcess` function with these properties is found in the source code, it is used as the background process. If no such function exists, no background process is created.

It is possible to call the `backgroundProcess()` function explicitly. In this case, the function is compiled normally and may even be inlined.

> [!IMPORTANT]
> Variables that are accessed from the background process as well as from a remote function must be declared `volatile`.

Example of a module with a background process:

```Mindcode
#set target = 8;

module backgroundProcess;

// Start at -1, because the background process is run once after module initialization
var invocations = -1;

void backgroundProcess()
    print($"Number of method calls: ${++invocations}");
    printflush(message1);
end;

remote void foo(in a, out b)
    b = 2 * a;
end;
```

The above module compiles into this mlog:

```mlog
jump 2 always 0 0
jump 10 always 0 0
set .invocations -1
set *signature "974935c9f6b9c503:v1"
op add .invocations .invocations 1
print "Number of method calls: "
print .invocations
printflush message1
wait 1e12
jump 4 always 0 0
op mul :foo:b 2 :foo:a
set :foo*finished true
jump 4 always 0 0
```

## Main processor code

All remote modules must be declared in the main processor code using the `require ... remote` statement. This declaration specifies which remote processor contains remote functions:

```
require "library.mnd" remote processor1;
```

> [!NOTE]
> A module containing remote functions can be also imported locally, through plain `require`. In this case, the remote variables are accessible locally, and the remote functions cannot be called.

The required file (in this example stored in `library.mnd` file) must be a module, and the compiled code of this module must be stored in processor `processor1`. The processor can be specified using a linked block name, a parameter, or a global variable. Using variables in the `remote` clause allows [dynamic binding](#dynamic-binding) of remote processors.

Modules imported remotely aren't compiled into the code, but remote variables and functions declared in them are available to the main processor.

When at least one remote function is called from a processor, guard code for the processor is generated. The guard code waits for the processor to be created and its `*signature` variable initialized. If the signature found in the remote processor doesn't match the expected signature, the main processor remains stuck at the initialization.

A module which is being requested for remote access may appear in only one `require` clause within a given source file.

### Multiple instantiations of a remote module

The same remote module might be compiled into two or more different remote processors. In this case, remote functions can be run concurrently in any number of remote processors. This is done by including more than one processor in the `remote` clause:

```
require "library.mnd" remote processor1, processor2, processor3;

async(processor1.foo(1));
async(processor2.foo(2));
async(processor3.foo(3));

// The code in the three processors executes concurrently. Here we'll wait for all of them to finish
await(processor1.foo);
await(processor2.foo);
await(processor3.foo);
```

### Remote variables

All variables nd arrays declared `remote` in remotely requested modules are accessible to the main processor. The variables and array elements are stored in the remote processor. Access to individual elements happens through the `read` and `write` instructions, not through remote calls. For random array access, jump tables are generated in the main processor code, meaning that random access to remote arrays has the same performance as random access to local arrays. Direct access to remote array elements gets resolved to remote variable access, which is as fast as external variable access (except it supports non-numerical values).

Remote variables are accessed as properties on bound processors, for example:

```
require "library.mnd" remote processor1, processor2;

print(processor1.variable);
processor2.array[10]++;
```

### Synchronous remote calls

When calling a remote function, the processor on which the function resides must be specified. The synchronous remote call syntax is similar to a local function call:

```
product = processor1.foo(10, 20, out sum);
```

Execution of the code is paused until the remote function completes and returns. The effect of calling a remote function is the same as calling a local function: function return value and output variables are set up as usual.

### Asynchronous remote calls

When a remote function is called asynchronously, the main processor continues to execute code while the remote function computes. It is possible to determine whether the remote function has completed and/or wait for its completion and receive return values. Multiple asynchronous remote calls may be active at the same time, provided each of them is executed on a different remote processor.

Asynchronous calls are started using the built-in `async()` function:

```
async(processor1.foo(10, 20));
async(processor2.foo(20, 30));
```

The `out` modifiers are disallowed in asynchronous function calls, and output-only arguments need to be completely omitted.

To check for completion, call the `finished()` function, passing the full name of the remote function as an argument:

```
done = finished(processor1.foo);
```

The function returns `true` if the asynchronous call to the given remote function is finished.

The `finished()` function has an optional output parameter that receives the return value of the function when it successfully completes:

```
if finished(processor1.foo, out value) then
    println("The resulting value of foo is ", value);
end;
```

Function return value may be obtained via the `await()` function (not to be confused with the `wait()` function mapped to the `wait` instruction):

```
product = await(processor1.foo);
sum = processor1.foo.z;
```

This call functions waits for `foo` completion, and then returns its return value. Other output parameters are available under their fully qualified names, e.g., `processor1.foo.z`. The output parameters may be queried as soon as `finished()` returned true. Calling `await()` is not necessary before starting another remote call on the same processor.

### Dynamic binding

A processor containing the remote code can also be located dynamically. In this case, the `remote` clause in the `require` directive contains a variable, not a linked block reference.

> [!NOTE]
> Variable which holds the reference to a dynamically bound processor needs to be global. The best way to ensure this is to declare the variable before the `require` clause, which works in both strict and relaxed syntax modes. It is possible to use an implicitly declared global variable in relaxed syntax mode, in which case the variable needs to be named in upper case. The recommended solution is to explicitly declare the variable regardless of the syntax mode.   

```
var proc;
require "remote.mnd" remote proc;
```

The compiler doesn't automatically generate code to verify the module signature for dynamically bound processors. It is possible to verify the signature using a `verifySignature()` built-in function. This function takes a processor as an argument, and returns `true` if the processor contains the expected module code.  

```
for var i in 0 ... @links do
    proc = getlink(i);
    if verifySignature(proc) then
        async(proc.foo(10));
    end;
end;
```

Using `verifySignature` ensures it is safe to call a remote function in the given processor. When `verifySignature` returns `false`, it means one of these situations happened:

* the block being inspected is not a processor,
* the processor doesn't contain code corresponding to the remote module, or the code is not compatible with an expected version (module signatures differ),
* the processor contains the correct remote module code, but the initialization hasn't completed yet. 

It is possible to use several variables in the `remote` clause, in which case the remote module can be accessed though any of them. Mindcode doesn't make any assumptions about the identity of these processors (particularly whether they point to the same processor or not).

Processor references can also be stored in an array. However, individual array elements cannot be directly used to access the remote module. Instead, the reference needs to be stored to a bound processor variable first:

```
var p1, p2;
require "remote.mnd" remote p1, p2;

var processors[10];

// Fills array with valid processors
index = 0;
MainLoop:
for out p1 in processors do
    while true do
        if index >= @links then break MainLoop; end;
        p1 = getlink(index++);
        if verifySignature(p1) then break; end;
    end;
end;

// Not possible
processors[1].foo(10);

// Possible
p2 = processors[1];
p2.foo(10);
```

## Remote call mechanism

This chapter briefly describes how synchronous and asynchronous calls are implemented.

### Local side - synchronous call

* The function parameters are set up in the remote processor.
* `:functionName*finished` is set to `false` in the remote processor.
* `@counter` in the remote processor is set to the function dispatch index.
* Waiting for call completion:
  * instruction `wait 1e-15` is executed. This skips the rest of the tick while transferring the unspent instruction execution quota to the next tick(s).
  * loop while `:functionName*finished` in the remote processor equals `false`
* The function return-value, if any, and values of its output parameters, are copied from the remote processor to the main processor.

### Local side—asynchronous call

* The function parameters are set up in the remote processor.
* `:functionName*finished` is set to `false` in the remote processor.
* `@counter` in the remote processor is set to the function dispatch index.
* The code execution continues in the main processor.

Monitoring the call, waiting for completion, and retrieving output values must be implemented by the programmer using the `await()` and/or `finished()` functions.

### Remote side

The call mechanism on the remote side is the same for both synchronous and asynchronous calls.

* Waiting for the remote call is implemented using the `wait 1e12` instruction. This wait is over 30 thousand years long, so it never completes. The wait also transfers unspent instruction execution quota to the next tick(s).
* Remote call is initiated by the main processor remotely setting `@counter` to the function entry point in the dispatch table. The function starts executing immediately, as the caller sets up all input parameters.
* Upon completion, the function performs these operations:
    * `:functionName*finished` is set to `true`.
    * The waiting phase is reentered.

## Terminating function calls

If the same or different remote function gets called while a previous call is active, the previous call is terminated. The corresponding `:functionName*finished` flag won't be set.

There's no way to define a cleanup routine in case a remote call is terminated, and Mindcode doesn't guard against possible corruption of the remote processor state.

It is also possible to start an asynchronous remote call and use remote variables to communicate with the remote function instead of waiting for it to finish. It's up to the programmer to devise a communication protocol for such a scenario. By calling the same (or different) function again, this remote processing could be restarted or terminated.

## Performance of remote calls

Instructions in Mindustry Logic processors aren't really executed in parallel. Instead, in each tick all processors are visited sequentially, and a number of instructions corresponding to the given processor's _instructions per tick_ rate are executed. This means that when a synchronous remote call is made, even if the call returned immediately, the call will always take at least a full tick. Performing many short calls to remote functions might, therefore, be very detrimental to the performance of your program.

> [!IMPORTANT]
> The faster the processor, the more instructions get executed in a single tick, and the bigger the impact from the delay of a remote call. Unboosted micro-processor executes two instructions per tick, and the overhead of even short remote calls is almost negligible (at most one instruction execution will be lost on each call). Fully boosted hyper-processor executes 62.5 instructions per seconds, and frequent short remote calls could reduce the effectivity of the hyper-processor by 90% or more. World processors with instruction rates at several hundred IPTs could be affected even more.
>
> An additional factor is the frame rate: processor updates aren't driven by ticks but by frame updates. A frame rate higher than 60 FPS means there are fewer instructions wasted per remote call, while lower frame rates mean more instructions wasted. Even code which performs well when the game runs at 60 FPS might develop performance problems when the frame rate drops.

### Managing the execution quota

Code generated by Mindcode uses the `wait` instruction when waiting for a remote procedure call to finish, and when the remote module waits for another call to serve. The `wait` instruction terminates the processor's execution in the current frame, saving up unspent execution quota. Up to four ticks of unspent instructions can be accumulated this way. When normal execution resumes, the unspent quota is spread over several ticks to provide a boost to the processor execution (a half of the remaining unspent execution quota is used up in each following tick that doesn't contain a `wait` or another yielding instruction - the others are `stop` and `message`).

When the interval between remote calls is longer than a single tick, and the duration of the remote calls is also longer than a single tick, synchronous remote calls might even provide execution boosts. This could lead to faster execution than running an equivalent code on a single processor. Getting the timing just right to consistently use this effect can be very tricky, though.

Code in a remote processor accrues execution quota when waiting for a remote call. Code in a main processor accrues execution quota when waiting for the return from a remote call during synchronous calls and when using the `await()` function on asynchronous calls.

Note: the execution quota only increases when `wait` instruction are being executed. If there is a background process defined, no execution quota accrues while it is being run (unless the background process itself contains some `wait` instructions).

# Arbitrary remote variable access

## Arbitrary access to remote variables

To access arbitrary remote processor variables using the `read` and `write` instructions., `read(variable)`  and `write(value, variable)` methods must be used, to be called on processors:

```Mindcode
#set target = 8;
print(processor1.read("foo"));
processor2.write(@coal, "bar");
```

produces

```mlog
read *tmp0 processor1 "foo"
print *tmp0
write @coal processor2 "bar"
```

> [!NOTE] 
> The variable names need to be specified as they appear in mlog. This feature is therefore meant for interfacing with processors containing mlog code not compiled by Mindcode. To access Mindcode variables, the `remote` variable declaration is preferred, as it automatically uses the mlog variable name generated by Mindcode.     

## Remote variables with storage specification

Sometimes you might want to use variables in remote processors which weren't declared in a module, possibly because the other processor's code was not compiled by Mindcode. Another use case is for the module to access the main processor's variables directly (although in this case, a reference to the main processor must be made available to the module in some way).

To specify where the remote variable is located and even what name it uses, include a _storage specification_ in the remote variable declaration:

```
remote <processor> [(mlog_name)] [var] <variable1>;
```

The _storage specification_ is included after the `remote` keyword. The storage clause consist of the name of the remote processor block (e.g., `processor1`, or a variable), and optionally an mlog name as a constant string expression, enclosed in parentheses. When the mlog name is not specified, it is derived from the name of the variable being declared this way using Mindcode's convention. All variables created this way are stored in the given remote processor.

Note: the storage specification is very similar to the [storage specification of external variables](SYNTAX-1-VARIABLES.markdown#external-variables).

The following restrictions apply to remote variables declared with storage specification:
* Such variables must be declared in global scope and are therefore always global.
* The declarations may appear either in a module or in a program. When declared in a remote module, these variables are local to the module and aren't made available in the scope of the program or module which requires the remote module.
* No initial value can be specified for these variables, as it is expected that it is the remote processor that will assign its variables an initial value.
* When the mlog name is present, only one variable can be specified per declaration (as otherwise multiple variables would share the same mlog name and would be redundant).
* Declaring remote arrays with storage specification is not supported.

```Mindcode
#set target = 8;
#set remarks = comments;

/// Remote variables named using Mindcode's naming convention
remote processor1 x, y;         
print(x, y);

/// Remote variable named using the specified name
remote processor1("foo") z;       
print(z);
```

compiles to:

```mlog
# Remote variables named using Mindcode's naming convention
read *tmp0 processor1 ".x"
print *tmp0
read *tmp2 processor1 ".y"
print *tmp2
# Remote variable named using the specified name
read *tmp4 processor1 "foo"
print *tmp4
```

# Schemacode support for modules

All [string values](SCHEMACODE.markdown#string-value-definition) defined in a Schemacode source file are accessible through the `require` keyword as files. The name of the file must be the same as the identifier used to declare the string value, without any path or extension. This makes it possible to `require` source code stored in a string value as a remote module:

```
schematic
    name = "Parallel processing"

a-message1:
    @message            at  (0, 0)
    @micro-processor    at +(1, 0) processor links a-* end mindcode = Main end
a-processor1:
    @micro-processor    at +(1, 0) processor links b-* end mindcode = RemoteTest end
b-processor1:
    @micro-processor    at +(1, 0) processor mindcode = RemoteTest2 end
end

Main = """
    #set target = 8;
    require "RemoteTest" remote processor1;

    greeting = name(out another);
    println($"Hello, $greeting $another!");
    printflush(message1);
    """

RemoteTest = """
    #set target = 8;
    module RemoteTest;
    require "RemoteTest2" remote processor1;

    remote def name(out another)
        another = anotherName();
        return "Dolly";
    end;
    """

RemoteTest2 = """
    #set target = 8;
    module RemoteTest2;

    remote def anotherName()
        return "and Polly";
    end;
    """
```

---

[« Previous: Mindustry Logic 8](MINDUSTRY-8.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Schemacode »](SCHEMACODE.markdown)
