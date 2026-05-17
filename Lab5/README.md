# Lab 5: Min Heap Implementation in Haskell

## Student Information
* **Name**: Subham
* **Roll Number**: 230101098
* **Department**: Computer Science and Engineering, IIT Guwahati

---

## Overview

A functional implementation of a **Min Heap** data structure in Haskell. This assignment demonstrates functional programming principles including pattern matching, recursion, and maintaining invariants in pure functional code.

---

## Key Concepts

### Min Heap Properties
- Binary tree where each parent node is less than or equal to its children
- Minimum element is always at the root
- Used for priority queues, sorting algorithms, and optimization problems

### Functional Implementation
- Immutable data structure using recursive algebraic data types
- Pure functions with no side effects
- Pattern matching for tree manipulation
- Recursive algorithms for heap operations

---

## Main Functions

### Core Operations
- `isEmpty`: Check if heap is empty
- `size`: Count total elements in the heap
- `findMin`: Retrieve the minimum element
- `heapToList`: Convert heap to list using preorder traversal
- `isHeap`: Verify min-heap property

### Advanced Operations
- Insertion and deletion while maintaining heap invariant
- Heap construction from list
- Heap sorting
- Custom comparators for different orderings

---

## How to Run

### Prerequisites
- GHC (Glasgow Haskell Compiler) 8.0 or higher
- Cabal or Stack (optional, for project management)

### Compile and Test
```bash
cd Lab5
ghc -o assignment5 Assignment_5.hs
./assignment5
```

### Interactive Mode (GHCi)
```bash
ghci Assignment_5.hs
```

Then execute queries like:
```haskell
ghci> let h = Node 5 (Node 10 Empty Empty) (Node 15 Empty Empty)
ghci> findMin h
5
ghci> size h
3
ghci> heapToList h
[5, 10, 15]
```

---

## Project Files

```
Lab5/
├── Assignment_5.hs              # Main implementation
├── Assignment_5_template.hs     # Template/skeleton
└── Assignment 5 - Haskell.pdf   # Assignment specification
```

---

## Learning Outcomes

- Understanding functional data structures
- Pattern matching and recursive algorithms
- Maintaining invariants in pure code
- Using type classes and constraints (`Ord a`)
- Lazy evaluation in Haskell
- Verification of data structure properties

---

## Key Design Decisions

- **Algebraic Data Type**: `MinHeap a` defined recursively for type-safe tree representation
- **Polymorphism**: Works with any type that implements `Ord`
- **Recursion**: All operations use recursion instead of loops
- **Immutability**: Every operation returns a new heap rather than modifying existing one
