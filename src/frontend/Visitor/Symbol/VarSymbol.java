package frontend.Visitor.Symbol;

import frontend.lexer.Token;
import frontend.parser.Node;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private boolean isConst;
    private boolean isStatic;
    private boolean isGlobal;
    private boolean isArray;
    private ArrayList<Integer> inits;

    // [const] [static] ident ['[' exp ']']
    public VarSymbol(Token ident, Node exp, boolean isConst, boolean isStatic, boolean isGlobal) {
        super();
        SymbolType symbolType;
        if (exp == null) {
            if (isConst) {
                symbolType = SymbolType.ConstInt;
            } else if (isStatic) {
                symbolType = SymbolType.StaticInt;
            } else {
                symbolType = SymbolType.Int;
            }
            isArray = false;
        } else {
            if (isConst) {
                symbolType = SymbolType.ConstIntArray;
            } else if (isStatic) {
                symbolType = SymbolType.StaticIntArray;
            } else {
                symbolType = SymbolType.IntArray;
            }
            isArray = true;
        }
        setValue(ident.getValue());
        setType(symbolType);
        this.isConst = isConst;
        this.isStatic = isStatic;
        this.isGlobal = isGlobal;
    }

    public VarSymbol(Token ident, boolean isArray) {
        super();
        SymbolType symbolType;
        if (isArray) {
            symbolType = SymbolType.IntArray;
        } else {
            symbolType = SymbolType.Int;
        }
        setValue(ident.getValue());
        setType(symbolType);
        this.isConst = false;
        this.isStatic = false;
        this.isGlobal = false;
        this.isArray = isArray;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public boolean isArray() {
        return isArray;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(value).append(" ").append(type).append("\n");
        return sb.toString();
    }
}
