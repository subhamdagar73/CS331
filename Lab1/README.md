# Prolog Lab 1:
---

## Student Information
* **Name**: Subham
* **Roll Number**: 230101098
* **Department**: Computer Science and Engineering, IIT Guwahati

---

## Tasks Overview

### Task 1: Flattening a List
A recursive predicate `flatten/2` that transforms a nested list (e.g., `[a, [b, c], d]`) into a single-level list (`[a, b, c, d]`).

### Task 2: Digit Translation
Translates a list of single-digit integers into their English word equivalents using a mapping predicate `means/2`.

### Task 3: Custom Operators
Demonstrates the use of `op/3` to define custom syntax in Prolog. It defines:
* `was`: Infix operator (xfx).
* `the`: Prefix operator (fy).
* `of`: Right-associative infix operator (yfx).

### Task 4: Subset Sum
The `subsum/3` predicate finds all possible subsets of a given list that add up to a specific target sum.

### Task 5: Integer Range Generation
A `between/3` predicate that generates or checks for integers within a specified inclusive range `[Num1, Num2]`.

---

## How to Run

### 1. Load the File
Open your terminal or Prolog interpreter (like SWI-Prolog) and load the file:
```prolog
?- [ 'Assignment1_230101098.pl' ].

?- flatten([a, [b, [c, d]], e], X).                       % Task 1
?- translate([9, 1, 1], X).                               % Task 2
?- joe was the head of the department.                    % Task 3
?- subsum([1, 2, 3, 4], 5, X).                            % Task 4
?- between(5, 8, X).                                      % Task 5


