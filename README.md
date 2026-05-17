# CS331: Programming Languages and Paradigms

**Student**: Subham (Roll Number: 230101098)  
**Department**: Computer Science and Engineering, IIT Guwahati

---

## Course Overview

This repository contains solutions and implementations for the CS331 course covering various programming paradigms and concepts:

- **Logical Programming** (Prolog)
- **Imperative Programming** (Java - Concurrency)
- **Functional Programming** (Haskell)

Each lab builds skills in different programming styles and demonstrates fundamental computer science concepts.

---

## Lab Summary

### [Lab 1: Prolog Fundamentals](Lab1)
**Topics**: List processing, recursion, custom operators, constraint solving

- **Task 1**: List flattening with nested structures
- **Task 2**: Digit-to-word translation using fact mapping
- **Task 3**: Custom operator definitions (prefix, infix, postfix)
- **Task 4**: Subset sum problem solving
- **Task 5**: Integer range generation

**Language**: Prolog  
**Key Concepts**: Logic programming, unification, backtracking

---

### [Lab 2: Prolog Continued](Lab2)
**Topics**: Advanced list operations, string manipulation, more complex predicates

**Language**: Prolog  
**Key Concepts**: Recursive data structures, pattern matching, constraint propagation

---

### [Lab 3: Prolog Advanced](Lab3)
**Topics**: Complex problem solving with logic programming

**Language**: Prolog  
**Key Concepts**: Logical reasoning, search strategies, optimization

---

### [Lab 4: Concurrent Print Spooler System](lab4)
**Topics**: Thread synchronization, monitors, race condition prevention, deadlock avoidance

- Bounded thread-safe queue
- Job state machine
- Cancellation protocol
- Comprehensive test suite and benchmarks

**Language**: Java  
**Key Concepts**: 
- Concurrent programming
- Synchronization primitives
- Monitor pattern
- Race condition avoidance
- Performance analysis

**Structure**:
- `final print spooler/` - Complete implementation with all features
- `partial completed- ps/` - Partial implementation for reference

---

### [Lab 5: Min Heap in Haskell](Lab5)
**Topics**: Functional data structures, recursion, type constraints

- Min heap property verification
- Binary tree manipulation
- Functional algorithms
- Preorder traversal and list conversion

**Language**: Haskell  
**Key Concepts**:
- Algebraic data types
- Pattern matching
- Recursive algorithms
- Type classes (`Ord`)
- Pure functional programming

---

### [Lab 6: Ticket Booking System](lab6)
**Topics**: Concurrent systems design, atomic operations, thread-safe collections

**Language**: Java  
**Key Concepts**:
- Atomic operations (`AtomicLong`)
- Reentrant locks
- Thread-safe collections
- Seat hold management
- Expiry mechanisms

---

### [Lab 7: MiniDraw - Drawing System](Lab7)
**Topics**: Functional graphics, expression evaluation, monoid operations

- Expression AST and evaluation
- Shape definitions and composition
- Scene management
- Drawing command processing

**Language**: Haskell  
**Key Concepts**:
- Abstract Syntax Trees (AST)
- Expression evaluation
- Monoid patterns
- Functional composition
- Type-safe graphics

---

## Repository Structure

```
CS331/
├── Lab1/                    # Prolog Lab 1
│   ├── source code.pl
│   ├── README.md
│   └── output.txt
├── Lab2/                    # Prolog Lab 2
│   ├── source code.pl
│   ├── README.md
│   └── output.txt
├── Lab3/                    # Prolog Lab 3
│   ├── source code.pl
│   ├── README.md
│   └── output.txt
├── lab4/                    # Java: Print Spooler
│   ├── README.md
│   ├── DESIGN.md
│   ├── final print spooler/
│   └── partial completed- ps/
├── Lab5/                    # Haskell: Min Heap
│   ├── README.md
│   ├── Assignment_5.hs
│   ├── Assignment_5_template.hs
│   └── Assignment 5 - Haskell.pdf
├── lab6/                    # Java: Ticket Booking
│   ├── README.md
│   └── assignment_6_java/
└── Lab7/                    # Haskell: MiniDraw
    ├── README.md
    ├── HW_MiniDraw.hs
    ├── HW_MiniDraw_Template.hs
    └── Assignment 7 - Haskell.pdf
```

---

## Quick Start

### Running Prolog Labs (1-3)
```bash
# Open SWI-Prolog and load the file
cd Lab1
swipl 'source code.pl'
```

### Running Java Labs (4, 6)
```bash
cd lab4/"final print spooler"
mvn clean compile
mvn test
```

### Running Haskell Labs (5, 7)
```bash
cd Lab5
ghc -o assignment5 Assignment_5.hs
./assignment5

# Or use interactive mode
ghci Assignment_5.hs
```

---

## Key Programming Paradigms

### 1. Logical Programming (Labs 1-3)
- **Language**: Prolog
- **Focus**: Declarative problem solving through logic
- **Key Features**: Unification, backtracking, logical inference

### 2. Imperative Programming with Concurrency (Labs 4, 6)
- **Language**: Java
- **Focus**: State management, synchronization, thread safety
- **Key Features**: Locks, monitors, atomic operations, thread coordination

### 3. Functional Programming (Labs 5, 7)
- **Language**: Haskell
- **Focus**: Pure functions, immutable data, algebraic types
- **Key Features**: Recursion, pattern matching, type safety, lazy evaluation

---

## Learning Outcomes

By completing this course, you will understand:

✓ How different programming paradigms approach problem solving  
✓ Trade-offs between expressiveness and safety  
✓ Concurrency challenges and synchronization techniques  
✓ Functional approaches to data structures and algorithms  
✓ Type systems and their benefits for program correctness  
✓ Performance considerations in different paradigms  

---

## Resources and References

- **Prolog**: SWI-Prolog documentation, logic programming fundamentals
- **Java**: Effective Java, Java Concurrency in Practice
- **Haskell**: Learn You a Haskell, Real World Haskell
- **Concurrency**: The Little Book of Semaphores, Java Memory Model

---

## Design Patterns and Best Practices

### Prolog
- Proper use of cut (!) to control backtracking
- Efficient list processing
- Clear predicate design

### Java (Concurrency)
- Monitor pattern for synchronization
- Condition variables with proper signaling
- Thread-safe collections and atomic operations
- Deadlock-free design

### Haskell
- Leveraging type system for correctness
- Pure functions and immutability
- Recursive algorithms
- Monoid and functor patterns

---

## Contact & Submission

For questions or clarifications regarding the assignments, refer to the individual lab READMEs and DESIGN documents.

---

**Last Updated**: May 2026
