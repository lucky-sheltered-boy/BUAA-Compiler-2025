package frontend.parser.AST.Exp.UnaryExp;

import frontend.lexer.Token;
import frontend.parser.AST.Exp.PrimaryExp.PELVal;
import frontend.parser.AST.Exp.PrimaryExp.PrimaryExp;
import frontend.parser.Node;

public class UEPrimaryExp implements UnaryExp, Node {
    private PrimaryExp primaryExp;

    // UnaryExp → PrimaryExp
    public UEPrimaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public Token tryGetArrayIdent() {
        if (primaryExp instanceof PELVal) {
            return ((PELVal) primaryExp).tryGetArrayIdent();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(primaryExp.toString());
        sb.append("<UnaryExp>\n");
        return sb.toString();
    }
}
