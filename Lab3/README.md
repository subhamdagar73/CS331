# Prolog Lab 3:
---
* **Name**: Subham
* **Roll Number**: 230101098
* **Department**: Computer Science and Engineering, IIT Guwahati

# N-Queens Problem using Constraint Logic Programming (CLPFD)

## Overview
This project implements the classical **N-Queens problem** using **Constraint Logic Programming over Finite Domains (CLPFD)** in **SWI-Prolog**.

The objective is to place `N` queens on an `N × N` chessboard such that:
- No two queens share the same row
- No two queens share the same column
- No two queens lie on the same diagonal

Each solution is represented as a list where the index represents the row number and the value represents the column position of the queen.

---

## Technologies Used
- **SWI-Prolog**
- **CLPFD library** (`library(clpfd)`)

## How to Run

### 1. Load the File
Open your terminal or Prolog interpreter (like SWI-Prolog) and load the file:

```prolog
?- consult(['c:/Users/subha/Downloads/cs331/230101098_assignment3/230101098.pl']).

```

Sample queries:
% Display constrained variables (without labeling)
?- queens(4, Sol).                    

% Naive Generate and Test
?- gen_test(4, Sol).

% Early Pruning (Constraints applied during selection)
?- prune_first(4, Sol).

% Intelligent Search (Using labeling)
?- smart_search(4, Sol, []).

% Find the first solution for an 8x8 board
?- smart_search(8, Sol, [ff]), !.

% Use the Most-Constrained (ffc) heuristic for a larger board
?- smart_search(20, Sol, [ffc]).

% Count total solutions for 8-Queens (Expect 92)
?- findall(S, smart_search(8, S, [ff]), Solutions), length(Solutions, Count).

% Benchmark performance difference between Naive and Smart Search
?- time(gen_test(8, Sol)).
?- time(smart_search(8, Sol, [ff])).