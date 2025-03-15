# Remote functions and variables

Mindcode supports accessing variables (including arrays) in linked processors and calling Mindcode functions in them. This functionality uses the Mindustry Logic 8 capability to read and write variables to/from other processors, including `@counter`, by name.

Calling functions stored in other processors is a feature which brings two main benefits to Mindcode:

1. Possibility to break a large program into several pieces stored in multiple processors, effectively surpassing the 1000 instruction limit per program.
2. Means to call remote functions asynchronously, resulting in parallel processing. 

## Architecture

Processor which accesses other processor's variables or functions is called _main processor_. The processor which contains these variables and functions is called _remote processor_. A main processor can use multiple remote processors, but each remote processor can be used by at most one main processor. Furthermore, a remote processor can act as a main processor to another remote processor. Processors can therefore be arranged into an oriented graph - a tree (in the sense of graph theory - the root is the main processor, which starts and controls the execution over the entire processor tree).

The execution of the entire distributed program starts with the topmost main processor (there's always just one), while the other remote processors wait until a remote call is initiated from their respective main processor. Both main processor and the remote processors wait until their respective linked processors are initialized.

To successfully run a distributed program in Mindustry World, all necessary processors with their code need to be built. The easiest way to do so is to create a schematics (using [Schemacode](SCHEMACODE.markdown)) containing all the processors, properly linked, and their code.

Code for each processor in a distributed system is compiled separately from a standalone source code. When using the command-line compiler, each source code would typically be stored in a separate file. It is also possible to include all processors and their code in a single Schematics file, which can be compiled by the web application.

A remote processor can act as a main processor to another remote processor. Processors can therefore be arranged into an oriented graph - a tree (in the sense of graph theory - the root is the main processor, which starts and controls the execution over the entire processor tree).

## Remote processors code

Code belonging to a remote processor must be placed in a module. A module is created by using the `module` keyword. Modules must be named, although at this moment there is no use for the name:

```
module library;
```

When compiling a module, the compiler generates initialization code. This code creates variables holding address of each remote function, named `:functionName*address`. Furthermore, `*mainProcessor` variable is initialized to `@this`. After executing the initialization, the code enters an endless loop, awaiting remote function call.

If the module contains any code outside functions (i.e. a main code block), this code is executed as part of the initialization of the remote processor. Only when this code completes the processor is ready to accept remote calls. Main processor's initialization waits for all remote processor initializations to complete, so it is crucial for the initialization code to be fast. If the initialization code contains an endless loop, the main processor execution will be blocked forever.

Note: there's no specific designation of remote modules. Any module may contain remote functions/variables. When such a module is imported locally through plain `require`, the remote variables are accessible locally, but the remote functions cannot be called.

### Remote variables

It is possible to declare a remote variable or array in a module using the `remote` keyword:

```
[noinit] [volatile] remote [var] <variable1> [= <initial value>];
[volatile] remote [var] <variable1>'['size']' [= (<initial values>)] [, <variable2> [= (<initial values>)] ... ];
```

Remote variables must be declared in global scope and are therefore always global. When an initial value is provided, it is assigned to the variable at the moment of the declaration. Any expression can be used as the initial value, including function calls.

When declaring remote variables these additional modifiers can be used:

* `noinit`: this modifier suppresses the "uninitialized variable" warning for the declared variable. This modifier cannot be used if the variable is assigned an initial value.
* `volatile`: remote variables are volatile by default, because they may be read or written by other processors. It is possible to specify the `volatile` modifier explicitly for better clarity. 

Modifiers can be specified in any order.

```Mindcode
#set target = 8;

// Remote variables, a unitialized, b initialized
remote a, b = 10;         

// Remote arrays: initialized and uninitialized
remote array1[10];
remote array2[5] = (1, 2, 3, 4, 5);
```

Remote variables are available through remote access in code which imports the module. Remote variables/arrays may or may not be initialized. When some remote variables or array elements are unused within the module (and the corresponding mlog variables would therefore not be created when parsing the code by the logic processor), the missing variables are explicitly created using `draw triangle` instruction(s), so that they may be accessed remotely. These instructions are placed at the end of the generated code and are never executed.

### Remote functions

Function to be called remotely must be declared using the `remote` keyword again:

```Mindcode
#set target = 8;

remote def foo(x, y, out z)
    z = x + y;
    return x * y;
end;
```

Function parameters may be input or output (as usual). A remote function is never inlined and can be declared neither `inline` nor `noinline`. Varags aren't supported. Remote function cannot be overloaded, although it is possible to overload a remote function with non-remote ones. Remote functions must not be called from the module which declares them, meaning they also can't be recursive. It is possible to call a recursive function from within a remote function, though.

All remote functions are active entry points for the compiler and thus are always included in the compiled code.

### Background process

When waiting for a remote call, the remote processor is idle. It is possible to define a function to be executed while waiting for a remote call. This function is executed after the remote module initializes, and subsequently whenever a remote call finishes. If the background function ever terminates (or if it is not defined at all), a `wait` instruction is used to wait for the next remote call.

When a remote call occurs while the background function still executes, the background process is terminated and the remote function starts executing immediately. There's no cleanup process. It is up to the programmer to make sure the background function can be safely terminated at any time and reentered.

The background process must be contained in a function named `backgroundProcess`. The function declaration needs to have these properties:

1. No `remote`, `inline` or `noinline` keywords used in function declaration.
2. Return type of the function is `void`.
3. The function has no parameters.
4. The function is not recursive.

If a `backgroundProcess` function with these properties is found in the source code, it is used as the background process. If no such function exists, no background process is created.

It is possible to call the `backgroundProcess()` function explicitly. In this case, the function is compiled normally and may even be inlined.

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
set .invocations -1
set :foo*address 9
set *mainProcessor @this
op add .invocations .invocations 1
print "Number of method calls: "
print .invocations
printflush message1
wait 1e12
jump 3 always 0 0
op mul :foo:b 2 :foo:a
write :foo:b *mainProcessor ":foo:b"
write true *mainProcessor ":foo*finished"
jump 3 always 0 0
```

## Main processor code

All remote modules must be declared in the main processor code using the `require ... remote` statement. This declaration specifies which remote processor contains remote functions:

```
require "library.mnd" remote processor1;
```

The required file (in this example stored in `library.mnd` file) must be a module, and the compiled code of this module must be stored in processor `processor1`. The processor can be specified using a linked block name, a parameter, or a variable. The variable needs to be initialized, e.g. by a function. 

When a module is being requested for remote access using two different processors, or is being requested both locally end remotely, a compilation error occurs.

Modules imported remotely aren't compiled into the code, but remote variables and functions declared in them are available to the main processor.

When at least one remote function is called from a processor, guard code for the processor is generated. The guard code waits for the processor to be created and its `*mainProcessor` variable initialized. When this state is reached, the following happens:

* The `*mainProcessor` variable in the remote processor is set to `@this`
* `:functionName*address` is copied from the remote processor to the local processor for each function called remotely.

### Remote variables

All variables declared `remote` in remotely requested modules are accessible in the main processor. It's not required to declare the variable in the main processor again.

An array declared `remote` in remotely imported modules is accessible in the main processor. The array elements are stored in the remote processor. Access to individual elements happens through the `read` and `write` instructions, not through remote calls. For random access, jump tables are generated in the main processor code, meaning that random access to remote arrays has he same performance as random access to local arrays. Direct access to remote array elements gets resolved to remote variable access, which is as fast as external variable access (except it supports non-numerical values).

### Synchronous remote calls

The syntax to call functions remotely is the same as for local function call:

```
product = foo(10, 20, out sum);
```

Execution of the code is paused until the remote function completes and returns. The effect of calling a remote function is the same as calling local function: function return value and output variables are set up as usual.

### Asynchronous remote calls

When a remote function is called asynchronously, the main processor continues to execute code while the remote function computes. It is possible to determine whether the remote function has completed, and/or wait for its completion and receive return values. Multiple asynchronous remote calls may be active at the same time, provided each of them is executed on a different remote processor.

Asynchronous calls are started using the built-in `async()` function:

```
async(foo(10, 20));
```

The `out` modifiers are disallowed in asynchronous function calls, and output only arguments need to be completely omitted.

To check for completion, call the `finished()` function, passing the name of the remote function as an argument:

```
done = finished(foo);
```

The function returns true if the asynchronous call to the given remote function is finished.

Function return value may be obtained via the `await()` function (not to be confused with the `wait()` function mapped to the `wait` instruction):

```
product = await(foo);
sum = foo.z;
```

This call functions waits for `foo` completion, and then returns its return value. Other output parameters are available under their fully qualified names, e.g. `foo.z`. The output parameters may be queried as soon as `finished()` returned true. Calling `await()` is not necessary before starting another remote call on the same processor. 

## Mechanism of the remote calls

This chapter briefly describes how synchronous and asynchronous calls are implemented. 

### Local side - synchronous call

* The function parameters are set up in the remote processor.
* `:functionName*finished` is set to `false` in the main processor.
* `@counter` in the remote processor is set to `:functionName*address`.
* Waiting for call completion:
  * instruction `wait 1e-15` is executed. This skips the rest of the tick, while transferring unspent instruction execution quota to the next tick(s). 
  * loop while `:functionName*finished` equals `false`
* At this point, it is known that the remote processor has written the output values (`:functionName*retval`, plus all output parameters) to the variables in this processor. Values of output parameters are copied to output arguments (this step is removed by optimization when possible).

### Local side - asynchronous call

* The function parameters are set up in the remote processor.
* `:functionName*finished` is set to `false` in the main processor.
* `@counter` in the remote processor is set to `:functionName*address`.
* The code execution continues in the main processor.
 
The mechanisms to monitor the call, wait for completion and retrieve output values are implemented by the programmer using the `await()` and/or `finished()` functions.

### Remote side

The call mechanism on the remote side is the same for both synchronous and asynchronous calls.

* Waiting for the remote call is implemented using the `wait 1e12` instruction. This wait is over 30 thousands years long, so it never completes. The wait also transfers unspent instruction execution quota to the next tick(s).
* Remote call is initiated by the main processor remotely setting `@counter` to the start of the function. The function starts executing immediately, as all parameters have been set by the caller.
* Upon completion, the function performs these operations:
    * All output values (`:functionName*retval`, plus all output parameters) are  set up in the main processor.
    * `:functionName*finished` in the main processor is set to `true`
    * The waiting phase is reentered.

## Interrupting function calls

If the same or different remote function gets called while a previous call is active, the previous call is terminated. The corresponding `:functionName*finished` flag won't be set.

There's no way to define a cleanup routine in case a remote call is terminated, and Mindcode doesn't guard against possible corruption of the remote processor state.

It is also possible to start an asynchronous remote call and use remote variables to communicate with the remote function instead of waiting for it to finish. It's up to the programmer to devise a communication protocol for that. By calling the same (or different) function again, this remote processing could be restarted or terminated.    

## Performance of remote calls

Instructions in Mindustry Logic processors aren't executed in parallel. Instead, in each tick all processors are visited sequentially and a number of instructions corresponding to given processor's _instructions per tick_ rate is executed. This means that when a synchronous remote call is made, it is never finished earlier than on the next tick. Even if the call returned immediately, a synchronous remote call will take a full tick. A synchronous remote call consist of these steps:

1. Main processor sets up the parameters (if any) and sets the remote `@counter` to the function address.
2. Remote processor executes the function, sets up output values in the main processor and sets the `finished` flag. 
3. Main processor reads the `finished` flag, finds out the call has ended and continues executing its code.

It is possible that each of these steps fits into one tick. In this case, either steps 1 and 2 get executed in the same tick, or steps 2 and 3 get executed in the same tick. Depending on processor speed and the complexity of the function and its parameters, some of these steps may take longer than one tick, but even if the code is very short, or the processor very fast, the performance penalty is always at least a tick. Performing many short calls to remote functions might therefore very detrimental to the performance of your program.  

> [!IMPORTANT]
> The faster the processor, the more instructions get executed in a single tick, and the bigger impact it has. Unboosted micro-processor executes 2 instructions per tick, and the overhead of even short remote calls is almost negligible (at most one instruction execution will be lost on each call). Fully boosted hyper-processor executes 62.5 instructions per seconds, and frequent short remote calls could reduce the effectivity of the hyper-processor by 90% or more. World processors with instruction rates at several hundred IPTs could be affected even more.
> 
> Additional consideration is frame rate. Processor updates aren't actually driven by ticks, but by frame updates. A frame rate higher than 60 FPS means there are fewer instructions wasted per remote call, while lower frame rates mean more instructions wasted. Even code which performs well under 60 FPS might develop performance problems when the frame rate drops.

### Managing the execution quota

Code generated by Mindcode utilizes the `wait` instruction when waiting for a remote procedure call to finish, and when the remote module waits for another call to serve. The `wait` instruction terminates the processor's execution in the current frame, saving up unspent execution quora. Up to four ticks of unspent instructions can be saved this way. When normal execution resumes, the unspent quota is spread over several ticks to provide a boost to the processor execution (a half of the remaining unspent execution quota is used up in each subsequent tick which doesn't contain `wait` or another yielding instruction - the others are `stop` and `message`).

When the interval between remote calls is longer than a single tick, and the duration of the remote calls is also longer than a single tick, synchronous remote calls might even provide execution boosts, leading to faster execution than running the code on a single processor. Getting the timing "just right" can be very tricky, though.  

Code in remote processors accrues execution quota when waiting for next remote call. Code in main processor accrues execution quota when waiting for the return from a remote call during synchronous calls and when using the `await()` function on asynchronous calls.

Note: the execution quota only accrues when `wait` instruction are being executed. If there is a background process defined, no execution quota accrues while it is being run (unless the background process contains some `wait` instructions placed there by the programmer).     

# Arbitrary remote variable access

To access arbitrary remote processor variables using the `read` and `write` instructions., `read(variable)`  and `write(value, variable)` methods must be used, to be called on processors.

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
