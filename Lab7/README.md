# Lab 7: MiniDraw - Drawing Commands in Haskell

## Student Information
* **Name**: Subham
* **Roll Number**: 230101098
* **Department**: Computer Science and Engineering, IIT Guwahati

---

## Overview

A **functional drawing system** implemented in Haskell that processes drawing commands and manages graphical scenes. This assignment explores functional approaches to managing state, composing operations, and working with complex data structures in a purely functional manner.

---

## Key Concepts

### Data Structures

#### Expression Evaluation
```haskell
data Expr = Val Int | Add Expr Expr | Mul Expr Expr
```
- Represents arithmetic expressions as abstract syntax trees
- Enables symbolic computation and lazy evaluation
- Allows for expression manipulation and optimization

#### Shape Definitions
```haskell
data Shape = Circle Expr | Rect Expr Expr
```
- Parametric shapes with algebraic expression dimensions
- Supports runtime computation of shape dimensions

#### Drawing Commands
```haskell
data Cmd = Draw Shape | ...
```
- Compositional commands for building complex scenes
- Functional approach to scene construction

### Scene Management
- `Scene`: List of (Shape, Point) pairs representing drawn objects
- `Program`: List of drawing commands
- Composition of multiple shapes and positions

---

## Main Features

### Core Operations
- Expression evaluation and simplification
- Shape creation and manipulation
- Command execution and scene generation
- Point-based positioning
- Scene composition and transformation

### Monoid Operations
- Uses `Data.Monoid` for composable operations
- Leverages associativity and identity properties
- Functional composition of drawing operations

---

## How to Run

### Prerequisites
- GHC (Glasgow Haskell Compiler) 8.0 or higher
- Cabal or Stack (optional)

### Compile
```bash
cd Lab7
ghc -o minidraw HW_MiniDraw.hs
./minidraw
```

### Interactive Mode (GHCi)
```bash
ghci HW_MiniDraw.hs
```

Then execute queries and test drawing operations:
```haskell
ghci> let circle = Circle (Val 10)
ghci> let scene = [(circle, (0, 0))]
```

---

## Project Files

```
Lab7/
├── HW_MiniDraw.hs              # Main implementation
├── HW_MiniDraw_Template.hs     # Template/skeleton
└── Assignment 7 - Haskell.pdf  # Assignment specification
```

---

## Learning Outcomes

- Functional approach to graphics and scene management
- Working with algebraic data types and pattern matching
- Expression representation and evaluation
- Monoid laws and functional composition
- Building complex programs from simple, composable parts
- Type safety in graphics operations

---

## Design Highlights

- **AST-Based Expressions**: Separates syntax from evaluation
- **Polymorphic Shapes**: Generic shape definitions with parameterized dimensions
- **Functional Commands**: Pure functions for scene manipulation
- **Type Safety**: Strong typing prevents invalid drawing operations
- **Composability**: Operations can be combined without side effects

---

## Key Design Decisions

- **Expression Trees**: Use ASTs instead of eager evaluation for flexibility
- **Immutable Scenes**: Every drawing operation produces a new scene
- **Point-Based Positioning**: Simple (Int, Int) coordinates for scene placement
- **Monoid Pattern**: Leverages laws of associativity for robust composition
