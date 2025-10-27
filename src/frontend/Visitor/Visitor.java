package frontend.Visitor;

import frontend.Visitor.Symbol.FuncSymbol;
import frontend.Visitor.Symbol.Symbol;
import frontend.Visitor.Symbol.SymbolType;
import frontend.Visitor.Symbol.VarSymbol;
import frontend.lexer.Token;
import frontend.parser.AST.*;
import frontend.parser.AST.CIV.CIVConstExp;
import frontend.parser.AST.CIV.CIVConstExps;
import frontend.parser.AST.CIV.ConstInitVal;
import frontend.parser.AST.Exp.*;
import frontend.parser.AST.Exp.PrimaryExp.PEExp;
import frontend.parser.AST.Exp.PrimaryExp.PELVal;
import frontend.parser.AST.Exp.PrimaryExp.PENumber;
import frontend.parser.AST.Exp.PrimaryExp.PrimaryExp;
import frontend.parser.AST.Exp.UnaryExp.UEFunc;
import frontend.parser.AST.Exp.UnaryExp.UEPrimaryExp;
import frontend.parser.AST.Exp.UnaryExp.UEUnaryOpExp;
import frontend.parser.AST.Exp.UnaryExp.UnaryExp;
import frontend.parser.AST.Func.*;
import frontend.parser.AST.IV.IVExp;
import frontend.parser.AST.IV.IVExps;
import frontend.parser.AST.IV.InitVal;
import frontend.parser.AST.Stmt.*;
import frontend.parser.Node;
import utils.ErrorLog;
import utils.Error;
import java.util.ArrayList;

import static frontend.lexer.TokenType.VOIDTK;

public class Visitor {
    private static Visitor visitor;
    private int idCount;
    private SymbolTable rootTable;
    private SymbolTable curTable;
    private int loopNum;
    private boolean inVoidFunc;

    public Visitor() {
        idCount = 1;
        rootTable = new SymbolTable(1, null);
        curTable = rootTable;
        loopNum = 0;
        inVoidFunc = false;
    }

    public static Visitor getInstance() {
        if (visitor == null) {
            visitor = new Visitor();
        }
        return visitor;
    }

    public SymbolTable check(CompUnit compUnit) {
        checkCompUnit(compUnit);
        return rootTable;
    }

    // CompUnit → {Decl} {FuncDef} MainFuncDef
    private void checkCompUnit(CompUnit compUnit) {
        ArrayList<Decl> decls = compUnit.getDecls();
        ArrayList<FuncDef> funcDefs = compUnit.getFuncDefs();
        MainFuncDef mainFuncDef = compUnit.getMainFuncDef();
        for (Decl decl : decls) {
            checkDecl(decl);
        }
        for (FuncDef funcDef : funcDefs) {
            checkFuncDef(funcDef);
        }
        checkMainFuncDef(mainFuncDef);
    }

    // MainFuncDef → 'int' 'main' '(' ')' Block // g j
    public void checkMainFuncDef(MainFuncDef mainFuncDef) {
        Block block = mainFuncDef.getBlock();
        ArrayList<BlockItem> blockItems = block.getBlockItems();
        if (blockItems.isEmpty() || !(blockItems.get(blockItems.size() - 1) instanceof ReturnStmt)) {
            addError(block.getRightBrace(), 'g');
        }
        enterBlock();
        checkBlock(block);
        leaveBlock();
    }

    // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block // b, g, j
    public void checkFuncDef(FuncDef funcDef) {
        FuncType funcType = funcDef.getFuncType();
        Token ident = funcDef.getIdent();
        FuncFParams funcFParams = funcDef.getFuncFParams();
        Block block = funcDef.getBlock();
        // check redefinition
        if (curTable.has(ident)) {
            addError(ident, 'b');
        }
        // check return
        ArrayList<BlockItem> blockItems = block.getBlockItems();
        if (funcType.getFuncType().is(VOIDTK)) {
            inVoidFunc = true;
        } else {
            inVoidFunc = false;
            if (blockItems.isEmpty() || !(blockItems.get(blockItems.size() - 1) instanceof ReturnStmt)) {
                addError(block.getRightBrace(), 'g');
            }
        }
        Symbol funcSymbol = new FuncSymbol(ident, funcType, funcFParams);
        curTable.addSymbol(funcSymbol);
        enterBlock();
        if (funcFParams != null) {
            checkFuncFParams(funcFParams);
        }
        checkBlock(block);
        leaveBlock();
        inVoidFunc = false;
    }

