# Mindcode

> Mindcode is a high-level programming language that compiles to Mindustry Logic (mlog) instructions. It is not a superset of mlog, and its syntax and semantics differ from those of native Mindustry Logic.

Mindcode provides a more expressive programming environment than native Mindustry Logic, including structured control flow, functions, variables, arrays, and compile-time optimizations.

## Quick reference

- Mindcode is a high-level language that compiles to Mindustry Logic (mlog).
- Keywords and identifiers are case-sensitive.
- Relaxed syntax is the default.
- Variables can be explicitly declared; relaxed syntax also permits undeclared variables.
- Global variables and constants must be declared before their first use.
- The default optimization level is `experimental`.
- The default target is Mindustry 8, build 160.5 or later.
- Mlog keywords use a `:` prefix in Mindcode and cannot be stored in variables.

## Document scope

Features introduced in particular Mindustry or Mindcode versions are explicitly identified.

## Mindcode Language Reference

This document is the authoritative reference for the Mindcode programming language.

Unless stated otherwise, examples assume:
- Mindcode version: 3.21.0
- Mindustry version: v8 build 160.5
- Default compiler settings; specifically:
    - Target: 8.2m
    - Syntax mode: relaxed
    - Optimization level: experimental

When generating Mindcode source code:
- Follow the syntax and semantics described here.
- Do not assume features from other programming languages are available in Mindcode.
- Respect Mindustry target-version restrictions.
- Prefer the documented Mindcode constructs over manually writing equivalent Mindustry Logic instructions.
- Do not invent functions, variables, or language features that aren't documented.
