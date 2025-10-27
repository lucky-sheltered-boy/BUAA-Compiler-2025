# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java-based compiler for a C-like language (SysY language subset). The compiler implements lexical analysis and syntax analysis phases, outputting tokens, an abstract syntax tree, and error diagnostics.

## Build and Run

This is an IntelliJ IDEA project with no build automation (no Maven/Gradle). To compile and run:

**Compile:**
```bash
javac -encoding UTF-8 -d out/production/zjy_compiler src/**/*.java src/*.java
```

**Run:**
```bash
java -cp out/production/zjy_compiler Compiler
```

The compiler reads input from `testfile.txt` and produces three output files:
- `lexer.txt` - Token stream from lexical analysis
- `parser.txt` - Abstract syntax tree from parsing
- `error.txt` - Compilation errors (if any)

## Architecture

### Compilation Pipeline (src/Compiler.java:14-39)

The compiler follows a traditional multi-phase architecture:

1. **File Reading** - Reads source code from `testfile.txt` using PushbackReader for lookahead capability
2. **Lexical Analysis** - `Lexer.lex()` produces a `TokenStream` of tokens
3. **Syntax Analysis** - `Parser.parse()` consumes the token stream and builds an AST rooted at `CompUnit`
4. **Error Reporting** - `ErrorLog` collects errors from all phases and outputs to `error.txt`

### Frontend Structure

**Lexer** (`frontend.lexer`):
- `Lexer` - Singleton pattern lexer with lookahead support via `PushbackReader`
- Handles single-line (`//`) and multi-line (`/* */`) comments
- Recognizes keywords, identifiers, numeric literals, string literals, operators
- `TokenStream` - Manages token sequence with positioning support for parser backtracking (see `setCurPos()`/`getCurPos()` used in Parser.java:148-159)

**Parser** (`frontend.parser`):
- `Parser` - Singleton recursive descent parser
- Implements speculative parsing with backtracking for disambiguation (e.g., LVal vs Exp in statements)
- `errorLayer` mechanism (Parser.java:28,149-151,605-642) suppresses error reporting during speculative parsing attempts
- Error recovery: Missing semicolons, brackets, and parentheses are handled via `checkParserError()` which continues parsing after reporting errors

**AST** (`frontend.parser.AST`):
- All AST nodes implement `Node` interface
- Organized by grammar categories:
  - `Exp/` - Expression nodes (AddExp, MulExp, UnaryExp, LVal, etc.)
  - `Stmt/` - Statement nodes (IfElseStmt, ForCycleStmt, ReturnStmt, etc.)
  - `Func/` - Function nodes (FuncDef, MainFuncDef, FuncFParams, etc.)
  - `CIV/` and `IV/` - Initializer value nodes (const vs variable)
- Nodes use wrapper pattern to distinguish grammar alternatives (e.g., `UEFunc`, `UEPrimaryExp`, `UEUnaryOpExp` all extend `UnaryExp`)

### Utilities

**ErrorLog** (`utils.ErrorLog`):
- Singleton pattern for global error collection
- Deduplicates errors (HashSet) and sorts by line number then error code before output
- Error codes are single characters (e.g., 'i' for missing semicolon, 'j' for missing right parenthesis, 'k' for missing right bracket)

**Printer** (`utils.Printer`):
- Centralized output to hardcoded file paths
- `printToLexer()`, `printToParser()`, `printToError()` write to respective output files

## Key Implementation Details

### Parser Backtracking

The parser uses position checkpointing for ambiguous grammar cases. Example in Parser.java:148-160:
```java
int curPos = tokenStream.getCurPos();
errorLayer++;  // Suppress errors during speculation
parseLVal();
errorLayer--;
if (tokenStream.getCurToken().is(ASSIGN)) {
    tokenStream.setCurPos(curPos);  // Backtrack
    return parseAssignStmt();
} else {
    tokenStream.setCurPos(curPos);  // Backtrack
    return parseExpStmt();
}
```

This pattern is used to distinguish between assignment statements (`LVal = Exp`) and expression statements.

### Grammar

The parser implements a recursive descent parser for a SysY language grammar with:
- Function definitions with parameters (arrays supported as parameters)
- Control flow: if/else, for loops, break, continue, return
- Expressions: arithmetic, relational, logical, function calls
- Arrays: single-dimensional array declarations and indexing
- `printf` statement with format strings
- `static` variable declarations
- `const` declarations

See grammar productions in comments throughout Parser.java (e.g., Parser.java:41, 67, 78, 98, etc.)

## File Path Conventions

- Input file: `testfile.txt` (hardcoded in Compiler.java:12)
- Output files: `lexer.txt`, `parser.txt`, `error.txt` (hardcoded in utils/Printer.java:6-8)

When modifying I/O behavior, update the constants in these locations.
