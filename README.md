# Compiler

A complete compiler for TL13 programming language, a simplified subset of Pascal. It includes four phase/parts: 

1. Scanner -- scans the LL(1) language
2. Predictive Recursive Descent Parser -- Its a top-down parser. For a syntactically valid input program, it outputs a Graphviz DOT file containing the program's parse tree
3. Core -- Its final output is MIPS assembly code. Intermediate output includes a type-annotated Abstract Syntax Tree (AST) and a Control Flow Graph (CFG) labeled with ILOC instructions
4. Optimizations -- It includes translation into SSA form using * algorithm
5. Language Extensions -- Multi-line comments are supported

# Project Details

<http://www.cs.utsa.edu/~vonronne/classes/cs5363-s13/project/>