    // Block → '{' { BlockItem } '}'
    public void checkBlock(Block block) {
        ArrayList<BlockItem> blockItems = block.getBlockItems();
        for (BlockItem blockItem : blockItems) {
            checkBlockItem(blockItem);
        }
    }

    // BlockItem → Decl | Stmt
    public void checkBlockItem(BlockItem blockItem) {
        if (blockItem instanceof Decl) {
            checkDecl((Decl) blockItem);
        } else if (blockItem instanceof Stmt) {
            checkStmt((Stmt) blockItem);
        }
    }

    //  Stmt → LVal '=' Exp ';' // h, i
    // | [Exp] ';' // i, l(printf 被调用时)
    // | Block
    // | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
    // | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // h
    // | 'break' ';' | 'continue' ';' // i m
    // | 'return' [Exp] ';' // f i
    // 'printf''('StringConst {','Exp}')'';' // i j l
    public void checkStmt(Stmt stmt) {
        if (stmt instanceof AssignStmt) {
            checkAssignStmt((AssignStmt) stmt);
        } else if (stmt instanceof ExpStmt) {
            checkExpStmt((ExpStmt) stmt);
        } else if (stmt instanceof BlockStmt) {
            checkBlockStmt((BlockStmt) stmt);
        } else if (stmt instanceof IfElseStmt) {
            checkIfElseStmt((IfElseStmt) stmt);
        } else if (stmt instanceof ForCycleStmt) {
            checkForCycleStmt((ForCycleStmt) stmt);
        } else if (stmt instanceof BreakStmt) {
            checkBreakStmt((BreakStmt) stmt);
        } else if (stmt instanceof ContinueStmt) {
            checkContinueStmt((ContinueStmt) stmt);
        } else if (stmt instanceof ReturnStmt) {
            checkReturnStmt((ReturnStmt) stmt);
        } else if (stmt instanceof PrintfStmt) {
            checkPrintfStmt((PrintfStmt) stmt);
        }
    }

    // Stmt → LVal '=' Exp ';' // h, i
    public void checkAssignStmt(AssignStmt stmt) {
        LVal lVal = stmt.getLVal();
        Exp exp = stmt.getExp();
        Token ident = lVal.getIdent();
        VarSymbol varSymbol = (VarSymbol) curTable.find(ident);
        if (varSymbol == null) {
            // 未定义变量，在 checkLVal 中报错，这里不处理
        } else {
            if (varSymbol.isConst()) {
                addError(ident, 'h');
            }
        }
        checkLVal(lVal);
        checkExp(exp);
    }

    // Stmt -> [Exp] ';' // i, l(printf 被调用时)
    public void checkExpStmt(ExpStmt stmt) {
        Exp exp = stmt.getExp();
        if (exp != null) {
            checkExp(exp);
        }
    }

    // Stmt -> Block
    public void checkBlockStmt(BlockStmt stmt) {
        Block block = stmt.getBlock();
        enterBlock();
        checkBlock(block);
        leaveBlock();
    }

    // Stmt -> 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
    public void checkIfElseStmt(IfElseStmt stmt) {
        Cond cond = stmt.getCond();
        Stmt ifStmt = stmt.getIfStmt();
        Stmt elseStmt = stmt.getElseStmt();
        checkCond(cond);
        checkStmt(ifStmt);
        if (elseStmt != null) {
            checkStmt(elseStmt);
        }
    }

    // Stmt -> 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // h
    public void checkForCycleStmt(ForCycleStmt stmt) {
        ForStmt initStmt = stmt.getInitStmt();
        Cond cond = stmt.getCond();
        ForStmt stepStmt = stmt.getStepStmt();
        Stmt bodyStmt = stmt.getBodyStmt();
        if (initStmt != null) {
            checkForStmt(initStmt);
        }
        if (cond != null) {
            checkCond(cond);
        }
        if (stepStmt != null) {
            checkForStmt(stepStmt);
        }
        loopNum++;
        checkStmt(bodyStmt);
        loopNum--;
    }

    // Stmt -> 'break' ';' | 'continue' ';' // i m
    public void checkBreakStmt(BreakStmt stmt) {
        if (loopNum == 0) {
            addError(stmt.getBreaktk(), 'm');
        }
    }

