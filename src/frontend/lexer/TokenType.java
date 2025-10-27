package frontend.lexer;

public enum TokenType {
    IDENFR,     // Ident
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
    NOT,        // !
    AND,        // &&
    OR,         // ||
    FORTK,      // for
    RETURNTK,   // return
    VOIDTK,     // void
    PLUS,       // +
    MINU,       // -
    PRINTFTK,   // printf
    MULT,       // *
    DIV,        // /
    MOD,        // %
    LSS,        // <
    LEQ,        // <=
    GRE,        // >
    GEQ,        // >=
    EQL,        // ==
    NEQ,        // !=
    SEMICN,     // ;
    COMMA,      // ,
    LPARENT,    // (
    RPARENT,    // )
    LBRACK,     // [
    RBRACK,     // ]
    LBRACE,     // {
    RBRACE,     // }
    ASSIGN,     // =
    EOF,        // end
}
