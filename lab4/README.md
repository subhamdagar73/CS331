# Lab 4: Print Spooler System

## Student Information
* **Name**: Subham
* **Roll Number**: 230101098
* **Department**: Computer Science and Engineering, IIT Guwahati

---

## Overview

A Java-based **concurrent print spooler system** that manages job queuing, printing, and cancellation safely in a multi-threaded environment. This project demonstrates advanced concurrency control patterns including monitors, condition variables, and race condition prevention.

---

## Key Features

### 1. Bounded Queue Management
- Thread-safe FIFO queue using `ArrayDeque`
- Blocking `putBlocking()` and `takeBlocking()` operations
- Timeout-based operations with `putWithTimeout()`
- Proper handling of full and empty queue conditions

### 2. Job State Machine
- State transitions: `QUEUED → PRINTING → DONE` or `QUEUED/PRINTING → CANCELLED`
- Atomic state transitions ensuring linearizability
- Prevention of cancelled jobs from starting to print

### 3. Race Condition Prevention
- Uses `synchronized` blocks for mutual exclusion
- `while(condition) wait()` loops to handle spurious wakeups
- `notifyAll()` to wake up waiting threads
- Volatile `cancelRequested` flag for visibility

### 4. Deadlock Prevention
- Single monitor pattern for queue operations
- Consistent lock ordering
- No nested locks without careful coordination

---

## Project Structure

```
lab4/
├── final print spooler/          # Complete implementation
│   ├── pom.xml
│   ├── README.md
│   ├── DESIGN.md
│   ├── scripts/
│   │   ├── run_tests.sh
│   │   └── run_bench.sh
│   └── src/
│       ├── main/java/...
│       └── test/java/...
└── partial completed- ps/        # Partial implementation
```

---

## How to Run

### Prerequisites
- Java 8 or higher
- Maven 3.6+

### Compile
```bash
cd "final print spooler"
mvn clean compile
```

### Run Tests
```bash
cd "final print spooler"
mvn test
```

Or use the provided script:
```bash
cd "final print spooler"
./scripts/run_tests.sh
```

### Run Benchmarks
```bash
cd "final print spooler"
./scripts/run_bench.sh
```

---

## Design Highlights

- **Monitor Pattern**: Encapsulates synchronization within queue objects
- **Condition Variables**: Uses `wait()` and `notifyAll()` for thread coordination
- **Job Registry**: Manages job lifecycle and state transitions
- **Cancellation Protocol**: Ensures safe cancellation even during printing

See [DESIGN.md](lab4/DESIGN.md) for detailed design documentation.

---

## Learning Outcomes

- Understanding monitors and mutual exclusion
- Implementing thread-safe data structures
- Avoiding race conditions and deadlocks
- Designing robust cancellation protocols
- Performance analysis of concurrent systems
