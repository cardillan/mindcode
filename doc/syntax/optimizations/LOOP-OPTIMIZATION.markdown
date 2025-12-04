# Loop Optimization

The loop optimization improves loops with the condition at the beginning by performing these modifications:

* If the loop jump condition is invertible, the unconditional jump at the end of the loop to the loop condition is replaced by a conditional jump with an inverted loop condition targeting the first instruction of the loop body. This doesn't affect the number of instructions but executes one less instruction per loop.
    * If the loop condition isn't invertible (that is, the jump condition is `===`), the optimization isn't done, since the saved jump would be spent on inverting the condition, and the code size would increase for no benefit at all.
* If the previous optimization was done and the loop condition is known to be true before the first iteration of the loop, the optimizer removes the jump at the front of the loop. The Loop Optimizer uses information gathered by Data Flow Optimization to evaluate the initial loop condition.
* Loop conditions that are complex expressions spanning several instructions can still be replicated at the end of the loop, if the code generation goal is set to `speed` (the default setting at the moment). As a result, the code size might actually increase after performing this kind of optimization. See [Dynamic optimizations](../SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations) for details on performing these optimizations.

The result of the first two optimizations in the list can be seen here:

```Mindcode
param LIMIT = 10;
for i in 0 ... LIMIT do
    cell1[i] = 1;
end;
print("Done.");
```

produces

```mlog
set LIMIT 10
set :i 0
jump 6 greaterThanEq 0 LIMIT
write 1 cell1 :i
op add :i :i 1
jump 3 lessThan :i LIMIT
print "Done."
```

Executing the entire loop (including the `i` variable initialization) takes 32 steps. Without optimization, the loop would require 43 steps. That's quite a significant difference, especially for tight loops.

The third modification is demonstrated here:

```Mindcode
while switch1.enabled and switch2.enabled do
    print("Doing something.");
end;
print("A switch has been reset.");
```

which produces:

```mlog
sensor *tmp0 switch1 @enabled
jump 6 equal *tmp0 false
sensor *tmp1 switch2 @enabled
jump 6 equal *tmp1 false
print "Doing something."
jump 0 always 0 0
print "A switch has been reset."
```

---

[&#xAB; Previous: Loop Hoisting](LOOP-HOISTING.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Loop Unrolling &#xBB;](LOOP-UNROLLING.markdown)