    // Stmt -> 'continue' ';' // i m
    public void checkContinueStmt(ContinueStmt stmt) {
        if (loopNum == 0) {
            addError(stmt.getContinuetk(), 'm');
        }
    }

    // Stmt -> 'return' [Exp] ';' // f i
    public void checkReturnStmt(ReturnStmt stmt) {
        Exp exp = stmt.getExp();
        if (exp != null) {
            checkExp(exp);
        }
        if (inVoidFunc) {
            if (exp != null) {
                addError(stmt.getReturntk(), 'f');
            }
        }
    }

    // Stmt -> 'printf''('StringConst {','Exp}')'';' // i j l
    public void checkPrintfStmt(PrintfStmt stmt) {
        Token formatString = stmt.getFormatString();
        ArrayList<Exp> exps = stmt.getExps();
        int numOfFormatSpecifiers = 0;
        String formatValue = formatString.getValue();
        for (int i = 0; i < formatValue.length() - 1;) {
            if (formatValue.charAt(i) == '%') {
                if (formatValue.charAt(i + 1) == 'd') {
                    numOfFormatSpecifiers++;
                    i += 2;
                } else {
                    i++;
                }
            } else {
                i++;
            }
        }
        if (exps != null) {
            if (numOfFormatSpecifiers != exps.size()) {
                addError(formatString, 'l');
            }
        } else {
            if (numOfFormatSpecifiers != 0) {
                addError(formatString, 'l');
            }
        }
        if (exps != null) {
            for (Exp exp : exps) {
                checkExp(exp);
            }
        }
    }

    // ForStmt → LVal '=' Exp { ',' LVal '=' Exp } // h
    // ForStmt -> ForInitStmt { ',' ForInitStmt } // h
    public void checkForStmt(ForStmt forStmt) {
        ArrayList<ForInitStmt> forInitStmts = forStmt.getForInitStmts();
        for (ForInitStmt forInitStmt : forInitStmts) {
            checkForInitStmt(forInitStmt);
        }
    }

    // ForInitStmt → LVal '=' Exp // h
    public void checkForInitStmt(ForInitStmt forInitStmt) {
        LVal lVal = forInitStmt.getLVal();
        Exp exp = forInitStmt.getExp();
        Token ident = lVal.getIdent();
        VarSymbol varSymbol = (VarSymbol) curTable.find(ident);
        if (varSymbol.isConst()) {
            addError(ident, 'h');
        }
        checkLVal(lVal);
        checkExp(exp);
    }

    // Cond → LOrExp
    public void checkCond(Cond cond) {
        LOrExp lOrExp = cond.getLOrExp();
        checkLOrExp(lOrExp);
    }

    // LOrExp → LAndExp | LOrExp '||' LAndExp // a
    // LOrExp → LAndExp { '||' LAndExp } // a
    public void checkLOrExp(LOrExp lOrExp) {
        ArrayList<LAndExp> landExps = lOrExp.getLAndExps();
        for (LAndExp landExp : landExps) {
            checkLAndExp(landExp);
        }
    }

    // LAndExp → EqExp | LAndExp '&&' EqExp // a
    // LAndExp -> EqExp { '&&' EqExp }
    public void checkLAndExp(LAndExp lAndExp) {
        ArrayList<EqExp> eqExps = lAndExp.getEqExps();
        for (EqExp eqExp : eqExps) {
            checkEqExp(eqExp);
        }
    }

    // EqExp → RelExp | EqExp ('==' | '!=') RelExp
    // EqExp → RelExp { ('==' | '!=') RelExp }
    public void checkEqExp(EqExp eqExp) {
        ArrayList<Node> nodes = eqExp.getNodes();
        for (int i = 0; i < nodes.size(); i += 2) {
            RelExp relExp = (RelExp) nodes.get(i);
            checkRelExp(relExp);
        }
    }

    // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    // RelExp -> AddExp { ('<' | '>' | '<=' | '>=') AddExp }
    public void checkRelExp(RelExp relExp) {
        ArrayList<Node> nodes = relExp.getNodes();
        for (int i = 0; i < nodes.size(); i += 2) {
            AddExp addExp = (AddExp) nodes.get(i);
            checkAddExp(addExp);
        }
    }

