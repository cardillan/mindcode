# Recursive Optimization

This optimization targets recursive functions. Two basic optimizations are applied.

## Return optimization

This optimization is a [dynamic optimization](../SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations) and is only applied when compatible with the optimization goal.

Returning from a recursive function requires two instructions (namely, updating the stack pointer and jumping to the return address obtained from the stack). Returns from instructions are normally implemented as jumps to the return sequence at the end of the function. This optimization replaces these jumps with the full return sequence, saving one instruction execution at the price of increasing the code size by one instruction.  

## Tail call optimization

Tail call optimization can be applied to a recursive function which contains a call to itself as the last statement in the function body. This last function call - the tail call - is replaced by a jump at the beginning of the function body, essentially turning the function into a loop. When no other recursive calls are present inside the function, the function becomes a stackless (non-recursive) one.

The tail call optimization is a static optimization and is applied whenever possible. In all but the simplest cases, the optimization relies on other optimizers to simplify the function body and actually make the function call the very last executable statement in the function body (specifically, [Stack Optimization](STACK-OPTIMIZATION.markdown) is essential).

The tail call optimization can only be applied to simply recursive calls (i.e., a function calling itself). Function inlining can turn a group of mutually recursive functions into a simply recursive function, making it possible to apply the tail call optimization even in this case.   

Example:

```Mindcode
#set symbolic-labels = true;
allocate stack in bank1;

def f(n)
    println("f: ", n);
    g(n - 1);
end;

def g(n)
    if n > 0 then
        println("g: ", n);
        f(n - 1);
    end;
end;

f(10);
g(10);
```

compiles to

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    set *sp 0
        # Function: def f(in n)
        print "f: 10\n"
        set :g:n 9
        op add :g*retaddr @counter 1
        jump label_11 always 0 0
        # Function: def g(in n)
            print "g: 10\nf: 9\n"
            op sub *tmp0 10 1
                # Function: def f(in n)
                op sub :g:n 10 2
                op add :g*retaddr @counter 1
                jump label_11 always 0 0
end
            # Function: def g(in n)
label_11:
            jump label_18 lessThanEq :g:n 0
                print "g: {0}\nf: {0}\n"
                format :g:n
                op sub *tmp0 :g:n 1
                    # Function: def f(in n)
                    format *tmp0
                    op sub :g:n :g:n 2
        jump label_11 always 0 0
label_18:
    set @counter :g*retaddr
```

---

[&#xAB; Previous: Print Merging](PRINT-MERGING.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Single Step Elimination &#xBB;](SINGLE-STEP-ELIMINATION.markdown)
