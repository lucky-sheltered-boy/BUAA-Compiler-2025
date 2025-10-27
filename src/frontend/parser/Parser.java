package frontend.parser;

import frontend.lexer.Token;
import frontend.lexer.TokenStream;
import frontend.lexer.TokenType;
import frontend.parser.AST.*;
import frontend.parser.AST.CIV.CIVConstExp;
import frontend.parser.AST.CIV.CIVConstExps;
import frontend.parser.AST.CIV.ConstInitVal;
import frontend.parser.AST.Exp.*;
import frontend.parser.AST.Exp.PrimaryExp.*;
import frontend.parser.AST.Exp.UnaryExp.*;
import frontend.parser.AST.Func.*;
import frontend.parser.AST.IV.IVExp;
import frontend.parser.AST.IV.IVExps;
import frontend.parser.AST.IV.InitVal;
import frontend.parser.AST.Stmt.*;
import utils.Error;
import utils.ErrorLog;

import java.util.ArrayList;

import static frontend.lexer.TokenType.*;

public class Parser {
    private static Parser instance = null;
    private final TokenStream tokenStream;
    private int errorLayer = 0;

    private Parser(TokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    public static Parser getInstance(TokenStream tokenStream) {
        if (instance == null) {
            instance = new Parser(tokenStream);
        }
        return instance;
    }

    // CompUnit → {Decl} {FuncDef} MainFuncDef
    public CompUnit parse() {
        tokenStream.next();
        // {Decl}
        ArrayList<Decl> decls = new ArrayList<>();
        while (true) {
            Token peek2 = tokenStream.peek(2);
            if (peek2.is(TokenType.LPARENT)) {
                break;  // is FuncDef or MainFuncDef
            }
            decls.add(parseDecl());
        }
        // {FuncDef}
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        while (true) {
            Token peek1 = tokenStream.peek(1);
            if (peek1.is(TokenType.MAINTK)) {
                break;  // is MainFuncDef
            }
            funcDefs.add(parseFuncDef());
        }
        // MainFuncDef
        MainFuncDef mainFuncDef = parseMainFuncDef();
        return new CompUnit(decls, funcDefs, mainFuncDef);
    }

    // MainFuncDef → 'int' 'main' '(' ')' Block
    public MainFuncDef parseMainFuncDef() {
        tokenStream.next(); // int
        Token mainToken = tokenStream.getCurToken(); // main
        tokenStream.next(); // main
        tokenStream.next(); // (
        checkParserError(RPARENT);
        Block block = parseBlock();
        return new MainFuncDef(block);
    }

    // FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
    public FuncDef parseFuncDef() {
        FuncType funcType = parseFuncType();
        Token ident = tokenStream.getCurToken(); // ident
        tokenStream.next(); // ident
        tokenStream.next(); // (
        FuncFParams funcFParams = null;
        if (tokenStream.getCurToken().is(LBRACE)) { // {
            Error error = new Error(tokenStream.last().getLine(), 'j');
            tryAddError(error);
        } else {
            if (!tokenStream.getCurToken().is(RPARENT)) {
                funcFParams = parseFuncFParams();
            }
            checkParserError(RPARENT);
        }
        Block block = parseBlock();
        return new FuncDef(funcType, ident, funcFParams, block);
    }

    // Block → '{' { BlockItem } '}'
    public Block parseBlock() {
        tokenStream.next(); // {
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        while (!tokenStream.getCurToken().is(RBRACE)) {
            blockItems.add(parseBlockItem());
        }
        Token rightBrace = tokenStream.getCurToken(); // }
        tokenStream.next(); // }
        return new Block(blockItems, rightBrace);
    }

    // BlockItem → Decl | Stmt
    public BlockItem parseBlockItem() {
        if (tokenStream.getCurToken().is(TokenType.CONSTTK)
            || tokenStream.getCurToken().is(INTTK)
            || tokenStream.getCurToken().is(TokenType.STATICTK)) {
            return (BlockItem) parseDecl();
        } else {
            return (BlockItem) parseStmt();
        }
    }

    /*
    Stmt → LVal '=' Exp ';' // i
         | [Exp] ';' // i
         | Block
         | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
         | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
         | 'break' ';' // i
         | 'continue' ';' // i
         | 'return' [Exp] ';' // i
         | 'printf''('StringConst {','Exp}')'';' // i j
    */
    public Stmt parseStmt() {
        if (tokenStream.getCurToken().is(IFTK)) {
            return parseIfStmt();
        } else if (tokenStream.getCurToken().is(FORTK)) {
            return parseForCycleStmt();
        } else if (tokenStream.getCurToken().is(BREAKTK)) {
            return parseBreakStmt();
        } else if (tokenStream.getCurToken().is(CONTINUETK)) {
            return parseContinueStmt();
        } else if (tokenStream.getCurToken().is(RETURNTK)) {
            return parseReturnStmt();
        } else if (tokenStream.getCurToken().is(PRINTFTK)) {
            return parsePrintfStmt();
        } else if (tokenStream.getCurToken().is(LBRACE)){
            return parseBlockStmt();
        } else {
            // LVal '=' Exp ';' | [Exp] ';'
            int curPos = tokenStream.getCurPos();
            errorLayer++;
            parseLVal();
            errorLayer--;
            if (tokenStream.getCurToken().is(ASSIGN)) {
                // is LVal '=' Exp ';'
                tokenStream.setCurPos(curPos);
                return parseAssignStmt();
            } else {
                // is [Exp] ';'
                tokenStream.setCurPos(curPos);
                return parseExpStmt();
            }
        }
    }

    public ExpStmt parseExpStmt() {
        Exp exp = null;
        if (!tokenStream.getCurToken().is(SEMICN)) {
            exp = parseExp();
        }
        checkParserError(SEMICN);
        return new ExpStmt(exp);
    }

    // Stmt -> LVal '=' Exp ';' // i
    public AssignStmt parseAssignStmt() {
        LVal lVal = parseLVal();
        tokenStream.next(); // =
        Exp exp = parseExp();
        checkParserError(SEMICN);
        return new AssignStmt(lVal, exp);
    }

    // Stmt -> Block
    public BlockStmt parseBlockStmt() {
        return new BlockStmt(parseBlock());
    }

    // Stmt -> 'printf' '(' StringConst { ',' Exp } ')' ';' // i j
    public PrintfStmt parsePrintfStmt() {
        Token printfToken = tokenStream.getCurToken(); // printf
        tokenStream.next(); // printf
        tokenStream.next(); // (
        Token stringConst = tokenStream.getCurToken(); // stringConst
        tokenStream.next(); // stringConst
        ArrayList<Exp> exps = new ArrayList<>();
        while (tokenStream.getCurToken().is(COMMA)) {
            tokenStream.next(); // ,
            exps.add(parseExp());
        }
        checkParserError(RPARENT);
        checkParserError(SEMICN);
        return new PrintfStmt(stringConst, exps);
    }

    // Stmt -> 'return' [Exp] ';' // i
    public ReturnStmt parseReturnStmt() {
        Token returnToken = tokenStream.getCurToken(); // return
        tokenStream.next(); // return
        Exp exp = null;
        if (!tokenStream.getCurToken().is(SEMICN)) {
            exp = parseExp();
        }
        checkParserError(SEMICN);
        return new ReturnStmt(returnToken, exp);
    }

    // Stmt -> 'continue' ';' // i
    public ContinueStmt parseContinueStmt() {
        Token continueToken = tokenStream.getCurToken(); // continue
        tokenStream.next(); // continue
        checkParserError(SEMICN);
        return new ContinueStmt(continueToken);
    }

    // Stmt -> 'break' ';' // i
    public BreakStmt parseBreakStmt() {
        Token breakToken = tokenStream.getCurToken(); // break
        tokenStream.next(); // break
        checkParserError(SEMICN);
        return new BreakStmt(breakToken);
    }

    // Stmt -> 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    public ForCycleStmt parseForCycleStmt() {
        Token forToken = tokenStream.getCurToken(); // for
        tokenStream.next(); // for
        tokenStream.next(); // (
        ForStmt firstForStmt = null;
        if (!tokenStream.getCurToken().is(SEMICN)) {
            firstForStmt = parseForStmt();
        }
        tokenStream.next(); // ;
        Cond cond = null;
        if (!tokenStream.getCurToken().is(SEMICN)) {
            cond = parseCond();
        }
        tokenStream.next(); // ;
        ForStmt secondForStmt = null;
        if (!tokenStream.getCurToken().is(RPARENT)) {
            secondForStmt = parseForStmt();
        }
        tokenStream.next(); // )
        Stmt stmt = parseStmt();
        return new ForCycleStmt(firstForStmt, cond, secondForStmt, stmt);
    }

