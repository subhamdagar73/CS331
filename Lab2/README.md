# Prolog Assignment 2: Cut, Negation, and List Processing

**Name:** Subham
**Roll Number:** 230101098


## Task 1: List Splitting
Define a procedure `split(Numbers, Positives, Negatives)` to partition a list of numbers.
**Positives:** Includes all numbers $\ge 0$.
**Negatives:** Includes all numbers $< 0$.

##Implementations
a. **Without Cut:** Uses explicit, mutually exclusive boundary conditions to ensure that backtracking does not result in incorrect unifications.
b. **With Cut:** Uses the `!` operator to commit to a specific rule once the condition is met, preventing Prolog from attempting unnecessary alternative branches.


## Task 2: Train Route Discover
 Implement a predicate `route/3` to find a list of towns visited when traveling between two locations in a provided train network.

## Task 3: Logical Correction
Identify and fix the flaw in the `number_of_parents` predicate.

### The original implementation incorrectly allowed individuals like `adam` to satisfy the rule for having 2 parents.
* If queried `?- number_of_parents(adam, 2).`, the first rule fails because `0 \= 2`.
* Prolog then backtracks to the general rule `number_of_parents(X, 2).`, which unifies `X` with `adam` and returns `true`.


## 🛠️ How to Run the Program

1. Open SWI-Prolog

2. Load the file
```prolog
?- consult(['c:/Users/subha/Downloads/cs331/230101098_assignment2/230101098.pl']).

```

3. Run any of the following sample queries:

?- split([3,-1,0,5,-2], P, N).                 %Task 1(a)
?- split_cut([3,-1,0,5,-2],P,N).               %Task 1(b)
?- route(aizawl, guwahati, R).                 %Task 2
?- connected(aizwal,guwahati)                  %Task 2
?- number_of_parents(adam, 0).                 %Task 3

