# Compiler

A complete compiler for TL13 programming language, a simplified subset of Pascal. It includes four phase/parts: 

1. Scanner -- scans the LL(1) language
2. Predictive Recursive Descent Parser -- Its a top-down parser. For a syntactically valid input program, it outputs a Graphviz DOT file containing the program's parse tree
3. Core -- Its final output is MIPS assembly code. Intermediate output includes a type-annotated Abstract Syntax Tree (AST) and a Control Flow Graph (CFG) labeled with ILOC instructions
4. Optimizations -- It includes translation into SSA form using * algorithm

It also contains the following language extensions:

1. Permits operators to occur adjacent to identifiers/numbers/keywords without intervening spaces (so that, for example, both "x := x + 1" and "x:=x+1;" would be legal---and equivalent---statements)
2. Supports multi-line comments whose beginning is marked by some (sequence of) character(s) and whose ending is marked by some (sequence of) character(s)
3. Supports "char" base type

# Description

1. Output file is named as Inputfile's base name followed by ".pt.dot", ".ast.dot", ".iloc.cgf.dot" , ".s" and ".ssa.dot"
2. compiler/test-inputs -- test inputs

# Run

1. Compile all the .java files (javac -d build -sourcepath src src/edu/utsa/tl13/Compiler.java)
2. Run Compiler.java program with inputfile name given as first parameter. (java -cp build/ edu.utsa.tl13.Compiler test-inputs/sqrt.tl13)

# Project Details

<http://www.cs.utsa.edu/~vonronne/classes/cs5363-s13/project/>