    // Cond → LOrExp
    public Cond parseCond() {
        return new Cond(parseLOrExp());
    }

    // LOrExp → LAndExp { '||' LAndExp }
    public LOrExp parseLOrExp() {
        ArrayList<LAndExp> lAndExps = new ArrayList<>();
        lAndExps.add(parseLAndExp());
        while (tokenStream.getCurToken().is(OR)) {
            tokenStream.next(); // ||
            lAndExps.add(parseLAndExp());
        }
        return new LOrExp(lAndExps);
    }

    // LAndExp → EqExp { '&&' EqExp }
    public LAndExp parseLAndExp() {
        ArrayList<EqExp> nodes = new ArrayList<>();
        nodes.add(parseEqExp());
        while (tokenStream.getCurToken().is(AND)) {
            tokenStream.next(); // &&
            nodes.add(parseEqExp());
        }
        return new LAndExp(nodes);
    }

    // EqExp → RelExp { ('==' | '!=') RelExp }
    public EqExp parseEqExp() {
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(parseRelExp());
        while (tokenStream.getCurToken().is(EQL) || tokenStream.getCurToken().is(NEQ)) {
            nodes.add(tokenStream.getCurToken()); // == !=
            tokenStream.next(); // == !=
            nodes.add(parseRelExp());
        }
        return new EqExp(nodes);
    }