    // FuncFParams → FuncFParam { ',' FuncFParam }
    public void checkFuncFParams(FuncFParams funcFParams) {
        ArrayList<FuncFParam> funcFParamsList = funcFParams.getFuncFParams();
        for (FuncFParam funcFParam : funcFParamsList) {
            checkFuncFParam(funcFParam);
        }
    }

    // FuncFParam → BType Ident ['[' ']'] // b, k
    public void checkFuncFParam(FuncFParam funcFParam) {
        Token ident = funcFParam.getIdent();
        boolean isArray = funcFParam.isArray();
        if (curTable.has(ident)) {
            addError(ident, 'b');
        }
        VarSymbol varSymbol = new VarSymbol(ident, isArray);
        curTable.addSymbol(varSymbol);
    }

    // Decl → ConstDecl | VarDecl
    public void checkDecl(Decl decl) {
        if (decl instanceof ConstDecl) {
            checkConstDecl((ConstDecl) decl);
        } else {
            checkVarDecl((VarDecl) decl);
        }
    }

    // VarDecl → [ 'static' ] BType VarDef { ',' VarDef } ';' // i
    public void checkVarDecl(VarDecl decl) {
        boolean isStatic = decl.isStatic();
        ArrayList<VarDef> varDefs = decl.getVarDefs();
        for (VarDef varDef: varDefs) {
            checkVarDef(varDef, isStatic);
        }
    }

    // VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal  b, k
    public void checkVarDef(VarDef varDef, boolean isStatic) {
        Token ident = varDef.getIdent();
        ConstExp constExp = varDef.getConstExp();
        InitVal initVal = varDef.getInitVal();
        if (curTable.has(ident)) {
            addError(ident, 'b');
        }
        VarSymbol varSymbol = new VarSymbol(ident, constExp, false, isStatic, isGlobal());
        curTable.addSymbol(varSymbol);
        if (constExp != null) {
            checkConstExp(constExp);
        }
        if (initVal != null) {
            checkInitVal(initVal);
        }
    }

    // InitVal → Exp | '{' [ Exp { ',' Exp } ] '}
    public void checkInitVal(InitVal initVal) {
        if (initVal instanceof IVExp) {
            checkExp(((IVExp) initVal).getExp());
        } else if (initVal instanceof IVExps) {
            for (Exp exp : ((IVExps) initVal).getExps()) {
                checkExp(exp);
            }
        }
    }

    // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' // i
    public void checkConstDecl(ConstDecl decl) {
        ArrayList<ConstDef> constDefs = decl.getConstDefs();
        for (ConstDef constDef: constDefs) {
            checkConstDef(constDef);
        }
    }

    // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal // b, k
    public void checkConstDef(ConstDef constDef) {
        Token ident = constDef.getIdent();
        ConstExp constExp = constDef.getConstExp();
        ConstInitVal constInitVal = constDef.getConstInitVal();
        if (curTable.has(ident)) {
            addError(ident, 'b');
        }
        VarSymbol varSymbol = new VarSymbol(ident, constExp, true, false, isGlobal());
        curTable.addSymbol(varSymbol);
        if (constExp != null) {
            checkConstExp(constExp);
        }
        checkConstInitVal(constInitVal);
    }

    // ConstExp → AddExp 注：使用的 Ident 必须是常量
    public void checkConstExp(ConstExp constExp) {
        AddExp addExp = constExp.getAddExp();
        checkAddExp(addExp);
    }

    // AddExp → MulExp | AddExp ('+' | '−') MulExp
    // AddExp → MulExp { ('+' | '−') MulExp }
    public void checkAddExp(AddExp addExp) {
        ArrayList<Node> nodes = addExp.getNodes();
        for (int i = 0; i < nodes.size(); i += 2) {
            MulExp mulExp = (MulExp) nodes.get(i);
            checkMulExp(mulExp);
        }
    }

    // MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    // MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
    private void checkMulExp(MulExp mulExp) {
        ArrayList<Node> nodes = mulExp.getNodes();
        for (int i = 0; i < nodes.size(); i+=2) {
            UnaryExp unaryExp = (UnaryExp) nodes.get(i);
            checkUnaryExp(unaryExp);
        }
    }

