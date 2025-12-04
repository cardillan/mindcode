# Jump Straightening

This optimization detects situations where a conditional jump skips a following, unconditional one and replaces it with a single conditional jump with a reversed condition and a target of the second jump. Example:

```
jump *label0 equal *tmp9 false
jump *label1
label *label0
```

will be turned to

```
jump *label1 notEqual *tmp9 false
```

Optimization won't be done if the condition does not have an inverse condition (`strictEqual`).

These sequences of instructions may arise when using the `break` or `continue` statements:

```
while true do
    ...
    if not_alive then
        break;
    end;
end;
```

---

[&#xAB; Previous: Jump Normalization](JUMP-NORMALIZATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Jump Threading &#xBB;](JUMP-THREADING.markdown)