    // RelExp → AddExp { ('<' | '<=' | '>' | '>=') AddExp }
    public RelExp parseRelExp() {
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(parseAddExp());
        while (tokenStream.getCurToken().is(LSS) || tokenStream.getCurToken().is(LEQ)
               || tokenStream.getCurToken().is(GRE) || tokenStream.getCurToken().is(GEQ)) {
            nodes.add(tokenStream.getCurToken()); // < <= > >=
            tokenStream.next(); // < <= > >=
            nodes.add(parseAddExp());
        }
        return new RelExp(nodes);
    }

    // ForStmt → LVal '=' Exp { ',' LVal '=' Exp }
    // 包装 ForInitStmt -> LVal '=' Exp
    // ForStmt -> ForInitStmt { ',' ForInitStmt }
    public ForStmt parseForStmt() {
        ArrayList<ForInitStmt> forInitStmts = new ArrayList<>();
        forInitStmts.add(parseForInitStmt());
        while (tokenStream.getCurToken().is(COMMA)) {
            tokenStream.next(); // ,
            forInitStmts.add(parseForInitStmt());
        }
        return new ForStmt(forInitStmts);
    }

    // ForSubStmt → LVal '=' Exp
    public ForInitStmt parseForInitStmt() {
        LVal lVal = parseLVal();
        tokenStream.next(); // =
        Exp exp = parseExp();
        return new ForInitStmt(lVal, exp);
    }

    // Stmt -> 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
    public IfElseStmt parseIfStmt() {
        Token ifToken = tokenStream.getCurToken(); // if
        tokenStream.next(); // if
        tokenStream.next(); // (
        Cond cond = parseCond();
        checkParserError(RPARENT);
        Stmt thenStmt = parseStmt();
        Stmt elseStmt = null;
        if (tokenStream.getCurToken().is(ELSETK)) {
            tokenStream.next(); // else
            elseStmt = parseStmt();
        }
        return new IfElseStmt(cond, thenStmt, elseStmt);
    }

    // FuncFParams → FuncFParam { ',' FuncFParam }
    public FuncFParams parseFuncFParams() {
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        funcFParams.add(parseFuncFParam());
        while (tokenStream.getCurToken().is(COMMA)) {
            tokenStream.next(); // ,
            funcFParams.add(parseFuncFParam());
        }
        return new FuncFParams(funcFParams);
    }

    // FuncFParam → BType Ident ['[' ']']
    public FuncFParam parseFuncFParam() {
        Token BType = tokenStream.getCurToken(); // int
        tokenStream.next(); // int
        Token ident = tokenStream.getCurToken(); // ident
        tokenStream.next(); // ident
        Boolean isArray = false;
        if (tokenStream.getCurToken().is(LBRACK)) {
            tokenStream.next(); // [
            checkParserError(RBRACK);
            isArray = true;
        }
        return new FuncFParam(BType, ident, isArray);
    }

    // FuncType → 'void' | 'int'
    public FuncType parseFuncType() {
        Token funcType = tokenStream.getCurToken(); // void | int
        tokenStream.next(); // void | int
        return new FuncType(funcType);
    }

    // Decl → ConstDecl | VarDecl
    public Decl parseDecl() {
        if (tokenStream.getCurToken().is(TokenType.CONSTTK)) {
            return parseConstDecl();
        } else {
            return parseVarDecl();
        }
    }

    // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    public Decl parseConstDecl() {
        Token Const = tokenStream.getCurToken();     // const
        tokenStream.next();
        Token BType = tokenStream.getCurToken();    // int
        tokenStream.next();
        ArrayList<ConstDef> constDefs = new ArrayList<>();
        constDefs.add(parseConstDef());
        while (tokenStream.getCurToken().is(TokenType.COMMA)) {
            tokenStream.next(); // ,
            constDefs.add(parseConstDef());
        }
        checkParserError(SEMICN);
        return new ConstDecl(Const, BType, constDefs);
    }

    // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    public ConstDef parseConstDef() {
        Token ident = tokenStream.getCurToken(); // ident
        tokenStream.next();
        ConstExp constExp = null;
        if (tokenStream.getCurToken().is(LBRACK)) {
            tokenStream.next(); // [
            constExp = parseConstExp();
            checkParserError(RBRACK);
        }
        tokenStream.next(); // =
        ConstInitVal constInitVal = parseConstInitVal();
        return new ConstDef(ident, constExp, constInitVal);
    }

    // ConstExp → AddExp
    public ConstExp parseConstExp() {
        return new ConstExp(parseAddExp());
    }

    // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}'
    public ConstInitVal parseConstInitVal() {
        if (tokenStream.getCurToken().is(LBRACE)) {
            tokenStream.next(); // {
            ArrayList<ConstExp> constExps = new ArrayList<>();
            if (!tokenStream.getCurToken().is(RBRACE)) {
                constExps.add(parseConstExp());
                while (tokenStream.getCurToken().is(COMMA)) {
                    tokenStream.next(); // ,
                    constExps.add(parseConstExp());
                }
            }
            tokenStream.next(); // }
            return new CIVConstExps(constExps);
        } else {
            return new CIVConstExp(parseConstExp());
        }
    }

    // VarDecl → [ 'static' ] BType VarDef { ',' VarDef } ';'
    public Decl parseVarDecl() {
        Token Static = null;
        if (tokenStream.getCurToken().is(TokenType.STATICTK)) {
            Static = tokenStream.getCurToken(); // static
            tokenStream.next(); // static
        }
        Token BType = tokenStream.getCurToken(); // int
        tokenStream.next(); // int
        ArrayList<VarDef> varDefs = new ArrayList<>();
        varDefs.add(parseVarDef());
        while (tokenStream.getCurToken().is(COMMA)) {
            tokenStream.next(); // ,
            varDefs.add(parseVarDef());
        }
        checkParserError(SEMICN);
        return new VarDecl(Static, BType, varDefs);
    }

    // VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
    public VarDef parseVarDef() {
        Token ident = tokenStream.getCurToken(); // ident
        tokenStream.next(); // ident
        ConstExp constExp = null;
        if (tokenStream.getCurToken().is(LBRACK)) {
            tokenStream.next(); // [
            constExp = parseConstExp();
            checkParserError(RBRACK);
        }
        InitVal initVal = null;
        if (tokenStream.getCurToken().is(ASSIGN)) {
            tokenStream.next(); // =
            initVal = parseInitVal();
        }
        return new VarDef(ident, constExp, initVal);
    }

    //  InitVal → Exp | '{' [ Exp { ',' Exp } ] '}'
    public InitVal parseInitVal() {
        if (tokenStream.getCurToken().is(LBRACE)) {
            tokenStream.next(); // {
            ArrayList<Exp> exps = new ArrayList<>();
            if (!tokenStream.getCurToken().is(RBRACE)) {
                exps.add(parseExp());
                while (tokenStream.getCurToken().is(COMMA)) {
                    tokenStream.next(); // ,
                    exps.add(parseExp());
                }
            }
            tokenStream.next(); // }
            return new IVExps(exps);
        } else {
            return new IVExp(parseExp());
        }
    }

    // Exp → AddExp
    public Exp parseExp() {
        return new Exp(parseAddExp());
    }

    // AddExp → MulExp { ( '+' | '-' ) MulExp }
    public AddExp parseAddExp() {
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(parseMulExp());
        while (tokenStream.getCurToken().is(PLUS) || tokenStream.getCurToken().is(MINU)) {
            nodes.add(tokenStream.getCurToken()); // + -
            tokenStream.next(); // + -
            nodes.add(parseMulExp());
        }
        return new AddExp(nodes);
    }

    // MulExp → UnaryExp { ( '*' | '/' | '%' ) UnaryExp }
    public MulExp parseMulExp() {
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(parseUnaryExp());
        while (tokenStream.getCurToken().is(MULT) || tokenStream.getCurToken().is(DIV) || tokenStream.getCurToken().is(MOD)) {
            nodes.add(tokenStream.getCurToken()); // * / %
            tokenStream.next(); // * / %
            nodes.add(parseUnaryExp());
        }
        return new MulExp(nodes);
    }

    // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    public UnaryExp parseUnaryExp() {
        if (tokenStream.getCurToken().is(PLUS) || tokenStream.getCurToken().is(MINU) || tokenStream.getCurToken().is(NOT)) {
            UnaryOp unaryOp = parseUnaryOp();
            UnaryExp unaryExp = parseUnaryExp();
            return new UEUnaryOpExp(unaryOp, unaryExp);
        } else if (tokenStream.getCurToken().is(IDENFR) && tokenStream.peek(1).is(LPARENT)) {
            Token ident = tokenStream.getCurToken(); // ident
            tokenStream.next(); // ident
            tokenStream.next(); // (
            FuncRParams funcRParams = null;
            // [FuncRParams] FIRST 集：+ - ! ( ident number
            if (tokenStream.getCurToken().is(PLUS)
                || tokenStream.getCurToken().is(MINU)
                || tokenStream.getCurToken().is(NOT)
                || tokenStream.getCurToken().is(LPARENT)
                || tokenStream.getCurToken().is(IDENFR)
                || tokenStream.getCurToken().is(INTCON)) {
                funcRParams = parseFuncRParams();
            }
            checkParserError(RPARENT);
            return new UEFunc(ident, funcRParams);
        } else {
            return new UEPrimaryExp(parsePrimaryExp());
        }
    }

    // PrimaryExp → '(' Exp ')' | LVal | Number
    public PrimaryExp parsePrimaryExp() {
        if (tokenStream.getCurToken().is(LPARENT)) {
            tokenStream.next(); // (
            Exp exp = parseExp();
            checkParserError(RPARENT);
            return new PEExp(exp);
        } else if (tokenStream.getCurToken().is(IDENFR)) {
            return new PELVal(parseLVal());
        } else {
            Token number = tokenStream.getCurToken(); // number
            tokenStream.next(); // number
            return new PENumber(new NumberExp(number));
        }
    }

    //  LVal → Ident ['[' Exp ']']
    public LVal parseLVal() {
        Token ident = tokenStream.getCurToken(); // ident
        tokenStream.next(); // ident
        Exp exp = null;
        if (tokenStream.getCurToken().is(LBRACK)) {
            tokenStream.next(); // [
            exp = parseExp();
            checkParserError(RBRACK);
        }
        return new LVal(ident, exp);
    }

    // FuncRParams → Exp { ',' Exp }
    public FuncRParams parseFuncRParams() {
        ArrayList<Exp> exps = new ArrayList<>();
        exps.add(parseExp());
        while (tokenStream.getCurToken().is(COMMA)) {
            tokenStream.next(); // ,
            exps.add(parseExp());
        }
        return new FuncRParams(exps);
    }

    // UnaryOp → '+' | '-' | '!'
    public UnaryOp parseUnaryOp() {
        Token op = tokenStream.getCurToken(); // + - !
        tokenStream.next(); // + - !
        return new UnaryOp(op);
    }

    private void checkParserError(TokenType type) {
        switch (type) {
            case SEMICN :{
                if (!tokenStream.getCurToken().is(SEMICN)) {
                    if (errorLayer == 0) {
                        Error error = new Error(tokenStream.last().getLine(), 'i');
                        ErrorLog.getInstance().addError(error);
                    }
                } else {
                    tokenStream.next();
                }
                break;
            }
            case RBRACK :{
                if (!tokenStream.getCurToken().is(RBRACK)) {
                    if (errorLayer == 0) {
                        Error error = new Error(tokenStream.last().getLine(), 'k');
                        ErrorLog.getInstance().addError(error);
                    }
                } else {
                    tokenStream.next();
                }
                break;
            }
            case RPARENT :{
                if (!tokenStream.getCurToken().is(RPARENT)) {
                    if (errorLayer == 0) {
                        Error error = new Error(tokenStream.last().getLine(), 'j');
                        ErrorLog.getInstance().addError(error);
                    }
                } else {
                    tokenStream.next();
                }
                break;
            }
        }
    }

    public void tryAddError(Error error) {
        if (errorLayer == 0) {
            ErrorLog.getInstance().addError(error);
        }
    }
}
