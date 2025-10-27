package frontend.parser.AST.Exp;

import frontend.lexer.Token;
import frontend.parser.Node;

public class LVal implements Node {
    private Token ident;
    private Exp exp; // 可能为null

    // LVal → Ident ['[' Exp ']']
    public LVal (Token ident, Exp exp) {
        this.ident = ident;
        this.exp = exp;
    }

    public Token getIdent() {
        return ident;
    }

    public Exp getExp() {
        return exp;
    }

    // 该方法在函数实参检查中调用，数组作为实参不能含有中括号，因此当 exp 为 null 时才返回 ident
    public Token tryGetArrayIdent() {
        if (exp != null) {
            return null;
        }
        return ident;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        if (exp != null) {
            sb.append("LBRACK [\n");
            sb.append(exp.toString());
            sb.append("RBRACK ]\n");
        }
        sb.append("<LVal>\n");
        return sb.toString();
    }
}
