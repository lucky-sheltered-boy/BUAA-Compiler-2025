package front_end.lexer;

public enum TokenType {
    IDENFER,    // Ident
    INTCON,     // IntConst
    STRCON,     // StringConst
    CONSTTK,    // const
    INTTK,      // int
    STATICTK,   // static
    BREAKTK,    // break
    CONTINUETK, // continue
    IFTK,       // if
    MAINTK,     // main
    ELSETK,     // else
    NOT,        
    AND,
    OR,
    FORTK,
    RETURNTK,
    VOIDTK,
    PLUS,
    MINU,
    PRINTFTK,
    MULT,
    DIC,
    MOD,
    LSS,
    LEQ,
    GRE,
    GEQ,
    EQL,
    NEQ,
    SEMICN,
    COMMA,
    LPARENT,
    RPARENT,
    LBRACK,
    RBRACK,
    LBRACE,
    RBRACE,
    ASSIGN,
    EOF,
}