    //  UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp // c d e j
    public void checkUnaryExp(UnaryExp unaryExp) {
        if (unaryExp instanceof UEPrimaryExp) {
            checkUEPrimaryExp(((UEPrimaryExp) unaryExp));
        } else if (unaryExp instanceof UEFunc) {
            checkUEFunc((UEFunc) unaryExp);
        } else if (unaryExp instanceof UEUnaryOpExp) {
            checkUEUnary((UEUnaryOpExp) unaryExp);
        }
    }

    // UnaryExp → PrimaryExp
    public void checkUEPrimaryExp(UEPrimaryExp uePrimaryExp) {
        PrimaryExp primaryExp = uePrimaryExp.getPrimaryExp();
        checkPrimaryExp(primaryExp);
    }

    // PrimaryExp → '(' Exp ')' | LVal | Number // j
    public void checkPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp instanceof PEExp) {
            checkPEExp((PEExp) primaryExp);
        } else if (primaryExp instanceof PELVal) {
            checkPELVal((PELVal) primaryExp);
        } else if (primaryExp instanceof PENumber){
            // no need to check
        }
    }

    // PrimaryExp → '(' Exp ')'
    private void checkPEExp(PEExp primaryExp) {
        Exp exp = primaryExp.getExp();
        checkExp(exp);
    }

    // Exp → AddExp
    private void checkExp(Exp exp) {
        AddExp addExp = exp.getAddExp();
        checkAddExp(addExp);
    }

    // PrimaryExp -> LVal
    public void checkPELVal(PELVal pelVal) {
        LVal lVal = pelVal.getLVal();
        checkLVal(lVal);
    }

    // LVal → Ident ['[' Exp ']'] // c k
    public void checkLVal(LVal lVal) {
        Token ident = lVal.getIdent();
        Exp exp = lVal.getExp();
        if (!curTable.isDefined(ident)) {
            addError(ident, 'c');
        }
        if (exp != null) {
            checkExp(exp);
        }
    }

    // UnaryExp → Ident '(' [FuncRParams] ')' // c d e j
    public void checkUEFunc(UEFunc ueFunc) {
        Token ident = ueFunc.getIdent();
        FuncRParams funcRParams = ueFunc.getFuncRParams();
        if (ident.getValue().equals("getint")) {
            ArrayList<Exp> params = new ArrayList<>();
            if (funcRParams != null) {
                params = funcRParams.getExps();
            }
            if (!params.isEmpty()) {
                addError(ident, 'd');
            }
        } else {
            if (!curTable.isDefined(ident)) {
                addError(ident, 'c');
            } else {
                FuncSymbol funcSymbol = (FuncSymbol) curTable.find(ident);
                ArrayList<Exp> params = new ArrayList<>();
                if (funcRParams != null) {
                    params = funcRParams.getExps();
                }
                if (funcSymbol.getSize() != params.size()) {
                    addError(ident, 'd');
                }
                if (funcSymbol.isParamsTypeError(params, curTable)) {
                    addError(ident, 'e');
                }
            }
        }
    }

    // UnaryExp → UnaryOp UnaryExp
    public void checkUEUnary(UEUnaryOpExp ueUnaryOpExp) {
        UnaryExp unaryExp = ueUnaryOpExp.getUnaryExp();
        checkUnaryExp(unaryExp);
    }

    // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}'
    public void checkConstInitVal(ConstInitVal constInitVal) {
        if (constInitVal instanceof CIVConstExp) {
            CIVConstExp civConstExp = (CIVConstExp) constInitVal;
            ConstExp constExp = civConstExp.getConstExp();
            checkConstExp(constExp);
        } else if (constInitVal instanceof CIVConstExps) {
            CIVConstExps civConstExps = (CIVConstExps) constInitVal;
            ArrayList<ConstExp> constExps = civConstExps.getConstExps();
            for (ConstExp constExp : constExps) {
                checkConstExp(constExp);
            }
        }
    }

    private void enterBlock() {
        idCount++;
        SymbolTable child = new SymbolTable(idCount, curTable);
        curTable.addChild(child);
        curTable = child;
    }

    private void leaveBlock() {
        curTable = curTable.getFather();
    }

    public void addError(Token ident, char errorKey) {
        Error error = new Error(ident.getLine(), errorKey);
        ErrorLog.getInstance().addError(error);
    }

    public boolean isGlobal() {
        return curTable.getId() == 1;
    }
}
