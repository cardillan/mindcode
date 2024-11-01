# Strict and Relaxed syntax in Mindcode

Since the inception of Mindcode, the goal was to minimize changes to the syntax which would break existing code. Unfortunately, the original syntax has serious drawbacks. Fixing these issues requires substantial changes to the existing syntax, meaning the existing Mindcode needs to be updated.

To make the transition less burdensome, two versions of the syntax will be supported for a limited time. The new syntax, being stricter than the original one, is called **Strict**, while the old one is called **Relaxed**. The default syntax, both in the web application and the command-line compiler, is Strict. To compile a code under the Relaxed syntax, add a `#relaxed;` directive into your source code:

```
#relaxed;                               // Switch to relaxed syntax

const RADIUS_WITHIN     = 8
const RADIUS_APPROACH   = 6

const SUPPLY_INTERVAL   = 50 - 3        // Some comment

const UNIT_CHECK_TIME   = 5000          // Some comment

DOME = dome1
while DOME == null
    print("[gold]Waiting for an overdrive dome to be connected...")
    printflush(message1)
    DOME = dome1
end
```

<small>([Try it](http://mindcode.herokuapp.com/?mindcode=%23relaxed%3B%0A%0Aconst%20RADIUS_WITHIN%20%20%20%20%20%3D%208%0Aconst%20RADIUS_APPROACH%20%20%20%3D%206%0A%0Aconst%20SUPPLY_INTERVAL%20%20%20%3D%2050%20-%203%0A%0Aconst%20UNIT_CHECK_TIME%20%20%20%3D%205000%0A%0ADOME%20%3D%20dome1%0Awhile%20DOME%20%3D%3D%20null%0A%20%20%20%20print(%22%5Bgold%5DWaiting%20for%20an%20overdrive%20dome%20to%20be%20connected...%22)%0A%20%20%20%20printflush(message1)%0A%20%20%20%20DOME%20%3D%20dome1%0Aend))</small>

Different, more prominent means for switching to the relaxed syntax were intentionally not provided, to encourage adoption of the new syntax by both new and existing users.

Additionally, some features are deprecate and support for them will be removed in future versions. Using deprecated features generates a warning. Deprecated features occurring in the source code should be removed or replaced before they're removed.  

## Differences between the Strict and Relaxed syntax

Under the Strict syntax, the following additional rules apply:

- Semicolons at the end of each statement or expression are compulsory.
- `then` keywords are compulsory in `if` statements and in the `when` branches of `case` expressions.
- `do` keywords are compulsory in all `for` and `while` loops.
- In function declarations, parentheses are compulsory even when the function doesn't have any parameters (`def foo print("no parameters"); end;` is not allowed in strict syntax).

If adapting to the new syntax brings unexpected challenges, you can try to have a look at some [troubleshooting tips](TROUBLESHOOTING.markdown).

## Deprecated features

The following features are deprecated:

- The **Relaxed** syntax as a whole.
- Using parentheses around the list of values in list iteration loops: `for i in (1, 2, 3) do ... end;`.
- Using escaped double quotes in string literals: `"The \"escaped double quotes\""`. String literals shouldn't contain any double quotes at all, as this is not supported by mlog itself.
- Kebab-case identifiers: `kebab-case-name`. Note that built-in mlog variables, such as `@blast-compound`, will continue to be supported.
- The `printf` function in language target `ML7A` and earlier. (This function is repurposed in the upcoming language target `ML8A`).
- The `configure` property. This name from Mindustry Logic 6 was replaced by `config` in Mindustry Logic 7.
- Not using `out` modifiers when passing arguments to output function parameters.
- Not using the `@` prefix for Mindustry built-in variables used as properties (`vault1.coal`). 
- Using void functions in assignments, as function parameters or in `return` statements.

The compiler produces a warning when it encounters a deprecated feature in the source code.

## Shortcomings of the Relaxed syntax

The most impactful problem of the original syntax stem from the decision to make semicolons separating individual statements and expressions optional. That, coupled with the fact the basic element of the language is an expression instead of a statement, leads to situation where a code entered by a user compiles without any error or warning, but produces vastly different result from what was intended. For example, the following code

```
set count = 10
for i in 1 .. count
    println i    
end
pritflush message1
```

produced this output under relaxed syntax:

```
set count 10
end
```

That doesn't inspire much confidence in Mindcode. Apart from large chunks of the code being apparently completely ignored, it also promulgates the (mistaken) idea that the proper syntax for the assignment statement features a `set` keyword.

The problem here is that, in the absence of semicolons unequivocally delimiting individual expressions, the compiler may break code into expressions at places that weren't intended by the user. In the short example above the compiler splits the code into these individual expressions and statements:

```
set;
count = 10;
for i in 1 .. count do
    println;
    i;
end;
pritflush;
message1;
```

`set`, `println`, `printflush` and `message1` here are interpreted as expressions comprised of a single variable, which is allowed in Mindcode. Since these expressions just represent the values of the variables, but aren't assigned to another variable or passed to a function, they do nothing and are removed from the code without even producing any warning.
