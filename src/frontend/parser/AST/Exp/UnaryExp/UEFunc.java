package frontend.parser.AST.Exp.UnaryExp;

import frontend.lexer.Token;
import frontend.parser.AST.Exp.FuncRParams;
import frontend.parser.Node;

public class UEFunc implements UnaryExp, Node {
    private Token ident;
    private FuncRParams funcRParams;    // 可能为 null

    // UnaryExp → Ident '(' [FuncRParams] ')'
    public UEFunc(Token ident, FuncRParams funcRParams) {
        this.ident = ident;
        this.funcRParams = funcRParams;
    }

    public Token getIdent() {
        return ident;
    }

    public FuncRParams getFuncRParams() {
        return funcRParams;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        sb.append("LPARENT (\n");
        if (funcRParams != null) {
            sb.append(funcRParams.toString());
        }
        sb.append("RPARENT )\n");
        sb.append("<UnaryExp>\n");
        return sb.toString();
    }
}
