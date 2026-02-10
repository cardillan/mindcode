# Array Optimization

The array optimization improves the performance of array operations in several ways. Some array optimizations are [dynamic optimizations](../SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations) and are only applied when compatible with the optimization goal. Many of these array optimizations require target 8.0 or 8.1, as noted in the corresponding sections.

**Types of arrays**

Mindcode supports the following types of arrays:

* **Internal arrays:** backed by processor variables, and index-based access is provided by the compiler.
* **Remote arrays:** backed by variables residing in a remote processor. Index-based access is provided by the compiler on the current processor.
* **Multiplexed arrays:** similar to remote arrays, but several copies of these arrays may exist in different processors. Index-based access is provided by the compiler on the current processor.
* **Constant arrays:** individual elements of the array are constant. The compiler implements index-based read-only access.
* **External arrays:** backed by a memory bank or a memory cell. Index-based access is provided directly by mlog instructions (`read` and `write`).

**Array Implementations**

The basic mechanism for providing index-based access to array elements is known as [`@counter` arrays](https://yrueii.github.io/MlogDocs/#counter-array): a separate branch for accessing each variable is created in mlog, and the array access is implemented by jumping to the desired branch using the element index and `@counter` manipulation.

> [!NOTE]
> To distinguish arrays as a language feature from the `@counter` arrays implementation in mlog, here we call a set of branches implementing an array a _`@counter` table_, or simply a _table_.

A branch in a table consists of an access instruction, which ensures reading or writing of the variable corresponding to the array element, and a jump to the place where the program execution should continue. This makes internal arrays rather expensive in terms of code size.

Mindcode supports several implementations of `@counter` tables. Some require specific Mindustry processor capabilities and are therefore only available when compiling the code for a specific target. This optimization tries to choose the most efficient implementation for the given array type, the way it is used in the program, and the target.

Individual `@counter` table implementations are described here.

## Regular tables

Available for every target. Generally, two tables are needed: one for reading, one for writing. When index-based access is not used to read or write a given array, the corresponding table is not created. A table supporting a regular array may be used by several separate places in the code (similarly to an out-of-line function).

All regular tables take up twice as many instructions as there are elements in the array. As two regular tables are needed for an array in the general case, four instructions are generated per a single array element just for the tables.

When the program needs to read or write an array element at a given index, a call into the table is made. For this, the address of the branch is computed, and the value of the element variable is either read into a transfer variable or updated from a transfer variable. The call sets up several arguments: the index to be accessed, the transfer variable (either as input or as output), the return address as an implicit argument, and, in case of multiplexed arrays, the actual processor being accessed. Several instructions are therefore executed to access an array element.

Constant arrays are also implemented using regular tables. Here, the table provides the (constant) element value directly, and since the array cannot be modified, no table for writes is required or generated.

## Compact tables

Available for targets `8.0` and higher. Implemented as a single table which provides the name of the variable corresponding to a given array element. The `read` and `write` instructions are then used to access the variable. The compact array can be used with both internal, remote, and multiplexed arrays. It is equally efficient for all these types of arrays, as the array element is read or written indirectly in all three cases using a single additional instruction. Due to this additional instruction requirement, the compact table is in most cases less efficient than a regular one.

However, if an array element at the same index is accessed multiple times (e.g., a read-update-write pattern such as `a[i]++`), Mindcode might be able to recognize this and for the second and later accesses use the name of the variable obtained through the table for the first access, providing considerable savings in both code-size and execution time. In these cases, a compact array is more efficient than a regular array and is preferred by the optimizer.

Since a compact table doesn't access the array element variables directly, but accesses them through their mlog names, the variables aren't created in the processor by the table itself (unlike regular arrays). Array elements which aren't accessed directly by the program must be explicitly created by a `draw triangle` instruction. Up to six variables can be created by a single instruction, meaning that an array implemented as a compact array may require approx. 2.17 instructions per array element for tables.

## Inlined tables

Initially, the compiler generates only one copy of a table for a given array, which is then used as an out-of-line function wherever index-based access is required. However, calls to `@counter` tables can be inlined in the same way as calls to out-of-line functions. This means the table is duplicated at the call site, increasing the code size, but all parameters to the call are inlined into the local table, saving one or more instruction executions. When all calls to a specific table get inlined, the original table is removed from the program.

Inlining is possible for all types of tables (compact and regular, folded and unfolded). As compact and regular tables have the same space requirements when inlined, the compact representation is only used for repeated access to the same array element, where it is more efficient both space-wise and execution-wise. Each inlined table is evaluated independently.

## Folded tables

Available for targets `8.1` and higher. Individual branches of the table serve two elements from the array (the proper one is chosen through `select` from the lower or upper half of the array, based on the index).

Folded tables can only support literals or mlog variables. Therefore, they can be used for compact tables or for reading regular tables of local arrays, including constant arrays. For remote arrays and writing tables, folding is not possible.

Folding requires an additional step to choose the correct branch of the table and is therefore always slower than an unfolded table (only when [text-based table dispatch](#text-based-table-dispatch) is supported, this additional step can be removed for inlined tables). However, folding reduces the table size in half. Since a folded compact table serves both reads and writes, it takes only one instruction per array element (plus one-sixth of instruction for `draw triangle` element variable creation). This means arrays of 600 or 700 elements are possible while still providing some space for actual logic.

## Short arrays

For short arrays, the tables can be avoided entirely:
* When the array has just one element, all index-based array access is resolved to that one element directly.
* For other short arrays up to three or four elements, the array access is replaced with a sequence of `select`s or if/else statements, depending on whether the `select` instruction is available in the given target (target `8.1` or higher is required for a `select` instruction).

This replacement is available for both compact and regular tables. The compact version is used when the same element is accessed multiple times, similarly to inlined arrays, and for remote arrays.

**Array access code size**

| Array size | Read<br>select | Write<br>select | Compact<br>select | Read<br>if | Write<br>if | Compact<br>if |
|:----------:|:--------------:|:---------------:|:-----------------:|:----------:|:-----------:|:-------------:|
|     2      |       1        |        2        |         2         |     4      |      4      |       5       |
|     3      |       2        |        3        |         3         |     7      |      7      |       8       |
|     4      |       3        |        4        |         4         |     -      |      -      |       -       |

**Instructions executed**

| Array size | Read<br>select | Write<br>select | Compact<br>select | Read<br>if | Write<br>if | Compact<br>if |
|:----------:|:--------------:|:---------------:|:-----------------:|:----------:|:-----------:|:-------------:|
|     2      |       1        |        2        |         2         |    2.5     |     2.5     |      3.5      |
|     3      |       2        |        3        |         3         |    3.33    |    3.33     |     4.33      |
|     4      |       3        |        4        |         4         |     -      |      -      |       -       |

### Using an if / else statement

When a `select` instruction is not available, access to elements of arrays of length 2 is replaced with if/else statements:

* `a[x] = b;` gets converted to `if x == 0 then a[0] = b; else a[1] = b; end;`.
* `b = a[x]` gets converted to  `if x == 0 then b = a[0]; else b = a[1]; end;`.

For arrays of length 3, this code is used:

* `a[x] = b;` gets converted to `if x == 0 then a[0] = b; elsif x == 1 then a[1] = b; else a[2] = b end;`.
* `b = a[x]` gets converted to  `if x == 0 then b = a[0]; elsif x == 1 then b = a[1]; else b = a[2] end;`.

This optimization allows additional [If Expression optimizations](IF-EXPRESSION-OPTIMIZATION.markdown) to take place.

### Using a `select` instruction

When a `select` instruction is available - in target `8.1` or higher, `select` instructions are used to replace array access.

For read access, including compact tables, the `select` instruction is used to access the array element variable.

Two-element arrays:

```
select result index equal 0 element*0 element*1
```

Three-element arrays:

```
select result index equal 0 element*0 element*1
select result index equal 2 element*2 result
```

Four-element arrays:

```
select *tmp1 index equal 0 element*0 element*1
select *tmp2 index equal 2 element*2 element*3
select result index lessThan 2 *tmp1 *tmp2
```

Write access is realized by a linear sequence of `select` instructions, where each instruction matches an array element and only updates it when the index matches:

```
select element*0 index equal 0 value element*0
select element*1 index equal 1 value element*1
select element*2 index equal 2 value element*2
 etc
```

## Lookup arrays

Mindustry 8 provides a limited ability to convert indexes to variable names without tables: first, a `lookup` instruction is used to convert the element index to a Mindustry content object, and then `sensor @name` is used to get the name of the object as a string, producing the name of the element's variable. The variable is then accessed using the `read` or `write` instruction, as is the case with compact tables -- the name lookup can be likewise skipped for repeated access at the same index.

Lookup arrays provide enormous savings of both code size and execution time for large arrays: each access takes either one or three instructions, and no table is generated. However, using lookup arrays also imposes some restrictions:

* The compilation target needs to be set to `8.0` or higher.
* Each lookup array needs a specific type of Mindustry content. Therefore, at most five arrays can be used in a single program (using blocks, units, items, liquids, and teams), and for most content types, the array size is quite limited.
* Array elements are named after Mindustry content. This has two disadvantages:
    * inspecting variable names in a Mindustry processor while debugging makes it much harder to figure out which variable represents which array element,
    * when a variable is declared using a specific mlog name (e.g. `mlog("coal") var foo`), the name becomes unavailable for lookup arrays, restricting or preventing the use of the corresponding mindustry content type for lookup arrays.
* Remote arrays can only be implemented as lookup arrays when explicitly declared as such, since the names of a remote array's elements must be exactly the same in all processors and can't be reassigned by the optimizer.

The following Mindustry content types can be used for lookup arrays:

| Content type | Capacity |
|--------------|---------:|
| block        |      260 |
| unit         |       56 |
| item         |       20 |
| liquid       |       11 |     
| team         |        7 |     

If a declared variable conflicts with a content name, the capacity of the corresponding content type is shortened to contain only the elements up to the first conflicting element found.

When choosing which arrays to implement using lookup, Mindcode chooses the largest array that can be converted to a lookup array given the not yet allocated content types and their available capacities. If there are two or more such arrays of the same size, the array with the largest total weight of its instructions is used.

Example of a lookup array:

```Mindcode
#set target = 8m;
#set syntax = strict;

const SIZE = 10;
param LIMIT = 100;

var array[SIZE];

begin
    for var out a in array do a = 0; end;

    for var i in 0 ... LIMIT do
        countDigits(i);
    end;

    print(array[0]);
    for var i in 1 ... SIZE do
        print("-", array[i]);
    end;
    printflush(message1);
end;

void countDigits(number)
    do
        array[number % 10]++;
        number \= 10;
    while number > 0;
end;
```

compiles to:

```mlog
set LIMIT 100
set water 0
set slag 0
set oil 0
set cryofluid 0
set neoplasm 0
set hydrogen 0
set ozone 0
set cyanogen 0
set gallium 0
set nitrogen 0
set :i 0
jump 24 greaterThanEq 0 LIMIT
set :countDigits:number :i
op mod *tmp2 :countDigits:number 10
lookup liquid *tmp9 *tmp2
sensor .array*elem *tmp9 @name
read *tmp3 @this .array*elem
op add *tmp3 *tmp3 1
write *tmp3 @this .array*elem
op idiv :countDigits:number :countDigits:number 10
jump 14 greaterThan :countDigits:number 0
op add :i :i 1
jump 13 lessThan :i LIMIT
print water
print "-{0}-{0}-{0}-{0}-{0}-{0}-{0}-{0}-"
format slag
format oil
format cryofluid
format neoplasm
format hydrogen
format ozone
format cyanogen
format gallium
print nitrogen
printflush message1
```

## Text-based table dispatch

In Mindustry 8, it is possible to [read character values from a string](../MINDUSTRY-8.markdown#reading-characters-from-strings) at a given index in a single operation. This allows encoding instruction addresses into strings instead of computing the target address from the element index. The following prerequisites need to be met for this optimization to be applied:

* The [target](../SYNTAX-5-OTHER.markdown#option-target) must be set to version `8` or higher.
* The [symbolic labels](../SYNTAX-5-OTHER.markdown#option-symbolic-labels) option must be inactive.
* The [use-text-jump-tables](../SYNTAX-5-OTHER.markdown#option-use-text-jump-tables) option must be active.

When text-based table dispatch is possible, it is always used by the compiler, as it always performs better in terms of both code size and execution time, compared to alternatives. When used with inlined tables, text-based table dispatch even compensates for the disadvantage of folded tables, making them perform identically to unfolded tables while halving the table size.

**Reversing branch order**

There's no jump in the last branch of an inlined table, saving one instruction execution when accessing elements served by the last branch. When arranged in natural order, the last branch contains the last element for unfolded tables, and the last element plus the middle one for folded tables. However, if the array has an odd number of elements, the last branch of a folded table serves only one element of the array.

When text-based table dispatch is used, the branches can be arranged in any order. Therefore, branches in inlined tables are reversed with text-based table dispatch. This way, it is guaranteed that the last branch of a folded table will serve two elements (the first one and the middle one), and in the case of unfolded tables, it will serve the first element. This brings a small improvement to odd-sized arrays implemented by folded tables, and a possible improvement from putting the first array element on the best path; as the first element is accessed the most often by most algorithms, certainly more often than the last element.

## Code size and performance

The code size and performance of individual array accesses depend on a lot of factors:

* array implementation (regular or compact),
* array folding,
* whether the array is inlined,
* whether the array is remote,
* how/whether runtime bound checks are performed,
* whether text jump tables are used.

Runtime bound checks aren't included in the tables, as their size and execution time are independent of the array implementation and only depend on the compiler options.

**Legend for the tables below**

The code size and performance are described in tables. Column headers denote the factors that are considered, using this notation:

* table organization
    * `U`: unfolded
    * `M`: multiplexed unfolded (only shown when different from an unfolded table)
    * `F`: folded
* Dispatching method:
    * `T`: Text-based jump dispatching
    * `D`: Direct addressing
    * `S`: Symbolic labels

When expressing the code size, `n` represents the number of elements in the array. For odd-sized folded arrays, `n` is rounded up to the next even number.

The "setup index" instruction either multiplies the index by two or passes it as a shared table parameter unchanged, depending on how the table is built.

### Non-inlined compact tables

**Array access code size**

| Instruction            |  U/T  |  U/D  |  U/S  |  F/T  |  F/D  |  F/S  |
|------------------------|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|
| set return address     |   1   |   1   |       |   1   |   1   |       |
| setup index            |       |   1   |   1   |   1   |   1   |   1   |
| compute branch address |       |       |       |       |   1   |       |      
| call to branch/table   |   1   |   1   |   2   |   1   |   1   |   2   |      
| read/write element     |   1   |   1   |   1   |   1   |   1   |   1   |      
| **TOTAL**              | **3** | **4** | **4** | **4** | **5** | **4** |      

**Shared jump table code size**

| Instruction            |  U/T   |  U/D   |   U/S    |  F/T  |  F/D  |   F/S   |
|------------------------|:------:|:------:|:--------:|:-----:|:-----:|:-------:|
| compute branch address |        |        |          |       |       |    1    |
| jump to branch         |        |        |    1     |       |       |    1    |      
| branches (set+return)  |   2n   |   2n   |    2n    |   n   |   n   |    n    |      
| **TOTAL**              | **2n** | **2n** | **2n+1** | **n** | **n** | **n+2** |      

**Instructions executed**

| Instruction            |  U/T  |  U/D  |  U/S  |  F/T  |  F/D  |  F/S  |
|------------------------|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|
| set return address     |   1   |   1   |       |   1   |   1   |       |
| setup index            |       |   1   |   1   |   1   |   1   |   1   |
| compute branch address |       |       |       |       |   1   |       |      
| call to branch/table   |   1   |   1   |   2   |   1   |   1   |   2   |      
| compute branch address |       |       |       |       |       |   1   |
| jump to branch         |       |       |   1   |       |       |   1   |      
| branch (set+return)    |   2   |   2   |   2   |   2   |   2   |   2   |      
| read/write element     |   1   |   1   |   1   |   1   |   1   |   1   |      
| **TOTAL**              | **5** | **6** | **7** | **6** | **7** | **8** |      

### Inlined compact tables

**Array access code size**

| Instruction            |   U/T    |   U/D    |   U/S    |   F/T   |   F/D   |   F/S   |
|------------------------|:--------:|:--------:|:--------:|:-------:|:-------:|:-------:|
| setup index            |          |    1     |    1     |         |    1    |    1    |
| compute branch address |          |          |          |         |    1    |    1    |      
| jump to branch         |    1     |    1     |    1     |    1    |    1    |    1    |      
| branches (set+exit)    |   2n-1   |   2n-1   |   2n-1   |   n-1   |   n-1   |   n-1   |      
| read/write element     |    1     |    1     |    1     |    1    |    1    |    1    |      
| **TOTAL**              | **2n+1** | **2n+2** | **2n+2** | **n+1** | **n+3** | **n+3** |      

**Instructions executed**

| Instruction            |  U/T  |  U/D  |  U/S  |  F/T  |  F/D  |  F/S  |
|------------------------|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|
| setup index            |       |   1   |   1   |       |   1   |   1   |
| compute branch address |       |       |       |       |   1   |   1   |      
| jump to branch         |   1   |   1   |   1   |   1   |   1   |   1   |      
| branch (set+exit)      |   2   |   2   |   2   |   2   |   2   |   2   |      
| read/write element     |   1   |   1   |   1   |   1   |   1   |   1   |      
| **TOTAL**              | **4** | **5** | **5** | **4** | **6** | **6** |      

### Non-inlined regular tables

In the case of regular arrays, the table accessing the elements for reading may be folded if the array is not remote. Different shared jump tables exist for both read and write operations and are generated on demand. If both jump tables are generated, both count towards the size requirement.

**Array access code size**

| Instruction                         |  U/T  |  U/D  |  U/S  |  M/T  |  M/D  |  M/S  |  F/T  |  F/D  |  F/S  |
|-------------------------------------|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|
| set remote processor                |       |       |       |   1   |   1   |   1   |       |       |       |
| set return address                  |   1   |   1   |       |   1   |   1   |       |   1   |   1   |       |
| transfer write variable<sup>1</sup> |   1   |   1   |   1   |   1   |   1   |   1   |   1   |   1   |   1   |      
| setup index                         |       |   1   |   1   |       |   1   |   1   |   1   |   1   |   1   |
| compute branch address              |       |       |       |       |       |       |       |   1   |       |      
| call to branch/table                |   1   |   1   |   2   |   1   |   1   |   2   |   1   |   1   |   2   |      
| transfer read variable<sup>1</sup>  |   -   |   -   |   -   |   -   |   -   |   -   |   -   |   -   |   -   |      
| **TOTAL**                           | **3** | **4** | **4** | **4** | **5** | **5** | **4** | **5** | **4** |      

<sup>1</sup>&nbsp;Either the _transfer read variable_ or the _transfer write variable_ instruction is generated, depending on the access type. In this table, they're accounted for in the _transfer write variable_ row.

**Shared jump table code size**

| Instruction            |  U/T   |  U/D   |   U/S    |  M/T   |  M/D   |   M/S    |  F/T  |  F/D  |   F/S   |
|------------------------|:------:|:------:|:--------:|:------:|:------:|:--------:|:-----:|:-----:|:-------:|
| compute branch address |        |        |          |        |        |          |       |       |    1    |
| jump to branch         |        |        |    1     |        |        |    1     |       |       |    1    |      
| branches (set+return)  |   2n   |   2n   |    2n    |   2n   |   2n   |    2n    |   n   |   n   |    n    |      
| **TOTAL**              | **2n** | **2n** | **2n+1** | **2n** | **2n** | **2n+1** | **n** | **n** | **n+2** |      

**Instructions executed**

| Instruction                           |   U/T   |   U/D   |   U/S   |   M/T   |   M/D   |   M/S   |   F/T   |   F/D   |   F/S   |
|---------------------------------------|:-------:|:-------:|:-------:|:-------:|:-------:|:-------:|:-------:|:-------:|:-------:|
| set remote processor                  |         |         |         |    1    |    1    |    1    |         |         |         |
| set return address                    |    1    |    1    |         |    1    |    1    |         |    1    |    1    |         |
| transfer write variable<sup>1,2</sup> |   0.8   |   0.8   |   0.8   |   0.8   |   0.8   |   0.8   |   0.8   |   0.8   |   0.8   |      
| setup index                           |         |    1    |    1    |         |    1    |    1    |    1    |    1    |    1    |
| compute branch address (local)        |         |         |         |         |         |         |         |    1    |         |      
| call to branch/table                  |    1    |    1    |    2    |    1    |    1    |    2    |    1    |    1    |    2    |      
| compute branch address (table)        |         |         |         |         |         |         |         |         |    1    |
| jump to branch                        |         |         |    1    |         |         |    1    |         |         |    1    |      
| branch (set+return)                   |    2    |    2    |    2    |    2    |    2    |    2    |    2    |    2    |    2    |
| transfer read variable<sup>1</sup>    |    -    |    -    |    -    |    -    |    -    |    -    |    -    |    -    |    -    |      
| **TOTAL**                             | **4.8** | **5.8** | **6.8** | **5.8** | **6.8** | **7.8** | **5.8** | **6.8** | **7.8** |      

<sup>1</sup>&nbsp;Either the _transfer read variable_ or the _transfer write variable_ instruction is generated, depending on the access type. In this table, they're accounted for in the _transfer write variable_ row.<br>
<sup>2</sup>&nbsp;Depending on the code structure, the _transfer write/read variable_ instruction might be eliminated by the Data Flow Optimizer. Since Array Optimizer doesn't know whether the instruction will be actually eliminated, it must be fully accounted for regarding the code size. The expected execution speed of the regular array is lowered a bit to express this possible optimization. As a result, the optimizer might choose to convert a compact array to a regular array if there's enough instruction space.

### Inlined regular tables

Note: multiplexed remote arrays are handled equally to simple remote arrays, as the remote processor is inlined into the local code.

**Array access code size**

| Instruction            |  U/T   |   U/D    |   U/S    |  F/T  |   F/D   |   F/S   |
|------------------------|:------:|:--------:|:--------:|:-----:|:-------:|:-------:|
| setup index            |        |    1     |    1     |       |    1    |    1    |
| compute branch address |        |          |          |       |    1    |    1    |      
| jump to branch         |   1    |    1     |    1     |   1   |    1    |    1    |      
| branches (set+exit)    |  2n-1  |   2n-1   |   2n-1   |  n-1  |   n-1   |   n-1   |      
| **TOTAL**              | **2n** | **2n+1** | **2n+1** | **n** | **n+2** | **n+2** |      

**Instructions executed**

| Instruction            |  U/T  |  U/D  |  U/S  |  F/T  |  F/D  |  F/S  |
|------------------------|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|
| setup index            |       |   1   |   1   |       |   1   |   1   |
| compute branch address |       |       |       |       |   1   |   1   |      
| jump to branch         |   1   |   1   |   1   |   1   |   1   |   1   |      
| branch (set+exit)      |   2   |   2   |   2   |   2   |   2   |   2   |      
| **TOTAL**              | **3** | **4** | **4** | **3** | **5** | **5** |      

### Special cases

* For repeated compact array access to the same element, the element lookup may be skipped. In this case, both code size and execution time are just a single instruction. This occurrence isn't reflected in the tables below.
* Lookup array access always uses either three instructions or just one instruction for repeated access to the same element, the same as for compact arrays.
* Inlining effects:
    * The size of branches decreased by one, as the last branch doesn't end with a jump but immediately continues with the next instruction.
    * One execution step is saved when using the last branch of the table. The last branch is used for the last element of the array, and in the case of folded even-sized arrays, for one additional element. This saving is considered by the optimizer but not shown in the table.

## Optimization progression

The compiler initially chooses array implementations to take the least amount of space possible. The initial implementations are always non-inlined. The specific implementation depends on the capabilities of the target processor:

1. Folded compact, if the `select` instruction is available (target `8.1` or higher).
2. Compact, if indirect access is possible (target `8.0` or higher).
3. Regular.

For constant arrays, regular tables are initially used (folded when the `select` instruction is available).

This table sums up the execution times of different array implementations:

| Array implementation | U/T | U/D | U/S | M/T | M/D | M/S | F/T | F/D | F/S |
|----------------------|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| Non-inlined compact  |  5  |  6  |  7  |  5  |  6  |  7  |  6  |  7  |  8  |      
| Non-inlined regular  | 4.8 | 5.8 | 6.8 | 5.8 | 6.8 | 7.8 | 5.8 | 6.8 | 7.8 |      
| Inlined compact      |  4  |  5  |  5  |  4  |  5  |  5  |  4  |  6  |  6  |      
| Inlined regular      |  3  |  4  |  4  |  3  |  4  |  4  |  3  |  5  |  5  |      

Code size can't be meaningfully compared, as it depends on the number of times the array access is performed.

Based on this, the optimizer considers the following optimizations:

**Unfolding tables**

Unfolding approximately doubles the size of the jump table and saves one instruction execution. However, when [text-based table dispatch](#text-based-table-dispatch) is available, folded inlined arrays perform the same as unfolded ones and are therefore kept folded.

**Promoting tables**

Compact tables can get promoted to regular ones. If the array is only read or only written to, or if the access is already inlined, promoting the table doesn't require additional space.

* This optimization doesn't take place when repeated access to the same element is detected: the second and later accesses can avoid the using table entirely, saving a lot of instructions and execution time.
* Regular tables of remote arrays and write tables of local arrays cannot be folded. When promoting these tables, unfolding is automatically performed too.
* For other non-inlined tables, no savings are guaranteed, but are assumed. For inlined tables, one instruction is always saved.

**Inlining tables**

This optimization inlines the table to the place of array access.

* Inlining saves between one and four execution steps, depending on the original implementation of the array.
* Inlining the last non-inlined array access means the original table is eliminated, decreasing code size. The last non-inlined array access using a specific table is always inlined.
* When [text-based table dispatch](#text-based-table-dispatch) is available, inlined arrays may also be folded if possible.

The optimizer considers possible optimizations for the following groups of array accesses:

* All non-inlined array accesses sharing an instance of the table. The optimization affects either all of these instructions or none of them. When the optimization succeeds, the original table is replaced by the optimized one(s). This decreases the cost of the optimization by eliminating the original table.
* Inlining (possibly combined with folding) of every array access is considered separately. In this case, since not all array accesses using the same table are inlined, the original table used by other, non-inlined instructions cannot be eliminated. Inlining only some instead of all instructions only happens when there isn't enough instruction space to inline all of them.
* Unfolding or promoting each inlined array access is also considered separately. Since the array access is inlined, only its table is affected by the optimization.

Note: it doesn't make sense to consider unfolding or promoting individual, non-inlined instructions. If the optimization benefits one such instruction, it benefits all of them in the same way, and applying the optimization to all allows eliminating the original table.

The [dynamic optimization](../SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations) mechanism is used to choose which optimizations to perform.

---

[Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Boolean Optimization &#xBB;](BOOLEAN-OPTIMIZATION.markdown)
