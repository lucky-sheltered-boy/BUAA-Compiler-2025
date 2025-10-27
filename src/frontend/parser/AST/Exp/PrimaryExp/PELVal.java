package frontend.parser.AST.Exp.PrimaryExp;

import frontend.lexer.Token;
import frontend.parser.AST.Exp.LVal;
import frontend.parser.Node;

public class PELVal implements PrimaryExp, Node {
    private LVal lVal;

    // PrimaryExp → LVal
    public PELVal(LVal lVal) {
        this.lVal = lVal;
    }

    public LVal getLVal() {
        return lVal;
    }

    public Token tryGetArrayIdent() {
        return lVal.tryGetArrayIdent();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal.toString());
        sb.append("<PrimaryExp>\n");
        return sb.toString();
    }
}
