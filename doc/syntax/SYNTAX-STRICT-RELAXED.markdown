# Strict and relaxed syntax in Mindcode

Since the inception of Mindcode, we strived to minimize changing the syntax in a way that would break existing code. Unfortunately, the original syntax has serious drawbacks. The most important one was the decision to make semicolons separating individual statements and expressions optional. That, coupled with the fact the basic element of the language is an expression, and not a statement, leads to situation where a code entered by a user compiles, but produces vastly different result from what was intended. For example, the following code () compiled:

```
set count = 10
for i in 1 .. count
    println i    
end
pritflush message1
```

and produced this output:

```
set count 10
end
```

That doesn't inspire much confidence in Mindcode.

The problem here is that, in the absence of semicolons unequivocally delimiting individual expressions, the compiler may break code into expressions at places that weren't intended by the user. In this short example the compiler splits the code into these individual expressions and statements:

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

`set`, `println`, `printflush` and `message1` are interpreted as expressions comprised of a single variable, which is allowed in Mindcode. Since these expressions just represent the values of the variables, but aren't assigned to another variable or passed to a function, they do nothing and are removed from the code without even producing any warning. (Technically, even `print(message1)` could be parsed by the compiler as `print; (message1);`, but fortunately Mindcode doesn't do that.) This ambiguity represents a serious drawback of the language.

There are probably several possible approaches to address this problem. Ensuring the expressions are always delimited exactly as the user intended has many advantages, though: it removes a lot of ambiguities from the syntax, making the parser faster, the error reporting more precise and the code conveying better the true intent of its author. This means that the semicolons at the end of each statement, as well as `do` keywords in the `for` and `while` loops and `then` keywords in `if` and `case` expressions become compulsory.

This is a substantial change to the existing syntax which will break existing code at many places. While updating even larger programs for the new syntax shouldn't take more than five to ten minutes, it can still be burdensome. To alleviate some pain brought up by the transition, two versions of Mindcode syntax will be supported for some time: **strict** and **relaxed**.

The default syntax, both in the web application and the command-line compiler, is strict. The only possibility to switch to the relaxed syntax is by placing the `#relaxed;` directive into the source code. Different, more prominent means for switching to the relaxed syntax were intentionally not provided, to encourage new users of the web application to use the syntax which doesn't produce the horrible results demonstrated above, and old users to start writing any new code using the strict syntax too. 