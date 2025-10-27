package frontend.Visitor.Symbol;

import frontend.Visitor.SymbolTable;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.AST.Func.FuncFParam;
import frontend.parser.AST.Func.FuncFParams;
import frontend.parser.AST.Func.FuncType;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<SymbolType> paramTypes;

    // FuncType Ident '(' [FuncFParams] ')' Block
    public FuncSymbol(Token ident, FuncType funcType, FuncFParams funcFParams) {
        super();
        setValue(ident.getValue());
        if (funcType.getFuncType().is(TokenType.INTTK)) {
            setType(SymbolType.IntFunc);
        } else {
            setType(SymbolType.VoidFunc);
        }
        paramTypes = new ArrayList<>();
        if (funcFParams != null) {
            for (FuncFParam funcFParam : funcFParams.getFuncFParams()) {
                Token bType = funcFParam.getBType();
                if (funcFParam.isArray()) {
                    paramTypes.add(SymbolType.IntArray);
                } else {
                    paramTypes.add(SymbolType.Int);
                }
            }
        }
    }

    public int getSize() {
        return paramTypes.size();
    }

    public boolean isParamsTypeError(ArrayList<Exp> params, SymbolTable curTable) {
        int minSize = Math.min(paramTypes.size(), params.size());
        for (int i = 0; i < minSize; i++) {
            SymbolType myType = paramTypes.get(i);
            Exp exp = params.get(i);
            Token ident = exp.tryGetArrayIdent();  // 该方法在函数实参检查中调用，数组作为实参不能含有中括号，因此当 exp 为 null 时才返回 ident
            // 这里的 ident 只考虑了变量标识符，没有考虑函数标识符
            if (ident != null) {
                Symbol symbol = curTable.find(ident);
                if (symbol == null) {
                    continue; // 未定义变量，在其他地方报错，这里不处理
                } else if (symbol instanceof VarSymbol) {
                    VarSymbol varSymbol = (VarSymbol) symbol;
                    boolean isArray = varSymbol.getType() == SymbolType.IntArray;
                    if ((myType == SymbolType.IntArray && !isArray) ||
                        (myType == SymbolType.Int && isArray)) {
                        return true;
                    }
                }
            } else {
                // 非变量标识符作为实参，只能是整型表达式 或 数组解引用
                if (myType == SymbolType.IntArray) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.value).append(" ").append(this.type).append("\n");
        return sb.toString();
    }
}
